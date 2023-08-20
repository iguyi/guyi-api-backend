package com.guyi.apimain.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guyi.apicommon.model.entity.UserInterfaceInfo;
import com.guyi.apimain.common.ErrorCode;
import com.guyi.apimain.exception.BusinessException;
import com.guyi.apimain.mapper.UserInterfaceInfoMapper;
import com.guyi.apimain.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 * @author guyi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2023-08-16 21:57:38
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    /**
     * 参数校验
     *
     * @param userinterfaceInfo
     * @param add
     */
    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userinterfaceInfo, boolean add) {
        if (userinterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // todo 完善验证

        // 创建时，所有参数必须非空
        if (add) {
            if (userinterfaceInfo.getInterfaceInfoId() <= 0 || userinterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userinterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "调用接口的剩余次数不足");
        }
    }

    /**
     * 更新接口调用次数和用户对接口的剩余调用次数
     *
     * @param interfaceInfoId 被调用接口的 id
     * @param userId          调用接口的用户的 id
     * @return
     */
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UpdateWrapper<UserInterfaceInfo> userInterfaceInfoUpdateWrapper = new UpdateWrapper<>();
        userInterfaceInfoUpdateWrapper.eq("interfaceInfoId", interfaceInfoId);
        userInterfaceInfoUpdateWrapper.eq("userId", userId);
        userInterfaceInfoUpdateWrapper.gt("leftNum", 0);
        userInterfaceInfoUpdateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(userInterfaceInfoUpdateWrapper);
    }
}




