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

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.*;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.*;

public class JavaCountActivity extends AppCompatActivity {
    private TextView tv_main_title, tv_back, tv_intro;
    private RelativeLayout rl_title_bar;
    //private SwipeBackLayout layout;

    // 横轴标签（工作年限）
    public final static String[] years = new String[]{
            "应届生", "1-2 年", "2-3 年", "3-5 年", "5-8 年", "8-10 年", "10 年+"
    };

    private LineChartView chartTop;
    private ColumnChartView chartBottom;
    private LineChartData lineData;
    private ColumnChartData columnData;

    // Y轴刻度值
    private int[] columnY = {
            0, 10000, 20000, 30000, 40000, 50000,
            60000, 70000, 80000, 90000, 100000
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
        //layout.attachToActivity(this);
        setContentView(R.layout.activity_java_count);

        init();
    }

    private void init() {
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("Java统计");

        rl_title_bar = findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));

        tv_intro = findViewById(R.id.tv_intro);
        tv_intro.setText(getResources().getString(R.string.java_count_text));

        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(view -> JavaCountActivity.this.finish());

        chartTop = findViewById(R.id.chart_top);
        chartBottom = findViewById(R.id.chart_bottom);

        generateInitialLineData(); // 折线图初始数据
        generateColumnData();      // 柱状图数据
    }

    private void generateColumnData() {
        int numColumns = years.length;

        List<AxisValue> axisValues = new ArrayList<>();
        List<AxisValue> axisYValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();

        for (int k = 0; k < columnY.length; k++) {
            axisYValues.add(new AxisValue(k).setValue(columnY[k]));
        }

        for (int i = 0; i < numColumns; i++) {
            List<SubcolumnValue> values = new ArrayList<>();
            switch (i) {
                case 0:
                    values.add(new SubcolumnValue((float) 6000, ChartUtils.COLOR_GREEN));
                    break;
                case 1:
                    values.add(new SubcolumnValue((float) 13000, ChartUtils.COLOR_ORANGE));
                    break;
                case 2:
                    values.add(new SubcolumnValue((float) 20000, ChartUtils.COLOR_BLUE));
                    break;
                case 3:
                    values.add(new SubcolumnValue((float) 26000, ChartUtils.COLOR_RED));
                    break;
                case 4:
                    values.add(new SubcolumnValue((float) 35000, ChartUtils.COLOR_VIOLET));
                    break;
                case 5:
                    values.add(new SubcolumnValue((float) 50000, ChartUtils.COLOR_ORANGE));
                    break;
                case 6:
                    values.add(new SubcolumnValue((float) 100000, ChartUtils.COLOR_BLUE));
                    break;
            }

            axisValues.add(new AxisValue(i).setLabel(years[i]));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);
        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis(axisYValues).setHasLines(true).setMaxLabelChars(6));
        chartBottom.setColumnChartData(columnData);

        chartBottom.setValueSelectionEnabled(true); // 允许点击柱状图元素
        chartBottom.setZoomType(ZoomType.HORIZONTAL); // 横向缩放
    }

    private void generateInitialLineData() {
        int numValues = 7;

        List<AxisValue> axisValues = new ArrayList<>();
        List<PointValue> values = new ArrayList<>();

        for (int i = 0; i < numValues; i++) {
            values.add(new PointValue(i, 0)); // 初始值为0
            axisValues.add(new AxisValue(i).setLabel(years[i]));
        }

        Line line = new Line(values).setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(6));

        chartTop.setLineChartData(lineData);
        chartTop.setViewportCalculationEnabled(false);

        Viewport v = new Viewport(0, 100000, 6, 0); // 左0，右6，上10万，下0
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
        chartTop.setZoomType(ZoomType.HORIZONTAL);

        generateLineData(); // 动态设置目标值
    }

    private void generateLineData() {
        Line line = lineData.getLines().get(0);

        for (int i = 0; i < line.getValues().size(); i++) {
            PointValue value = line.getValues().get(i);

            switch (i) {
                case 0:
                    value.setTarget(value.getX(), (float) 6000);
                    break;
                case 1:
                    value.setTarget(value.getX(), (float) 13000);
                    break;
                case 2:
                    value.setTarget(value.getX(), (float) 20000);
                    break;
                case 3:
                    value.setTarget(value.getX(), (float) 26000);
                    break;
                case 4:
                    value.setTarget(value.getX(), (float) 35000);
                    break;
                case 5:
                    value.setTarget(value.getX(), (float) 50000);
                    break;
                case 6:
                    value.setTarget(value.getX(), (float) 100000);
                    break;
            }
        }

        chartTop.startDataAnimation(300); // 动画显示
    }
}
