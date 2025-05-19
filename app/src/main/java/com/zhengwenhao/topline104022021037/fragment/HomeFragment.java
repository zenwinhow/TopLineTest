package com.zhengwenhao.topline104022021037.fragment;

// 导入基础Android类
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

// 导入自定义控件和适配器
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhengwenhao.PullToRefreshView;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.activity.PythonActivity;
import com.zhengwenhao.topline104022021037.adapter.AdBannerAdapter;
import com.zhengwenhao.topline104022021037.adapter.HomeListAdapter;
import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.utils.Constant;
import com.zhengwenhao.topline104022021037.utils.JsonParse;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
import com.zhengwenhao.topline104022021037.view.WrapRecyclerView;
import com.zhengwenhao.topline104022021037.view.ViewPagerIndicator;

// 导入网络请求库

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    private PullToRefreshView mPullToRefreshView;    // 下拉刷新控件
    private WrapRecyclerView recyclerView;           // 支持 Header 的 RecyclerView
    public static final int REFRESH_DELAY = 1000;    // 刷新延迟（毫秒）

    private ViewPager adPager;                       // 广告轮播 ViewPager
    private ViewPagerIndicator vpi;                  // 轮播指示器
    private TextView tvAdName;                       // 广告标题
    private View adBannerLay;                        // 广告容器布局
    private AdBannerAdapter ada;                     // 广告适配器

    public static final int MSG_AD_SLID  = 1;        // 自动轮播消息
    public static final int MSG_AD_OK    = 2;        // 广告数据加载完成
    public static final int MSG_NEWS_OK  = 3;        // 新闻数据加载完成

    private MHandler mHandler;                       // 事件处理 Handler
    private LinearLayout ll_python;                  // “Python 学科”入口
    private OkHttpClient okHttpClient;               // OkHttp 客户端
    private HomeListAdapter adapter;                 // 列表适配器
    private RelativeLayout rl_title_bar;             // 标题栏

    public HomeFragment() {
        // Fragment 必须要有无参构造
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //先initView
        //View view = initView(inflater, container);

        // 初始化网络客户端和 Handler
        okHttpClient = new OkHttpClient();
        mHandler     = new MHandler();

        // 请求广告和新闻数据
        getADData();
        getNewsData();

        //return view;

        // 初始化并返回布局视图
        return initView(inflater, container);
    }

    /**
     * 初始化界面：RecyclerView、Header及各种控件
     */
    private View initView(LayoutInflater inflater, ViewGroup container) {
        // 加载 fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rl_title_bar = view.findViewById(R.id.title_bar);
        rl_title_bar.setVisibility(View.GONE);

        // 配置 RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 加载头部布局 head_view.xml
        View headView = inflater.inflate(R.layout.head_view, container, false);
        recyclerView.addHeaderView(headView);

        // 列表适配器
        adapter = new HomeListAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        // 下拉刷新控件
        mPullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 延迟结束刷新并重新加载
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        getADData();
                        getNewsData();
                    }
                }, REFRESH_DELAY);
            }
        });

        // 头部广告相关控件
        adBannerLay = headView.findViewById(R.id.adbanner_layout);
        adPager     = headView.findViewById(R.id.slidingAdvertBanner);
        vpi         = headView.findViewById(R.id.advert_indicator);
        tvAdName    = headView.findViewById(R.id.tv_advert_title);
        ll_python   = headView.findViewById(R.id.ll_python);

        // 禁止长按弹出菜单
        adPager.setLongClickable(false);

        // 初始化广告适配器
        ada = new AdBannerAdapter(getActivity().getSupportFragmentManager(), mHandler);
        adPager.setAdapter(ada);
        adPager.setOnTouchListener(ada);

        // 监听页面滑动，更新标题和指示器
        adPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                if (ada.getSize() > 0) {
                    String title = ada.getTitle(index % ada.getSize());
                    if (title != null) {
                        tvAdName.setText(title);
                    }
                }
                if (ada.getSize() != 0) {
                    vpi.setCurrentPosition(index % ada.getSize());
                } else {
                    Log.e("Error", "无法获取广告数据");
                }
            }
            @Override public void onPageScrolled(int pos, float offset, int offsetPx) { }
            @Override public void onPageScrollStateChanged(int state) { }
        });

        // 计算广告尺寸，并注册入口点击
        resetSize();
        setListener();

        // 启动自动轮播线程
        new AdAutoSlidThread().start();

        return view;
    }

    /** 注册头部“Python 学科”点击事件 */
    private void setListener() {
        ll_python.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PythonActivity.class);
                startActivity(intent);
            }
        });
    }

    /** 自定义 Handler，用于接收异步消息并更新 UI */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_AD_SLID:
                    // 自动轮播下一页
                    if (ada.getCount() > 0) {
                        adPager.setCurrentItem(adPager.getCurrentItem() + 1);
                    }
                    break;
                case MSG_AD_OK:
                    // 广告数据回调
                    if (msg.obj != null) {
                        String adResult = (String) msg.obj;
                        List<NewsBean> adl = JsonParse.getInstance().getAdList(adResult);
                        if (adl != null && adl.size() > 0) {
                            ada.setData(adl);
                            tvAdName.setText(adl.get(0).getNewsName());
                            vpi.setCount(adl.size());
                            vpi.setCurrentPosition(0);
                        }
                    }
                    break;
                case MSG_NEWS_OK:
                    // 新闻数据回调
                    if (msg.obj != null) {
                        String newsResult = (String) msg.obj;
                        List<NewsBean> nbl = JsonParse.getInstance().getNewsList(newsResult);
                        if (nbl != null && nbl.size() > 0) {
                            adapter.setData(nbl);
                        }
                    }
                    break;
            }
        }
    }

    /** 自动轮播线程，每5秒发送一次滑动消息 */
    class AdAutoSlidThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(MSG_AD_SLID);
                }
            }
        }
    }

    /** 异步请求新闻数据 */
    private void getNewsData() {
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.REQUEST_NEWS_URL)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
                Message msg = Message.obtain();
                msg.what = MSG_NEWS_OK;
                msg.obj  = res;
                mHandler.sendMessage(msg);
            }


        });
    }

    /** 异步请求广告数据 */
    private void getADData() {
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.REQUEST_AD_URL)
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
                Message msg = Message.obtain();
                msg.what = MSG_AD_OK;
                msg.obj  = res;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 计算并重置广告容器的宽高：
     * 宽度 = 屏幕宽度，高度 = 屏幕宽度的一半
     */
    private void resetSize() {
        int sw = UtilsHelper.getScreenWidth(getActivity()); // 屏幕宽
        int adHeight = sw / 2;                             // 广告高

        LayoutParams adlp = adBannerLay.getLayoutParams(); // 当前布局参数
        adlp.width  = sw;        // 设置宽度
        adlp.height = adHeight;  // 设置高度
        adBannerLay.setLayoutParams(adlp);                 // 生效
    }
}
