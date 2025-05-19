package com.zhengwenhao.topline104022021037.adapter; // 包名已修改为你的要求

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.PythonBean;

import java.util.List;

// 自定义 RecyclerView 适配器，用于展示 PythonBean 列表
public class PythonListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PythonBean> pbl; // 数据源：PythonBean 列表

    // 设置数据并刷新列表
    public void setData(List<PythonBean> pbl) {
        this.pbl = pbl;
        notifyDataSetChanged(); // 通知适配器数据更新
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 加载布局：python_list_item.xml
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.python_list_item, viewGroup, false);
        // 创建并返回 ViewHolder 实例
        PythonViewHolder viewHolder = new PythonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        // 获取对应位置的数据对象
        final PythonBean bean = pbl.get(i);
        // 设置地址文本
        ((PythonViewHolder) holder).tv_address.setText(bean.getAddress());
        // 设置内容文本
        ((PythonViewHolder) holder).tv_content.setText(bean.getContent());
    }

    @Override
    public int getItemCount() {
        // 如果数据源为空返回 0，否则返回数据个数
        return pbl == null ? 0 : pbl.size();
    }

    // 自定义 ViewHolder，用于缓存 item 中的控件
    public class PythonViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_address, tv_content; // 地址与内容的文本控件

        public PythonViewHolder(View itemView) {
            super(itemView);
            // 绑定控件
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
