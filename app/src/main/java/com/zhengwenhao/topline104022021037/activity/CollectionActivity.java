package com.zhengwenhao.topline104022021037.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.view.View;
import android.content.Intent;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.adapter.CollectionAdapter;
import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.utils.DBUtils;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏页面Activity，实现收藏新闻的展示、跳转和删除
 */
public class CollectionActivity extends AppCompatActivity
        implements CollectionAdapter.IonSlidingViewClickListener {

    // 新闻列表展示控件
    private RecyclerView mRecyclerView;
    // 收藏新闻的Adapter
    private CollectionAdapter mAdapter;
    // 标题栏相关控件
    private TextView tv_main_title, tv_back, tv_none;
    private RelativeLayout rl_title_bar;
    // 数据库操作工具
    private DBUtils db;
    // 收藏新闻数据列表
    private List<NewsBean> newsList;
    // 当前登录用户名
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection); // 设置界面布局

        // 获取数据库工具类实例
        db = DBUtils.getInstance(CollectionActivity.this);
        // 读取当前登录用户
        userName = UtilsHelper.readLoginUserName(CollectionActivity.this);
        // 初始化控件和数据
        initView();
        // 设置适配器与列表
        setAdapter();
    }

    /**
     * 初始化控件和加载初始数据
     */
    private void initView() {
        newsList = new ArrayList<>(); // 初始化数据列表，防止空指针
        newsList = db.getCollectionNewsInfo(userName); // 加载用户的收藏数据

        // 初始化标题栏和返回按钮
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("收藏"); // 设置标题

        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress)); // 设置标题栏颜色

        // 返回按钮
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);

        // 列表控件
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recyclerView);
        // 空数据时提示
        tv_none = (TextView) findViewById(R.id.tv_none);

        // 返回按钮点击事件：关闭本Activity
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CollectionActivity.this.finish();
            }
        });
    }

    /**
     * 配置RecyclerView的Adapter、布局管理器与动画效果
     */
    private void setAdapter() {
        mAdapter = new CollectionAdapter(this); // 传递Activity做回调监听
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 竖直列表
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setData(newsList); // 填充数据
        // 如果没有收藏，显示“暂无收藏信息”
        if (newsList.size() == 0) tv_none.setVisibility(View.VISIBLE);
        // 设置列表动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 列表项点击事件（回调）
     * 跳转到新闻详情页，并传递当前项的数据
     */
    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(CollectionActivity.this, NewsDetailActivity.class);
        // 传递当前点击的新闻对象
        intent.putExtra("newsBean", newsList.get(position));
        // 传递当前新闻在列表中的位置（便于返回后移除）
        intent.putExtra("position", position + "");
        // 跳转并等待结果返回
        startActivityForResult(intent, 1);
    }

    /**
     * 删除按钮点击事件（回调）
     * 从适配器和数据库中移除该收藏项
     */
    @Override
    public void onDeleteBtnClick(View view, int position) {
        mAdapter.removeData(position, tv_none, userName);
    }

    /**
     * 处理从详情页返回的数据
     * 用于移除已经取消收藏的项
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            // 获取需要移除的item在列表中的位置
            String position = data.getStringExtra("position");
            mAdapter.removeData(Integer.parseInt(position), tv_none, userName);
        }
    }
}
