package com.zhengwenhao.topline104022021037.bean;

import java.util.List;

public class VideoBean {
    private int id;
    private String name;
    private String intro;
    private String img;
    private List<VideoDetailBean>videoDetailList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<VideoDetailBean> getVideoDetailList() {
        return videoDetailList;
    }

    public void setVideoDetailList(List<VideoDetailBean> videoDetailList) {
        this.videoDetailList = videoDetailList;
    }
}
