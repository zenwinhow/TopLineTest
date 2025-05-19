package com.zhengwenhao.topline104022021037.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.activity.AndroidCountActivity;
import com.zhengwenhao.topline104022021037.activity.JavaCountActivity;
import com.zhengwenhao.topline104022021037.utils.BuilderManager;

public class CountFragment extends Fragment {
    private BoomMenuButton bmb; // 圆形爆炸菜单控件

    public CountFragment() {
        // Fragment 默认构造函数
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 加载 fragment 布局
        View view = inflater.inflate(R.layout.fragment_count, container, false);

        // 获取 BoomMenuButton 控件
        bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
        assert bmb != null;

        // 设置菜单按钮样式为：文字在内的圆形按钮
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);

        // 设置下方显示的“爆炸碎片”的排布方式：9 个圆点
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_9_1);

        // 设置按钮的排列方式（按钮显示样式）
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_9_1);

        // 添加多个按钮，每个按钮执行 addBuilder() 方法配置
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            addBuilder();
        }

        return view;
    }

    // 添加按钮构造器，每个按钮绑定图标、文字和点击事件
    private void addBuilder() {
        bmb.addBuilder(new TextInsideCircleButton.Builder()
                .normalImageRes(BuilderManager.getImageResource()) // 设置按钮图标
                .normalTextRes(BuilderManager.getTextResource())   // 设置按钮文字
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        switch (index) {
                            case 0:
                                Intent android = new Intent(getActivity(), AndroidCountActivity.class);
                                startActivity(android);
                                break;
                            case 1:
                                Intent java = new Intent(getActivity(), JavaCountActivity.class);
                                startActivity(java);
                                break;
                            // 其他 case 可继续添加
                        }
                    }
                }));
    }
}
