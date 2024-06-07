package com.jzx.jt808.handler;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.annotation.TerminalOrderType;
import com.jzx.jt808.cluster.NodeRegisterCluster;
import com.jzx.jt808.dispatcher.Jt808MessageHandler;
import com.jzx.jt808.enums.TerminalOrderEnums;
import com.jzx.jt808.protocol.jt808.Jt808LoginReq;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端登录消息Handler类<br/>
 * 消息ID：0x0100<br/>
 *
 * @author yangjie
 * @date 2023/2/10
 * @version 1.0.0
 */
@Slf4j
@Component
@TerminalOrderType(value = TerminalOrderEnums.TERMINAL_LOGIN_REQ, initialize = true)
public class TerminalLoginHandler implements Jt808MessageHandler {
    @Autowired
    private SessionChannelManager sessionManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Value("${business.authCode}")
    private String authCode;
    private final static int BODY_LENGTH = 25;

    @Override
    public void execute(Channel channel, PackageData packageData) {
        log.info("TerminalLoginHandler接收到的报文为：{}", JSON.toJSONString(packageData));
        try {
            byte[] body = packageData.getMsgBody();
            Jt808LoginReq loginMsg = new Jt808LoginReq();
            loginMsg.setHost(NodeRegisterCluster.REDIS_QUEUE);
            loginMsg.setMacId(packageData.getMsgHeader().getHeadMsgTerminalId());
            // 省域ID[0-2]
            loginMsg.setProvinceId(BitOperatorUtils.byteToInteger(BitOperatorUtils.subByte(body, 0, 2, 2)));
            // 市县域ID[2-4]
            loginMsg.setCityId(BitOperatorUtils.byteToInteger(BitOperatorUtils.subByte(body, 2, 4, 2)));
            // 制造商ID[4-9]
            loginMsg.setManufacturerId(HexUtil.encodeHexStr(BitOperatorUtils.subByte(body, 4, 9, 5)));
            // 终端型号[9-17]
            loginMsg.setTerminalType(HexUtil.encodeHexStr(BitOperatorUtils.subByte(body, 9, 17, 8)));
            // 终端 ID[17-24]
            loginMsg.setTerminalId(HexUtil.encodeHexStr(BitOperatorUtils.subByte(body, 17, 24, 7)));
            // 车牌颜色[24-25] 偷懒 直接强转 不会越界
            loginMsg.setPlateColor((int)BitOperatorUtils.subByte(body, 24, 25, 1)[0]);
            if (body.length > BODY_LENGTH) {
                // 车牌[25-n]
                byte[] plateByte = BitOperatorUtils.subByte(body, 25, body.length, body.length - 25);
                String licensePlate = new String(plateByte);
                loginMsg.setLicensePlate(licensePlate);
            }
            Session session = sessionManager.findSessionByChannelId(channel.id().asLongText());
            byte[] msgSerialNumberBytes =
                BitOperatorUtils.integerTo2Bytes(packageData.getMsgHeader().getHeaderMsgSerialNumber());
            byte[] resultBytes = new byte[] {0x00};
            byte[] authBytes = authCode.getBytes(StandardCharsets.UTF_8);
            // 终端注册应答 应答流水号2byte + 结果1byte+鉴权码(nByte)
            byte[] bodyBytes = ArrayUtil.addAll(msgSerialNumberBytes, resultBytes, authBytes);
            sessionManager.sendMsg(channel, Jt808MessageHandler.outputRespMsgBytes(packageData, session,
                BitOperatorUtils.integerTo2Bytes(TerminalOrderEnums.TERMINAL_LOGIN_RESP.getOrder()), bodyBytes));
            // 向MQ发送登录消息
            rabbitTemplate.convertAndSend(BusinessConstants.JT808_LOGIN_EXCHANGE,
                BusinessConstants.JT808_LOGIN_ROUTE_KEY, JSON.toJSONString(loginMsg));
        } catch (Exception e) {
            log.error("TerminalLoginHandler error:", e);
        }
    }
}
