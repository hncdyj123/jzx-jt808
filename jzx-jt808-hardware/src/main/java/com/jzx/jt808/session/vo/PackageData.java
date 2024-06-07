package com.jzx.jt808.session.vo;

import com.alibaba.fastjson2.annotation.JSONField;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;

/**
 * 指令解析实体类
 * 
 * @author 杨杰
 * @version 2019年4月12日
 * @see PackageData
 */
@Data
public class PackageData {
    /** 消息头解析类 **/
    private MsgHeader msgHeader;
    /** 消息体byte[] **/
    private byte[] msgBody;
    /** 校验码byte[1] **/
    private byte[] checkByte;
    /** 检验码 **/
    protected int checkSum;
    /** 数据包是否验证通过 **/
    private boolean isValid = true;

    @JSONField(serialize = false)
    private Channel channel;
    /** 消息体byte[] **/
    private byte[] fullMsgBytes;
    /**
     * 软件协议 2011 or 2013 or 2019
     */
    private String softVersion;

    /**
     * 获取命令标识
     * 
     * @return Integer
     */
    public Integer getMainSign() {
        return Integer.parseInt(msgHeader.getHeaderMsgId().toString());
    }

    /**
     * 消息头<br/>
     * 按照JT-808协议规范, 当前类中的Integer应该都定义成Short<br/>
     * 为了使用方便，因此定义成Integer<br/>
     *
     * @author 杨杰
     * @version 2019年5月16日
     * @see MsgHeader
     */
    @Data
    @ToString
    public static class MsgHeader {
        /** 原始消息的byte数组 **/
        private byte[] headMsgByte;
        /** 消息ID **/
        private Integer headerMsgId;
        /** 消息体属性 **/
        private Integer headerMsgAttribute;
        /** 消息体长度 **/
        private Integer msgLength;
        /** 数据加密方式 **/
        private int encryptionType;
        /** 是否分包 **/
        private boolean hasSubPackage;
        /** 终端手机号(终端唯一ID) **/
        private String headMsgTerminalId;
        /** 消息流水号 **/
        private Integer headerMsgSerialNumber;
        /** 总包数 **/
        private Integer headerMsgTotal;
        /** 序号 **/
        private Integer headerMsgSequence;
    }
}
