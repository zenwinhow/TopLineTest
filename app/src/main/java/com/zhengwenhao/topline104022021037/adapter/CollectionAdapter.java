package com.zhengwenhao.topline104022021037.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.utils.DBUtils;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
import com.zhengwenhao.topline104022021037.view.SlidingButtonView;

import java.util.ArrayList;
import java.util.List;

// 收藏新闻列表的Adapter，支持侧滑删除
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {

    private Context mContext;
    private IonSlidingViewClickListener mDeleteBtnClickListener;
    private List<NewsBean> newsList = new ArrayList<>();
    private SlidingButtonView mMenu = null;

    public CollectionAdapter(Context context) {
        mContext = context;
        mDeleteBtnClickListener = (IonSlidingViewClickListener) context;
    }

    // 设置数据并刷新
    public void setData(List<NewsBean> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        NewsBean bean = newsList.get(position);
        holder.tv_name.setText(bean.getNewsName());
        holder.tv_newsTypeName.setText(bean.getNewsTypeName());

        // 加载图片
        Glide.with(mContext)
                .load(bean.getImg1())
                .error(R.mipmap.ic_launcher)
                .into(holder.iv_img);

        // 设置内容布局宽度为屏幕宽度
        holder.layout_content.getLayoutParams().width =
                UtilsHelper.getScreenWidth(mContext);

        // item内容点击事件
        holder.layout_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断当前有无菜单打开
                if (menuIsOpen()) {
                    closeMenu(); // 关闭
                } else {
                    int n = holder.getLayoutPosition();
                    mDeleteBtnClickListener.onItemClick(v, n);
                }
            }
        });

        // 删除按钮点击事件
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();
                mDeleteBtnClickListener.onDeleteBtnClick(v, n);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.collection_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    // ViewHolder实现
    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_img;
        public TextView btn_Delete, tv_name, tv_newsTypeName;
        public ViewGroup layout_content;

        public MyViewHolder(View itemView) {
            super(itemView);
            btn_Delete = (TextView) itemView.findViewById(R.id.tv_delete);
            layout_content = (ViewGroup) itemView.findViewById(R.id.layout_content);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_newsTypeName = (TextView) itemView.findViewById(R.id.tv_newsType_name);

            ((SlidingButtonView) itemView).setSlidingButtonListener(CollectionAdapter.this);
        }
    }

    // 删除数据
    public void removeData(int position, TextView tv_none, String userName) {
        NewsBean bean = newsList.get(position);
        // 删除数据库中的收藏新闻
        DBUtils.getInstance(mContext)
                .delCollectionNewsInfo(bean.getId(), bean.getType(), userName);
        newsList.remove(position);
        notifyItemRemoved(position);
        if (newsList.size() == 0) {
            tv_none.setVisibility(View.VISIBLE);
        }
    }

    /** 删除菜单打开信息接收 **/
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /** 滑动或点击item监听 **/
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /** 关闭菜单 **/
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;
    }

    /** 判断是否有菜单打开 **/
    public Boolean menuIsOpen() {
        return mMenu != null;
    }

    // item点击与删除回调接口
    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnClick(View view, int position);
    }
}
