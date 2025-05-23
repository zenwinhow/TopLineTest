// 定义包名，这是 Android 项目中组织代码的方式
package com.zhengwenhao.topline104022021037.activity;

// 导入所需的 Android 和 Java 类库
import android.app.AlertDialog; // 用于创建对话框
import android.content.DialogInterface; // 对话框按钮点击事件的接口
import android.content.Intent; // 用于 Activity 之间的跳转和数据传递
import android.graphics.Bitmap; // Android 中的位图对象，用于处理图片
import android.graphics.BitmapFactory; // 用于从各种来源解码创建 Bitmap 对象
import android.net.Uri; // 表示一个统一资源标识符，常用于指向数据（如图片）
import android.os.Bundle; // 用于在 Activity 之间传递少量数据
import android.provider.MediaStore; // 提供对媒体库（图片、视频、音频）的访问
import android.text.TextUtils; // 用于处理字符串的工具类，如判断是否为空
import android.util.Log; // 用于在 Logcat 中输出调试信息
import android.view.View; // UI 组件的基类
import android.widget.RelativeLayout; // 一种布局容器，允许子视图相对于父视图或彼此定位
import android.widget.TextView; // 用于显示文本的 UI 组件
import android.widget.Toast; // 用于显示短暂的提示信息

import androidx.appcompat.app.AppCompatActivity; // 向后兼容的 Activity 基类，提供 ActionBar 等特性
import androidx.core.app.ActivityCompat; // 用于请求权限等辅助功能
import androidx.core.content.ContextCompat; // 用于访问颜色、Drawable 等资源的辅助类，并处理权限

import android.Manifest; // 定义了 Android 系统中的各种权限字符串
import android.content.pm.PackageManager; // 用于获取已安装应用包的信息，包括权限状态
import androidx.annotation.NonNull; // 注解，表示参数或返回值不能为 null

// 导入项目内部的类
import com.zhengwenhao.topline104022021037.R; // 项目的资源类，如布局、字符串、ID 等
import com.zhengwenhao.topline104022021037.bean.UserBean; // 用户信息的实体类
import com.zhengwenhao.topline104022021037.receiver.UpdateUserInfoReceiver; // 用于接收用户信息更新广播的接收器
import com.zhengwenhao.topline104022021037.utils.DBUtils; // 数据库操作工具类
import com.zhengwenhao.topline104022021037.utils.UtilsHelper; // 通用工具类，如此处用于读取登录用户名
import com.zhengwenhao.topline104022021037.view.ImageViewRoundOval; // 自定义的圆角或椭圆形 ImageView

import java.io.File; // Java 中的文件操作类
import java.io.FileOutputStream; // 用于向文件写入数据的输出流
import java.io.IOException; // 输入输出异常

