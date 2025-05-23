package com.zhengwenhao.topline104022021037.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.utils.MD5Utils;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
//import com.zhengwenhao.topline104022021037.view.SwipeBackLayout;

public class FindPswActivity extends AppCompatActivity {

    private EditText et_validate_name, et_user_name; // 验证姓名与用户名输入框
    private Button btn_validate; // 验证按钮
    private TextView tv_main_title; // 主标题
    private TextView tv_back; // 返回按钮

    // from 为 security 时表示是从“设置密保”界面跳转过来的，否则就是从“登录”界面跳转过来的
    private String from;

    private TextView tv_reset_psw, tv_user_name; // 密码重置提示框、用户名提示
//    private SwipeBackLayout layout; // 侧滑返回布局
    private RelativeLayout rl_title_bar; // 顶部标题栏布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null); // 加载基础布局
//        layout.attachToActivity(this); // 绑定到当前Activity
        setContentView(R.layout.activity_find_psw); // 设置主界面布局

        // 获取从“登录”界面或“设置”界面传递过来的数据
        from = getIntent().getStringExtra("from");
        init(); // 初始化控件
    }

    /**
     * 获取界面控件及处理相应控件的点击事件
     */
    private void init() {
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);

        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress)); // 设置标题栏背景色

        et_validate_name = findViewById(R.id.et_validate_name);
        btn_validate = findViewById(R.id.btn_validate);
        tv_reset_psw = findViewById(R.id.tv_reset_psw);
        et_user_name = findViewById(R.id.et_user_name);
        tv_user_name = findViewById(R.id.tv_user_name);

        if ("security".equals(from)) { // 设置密保流程
            tv_main_title.setText("设置密保");
        } else {
            tv_main_title.setText("找回密码");
            tv_user_name.setVisibility(View.VISIBLE);
            et_user_name.setVisibility(View.VISIBLE);
        }

        tv_back.setOnClickListener(v -> finish()); // 返回按钮点击事件

        btn_validate.setOnClickListener(v -> {
            String validateName = et_validate_name.getText().toString().trim();
            if ("security".equals(from)) { // 设置密保
                if (TextUtils.isEmpty(validateName)) {
                    Toast.makeText(this, "请输入要验证的姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(this, "密保设置成功", Toast.LENGTH_SHORT).show();
                    saveSecurity(validateName); // 存储密保答案
                    finish();
                }
            } else { // 找回密码流程
                String userName = et_user_name.getText().toString().trim();
                String sp_security = readSecurity(userName); // 获取保存的密保答案

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(this, "请输入您的用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isExistUserName(userName)) {
                    Toast.makeText(this, "您输入的用户名不存在", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(validateName)) {
                    Toast.makeText(this, "请输入要验证的姓名", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateName.equals(sp_security)) {
                    Toast.makeText(this, "输入的密保不正确", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    tv_reset_psw.setVisibility(View.VISIBLE);
                    tv_reset_psw.setText("初始密码：123456");
                    savePsw(userName); // 重置密码
                }
            }
        });
    }

    /**
     * 保存初始化的密码
     */
    private void savePsw(String userName) {
        String md5Psw = MD5Utils.md5("123456"); // 将密码加密
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(userName, md5Psw); // 存储密码
        editor.commit();
    }

    /**
     * 保存密保到 SharedPreferences 中
     */
    private void saveSecurity(String validateName) {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UtilsHelper.readLoginUserName(this) + "_security", validateName); // 存储密保信息
        editor.commit();
    }

    /**
     * 从 SharedPreferences 中读取密保
     */
    private String readSecurity(String userName) {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString(userName + "_security", "");
    }

    /**
     * 从 SharedPreferences 中根据用户输入的用户名判断是否有此用户
     */
    private boolean isExistUserName(String userName) {
        boolean hasUserName = false;
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String spPsw = sp.getString(userName, ""); // 读取该用户名的密码字段
        if (!TextUtils.isEmpty(spPsw)) {
            hasUserName = true; // 如果密码字段存在则表示用户存在
        }
        return hasUserName;
    }
}
