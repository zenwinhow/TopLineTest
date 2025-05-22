package com.zhengwenhao.topline104022021037.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 用户信息更新的广播接收器
 * 用于处理如头像更新等 UI 变化
 */
public class UpdateUserInfoReceiver extends BroadcastReceiver {

    // 广播的 Action 定义
    public interface ACTION {
        String UPDATE_USERINFO = "update_userinfo"; // 触发用户信息更新的 action 字符串
    }

    // 广播中 intent 携带的键名定义
    public interface INTENT_TYPE {
        String TYPE_NAME = "intent_name"; // intent 类型标记
        String UPDATE_HEAD = "update_head"; // 更新头像的键名
    }

    private BaseOnReceiveMsgListener onReceiveMsgListener;

    /**
     * 构造方法，注入回调监听器
     * @param onReceiveMsgListener 接收到广播后的处理接口
     */
    public UpdateUserInfoReceiver(BaseOnReceiveMsgListener onReceiveMsgListener) {
        this.onReceiveMsgListener = onReceiveMsgListener;
    }

    /**
     * 广播接收回调
     * @param context 上下文
     * @param intent  收到的广播 intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        onReceiveMsgListener.onReceiveMsg(context, intent); // 将广播转发给回调接口处理
    }

    /**
     * 广播回调接口，由使用者实现具体处理逻辑
     */
    public interface BaseOnReceiveMsgListener {
        void onReceiveMsg(Context context, Intent intent); // 接收并处理广播内容
    }
}
