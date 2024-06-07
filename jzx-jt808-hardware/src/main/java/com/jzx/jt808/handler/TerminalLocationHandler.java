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
import com.jzx.jt808.protocol.jt808.Jt808LocationReq;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端位置上报Handler类<br/>
 * 消息ID:0x0200<br/>
 * 
 * @author yangjie
 * @date 2023/2/10
 * @version 1.0.0
 */
@Slf4j
@Component
@TerminalOrderType(value = TerminalOrderEnums.TERMINAL_LOCATION_REQ, initialize = true)
public class TerminalLocationHandler implements Jt808MessageHandler {
    @Autowired
    private SessionChannelManager sessionManager;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final static int BODY_LENGTH = 28;

    @Override
    public void execute(Channel channel, PackageData packageData) {
        log.info("TerminalLocationHandler接收到的报文为：{}", JSON.toJSONString(packageData));
        try {
            String terminalUniqueId = packageData.getMsgHeader().getHeadMsgTerminalId();
            sessionManager.put(terminalUniqueId, channel);
            Session session = sessionManager.findSessionByChannelId(channel.id().asLongText());

            sessionManager.sendMsg(channel,
                Jt808MessageHandler.outputCommonBytes(packageData, session, Jt808MessageHandler.SUCCESS_RESULT));

            byte[] bodyBytes = packageData.getMsgBody();
            Jt808LocationReq locationReq = getJt808LocationReq(packageData, bodyBytes);
            // 向MQ发送位置上报消息
            rabbitTemplate.convertAndSend(BusinessConstants.JT808_LOCATION_EXCHANGE,
                BusinessConstants.JT808_LOCATION_ROUTE_KEY, JSON.toJSONString(locationReq));
        } catch (Exception e) {
            log.error("TerminalLocationHandler error:", e);
        }
    }

