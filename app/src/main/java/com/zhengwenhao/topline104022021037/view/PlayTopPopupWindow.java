package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;

import com.zhengwenhao.topline104022021037.R;
/**
 * 顶部弹出菜单（用于播放设置选择，如清晰度等）
 */
public class PlayTopPopupWindow {

    private PopupWindow popupWindow;
    private RadioGroup rgScreenSize;

    /**
     * 构造方法，创建 PopupWindow 并初始化界面
     * @param context 上下文
     * @param height 高度用于计算弹窗大小
     */
    public PlayTopPopupWindow(Context context, int height) {
        View view = LayoutInflater.from(context).inflate(R.layout.play_top_menu, null);
        rgScreenSize = findById(R.id.rg_screensize, view);

        popupWindow = new PopupWindow(view, height * 2 / 3, height);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(178, 0, 0, 0))); // 半透明背景
    }

    /**
     * 设置 RadioGroup 的选择监听器
     */
    public void setScreenSizeCheckLister(RadioGroup.OnCheckedChangeListener listener) {
        rgScreenSize.setOnCheckedChangeListener(listener);
    }

    /**
     * 显示弹窗
     */
    public void showAsDropDown(View parent) {
        popupWindow.showAtLocation(parent, Gravity.RIGHT, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    /**
     * 隐藏弹窗
     */
    public void dismiss() {
        popupWindow.dismiss();
    }

    /**
     * 泛型工具方法，通过 View 查找 ID 并强转
     */
    @SuppressWarnings("unchecked")
    private <T extends View> T findById(int resId, View view) {
        return (T) view.findViewById(resId);
    }
}
