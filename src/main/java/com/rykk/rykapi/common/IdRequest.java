package com.rykk.rykapi.common;

import lombok.Data;

import java.io.Serializable;

/**
 * id参数对象
 *
 * @author rykk
 * 
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}