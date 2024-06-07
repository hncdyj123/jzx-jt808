package com.jzx.jt808.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.Builder;
import lombok.Data;

/**
 * 日志收集实体类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Data
@Builder
public class ComLogDto implements java.io.Serializable {
    /** 服务端host **/
    @JSONField(name = "s_host")
    @Field("s_host")
    private String sHost;
    /** 服务端port **/
    @JSONField(name = "s_port")
    @Field("s_port")
    private Integer sPort;
    /** 服务端创建时间戳 **/
    @JSONField(name = "s_time")
    @Field("s_time")
    private Long sTime;
    /** 客户端host **/
    @JSONField(name = "c_host")
    @Field("c_host")
    private String cHost;
    /** 客户端port **/
    @JSONField(name = "c_port")
    @Field("c_port")
    private Integer cPort;
    /**
     * 客户端创建时间戳<br>
     * json、tagjson协议为txnNo<br>
     * jtt808无该字段<br>
     **/
    @JSONField(name = "txn_no")
    @Field("txn_no")
    private Long txnNo;
    /** 客户端唯一标识 **/
    @JSONField(name = "macid")
    @Field("macid")
    private String macId;
    /**
     * json、tagjson协议为json字符串<br>
     * jtt808为16进制字符串<br>
     **/
    private String datas;
    /**
     * 数据流向<br/>
     * 服务端到设备端<s->c><br/>
     * 设备端到服务端<c->s><br/>
     */
    private String target;

    /**
     * 服务器时间
     */
    private String serverTime;
}
