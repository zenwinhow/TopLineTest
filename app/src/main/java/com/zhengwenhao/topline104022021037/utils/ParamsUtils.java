package com.zhengwenhao.topline104022021037.utils;

import android.content.Context;

/**
 * 参数工具类
 */
public class ParamsUtils {

    // 无效值常量
    public final static int INVALID = -1;

    /**
     * 将字符串转换为整数，如果转换失败则返回 INVALID
     */
    public static int getInt(String str) {
        int num = INVALID;
        try {
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // 转换失败，返回 INVALID
        }
        return num;
    }

    /**
     * 把时间（秒）转换成 hh:mm:ss 格式的字符串
     */
    public static String millisecondsToStr(int seconds) {
        seconds = seconds / 1000; // 转换为秒
        String result = "";
        int hour = 0, min = 0, second = 0;

        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;

        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }

        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }

        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }

        return result;
    }

    /**
     * 将 dp 转换为 px 值
     */
    public static int dpToPx(Context context, int height) {
        float density = context.getResources().getDisplayMetrics().density;
        height = (int) (height * density + 0.5f); // 四舍五入
        return height;
    }
}
