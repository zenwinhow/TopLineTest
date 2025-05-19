package com.zhengwenhao.topline104022021037.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.NewsBean;
// import com.zhengwenhao.topline104022021037.view.SwipeBackLayout; // ← 已注释

public class NewsDetailActivity extends AppCompatActivity {

    private RelativeLayout rl_title_bar;      // 标题栏布局
    private WebView webView;                  // 网页浏览控件
    private TextView tv_main_title, tv_back;  // 标题文本、返回按钮
    private ImageView iv_collection;          // 收藏图标
    // private SwipeBackLayout layout;        // ← 已注释，滑动返回布局
    private String newsUrl;                   // 新闻详情页链接
    private NewsBean bean;                    // 新闻数据实体类
    private String position;                  // 位置标识（如列表位置）
    private LinearLayout ll_loading;          // 加载动画控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null); // ← 注释掉滑动返回初始化
        // layout.attachToActivity(this); // ← 注释掉绑定
        setContentView(R.layout.activity_news_detail); // 正常设置内容视图

        // 获取传入的 NewsBean 对象和位置
        bean = (NewsBean) getIntent().getSerializableExtra("newsBean");
        position = getIntent().getStringExtra("position");

        if (bean == null) return;

        newsUrl = bean.getNewsUrl();
        init();         // 初始化控件
        initWebView();  // 设置 WebView 属性并加载网页
    }

    private void init() {
        // 初始化控件及设置
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("新闻详情");

        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));

        ll_loading = findViewById(R.id.ll_loading);
        iv_collection = findViewById(R.id.iv_collection);
        iv_collection.setVisibility(View.VISIBLE);

        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailActivity.this.finish(); // 点击返回，关闭页面
            }
        });

        webView = findViewById(R.id.webView);
        iv_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 收藏功能未实现
            }
        });
    }

    private void initWebView() {
        webView.loadUrl(newsUrl); // 加载新闻链接

        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setSupportZoom(true); // 支持缩放
        mWebSettings.setLoadWithOverviewMode(true); // 页面自适应屏幕
        mWebSettings.setUseWideViewPort(true); // 支持宽视图
        mWebSettings.setDefaultTextEncodingName("GBK"); // 设置编码
        mWebSettings.setLoadsImagesAutomatically(true); // 自动加载图片
        mWebSettings.setJavaScriptEnabled(true); // 支持 JavaScript

        // 设置 WebViewClient，防止跳转外部浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 自己加载 URL
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ll_loading.setVisibility(View.VISIBLE); // 开始加载，显示动画
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ll_loading.setVisibility(View.GONE); // 加载完成，隐藏动画
            }
        });
    }
}
