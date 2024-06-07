package com.jzx.jt808.receive;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.dao.CommandDao;
import com.jzx.jt808.entity.CommandDto;
import com.jzx.jt808.enums.OrderStatusEnums;
import com.jzx.jt808.protocol.PlatformCommonReq;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 指令下发接收配置类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Component
public class CommandReceiveConfig {
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SessionChannelManager sessionChannelManager;
    @Resource
    private CommandDao commandDao;

    @PostConstruct
    public void initMethod() {
        new Thread(() -> {
            while (true) {
                try {
                    // 阻塞队列
                    HashMap commandMap = (HashMap)redisTemplate.opsForList()
                        .leftPop(BusinessConstants.SYSTEM_COMMAND_DOWN, Integer.MAX_VALUE, TimeUnit.SECONDS);

                    PlatformCommonReq platformCommonReq = BeanUtil.toBean(commandMap, PlatformCommonReq.class);

                    String terminalId = HexUtil.encodeHexStr(platformCommonReq.getTerminalId());

                    String mainSign = HexUtil.toHex(BitOperatorUtils.twoBytesToInteger(platformCommonReq.getMsgId()));

                    Session session = sessionChannelManager.findSessionByUnique(terminalId);

                    CommandDto commandDto = CommandDto.builder().build();

                    if (session == null) {
                        commandDto.setOrderStatus(OrderStatusEnums.OFFLINE.name());
                    } else {
                        Integer currentFlowId = session.getCurrentFlowId();
                        commandDto.setCurrentFlowId(currentFlowId);
                        // 获取session中的下发流水号
                        platformCommonReq
                            .setMsgSerialNumber(BitOperatorUtils.integerTo2Bytes(currentFlowId));
                        byte[] resultBytes = platformCommonReq.getResultByte();
                        String commandHexStr = HexUtil.format(HexUtil.encodeHexStr(platformCommonReq.getResultByte()));
                        boolean hasOk = sessionChannelManager.sendMsg(session.getChannel(), resultBytes);
                        if (!hasOk) {
                            commandDto.setOrderStatus(OrderStatusEnums.FAIL.name());
                        } else {
                            commandDto.setOrderStatus(OrderStatusEnums.DOWN.name());
                            commandDto.setDownTimestamp(DateUtil.current());
                        }
                        commandDto.setCommandHexStr(commandHexStr);
                    }
                    // 添加下发记录
                    commandDao.updateById(platformCommonReq.getId(), commandDto, BusinessConstants.COMMAND_COLLECTION_NAME);
                } catch (Exception e) {
                    log.error("CommandReceiveConfig initMethod() error:", e);
                }
            }
        }).start();
    }
}