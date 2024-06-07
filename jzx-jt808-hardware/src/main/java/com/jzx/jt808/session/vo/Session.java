package com.jzx.jt808.session.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;

/**
 * TCP链接自定义Session实体类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Data
@Builder
public class Session implements java.io.Serializable {
    /**
     * 意义，目的和功能，以及被用到的地方<br>
     */
    private static final long serialVersionUID = -8347176321910590011L;
    /** sessionId **/
    private String id;
    /** 终端唯一标识 **/
    private String userUnique;
    /** channel通道 **/
    @JsonIgnore
    @Builder.Default
    @JSONField(serialize = false)
    private Channel channel = null;
    /** 是否鉴权 **/
    @Builder.Default
    private boolean isAuth = false;
    /** 客户端上次的连接时间 **/
    @Builder.Default
    private long lastConnectionTime = 0L;
    /** 协议类型(JT808or自定义 设备鉴权写入) **/
    private String protocol;
    /** 远程ip **/
    private String serverHost;
    /** 远程port **/
    private Integer serverPort;
    @JSONField(serialize = false)
    private int currentFlowId;
    @Builder.Default
    @JSONField(serialize = false)
    private int max = 0xFFFF;

    public synchronized int currentFlowId() {
        if (currentFlowId >= max) {
            currentFlowId = 0;
        }
        return currentFlowId++;
    }
}