package com.zhengwenhao.topline104022021037.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.zhengwenhao.topline104022021037.bean.NewsBean;
import com.zhengwenhao.topline104022021037.bean.PythonBean;
import com.zhengwenhao.topline104022021037.bean.VideoBean;

public class JsonParse {
    private static JsonParse instance;
    private JsonParse() {
    }

    public static JsonParse getInstance() {
        if (instance == null) {
            instance = new JsonParse();
        }

        return instance;
    }

    // Gson 用于将 JSON 数据转换为 Java 对象
    public List<NewsBean> getAdList(String json) {
        // 使用 gson 库解析 JSON 数据
        Gson gson = new Gson();

        // 创建一个 TypeToken 的匿名子类对象，并调用对象的 getType() 方法
        Type listType = new TypeToken<List<NewsBean>>() {}.getType();

        // 把获取到的信息集合存到 adList 中
        List<NewsBean> adList = gson.fromJson(json, listType);

        return adList;
    }

    public List<NewsBean> getNewsList(String json) {
        // 使用 gson 库解析 JSON 数据
        Gson gson = new Gson();

        // 创建一个 TypeToken 的匿名子类对象，并调用对象的 getType() 方法
        Type listType = new TypeToken<List<NewsBean>>() {}.getType();

        // 把获取到的信息集合存到 newsList 中
        List<NewsBean> newsList = gson.fromJson(json, listType);

        return newsList;
    }

    public List<PythonBean> getPythonList(String json) {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<PythonBean>>() {}.getType();

        List<PythonBean> pythonList = gson.fromJson(json, listType);

        return pythonList;
    }

    public List<VideoBean> getVideoList(String json) {
        Gson gson = new Gson();

        Type listType = new TypeToken<List<VideoBean>>() {}.getType();

        List<VideoBean>videoList = gson.fromJson(json, listType);

        return videoList;
    }
}
