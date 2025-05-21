package com.zhengwenhao.topline104022021037.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * 视频详情页的 ViewPager 适配器
 */
public class VideoDetailPagerAdapter extends PagerAdapter {

    private List<View> mViewList;       // 页面视图列表
    private List<String> mTitleList;    // 页面标题列表

    public VideoDetailPagerAdapter(List<View> mViewList, List<String> mTitleList) {
        this.mViewList = mViewList;
        this.mTitleList = mTitleList;
    }

    // 返回页卡数量
    @Override
    public int getCount() {
        return mViewList.size();
    }

    // 判断是否由对象生成界面（官方推荐写法）
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    // 实例化页卡
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    // 删除页卡
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }

    // 获取页卡标题
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
