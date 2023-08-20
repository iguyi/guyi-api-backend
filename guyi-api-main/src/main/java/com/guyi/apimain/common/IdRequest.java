package com.guyi.apimain.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据 id 进行非删除操作的请求
 */
@Data
public class IdRequest implements Serializable {

    private static final long serialVersionUID = 8049642638374631265L;

    /**
     * id
     */
    private Long id;
}