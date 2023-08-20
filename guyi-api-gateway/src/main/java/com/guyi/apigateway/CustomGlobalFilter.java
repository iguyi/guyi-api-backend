package com.guyi.apigateway;


import com.guyi.apicommon.model.entity.InterfaceInfo;
import com.guyi.apicommon.model.entity.User;
import com.guyi.apicommon.service.InnerInterfaceInfoService;
import com.guyi.apicommon.service.InnerUserInterfaceInfoService;
import com.guyi.apicommon.service.InnerUserService;
import com.guyi.clientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户发送请求到 API 网关, 进行全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    @Resource
    private InnerUserService innerUserService;

    @DubboReference
    @Resource
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    @Resource
    private InnerInterfaceInfoService innerInterfaceInfoService;

    public static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");

    public static final String INTERFACE_HOST = "http://localhost:8081";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();

        // 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String url = INTERFACE_HOST + request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).toString();

        log.info("请求唯一标识:" + request.getId());
        log.info("请求路径:" + url);
        log.info("请求方法:" + method);
        log.info("请求参数:" + request.getQueryParams());
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求来源地址:" + sourceAddress);
        log.info("请求来源地址:" + request.getRemoteAddress());

        // 访问控制 - 白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }

        // 用户鉴权（ak, sk）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        if (accessKey == null || nonce == null || timestamp == null || sign == null || body == null) {
            return handleNoAuth(response);
        }

        // 根据 ak 去数据库中查询这个 ak 是否以分配给了用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvoke error ", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        // todo 后面需要存储随机数
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }
        // 时间不能和当前时间超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 5L * 60L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        // 获取从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(body, secretKey);
        if (!sign.equals(serverSign)) {
            return handleNoAuth(response);
        }

        // 从数据库中查询模拟接口是否存在，请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error ", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 检查是否有调用次数
        long interfaceInfoId = interfaceInfo.getId();
        long invokeUserId = invokeUser.getId();
        int leftNum = innerUserInterfaceInfoService.getLeftNum(interfaceInfoId, invokeUserId);
        if (leftNum <= 0) {
            handleNoAuth(response);
        }

        // 请求转发，调用模拟接口 + 响应日志
        return handleResponse(exchange, chain, interfaceInfoId, invokeUserId);
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工程对象
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 等调用完转发的接口后才会执行这个方法
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error ", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);  //释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        sb2.append("<--- {} {} \n");
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8);  //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果: " + data);
                                        return bufferFactory.wrap(content);
                                    })
                            );
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);  //降级处理返回数据
        } catch (Exception e) {
            log.error("网关响应处理异常." + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 设置 Filter 执行顺序
     *
     * @return
     */
    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 无权限访问
     *
     * @param response
     * @return
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);  // 403
        return response.setComplete();
    }

}
