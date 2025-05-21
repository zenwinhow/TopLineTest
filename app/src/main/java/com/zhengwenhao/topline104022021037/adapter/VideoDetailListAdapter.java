package com.zhengwenhao.topline104022021037.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.VideoDetailBean;

import java.util.List;

/**
 * 视频详情列表适配器
 */
public class VideoDetailListAdapter extends BaseAdapter {
    private Context mContext;
    private List<VideoDetailBean> vdbl; // 视频数据列表
    private int selectedPosition = -1; // 当前选中的位置
    private OnSelectListener onSelectListener; // 点击事件监听器

    public VideoDetailListAdapter(Context context, OnSelectListener onSelectListener) {
        this.mContext = context;
        this.onSelectListener = onSelectListener;
    }

    // 设置选中的位置
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    /**
     * 设置数据，并刷新界面
     */
    public void setData(List<VideoDetailBean> vdbl) {
        this.vdbl = vdbl;
        notifyDataSetChanged();
    }

    /**
     * 获取 Item 的总数
     */
    @Override
    public int getCount() {
        return vdbl == null ? 0 : vdbl.size();
    }

    /**
     * 根据 position 得到对应 Item 的对象
     */
    @Override
    public VideoDetailBean getItem(int position) {
        return vdbl == null ? null : vdbl.get(position);
    }

    /**
     * 根据 position 得到对应 Item 的 Id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取对应 position 对应的 Item 视图，convertView 是缓存的视图
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;

        // 复用视图
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_detail_item, null);
            vh.title = (TextView) convertView.findViewById(R.id.tv_video_name);
            vh.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // 获取对应数据项
        final VideoDetailBean bean = getItem(position);
        vh.title.setTextColor(mContext.getResources().getColor(R.color.video_detail_text_color));
        if (bean != null) {
            vh.title.setText(bean.getVideo_name());

            // 设置选中效果
            if (selectedPosition == position) {
                vh.iv_icon.setImageResource(R.drawable.iv_video_selected_icon);
                vh.title.setTextColor(mContext.getResources().getColor(R.color.rdTextColorPress));
            } else {
                vh.iv_icon.setImageResource(R.drawable.iv_video_icon);
                vh.title.setTextColor(mContext.getResources().getColor(R.color.video_detail_text_color));
            }
        }

        // 每个 Item 的点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到习题详情界面（逻辑交由外部处理）
                if (bean == null) {
                    return;
                }

                // 播放视频或其他点击操作
                onSelectListener.onSelect(position, vh.iv_icon);
            }
        });

        return convertView;
    }

    // ViewHolder 结构，减少 findViewById 调用次数
    static class ViewHolder {
        public TextView title;
        public ImageView iv_icon;
    }

    /**
     * 创建 OnSelectListener 接口，将 position 和控件传递到 Activity
     */
    public interface OnSelectListener {
        void onSelect(int position, ImageView iv);
    }
}
