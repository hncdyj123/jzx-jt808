package com.jzx.jt808.protocol.jt808;

import lombok.Data;

/**
 * 类描述：公共请求类
 *
 * @author yangjie
 * @date 2023-08-25 14:43
 **/
@Data
public class Jt808CommonReq implements java.io.Serializable {
    /**
     * 设备ID
     */
    private String macId;
    /**
     * 设备所在的host
     */
    private String host;
}
