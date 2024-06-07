package com.jzx.jt808.protocol;

import com.jzx.jt808.utils.BcdUtils;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 平台公共请求实体类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformCommonReq {
    /** 开始标识 **/
    private byte[] beginSign = new byte[] {0x7E};
    /** 消息ID **/
    private byte[] msgId = new byte[2];
    /** 消息体属性 **/
    private byte[] msgBodyProperties = new byte[2];
    /** 终端手机号 **/
    private byte[] terminalId = new byte[] {};
    /** 消息流水号(服务端产生) **/
    private byte[] msgSerialNumber = new byte[2];
    /** 消息体byte[] **/
    private byte[] msgBody;
    /** 校验码 **/
    private byte[] checkByte = new byte[1];
    /** 结束标识 **/
    private byte[] endSign = new byte[] {0x7E};

    private String id;

    private PlatformCommonReq(Builder builder) {
        // 消息头:消息ID
        this.msgId = BitOperatorUtils.integerTo2Bytes(builder.getHeaderMsgId());
        // 消息头:消息体属性
        this.msgBodyProperties = BitOperatorUtils.integerTo2Bytes(builder.headMsgProperties);
        // 消息头:终端手机号
        this.terminalId = BcdUtils.strToBcd(builder.terminalId);
        // 消息头:消息流水号
        this.msgSerialNumber = BitOperatorUtils.integerTo2Bytes(builder.currentFlowId);
        // 消息体
        this.msgBody = builder.getMsgBody();
        byte[] checkBytes = ArrayUtil.addAll(msgId, msgBodyProperties, terminalId, msgSerialNumber, msgBody);
        // 检验码
        this.checkByte =
            BitOperatorUtils.integerTo1Bytes(BitOperatorUtils.getCheckSumJt808(checkBytes, 0, checkBytes.length));

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @ToString
    public static final class Builder {
        /**
         * 消息头:消息ID
         */
        private int headerMsgId;
        /**
         * 消息头:消息体属性:<br>
         * |15 |14| 13 |12| 11 |10 |9|8|7|6|5|4|3|2|1|0|<br/>
         * | 保留 | 分包| 数据加密方式 | 消息体长度 |<br/>
         */
        private int headMsgProperties;
        /**
         * 消息头:终端手机号
         */
        private String terminalId;

        /**
         * 消息头:消息流水号
         */
        private int currentFlowId;

        /**
         * 消息体
         */
        private byte[] msgBody;

        public int getHeaderMsgId() {
            return headerMsgId;
        }

        public Builder setHeaderMsgId(int headerMsgId) {
            this.headerMsgId = headerMsgId;
            return this;
        }

        public int getHeadMsgProperties() {
            return headMsgProperties;
        }

        public Builder setHeadMsgProperties(int headMsgProperties) {
            this.headMsgProperties = headMsgProperties;
            return this;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public Builder setTerminalId(String terminalId) {
            this.terminalId = terminalId;
            return this;
        }

        public int getCurrentFlowId() {
            return currentFlowId;
        }

        public Builder setCurrentFlowId(int currentFlowId) {
            this.currentFlowId = currentFlowId;
            return this;
        }

        public byte[] getMsgBody() {
            return msgBody;
        }

        public Builder setMsgBody(byte[] msgBody) {
            this.msgBody = msgBody;
            return this;
        }

        public PlatformCommonReq build() {
            log.debug("Builder toString:{}", toString());
            return new PlatformCommonReq(this);
        }
    }

    /**
     * 输出完成数据包
     * 
     * @return
     * @throws Exception
     */
    public byte[] getResultByte() throws Exception {
        // 获取待转义数组
        byte[] waitEscapeBytes =
            ArrayUtil.addAll(msgId, msgBodyProperties, terminalId, msgSerialNumber, msgBody, checkByte);
        byte[] escapeBytes = BitOperatorUtils.serverSendEscape(waitEscapeBytes, 0, waitEscapeBytes.length);
        return ArrayUtil.addAll(beginSign, escapeBytes, endSign);
    }

}
