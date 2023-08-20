package com.guyi.apimain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guyi.apimain.common.ErrorCode;
import com.guyi.apimain.exception.BusinessException;
import com.guyi.apimain.mapper.InterfaceInfoMapper;
import com.guyi.apicommon.model.entity.InterfaceInfo;
import com.guyi.apimain.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 参数校验
 *
 * @author guyi
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-08-14 22:10:40
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    /**
     * 参数校验
     *
     * @param interfaceInfo
     * @param add
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // todo 更多的校验
        String name = interfaceInfo.getName();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称不能为空");
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长");
        }
    }
}




