package cn.onism.chatglm.utils;

import cn.hutool.crypto.digest.MD5;

/**
 * 加密工具类
 */
public class EncryptUtils {
    /**
     * 加密盐
     */
    static final byte[] SLAT = {'o', 'n', 'i', 's', 'm','c','h','a','t','g','l','m'};

    private static final MD5 md5 = new MD5(SLAT);
    private EncryptUtils() {
    }

    /**
     * 加密
     */
    public static String encrypt(String code) {
        return md5.digestHex16(md5.digestHex(code));
    }

    /**
     * 代码配对
     */
    public static boolean match(String code, String encryptedCode) {
        return encryptedCode.equals(encrypt(code));
    }
}
