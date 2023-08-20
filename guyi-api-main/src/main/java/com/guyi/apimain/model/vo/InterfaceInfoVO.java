package com.guyi.apimain.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息封装视图
 */
@Data
public class InterfaceInfoVO implements Serializable {

    private static final long serialVersionUID = -2789598028855135129L;

    /**
     * 调用次数
     */
    private Integer totalNum;

    /**
     * 接口名称
     */
    private String name;
}
