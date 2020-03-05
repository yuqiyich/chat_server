package com.ruqi.appserver.ruqi.utils;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptUtils {
    private static final String KEY_DES_DEFAULT = "ruqi!@#2020";

    private static final byte[] DESIV = new byte[] { 0x12, 0x34, 0x56, 120, (byte) 0x90, (byte) 0xab, (byte) 0xcd, (byte) 0xef };// 向量  
    private static AlgorithmParameterSpec iv = null;// 加密算法的参数接口  
    private static Key key = null;  
  
    private static String charset = "utf-8";  
  
    static {
        try {
            DESKeySpec keySpec = new DESKeySpec(KEY_DES_DEFAULT.getBytes(charset));// 设置密钥参数  
            iv = new IvParameterSpec(DESIV);// 设置向量  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂  
            key = keyFactory.generateSecret(keySpec);// 得到密钥对象  
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
  
    /** 
     * 加密 
     */  
    public static String encode(String data){  
        try {
            Cipher enCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");// 得到加密对象Cipher  
            enCipher.init(Cipher.ENCRYPT_MODE, key, iv);// 设置工作模式为加密模式，给出密钥和向量  
            byte[] pasByte = enCipher.doFinal(data.getBytes("utf-8"));  
            BASE64Encoder base64Encoder = new BASE64Encoder();  
            return base64Encoder.encode(pasByte);  
        } catch(Exception e) {
            System.out.println("--->encode error:" + e.getMessage());
            return "";
        }
    }  
  
    /** 
     * 解密
     */  
    public static String decode(String data) {  
        try {
            Cipher deCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
            deCipher.init(Cipher.DECRYPT_MODE, key, iv);  
            BASE64Decoder base64Decoder = new BASE64Decoder();  
            byte[] pasByte = deCipher.doFinal(base64Decoder.decodeBuffer(data));  
            return new String(pasByte, "UTF-8");  
        } catch(Exception e) {
            System.out.println("--->decode error:" + e.getMessage());
            return "";
        }
    }  
}