package com.jzx.jt808.protocol.jt808;

import lombok.Data;

/**
 * 属性上报解析类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Data
public class Jt808QueryUploadReq extends Jt808CommonReq implements java.io.Serializable {
    /**
     * 终端类型
     */
    private Integer terminalType;
    /**
     * 制造商ID
     */
    private byte[] makeIdBytes = new byte[5];
    /**
     * 终端型号
     */
    private byte[] terminalModelBytes = new byte[20];
    /**
     * 终端ID
     */
    private String terminalId;
    /**
     * 终端SIM卡ICCID号
     */
    private String iccid;
    /**
     * 终端硬件版本号
     */
    private String hardwareVersion;
    /**
     * 终端固件版本号
     */
    private String firmwareVersion;
    /**
     * gnss模块属性
     */
    private byte gnssModelByte;
    /**
     * 通信模块属性
     */
    private byte communicationModelByte;

}
