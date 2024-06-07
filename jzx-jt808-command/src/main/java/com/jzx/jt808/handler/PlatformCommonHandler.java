package com.jzx.jt808.handler;

import com.alibaba.fastjson2.JSONObject;
import com.jzx.jt808.handler.req.CommandReq;
import com.jzx.jt808.protocol.PlatformCommonReq;

/**
 * 平台指令下发公共接口类
 * 
 * @author yangjie
 * @date 2023/12/22
 * @version 1.0.0
 */
public interface PlatformCommonHandler {

    /**
     * 构建下发消息
     * 
     * @author yangjie
     * @date 2023/12/22
     * @param jsonObject 下发数据,由前端和后端约定
     * @return {@link String} 返回消息唯一编号
     */
    public PlatformCommonReq sendMessage(CommandReq commandReq);
}
