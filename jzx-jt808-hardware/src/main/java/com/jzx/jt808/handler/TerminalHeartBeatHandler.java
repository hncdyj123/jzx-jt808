package com.jzx.jt808.handler;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.annotation.TerminalOrderType;
import com.jzx.jt808.cluster.NodeRegisterCluster;
import com.jzx.jt808.dispatcher.Jt808MessageHandler;
import com.jzx.jt808.enums.TerminalOrderEnums;
import com.jzx.jt808.protocol.jt808.Jt808HeartReq;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端心跳消息Handler类<br/>
 * 消息ID:0x0002<br/>
 * 
 * @author yangjie
 * @date 2023/2/10
 * @version 1.0.0
 */
@Slf4j
@Component
@TerminalOrderType(value = TerminalOrderEnums.TERMINAL_HEART_BEAT_REQ, initialize = true)
public class TerminalHeartBeatHandler implements Jt808MessageHandler {
    @Autowired
    private SessionChannelManager sessionManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void execute(Channel channel, PackageData packageData) {
        log.info("TerminalHeartBeatHandler接收到的报文为:{}", JSON.toJSONString(packageData));
        try {
            String terminalUniqueId = packageData.getMsgHeader().getHeadMsgTerminalId();
            sessionManager.put(terminalUniqueId, channel);
            Session session = sessionManager.findSessionByChannelId(channel.id().asLongText());

            // 通用应答
            sessionManager.sendMsg(channel,
                Jt808MessageHandler.outputCommonBytes(packageData, session, Jt808MessageHandler.SUCCESS_RESULT));

            byte[] bodyBytes = packageData.getMsgBody();
            Jt808HeartReq jt808HeartReq = new Jt808HeartReq();
            jt808HeartReq.setMacId(terminalUniqueId);
            jt808HeartReq.setHost(NodeRegisterCluster.REDIS_QUEUE);
            // 向MQ发送心跳消息
            rabbitTemplate.convertAndSend(BusinessConstants.JT808_HEART_EXCHANGE,
                BusinessConstants.JT808_HEART_ROUTE_KEY, JSON.toJSONString(jt808HeartReq));
        } catch (Exception e) {
            log.error("TerminalHeartBeatHandler error:", e);
        }
    }
}
