package com.zhengwenhao.topline104022021037.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhengwenhao.topline104022021037.sqlite.SQLiteHelper;

/**
 * SQLite 数据库工具类（单例封装）
 */
public class DBUtils {

    private static DBUtils instance = null;
    private static SQLiteHelper helper;
    private static SQLiteDatabase db;

    /**
     * 构造方法 - 初始化 SQLiteHelper 和数据库
     */
    public DBUtils(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase(); // 获取可写数据库对象
    }

    /**
     * 获取 DBUtils 单例对象
     */
    public static DBUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }

    /**
     * 根据登录名获取用户头像
     */
    public String getUserHead(String userName) {
        // 构造SQL语句：从用户表中查询指定用户名对应的头像路径
        String sql = "SELECT head FROM " + SQLiteHelper.U_USERINFO + " WHERE userName=?";

        // 执行查询语句，使用用户名作为参数
        Cursor cursor = db.rawQuery(sql, new String[]{userName});

        String head = "";
        // 如果有查询结果，提取头像字段
        while (cursor.moveToNext()) {
            head = cursor.getString(cursor.getColumnIndexOrThrow("head"));
        }

        // 关闭游标
        cursor.close();
        return head;
    }

    // 如需补充：CRUD 操作方法（insert/update/query/delete），可继续添加
}
