package sochat.so.com.android.live.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.activity.LiveRoomActivity;
import sochat.so.com.android.live.base.LiveBaseFragment;
import sochat.so.com.android.live.liveplayer.LivePlayerController;
import sochat.so.com.android.live.liveplayer.NEVideoControlLayout;
import sochat.so.com.android.live.liveplayer.NEVideoView;
import sochat.so.com.android.live.liveplayer.PlayerContract;
import sochat.so.com.android.live.liveplayer.VideoConstant;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;


/**
 * Created by zhukkun on 1/5/17.
 * 直播观众拉流Fragment
 */
public class AudienceFragment extends LiveBaseFragment implements PlayerContract.PlayerUi{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_audience, container, false);
    }

    public static final String EXTRA_URL = "extra_url";
    public static final String IS_LIVE = "is_live";
    public static final String IS_SOFT_DECODE = "is_soft_decode";
    public static final String IS_ATTENTION = "is_attention";
    public static final String SCHOOL_ID = "school_id";

    private String mUrl; // 拉流地址

    private LiveRoomActivity liveActivity;

    /**
     * 视频展示SurfaceView
     */
    NEVideoView mVideoView;

    /**
     * 顶部View
     */
    View mBackView;

    /**
     * 缓冲View
     */
    View mLoadingView;

    /**
     * 纯音频动画
     */
    View audioAnimate;

    /**
     * 直播状态控制View
     */
    NEVideoControlLayout controlLayout;

    /**
     * 直播控制器
     */
    LivePlayerController mediaPlayController;


    private ImageView live_video_btn;

private int foll;
private String school_id;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    live_video_btn.setImageResource(R.drawable.jiaguanzhuhuang);
                    break;
                case 1:
                    live_video_btn.setImageResource(R.drawable.zhiboyiguanzhu);
                    break;
            }
        }
    };

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initAudienceParam();
    }


    private void initView() {
        bindView();
        clickView();
    }

    private void bindView() {
        mVideoView = (NEVideoView) findViewById(R.id.video_view);
        mLoadingView = findViewById(R.id.buffering_prompt);
        mBackView = findViewById(R.id.player_exit);
        audioAnimate = findViewById(R.id.layout_audio_animate);
        controlLayout = new NEVideoControlLayout(getActivity());

        live_video_btn = (ImageView) findViewById(R.id.live_video_btn);
    }

    private void clickView() {
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        controlLayout.setChangeVisibleListener(new NEVideoControlLayout.ChangeVisibleListener() {
            @Override
            public void onShown() {
                mVideoView.invalidate();
            }

            @Override
            public void onHidden() {

            }
        });

        live_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true){
                    attentionCreator();
                }else{
                    cancelAttentionCreator();
                }

            }
        });
    }

    private void attentionCreator() {
        final String url = ConfigInfo.ApiUrl+"index.php/Api/SchoolManger/add_attention?user_id="+ DemoHelper.getUid()+"&school_id="+school_id;
        HttpUtils.doGetAsyn(getActivity(),false,url, null, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                handler.sendEmptyMessage(1);
            }
        });

    }
    private void cancelAttentionCreator() {
        final String url = ConfigInfo.ApiUrl+"index.php/Api/SchoolManger/cancel_attention?user_id="+ DemoHelper.getUid()+"&school_id="+school_id;
        HttpUtils.doGetAsyn(getActivity(),false,url, null, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
            handler.sendEmptyMessage(0);

            }
        });

    }

    public void attachBottomBarToFragment(final LiveBottomBar liveBottomBar){
        liveBottomBar.setMsgClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                liveActivity.showInputPanel();
            }
        });

        liveBottomBar.setCaptureClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaPlayController.getSnapshot();
            }
        });

    }

    private void initAudienceParam() {
        Intent intent = getActivity().getIntent();
        mUrl = intent.getStringExtra(EXTRA_URL);
        boolean isLive = intent.getBooleanExtra(IS_LIVE, true);
        boolean isSoftDecode = intent.getBooleanExtra(IS_SOFT_DECODE, true);
        mediaPlayController = new LivePlayerController(getActivity(), this, mVideoView, null, mUrl, VideoConstant.VIDEO_SCALING_MODE_FILL_SCALE, isLive, !isSoftDecode);
        mediaPlayController.initVideo();
        foll = intent.getIntExtra(IS_ATTENTION,0);
        school_id = intent.getStringExtra(SCHOOL_ID);

        if (foll==0){
            live_video_btn.setImageResource(R.drawable.jiaguanzhuhuang);
        }else{
            live_video_btn.setImageResource(R.drawable.zhiboyiguanzhu);
        }
    }

    @Override
    public void onResume() {
        // 恢复播放
        if(mediaPlayController != null){
            mediaPlayController.onActivityResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        //暂停播放
        if(mediaPlayController != null){
            mediaPlayController.onActivityPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // 释放资源
        if(mediaPlayController != null){
            mediaPlayController.onActivityDestroy();
        }
        super.onDestroy();
    }


    /**
     *  显示视频推流结束
     */
    @Override
    public boolean onCompletion() {
        //由于设计了客户端重连机制,故认为源站发送的直播结束信号不可靠.转由云信SDK聊天室的直播状态来判断直播是否结束
        //此处收到直播完成时,进行重启直播处理
        showLoading(true);
        mediaPlayController.restart();
        return true;
    }

    @Override
    public boolean onError(final String errorInfo) {
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    liveActivity.onChatRoomFinished(errorInfo);
                }
            });
        }
        return true;
    }

    @Override
    public void setFileName(String name) {
        if(name!=null) {

        }
    }

    @Override
    public void showLoading(final boolean show) {
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (show) {
                        mLoadingView.setVisibility(View.VISIBLE);
                    } else {
                        mLoadingView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void showAudioAnimate(final boolean show) {
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(audioAnimate!=null){
                        if(show) {
                            audioAnimate.setVisibility(View.VISIBLE);
                        }else{
                            audioAnimate.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onBufferingUpdate() {
    }

    @Override
    public void onSeekComplete() {
    }

    public void stopWatching() {
        mediaPlayController.stopPlayback();
        showLoading(false);
    }
}
