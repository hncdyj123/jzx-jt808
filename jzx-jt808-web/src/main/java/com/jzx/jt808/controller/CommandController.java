package com.jzx.jt808.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jzx.jt808.handler.req.CommandReq;
import com.jzx.jt808.service.CommandService;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 类描述：指令控制Controller
 *
 * @author yangjie
 * @date 2023-09-11 18:08
 **/
@Controller
@RequestMapping("/command")
public class CommandController {
    @Resource
    private CommandService commandService;

    @PostMapping("/setting")
    @ResponseBody
    public Object setting(@RequestBody CommandReq commandReq) {
        return commandService.sendCommand(commandReq);
    }
}
