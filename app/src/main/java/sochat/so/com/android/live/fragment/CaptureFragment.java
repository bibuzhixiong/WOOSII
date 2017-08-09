package sochat.so.com.android.live.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.vcloud.video.effect.VideoEffect;
import com.netease.vcloud.video.render.NeteaseView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.BasePopuWindow;
import sochat.so.com.android.live.activity.LiveRoomActivity;
import sochat.so.com.android.live.base.LiveBaseFragment;
import sochat.so.com.android.live.livestreaming.CapturePreviewContract;
import sochat.so.com.android.live.livestreaming.CapturePreviewController;
import sochat.so.com.android.live.livestreaming.PublishParam;
import sochat.so.com.android.live.utils.NetworkUtils;
import sochat.so.com.android.live.widget.MixAudioLayout;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by zhukkun on 1/5/17.
 * 直播采集推流Fragment
 */
public class CaptureFragment extends LiveBaseFragment implements CapturePreviewContract.CapturePreviewUi {

    @Bind(R.id.iv_switch_camera)
    ImageView ivSwitchCamera;
    @Bind(R.id.iv_openclose_flash)
    ImageView ivOpencloseFlash;
    @Bind(R.id.iv_setting)
    ImageView ivSetting;
    @Bind(R.id.iv_meiyan)
    ImageView ivMeiyan;
    @Bind(R.id.iv_finish)
    ImageView ivFinish;
    @Bind(R.id.tv_room_name)
    EditText etRoomName;
    @Bind(R.id.tv_if_charge)
    TextView tvIfCharge;
    /**
     * Ui 基础控件
     */
    private ImageView btnFlash;
    private ImageView btnCancel;
    private ImageView btnAudio;
    private ImageView btnVideo;
    private ImageView btnCamSwitch;
    private Button btnStartLive;
    private View audioAnimate;
    private SeekBar focusSeekBar;
    private SeekBar filterSeekBar;

    MixAudioLayout mixAudioLayout = null; //伴音控制布局

    boolean canUse4GNetwork = false; //是否能使用4G网络进行直播

    /**
     * 滤镜模式的SurfaceView
     */
//    private NeteaseGLSurfaceView filterSurfaceView;

    /**
     * 普通模式的SurfaceView
     */
    private NeteaseView normalSurfaceView;

    /**
     * 控制器
     */
    CapturePreviewController controller;

    LiveRoomActivity liveActivity;

    LiveBottomBar liveBottomBar;

    private long lastClickTime;


    //顶部的按钮布局
    private RelativeLayout rlEnterLiveTop;
    //底部整体美颜布局
    private RelativeLayout rlBottomLayout;
    //底部的美颜布局
    private LinearLayout rl_lvjing_layout;
    //底部的设置清晰度布局
    private RelativeLayout ll_setting_layout;
    //底部的美颜布局的左边美颜
    private LinearLayout ll_beauty_layout;
    //底部的美颜布局的右边滤镜
    private LinearLayout ll_beauty_filter;
    //底部美颜的美颜功能区
    private LinearLayout ll_layout1;
    //底部美颜的滤镜功能区
    private HorizontalScrollView hs_layout;

    private TextView tvLD;
    private TextView tvSD;
    private TextView tvHD;


