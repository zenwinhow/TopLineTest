package com.zhengwenhao.topline104022021037.adapter;

// 导入所需的类和包
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

// 泛型类 WrapAdapter，用于给 RecyclerView.Adapter 添加头部视图支持
public class WrapAdapter<T extends RecyclerView.Adapter> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final T mRealAdapter; // 原始适配器
    private boolean isStaggeredGrid; // 是否为瀑布流布局
    private static final int BASE_HEADER_VIEW_TYPE = -1 << 10; // 头部视图的起始类型值（确保不与原始适配器冲突）
    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>(); // 存放所有头部视图的信息

    // 内部类，用于描述一个头部视图
    public class FixedViewInfo {
        public View view;     // 视图本体
        public int viewType;  // 对应的 viewType
    }

    // 构造方法，接收真实的适配器
    public WrapAdapter(T adapter) {
        super();
        mRealAdapter = adapter;
    }

    // 获取真实适配器
    public T getWrappedAdapter() {
        return mRealAdapter;
    }

    // 添加头部视图的方法
    public void addHeaderView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to add must not be null!"); // 为空则抛异常
        }
        final FixedViewInfo info = new FixedViewInfo(); // 创建新头部对象
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + mHeaderViewInfos.size(); // 动态生成 viewType
        mHeaderViewInfos.add(info); // 加入头部集合
        notifyDataSetChanged(); // 通知数据更新
    }

    // 在 GridLayoutManager 或瀑布流中调整头部视图的跨列
    public void adjustSpanSize(RecyclerView recycler) {
        if (recycler.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    boolean isHeaderOrFooter = isHeaderPosition(position); // 判断是否头部
                    return isHeaderOrFooter ? layoutManager.getSpanCount() : 1; // 是头部则占满一行
                }
            });
        }

        // 如果是瀑布流布局，设置标志
        if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            this.isStaggeredGrid = true;
        }
    }

    // 判断一个 viewType 是否属于头部
    private boolean isHeader(int viewType) {
        return viewType >= BASE_HEADER_VIEW_TYPE
                && viewType < (BASE_HEADER_VIEW_TYPE + mHeaderViewInfos.size());
    }

    // 判断当前位置是否是头部
    private boolean isHeaderPosition(int position) {
        return position < mHeaderViewInfos.size();
    }

    // 创建 ViewHolder，根据位置判断是否创建头部的 ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (isHeader(viewType)) {
            int whichHeader = Math.abs(viewType - BASE_HEADER_VIEW_TYPE); // 计算是第几个头部
            View headerView = mHeaderViewInfos.get(whichHeader).view; // 获取视图
            return createHeaderFooterViewHolder(headerView); // 创建包裹的 ViewHolder
        } else {
            return mRealAdapter.onCreateViewHolder(viewGroup, viewType); // 委托真实适配器处理
        }
    }

    // 创建头部或尾部的 ViewHolder（全屏）
    private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
        if (isStaggeredGrid) {
            // 设置瀑布流的 full span 属性
            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                    StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            view.setLayoutParams(params);
        }
        // 返回匿名 ViewHolder
        return new RecyclerView.ViewHolder(view) {};
    }

    // 绑定 ViewHolder 的方法
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < mHeaderViewInfos.size()) {
            // 头部视图不需要绑定数据
        } else if (position < mHeaderViewInfos.size() + mRealAdapter.getItemCount()) {
            // 将位置偏移后委托给真实的 Adapter
            mRealAdapter.onBindViewHolder(viewHolder, position - mHeaderViewInfos.size());
        }
    }

    // 返回总的 item 数（头部 + 原始项）
    @Override
    public int getItemCount() {
        return mHeaderViewInfos.size() + mRealAdapter.getItemCount();
    }

    // 返回对应位置的 viewType
    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return mHeaderViewInfos.get(position).viewType; // 返回头部 viewType
        } else {
            return mRealAdapter.getItemViewType(position - mHeaderViewInfos.size()); // 委托原始适配器
        }
    }
}
