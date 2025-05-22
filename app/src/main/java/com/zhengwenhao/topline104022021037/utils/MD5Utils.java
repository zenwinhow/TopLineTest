package com.zhengwenhao.topline104022021037.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类
 */
public class MD5Utils {

    /**
     * 进行 MD5 加密
     * @param text 原始字符串
     * @return 加密后的字符串（32位小写十六进制）
     */
    public static String md5(String text) {
        MessageDigest digest = null;
        try {
            // 获取MD5摘要器实例
            digest = MessageDigest.getInstance("md5");

            // 对输入字符串进行加密，结果为字节数组
            byte[] result = digest.digest(text.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() == 1) {
                    sb.append("0").append(hex); // 不足两位补零
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
