package com.jzx.jt808.entity;

import lombok.*;

/**
 * 类描述：指令传输Dto
 *
 * @author yangjie
 * @date 2023-09-11 18:24
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CommandDto {
    /**
     * mongo新增后返回ID
     */
    private String id;
    /**
     * 设备ID
     */
    private String terminalId;
    /**
     * 主指令
     */
    private String mainSign;
    /**
     * 设备ID
     */
    private Integer currentFlowId;
    /**
     * 指令状态<br/>
     * 初始化:INIT<br/>
     * 设备不在线:OFFLINE<br/>
     * 下发失败:FAIL<br/>
     * 已下发:DOWN<br/>
     * 已回复:REPLY<br/>
     */
    private String orderStatus;
    /**
     * 指令内容
     */
    private String orderBody;
    /**
     * 指令内容 16进制字符串
     */
    private String commandHexStr;
    /**
     * 下发时间
     */
    private Long downTimestamp;
    /**
     * 创建时间
     */
    private Long createTimestamp;
    /**
     * 回复时间
     */
    private Long replyTimestamp;
    /**
     * 结果<br/>
     * 0:成功/确认 1:失败 2:消息有误 3:不支持<br/>
     */
    private Integer result;
}
