package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.utils.AnimHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 头像Behavior
 * 用于在 AppBarLayout 滚动时控制头像的缩放和位置变化
 */
public class AvatarBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    private static final float ANIM_CHANGE_POINT = 0.2f; // 缩放动画变化的支点（比例大于此时开始缩放）

    private Context mContext;
    private int mTotalScrollRange;      // 整个滚动的范围
    private int mAppBarHeight;          // AppBarLayout 高度
    private int mAppBarWidth;           // AppBarLayout 宽度
    private int mOriginalSize;          // 控件原始大小
    private int mFinalSize;             // 控件最终大小
    private float mScaleSize;           // 控件最终缩放的尺寸，计算坐标差用
    private float mOriginalX;           // 原始 x 坐标
    private float mFinalX;              // 最终 x 坐标
    private float mOriginalY;           // 原始 y 坐标
    private float mFinalY;              // 最终 y 坐标
    private int mToolBarHeight;         // ToolBar 高度
    private int mAppBarStartY;          // AppBar 的起始 Y 坐标
    private float mPercent;             // 滚动执行百分比[0~1]

    private DecelerateInterpolator mMoveYInterpolator; // Y 轴移动插值器（减速）
    private AccelerateInterpolator mMoveXInterpolator; // X 轴移动插值器（加速）

    // 最终变换的头像，用于兼容 Android 5.0+ CollapsingToolbarLayout 收缩遮挡问题
    private CircleImageView mFinalView;
    private int mFinalViewMarginBottom; // 最终头像底部 margin

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mMoveYInterpolator = new DecelerateInterpolator();
        mMoveXInterpolator = new AccelerateInterpolator();

        // 从布局中读取自定义属性值
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.AvatarImageBehavior);
            mFinalSize = (int) a.getDimension(R.styleable.AvatarImageBehavior_finalSize, 0);
            mFinalX = a.getDimension(R.styleable.AvatarImageBehavior_finalX, 0);
            mToolBarHeight = (int) a.getDimension(R.styleable.AvatarImageBehavior_toolBarHeight, 0);
            a.recycle();
        }
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        // 依赖 AppBarLayout 的滚动事件
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            _initVariables(child, dependency);

            // 计算滚动百分比
            mPercent = (mAppBarStartY - dependency.getY()) * 1.0f / mTotalScrollRange;
            float percentY = mMoveYInterpolator.getInterpolation(mPercent); // Y 方向位移插值

            // 设置 Y 坐标位置
            AnimHelper.setViewY(child, mOriginalY, mFinalY - mScaleSize, percentY);

            if (mPercent > ANIM_CHANGE_POINT) {
                // 缩放和 X 位移
                float scalePercent = (mPercent - ANIM_CHANGE_POINT) / (1 - ANIM_CHANGE_POINT);
                float percentX = mMoveXInterpolator.getInterpolation(scalePercent);
                AnimHelper.scaleView(child, mOriginalSize, mFinalSize, scalePercent);
                AnimHelper.setViewX(child, mOriginalX, mFinalX - mScaleSize, percentX);
            } else {
                // 无缩放状态
                AnimHelper.scaleView(child, mOriginalSize, mFinalSize, 0);
                AnimHelper.setViewX(child, mOriginalX, mFinalX - mScaleSize, 0);
            }

            // 显示或隐藏最终头像（滚动到顶部时显示）
            if (mFinalView != null) {
                if (percentY == 1.0f) {
                    mFinalView.setVisibility(View.VISIBLE);
                } else {
                    mFinalView.setVisibility(View.GONE);
                }
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                dependency instanceof CollapsingToolbarLayout) {
            // Android 5.0+ 兼容处理：AppBar 收缩状态会遮挡头像

            if (mFinalView == null && mFinalSize != 0 && mFinalX != 0 && mFinalViewMarginBottom != 0) {
                mFinalView = new CircleImageView(mContext);
                mFinalView.setVisibility(View.GONE);
                ((CollapsingToolbarLayout) dependency).addView(mFinalView);

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mFinalView.getLayoutParams();
                params.width = mFinalSize;
                params.height = mFinalSize;
                params.gravity = Gravity.BOTTOM;
                params.leftMargin = (int) mFinalX;
                params.bottomMargin = mFinalViewMarginBottom;

                mFinalView.setLayoutParams(params);
            }

            if (mFinalView != null) {
                mFinalView.setImageDrawable(child.getDrawable());
                mFinalView.setBorderColor(child.getBorderColor());

                // 按缩放比例调整边框宽度
                int borderWidth = (int) ((mFinalSize * 1.0f / mOriginalSize) * child.getBorderWidth());
                mFinalView.setBorderWidth(borderWidth);
            }
        }

        return true;
    }

    /**
     * 初始化变量（只初始化一次，避免重复计算）
     */
    private void _initVariables(CircleImageView child, View dependency) {
        if (mAppBarHeight == 0) {
            mAppBarHeight = dependency.getHeight();
            mAppBarStartY = (int) dependency.getY();
        }

        if (mTotalScrollRange == 0) {
            mTotalScrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        }

        if (mOriginalSize == 0) {
            mOriginalSize = child.getWidth();
        }

        if (mFinalSize == 0) {
            mFinalSize = mContext.getResources().getDimensionPixelSize(R.dimen.avatar_final_size);
        }

        if (mAppBarWidth == 0) {
            mAppBarWidth = dependency.getWidth();
        }

        if (mOriginalX == 0) {
            mOriginalX = child.getX();
        }

        if (mFinalX == 0) {
            mFinalX = mContext.getResources().getDimensionPixelSize(R.dimen.avatar_final_x);
        }

        if (mOriginalY == 0) {
            mOriginalY = child.getY();
        }

        if (mFinalY == 0) {
            if (mToolBarHeight == 0) {
                mToolBarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);
            }
            mFinalY = (mToolBarHeight - mFinalSize) / 2 + mAppBarStartY;
        }

        if (mScaleSize == 0) {
            mScaleSize = (mOriginalSize - mFinalSize) * 1.0f / 2;
        }

        if (mFinalViewMarginBottom == 0) {
            mFinalViewMarginBottom = (mToolBarHeight - mFinalSize) / 2;
        }
    }
}
