package com.jzx.jt808.handler.jt808;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jzx.jt808.annotation.PlatFormOrderType;
import com.jzx.jt808.enums.PlatformOrderEnums;
import com.jzx.jt808.handler.PlatformCommonHandler;
import com.jzx.jt808.handler.req.CommandReq;
import com.jzx.jt808.protocol.PlatformCommonReq;
import com.jzx.jt808.utils.BitOperatorUtils;
import com.jzx.jt808.utils.Jt808MsgUtils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 类描述：平台下发设置类<br/>
 *
 * @author yangjie
 * @date 2023-09-11 18:16
 **/
@Slf4j
@Component
@PlatFormOrderType(value = PlatformOrderEnums.PLATFORM_SETTING_8103_REQ, initialize = true, orderDesc = "平台下发设置")
public class PlatformSettingHandler implements PlatformCommonHandler {

    @Override
    public PlatformCommonReq sendMessage(CommandReq commandReq) {
        // 设备ID
        String terminalId = commandReq.getTerminalId();
        List<Map<String, Object>> paramsList = commandReq.getParamsList();
        byte[] bodyBytes = new byte[] {};
        for (Map<String, Object> paramsMap : paramsList) {
            String key = Convert.toStr(paramsMap.get("key")).replaceAll("^0X|0x", "");
            Object value = Convert.toStr(paramsMap.get("value"));
            byte[] keyBytes = HexUtil.decodeHex(key);
            byte[] valueBytes = null;
            if (StrUtil.equalsIgnoreCase("0018", key)) {
                valueBytes = BitOperatorUtils.integerTo2Bytes(Convert.toInt(value));
            } else if (StrUtil.equalsIgnoreCase("0013", key)) {
                valueBytes = Convert.toStr(value).getBytes(StandardCharsets.UTF_8);
            }
            byte[] paramBytes =
                ArrayUtil.addAll(keyBytes, BitOperatorUtils.integerTo1Bytes(valueBytes.length), valueBytes);
            bodyBytes = ArrayUtil.addAll(bodyBytes, paramBytes);
        }
        // 消息体属性
        int headMsgProperties = Jt808MsgUtils.generateMsgBodyProps(bodyBytes.length, 0, false, 0);
        // 构造下发数据
        PlatformCommonReq platformCommonReq =
            new PlatformCommonReq.Builder().setHeaderMsgId(PlatformOrderEnums.PLATFORM_SETTING_8103_REQ.getOrder())
                .setHeadMsgProperties(headMsgProperties).setTerminalId(terminalId).setMsgBody(bodyBytes).build();
        return platformCommonReq;
    }
}
