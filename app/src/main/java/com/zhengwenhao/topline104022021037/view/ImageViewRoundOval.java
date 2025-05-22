package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 实现圆形、圆角、椭圆等自定义图片 View
 */
public class ImageViewRoundOval extends AppCompatImageView {

    private Paint mPaint;                    // 画笔
    private int mWidth;                      // 圆形宽度
    private int mRadius;                     // 圆半径
    private RectF mRect;                     // 圆角矩形范围
    private int mRoundRadius;                // 圆角大小
    private BitmapShader mBitmapShader;      // 图形着色器
    private Matrix mMatrix;                  // 变换矩阵
    private int mType;                       // 当前类型（圆、圆角矩形、椭圆）

    public static final int TYPE_CIRCLE = 0;           // 圆形
    public static final int TYPE_ROUND = 1;            // 圆角矩形
    public static final int TYPE_OVAL = 2;             // 椭圆形
    public static final int DEFAULT_ROUND_RADIUS = 10; // 默认圆角半径

    public ImageViewRoundOval(Context context) {
        this(context, null);
    }

    public ImageViewRoundOval(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewRoundOval(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 抗锯齿
        mMatrix = new Matrix();
        mRoundRadius = DEFAULT_ROUND_RADIUS;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 如果是绘制圆形，强制设置宽高一致
        if (mType == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) return;

        setBitmapShader();

        if (mType == TYPE_CIRCLE) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        } else if (mType == TYPE_ROUND) {
            mPaint.setColor(Color.RED); // 可选：绘制颜色
            canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint);
        } else if (mType == TYPE_OVAL) {
            canvas.drawOval(mRect, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect = new RectF(0, 0, getWidth(), getHeight());
    }

    /**
     * 设置 BitmapShader
     */
    private void setBitmapShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        Bitmap bitmap = drawableToBitmap(drawable);

        // 创建 BitmapShader
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float scale = 1.0f;
        if (mType == TYPE_CIRCLE) {
            // 取宽高中最小值作为直径比例基准
            int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            scale = mWidth * 1.0f / bSize;
        } else if (mType == TYPE_ROUND || mType == TYPE_OVAL) {
            // 使用最大缩放比例以填满 view
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(),
                    getHeight() * 1.0f / bitmap.getHeight());
        }

        mMatrix.setScale(scale, scale);                    // 设置缩放矩阵
        mBitmapShader.setLocalMatrix(mMatrix);             // 应用缩放矩阵
        mPaint.setShader(mBitmapShader);                   // 设置画笔着色器
    }

    /**
     * 将 drawable 转换为 bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public int getType() {
        return mType;
    }

    /**
     * 设置图片类型：圆形、圆角矩形、椭圆形
     */
    public void setType(int mType) {
        if (this.mType != mType) {
            this.mType = mType;
            invalidate(); // 重新绘制
        }
    }
}
