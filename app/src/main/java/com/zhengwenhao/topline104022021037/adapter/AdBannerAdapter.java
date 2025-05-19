package com.zhengwenhao.topline104022021037.adapter;

// ✅ 必要导入
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.fragment.AdBannerFragment;
import com.zhengwenhao.topline104022021037.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class AdBannerAdapter extends FragmentStatePagerAdapter implements View.OnTouchListener {
    private android.os.Handler mHandler;  // 用于控制广告轮播
    private List<NewsBean> abl;  // 广告数据列表

    public AdBannerAdapter(FragmentManager fm, android.os.Handler handler) {
        super(fm);
        this.mHandler = handler;
        abl = new ArrayList<NewsBean>();
    }

    /**
     * 设置数据并刷新界面
     */
    public void setData(List<NewsBean> abl) {
        this.abl = abl;
        notifyDataSetChanged();  // 通知 UI 重新加载数据
    }

    @Override
    public Fragment getItem(int index) {
        Bundle args = new Bundle();
        if (abl.size() > 0) {
            // 做取模处理实现“无限轮播”效果
            args.putSerializable("ad", abl.get(index % abl.size()));
        }
        return AdBannerFragment.newInstance(args);  // 创建新的 Fragment
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE; // 无限数量，用于轮播
    }

    /**
     * 返回真实的广告数量
     */
    public int getSize() {
        return abl == null ? 0 : abl.size();
    }

    /**
     * 获取广告名称
     */
    public String getTitle(int index) {
        return abl == null ? null : abl.get(index).getNewsName();
    }

    @Override
    public int getItemPosition(Object object) {
        // 防止刷新时复用旧视图导致崩溃，强制全部重新加载
        return POSITION_NONE;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(HomeFragment.MSG_AD_SLID); // 暂停轮播
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandler.sendEmptyMessageDelayed(HomeFragment.MSG_AD_SLID, 5000); // 5秒后恢复轮播
                break;
        }
        return false;
    }

}
