package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.zhengwenhao.topline104022021037.adapter.WrapAdapter;

import java.util.ArrayList;

// 自定义 RecyclerView，用于支持添加头部功能
public class WrapRecyclerView extends RecyclerView {

    private WrapAdapter mWrapAdapter;  // 自定义封装的 Adapter
    private boolean shouldAdjustSpanSize;  // 是否需要调整 Grid 或瀑布流的 span size
    private ArrayList<View> mTmpHeaderView = new ArrayList<>();  // 临时缓存头部 View（还没设置 adapter）

    // 构造函数 1：只传 context
    public WrapRecyclerView(Context context) {
        super(context);
    }

    // 构造函数 2：传 context + AttributeSet
    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 构造函数 3：传 context + AttributeSet + defStyle
    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 设置 Adapter，内部封装逻辑
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof WrapAdapter) {
            // 如果本来就是 WrapAdapter，直接赋值
            mWrapAdapter = (WrapAdapter) adapter;
            super.setAdapter(adapter);
        } else {
            // 否则使用我们封装过的 Adapter
            mWrapAdapter = new WrapAdapter(adapter);

            // 把临时添加的头部 View 全部加入封装的 Adapter
            for (View view : mTmpHeaderView) {
                mWrapAdapter.addHeaderView(view);
            }

            // 清空临时头部缓存
            if (mTmpHeaderView.size() > 0) {
                mTmpHeaderView.clear();
            }

            // 设置封装后的 Adapter
            super.setAdapter(mWrapAdapter);
        }

        // 如果需要自动调整 span（网格或瀑布流）
        if (shouldAdjustSpanSize) {
            mWrapAdapter.adjustSpanSize(this);
        }

        // 监听数据变化，转发给 WrapAdapter
        getWrappedAdapter().registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged(); // 手动触发一次刷新
    }

    // 获取封装后的 Adapter
    @Override
    public WrapAdapter getAdapter() {
        return mWrapAdapter;
    }

    // 获取原始的 Adapter（未封装的）
    public Adapter getWrappedAdapter() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getWrappedAdapter();
    }

    // 添加头部 View
    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!");
        } else if (mWrapAdapter == null) {
            // 还没设置 adapter，就先加入临时缓存
            mTmpHeaderView.add(view);
        } else {
            // 已设置 adapter，直接加到 wrapAdapter 里
            mWrapAdapter.addHeaderView(view);
        }
    }

    // 设置 LayoutManager，并判断是否需要调整 span（针对 Grid 和 瀑布流）
    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager || layout instanceof StaggeredGridLayoutManager) {
            this.shouldAdjustSpanSize = true;
        }
    }

    // 数据变化观察者，用于代理转发通知给 WrapAdapter
    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };
}
