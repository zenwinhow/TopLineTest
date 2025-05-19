package com.zhengwenhao.topline104022021037.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.zhengwenhao.topline104022021037.activity.NewsDetailActivity;
import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.R;

public class AdBannerFragment extends Fragment {

    private NewsBean nb;     // 广告对象
    private ImageView iv;    // 图片视图控件

    // 创建 fragment 实例并传入参数
    public static AdBannerFragment newInstance(Bundle args) {
        AdBannerFragment af = new AdBannerFragment();
        af.setArguments(args);
        return af;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 从 arguments 中获取广告对象
        Bundle arg = getArguments();
        nb = (NewsBean) arg.getSerializable("ad");  // 取出序列化的广告数据
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 可用于额外初始化操作
    }

    @Override
    public void onResume() {
        super.onResume();

        if (nb != null) {
            // 使用 Glide 加载图片
            Glide.with(getActivity())
                    .load(nb.getImg1())                         // 图片地址
                    .error(R.mipmap.ic_launcher)               // 加载失败的默认图
                    .into(iv);                                 // 设置到 iv 上
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 创建一个 ImageView 作为 Fragment 的根视图
        iv = new ImageView(getActivity());

        // 设置布局参数：宽高填满父控件
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
        );
        iv.setLayoutParams(lp);  // 应用布局参数
        iv.setScaleType(ImageView.ScaleType.FIT_XY);  // 让图片填满整个控件

        // 设置点击事件（目前为空实现）
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nb == null) return;
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("newsBean", nb);
                getActivity().startActivity(intent);
            }
        });

        return iv;  // 返回该视图作为 fragment 的视图层
    }
}
