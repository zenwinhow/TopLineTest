package com.zhengwenhao.topline104022021037.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// ==== 动态权限相关导入 ====
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.bean.UserBean;
import com.zhengwenhao.topline104022021037.receiver.UpdateUserInfoReceiver;
import com.zhengwenhao.topline104022021037.utils.DBUtils;
import com.zhengwenhao.topline104022021037.utils.UtilsHelper;
import com.zhengwenhao.topline104022021037.view.ImageViewRoundOval;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_main_title, tv_back;
    //    private SwipeBackLayout layout;
    private TextView tv_nickName, tv_signature, tv_user_name, tv_sex;
    private RelativeLayout rl_nickName, rl_sex, rl_signature, rl_head, rl_title_bar;
    private String spUserName;
    private static final int CROP_PHOTO1 = 3; // 裁剪图片请求码1
    private static final int CROP_PHOTO2 = 4; // 裁剪图片请求码2
    private static final int SAVE_PHOTO = 5;  // 保存图片请求码
    private ImageViewRoundOval iv_photo; // 头像控件
    private Bitmap head;                 // 用户头像Bitmap
    private static final String path = "/sdcard/TopLine/myHead/"; // sd卡路径，存头像

    // ==== 权限相关请求码 ====
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1002; // 动态权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ---- 优先检查并申请SD卡读写权限 ----
        requestSDCardPermission();

        setContentView(R.layout.activity_user_info);

        // 从SharedPreferences获取登录用户名
        spUserName = UtilsHelper.readLoginUserName(this);
        init();      // 初始化控件
        initData();  // 初始化数据
        setListener(); // 设置监听器
    }

    /**
     * 检查并动态申请SD卡读写权限
     * Android 6.0+必须动态申请，否则无法正常保存和读取头像等文件
     * 只要用户没授权，部分功能会失效
     */
    private void requestSDCardPermission() {
        // 判断是否已获得读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // 未授权则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        // 已有权限则无需处理
    }

    /**
     * 权限请求结果回调
     * 用户授权后即可正常使用SD卡相关功能
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已获得
                Toast.makeText(this, "已获得SD卡读写权限", Toast.LENGTH_SHORT).show();
            } else {
                // 权限被拒绝
                Toast.makeText(this, "SD卡读写权限被拒绝，部分功能将无法使用", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init() {
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("个人资料");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        rl_nickName = (RelativeLayout) findViewById(R.id.rl_nickName);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        rl_head = (RelativeLayout) findViewById(R.id.rl_head);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        iv_photo = (ImageViewRoundOval) findViewById(R.id.iv_head_icon);
    }

    /**
     * 获取数据
     * 此方法负责从数据库中读取用户信息，如无则初始化默认数据
     */
    private void initData() {
        UserBean bean = null;
        bean = DBUtils.getInstance(this).getUserInfo(spUserName); // 查询数据库获取数据
        // 如果无数据则插入默认数据
        if (bean == null) {
            bean = new UserBean();
            bean.setUserName(spUserName);
            bean.setNickName("问答精灵");
            bean.setSex("男");
            bean.setSignature("传智播客问答精灵");
            iv_photo.setImageResource(R.drawable.default_head);
            // 保存用户信息到数据库
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
        setValue(bean); // 设置界面显示内容
    }

    /**
     * 为界面控件设置值
     */
    private void setValue(UserBean bean) {
        tv_nickName.setText(bean.getNickName());
        tv_user_name.setText(bean.getUserName());
        tv_sex.setText(bean.getSex());
        tv_signature.setText(bean.getSignature());
        // 尝试加载头像
        Bitmap bt = BitmapFactory.decodeFile(bean.getHead());
        if (bt != null) {
            // 将Bitmap转换为Drawable并显示
            @SuppressWarnings("deprecation")
            BitmapDrawable drawable = new BitmapDrawable(bt);
            iv_photo.setImageDrawable(drawable);
        } else {
            iv_photo.setImageResource(R.drawable.default_head);
        }
    }

    /**
     * 设置控件的点击监听事件
     */
    private void setListener() {
        tv_back.setOnClickListener(this);
        rl_nickName.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
        rl_head.setOnClickListener(this);
    }

    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            // 返回键
            this.finish();
        } else if (id == R.id.rl_nickName) {
            // 修改昵称
            // TODO: 打开昵称修改对话框
        } else if (id == R.id.rl_sex) {
            // 性别选择
            String sex = tv_sex.getText().toString();
            sexDialog(sex);
        } else if (id == R.id.rl_signature) {
            // 修改签名
            // TODO: 打开签名修改对话框
        } else if (id == R.id.rl_head) {
            // 更换头像
            showTypeDialog();
        } else {
            // 默认情况（可省略）
        }
    }

    // 头像选择对话框，允许用户从相册或拍照选择头像
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        // 相册选取
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, CROP_PHOTO1);
                dialog.dismiss();
            }
        });
        // 拍照选取
        tv_select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), spUserName + "_head.jpg")));
                startActivityForResult(intent2, CROP_PHOTO2);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 性别弹窗选择框
     */
    private void sexDialog(String sex) {
        int sexFlag = 0;
        if ("男".equals(sex)) {
            sexFlag = 0;
        } else if ("女".equals(sex)) {
            sexFlag = 1;
        }
        final String[] items = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("性别");
        builder.setSingleChoiceItems(items, sexFlag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(UserInfoActivity.this, items[which], Toast.LENGTH_SHORT).show();
                setSex(items[which]);
            }
        });
        builder.create().show();
    }

    /**
     * 更新界面上的性别，并同步数据库
     */
    private void setSex(String sex) {
        tv_sex.setText(sex);
        // 更新数据库中的性别字段
        DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("sex", sex, spUserName);
    }

    /**
     * 启动带返回结果的跳转
     */
    public void enterActivityForResult(Class<?> to, int requestCode, Bundle b) {
        Intent i = new Intent(this, to);
        i.putExtras(b);
        startActivityForResult(i, requestCode);
    }

    /**
     * 处理跳转返回的数据，包括图片裁剪和保存
     */
    private String new_info; // 存储新数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CROP_PHOTO1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData()); // 调用裁剪
                }
                break;
            case CROP_PHOTO2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + spUserName + "_head.jpg");
                    cropPhoto(Uri.fromFile(temp));
                }
                break;
            case SAVE_PHOTO:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");
                    if (head != null) {
                        String fileName = setPicToView(head);
                        // 保存头像地址到数据库
                        DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("head", fileName, spUserName);
                        iv_photo.setImageBitmap(head); // 显示新头像
                        // 发送广播通知界面更新头像
                        Intent intent = new Intent(UpdateUserInfoReceiver.ACTION.UPDATE_USERINFO);
                        intent.putExtra(UpdateUserInfoReceiver.INTENT_TYPE.TYPE_NAME, UpdateUserInfoReceiver.INTENT_TYPE.UPDATE_HEAD);
                        intent.putExtra("head", fileName);
                        sendBroadcast(intent);
                    }
                }
                break;
        }
    }

    /**
     * 系统裁剪功能，调用系统裁剪图片界面
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 设置宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 设置输出图片大小
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, SAVE_PHOTO);
    }

    /**
     * 保存裁剪后的头像到SD卡，并返回保存路径
     * 需要有SD卡读写权限，否则写入失败
     */
    private String setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检查sd卡可用
            return "";
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs(); // 创建目录
        String fileName = path + spUserName + "_head.jpg";
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b); // 写入文件
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
}
