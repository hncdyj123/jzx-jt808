package com.jzx.jt808.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 类描述：终端指令枚举类
 *
 * @author yangjie
 * @date 2023-08-25 11:42
 **/
@AllArgsConstructor
public enum TerminalOrderEnums {
    /** 终端注册 **/
    TERMINAL_LOGIN_REQ(0x0100, "终端注册请求", "client -> server"),
    /** 终端注册响应 **/
    TERMINAL_LOGIN_RESP(0x8100, "终端注册响应", "server -> client"),
    /** 终端鉴权 **/
    TERMINAL_AUTH_REQ(0x0102, "终端鉴权", "client -> server"),
    /** 终端公共应答消息 **/
    TERMINAL_REPLY_REQ(0x0001, "终端应答", "client -> server"),
    /** 终端心跳 **/
    TERMINAL_HEART_BEAT_REQ(0x0002, "终端心跳", "client -> server"),
    /** 终端位置上报 **/
    TERMINAL_LOCATION_REQ(0x0200, "终端位置上报", "client -> server"),
    /** 终端位置信息查询上报 **/
    TERMINAL_LOCATION_QUERY_REQ(0x0201, "终端位置信息查询上报", "client -> server"),
    /** 终端配置查询上报 **/
    TERMINAL_QUERY_REQ(0x0107, "终端位置上报", "client -> server"),
    /** 终端数据透传上报 **/
    TERMINAL_TRANSPARENT_REQ(0x0900, "终端数据透传上报", "client -> server"),
    /** 终端注销 **/
    TERMINAL_LOGOUT_REQ(0x0003, "终端注销", "client -> server"),
    /** 设置终端参数 **/
    TERMINAL_LOCATION_INFO_UPLOAD_REQ(0x8103, "设置终端参数", "client -> server");

    /**
     * 指令ID
     */
    @Getter
    @Setter
    private int order;
    /**
     * 描述
     */
    @Getter
    @Setter
    private String orderDesc;
    /**
     * 数据方向(用于日志展示)
     */
    @Getter
    @Setter
    private String target;
}
