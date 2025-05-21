package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 垂直方向的 SeekBar（音量或亮度调节常见）
 */
public class VerticalSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 当组件尺寸变化时触发
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw); // 注意 w 和 h 互换
    }

    // 测量控件尺寸（将宽高互换）
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth()); // 设置为旋转后正确的尺寸
    }

    // 绘制控件（旋转画布）
    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90); // 逆时针旋转90度
        c.translate(-getHeight(), 0); // 将坐标系移到左边
        super.onDraw(c); // 执行默认绘制
    }

    // 设置进度时触发尺寸变化，更新UI
    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    // 响应用户触摸事件，改变进度（根据y轴位置设置）
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // 计算当前位置对应的进度（越靠下值越小）
                setProgress((int) (getMax() - (getMax() * event.getY() / getHeight())));
                break;
            default:
                return super.onTouchEvent(event);
        }

        return true;
    }
}
