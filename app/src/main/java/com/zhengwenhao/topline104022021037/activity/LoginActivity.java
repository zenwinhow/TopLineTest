package com.zhengwenhao.topline104022021037.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.utils.MD5Utils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_psw, et_user_name;
    private TextView tv_quick_register, tv_forget_psw;
    private ImageView iv_show_psw;
    private Button btn_login;
    private boolean isShowPsw = false;
    private String userName, psw, spPsw;
    private TextView tv_main_title, tv_back;
    private RelativeLayout rl_title_bar;
//    private SwipeBackLayout layout; // 若用到侧滑返回，自定义SwipeBackLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化滑动返回（可选）
//        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
//        layout.attachToActivity(this);

        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("登录");
        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);

        et_user_name = findViewById(R.id.et_user_name);
        et_psw = findViewById(R.id.et_psw);
        iv_show_psw = findViewById(R.id.iv_show_psw);
        tv_quick_register = findViewById(R.id.tv_quick_register);
        tv_forget_psw = findViewById(R.id.tv_forget_psw);
        btn_login = findViewById(R.id.btn_login);

        tv_back.setOnClickListener(this);
        iv_show_psw.setOnClickListener(this);
        tv_quick_register.setOnClickListener(this);
        tv_forget_psw.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_back) {
            // 返回
            LoginActivity.this.finish();

        } else if (id == R.id.iv_show_psw) {
            // 显示或隐藏密码
            psw = et_psw.getText().toString();
            if (isShowPsw) {
                iv_show_psw.setImageResource(R.drawable.hide_psw_icon);
                et_psw.setTransformationMethod(PasswordTransformationMethod.getInstance()); // 隐藏密码
                isShowPsw = false;
                if (psw != null) et_psw.setSelection(psw.length());
            } else {
                iv_show_psw.setImageResource(R.drawable.show_psw_icon);
                et_psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // 显示密码
                isShowPsw = true;
                if (psw != null) et_psw.setSelection(psw.length());
            }

        } else if (id == R.id.btn_login) {
            // 登录按钮点击
            userName = et_user_name.getText().toString().trim();
            psw = et_psw.getText().toString().trim();
            String md5Psw = MD5Utils.md5(psw);
            spPsw = readPsw(userName); // 从本地读取已保存的加密密码

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(psw)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            } else if (md5Psw.equals(spPsw)) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                // 保存登录状态
                saveLoginStatus(true, userName);
                // 把登录状态通过 Intent 传递到 MeFragment 等界面
                Intent data = new Intent();
                data.putExtra("isLogin", true);
                setResult(RESULT_OK, data);
                LoginActivity.this.finish();
                return;
            } else if (!TextUtils.isEmpty(spPsw) && !md5Psw.equals(spPsw)) {
                Toast.makeText(this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "此用户名不存在", Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (id == R.id.tv_quick_register) {
            // 跳转注册页面
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivityForResult(intent, 1);

        } else if (id == R.id.tv_forget_psw) {
            // TODO:忘记密码，跳转相关页面（可实现）
        }

    }

    /**
     * 从 SharedPreferences 根据用户名读取密码
     */
    private String readPsw(String userName) {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString(userName, "");
    }

    /**
     * 保存登录状态和登录用户名到 SharedPreferences
     */
    private void saveLoginStatus(boolean status, String userName) {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", status);
        editor.putString("loginUserName", userName);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 从注册界面传递过来的用户名，自动填充用户名输入框
        if (data != null) {
            String userName = data.getStringExtra("userName");
            if (!TextUtils.isEmpty(userName)) {
                et_user_name.setText(userName);
                et_user_name.setSelection(userName.length()); // 光标定位到末尾
            }
        }
    }
}
