package com.zhengwenhao.topline104022021037.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhengwenhao.PullToRefreshView;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.adapter.VideoListAdapter;
import com.zhengwenhao.topline104022021037.bean.VideoBean;
import com.zhengwenhao.topline104022021037.utils.Constant;
import com.zhengwenhao.topline104022021037.view.WrapRecyclerView;
import com.zhengwenhao.topline104022021037.utils.JsonParse;

import java.io.IOException;
import java.util.List;


public class VideoFragment extends Fragment {

    private PullToRefreshView mPullToRefreshView;
    private WrapRecyclerView recyclerView;

    public static final int REFRESH_DELAY = 1000;  // 刷新延迟时间
    public static final int MSG_VIDEO_OK = 1;       // 获取数据成功标志

    private MHandler mHandler;
    private VideoListAdapter adapter;

    public VideoFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHandler = new MHandler(); // 初始化 Handler
        initData();                // 异步获取数据
        return initView(inflater, container);
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoListAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        // 初始化下拉刷新组件
        mPullToRefreshView = view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 模拟刷新数据
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        initData(); // 重新拉取数据
                    }
                }, REFRESH_DELAY);
            }
        });

        return view;
    }

    // 初始化数据，发起网络请求
    private void initData() {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constant.WEB_SITE + Constant.REQUEST_VIDEO_URL)
                .build();

        Call call = okHttpClient.newCall(request);

        // 启动异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String res = response.body().string();
                Message msg = new Message();
                msg.what = MSG_VIDEO_OK;
                msg.obj = res;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * Handler 处理异步数据响应
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            switch (msg.what) {
                case MSG_VIDEO_OK:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;

                        // 使用 Gson 解析数据
                        List<VideoBean> videoList = JsonParse.getInstance().getVideoList(vlResult);
                        adapter.setData(videoList);
                    }
                    break;
            }
        }
    }
}
