package com.guyi.apimain.service;

import com.guyi.apicommon.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author guyi
 * @description 针对表【interface_info(接口信息)】的数据库操作Service
 * @createDate 2023-08-14 22:10:40
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 参数校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