    private RadioButton rb_huaijiu;
    private RadioButton rb_ganjing;
    private RadioButton rb_ziran;
    private RadioButton rb_jiankang;
    private RadioButton rb_fugu;
    private RadioButton rb_wenrou;
    private RadioButton rb_meibai;

private PublishParam mPublishParam;
    //用来处理点击事件显示隐藏底部美颜布局
    private View myview;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capture, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPublishParam = (PublishParam)getActivity().getIntent().getSerializableExtra("extra_params");
        initView();
        controller = getCaptureController();
        controller.handleIntent(getActivity().getIntent(),normalSurfaceView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        liveActivity = (LiveRoomActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        liveActivity = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        controller.onPause();
    }

    /**
     * 初始化Ui
     */
    private void initView() {
        bindView();
        clickView();
    }

    /**
     * 绑定Ui控件
     */
    private void bindView() {
        btnFlash = (ImageView) findViewById(R.id.live_flash);
        btnCancel = (ImageView) findViewById(R.id.live_cancel);
        btnAudio = (ImageView) findViewById(R.id.live_audio_btn);
        btnVideo = (ImageView) findViewById(R.id.live_video_btn);
        btnCamSwitch = (ImageView) findViewById(R.id.live_camera_btn);
        focusSeekBar = (SeekBar) findViewById(R.id.live_seekbar_focus);
        filterSeekBar = (SeekBar) findViewById(R.id.live_seekbar_filter);
        btnStartLive = (Button) findViewById(R.id.btn_star_live);
        audioAnimate = findViewById(R.id.layout_audio_animate);
        normalSurfaceView = (NeteaseView) findViewById(R.id.live_normal_view);
//        filterSurfaceView = (NeteaseGLSurfaceView) findViewById(R.id.live_filter_view);

        rlEnterLiveTop = (RelativeLayout) findViewById(R.id.rl_enter_live_top);
        rlBottomLayout = (RelativeLayout) findViewById(R.id.rl_live_beauty_bottom_layout);
        rl_lvjing_layout = (LinearLayout) findViewById(R.id.rl_lvjing_layout);
        ll_setting_layout = (RelativeLayout) findViewById(R.id.ll_setting_layout);
        ll_layout1 = (LinearLayout) findViewById(R.id.ll_layout1);
        ll_beauty_layout = (LinearLayout) findViewById(R.id.ll_beauty_layout);
        ll_beauty_filter = (LinearLayout) findViewById(R.id.ll_beauty_filter);

        tvLD = (TextView) findViewById(R.id.tv_LD);
        tvSD = (TextView) findViewById(R.id.tv_SD);
        tvHD = (TextView) findViewById(R.id.tv_HD);

        hs_layout = (HorizontalScrollView) findViewById(R.id.hs_layout);
        rb_huaijiu = (RadioButton) findViewById(R.id.rb_huaijiu);
        rb_ganjing = (RadioButton) findViewById(R.id.rb_ganjing);
        rb_ziran = (RadioButton) findViewById(R.id.rb_ziran);
        rb_jiankang = (RadioButton) findViewById(R.id.rb_jiankang);
        rb_fugu = (RadioButton) findViewById(R.id.rb_fugu);
        rb_wenrou = (RadioButton) findViewById(R.id.rb_wenrou);
        rb_meibai = (RadioButton) findViewById(R.id.rb_meibai);

        myview = findViewById(R.id.view);
        etRoomName.setText(mPublishParam.roomtitle+"的直播室");
    }

    public void attachBottomBarToFragment(final LiveBottomBar liveBottomBar) {

        this.liveBottomBar = liveBottomBar;

        liveBottomBar.setMusicClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mixAudioLayout == null) {
                    mixAudioLayout = new MixAudioLayout(getContext());
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    liveBottomBar.addView(mixAudioLayout, layoutParams);
                } else {
                    mixAudioLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        liveBottomBar.setMsgClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                liveActivity.showInputPanel();
            }
        });

        liveBottomBar.setCaptureClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                controller.screenShot();
            }
        });
        liveBottomBar.getfilterSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller.setFilterStrength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        liveBottomBar.getfocusSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller.setBeautyLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        liveBottomBar.getrb_huaijiu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_huaijiu.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.fairytale);
            }
        });
        liveBottomBar.getrb_ganjing().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ganjing.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.clean);
            }
        });
        liveBottomBar.getrb_ziran().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ziran.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.nature);
            }
        });
        liveBottomBar.getrb_jiankang().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_jiankang.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.healthy);
            }
        });
        liveBottomBar.getrb_fugu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_fugu.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.pixar);
            }
        });
        liveBottomBar.getrb_wenrou().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_wenrou.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.tender);
            }
        });
        liveBottomBar.getrb_meibai().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_meibai.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.whiten);
            }
        });

        liveBottomBar.gettvLD().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.MEDIUM);
                liveBottomBar.gettvLD().setBackgroundResource(R.drawable.bg_live_setting_corner);
                liveBottomBar.gettvSD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                liveBottomBar.gettvHD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);

            }
        });
        liveBottomBar.gettvSD().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.HIGH);
                liveBottomBar.gettvLD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                liveBottomBar.gettvSD().setBackgroundResource(R.drawable.bg_live_setting_corner);
                liveBottomBar.gettvHD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
            }
        });
        liveBottomBar.gettvHD().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.SUPER);
                liveBottomBar.gettvLD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                liveBottomBar.gettvSD().setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                liveBottomBar.gettvHD().setBackgroundResource(R.drawable.bg_live_setting_corner);
            }
        });

        liveBottomBar.getll_beauty_filter().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveBottomBar.getll_beauty_filter().setBackgroundResource(R.color.bg_live_beauty_filter);
                liveBottomBar.getll_beauty_layout().setBackgroundResource(R.color.transparent);
                liveBottomBar.geths_layout().setVisibility(View.VISIBLE);
                liveBottomBar.getll_layout1().setVisibility(View.GONE);
            }
        });
        liveBottomBar.getll_beauty_layout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveBottomBar.getll_beauty_layout().setBackgroundResource(R.color.bg_live_beauty_filter);
                liveBottomBar.getll_beauty_filter().setBackgroundResource(R.color.transparent);
                liveBottomBar.geths_layout().setVisibility(View.GONE);
                liveBottomBar.getll_layout1().setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 设置Ui点击事件
     */
    private void clickView() {

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.switchFlash();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.switchAudio();
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.switchVideo();
            }
        });

        btnStartLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变直播间名字
                changeRoomTitle();

                if (!NetworkUtils.isNetworkConnected(true)) {
                    showToast("无网络,请检查网络设置后重新直播");
                    return;
                }
                if (!canUse4GNetwork && NetworkUtils.getNetworkType() == NetworkUtils.TYPE_MOBILE) {
                    showConfirmDialog(null, "正在使用手机流量,是否开始直播?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            controller.liveStartStop();
                            controller.canUse4GNetwork(true);
                            canUse4GNetwork = true;
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                } else {
                    controller.liveStartStop();
                }
            }
        });

        btnCamSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > 1000) {
                    controller.switchCam();
                    lastClickTime = currentTime;
                }
            }
        });

        focusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller.setBeautyLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        filterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller.setFilterStrength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ll_beauty_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_beauty_filter.setBackgroundResource(R.color.bg_live_beauty_filter);
                ll_beauty_layout.setBackgroundResource(R.color.transparent);
                hs_layout.setVisibility(View.VISIBLE);
                ll_layout1.setVisibility(View.GONE);
            }
        });
        ll_beauty_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_beauty_layout.setBackgroundResource(R.color.bg_live_beauty_filter);
                ll_beauty_filter.setBackgroundResource(R.color.transparent);
                hs_layout.setVisibility(View.GONE);
                ll_layout1.setVisibility(View.VISIBLE);
            }
        });

        tvLD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.MEDIUM);
                tvLD.setBackgroundResource(R.drawable.bg_live_setting_corner);
                tvSD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                tvHD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);

            }
        });
        tvSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.HIGH);
                tvLD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                tvSD.setBackgroundResource(R.drawable.bg_live_setting_corner);
                tvHD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
            }
        });
        tvHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setCaptureFormat(lsMediaCapture.VideoQuality.MEDIUM);
                tvLD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                tvSD.setBackgroundResource(R.drawable.bg_live_setting_corner_gray);
                tvHD.setBackgroundResource(R.drawable.bg_live_setting_corner);
            }
        });


        rb_huaijiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_huaijiu.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.fairytale);
            }
        });
        rb_ganjing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ganjing.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.clean);
            }
        });
        rb_ziran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ziran.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.nature);
            }
        });
        rb_jiankang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_jiankang.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.healthy);
            }
        });
        rb_fugu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_fugu.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.pixar);
            }
        });
        rb_wenrou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_wenrou.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.tender);
            }
        });
        rb_meibai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_meibai.setChecked(true);
                controller.switchFilterTo(VideoEffect.FilterType.whiten);
            }
        });

        myview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rlBottomLayout!=null){
                    rlBottomLayout.setVisibility(View.GONE);
                }
                myview.setVisibility(View.GONE);
                btnStartLive.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * 设置显示的SurfaceView
     *
     * @param hasFilter 是否带滤镜功能
     */
    @Override
    public void setSurfaceView(boolean hasFilter) {
        if (hasFilter) {
//            filterSurfaceView.setVisibility(View.VISIBLE);
            normalSurfaceView.setVisibility(View.GONE);
        } else {
            normalSurfaceView.setVisibility(View.VISIBLE);
//            filterSurfaceView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置预览画面 大小
     *
     * @param hasFilter 是否有滤镜
     */
    @Override
    public void setPreviewSize(boolean hasFilter, int previewWidth, int previewHeight) {
        if (hasFilter) {
//            filterSurfaceView.setPreviewSize(previewWidth, previewHeight);
        } else {
//            normalSurfaceView.setPreviewSize(previewWidth, previewHeight);
        }
    }

    /**
     * 获取正在显示的SurfaceView
     *
     * @return
     */
    @Override
        public View getDisplaySurfaceView(boolean hasFilter) {
        if (hasFilter) {
            return null;
        } else {
            return normalSurfaceView;
        }
    }

    /**
     * 设置直播开始按钮, 是否可点击
     *
     * @param clickable
     */
    @Override
    public void setStartBtnClickable(boolean clickable) {
        btnStartLive.setClickable(clickable);
    }

    /**
     * 正常开始直播
     */
    @Override
    public void onStartLivingFinished() {
//        btnAudio.setVisibility(View.VISIBLE);

        //隐藏头部布局
        rlEnterLiveTop.setVisibility(View.GONE);
        //显示取消按钮
        btnCancel.setVisibility(View.VISIBLE);
        btnCamSwitch.setVisibility(View.VISIBLE);


        btnStartLive.setVisibility(View.GONE);
        btnStartLive.setText("开始直播");
        if (liveActivity != null) {
            liveActivity.onStartLivingFinished();
        }
        DialogMaker.dismissProgressDialog();
    }

    /**
     * 停止直播完成时回调
     */
    @Override
    public void onStopLivingFinished() {
        //btnRestart.setVisibility(View.GONE);
    }

    /**
     * 设置audio按钮状态
     *
     * @param isPlay 是否正在开启
     */
    @Override
    public void setAudioBtnState(boolean isPlay) {
        if (isPlay) {
            btnAudio.setImageResource(R.drawable.btn_audio_on_n);
        } else {
            btnAudio.setImageResource(R.drawable.btn_audio_off_n);
        }
    }

    /**
     * 设置Video按钮状态
     *
     * @param isPlay 是否正在开启
     */
    @Override
    public void setVideoBtnState(boolean isPlay) {
        if (isPlay) {
            btnVideo.setImageResource(R.drawable.btn_camera_on_n);
        } else {
            btnVideo.setImageResource(R.drawable.btn_camera_off_n);
        }
    }

    @Override
    public void setFilterSeekBarVisible(boolean visible) {
        if (visible) {
            //fixme 如要显示滤镜强度进度条,解除注释
            filterSeekBar.setVisibility(View.VISIBLE);
        } else {
            filterSeekBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void checkInitVisible(PublishParam mPublishParam) {
        if (mPublishParam.openVideo) {
//            btnCamSwitch.setVisibility(View.VISIBLE);
            if (liveBottomBar != null) {
                liveBottomBar.getFilterView().setVisibility(View.VISIBLE);
                liveBottomBar.getCaptureView().setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取Ui对应的controller
     *
     * @return
     */
    private CapturePreviewController getCaptureController() {
        return new CapturePreviewController(getActivity(), this);
    }

    /**
     * 按下返回键
     */
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void showAudioAnimate(boolean b) {
        if (audioAnimate != null) {
            if (b) {
                audioAnimate.setVisibility(View.VISIBLE);
            } else {
                audioAnimate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDisconnect() {
        btnAudio.setVisibility(View.GONE);
        btnStartLive.setVisibility(View.VISIBLE);

        //liveActivity为空,则已关闭直播页面
        if (liveActivity != null) {
            liveActivity.onLiveDisconnect();
            controller.liveStartStop();
        }
    }

    @Override
    public void normalFinish() {
        if (liveActivity != null) {
            liveActivity.normalFinishLive();
        }
    }

    @Override
    public void onStartInit() {
        btnStartLive.setText("准备中...");
    }

    @Override
    public void onCameraPermissionError() {
        showConfirmDialog("无法打开相机", "可能没有相关的权限,请开启权限后重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }, null);
    }

    @Override
    public void onAudioPermissionError() {
        showConfirmDialog("无法开启录音", "可能没有相关的权限,请开启权限后重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        }, null);
    }

    @Override
    public void setCameraFocus() {
        controller.setCameraFocus();
    }

    public void destroyController() {
        controller.tryToStopLivingStreaming();
        controller.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.iv_switch_camera, R.id.iv_openclose_flash, R.id.iv_setting, R.id.iv_meiyan, R.id.iv_finish, R.id.tv_if_charge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_switch_camera:
//                controller.switchVideo();
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > 1000) {
                    controller.switchCam();
                    lastClickTime = currentTime;
                }
                break;
            case R.id.iv_openclose_flash:
                if (controller.isfrontOrBack){
                    controller.switchFlash();
                    ivOpencloseFlash.setImageResource(R.drawable.shanguangdeng);
                }else{
                    ivOpencloseFlash.setImageResource(R.drawable.shanguangdeng_guanbi);
                    MyToast.makeShortToast(getActivity(),"后置摄像头才能打开闪光灯");
                }

                break;
            case R.id.iv_setting:
                myview.setVisibility(View.VISIBLE);
                setBottomLayoutVisibility(true);
                ll_setting_layout.setVisibility(View.VISIBLE);
                rl_lvjing_layout.setVisibility(View.GONE);
                break;
            case R.id.iv_meiyan:
                myview.setVisibility(View.VISIBLE);
                //显示美颜布局
                setBottomLayoutVisibility(true);
                ll_setting_layout.setVisibility(View.GONE);
                rl_lvjing_layout.setVisibility(View.VISIBLE);

                break;
            case R.id.iv_finish:
                onBackPressed();
                break;
            case R.id.tv_if_charge:
                liveRoomPopupWindow = new LiveRoomPopupWindow(getActivity());
//                liveRoomPopupWindow.setAnimationStyle(R.style.anim_popwindow);
//                liveRoomPopupWindow.showAsDropDown(rlEnterLiveTop,0,0);
                liveRoomPopupWindow.showAtLocation(view, Gravity.TOP,0,0);
                break;
        }
    }

    public void setBottomLayoutVisibility(boolean visibility) {
        if (visibility){
            btnStartLive.setVisibility(View.GONE);
            rlBottomLayout.setVisibility(View.VISIBLE);
        }
    }


    private void changeRoomTitle(){
        try {
            String url = ConfigInfo.ApiUrl+"index.php/Api/Wylive/set_video?school_id="+ DemoHelper.getSchool_id()+"&wobi="+wobi+"&needRecord=1&format=1&theme="+ URLEncoder.encode(etRoomName.getText().toString().trim(), "UTF-8");
            HttpUtils.doGetAsyn(getActivity(), false, url, null, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    //更改房间名字,这里直接调用
                    liveActivity.refreshRoomTitle(etRoomName.getText().toString().trim());
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //记录钱的字符串
    private String wobi ="0";
    private LiveRoomPopupWindow liveRoomPopupWindow;

    public class LiveRoomPopupWindow extends BasePopuWindow {
        private Context context;

        public LiveRoomPopupWindow(Context context) {
            super(context);
        }

        @Override
        protected View getRootView(LayoutInflater inflater) {
            View popopview = inflater.inflate(R.layout.popupwindow_liveroom_setprivce,null);

            TextView tvone = (TextView) popopview.findViewById(R.id.tvone);
            TextView tvtwo = (TextView) popopview.findViewById(R.id.tvtwo);
            TextView tvthree = (TextView) popopview.findViewById(R.id.tvthree);
            TextView tvCommit = (TextView) popopview.findViewById(R.id.tvCommit);
            View view1 = popopview.findViewById(R.id.view1);
            View view_top = popopview.findViewById(R.id.view_top);
            View view2 = popopview.findViewById(R.id.view2);
            final EditText etcustom = (EditText) popopview.findViewById(R.id.etcustom);

            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            view_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            //动态设置view_top的高
            Rect rect = new Rect();
            getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);//获取状态栏高度
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(rlEnterLiveTop.getWidth(),rlEnterLiveTop.getHeight()-rect.top);
            Log.e("WWW",rlEnterLiveTop.getWidth()+"---"+rlEnterLiveTop.getHeight()+"--"+rect.top);
          view_top.setLayoutParams(params);
            tvone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wobi = "10";
                    tvIfCharge.setText("10 沃币");
                    dismiss();
                }
            });
            tvtwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wobi = "20";
                    tvIfCharge.setText("20 沃币");
                    dismiss();
                }
            });
            tvthree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    wobi = "30";
                    tvIfCharge.setText("30 沃币");
                    dismiss();
                }
            });
            tvCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etcustom.getText().toString())){
                        wobi = "0";
                        tvIfCharge.setText("免费");
                    }else{
                        wobi = etcustom.getText().toString();
                        tvIfCharge.setText(etcustom.getText().toString()+" 沃币");
                    }

                    dismiss();
                }
            });
            return popopview;
        }

        @Override
        public void dismiss() {

            super.dismiss();
        }
    }


}
