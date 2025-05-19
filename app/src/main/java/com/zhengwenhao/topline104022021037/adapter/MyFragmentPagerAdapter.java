package com.zhengwenhao.topline104022021037.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 首页 ViewPager 的 Fragment 适配器
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> list;  // Fragment 列表，用于填充 ViewPager 页面

    // 构造方法，传入 Fragment 管理器和 Fragment 列表
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    // 获取当前页的 Fragment
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    // 返回总页面数量
    @Override
    public int getCount() {
        return list.size();
    }
}
