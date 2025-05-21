package com.zhengwenhao.topline104022021037.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengwenhao.topline104022021037.utils.ParamsUtils;
import com.zhengwenhao.topline104022021037.R;

import java.util.ArrayList;

/**
 * 自定义下拉弹出菜单 PopMenu
 */
public class PopMenu implements AdapterView.OnItemClickListener {

    // 定义点击事件监听接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<String> itemList;
    private Context context;
    private PopupWindow popupWindow;
    private ListView listView;
    private OnItemClickListener listener;
    private int checkedPosition;

    public PopMenu(Context context, int resid, int checkedPosition, int height) {
        this.context = context;
        this.checkedPosition = checkedPosition;
        itemList = new ArrayList<>();

        // 外层容器布局
        RelativeLayout view = new RelativeLayout(context);

        // 创建 ListView 并设置内边距
        listView = new ListView(context);
        listView.setPadding(
                0,
                ParamsUtils.dpToPx(context, 3),
                0,
                ParamsUtils.dpToPx(context, 3)
        );

        // 添加到容器中
        view.addView(listView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // 设置适配器
        listView.setAdapter(new PopAdapter());
        listView.setOnItemClickListener(this);

        // 创建弹窗
        popupWindow = new PopupWindow(
                view,
                context.getResources().getDimensionPixelSize(R.dimen.popmenu_width),
                height
        );
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(178, 0, 0, 0)));
    }

    // 点击事件回调
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            listener.onItemClick(position);
            checkedPosition = position;
            listView.invalidate(); // 刷新视图
        }
        dismiss(); // 自动关闭菜单
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // 添加多个菜单项
    public void addItems(String[] items) {
        for (String s : items) {
            itemList.add(s);
        }
    }

    // 添加单个菜单项
    public void addItem(String item) {
        itemList.add(item);
    }

    // 显示菜单（以 Dropdown 形式）
    public void showAsDropDown(View parent) {
        popupWindow.showAsDropDown(
                parent,
                parent.getWidth() / 2 * -1,
                context.getResources().getDimensionPixelSize(R.dimen.popmenu_yoff)
        );
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.dismiss();
    }

    /**
     * 自定义适配器，用于显示菜单项
     */
    private final class PopAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout layoutView = new RelativeLayout(context);
            TextView textView = new TextView(context);

            textView.setTextSize(13);
            textView.setText(itemList.get(position));
            textView.setTag(position);

            // 设置选中项颜色
            if (checkedPosition == position || itemList.size() == -1) {
                textView.setTextColor(context.getResources().getColor(R.color.rb_text_check));
            } else {
                textView.setTextColor(Color.WHITE);
            }

            // 居中并添加到布局中
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutView.addView(textView, params);

            // 设置最小高度
            layoutView.setMinimumHeight(ParamsUtils.dpToPx(context, 26));

            return layoutView;
        }
    }
}
