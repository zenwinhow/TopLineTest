package com.zhengwenhao.topline104022021037.utils;

import com.zhengwenhao.topline104022021037.R;

/**
 * BuilderManager 用于管理 BoomMenuButton 中每个按钮的图标和文本资源
 */
public class BuilderManager {

    // 图标资源数组（共 18 个，用于 BoomMenuButton 按钮图标）
    private static int[] imageResources = new int[]{
            R.drawable.bat,
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.butterfly,
            R.drawable.cat,
            R.drawable.deer,
            R.drawable.dolphin,
            R.drawable.eagle,
            R.drawable.horse,
            R.drawable.elephant,
            R.drawable.owl,
            R.drawable.peacock,
            R.drawable.pig,
            R.drawable.rat,
            R.drawable.snake,
            R.drawable.squirrel
    };

    // 文本资源数组（共 9 个，用于 BoomMenuButton 按钮文字）
    private static int[] textResources = new int[]{
            R.string.android,
            R.string.java,
            R.string.python,
            R.string.php,
            R.string.c,
            R.string.ios,
            R.string.fore_end,
            R.string.ui,
            R.string.network
    };

    // 当前图片资源索引
    private static int imageResourceIndex = 0;

    // 当前文本资源索引
    private static int textResourceIndex = 0;

    // 返回下一个图片资源（按顺序循环）
    public static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    // 返回下一个文字资源（按顺序循环）
    public static int getTextResource() {
        if (textResourceIndex >= textResources.length) textResourceIndex = 0;
        return textResources[textResourceIndex++];
    }

    // 单例模式实现 BuilderManager 的唯一实例
    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    // 私有构造函数，禁止外部直接实例化
    private BuilderManager() {
    }
}
