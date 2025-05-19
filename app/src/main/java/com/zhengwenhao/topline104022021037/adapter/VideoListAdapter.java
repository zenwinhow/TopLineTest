package com.zhengwenhao.topline104022021037.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.VideoBean;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VideoBean> videoList;
    private Context context;

    public VideoListAdapter(Context context) {
        this.context = context;
    }

    // 设置数据源
    public void setData(List<VideoBean> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.video_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        final VideoBean bean = videoList.get(i);

        // 使用 Glide 加载图片
        Glide.with(context)
                .load(bean.getImg()) // bean.getImg() 应返回图片 URL 或资源 ID
                .error(R.mipmap.ic_launcher)
                .into(((ViewHolder) holder).iv_img);

        // 设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击后执行的逻辑，如跳转播放页等
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    // 内部类 ViewHolder：封装 item 的视图引用
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_img;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = (ImageView) itemView.findViewById(R.id.iv_img_round); // 对应布局中的封面图
        }
    }
}
