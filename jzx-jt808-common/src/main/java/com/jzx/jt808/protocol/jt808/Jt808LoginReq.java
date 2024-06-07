package com.jzx.jt808.protocol.jt808;

import lombok.Data;

/**
 * 登录请求类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Data
public class Jt808LoginReq extends Jt808CommonReq implements java.io.Serializable {
    /**
     * 省份ID
     */
    private Integer provinceId;
    /**
     * 城市ID
     */
    private Integer cityId;
    /**
     * 区域ID
     */
    private String manufacturerId;
    /**
     * 设备类型
     */
    private String terminalType;
    /**
     * 设备ID
     */
    private String terminalId;
    /**
     * 车牌颜色
     */
    private Integer plateColor;
    /**
     * 车牌号码
     */
    private String licensePlate;
}
