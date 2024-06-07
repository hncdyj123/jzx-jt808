package com.jzx.jt808.service;

import com.alibaba.fastjson2.JSONObject;
import com.jzx.jt808.common.Message;
import com.jzx.jt808.handler.req.CommandReq;

/**
 * 类描述：指令下发Service
 *
 * @author yangjie
 * @date 2023-12-25 14:47
 **/
public interface CommandService {
    /**
     * 发送指令
     * 
     * @author yangjie
     * @date 2023/12/25
     * @param jsonObject
     * @return {@link String} 指令唯一ID
     */
    public Message sendCommand(CommandReq commandReq);
}
