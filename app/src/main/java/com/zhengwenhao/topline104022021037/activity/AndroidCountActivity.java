package com.zhengwenhao.topline104022021037.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zhengwenhao.topline104022021037.R;
//import com.zhengwenhao.topline104022021037.view.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class AndroidCountActivity extends AppCompatActivity {

    private TextView tv_main_title, tv_back, tv_intro;
    private RelativeLayout rl_title_bar;
    //private SwipeBackLayout layout;
    private PieChartView chart;
    private PieChartData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 加载基础布局（用于实现滑动返回）
        //layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
        //layout.attachToActivity(this);

        // 设置内容视图为统计页面
        setContentView(R.layout.activity_android_count);

        init(); // 初始化控件和数据
    }

    private void init() {
        // 标题栏主标题
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("Android 统计");

        // 设置标题栏背景
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));

        // 设置返回按钮
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidCountActivity.this.finish(); // 点击返回
            }
        });

        // 设置介绍文本
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_intro.setText(getResources().getString(R.string.android_count_text));

        // 查找饼图控件并加载数据
        chart = (PieChartView) findViewById(R.id.chart);
        generateData(); // 生成数据
        chart.startDataAnimation(); // 动画展示
    }

    // 生成饼状图的数据
    private void generateData() {
        int numValues = 4; // 设置饼图扇形的数量

        List<SliceValue> values = new ArrayList<>();

        for (int i = 0; i < numValues; i++) {
            switch (i + 1) {
                case 1: // 第一个扇形：月薪 8~15k
                    SliceValue sliceValue1 = new SliceValue(i + 1, ChartUtils.COLOR_GREEN);
                    sliceValue1.setTarget(4);
                    sliceValue1.setLabel("月薪 8~15k");
                    values.add(sliceValue1);
                    break;
                case 2: // 第二个扇形：月薪 15~20k
                    SliceValue sliceValue2 = new SliceValue(i + 1, ChartUtils.COLOR_VIOLET);
                    sliceValue2.setTarget(3);
                    sliceValue2.setLabel("月薪 15~20k");
                    values.add(sliceValue2);
                    break;
                case 3: // 第三个扇形：月薪 20~30k
                    SliceValue sliceValue3 = new SliceValue(i + 1, ChartUtils.COLOR_BLUE);
                    sliceValue3.setTarget(2);
                    sliceValue3.setLabel("月薪 20~30k");
                    values.add(sliceValue3);
                    break;
                case 4: // 第四个扇形：月薪 30k+
                    SliceValue sliceValue4 = new SliceValue(i + 1, ChartUtils.COLOR_ORANGE);
                    sliceValue4.setTarget(1);
                    sliceValue4.setLabel("月薪 30k+");
                    values.add(sliceValue4);
                    break;
            }
        }

        data = new PieChartData(values);
        data.setHasLabels(true);                     // 显示标签
        data.setHasLabelsOnlyForSelected(false);     // 所有切片均显示
        data.setHasLabelsOutside(false);             // 标签是否显示在扇形外部
        chart.setPieChartData(data);
    }
}
