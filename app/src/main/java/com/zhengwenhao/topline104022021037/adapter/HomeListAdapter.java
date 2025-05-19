package com.zhengwenhao.topline104022021037.adapter;

// ✅ 必要导入
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.activity.NewsDetailActivity;
import com.zhengwenhao.topline104022021037.bean.NewsBean;

import java.util.List;

public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsBean> newsList;  // 新闻数据集合
    private static final int TYPE_ONE = 1; // 一张图样式
    private static final int TYPE_TWO = 2; // 三张图样式
    private Context context;

    public HomeListAdapter(Context context) {
        this.context = context;
    }

    // 设置数据并刷新
    public void setData(List<NewsBean> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TWO) {
            // 三图样式布局
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.home_item_two, viewGroup, false);
            return new TypeTwoViewHolder(view);
        } else {
            // 一图样式布局
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.home_item_one, viewGroup, false);
            return new TypeOneViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        if (newsList == null) return;
        final NewsBean bean = newsList.get(i);

        if (holder instanceof TypeOneViewHolder) {
            // 绑定单图样式内容
            ((TypeOneViewHolder) holder).tv_name.setText(bean.getNewsName());
            ((TypeOneViewHolder) holder).tv_news_type_name.setText(bean.getNewsTypeName());

            Glide.with(context)
                    .load(bean.getImg1())
                    .error(R.mipmap.ic_launcher)
                    .into(((TypeOneViewHolder) holder).iv_img);

        } else if (holder instanceof TypeTwoViewHolder) {
            // 绑定三图样式内容
            ((TypeTwoViewHolder) holder).tv_name.setText(bean.getNewsName());
            ((TypeTwoViewHolder) holder).tv_news_type_name.setText(bean.getNewsTypeName());

            Glide.with(context)
                    .load(bean.getImg1())
                    .error(R.mipmap.ic_launcher)
                    .into(((TypeTwoViewHolder) holder).iv_img1);

            Glide.with(context)
                    .load(bean.getImg2())
                    .error(R.mipmap.ic_launcher)
                    .into(((TypeTwoViewHolder) holder).iv_img2);

            Glide.with(context)
                    .load(bean.getImg3())
                    .error(R.mipmap.ic_launcher)
                    .into(((TypeTwoViewHolder) holder).iv_img3);
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("newsBean", bean);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        // 根据类型返回不同布局
        if (1 == newsList.get(position).getType()) {
            return TYPE_ONE;
        } else if (2 == newsList.get(position).getType()) {
            return TYPE_TWO;
        } else {
            return TYPE_ONE; // 默认
        }
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    // 一图样式的 ViewHolder
    public class TypeOneViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_news_type_name;
        public ImageView iv_img;

        public TypeOneViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_news_type_name = itemView.findViewById(R.id.tv_newsType_name);
            iv_img = itemView.findViewById(R.id.iv_img);
        }
    }

    // 三图样式的 ViewHolder
    public class TypeTwoViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_news_type_name;
        public ImageView iv_img1, iv_img2, iv_img3;

        public TypeTwoViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_news_type_name = itemView.findViewById(R.id.tv_newsType_name);
            iv_img1 = itemView.findViewById(R.id.iv_img1);
            iv_img2 = itemView.findViewById(R.id.iv_img2);
            iv_img3 = itemView.findViewById(R.id.iv_img3);
        }
    }
}
