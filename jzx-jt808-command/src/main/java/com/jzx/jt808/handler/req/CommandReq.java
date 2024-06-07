package com.jzx.jt808.handler.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 类描述：指令请求实体类
 *
 * @author yangjie
 * @date 2023-12-25 17:33
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandReq implements java.io.Serializable {
    /**
     * 设备ID
     */
    private String terminalId;
    /**
     * 消息ID
     */
    private String mainSign;
    /**
     * 数据区,一般由前后端约定好<br/>
     * 例如:下发设置指令0x8103,设置硬件连接地址<br/>
     * key:0x0013,value:123.123.123.123<br/>
     * key:0x0018,value:3059<br/>
     * 组包为：{"key":"0x0013",value:"123.123.123.123"}, {"key":"0x0018",value:"3059"}<br/>
     */
    private List<Map<String,Object>> paramsList;
}
