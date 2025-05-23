package com.zhengwenhao.topline104022021037.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.zhengwenhao.topline104022021037.activity.SettingActivity;

public class UtilsHelper {
    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 从SharedPreference中读取登录状态
     */
    public static boolean readLoginStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        return sp.getBoolean("isLogin", false);
    }

    /**
     * 从SharedPreference中读取登录用户名
     */
    public static String readLoginUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("loginInfo", context.MODE_PRIVATE);
        return sp.getString("loginUserName","");
    }

    /**
     * 清除 SharedPreferences 中的登录状态和登录时的用户名
     * 用于“退出登录”操作
     */
    public static void clearLoginStatus(Context context) {
        // 获取名为 "loginInfo" 的 SharedPreferences 实例，模式为私有
        SharedPreferences sp = context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        // 获取编辑器
        SharedPreferences.Editor editor = sp.edit();
        // 设置登录状态为 false
        editor.putBoolean("isLogin", false);
        // 清空用户名
        editor.putString("loginUserName", "");
        // 提交修改
        editor.commit();
    }

}
