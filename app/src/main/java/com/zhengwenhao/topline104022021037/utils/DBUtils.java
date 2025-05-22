package com.zhengwenhao.topline104022021037.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.sqlite.SQLiteHelper;
import com.zhengwenhao.topline104022021037.bean.UserBean;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 保存个人资料信息
     * 保存一个用户对象的信息到数据库
     */
    public void saveUserInfo(UserBean bean) {
        // 创建一个ContentValues对象，用于存储列名与对应的值
        ContentValues cv = new ContentValues();
        cv.put("userName", bean.getUserName());   // 存储用户名
        cv.put("nickName", bean.getNickName());   // 存储昵称
        cv.put("sex", bean.getSex());             // 存储性别
        cv.put("signature", bean.getSignature()); // 存储签名
        db.insert(SQLiteHelper.U_USERINFO, null, cv); // 插入数据到数据库
    }

    /**
     * 获取个人资料信息
     * 根据用户名从数据库读取用户信息
     */
    public UserBean getUserInfo(String userName) {
        // 构建SQL语句，查询指定用户名的用户信息
        String sql = "SELECT * FROM " + SQLiteHelper.U_USERINFO + " WHERE userName=?";
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        UserBean bean = null; // 用于存储返回的用户信息
        // 遍历结果集，实际上只会有一条
        while (cursor.moveToNext()) {
            bean = new UserBean();

            // 使用 getColumnIndexOrThrow 更安全，字段不存在会抛出异常，便于排查问题
            bean.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));     // 读取用户名
            bean.setNickName(cursor.getString(cursor.getColumnIndexOrThrow("nickName")));     // 读取昵称
            bean.setSex(cursor.getString(cursor.getColumnIndexOrThrow("sex")));               // 读取性别
            bean.setSignature(cursor.getString(cursor.getColumnIndexOrThrow("signature")));   // 读取签名
            bean.setHead(cursor.getString(cursor.getColumnIndexOrThrow("head")));             // 读取头像路径
        }

        cursor.close(); // 关闭游标
        return bean;    // 返回用户信息对象
    }

    /**
     * 修改个人资料
     * 根据key修改指定用户的某个字段
     */
    public void updateUserInfo(String key, String value, String userName) {
        ContentValues cv = new ContentValues();
        cv.put(key, value); // 要更新的字段及新值
        // 执行更新操作，WHERE userName=?
        db.update(SQLiteHelper.U_USERINFO, cv, "userName=?", new String[]{userName});
    }

    /**
     * 保存收藏信息
     * <p>
     * 方法功能：将 NewsBean 实例的数据及用户名插入收藏表。
     * 用 ContentValues 封装字段，db.insert() 方法插入到数据库表中。
     * 表名常量 COLLECTION_NEWS_INFO 来自 SQLiteHelper。
     */
    public void saveCollectionNewsInfo(NewsBean bean, String userName) {
        ContentValues cv = new ContentValues();
        cv.put("id", bean.getId());
        cv.put("type", bean.getType());
        cv.put("userName", userName);
        cv.put("newsName", bean.getNewsName());
        cv.put("newsTypeName", bean.getNewsTypeName());
        cv.put("img1", bean.getImg1());
        cv.put("img2", bean.getImg2());
        cv.put("img3", bean.getImg3());
        cv.put("newsUrl", bean.getNewsUrl());
        db.insert(SQLiteHelper.COLLECTION_NEWS_INFO, null, cv);
    }

    /**
     * 获取收藏信息
     */
    public List<NewsBean> getCollectionNewsInfo(String userName) {
        String sql = "SELECT * FROM " + SQLiteHelper.COLLECTION_NEWS_INFO
                + " WHERE userName = ?;";
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        List<NewsBean> newsList = new ArrayList<>();
        NewsBean bean = null;
        while (cursor.moveToNext()) {
            bean = new NewsBean();
            // 填充新闻对象属性
            bean.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            bean.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
            bean.setNewsName(cursor.getString(cursor.getColumnIndexOrThrow("newsName")));
            bean.setNewsTypeName(cursor.getString(cursor.getColumnIndexOrThrow("newsTypeName")));
            bean.setImg1(cursor.getString(cursor.getColumnIndexOrThrow("img1")));
            bean.setImg2(cursor.getString(cursor.getColumnIndexOrThrow("img2")));
            bean.setImg3(cursor.getString(cursor.getColumnIndexOrThrow("img3")));
            bean.setNewsUrl(cursor.getString(cursor.getColumnIndexOrThrow("newsUrl")));
            newsList.add(bean); // 添加到结果列表
        }
        cursor.close();
        return newsList;
    }

    /**
     * 判断一条新闻是否被收藏
     */
    public boolean hasCollectionNewsInfo(int id, int type, String userName) {
        boolean hasNewsInfo = false;
        String sql = "SELECT * FROM " + SQLiteHelper.COLLECTION_NEWS_INFO
                + " WHERE id=? AND type=? AND userName=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id), String.valueOf(type), userName});
        if (cursor.moveToFirst()) {
            hasNewsInfo = true;
        }
        cursor.close();
        return hasNewsInfo;
    }

    /**
     * 删除某一条收藏信息
     */
    public boolean delCollectionNewsInfo(int id, int type, String userName) {
        boolean delSuccess = false;
        if (hasCollectionNewsInfo(id, type, userName)) {
            int row = db.delete(SQLiteHelper.COLLECTION_NEWS_INFO,
                    "id=? AND type=? AND userName=?",
                    new String[]{String.valueOf(id), String.valueOf(type), userName});
            if (row > 0) {
                delSuccess = true;
            }
        }
        return delSuccess;
    }
}
