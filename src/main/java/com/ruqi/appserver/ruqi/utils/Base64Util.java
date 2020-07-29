package com.ruqi.appserver.ruqi.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by liangbingkun on 2019/3/12 10:57
 * base64工具类
 */
public class Base64Util {

    /**
     * 字节数组转Base64编码
     *
     * @param bytes
     * @return
     */
    public static String byte2Base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * Base64编码转字节数组
     *
     * @param base64Key
     * @return
     */
    public static byte[] base642Byte(String base64Key) {
        return Base64.decodeBase64(base64Key);
    }

}
