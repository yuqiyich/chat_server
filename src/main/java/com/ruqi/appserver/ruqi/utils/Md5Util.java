package com.ruqi.appserver.ruqi.utils;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *    
 *  * @Description:  [md5 加密的工具类]   
 *  * @Author:    [yuqi]  
 *  * @CreateDate:   [2019/9/9 16:33]   
 *  * @email:   [yichitgo@gmail.com]   
 *  
 */
public class Md5Util {
    private static final String TAG = "Md5Util";

    /**
     * MD5加密字符串（32位大写）
     *
     * @param string 需要进行MD5加密的字符串
     * @return 加密后的字符串（大写）
     */
    public static String md5Encrypt32Upper(String string) {
        byte[] hash = null;
        try {
            //创建一个MD5算法对象，并获得MD5字节数组,16*8=128位
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (hash == null) {
            return "";
        }
        //转换为十六进制字符串
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().toUpperCase();
    }

    /**
     * 对字符串加盐后再md5值，然后截取后16位
     *
     * @param string 准备md5的字符串
     * @param slat   加盐值
     * @return
     */
    public static String md5StrBySalt(String string, String slat) {
        return Md5Util.md5Encrypt32Upper(string + slat);
    }

    public static String commonMd5(String str) {
        String slat = "2020*.ruqi#!888";
        return Md5Util.md5Encrypt32Upper(str + slat);
    }

}
