package com.guyi.apicommon.service;

/**
 * 对 user_interface_info 表的公共操作
 *
 * @author guyi
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
 * @createDate 2023-07-14 14:01:47
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口次数统计
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 根据用户 id 查询对应用户对接口调用的剩余次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    int getLeftNum(long interfaceInfoId, long userId);
}
