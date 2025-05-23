package com.zhengwenhao.topline104022021037.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.activity.CollectionActivity;
import com.zhengwenhao.topline104022021037.activity.LoginActivity;
import com.zhengwenhao.topline104022021037.activity.SettingActivity;
import com.zhengwenhao.topline104022021037.receiver.UpdateUserInfoReceiver;
import com.zhengwenhao.topline104022021037.utils.DBUtils;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
import com.zhengwenhao.topline104022021037.activity.UserInfoActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout ll_calendar, ll_constellation, ll_scraw, ll_map;
    private RelativeLayout rl_collection, rl_setting;
    private CircleImageView iv_avatar;
    private View view;
    private UpdateUserInfoReceiver updateUserInfoReceiver;
    private IntentFilter filter;
    private boolean isLogin = false;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    public MeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);
        initView(view); // 初始化界面控件
        return view;
    }

    private void initView(View view) {
        // 获取各控件实例
        ll_calendar = view.findViewById(R.id.ll_calendar);
        ll_constellation = view.findViewById(R.id.ll_constellation);
        ll_scraw = view.findViewById(R.id.ll_scraw);
        ll_map = view.findViewById(R.id.ll_map);
        rl_collection = view.findViewById(R.id.rl_collection);
        rl_setting = view.findViewById(R.id.rl_setting);
        iv_avatar = view.findViewById(R.id.iv_avatar);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_tool_bar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarTitle);

        // 读取登录状态
        isLogin = UtilsHelper.readLoginStatus(getActivity());
        setLoginParams(isLogin);
        setListener(); // 设置点击事件监听
        receiver();    // 注册广播接收器
    }

    private void receiver() {
        updateUserInfoReceiver = new UpdateUserInfoReceiver(new UpdateUserInfoReceiver.BaseOnReceiveMsgListener() {
            @Override
            public void onReceiveMsg(Context context, Intent intent) {
                String action = intent.getAction();
                if (UpdateUserInfoReceiver.ACTION.UPDATE_USERINFO.equals(action)) {
                    String type = intent.getStringExtra(UpdateUserInfoReceiver.INTENT_TYPE.TYPE_NAME);

                    // 更新头像
                    if (UpdateUserInfoReceiver.INTENT_TYPE.UPDATE_HEAD.equals(type)) {
                        String head = intent.getStringExtra("head");
                        Bitmap bt = BitmapFactory.decodeFile(head);
                        if (bt != null) {
                            Drawable drawable = new BitmapDrawable(bt); // 转换成 drawable
                            iv_avatar.setImageDrawable(drawable);
                        } else {
                            iv_avatar.setImageResource(R.drawable.default_head);
                        }
                    }
                }
            }
        });

        filter = new IntentFilter(UpdateUserInfoReceiver.ACTION.UPDATE_USERINFO);
        getActivity().registerReceiver(updateUserInfoReceiver, filter); // 注册广播
    }

    private void setListener() {
        // 设置各项点击监听器
        ll_calendar.setOnClickListener(this);
        ll_constellation.setOnClickListener(this);
        ll_scraw.setOnClickListener(this);
        ll_map.setOnClickListener(this);
        rl_collection.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateUserInfoReceiver != null) {
            getActivity().unregisterReceiver(updateUserInfoReceiver); // 注销广播
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.ll_calendar) {
            // TODO:跳转到日历界面（待实现）
        } else if (id == R.id.ll_constellation) {
            // TODO:跳转到星座界面（待实现）
        } else if (id == R.id.ll_scraw) {
            // TODO:跳转到涂鸦界面（待实现）
        } else if (id == R.id.ll_map) {
            // TODO:跳转到地图界面（待实现）
        } else if (id == R.id.rl_collection) {
            if (isLogin) {
                // 跳转到“收藏”界面
                Intent collection = new Intent(getActivity(), CollectionActivity.class);
                startActivity(collection);
            } else {
                Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.rl_setting) {
            if (isLogin) {
                // 跳转到“设置”界面
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivityForResult(settingIntent, 1);
            } else {
                Toast.makeText(getActivity(), "您还未登录，请先登录", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_avatar) {
            if (isLogin) {
                // 跳转到“个人资料”界面
                Intent userinfo = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(userinfo);
            } else {
                // 跳转到“登录”界面
                Intent login = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(login, 1);
            }
        }

    }

    /**
     * 根据登录状态设置头像与标题
     */
    public void setLoginParams(boolean isLogin) {
        if (isLogin) {
            String userName = UtilsHelper.readLoginUserName(getActivity());
            collapsingToolbarLayout.setTitle(userName);

            String head = DBUtils.getInstance(getActivity()).getUserHead(userName);
            Bitmap bt = BitmapFactory.decodeFile(head);
            if (bt != null) {
                Drawable drawable = new BitmapDrawable(bt); // 转换成 drawable
                iv_avatar.setImageDrawable(drawable);
            } else {
                iv_avatar.setImageResource(R.drawable.default_head);
            }
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
            collapsingToolbarLayout.setTitle("点击登录");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            boolean isLogin = data.getBooleanExtra("isLogin", false);
            setLoginParams(isLogin);
            this.isLogin = isLogin;
        }
    }
}
