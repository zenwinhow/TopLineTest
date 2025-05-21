package com.zhengwenhao.topline104022021037.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite 数据库帮助类，用于创建和管理用户信息表
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1; // 数据库版本号
    public static final String DB_NAME = "topline.db"; // 数据库名称
    public static final String U_USERINFO = "userinfo"; // 用户信息表名

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 创建用户信息表
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS " + U_USERINFO + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "     // 主键，自增
                + "userName VARCHAR, "                          // 用户名
                + "nickName VARCHAR, "                          // 昵称
                + "sex VARCHAR, "                               // 性别
                + "signature VARCHAR, "                         // 签名
                + "head VARCHAR"                                // 头像
                + ")");
    }

    /**
     * 当数据库版本号发生变化时会自动调用此方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + U_USERINFO); // 删除旧表
        onCreate(db); // 重新创建表
    }
}
