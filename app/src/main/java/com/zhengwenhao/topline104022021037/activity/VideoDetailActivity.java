package com.zhengwenhao.topline104022021037.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.google.android.material.tabs.TabLayout;
import com.zhengwenhao.topline104022021037.R;
import com.zhengwenhao.topline104022021037.TopLineApplication;
import com.zhengwenhao.topline104022021037.adapter.VideoDetailListAdapter;
import com.zhengwenhao.topline104022021037.adapter.VideoDetailPagerAdapter;
import com.zhengwenhao.topline104022021037.bean.VideoDetailBean;
import com.zhengwenhao.topline104022021037.utils.ParamsUtils;
import com.zhengwenhao.topline104022021037.view.PlayTopPopupWindow;
import com.zhengwenhao.topline104022021037.view.PopMenu;
import com.zhengwenhao.topline104022021037.view.VerticalSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * 视频播放界面
 */
public class VideoDetailActivity extends Activity implements
        DWMediaPlayer.OnBufferingUpdateListener,
        DWMediaPlayer.OnInfoListener,
        DWMediaPlayer.OnPreparedListener, DWMediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback, SensorEventListener, OnCompletionListener {
    private boolean networkConnected = true;
    private DWMediaPlayer player;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ProgressBar bufferProgressBar;
    private SeekBar skbProgress;
    private ImageView backPlayList, ivFullscreen;
    private TextView videoIdText, playDuration, videoDuration, tvDefinition, tv_intro;
    private PopMenu definitionMenu;
    private LinearLayout playerTopLayout, volumeLayout, playerBottomLayout, llBelow;
    private AudioManager audioManager;
    private VerticalSeekBar volumeSeekBar;
    private int currentVolume, maxVolume;
    private boolean isPrepared;
    private Map<String, Integer> definitionMap;
    private Handler playerHandler;
    private Timer timer = new Timer();
    private TimerTask timerTask, networkInfoTimerTask;
    private int currentScreenSizeFlag = 1;
    private int currentDefinitionIndex = 0;
    // 默认设置为普清
    private int defaultDefinition = DWMediaPlayer.NORMAL_DEFINITION;
    private Boolean isPlaying;
    // 当player未准备好，并且当前activity经过onPause()生命周期时，此值为true
    private boolean isFreeze = false;
    private int currentPosition, currentPlayPosition;
    private String[] definitionArray;
    private final String[] screenSizeArray = new String[]{"满屏", "100%", "75%", "50%"};
    private TopLineApplication application;
    private GestureDetector detector;
    private float scrollTotalDistance;
    private RelativeLayout rlPlay;
    private WindowManager wm;
    public final static String API_KEY = "ERNcidRowobGEtL4u3i3zx7s6YS9sk1W"; // 配置API KEY
    public final static String USERID = "ED9742DB71BAEFEF";                    // 配置帐户ID
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>(); //页卡标题集合
    private View view1, view2;                           //页卡视图
    private List<View> mViewList = new ArrayList<>();   //页卡视图集合
    private String intro, videoDetailName, videoId;
    private ListView lv_list;
    private List<VideoDetailBean> videoList;
    private VideoDetailListAdapter videoDetailListAdapter;
    // 隐藏界面的线程
    private Runnable hidePlayRunnable = new Runnable() {
        @Override
        public void run() {
            setLayoutVisibility(View.GONE, false);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题
        application = (TopLineApplication) getApplication();
        application.getDRMServer().reset();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_detail);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        detector = new GestureDetector(this, new MyGesture());
        intro = getIntent().getStringExtra("intro");
        videoList = (List<VideoDetailBean>) getIntent().getSerializableExtra("videoDetailList");
        if (videoList != null) {
            videoId = videoList.get(0).getVideo_id();//刚进此界面时首先播放的是列表中的第一个视频
            videoDetailName = videoList.get(0).getVideo_name();
        }
        initView();
        initViewPager();
        initPlayHander();
        initPlayInfo();
        initNetworkTimerTask();
    }
    private void initNetworkTimerTask() {
        networkInfoTimerTask = new TimerTask() {
            @Override
            public void run() {
                parseNetworkInfo();
            }
        };
        timer.schedule(networkInfoTimerTask, 0, 600);
    }
    enum NetworkStatus {
        WIFI,
        MOBILEWEB,
        NETLESS,
    }
    private NetworkStatus currentNetworkStatus;
    ConnectivityManager cm;
    private void parseNetworkInfo() {
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                if (currentNetworkStatus != null && currentNetworkStatus == NetworkStatus.WIFI) {
                    return;
                } else {
                    currentNetworkStatus = NetworkStatus.WIFI;
                    showWifiToast();
                }
            } else {
                if (currentNetworkStatus != null && currentNetworkStatus == NetworkStatus.MOBILEWEB) {
                    return;
                } else {
                    currentNetworkStatus = NetworkStatus.MOBILEWEB;
                    showMobileDialog();
                }
            }
            startPlayerTimerTask();
            networkConnected = true;
        } else {
            if (currentNetworkStatus != null && currentNetworkStatus == NetworkStatus.NETLESS) {
                return;
            } else {
                currentNetworkStatus = NetworkStatus.NETLESS;
                showNetlessToast();
            }
            if (timerTask != null) {
                timerTask.cancel();
            }
            networkConnected = false;
        }
    }
    private void showWifiToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "已切换至wifi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showMobileDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoDetailActivity.this);
                AlertDialog dialog = builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setPositiveButton("继续", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage("当前为移动网络，是否继续播放？").create();
                dialog.show();
            }
        });
    }
    private void showNetlessToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "当前无网络信号，无法播放", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startPlayerTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!isPrepared) {
                    return;
                }
                playerHandler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
    ImageView lockView;
    ImageView ivCenterPlay;
    ImageView ivTopMenu;
    ImageView ivBackVideo, ivNextVideo, ivPlay;
    private void initView() {
        llBelow = (LinearLayout) findViewById(R.id.ll_below_info);
        rlPlay = (RelativeLayout) findViewById(R.id.rl_play);
        rlPlay.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isPrepared) {
                    return true;
                }
                resetHideDelayed();
                // 事件监听交给手势类来处理
                detector.onTouchEvent(event);
                return true;
            }
        });
        rlPlay.setClickable(true);
        rlPlay.setLongClickable(true);
        rlPlay.setFocusable(true);
        ivTopMenu = (ImageView) findViewById(R.id.iv_top_menu);
        ivTopMenu.setOnClickListener(onClickListener);
        surfaceView = (SurfaceView) findViewById(R.id.playerSurfaceView);
        bufferProgressBar = (ProgressBar) findViewById(R.id.bufferProgressBar);
        ivCenterPlay = (ImageView) findViewById(R.id.iv_center_play);
        ivCenterPlay.setOnClickListener(onClickListener);
        backPlayList = (ImageView) findViewById(R.id.backPlayList);
        videoIdText = (TextView) findViewById(R.id.videoIdText);
        playDuration = (TextView) findViewById(R.id.playDuration);
        videoDuration = (TextView) findViewById(R.id.videoDuration);
        playDuration.setText(ParamsUtils.millsecondsToStr(0));
        videoDuration.setText(ParamsUtils.millsecondsToStr(0));
        ivBackVideo = (ImageView) findViewById(R.id.iv_video_back);
        ivNextVideo = (ImageView) findViewById(R.id.iv_video_next);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivBackVideo.setOnClickListener(onClickListener);
        ivNextVideo.setOnClickListener(onClickListener);
        ivPlay.setOnClickListener(onClickListener);
        tvDefinition = (TextView) findViewById(R.id.tv_definition);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar = (VerticalSeekBar) findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setThumbOffset(2);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        volumeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);
        playerTopLayout = (LinearLayout) findViewById(R.id.playerTopLayout);
        volumeLayout = (LinearLayout) findViewById(R.id.volumeLayout);
        playerBottomLayout = (LinearLayout) findViewById(R.id.playerBottomLayout);
        ivFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
        ivFullscreen.setOnClickListener(onClickListener);
        backPlayList.setOnClickListener(onClickListener);
        tvDefinition.setOnClickListener(onClickListener);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
        surfaceHolder.addCallback(this);
        lockView = (ImageView) findViewById(R.id.iv_lock);
        lockView.setSelected(false);
        lockView.setOnClickListener(onClickListener);
    }
    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(this);
        view1 = mInflater.inflate(R.layout.video_detail_viewpager1, null);
        view2 = mInflater.inflate(R.layout.video_detail_viewpager2, null);
        tv_intro = (TextView) view1.findViewById(R.id.tv_intro);
        tv_intro.setText(intro);
        lv_list = (ListView) view2.findViewById(R.id.lv_list);
        videoDetailListAdapter = new VideoDetailListAdapter(VideoDetailActivity.this, new VideoDetailListAdapter.OnSelectListener() {
            @Override
            public void onSelect(int position, ImageView iv) {
                videoDetailListAdapter.setSelectedPosition(position); //设置适配器的选中项
                VideoDetailBean bean = videoList.get(position);
                String videoId = bean.getVideo_id();
                videoIdText.setText(bean.getVideo_name());
                videoDetailListAdapter.notifyDataSetChanged();          //更新列表框
                if (TextUtils.isEmpty(videoId)) {
                    Toast.makeText(VideoDetailActivity.this,
                            "本地没有此视频，暂无法播放", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    VideoPlay(videoId);
                }
            }
        });
        videoDetailListAdapter.setSelectedPosition(0);//没有点击视频时默认是播放第一个视频的
        videoDetailListAdapter.setData(videoList);
        lv_list.setAdapter(videoDetailListAdapter);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("视频简介");
        mTitleList.add("视频目录");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        VideoDetailPagerAdapter mAdapter = new VideoDetailPagerAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }
    private void VideoPlay(String videoId) {
        isPrepared = false;
        player.pause();
        player.stop();
        bufferProgressBar.setVisibility(View.VISIBLE);
        player.reset();
        player.setDefaultDefinition(defaultDefinition);
        player.setVideoPlayInfo(videoId, USERID, API_KEY, VideoDetailActivity.this);
        player.setDisplay(surfaceHolder);
        application.getDRMServer().reset();
        player.prepareAsync();
    }
    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }
                // 更新播放进度
                currentPlayPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                if (duration > 0) {
                    long pos = skbProgress.getMax() * currentPlayPosition / duration;
                    playDuration.setText(ParamsUtils.millsecondsToStr(player.getCurrentPosition()));
                    skbProgress.setProgress((int) pos);
                }
            }
        };
    }
    private void initPlayInfo() {
        // 通过定时器和Handler来更新进度
        isPrepared = false;
        player = new DWMediaPlayer();
        player.reset();
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.setOnInfoListener(this);
        player.setDRMServerPort(application.getDrmServerPort());
        videoIdText.setText(videoDetailName);
        try {
            player.setVideoPlayInfo(videoId, USERID, API_KEY, this);
            // 设置默认清晰度
            player.setDefaultDefinition(defaultDefinition);
        } catch (IllegalArgumentException e) {
            Log.e("player error", e.getMessage());
        } catch (SecurityException e) {
            Log.e("player error", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("player error", e + "");
        }
    }
    private LayoutParams getScreenSizeParams(int position) {
        currentScreenSizeFlag = position;
        int width = 600;
        int height = 400;
        if (isPortrait()) {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight() * 2 / 5;
        } else {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        }
        String screenSizeStr = screenSizeArray[position];
        if (screenSizeStr.indexOf("%") > 0) {// 按比例缩放
            int vWidth = player.getVideoWidth();
            if (vWidth == 0) {
                vWidth = 600;
            }
            int vHeight = player.getVideoHeight();
            if (vHeight == 0) {
                vHeight = 400;
            }
            if (vWidth > width || vHeight > height) {
                float wRatio = (float) vWidth / (float) width;
                float hRatio = (float) vHeight / (float) height;
                float ratio = Math.max(wRatio, hRatio);
                width = (int) Math.ceil((float) vWidth / ratio);
                height = (int) Math.ceil((float) vHeight / ratio);
            } else {
                float wRatio = (float) width / (float) vWidth;
                float hRatio = (float) height / (float) vHeight;
                float ratio = Math.min(wRatio, hRatio);
                width = (int) Math.ceil((float) vWidth * ratio);
                height = (int) Math.ceil((float) vHeight * ratio);
            }
            int screenSize = ParamsUtils.getInt(screenSizeStr.substring(0, screenSizeStr.indexOf("%")));
            width = (width * screenSize) / 100;
            height = (height * screenSize) / 100;
        }
        LayoutParams params = new LayoutParams(width, height);
        return params;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnBufferingUpdateListener(this);
            player.setOnPreparedListener(this);
            player.setDisplay(holder);
            player.setScreenOnWhilePlaying(true);
            application.getDRMServer().reset();
            player.prepareAsync();
        } catch (Exception e) {
            Log.e("videoPlayer", "error", e);
        }
        Log.i("videoPlayer", "surface created");
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        holder.setFixedSize(width, height);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player == null) {
            return;
        }
        if (isPrepared) {
            currentPosition = player.getCurrentPosition();
        }
        isPrepared = false;
        player.stop();
        player.reset();
    }
    private void initTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isPrepared) {
                    return;
                }
                playerHandler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        initTimerTask();
        isPrepared = true;
        if (!isFreeze) {
            if (isPlaying == null || isPlaying.booleanValue()) {
                player.start();
                ivCenterPlay.setVisibility(View.GONE);
                ivPlay.setImageResource(R.drawable.smallstop_ic);
            }
        }
        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }
        definitionMap = player.getDefinitions();
        initDefinitionPopMenu();
        bufferProgressBar.setVisibility(View.GONE);
        setSurfaceViewLayout();
        videoDuration.setText(ParamsUtils.millsecondsToStr(player.getDuration()));
    }
    // 设置surfaceview的布局
    private void setSurfaceViewLayout() {
        LayoutParams params = getScreenSizeParams(currentScreenSizeFlag);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(params);
    }
    private void initDefinitionPopMenu() {
        if (definitionMap.size() > 1) {
            currentDefinitionIndex = 1;
            Integer[] definitions = new Integer[]{};
            definitions = definitionMap.values().toArray(definitions);
            // 设置默认为普清，所以此处需要判断一下
            for (int i = 0; i < definitions.length; i++) {
                if (definitions[i].intValue() == defaultDefinition) {
                    currentDefinitionIndex = i;
                }
            }
        }
        definitionMenu = new PopMenu(this, R.drawable.popdown, currentDefinitionIndex, getResources().getDimensionPixelSize(R.dimen.popmenu_height));
        // 设置清晰度列表
        definitionArray = new String[]{};
        definitionArray = definitionMap.keySet().toArray(definitionArray);
        definitionMenu.addItems(definitionArray);
        definitionMenu.setOnItemClickListener(new PopMenu.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                try {
                    currentDefinitionIndex = position;
                    defaultDefinition = definitionMap.get(definitionArray[position]);
                    if (isPrepared) {
                        currentPosition = player.getCurrentPosition();
                        if (player.isPlaying()) {
                            isPlaying = true;
                        } else {
                            isPlaying = false;
                        }
                    }
                    isPrepared = false;
                    setLayoutVisibility(View.GONE, false);
                    bufferProgressBar.setVisibility(View.VISIBLE);
                    application.getDRMServer().disconnectCurrentStream();
                    player.reset();
                    application.getDRMServer().reset();
                    player.setDefinition(getApplicationContext(), defaultDefinition);
                } catch (IOException e) {
                    Log.e("player error", e.getMessage());
                }
            }
        });
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        skbProgress.setSecondaryProgress(percent);
    }
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            resetHideDelayed();
            int id = v.getId();

            if (id == R.id.backPlayList) {
                if (isPortrait()) {
                    finish();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            } else if (id == R.id.iv_fullscreen) {
                if (isPortrait()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            } else if (id == R.id.tv_definition) {
                definitionMenu.showAsDropDown(v);

            } else if (id == R.id.iv_lock) {
                if (lockView.isSelected()) {
                    lockView.setSelected(false);
                    setLayoutVisibility(View.VISIBLE, true);
                    toastInfo("已解开屏幕");
                } else {
                    lockView.setSelected(true);
                    setLandScapeRequestOrientation();
                    setLayoutVisibility(View.GONE, true);
                    lockView.setVisibility(View.VISIBLE);
                    toastInfo("已锁定屏幕");
                }

            } else if (id == R.id.iv_center_play || id == R.id.iv_play) {
                changePlayStatus();

            } else if (id == R.id.iv_top_menu) {
                setLayoutVisibility(View.GONE, false);
                showTopPopupWindow();
            }

        }
    };
    // 设置横屏的固定方向，禁用掉重力感应方向
    private void setLandScapeRequestOrientation() {
        int rotation = wm.getDefaultDisplay().getRotation();
        // 旋转90°为横屏正向，旋转270°为横屏逆向
        if (rotation == Surface.ROTATION_90) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (rotation == Surface.ROTATION_270) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }
    PlayTopPopupWindow playTopPopupWindow;
    private void showTopPopupWindow() {
        if (playTopPopupWindow == null) {
            initPlayTopPopupWindow();
        }
        playTopPopupWindow.showAsDropDown(rlPlay);
    }
    private void initPlayTopPopupWindow() {
        playTopPopupWindow = new PlayTopPopupWindow(this, surfaceView.getHeight());
        playTopPopupWindow.setScreenSizeCheckLister(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = 0;
                if (checkedId == R.id.rb_screensize_full) {
                    position = 0;
                } else if (checkedId == R.id.rb_screensize_100) {
                    position = 1;
                } else if (checkedId == R.id.rb_screensize_75) {
                    position = 2;
                } else if (checkedId == R.id.rb_screensize_50) {
                    position = 3;
                }

                Toast.makeText(getApplicationContext(), screenSizeArray[position], Toast.LENGTH_SHORT).show();
                LayoutParams params = getScreenSizeParams(position);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                surfaceView.setLayoutParams(params);
            }
        });
    }
    private void toastInfo(String info) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
    OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
        int progress = 0;
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (networkConnected) {
                player.seekTo(progress);
                playerHandler.postDelayed(hidePlayRunnable, 5000);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            playerHandler.removeCallbacks(hidePlayRunnable);
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (networkConnected) {
                this.progress = progress * player.getDuration() / seekBar.getMax();
            }
        }
    };
    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            currentVolume = progress;
            volumeSeekBar.setProgress(progress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            playerHandler.removeCallbacks(hidePlayRunnable);
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            playerHandler.postDelayed(hidePlayRunnable, 5000);
        }
    };
    // 控制播放器面板显示
    private boolean isDisplay = false;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 监测音量变化
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume != volume) {
                currentVolume = volume;
                volumeSeekBar.setProgress(currentVolume);
            }
            if (isPrepared) {
                setLayoutVisibility(View.VISIBLE, true);
            }
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * @param visibility 显示状态
     * @param isDisplay  是否延迟消失
     */
    private void setLayoutVisibility(int visibility, boolean isDisplay) {
        if (player == null || player.getDuration() <= 0) {
            return;
        }
        playerHandler.removeCallbacks(hidePlayRunnable);
        this.isDisplay = isDisplay;
        if (definitionMenu != null && visibility == View.GONE) {
            definitionMenu.dismiss();
        }
        if (isDisplay) {
            playerHandler.postDelayed(hidePlayRunnable, 5000);
        }
        if (isPortrait()) {
            ivFullscreen.setVisibility(visibility);
            lockView.setVisibility(View.GONE);
            volumeLayout.setVisibility(View.GONE);
            tvDefinition.setVisibility(View.GONE);
            ivTopMenu.setVisibility(View.GONE);
            ivBackVideo.setVisibility(View.GONE);
            ivNextVideo.setVisibility(View.GONE);
        } else {
            ivFullscreen.setVisibility(View.GONE);
            lockView.setVisibility(visibility);
            if (lockView.isSelected()) {
                visibility = View.GONE;
            }
            volumeLayout.setVisibility(visibility);
            tvDefinition.setVisibility(visibility);
            ivTopMenu.setVisibility(visibility);
            ivBackVideo.setVisibility(visibility);
            ivNextVideo.setVisibility(visibility);
        }
        playerTopLayout.setVisibility(visibility);
        playerBottomLayout.setVisibility(visibility);
    }
    private Handler alertHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            toastInfo("视频异常，无法播放。");
            super.handleMessage(msg);
        }

    };
    @Override
    public boolean onError(MediaPlayer mp, final int what, int extra) {
        if (DWMediaPlayer.MEDIA_ERROR_DRM_LOCAL_SERVER == what) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastInfo("server_error");
                }
            });
        }
        Message msg = new Message();
        msg.what = what;
        if (alertHandler != null) {
            alertHandler.sendMessage(msg);
        }
        return false;
    }
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        setSurfaceViewLayout();
    }
    // 重置隐藏界面组件的延迟时间
    private void resetHideDelayed() {
        playerHandler.removeCallbacks(hidePlayRunnable);
        playerHandler.postDelayed(hidePlayRunnable, 5000);
    }
    // 手势监听器类
    private class MyGesture extends SimpleOnGestureListener {
        private Boolean isVideo;
        private float scrollCurrentPosition;
        private float scrollCurrentVolume;
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (lockView.isSelected()) {
                return true;
            }
            if (isVideo == null) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    isVideo = true;
                } else {
                    isVideo = false;
                }
            }
            if (isVideo.booleanValue()) {
                parseVideoScroll(distanceX);
            } else {
                parseAudioScroll(distanceY);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
        private void parseVideoScroll(float distanceX) {
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            scrollTotalDistance += distanceX;
            float duration = (float) player.getDuration();
            float width = wm.getDefaultDisplay().getWidth() * 0.75f; // 设定总长度是多少，此处根据实际调整
            //右滑distanceX为负
            float currentPosition = scrollCurrentPosition - (float) duration * scrollTotalDistance / width;
            if (currentPosition < 0) {
                currentPosition = 0;
            } else if (currentPosition > duration) {
                currentPosition = duration;
            }
            player.seekTo((int) currentPosition);
            playDuration.setText(ParamsUtils.millsecondsToStr((int) currentPosition));
            int pos = (int) (skbProgress.getMax() * currentPosition / duration);
            skbProgress.setProgress(pos);
        }
        private void parseAudioScroll(float distanceY) {
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            scrollTotalDistance += distanceY;
            float height = wm.getDefaultDisplay().getHeight() * 0.75f;
            // 上滑distanceY为正
            currentVolume = (int) (scrollCurrentVolume + maxVolume * scrollTotalDistance / height);
            if (currentVolume < 0) {
                currentVolume = 0;
            } else if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            }
            volumeSeekBar.setProgress(currentVolume);
        }
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }
        @Override
        public boolean onDown(MotionEvent e) {
            scrollTotalDistance = 0f;
            isVideo = null;
            scrollCurrentPosition = (float) player.getCurrentPosition();
            scrollCurrentVolume = currentVolume;
            return super.onDown(e);
        }
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (lockView.isSelected()) {
                return true;
            }
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE, true);
            }
            changePlayStatus();
            return super.onDoubleTap(e);
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isDisplay) {
                setLayoutVisibility(View.GONE, false);
            } else {
                setLayoutVisibility(View.VISIBLE, true);
            }
            return super.onSingleTapConfirmed(e);
        }
    }
    private void changePlayStatus() {
        if (player.isPlaying()) {
            player.pause();
            ivCenterPlay.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.drawable.smallbegin_ic);
        } else {
            player.start();
            ivCenterPlay.setVisibility(View.GONE);
            ivPlay.setImageResource(R.drawable.smallstop_ic);
        }
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (player.isPlaying()) {
                    bufferProgressBar.setVisibility(View.VISIBLE);
                }
                break;
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_END:
                bufferProgressBar.setVisibility(View.GONE);
                break;
        }
        return false;
    }
    // 获得当前屏幕的方向
    private boolean isPortrait() {
        int mOrientation = getApplicationContext().getResources().getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
            return true;
        }
    }
    private int mX, mY, mZ;
    private long lastTimeStamp = 0;
    private Calendar mCalendar;
    private SensorManager sensorManager;
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (!lockView.isSelected() && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis() / 1000l;
            int second = mCalendar.get(Calendar.SECOND);
            int px = Math.abs(mX - x);
            int py = Math.abs(mY - y);
            int pz = Math.abs(mZ - z);
            int maxvalue = getMaxValue(px, py, pz);
            if (maxvalue > 2 && (stamp - lastTimeStamp) > 1) {
                lastTimeStamp = stamp;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }
    /**
     * 获取一个最大值
     */
    private int getMaxValue(int px, int py, int pz) {
        int max = 0;
        if (px > py && px > pz) {
            max = px;
        } else if (py > px && py > pz) {
            max = py;
        } else if (pz > px && pz > py) {
            max = pz;
        }
        return max;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    public void onBackPressed() {
        if (isPortrait()) {
            super.onBackPressed();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    public void onResume() {
        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                player.start();
            }
        } else {
            if (isPlaying != null && isPlaying.booleanValue() && isPrepared) {
                player.start();
            }
        }
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }
    @Override
    public void onPause() {
        if (isPrepared) {
            // 如果播放器prepare完成，则对播放器进行暂停操作，并记录状态
            if (player.isPlaying()) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }
            player.pause();
        } else {
            // 如果播放器没有prepare完成，则设置isFreeze为true
            isFreeze = true;
        }
        super.onPause();
    }
    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        setLandScapeRequestOrientation();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        playerHandler.removeCallbacksAndMessages(null);
        playerHandler = null;
        alertHandler.removeCallbacksAndMessages(null);
        alertHandler = null;
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
        application.getDRMServer().disconnectCurrentStream();
        networkInfoTimerTask.cancel();
        super.onDestroy();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isPrepared) {
            // 刷新界面
            setLayoutVisibility(View.GONE, false);
            setLayoutVisibility(View.VISIBLE, true);
        }
        lockView.setSelected(false);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            llBelow.setVisibility(View.VISIBLE);
            ivFullscreen.setImageResource(R.drawable.fullscreen_close);
            if (playTopPopupWindow != null) {
                playTopPopupWindow.dismiss();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            llBelow.setVisibility(View.GONE);
            ivFullscreen.setImageResource(R.drawable.fullscreen_open);
        }
        setSurfaceViewLayout();
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isPrepared) {
            toastInfo("播放完成，请选择其他视频");
            player.pause();
            ivCenterPlay.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.drawable.smallbegin_ic);
        }
    }
}
