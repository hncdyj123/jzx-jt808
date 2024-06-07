package com.jzx.jt808.dispatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzx.jt808.protocol.ServiceCommonResp;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import io.netty.channel.Channel;

/**
 * 设备上行消息消息解析接口类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
public interface Jt808MessageHandler {
    public final static Logger logger = LoggerFactory.getLogger(Jt808MessageHandler.class);
    public final static int MSG_MAX_LENGTH = 1024;
    public final static int SUCCESS_RESULT = 0;

    /**
     * 执行处理消息
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param channel TCP通道
     * @param packageData 数据包
     * @return
     */
    void execute(Channel channel, PackageData packageData);

    /**
     * 公共应答
     * 
     * @param packageData 解析包对象
     * @param session session通道
     * @param result 结果 0:成功/确认 1:失败 2:消息有误 3:不支持
     * @return byte[]
     */
    public static byte[] outputCommonBytes(PackageData packageData, Session session, int result) {
        ServiceCommonResp.Builder builder = ServiceCommonResp.newBuilder();
        // 终端手机号
        builder.setTerminalId(packageData.getMsgHeader().getHeadMsgTerminalId());
        // 消息体属性
        builder.setMsgProperties(generateMsgBodyProps(5, 0, false, 0));
        // 消息流水号(服务端产生)
        builder.setCurrentFlowId(session.currentFlowId());
        // 应答流水号(对应的终端消息的流水号)
        builder.setHeaderMsgSerialNumber(packageData.getMsgHeader().getHeaderMsgSerialNumber());
        // 应答ID(对应的终端消息的ID)
        builder.setHeaderMsgId(packageData.getMsgHeader().getHeaderMsgId());
        // 结果
        builder.setResult(result);
        ServiceCommonResp commonResp = builder.build();
        byte[] checkBytes = commonResp.getCheckByte();
        int checkSign = BitOperatorUtils.getCheckSumJt808(checkBytes, 0, checkBytes.length);
        byte[] resultBytes = commonResp.getResultByte(checkBytes, checkSign);
        resultBytes = covertSendBytes(resultBytes, 1, resultBytes.length - 1);
        return resultBytes;
    }

    /**
     * 非公共应答消息构建
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param packageData 解析包对象
     * @param session session通道
     * @param hexRespMsgByte 返回消息ID的byte[]
     * @param bodyBytes 返回内容byte[]
     * @return {@link byte[]}
     */
    public static byte[] outputRespMsgBytes(PackageData packageData, Session session, byte[] hexRespMsgByte,
        byte[] bodyBytes) {
        ServiceCommonResp.Builder builder = ServiceCommonResp.newBuilder();
        // 终端手机号
        builder.setTerminalId(packageData.getMsgHeader().getHeadMsgTerminalId());
        // 消息体属性(应答流水号2byte 结果1byte = 3 + 鉴权码的长度)
        builder.setMsgProperties(generateMsgBodyProps(bodyBytes.length, 0, false, 0));
        // 消息流水号(服务端产生)
        builder.setCurrentFlowId(session.currentFlowId());
        // 应答流水号(对应的终端消息的流水号)
        builder.setHeaderMsgSerialNumber(packageData.getMsgHeader().getHeaderMsgSerialNumber());
        // 返回消息ID
        builder.setRespMsgIdByte(hexRespMsgByte);
        builder.setResultByte(bodyBytes);
        ServiceCommonResp commonResp = builder.build();
        byte[] checkBytes = commonResp.getCheckByte();
        int checkSign = BitOperatorUtils.getCheckSumJt808(checkBytes, 0, checkBytes.length);
        byte[] resultBytes = commonResp.getResultByte(checkBytes, checkSign);
        resultBytes = covertSendBytes(resultBytes, 1, resultBytes.length - 1);
        return resultBytes;
    }

    /**
     * 获取消息体属性
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param msgLen 消息长度
     * @param encryptionType 加密类型
     * @param isSubPackage 是否有子包
     * @param reversed1415 保留位置
     * @return {@link int}
     */
    public static int generateMsgBodyProps(int msgLen, int encryptionType, boolean isSubPackage, int reversed1415) {
        // 消息体长度[0-9] (3FF) (0000,0011,1111,1111)
        // 加密类型[10-12] (1C00) (0001,1100,0000,0000)
        // 是否有子包[13] (2000) (0010,0000,0000,0000)
        // (保留位)[14-15] (C000) (1100,0000,0000,0000)
        if (msgLen >= MSG_MAX_LENGTH) {
            logger.warn("消息超过最大长度", msgLen);
        }
        int subPkg = isSubPackage ? 1 : 0;
        int ret = (msgLen & 0x3FF) | ((encryptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000)
            | ((reversed1415 << 14) & 0xC000);
        return ret & 0xffff;
    }

    /**
     * 转换输出数组
     *
     * @param bs
     * @param start
     * @param end
     * @return
     * @see
     */
    public static byte[] covertSendBytes(byte[] bs, int start, int end) {
        if (start < 0 || end > bs.length) {
            throw new ArrayIndexOutOfBoundsException("covertSendBytes error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.length + ")");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 头处理
            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }
            for (int i = start; i < end; i++) {
                if (bs[i] == 0x7e) {
                    baos.write(0x7d);
                    baos.write(0x02);
                } else if (bs[i] == 0x7d) {
                    baos.write(0x7d);
                    baos.write(0x01);
                } else {
                    baos.write(bs[i]);
                }
            }
            // 尾处理
            for (int i = end; i < bs.length; i++) {
                baos.write(bs[i]);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("covertSendBytes error:", e);
            throw e;
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.error("covertSendBytes close error:", e);
                }
                baos = null;
            }
        }
    }
}
