package com.jzx.jt808.service.impl;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.common.Message;
import com.jzx.jt808.container.PlatformHandlerContainer;
import com.jzx.jt808.dao.CommandDao;
import com.jzx.jt808.entity.CommandDto;
import com.jzx.jt808.enums.OrderStatusEnums;
import com.jzx.jt808.handler.PlatformCommonHandler;
import com.jzx.jt808.handler.req.CommandReq;
import com.jzx.jt808.protocol.PlatformCommonReq;
import com.jzx.jt808.service.CommandService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.http.HttpStatus;

/**
 * 类描述：指令下发ServiceImpl
 *
 * @author yangjie
 * @date 2023-12-25 14:48
 **/
@Component
public class CommandServiceImpl implements CommandService {
    @Resource
    private PlatformHandlerContainer platformHandlerContainer;
    @Resource
    private CommandDao commandDao;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Message sendCommand(CommandReq commandReq) {
        Message message = new Message();
        String terminalId = commandReq.getTerminalId();
        String mainSign = commandReq.getMainSign();
        PlatformCommonHandler platformCommonHandler =
            platformHandlerContainer.getPlatformCommonHandler(HexUtil.hexToInt(mainSign));
        if (platformCommonHandler == null) {
            message.setCode(HttpStatus.HTTP_INTERNAL_ERROR);
            message.setMessage("未发现指令类,请检查[mainSign]参数.");
            return message;
        }
        CommandDto commandDto = CommandDto.builder().terminalId(terminalId).mainSign(mainSign)
            .orderStatus(OrderStatusEnums.INIT.name()).createTimestamp(DateUtil.current()).build();
        commandDao.insert(commandDto, BusinessConstants.COMMAND_COLLECTION_NAME);
        message.setResult(new HashMap<String, String>() {
            {
                put("msgId", commandDto.getId());
            }
        });
        // 构建指令
        PlatformCommonReq platformCommonReq = platformCommonHandler.sendMessage(commandReq);
        platformCommonReq.setId(commandDto.getId());
        // 推送下发消息
        redisTemplate.opsForList().rightPush(BusinessConstants.SYSTEM_COMMAND_DOWN, platformCommonReq);
        return message;
    }
}
