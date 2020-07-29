package com.ruqi.appserver.ruqi.utils;

import com.aliyuncs.utils.StringUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wwt on 2019/3/12 9:23
 * 944799421@qq.com
 */

public class AESUtils {
    private final static String SHA1_PRNG = "SHA1PRNG";
    private static final int KEY_SIZE = 32;

    private static final String Cipher_Mode = "AES/ECB/PKCS5Padding";

    /**
     * 生成随机数，可以当做动态的密钥 加密和解密的密钥必须一致，不然将不能解密
     */
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1_PRNG);
            byte[] bytes_key = new byte[16];
            localSecureRandom.nextBytes(bytes_key);
            return parseByte2HexStr(bytes_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Aes加密/解密
     *
     * @param content  字符串
     * @param password 密钥
     * @param type     加密：{@link Cipher#ENCRYPT_MODE}，解密：{@link Cipher#DECRYPT_MODE}
     * @return 加密/解密结果字符串
     */
    public static String des(String content, String password, int type) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(password)) {
            return null;
        }
        try {
            SecretKeySpec secretKeySpec;
//            if (Build.VERSION.SDK_INT >= 28) {
//                secretKeySpec = deriveKeyInsecurely(password);
//            } else {
//                secretKeySpec = fixSmallVersion(password);
//            }
            secretKeySpec = fixSmallVersion(password);
            Cipher cipher = Cipher.getInstance(Cipher_Mode);
            cipher.init(type, secretKeySpec);
            if (type == Cipher.ENCRYPT_MODE) {
                byte[] byteContent = content.getBytes();
                String encode = Base64Util.byte2Base64(cipher.doFinal(byteContent));
                return encode.replaceAll("\n", "");
            } else {
                byte[] byteContent = Base64Util.base642Byte(content);
                String string = new String(cipher.doFinal(byteContent));
                return string.replaceAll("\n", "");
            }
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKeySpec fixSmallVersion(String password) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] passwordBytes = password.getBytes();
        return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, AESUtils.KEY_SIZE), "AES");

    }

    private static SecretKeySpec deriveKeyInsecurely(String password) {
        byte[] passwordBytes = password.getBytes();
        return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(passwordBytes, AESUtils.KEY_SIZE), "AES");
    }

    private static String parseByte2HexStr(byte buf[]) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {return null;}
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static final class CryptoProvider extends Provider {
        CryptoProvider() {
            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}
