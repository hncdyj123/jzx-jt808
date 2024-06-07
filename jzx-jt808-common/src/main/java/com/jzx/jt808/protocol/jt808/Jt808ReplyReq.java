package com.jzx.jt808.protocol.jt808;

import lombok.Data;

/**
 * 终端公共回复类
 * 
 * @author yangjie
 * @date 2024/1/19
 * @version 1.0.0
 */
@Data
public class Jt808ReplyReq extends Jt808CommonReq implements java.io.Serializable {
    /**
     * 应答流水号<br/>
     * 对应的平台消息的流水号<br/>
     */
    private Integer replyFlowId;
    /**
     * 应答指令<br/>
     * 对应的平台消息的ID<br/>
     */
    private Integer replyMainSign;
    /**
     * 结果<br/>
     * 0:成功/确认 1:失败 2:消息有误 3:不支持<br/>
     */
    private Integer result;
}
