package com.jzx.jt808.enums;

/**
 * 类描述：TODO
 *
 * @author yangjie
 * @date 2023-09-12 09:20
 **/
public enum OrderStatusEnums {
    INIT("初始化"),

    OFFLINE("设备不在线"),

    FAIL("下发失败"),

    DOWN("已下发"),

    REPLY("已回复");

    private String status;

    OrderStatusEnums(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
