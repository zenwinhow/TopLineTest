package com.zhengwenhao.topline104022021037;

import android.app.Application;
import android.widget.Toast;

import com.bokecc.sdk.mobile.drm.DRMServer;


/**
 * 应用启动类：用于启动 DRMServer（视频播放用到的解密服务）
 */
public class TopLineApplication extends Application {

    private DRMServer drmServer; // 视频播放时用到的服务
    private int drmServerPort;   // DRMServer使用的端口

    @Override
    public void onCreate() {
        startDRMServer(); // 启动 DRM 服务
        super.onCreate();
    }

    /**
     * 启动 DRMServer
     */
    public void startDRMServer() {
        if (drmServer == null) {
            drmServer = new DRMServer();
            drmServer.setRequestRetryCount(10); // 设置重试次数
        }
        try {
            drmServer.start(); // 启动服务
            setDrmServerPort(drmServer.getPort()); // 保存启动后的端口
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "启动解密服务失败，请检查网络限制情况", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTerminate() {
        if (drmServer != null) {
            drmServer.stop(); // 停止服务
        }
        super.onTerminate();
    }

    // 获取 DRMServer 监听的端口
    public int getDrmServerPort() {
        return drmServerPort;
    }

    // 设置 DRMServer 监听端口
    public void setDrmServerPort(int drmServerPort) {
        this.drmServerPort = drmServerPort;
    }

    // 获取 DRMServer 实例
    public DRMServer getDRMServer() {
        return drmServer;
    }
}
