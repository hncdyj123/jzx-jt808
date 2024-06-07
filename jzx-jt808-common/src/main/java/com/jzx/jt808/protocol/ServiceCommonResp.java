package com.jzx.jt808.protocol;

import java.util.ArrayList;
import java.util.List;

import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.codec.BCD;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 公共返回实体类<br/>
 * 暂不考虑消息包封顶<br/>
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Slf4j
@Data
@NoArgsConstructor
public class ServiceCommonResp {
    /** 开始标识 **/
    private byte[] beginSign = new byte[] {0x7E};
    /** 消息ID **/
    private byte[] respMsgId = new byte[2];
    /** 消息体属性 **/
    private byte[] respBodyProperties = new byte[2];
    /** 终端手机号 **/
    private byte[] respTerminalId = new byte[] {};
    /** 消息流水号(服务端产生) **/
    private byte[] serverMsgSerialNumber = new byte[2];
    /** 应答消息体(公共包含应答流水号 + 应答ID + 应答结果) **/
    private byte[] result = new byte[] {};
    /** 校验码 **/
    private byte[] checkByte = new byte[1];
    /** 结束标识 **/
    private byte[] endSign = new byte[] {0x7E};

    private ServiceCommonResp(Builder builder) {
        this.respTerminalId = BCD.strToBcd(builder.terminalId);
        this.respBodyProperties = BitOperatorUtils.integerTo2Bytes(builder.msgProperties);
        this.serverMsgSerialNumber = BitOperatorUtils.integerTo2Bytes(builder.currentFlowId);
        // 公共返回包含应答流水号、应答ID、结果
        if (builder.resultByte == null) {
            List<byte[]> resultByteList = new ArrayList<byte[]>();
            byte[] terminalSerialNumberByte = BitOperatorUtils.integerTo2Bytes(builder.headerMsgSerialNumber);
            byte[] terminalMsgIdByte = BitOperatorUtils.integerTo2Bytes(builder.headerMsgId);
            byte[] resultByte = BitOperatorUtils.integerTo1Bytes(builder.result);
            resultByteList.add(terminalSerialNumberByte);
            resultByteList.add(terminalMsgIdByte);
            resultByteList.add(resultByte);
            this.result = BitOperatorUtils.concatAll(resultByteList);
        } else {
            // 非公共应答，根据结果组装一个resultByte
            this.result = builder.resultByte;
        }
        this.respMsgId = builder.respMsgIdByte;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Data
    @ToString
    public static final class Builder {
        /** 终端手机号 **/
        private String terminalId;
        /** 消息体属性 **/
        private int msgProperties;
        /** 消息流水号(服务端产生) **/
        private int currentFlowId;
        /** 应答流水号(对应的终端消息的流水号) **/
        private int headerMsgSerialNumber;
        /** 应答ID(对应的终端消息的ID) **/
        private int headerMsgId;
        /** 结果 **/
        private int result;
        /** 结果数组(用于不是公共返回) **/
        private byte[] resultByte;
        // 返回消息ID(0x80 0x01) **/
        private byte[] respMsgIdByte = new byte[] {-128, 01};

        public ServiceCommonResp build() {
            log.debug("Builder toString:{}", toString());
            return new ServiceCommonResp(this);
        }
    }

    /**
     * 获取校验校验数组
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param
     * @return {@link byte[]}
     */
    public byte[] getCheckByte() {
        // 校验码数组的长度为:消息头+消息体
        List<byte[]> checkByteList = new ArrayList<byte[]>();
        checkByteList.add(respMsgId);
        checkByteList.add(respBodyProperties);
        checkByteList.add(respTerminalId);
        checkByteList.add(serverMsgSerialNumber);
        checkByteList.add(result);
        byte[] checkByte = BitOperatorUtils.concatAll(checkByteList);
        return checkByte;
    }

    /**
     * 获取需要返回的数组
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param checkBytes 待校验的byte[]
     * @param checkSign crc校验值
     * @return {@link byte[]}
     */
    public byte[] getResultByte(byte[] checkBytes, int checkSign) {
        checkByte = BitOperatorUtils.integerTo1Bytes(checkSign);
        List<byte[]> resultByteList = new ArrayList<byte[]>();
        // 标识
        resultByteList.add(beginSign);
        // 校验数组(消息头 + 消息体)
        resultByteList.add(checkBytes);
        // 校验码
        resultByteList.add(checkByte);
        // 标识
        resultByteList.add(endSign);
        return BitOperatorUtils.concatAll(resultByteList);
    }
}
