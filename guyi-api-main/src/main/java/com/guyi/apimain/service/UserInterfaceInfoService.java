package com.guyi.apimain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guyi.apicommon.model.entity.UserInterfaceInfo;

/**
 * @author guyi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
 * @createDate 2023-08-16 21:57:38
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 参数校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 统计调用接口次数
     *
     * @param interfaceInfoId 被调用接口的 id
     * @param userId          调用接口的用户的 id
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
