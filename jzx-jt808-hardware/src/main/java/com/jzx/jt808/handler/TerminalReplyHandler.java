package com.jzx.jt808.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.annotation.TerminalOrderType;
import com.jzx.jt808.cluster.NodeRegisterCluster;
import com.jzx.jt808.dao.CommandDao;
import com.jzx.jt808.dispatcher.Jt808MessageHandler;
import com.jzx.jt808.entity.CommandDto;
import com.jzx.jt808.enums.OrderStatusEnums;
import com.jzx.jt808.enums.TerminalOrderEnums;
import com.jzx.jt808.protocol.jt808.Jt808ReplyReq;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端公共应答消息handler类<br/>
 * 消息ID:0x0001<br/>
 * 
 * @author yangjie
 * @date 2023/2/10
 * @version 1.0.0
 */
@Slf4j
@Component
@TerminalOrderType(value = TerminalOrderEnums.TERMINAL_REPLY_REQ, initialize = true)
public class TerminalReplyHandler implements Jt808MessageHandler {
    @Resource
    private CommandDao commandDao;

    @Override
    public void execute(Channel channel, PackageData packageData) {
        log.info("TerminalCommonReplyHandler接收到的报文为:{}", JSON.toJSONString(packageData));
        try {
            byte[] body = packageData.getMsgBody();

            Jt808ReplyReq replyMsg = new Jt808ReplyReq();
            replyMsg.setHost(NodeRegisterCluster.REDIS_QUEUE);
            replyMsg.setMacId(packageData.getMsgHeader().getHeadMsgTerminalId());
            int pointer = 0;
            replyMsg.setReplyFlowId(BitOperatorUtils.twoBytesToInteger(ArrayUtil.sub(body, pointer, pointer + 2)));
            pointer += 2;
            replyMsg.setReplyMainSign(BitOperatorUtils.twoBytesToInteger(ArrayUtil.sub(body, pointer, pointer + 2)));
            pointer += 2;
            replyMsg.setResult((int)ArrayUtil.sub(body, pointer, ++pointer)[0]);

            Map<String, Object> criteriaMap = new HashMap<String, Object>();
            criteriaMap.put("terminalId", replyMsg.getMacId());
            criteriaMap.put("currentFlowId", replyMsg.getReplyFlowId());
            criteriaMap.put("mainSign", HexUtil.toHex(replyMsg.getReplyMainSign()));
            CommandDto commandDto = CommandDto.builder().orderStatus(OrderStatusEnums.REPLY.name())
                .result(replyMsg.getResult()).replyTimestamp(DateUtil.current()).build();
            // 更新指令结果
            commandDao.updateByCriteriaMap(criteriaMap, commandDto, BusinessConstants.COMMAND_COLLECTION_NAME);
        } catch (Exception e) {
            log.error("TerminalReplyHandler error:", e);
        }
    }
}
