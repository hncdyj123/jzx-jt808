package com.jzx.jt808.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 类描述:消息相关处理工具类
 *
 * @author yangjie
 * @date 2023-12-22 15:11
 **/
@Slf4j
public class Jt808MsgUtils {
    public final static int MSG_MAX_LENGTH = 1024;

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
            log.error("消息超过最大长度", msgLen);
            return -1;
        }
        int subPkg = isSubPackage ? 1 : 0;
        int ret = (msgLen & 0x3FF) | ((encryptionType << 10) & 0x1C00) | ((subPkg << 13) & 0x2000)
            | ((reversed1415 << 14) & 0xC000);
        return ret & 0xffff;
    }
}
