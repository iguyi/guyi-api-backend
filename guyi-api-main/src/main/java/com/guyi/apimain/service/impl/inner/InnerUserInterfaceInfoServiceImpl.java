package com.guyi.apimain.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guyi.apicommon.model.entity.UserInterfaceInfo;
import com.guyi.apicommon.service.InnerUserInterfaceInfoService;
import com.guyi.apimain.common.ErrorCode;
import com.guyi.apimain.exception.BusinessException;
import com.guyi.apimain.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    /**
     * 根据接口 id 和 用户 id 查询对应用户对接口调用的剩余次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public int getLeftNum(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "找不到用户或接口");
        }
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId", interfaceInfoId);
        userInterfaceInfoQueryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);
        Integer leftNum = userInterfaceInfo.getLeftNum();
        if (leftNum == null) {
            leftNum = 0;
        }
        return leftNum;
    }

}
