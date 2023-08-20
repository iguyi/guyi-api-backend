package com.guyi.apicommon.service;


import com.guyi.apicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author guyi
 */
public interface InnerUserService {

    /**
     * 数据库中查询是否已分配给用户密钥（accessKey）
     *
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
