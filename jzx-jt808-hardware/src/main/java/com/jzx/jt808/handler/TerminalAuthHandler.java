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
import com.jzx.jt808.protocol.jt808.Jt808AuthReq;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.codec.BCD;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端鉴权消息handler类<br/>
 * 消息ID:0x0102<br/>
 * 
 * @author yangjie
 * @date 2023/2/10
 * @version 1.0.0
 */
@Slf4j
@Component
@TerminalOrderType(value = TerminalOrderEnums.TERMINAL_AUTH_REQ, initialize = true)
public class TerminalAuthHandler implements Jt808MessageHandler {
    @Autowired
    private SessionChannelManager sessionManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void execute(Channel channel, PackageData packageData) {
        log.info("TerminalAuthHandler接收到的报文为:{}", JSON.toJSONString(packageData));
        try {
            // session鉴权并且设置协议
            String terminalUniqueId = packageData.getMsgHeader().getHeadMsgTerminalId();
            channel.attr(AttributeKey.valueOf(BusinessConstants.PROTOCOL_KEY)).set(BusinessConstants.PROTOCOL_JTT808);
            sessionManager.put(terminalUniqueId, channel);

            byte[] body = packageData.getMsgBody();
            Jt808AuthReq authMsg = new Jt808AuthReq();
            authMsg.setHost(NodeRegisterCluster.REDIS_QUEUE);
            authMsg.setMacId(packageData.getMsgHeader().getHeadMsgTerminalId());
            authMsg.setAuthCode(BCD.bcdToStr(BitOperatorUtils.subByte(body, 0, body.length, body.length)));
            if (log.isDebugEnabled()) {
                log.debug("终端鉴权:{}", JSON.toJSONString(authMsg));
            }

            Session session = sessionManager.findSessionByChannelId(channel.id().asLongText());
            // 通用应答
            sessionManager.sendMsg(channel,
                Jt808MessageHandler.outputCommonBytes(packageData, session, Jt808MessageHandler.SUCCESS_RESULT));
            // 向MQ发送鉴权消息
            rabbitTemplate.convertAndSend(BusinessConstants.JT808_AUTH_EXCHANGE, BusinessConstants.JT808_AUTH_ROUTE_KEY,
                JSON.toJSONString(authMsg));
        } catch (Exception e) {
            log.error("TerminalAuthHandler error:", e);
        }
    }
}
