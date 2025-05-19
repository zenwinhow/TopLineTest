package com.zhengwenhao.topline104022021037.bean;

import java.io.Serializable;

public class VideoDetailBean implements Serializable {
    private static final long serialVersionID = 1L;
    private String video_id;
    private String video_name;

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }
}