    /**
     * 抽取位置解析，位置上报也能解析
     * 
     * @author yangjie
     * @date 2022/11/16
     * @param packageData
     * @return {@link Jt808LocationReq}
     */
    protected Jt808LocationReq getJt808LocationReq(PackageData packageData, byte[] bodyBytes) {
        Jt808LocationReq locationReq = new Jt808LocationReq();
        locationReq.setMacId(packageData.getMsgHeader().getHeadMsgTerminalId());
        locationReq.setHost(NodeRegisterCluster.REDIS_QUEUE);
        locationReq.setFullMsgBytes(packageData.getFullMsgBytes());
        locationReq.setAlarm(NumberUtil.toInt(ArrayUtil.sub(bodyBytes, 0, 4)));
        locationReq.setStatus(NumberUtil.toInt(ArrayUtil.sub(bodyBytes, 4, 8)));
        locationReq.setLat(NumberUtil.toInt(ArrayUtil.sub(bodyBytes, 8, 12)));
        locationReq.setLng(NumberUtil.toInt(ArrayUtil.sub(bodyBytes, 12, 16)));
        locationReq.setElevation((short)BitOperatorUtils.byteToInteger(ArrayUtil.sub(bodyBytes, 16, 18)));
        locationReq.setSpeed((short)BitOperatorUtils.byteToInteger(ArrayUtil.sub(bodyBytes, 18, 20)));
        locationReq.setDirect((short)BitOperatorUtils.byteToInteger(ArrayUtil.sub(bodyBytes, 20, 22)));
        locationReq.setTime(BCD.bcdToStr(ArrayUtil.sub(bodyBytes, 22, 28)));
        // 状态解析
        int status = locationReq.getStatus();
        locationReq.setStatusAcc(status & 0x01);
        locationReq.setStatusOil((status >> 10) & 0x01);
        locationReq.setStatusCircuit((status >> 11) & 0x01);
        locationReq.setStatusDoor((status >> 12) & 0x01);
        locationReq.setStatusGpsLoc((status >> 18) & 0x01);
        locationReq.setStatusBeidouLoc((status >> 19) & 0x01);
        locationReq.setStatusGlonassLoc((status >> 20) & 0x01);
        locationReq.setStatusGalileoLoc((status >> 21) & 0x01);
        locationReq.setStatusLocation((status >> 1) & 0x01);
        locationReq.setMainElecStatus((locationReq.getAlarm() >> 8) & 0x01);
        // 有附加数据
        if (bodyBytes.length > BODY_LENGTH) {
            byte[] extBytes = ArrayUtil.sub(bodyBytes, 28, bodyBytes.length);
            int position = 0;
            for (int i = 0; i < extBytes.length;) {
                if (position == extBytes.length) {
                    break;
                }
                int extId = BitOperatorUtils.oneByteToInteger(extBytes[position]);
                position += 1;
                int extLength = BitOperatorUtils.byteToInteger(ArrayUtil.sub(extBytes, position, position + 1));
                position += 1;
                // 里程数据
                if (extId == 0x01) {
                    byte[] kmExtBytes = ArrayUtil.sub(extBytes, position, position + extLength);
                    locationReq.setExtKm(BitOperatorUtils.byteToInteger(kmExtBytes) / 10);
                    position += extLength;
                    continue;
                }
                // 信号等级：无线通信网络信号强度
                else if (extId == 0x30) {
                    byte[] gpsLevelBytes = ArrayUtil.sub(extBytes, position, position + extLength);
                    locationReq.setExtGpsLevel(BitOperatorUtils.byteToInteger(gpsLevelBytes));
                    position += extLength;
                    continue;
                }
                // GNSS 定位卫星数
                else if (extId == 0x31) {
                    byte[] gnssCountBytes = ArrayUtil.sub(extBytes, position, position + extLength);
                    locationReq.setExtGnssCount(BitOperatorUtils.byteToInteger(gnssCountBytes));
                    position += extLength;
                    continue;
                } else if (extId == 0xe3) {
                    // 解析电压，电流，充电电压
                    byte[] batteryDataBytes = ArrayUtil.sub(extBytes, position, position + extLength);
                    double batteryQuantity =
                        new Double(BitOperatorUtils.byteToInteger(ArrayUtil.sub(batteryDataBytes, 0, 2))) * 0.01;
                    locationReq.setBatteryQuantity(NumberUtil.round(batteryQuantity, 2));
                    double batteryVoltage =
                        new Double(BitOperatorUtils.byteToInteger(ArrayUtil.sub(batteryDataBytes, 2, 4))) * 0.01;
                    locationReq.setBatteryVoltage(NumberUtil.round(batteryVoltage, 2));
                    double batteryRechargeVoltage =
                        new Double(BitOperatorUtils.byteToInteger(ArrayUtil.sub(batteryDataBytes, 4, 6))) * 0.01;
                    locationReq.setBatteryRechargeVoltage(NumberUtil.round(batteryRechargeVoltage, 2));
                    position += extLength;
                    continue;
                } else if (extId == 0x82) {
                    byte[] cellBytes = ArrayUtil.sub(extBytes, position, position + extLength);
                    locationReq.setMcc(BitOperatorUtils.byteToInteger(ArrayUtil.sub(cellBytes, 0, 2)));
                    locationReq.setMnc(BitOperatorUtils.byteToInteger(ArrayUtil.sub(cellBytes, 2, 4)));
                    locationReq.setCi(BitOperatorUtils.byteToInteger(ArrayUtil.sub(cellBytes, 4, 8)));
                    locationReq.setLac(BitOperatorUtils.byteToInteger(ArrayUtil.sub(cellBytes, 8, 12)));
                    position += extLength;
                    continue;
                } else {
                    position += (2 + extLength);
                    log.warn("位置解析未命中,extId = {} 解析逻辑", HexUtil.toHex(extId));
                    continue;
                }
            }
        }
        return locationReq;
    }
}
