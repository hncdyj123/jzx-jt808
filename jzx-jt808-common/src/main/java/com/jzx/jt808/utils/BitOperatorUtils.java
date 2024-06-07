package com.jzx.jt808.utils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 位运算帮助类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
public class BitOperatorUtils {

    /**
     * int转byte
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte integerTo1Byte(int value) {
        return (byte)(value & 0xFF);
    }

    /**
     * int转byte[1]数组
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] integerTo1Bytes(int value) {
        byte[] result = new byte[1];
        result[0] = (byte)(value & 0xFF);
        return result;
    }

    /**
     * int转byte[2]数组
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] integerTo2Bytes(int value) {
        byte[] result = new byte[2];
        result[0] = (byte)((value >>> 8) & 0xFF);
        result[1] = (byte)(value & 0xFF);
        return result;
    }

    /**
     * int转byte[3]数组
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] integerTo3Bytes(int value) {
        byte[] result = new byte[3];
        result[0] = (byte)((value >>> 16) & 0xFF);
        result[1] = (byte)((value >>> 8) & 0xFF);
        result[2] = (byte)(value & 0xFF);
        return result;
    }

    /**
     * in转byte[4]数组
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] integerTo4Bytes(int value) {
        byte[] result = new byte[4];
        result[0] = (byte)((value >>> 24) & 0xFF);
        result[1] = (byte)((value >>> 16) & 0xFF);
        result[2] = (byte)((value >>> 8) & 0xFF);
        result[3] = (byte)(value & 0xFF);
        return result;
    }

    private static int length_2 = 2;
    private static int length_3 = 3;
    private static int length_4 = 3;

    /**
     * 把byte[]转化位整形,通常为指令用
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static int byteToInteger(byte[] value) {
        int result;
        if (value.length == 1) {
            result = oneByteToInteger(value[0]);
        } else if (value.length == length_2) {
            result = twoBytesToInteger(value);
        } else if (value.length == length_3) {
            result = threeBytesToInteger(value);
        } else if (value.length == length_4) {
            result = fourBytesToInteger(value);
        } else {
            result = fourBytesToInteger(value);
        }
        return result;
    }

    /**
     * byte转int
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static int oneByteToInteger(byte value) {
        return (int)value & 0xFF;
    }

    /**
     * byte[2]转int
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static int twoBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        return ((temp0 << 8) + temp1);
    }

    /**
     * byte[3]转int
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static int threeBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        return ((temp0 << 16) + (temp1 << 8) + temp2);
    }

    /**
     * byte[4]转int
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static int fourBytesToInteger(byte[] value) {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
    }

    /**
     * byte[4]转long
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static long fourBytesToLong(byte[] value) throws Exception {
        int temp0 = value[0] & 0xFF;
        int temp1 = value[1] & 0xFF;
        int temp2 = value[2] & 0xFF;
        int temp3 = value[3] & 0xFF;
        return (((long)temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
    }

    /**
     * byte[n]转long
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static long bytesToLong(byte[] value) {
        long result = 0;
        int len = value.length;
        int temp;
        for (int i = 0; i < len; i++) {
            temp = (len - 1 - i) * 8;
            if (temp == 0) {
                result += (value[i] & 0x0ff);
            } else {
                result += (value[i] & 0x0ff) << temp;
            }
        }
        return result;
    }

    /**
     * long转byte[]
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] longToBytes(long value) {
        return longToBytes(value, 8);
    }

    /**
     * long转byte[]
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] longToBytes(long value, int len) {
        byte[] result = new byte[len];
        int temp;
        for (int i = 0; i < len; i++) {
            temp = (len - 1 - i) * 8;
            if (temp == 0) {
                result[i] += (value & 0x0ff);
            } else {
                result[i] += (value >>> temp) & 0x0ff;
            }
        }
        return result;
    }

    /**
     * 合并数组
     * 
     * @param first
     * @param rest
     * @return
     * @see
     */
    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            if (array != null) {
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
        }
        return result;
    }

    /**
     * 将List<byte[]>转换成byte[]
     * 
     * @param rest
     * @return
     */
    public static byte[] concatAll(List<byte[]> rest) {
        int totalLength = 0;
        for (byte[] array : rest) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] array : rest) {
            if (array != null) {
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
        }
        return result;
    }

    /**
     * jt808校验
     * 
     * @param bs 校验数组
     * @param start 开始位置
     * @param end 结束位置
     * @return
     * @see
     */
    public static int getCheckSumJt808(byte[] bs, int start, int end) {
        if (start < 0 || end > bs.length) {
            throw new ArrayIndexOutOfBoundsException("getCheckSum4JT808 error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.length + ")");
        }
        int cs = 0;
        for (int i = start; i < end; i++) {
            cs ^= bs[i];
        }
        return cs;
    }

    /**
     * 数组截取
     * 
     * @param bytes 原来素组
     * @param start 开始位置
     * @param end 结束位置
     * @param count 新数据大小
     * @return
     * @see
     */
    public static byte[] subByte(byte[] bytes, int start, int end, int count) {
        byte[] rBytes = new byte[count];
        int index = 0;
        for (int i = start; i < end; i++) {
            rBytes[index] = bytes[i];
            index++;
        }
        return rBytes;
    }

    /**
     * 服务端发送转义
     *
     * @author yangjie
     * @date 2023/7/11
     * @param bs
     * @param start
     * @param end
     * @return {@link byte[]}
     */
    public static byte[] serverSendEscape(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
                + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
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
            for (int i = end; i < bs.length; i++) {
                baos.write(bs[i]);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
            if (baos != null) {
                baos.close();
                baos = null;
            }
        }
    }
}
