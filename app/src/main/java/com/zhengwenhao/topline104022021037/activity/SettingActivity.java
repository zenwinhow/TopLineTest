package com.zhengwenhao.topline104022021037.activity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
//import com.zhengwenhao.topline104022021037.view.SwipeBackLayout;

/**
 * 设置页面 Activity
 * 负责展示和处理“修改密码”、“设置密保”、“退出登录”等功能项
 */
public class SettingActivity extends AppCompatActivity {
    private TextView tv_main_title, tv_back; // 标题栏文本、返回按钮
    private RelativeLayout rl_title_bar; // 标题栏布局
    private RelativeLayout rl_modify_psw, rl_security_setting, rl_exit_login; // 各功能区域
    public static SettingActivity instance = null; // 单例（部分业务可能需要）
//    private SwipeBackLayout layout; // 支持滑动返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 用 SwipeBackLayout 包裹 Activity，实现滑动返回
//        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
//                R.layout.base, null);
//        layout.attachToActivity(this);
        setContentView(R.layout.activity_setting); // 设置主布局
        instance = this;
        init(); // 初始化控件和事件
    }

    /**
     * 初始化控件、标题栏和功能项点击事件
     */
    private void init() {
        // 标题栏相关控件
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("设置"); // 设置标题为“设置”

        tv_back = (TextView) findViewById(R.id.tv_back);
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress)); // 标题栏背景色

        // 功能区块
        rl_modify_psw = (RelativeLayout) findViewById(R.id.rl_modify_psw); // 修改密码
        rl_security_setting = (RelativeLayout) findViewById(R.id.rl_security_setting); // 设置密保
        rl_exit_login = (RelativeLayout) findViewById(R.id.rl_exit_login); // 退出登录

        // 返回按钮可见并响应点击：关闭当前Activity
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        // “修改密码”点击事件（跳转到修改密码界面，可自行实现跳转逻辑）
        rl_modify_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到“修改密码”界面
                Intent intent = new Intent(SettingActivity.this, ModifyPswActivity.class);
                startActivity(intent);
            }
        });

        // “设置密保”点击事件（跳转到密保设置界面，可自行实现跳转逻辑）
        rl_security_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 跳转到“设置密保”界面
            }
        });

        // “退出登录”点击事件
        rl_exit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出提示
                Toast.makeText(SettingActivity.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                // 清除登录状态和登录用户名
                UtilsHelper.clearLoginStatus(SettingActivity.this);
                // 把退出成功的状态传递到上一个页面（如 MeFragment）
                Intent data = new Intent();
                data.putExtra("isLogin", false); // 标记未登录
                setResult(RESULT_OK, data); // 设置返回结果
                SettingActivity.this.finish(); // 关闭设置页面
            }
        });
    }
}
