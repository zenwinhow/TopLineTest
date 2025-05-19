package com.zhengwenhao.topline104022021037.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
}
