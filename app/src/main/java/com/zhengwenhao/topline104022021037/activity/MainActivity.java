package com.zhengwenhao.topline104022021037.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.adapter.MyFragmentPagerAdapter;
import com.zhengwenhao.topline104022021037.fragment.CountFragment;
import com.zhengwenhao.topline104022021037.fragment.HomeFragment;
import com.zhengwenhao.topline104022021037.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("首页");

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(getResources().getColor(R.color.rdTextColorPress));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_home) {
                    viewPager.setCurrentItem(0, false);
//                    tv_main_title.setText("首页");
//                    rl_title_bar.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rb_count) {
                    viewPager.setCurrentItem(1, false);
//                    tv_main_title.setText("统计");
//                    rl_title_bar.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rb_video) {
                    viewPager.setCurrentItem(2, false);
//                    tv_main_title.setText("视频");
//                    rl_title_bar.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rb_me) {
                    viewPager.setCurrentItem(3, false);
//                    rl_title_bar.setVisibility(View.GONE);
                }
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        HomeFragment homeFragment = new HomeFragment();
        CountFragment countFragment = new CountFragment();
        VideoFragment videoFragment = new VideoFragment();

        List<Fragment>alFragment = new ArrayList<Fragment>();
        alFragment.add(homeFragment);
        alFragment.add(countFragment);
        alFragment.add(videoFragment);

        //ViewPager设置适配器
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), alFragment));
        viewPager.setCurrentItem(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.rb_home);
                        tv_main_title.setText("首页");
                        rl_title_bar.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        radioGroup.check(R.id.rb_count);
                        tv_main_title.setText("统计");
                        rl_title_bar.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        radioGroup.check(R.id.rb_video);
                        tv_main_title.setText("视频");
                        rl_title_bar.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        radioGroup.check(R.id.rb_me);
                        rl_title_bar.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    protected long exitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}