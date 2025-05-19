package com.zhengwenhao.topline104022021037.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.adapter.PythonListAdapter;
import com.zhengwenhao.topline104022021037.bean.PythonBean;
import com.zhengwenhao.PullToRefreshView;
import com.zhengwenhao.topline104022021037.utils.Constant;
import com.zhengwenhao.topline104022021037.utils.JsonParse;
import com.zhengwenhao.topline104022021037.view.WrapRecyclerView;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;

/**
 * PythonActivity 类，用于展示 Python 学科相关的信息。
 * 该 Activity 使用 OkHttp 进行网络请求，PullToRefreshView 进行下拉刷新，WrapRecyclerView 进行列表展示。
 */
public class PythonActivity extends AppCompatActivity {

    private PullToRefreshView mPullToRefreshView; // 下拉刷新控件
    private WrapRecyclerView recyclerView; // 自定义的 RecyclerView，用于展示数据列表

    public static final int REFRESH_DELAY = 1000; // 下拉刷新延迟时间，单位：毫秒
    public static final int MSG_PYTHON_OK = 1;  // 消息标识：获取 Python 数据成功

    private TextView tv_main_title, tv_back; // 标题栏的标题和返回按钮
    private RelativeLayout rl_title_bar; // 标题栏布局

    private MHandler mHandler; // 自定义的 Handler，用于处理消息
    private PythonListAdapter adapter; // RecyclerView 的适配器，用于绑定数据到列表

    /**
     * Activity 的 onCreate 方法，初始化操作在这里进行。
     *
     * @param savedInstanceState 保存的实例状态，用于恢复 Activity 的状态。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home); // 设置布局文件

        mHandler = new MHandler(); // 创建 MHandler 实例

        initData();   // 初始化数据，进行网络请求
        initView();   // 初始化视图，设置 UI 元素
    }

    /**
     * 初始化视图，包括设置标题栏、返回按钮、RecyclerView 和下拉刷新控件。
     */
    private void initView() {
        // 获取标题栏标题 TextView，并设置标题文本
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("Python 学科");

        // 获取标题栏布局，并设置背景颜色
        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));

        // 获取返回按钮 TextView，并设置可见和点击事件
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PythonActivity.this.finish(); // 点击后关闭当前 Activity
            }
        });

        // 获取 RecyclerView，并设置布局管理器和适配器
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 使用线性布局管理器
        adapter = new PythonListAdapter(); // 创建 PythonListAdapter 实例
        recyclerView.setAdapter(adapter); // 设置适配器

        // 获取下拉刷新控件，并设置刷新监听器
        mPullToRefreshView = findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 在下拉刷新触发时，延迟一段时间后执行数据重新加载
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false); // 停止刷新动画
                        initData();  // 重新加载数据
                    }
                }, REFRESH_DELAY); // 设置延迟时间
            }
        });
    }

    /**
     * 初始化数据，通过 OkHttp 发起网络请求获取 Python 相关数据。
     */
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient(); // 创建 OkHttpClient 实例
        // 构建 Request 对象，指定请求的 URL
        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.REQUEST_PYTHON_URL) // 从 Constant 类中获取 URL
                .build();

        Call call = okHttpClient.newCall(request); // 创建 Call 对象

        // 异步访问网络
        call.enqueue(new Callback() {
            /**
             * 网络请求失败时回调
             *
             * @param request 请求对象
             * @param e       异常对象
             */
            @Override
            public void onFailure(Request request, IOException e) {
                // 这里通常进行错误处理，例如弹出 Toast 提示用户
            }

            /**
             * 网络请求成功时回调
             *
             * @param response 响应对象
             * @throws IOException 可能抛出的 IO 异常
             */
            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string(); // 获取响应体中的字符串数据
                Message msg = new Message(); // 创建 Message 对象，用于在 Handler 中传递消息
                msg.what = MSG_PYTHON_OK; // 设置消息标识
                msg.obj = res; // 将网络请求返回的字符串数据放到 Message 中
                mHandler.sendMessage(msg); // 通过 Handler 发送消息
            }
        });
    }

    /**
     * 自定义的 Handler 类，用于处理网络请求返回的数据。
     */
    class MHandler extends Handler {
        /**
         * Handler 处理消息的方法
         *
         * @param msg 接收到的消息对象
         */
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_PYTHON_OK: // 如果是获取 Python 数据成功的消息
                    if (msg.obj != null) {
                        String jsonResult = (String) msg.obj; // 获取消息中携带的字符串数据
                        // 使用 JsonParse 工具类解析 JSON 数据，得到 PythonBean 列表
                        List<PythonBean> pythonList = JsonParse.getInstance().getPythonList(jsonResult);
                        adapter.setData(pythonList);  // 将解析得到的数据设置到适配器中
                    }
                    break;
            }
        }
    }
}