/**
 * UserInfoActivity 类，用于显示和编辑用户的个人资料。
 * 实现了 View.OnClickListener 接口来处理点击事件。
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    // UI 控件声明
    private TextView tv_main_title, tv_back; // 标题栏的标题和返回按钮
    private TextView tv_nickName, tv_signature, tv_user_name, tv_sex; // 显示昵称、签名、用户名和性别的文本视图
    private RelativeLayout rl_nickName, rl_sex, rl_signature, rl_head, rl_title_bar; // 各个信息项的布局容器以及标题栏布局
    private ImageViewRoundOval iv_photo; // 自定义的圆形头像 ImageView

    // 数据和常量声明
    private String spUserName; // 从 SharedPreferences 或其他地方获取的当前登录用户名
    private Bitmap head; // 用于存储用户选择或拍摄的头像位图

    // ActivityForResult 请求码常量
    private static final int CHANGE_NICKNAME = 1;  // 修改昵称的请求码
    private static final int CHANGE_SIGNATURE = 2; // 修改签名的请求码
    private static final int CROP_PHOTO1 = 3;      // 从相册选择图片后进行裁剪的请求码 (选取图片)
    private static final int CROP_PHOTO2 = 4;      // 拍照后进行裁剪的请求码 (拍照)
    private static final int SAVE_PHOTO = 5;       // 保存裁剪后的图片的请求码 (裁剪完成)

    // 权限请求码
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1002; // 存储权限的请求码

    /**
     * Activity 的生命周期方法，在 Activity 创建时调用。
     * @param savedInstanceState 如果 Activity 被重新创建，则包含先前保存的状态信息。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 调用父类的 onCreate 方法
        setContentView(R.layout.activity_user_info); // 设置 Activity 的布局文件

        // 优先检查/申请存储权限，因为后续的头像操作可能需要
        checkStoragePermission();

        // 从 SharedPreferences (通过 UtilsHelper) 读取当前登录的用户名
        spUserName = UtilsHelper.readLoginUserName(this);

        // 初始化 UI 控件
        init();
        // 加载并显示用户数据
        initData();
        // 设置监听器
        setListener();
    }

    /**
     * 检查并动态申请外部存储的读写权限。
     * Android 6.0 (API 23) 及以上版本需要动态申请危险权限。
     */
    private void checkStoragePermission() {
        // 检查是否已经授予读取外部存储的权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || // 或运算符，检查是否已授予写入外部存储的权限
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // 如果任一权限未被授予，则请求权限
            ActivityCompat.requestPermissions(this,
                    new String[] { // 需要请求的权限数组
                            Manifest.permission.READ_EXTERNAL_STORAGE, // 读取外部存储权限
                            Manifest.permission.WRITE_EXTERNAL_STORAGE  // 写入外部存储权限
                    },
                    REQUEST_CODE_STORAGE_PERMISSION); // 传入权限请求码，用于在 onRequestPermissionsResult 中识别
        }
    }

    /**
     * 处理权限请求结果的回调方法。
     * @param requestCode  之前在 requestPermissions() 中传递的请求码。
     * @param permissions  请求的权限数组。
     * @param grantResults 对应权限的授予结果 (PackageManager.PERMISSION_GRANTED 或 PackageManager.PERMISSION_DENIED)。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // 调用父类方法
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) { // 判断是否是存储权限的请求结果
            boolean granted = true; // 标记所有请求的权限是否都已授予
            for (int result : grantResults) { // 遍历授予结果数组
                if (result != PackageManager.PERMISSION_GRANTED) { // 如果有任何一个权限未被授予
                    granted = false; // 将标记设为 false
                    break; // 并跳出循环
                }
            }
            if (!granted) { // 如果最终有权限未被授予
                // 提示用户缺少权限，某些功能可能受影响
                Toast.makeText(this, "缺少存储权限，头像功能可能不可用", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 初始化 UI 控件。
     * 通过 findViewById 获取布局文件中的各个视图组件。
     */
    private void init() {
        // 初始化标题栏
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("个人资料"); // 设置标题栏文字
        rl_title_bar = findViewById(R.id.title_bar);
        //noinspection deprecation 旧版本 getColor 方法，为了兼容性。推荐使用 ContextCompat.getColor
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress)); // 设置标题栏背景颜色
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE); // 显示返回按钮

        // 初始化各个信息项的 RelativeLayout 容器
        rl_nickName = findViewById(R.id.rl_nickName);
        rl_sex = findViewById(R.id.rl_sex);
        rl_signature = findViewById(R.id.rl_signature);
        rl_head = findViewById(R.id.rl_head);

        // 初始化显示用户信息的 TextView
        tv_nickName = findViewById(R.id.tv_nickName);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_signature = findViewById(R.id.tv_signature);

        // 初始化头像 ImageView
        iv_photo = findViewById(R.id.iv_head_icon);
    }

    /**
     * 初始化用户数据。
     * 从数据库中获取用户信息，如果不存在则创建默认信息并保存。
     */
    private void initData() {
        // 从数据库中获取指定用户名的用户信息
        UserBean bean = DBUtils.getInstance(this).getUserInfo(spUserName);
        if (bean == null) { // 如果数据库中没有该用户的信息
            // 创建一个新的 UserBean 对象并设置默认值
            bean = new UserBean();
            bean.setUserName(spUserName);
            bean.setNickName("问答精灵"); // 默认昵称
            bean.setSex("男");         // 默认性别
            bean.setSignature("传智播客问答精灵"); // 默认签名
            iv_photo.setImageResource(R.drawable.default_head); // 设置默认头像
            // 将新的用户信息保存到数据库
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
        // 将获取到或创建的用户信息设置到 UI 控件上显示
        setValue(bean);
    }

    /**
     * 将 UserBean 对象中的数据显示到对应的 UI 控件上。
     * @param bean 包含用户信息的 UserBean 对象。
     */
    private void setValue(UserBean bean) {
        if (bean == null) return; // 防御性编程，如果 bean 为 null 则直接返回

        // 设置昵称、用户名、性别和签名
        tv_nickName.setText(bean.getNickName());
        tv_user_name.setText(bean.getUserName());
        tv_sex.setText(bean.getSex());
        tv_signature.setText(bean.getSignature());

        // 设置头像
        String headPath = bean.getHead(); // 获取头像文件的路径
        if (headPath != null && !headPath.isEmpty()) { // 如果头像路径不为空
            File headFile = new File(headPath); // 创建文件对象
            if (headFile.exists()) { // 如果头像文件存在
                Bitmap bitmap = BitmapFactory.decodeFile(headPath); // 从文件路径解码生成 Bitmap 对象
                if (bitmap != null) { // 如果 Bitmap 解码成功
                    iv_photo.setImageBitmap(bitmap); // 设置 ImageView 显示该 Bitmap
                } else { // 如果 Bitmap 解码失败（例如文件损坏）
                    iv_photo.setImageResource(R.drawable.default_head); // 显示默认头像
                }
            } else { // 如果头像文件不存在
                iv_photo.setImageResource(R.drawable.default_head); // 显示默认头像
            }
        } else { // 如果头像路径为空
            iv_photo.setImageResource(R.drawable.default_head); // 显示默认头像
        }
    }

    /**
     * 为需要响应点击事件的 UI 控件设置监听器。
     * 当前 Activity 实现了 View.OnClickListener 接口，所以将 this 作为监听器对象。
     */
    private void setListener() {
        tv_back.setOnClickListener(this);       // 返回按钮
        rl_nickName.setOnClickListener(this);   // 昵称栏
        rl_sex.setOnClickListener(this);        // 性别栏
        rl_signature.setOnClickListener(this);  // 签名栏
        rl_head.setOnClickListener(this);       // 头像栏
    }

    /**
     * 处理所有通过 setOnClickListener 设置的点击事件。
     * @param v 被点击的 View 对象。
     */
    @Override
    public void onClick(View v) {
        int id = v.getId(); // 获取被点击 View 的 ID
        if (id == R.id.tv_back) { // 如果点击的是返回按钮
            this.finish(); // 关闭当前 Activity
        } else if (id == R.id.rl_nickName) { // 如果点击的是昵称栏
            String name = tv_nickName.getText().toString(); // 获取当前昵称
            Bundle bdName = new Bundle(); // 创建 Bundle 用于传递数据
            bdName.putString("content", name); // 存入当前昵称
            bdName.putString("title", "昵称");   // 存入标题，用于下一个 Activity 显示
            bdName.putInt("flag", 1);          // 存入标志，用于区分是修改昵称还是签名
            // 启动 ChangeUserInfoActivity 并期望返回结果，请求码为 CHANGE_NICKNAME
            enterActivityForResult(ChangeUserInfoActivity.class, CHANGE_NICKNAME, bdName);
        } else if (id == R.id.rl_sex) { // 如果点击的是性别栏
            String sex = tv_sex.getText().toString(); // 获取当前性别
            sexDialog(sex); // 调用显示性别选择对话框的方法
        } else if (id == R.id.rl_signature) { // 如果点击的是签名栏
            String signature = tv_signature.getText().toString(); // 获取当前签名
            Bundle bdSignature = new Bundle(); // 创建 Bundle 用于传递数据
            bdSignature.putString("content", signature); // 存入当前签名
            bdSignature.putString("title", "签名");      // 存入标题
            bdSignature.putInt("flag", 2);             // 存入标志
            // 启动 ChangeUserInfoActivity 并期望返回结果，请求码为 CHANGE_SIGNATURE
            enterActivityForResult(ChangeUserInfoActivity.class, CHANGE_SIGNATURE, bdSignature);
        } else if (id == R.id.rl_head) { // 如果点击的是头像栏
            // 在尝试操作前再次检查存储权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // 如果没有读取权限，则再次请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, // 同时请求读写
                        REQUEST_CODE_STORAGE_PERMISSION);
                Toast.makeText(this, "请授权存储权限后再更换头像", Toast.LENGTH_SHORT).show();
            } else {
                // 如果有权限，则显示选择图片来源（相册/相机）的对话框
                showTypeDialog();
            }
        }
    }

    /**
     * 显示选择图片来源（相册或相机）的对话框。
     */
    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 创建 AlertDialog 构造器
        final AlertDialog dialog = builder.create(); // 创建 AlertDialog 实例
        // 加载自定义的对话框布局
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = view.findViewById(R.id.tv_select_gallery); // "从相册选择" 文本视图
        TextView tv_select_camera = view.findViewById(R.id.tv_select_camera);   // "拍照" 文本视图

        // 设置 "从相册选择" 的点击事件
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个 Intent，动作为选择图片 (ACTION_PICK)
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                // 设置数据源为外部存储的图片 (MediaStore.Images.Media.EXTERNAL_CONTENT_URI)，类型为所有图片 ("image/*")
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                // 为目标应用授予读取 URI 的权限，增强兼容性，特别是对于某些文件选择器
                intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 启动 Activity 并期望返回结果，请求码为 CROP_PHOTO1
                startActivityForResult(intent1, CROP_PHOTO1);
                dialog.dismiss(); // 关闭对话框
            }
        });

        // 设置 "拍照" 的点击事件
        tv_select_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个 Intent，动作为拍摄图片 (ACTION_IMAGE_CAPTURE)
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 获取应用的外部文件目录下的 "myHead" 子目录
                File dir = getExternalFilesDir("myHead");
                if (dir != null && !dir.exists()) { // 如果目录不为 null 且不存在
                    dir.mkdirs(); // 创建目录
                }
                // 在该目录下创建一个临时文件用于存储拍摄的照片
                File tempFile = new File(dir, spUserName + "_head.jpg");
                // 将拍摄的照片输出到这个临时文件的 Uri
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                // 为目标应用授予写入 URI 的权限（如果需要的话，虽然这里是输出，但有些相机应用可能需要）
                intent2.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // 启动 Activity 并期望返回结果，请求码为 CROP_PHOTO2
                startActivityForResult(intent2, CROP_PHOTO2);
                dialog.dismiss(); // 关闭对话框
            }
        });
        dialog.setView(view); // 将自定义布局设置到对话框中
        dialog.show(); // 显示对话框
    }

    /**
     * 显示性别选择对话框。
     * @param sex 当前用户的性别字符串 ("男" 或 "女")。
     */
    private void sexDialog(String sex) {
        // 根据当前性别确定对话框中默认选中的项
        int sexFlag = "男".equals(sex) ? 0 : 1; // 0 代表 "男"，1 代表 "女"
        final String[] items = {"男", "女"}; // 性别选项数组

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 创建 AlertDialog 构造器
        builder.setTitle("性别"); // 设置对话框标题
        // 设置单选列表项
        builder.setSingleChoiceItems(items, sexFlag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { // which 是被选中项的索引
                dialog.dismiss(); // 关闭对话框
                Toast.makeText(UserInfoActivity.this, items[which], Toast.LENGTH_SHORT).show(); // 显示选择的性别
                setSex(items[which]); // 调用方法更新性别信息
            }
        });
        builder.create().show(); // 创建并显示对话框
    }

    /**
     * 更新性别信息到 UI 和数据库。
     * @param sex 选择的新性别字符串。
     */
    private void setSex(String sex) {
        tv_sex.setText(sex); // 更新 UI 上的性别显示
        // 将新的性别信息更新到数据库
        DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("sex", sex, spUserName);
    }

    /**
     * 启动一个新的 Activity 并期望返回结果的辅助方法。
     * @param to          目标 Activity 的 Class 对象。
     * @param requestCode 请求码。
     * @param b           包含要传递给目标 Activity 的数据的 Bundle 对象。
     */
    public void enterActivityForResult(Class<?> to, int requestCode, Bundle b) {
        Intent i = new Intent(this, to); // 创建 Intent
        if (b != null) {
            i.putExtras(b); // 如果 Bundle 不为 null，则将其附加到 Intent
        }
        startActivityForResult(i, requestCode); // 启动 Activity
    }

    private String new_info; // 用于临时存储从 ChangeUserInfoActivity 返回的新信息（昵称或签名）

    /**
     * 处理由 startActivityForResult 启动的 Activity 返回的结果。
     * @param requestCode 原始请求中提供的请求码。
     * @param resultCode  子 Activity 返回的结果码 (通常是 RESULT_OK 或 RESULT_CANCELED)。
     * @param data        一个 Intent 对象，包含返回的数据。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // 调用父类方法

        // 如果用户取消了操作 (resultCode != RESULT_OK)，通常不需要做额外处理，除非有特定逻辑
        if (resultCode != RESULT_OK) {
            // 可以选择性地处理取消操作，例如显示一个 Toast
            // Toast.makeText(this, "操作已取消", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CROP_PHOTO1: // 从相册选择图片返回
                if (data != null && data.getData() != null) { // 确保返回了数据且包含 Uri
                    cropPhoto(data.getData()); // 调用裁剪图片的方法，传入图片的 Uri
                } else {
                    Toast.makeText(this, "选择图片失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_PHOTO2: // 拍照返回
                // 拍照的结果通常是直接写入到之前指定的 Uri，data 可能为 null 或不包含 Uri
                File dir = getExternalFilesDir("myHead");
                if (dir == null) {
                    Toast.makeText(this, "无法访问存储目录", Toast.LENGTH_SHORT).show();
                    return;
                }
                File tempFile = new File(dir, spUserName + "_head.jpg"); // 之前保存照片的临时文件
                if (tempFile.exists()) {
                    cropPhoto(Uri.fromFile(tempFile)); // 调用裁剪图片的方法，传入临时文件的 Uri
                } else {
                    Toast.makeText(this, "拍照失败或照片未保存", Toast.LENGTH_SHORT).show();
                }
                break;
            case SAVE_PHOTO: // 裁剪图片后返回
                if (data != null) {
                    Bundle extras = data.getExtras(); // 尝试从 extras 中获取裁剪后的 Bitmap (一些裁剪应用会这样做)
                    if (extras != null) {
                        head = extras.getParcelable("data"); // 获取 Bitmap 数据
                        if (head != null) {
                            String fileName = setPicToView(head); // 将 Bitmap 保存到文件，并获取文件名
                            if (fileName != null) {
                                // 更新数据库中的头像路径
                                DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("head", fileName, spUserName);
                                iv_photo.setImageBitmap(head); // 在 ImageView 中显示新的头像
                                // 发送广播通知其他地方用户信息已更新（特别是头像）
                                Intent intent = new Intent(UpdateUserInfoReceiver.ACTION.UPDATE_USERINFO);
                                intent.putExtra(UpdateUserInfoReceiver.INTENT_TYPE.TYPE_NAME, UpdateUserInfoReceiver.INTENT_TYPE.UPDATE_HEAD);
                                intent.putExtra("head", fileName); // 携带新的头像路径
                                sendBroadcast(intent);
                            } else {
                                Toast.makeText(this, "保存头像失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "没有获取到裁剪后的图片数据", Toast.LENGTH_SHORT).show();
                        }
                    } else if (data.getData() != null) { // 如果 extras 中没有，尝试从 data.getData() 获取裁剪后图片的 Uri
                        Uri uri = data.getData();
                        Bitmap bitmap = null;
                        try {
                            // 对于某些返回的 Uri (特别是通过 ContentProvider)，可能需要持久化 URI 权限
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (SecurityException e) {
                            // 有些 Uri 不支持持久化权限或不需要，忽略这个异常
                            Log.w("UserInfoActivity", "takePersistableUriPermission failed for " + uri, e);
                        }
                        try {
                            // 从 Uri 中打开输入流并解码成 Bitmap
                            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                            if (inputStream != null) {
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                inputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "读取裁剪后的图片失败", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (bitmap != null) {
                            head = bitmap; // 更新 head 变量
                            String fileName = setPicToView(head); // 保存 Bitmap
                            if (fileName != null) {
                                DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("head", fileName, spUserName);
                                iv_photo.setImageBitmap(head);
                                Intent intent = new Intent(UpdateUserInfoReceiver.ACTION.UPDATE_USERINFO);
                                intent.putExtra(UpdateUserInfoReceiver.INTENT_TYPE.TYPE_NAME, UpdateUserInfoReceiver.INTENT_TYPE.UPDATE_HEAD);
                                intent.putExtra("head", fileName);
                                sendBroadcast(intent);
                            } else {
                                Toast.makeText(this, "保存头像失败", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "裁剪后的图片数据为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "裁剪后的图片数据为空", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "裁剪操作未返回数据", Toast.LENGTH_SHORT).show();
                }
                break;
            case CHANGE_NICKNAME: // 修改昵称返回
                if (data != null) {
                    new_info = data.getStringExtra("nickName"); // 获取返回的新昵称
                    if (TextUtils.isEmpty(new_info)) return; // 如果为空则不处理
                    tv_nickName.setText(new_info); // 更新 UI
                    // 更新数据库中的昵称
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("nickName", new_info, spUserName);
                }
                break;
            case CHANGE_SIGNATURE: // 修改签名返回
                if (data != null) {
                    new_info = data.getStringExtra("signature"); // 获取返回的新签名
                    if (TextUtils.isEmpty(new_info)) return; // 如果为空则不处理
                    tv_signature.setText(new_info); // 更新 UI
                    // 更新数据库中的签名
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("signature", new_info, spUserName);
                }
                break;
        }
    }

    /**
     * 调用系统裁剪功能来裁剪图片。
     * @param uri 要裁剪的图片的 Uri。
     */
    public void cropPhoto(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "图片 Uri 为空，无法裁剪", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP"); // 指定裁剪动作
        // 设置数据源 (Uri) 和类型 (MIME type)
        // 同时授予读取该 Uri 的权限给将要处理此 Intent 的裁剪应用
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 授予读取URI的权限

        // 设置裁剪参数
        intent.putExtra("crop", "true");        // 表示需要裁剪
        intent.putExtra("aspectX", 1);          // 裁剪框的 X 方向的比例
        intent.putExtra("aspectY", 1);          // 裁剪框的 Y 方向的比例 (1:1 表示正方形)
        intent.putExtra("outputX", 150);        // 输出图片的 X 方向的像素大小
        intent.putExtra("outputY", 150);        // 输出图片的 Y 方向的像素大小
        intent.putExtra("return-data", true);   // 将裁剪后的数据以 Bitmap 的形式返回在 Intent 的 extras 中
        // 注意：对于大图，这可能导致 TransactionTooLargeException，
        // 更稳妥的方式是指定一个输出 Uri 让裁剪应用将结果写入文件。
        // 但此处结合 onActivityResult 中的处理，也尝试了从 Uri 获取。

        // 启动裁剪 Activity 并期望返回结果，请求码为 SAVE_PHOTO
        startActivityForResult(intent, SAVE_PHOTO);
    }

    /**
     * 将 Bitmap 保存到应用的外部文件目录，并返回文件的绝对路径。
     * @param mBitmap 要保存的 Bitmap 对象。
     * @return 保存成功则返回文件的绝对路径，否则返回 null。
     */
    public String setPicToView(Bitmap mBitmap) {
        if (mBitmap == null) return null;

        // 获取应用的外部文件目录下的 "myHead" 子目录
        File dir = getExternalFilesDir("myHead");
        if (dir == null) { // 如果外部存储不可用或无法获取目录
            Log.e("setPicToView", "外部存储不可用或无法获取 myHead 目录");
            Toast.makeText(this, "存储不可用，无法保存头像", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (!dir.exists()) { // 如果目录不存在
            if (!dir.mkdirs()) { //尝试创建目录
                Log.e("setPicToView", "无法创建 myHead 目录");
                Toast.makeText(this, "无法创建头像目录", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        // 定义头像文件的完整路径和名称
        String fileName = new File(dir, spUserName + "_head.jpg").getAbsolutePath();
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(fileName); // 创建文件输出流
            // 将 Bitmap 以 JPEG 格式压缩并写入到输出流，质量为 100 (最高)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setPicToView", "保存图片失败: " + e.getMessage());
            Toast.makeText(this, "保存图片时出错", Toast.LENGTH_SHORT).show();
            return null; // 保存失败返回 null
        } finally {
            try {
                if (b != null) {
                    b.flush(); // 刷新此输出流并强制写出所有缓冲的输出字节
                    b.close(); // 关闭此文件输出流并释放与此流有关的所有系统资源
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("setPicToView", "关闭文件流失败: " + e.getMessage());
            }
        }
        return fileName; // 保存成功返回文件名
    }
}