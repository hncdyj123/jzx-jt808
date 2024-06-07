package com.jzx.jt808.protocol.jt808;

import lombok.Data;

/**
 * 鉴权解析类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Data
public class Jt808AuthReq extends Jt808CommonReq implements java.io.Serializable {
    /**
     * 鉴权码
     */
    private String authCode;
}
