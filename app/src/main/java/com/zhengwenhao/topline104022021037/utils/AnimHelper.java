package com.zhengwenhao.topline104022021037.utils;

import android.view.View;

/**
 * 动画帮助类
 * 用于设置视图的平移 (X/Y) 与缩放动画
 */
public class AnimHelper {

    // 私有构造方法，禁止实例化该工具类
    private AnimHelper() {
        throw new RuntimeException("AnimHelper cannot be initialized!");
    }

    /**
     * 设置视图 X 坐标
     * @param view      目标视图
     * @param originalX 原始 X 坐标
     * @param finalX    最终 X 坐标
     * @param percent   百分比（0~1）
     *
     * 说明：此方法将根据滚动进度计算出当前 X 坐标的位置，实现平滑过渡
     */
    public static void setViewX(View view, float originalX, float finalX, float percent) {
        float calcX = (finalX - originalX) * percent + originalX;
        view.setX(calcX);  // 应用计算后的 X 坐标
    }

    /**
     * 设置视图 Y 坐标
     * @param view      目标视图
     * @param originalY 原始 Y 坐标
     * @param finalY    最终 Y 坐标
     * @param percent   百分比（0~1）
     *
     * 说明：此方法将根据滚动进度计算出当前 Y 坐标的位置，实现平滑过渡
     */
    public static void setViewY(View view, float originalY, float finalY, float percent) {
        float calcY = (finalY - originalY) * percent + originalY;
        view.setY(calcY);  // 应用计算后的 Y 坐标
    }

    /**
     * 缩放视图（同时设置 X/Y 缩放比例）
     * @param view         目标视图
     * @param originalSize 原始尺寸
     * @param finalSize    最终尺寸
     * @param percent      百分比（0~1）
     *
     * 说明：此方法将计算视图的缩放比，并应用到 X/Y 缩放轴上，实现缩放动画
     */
    public static void scaleView(View view, float originalSize, float finalSize, float percent) {
        float calcSize = (finalSize - originalSize) * percent + originalSize;
        float calcScale = calcSize / originalSize;
        view.setScaleX(calcScale); // 设置缩放比例 X
        view.setScaleY(calcScale); // 设置缩放比例 Y
    }
}
