package com.jzx.jt808.protocol.jt808;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 位置汇报请求类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Data
public class Jt808LocationReq extends Jt808CommonReq implements java.io.Serializable {
    /**
     * 全量数据包
     */
    private byte[] fullMsgBytes;
    /**
     * 告警标志
     */
    private Integer alarm;
    /**
     * 状态（参考JT808-2019表24）
     */
    private Integer status;
    /**
     * 纬度
     */
    private Integer lat;
    /**
     * 经度
     */
    private Integer lng;
    /**
     * 高程 海拔高度，单位为米
     */
    private Short elevation;
    /**
     * 速度(1/10km/h)
     */
    private Short speed;
    /**
     * 方向(0-359，正北为 0，顺时针)
     */
    private Short direct;
    /**
     * 时间（yyMMddHHmmss）
     */
    private String time;
    /**
     * 告警 主电状态(JT808 )
     */
    private Integer mainElecStatus;
    /**
     * acc开关（0：ACC 关；1：ACC 开）<br>
     * status第0位<br>
     */
    private Integer statusAcc;
    /**
     * 油路开关 (0：车辆油路正常；1：车辆油路断开)<br>
     * status第10位<br>
     */
    private Integer statusOil;
    /**
     * 电路开关(0：车辆电路正常；1：车辆电路断开)<br>
     * status第11位<br>
     */
    private Integer statusCircuit;
    /**
     * 门开关（0：车门解锁（撤防）；1：车门加锁（设防））<br>
     * status第12位<br>
     */
    private Integer statusDoor;
    /**
     * 0：未使用 GPS 卫星进行定位；1：使用 GPS 卫星进行定位<br>
     * status第18位<br>
     */
    private Integer statusGpsLoc;
    /**
     * 0：未使用北斗卫星进行定位；1：使用北斗卫星进行定位<br>
     * status第19位<br>
     */
    private Integer statusBeidouLoc;
    /**
     * 0：未使用 GLONASS 卫星进行定位；1：使用 GLONASS 卫星进行 定位<br>
     * status第20位<br>
     */
    private Integer statusGlonassLoc;
    /**
     * 0：未使用 Galileo 卫星进行定位；1：使用 Galileo 卫星进行 定位<br>
     * status第21位<br>
     */
    private Integer statusGalileoLoc;
    /**
     * 0：未定位；1：定位<br>
     * status第1位<br>
     */
    private Integer statusLocation;
    /**
     * 附加数据 里程：1/10km
     */
    private Integer extKm;
    /**
     * 附加数据 信号等级：无线通信网络信号强度 单位:byte
     */
    private Integer extGpsLevel;
    /**
     * 附加数据 GNSS 定位卫星数 单位：byte
     */
    private Integer extGnssCount;
    /**
     * 电流
     */
    private BigDecimal batteryQuantity;
    /**
     * 电压
     */
    private BigDecimal batteryVoltage;
    /**
     * 充电电压
     */
    private BigDecimal batteryRechargeVoltage;
    /**
     * 移动国家代码
     */
    private Integer mcc;
    /**
     * 移动网络号码
     */
    private Integer mnc;
    /**
     * 位置区域码
     */
    private Integer lac;
    /**
     * 基站编号
     */
    private Integer ci;
}
