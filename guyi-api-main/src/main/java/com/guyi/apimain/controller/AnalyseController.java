package com.guyi.apimain.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.guyi.apicommon.model.entity.InterfaceInfo;
import com.guyi.apicommon.model.entity.UserInterfaceInfo;
import com.guyi.apimain.annotation.AuthCheck;
import com.guyi.apimain.common.BaseResponse;
import com.guyi.apimain.common.ErrorCode;
import com.guyi.apimain.common.ResultUtils;
import com.guyi.apimain.exception.BusinessException;
import com.guyi.apimain.mapper.UserInterfaceInfoMapper;
import com.guyi.apimain.model.vo.InterfaceInfoVO;
import com.guyi.apimain.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器, 用于分析接口调用情况
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalyseController {
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfos.stream().
                collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<InterfaceInfoVO> interfaceInfoVoList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVo = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVo);
            Integer totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVo.setTotalNum(totalNum);
            return interfaceInfoVo;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVoList);
    }
}
