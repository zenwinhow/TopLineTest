package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.zhengwenhao.topline104022021037.R;

// 侧滑菜单按钮自定义View，实现类似QQ侧滑删除的效果
public class SlidingButtonView extends HorizontalScrollView {

    private TextView mTextView_Delete; // 右侧删除按钮
    private int mScrollWidth; // 可滑动的距离（删除按钮宽度）
    private IonSlidingButtonListener mIonSlidingButtonListener;
    private Boolean isOpen = false; // 当前菜单是否打开
    private Boolean once = false;   // 是否已测量过

    public SlidingButtonView(Context context) {
        this(context, null);
    }

    public SlidingButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOverScrollMode(OVER_SCROLL_NEVER); // 不显示滑动边缘阴影
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!once) {
            // 第一次测量时初始化删除按钮
            mTextView_Delete = (TextView) findViewById(R.id.tv_delete);
            once = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            // 默认不显示菜单，滑动到最左边
            this.scrollTo(0, 0);
            // 获取滑动的最大距离（删除按钮宽度）
            mScrollWidth = mTextView_Delete.getWidth();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 通知外部有滑动（用于关闭其它已打开的菜单）
                if (mIonSlidingButtonListener != null) {
                    mIonSlidingButtonListener.onDownOrMove(this);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 抬起时判断菜单是打开还是关闭
                changeScrollx();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // 滑动时同步删除按钮的偏移，实现联动
        if (mTextView_Delete != null) {
            mTextView_Delete.setTranslationX(l - mScrollWidth);
        }
    }

    /**
     * 根据松手时的滑动距离判断菜单的开/关
     */
    public void changeScrollx() {
        if (getScrollX() >= (mScrollWidth / 2)) {
            // 滑动超过一半，打开菜单
            this.smoothScrollTo(mScrollWidth, 0);
            isOpen = true;
            if (mIonSlidingButtonListener != null) {
                mIonSlidingButtonListener.onMenuIsOpen(this);
            }
        } else {
            // 未超过一半，关闭菜单
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }

    /**
     * 打开菜单
     */
    public void openMenu() {
        if (isOpen) return;
        this.smoothScrollTo(mScrollWidth, 0);
        isOpen = true;
        if (mIonSlidingButtonListener != null) {
            mIonSlidingButtonListener.onMenuIsOpen(this);
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (!isOpen) return;
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }

    // 设置回调监听器
    public void setSlidingButtonListener(IonSlidingButtonListener listener) {
        mIonSlidingButtonListener = listener;
    }

    // 侧滑按钮监听接口
    public interface IonSlidingButtonListener {
        void onMenuIsOpen(View view); // 菜单打开时回调
        void onDownOrMove(SlidingButtonView slidingButtonView); // 按下或移动时回调
    }
}
