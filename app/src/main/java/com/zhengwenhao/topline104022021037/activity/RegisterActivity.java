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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_main_title, tv_back;
    private RelativeLayout rl_title_bar;
//    private SwipeBackLayout layout; // 如果项目中有自定义的 SwipeBackLayout
    private EditText et_psw, et_user_name;
    private ImageView iv_show_psw;
    private Button btn_register;

    private String userName, psw;
    private boolean isShowPsw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化滑动返回布局（可选）
//        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
//        layout.attachToActivity(this);

        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("注册");
        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);

        btn_register = findViewById(R.id.btn_register);
        et_user_name = findViewById(R.id.et_user_name);
        et_psw = findViewById(R.id.et_psw);
        iv_show_psw = findViewById(R.id.iv_show_psw);

        tv_back.setOnClickListener(this);
        iv_show_psw.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.tv_back) {
            RegisterActivity.this.finish(); // 返回上一个界面

        } else if (id == R.id.iv_show_psw) {
            // 显示/隐藏密码
            psw = et_psw.getText().toString();
            if (isShowPsw) {
                iv_show_psw.setImageResource(R.drawable.hide_psw_icon);
                et_psw.setTransformationMethod(PasswordTransformationMethod.getInstance()); // 隐藏密码
                isShowPsw = false;
                if (psw != null) {
                    et_psw.setSelection(psw.length());
                }
            } else {
                iv_show_psw.setImageResource(R.drawable.show_psw_icon);
                et_psw.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); // 显示密码
                isShowPsw = true;
                if (psw != null) {
                    et_psw.setSelection(psw.length());
                }
            }

        } else if (id == R.id.btn_register) {
            // 注册按钮点击事件
            userName = et_user_name.getText().toString().trim();
            psw = et_psw.getText().toString().trim();

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(psw)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                return;
            } else if (isExistUserName(userName)) {
                Toast.makeText(this, "此账户名已经存在", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                // 保存注册信息到 SharedPreferences
                saveRegisterInfo(userName, psw);

                // 注册成功后把用户名传递到 LoginActivity
                Intent data = new Intent();
                data.putExtra("userName", userName);
                setResult(RESULT_OK, data);
                RegisterActivity.this.finish();
            }
        }

    }

    /**
     * 从 SharedPreferences 读取输入用户名，判断是否有此用户
     */
    private boolean isExistUserName(String userName) {
        boolean has_userName = false;
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        String spPsw = sp.getString(userName, "");
        if (!TextUtils.isEmpty(spPsw)) {
            has_userName = true;
        }
        return has_userName;
    }

    /**
     * 保存用户名和加密后的密码到 SharedPreferences
     */
    private void saveRegisterInfo(String userName, String psw) {
        String md5Psw = MD5Utils.md5(psw); // 密码用 MD5 加密
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        // 以用户名为 key，密码为 value 保存
        editor.putString(userName, md5Psw);
        editor.commit(); // 提交修改
    }
}
