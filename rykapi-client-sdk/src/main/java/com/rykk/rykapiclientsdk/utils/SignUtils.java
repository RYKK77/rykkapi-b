package com.rykk.rykapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具类
 * @author rykk
 */
public class SignUtils {
    /**
     * 根据参数生成不可逆的签名
     * @param body
     * @param secretKey
     * @return 签名字符串
     */
    public static String genSign(String body, String secretKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "+" + secretKey;
        return md5.digestHex(content);
    }
}
