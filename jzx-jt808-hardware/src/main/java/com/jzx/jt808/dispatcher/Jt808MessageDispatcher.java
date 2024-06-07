package com.jzx.jt808.dispatcher;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.jzx.jt808.BusinessConstants;
import com.jzx.jt808.enums.TerminalOrderEnums;
import com.jzx.jt808.factory.ExecutorFactory;
import com.jzx.jt808.session.SessionChannelManager;
import com.jzx.jt808.session.vo.PackageData;
import com.jzx.jt808.session.vo.Session;
import com.jzx.jt808.utils.BitOperatorUtils;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息抽象业务处理类
 * 
 * @author yangjie
 * @date 2023/8/25
 * @version 1.0.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class Jt808MessageDispatcher extends ChannelInboundHandlerAdapter {
    @Resource
    private MessageHandlerContainer messageHandlerContainer;
    @Resource
    private SessionChannelManager sessionManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        try {
            ByteBuf buf = (ByteBuf)msg;
            if (buf.readableBytes() <= 0) {
                return;
            }
            byte[] bs = new byte[buf.readableBytes()];
            buf.readBytes(bs);

            // 格式化消息为16进制字符串
            byte[] flagBytes = new byte[] {BusinessConstants.PACKAGE_HEADER_JT808};
            String dataMsg =
                HexUtil.format(HexUtil.encodeHexStr(ArrayUtil.addAll(flagBytes, bs, flagBytes))).toUpperCase();
            if (log.isInfoEnabled()) {
                log.info("[client -> server],data: {}", dataMsg);
            }
            // 数据转义
            byte[] covertByte = covertByte(bs);
            PackageData packageData = this.commonAnalysis(covertByte);
            if (!packageData.isValid()) {
                // 检验码不一致关闭连接
                ctx.channel().close();
                return;
            }
            packageData.setChannel(ctx.channel());
            // 非登录||心跳请求，需要验证是否鉴权
            if (!(packageData.getMainSign() == TerminalOrderEnums.TERMINAL_LOGIN_REQ.getOrder()
                || packageData.getMainSign() == TerminalOrderEnums.TERMINAL_AUTH_REQ.getOrder())) {
                Session session = sessionManager.findSessionByChannelId(ctx.channel().id().asLongText());
                if (!session.isAuth()) {
                    ctx.close();
                    return;
                }
            }
            // 获得 type 对应的 MessageHandler 处理器
            Jt808MessageHandler messageHandler =
                messageHandlerContainer.getJt808MessageHandler(packageData.getMainSign());
            if (messageHandler == null) {
                log.warn("Jt808MessageHandler找不到处理器,msg:{}", msg);
                return;
            }
            // 执行逻辑
            ExecutorFactory.getInstance().submit(() -> {
                messageHandler.execute(ctx.channel(), packageData);
            });
            // 构建请求日志数据投递
            String macId = packageData.getMsgHeader().getHeadMsgTerminalId();
            sessionManager.buildLogMsg(ctx.channel(), macId, dataMsg, "[client->server]");
        } catch (Exception ex) {
            log.error("解析包错误:{}", ex);
            ctx.channel().close();
        } finally {
            release(msg);
        }
    }

    /**
     * 处理转义字符<br/>
     * 转换前：0x30 0x7d 0x02 0x08 0x7d 0x01 0x55<br/>
     * 转换后：0x30 0x7e 0x08 0x7d 0x55<br/>
     * 
     * @param bs
     * @return
     * @see
     */
    private byte[] covertByte(byte[] bs) {
        List<Byte> transByteList = new ArrayList<Byte>();
        for (int i = 0; i < bs.length; i++) {
            transByteList.add(bs[i]);
        }
        for (int i = transByteList.size() - 1; i >= 0; i--) {
            if (i == 0) {
                break;
            }
            if (transByteList.get(i - 1) == 0x7d && transByteList.get(i) == 0x02) {
                transByteList.remove(i);
                transByteList.set(i - 1, new Byte((byte)0x7e));

            } else if (transByteList.get(i - 1) == 0x7d && transByteList.get(i) == 0x01) {
                transByteList.remove(i);
                transByteList.set(i - 1, new Byte((byte)0x7d));
            }
        }

        byte[] transByte = new byte[transByteList.size()];
        for (int i = 0; i < transByteList.size(); i++) {
            transByte[i] = transByteList.get(i);
        }
        return transByte;
    }

    /**
     * ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放 <br/>
     * 请记住处理器的职责是释放所有传递到处理器的引用计数对象<br/>
     * 抛弃收到的数据<br/>
     */
    private void release(Object msg) {
        try {
            while (true) {
                boolean ref = ReferenceCountUtil.release(msg);
                if (ref) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息解析抽象方法
     * 
     * @author yangjie
     * @date 2023/8/25
     * @param bs 消息体,不包含包头、包尾
     * @return {@link PackageData}
     */
    public PackageData commonAnalysis(byte[] bs) {
        PackageData packageData = new PackageData();
        PackageData.MsgHeader msgHeader = new PackageData.MsgHeader();
        // 消息ID
        msgHeader.setHeaderMsgId(BitOperatorUtils.byteToInteger(ArrayUtil.sub(bs, 0, 2)));
        // 消息体属性(WORD)
        msgHeader.setHeaderMsgAttribute(BitOperatorUtils.byteToInteger(ArrayUtil.sub(bs, 2, 4)));
        // 以下运算如果看不懂，请复习位操作相关姿势
        // 消息体长度 [0-9] 0000,0011,1111,1111(0x1FF)
        msgHeader.setMsgLength(msgHeader.getHeaderMsgAttribute() & 0x1FF);
        // 消息加密方式[10] 1110,0000,0000(0xE00)
        msgHeader.setEncryptionType((msgHeader.getHeaderMsgAttribute() & 0xE00) >> 10);
        // 是否分包[13] 0010,0000,0000,0000(0x2000)
        msgHeader.setHasSubPackage(((msgHeader.getHeaderMsgAttribute() & 0x2000) >> 13) == 1);
        // 终端手机号(终端唯一标识)
        msgHeader.setHeadMsgTerminalId(BCD.bcdToStr(ArrayUtil.sub(bs, 4, 10)));
        // 消息流水号
        msgHeader.setHeaderMsgSerialNumber(BitOperatorUtils.byteToInteger(ArrayUtil.sub(bs, 10, 12)));
        int bodyIndex = 12;
        if (msgHeader.isHasSubPackage()) {
            // 消息包总数
            msgHeader.setHeaderMsgTotal(BitOperatorUtils.byteToInteger(ArrayUtil.sub(bs, 12, 14)));
            // 消息包序号
            msgHeader.setHeaderMsgSequence(BitOperatorUtils.byteToInteger(ArrayUtil.sub(bs, 14, 16)));
            bodyIndex = 16;
        }
        packageData.setMsgHeader(msgHeader);
        if (msgHeader.getMsgLength() > 0) {
            // 包体
            packageData.setMsgBody(ArrayUtil.sub(bs, bodyIndex, bs.length - 1));
        }
        // 校验码
        packageData.setCheckByte(ArrayUtil.sub(bs, bs.length - 1, bs.length));

        int checkSum = bs[bs.length - 1];
        int calcCheckSum = BitOperatorUtils.getCheckSumJt808(bs, 0, bs.length - 1);

        packageData.setCheckSum(checkSum);
        packageData.setFullMsgBytes(ArrayUtil.addAll(new byte[] {0x7e}, bs, new byte[] {0x7e}));

        if (checkSum != calcCheckSum) {
            log.warn("校验码不一致,命令ID:{},数据包中校验码:{},计算的校验码为:{}", Integer.toHexString(packageData.getMainSign()), checkSum,
                calcCheckSum);
            packageData.setValid(false);
        }
        return packageData;
    }
}