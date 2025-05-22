package com.zhengwenhao.topline104022021037.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;
import android.text.TextUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
//import com.zhengwenhao.topline104022021037.view.SwipeBackLayout;

// 用户信息修改界面 Activity
public class ChangeUserInfoActivity extends AppCompatActivity {

    private TextView tv_main_title, tv_save;
    private RelativeLayout rl_title_bar;
    private TextView tv_back;
    private String title, content;
    private int flag; // flag为1表示修改昵称，为2表示修改签名
    private EditText et_content;
    private ImageView iv_delete;
//    private SwipeBackLayout layout; // 侧滑返回布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定侧滑返回布局
//        layout = (SwipeBackLayout) LayoutInflater.from(this)
//                .inflate(R.layout.base, null);
//        layout.attachToActivity(this);

        setContentView(R.layout.activity_change_user_info);
        // 设置此界面为竖屏（可以在Manifest指定，此处仅注释）
        init(); // 初始化视图与数据
    }

    private void init() {
        // 从个人资料界面传递过来的标题和内容
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        flag = getIntent().getIntExtra("flag", 0);

        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText(title);

        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        // 设置标题栏背景颜色
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_save = (TextView) findViewById(R.id.tv_save);

        // 返回与保存按钮都显示
        tv_back.setVisibility(View.VISIBLE);
        tv_save.setVisibility(View.VISIBLE);

        et_content = (EditText) findViewById(R.id.et_content);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);

        if (!TextUtils.isEmpty(content)) {
            // 设置初始内容并选中
            et_content.setText(content);
            et_content.setSelection(content.length());
        }

        contentListener(); // 监听输入内容
        // 返回按钮事件
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUserInfoActivity.this.finish();
            }
        });
        // 清空内容按钮事件
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_content.setText("");
            }
        });
        // 保存按钮事件
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                String etContent = et_content.getText().toString().trim();
                switch (flag) {
                    case 1: // 修改昵称
                        if (!TextUtils.isEmpty(etContent)) {
                            data.putExtra("nickName", etContent);
                            setResult(RESULT_OK, data);
                            Toast.makeText(ChangeUserInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        } else {
                            Toast.makeText(ChangeUserInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2: // 修改签名
                        if (!TextUtils.isEmpty(etContent)) {
                            data.putExtra("signature", etContent);
                            setResult(RESULT_OK, data);
                            Toast.makeText(ChangeUserInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            ChangeUserInfoActivity.this.finish();
                        } else {
                            Toast.makeText(ChangeUserInfoActivity.this, "签名不能为空", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 监听个人资料修改界面输入人的文字
     * 这里用于动态显示清空按钮和限制输入字数
     */
    private void contentListener() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = et_content.getText();
                int len = editable.length(); // 当前输入长度
                if (len > 0) {
                    iv_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_delete.setVisibility(View.GONE);
                }
                // 按类型限制字数
                switch (flag) {
                    case 1: // 昵称
                        // 昵称限制最多8个汉字
                        if (len > 8) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            // 截取前8位
                            String newStr = str.substring(0, 8);
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            int newLen = editable.length();
                            // 修正光标位置
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            Selection.setSelection(editable, selEndIndex);
                        }
                        break;
                    case 2: // 签名
                        // 签名限制最多16个汉字
                        if (len > 16) {
                            int selEndIndex = Selection.getSelectionEnd(editable);
                            String str = editable.toString();
                            // 截取前16位
                            String newStr = str.substring(0, 16);
                            et_content.setText(newStr);
                            editable = et_content.getText();
                            int newLen = editable.length();
                            // 修正光标位置
                            if (selEndIndex > newLen) {
                                selEndIndex = editable.length();
                            }
                            Selection.setSelection(editable, selEndIndex);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 输入前，无需处理
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 输入后，无需处理
            }
        });
    }
}
