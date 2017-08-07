package sochat.so.com.android.live.liveplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.netease.neliveplayer.NELivePlayer;
import com.netease.neliveplayer.NEMediaInfo;
import com.netease.neliveplayer.NEMediaPlayer;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.live.utils.NetworkChangeBroadcastReceiver;
import sochat.so.com.android.live.utils.NetworkUtils;
import sochat.so.com.android.live.utils.VcloudFileUtils;


/**
 * Created by zhukkun on 3/23/17.
 * 点播视频播放控制器
 */
public class VideoPlayerController implements PlayerContract.MediaPlayControllerBase {
    public static final String TAG = VideoPlayerController.class.getSimpleName();
    public static final int _10S = 10000;

    public static final String TYPE_LIVE_STREAM = "livestream";
    public static final String TYPE_LOCAL_AUDIO = "localaudio";
    public static final String TYPE_VIDEO_ON_DEMAND = "videoondemand";

    //states refer to MediaPlayer
    private static final int IDLE = 0;
    private static final int INITIALIZED = 1;
    private static final int PREPARING = 2;
    private static final int PREPARED = 3;
    private static final int STARTED = 4;
    private static final int PAUSED = 5;
    private static final int STOPED = 6;
    private static final int PLAYBACKCOMPLETED = 7;
    private static final int END = 8;
    private static final int RESUME = 9;
    private static final int ERROR = -1;

    /**
     * sdk controller
     */
    private NELivePlayer mMediaPlayer = null;

    /**
     * views
     */
    private Activity mContext;
    private PlayerContract.PlayerUi mUi;
    private NEVideoView mVideoView;
    private NEVideoControlLayout mMediaControlLayout;
    private SurfaceHolder mSurfaceHolder;

    /**
     * params
     */
    private String mMediaType = TYPE_LIVE_STREAM;
    private String mVideoPath;
    private int mBufferStrategy = 0; //直播低延时
    private Uri mUri;
    private int mVideoScalingMode = VideoConstant.VIDEO_SCALING_MODE_FIT;

    /**
     * time
     */
    private long mDuration = 0;
    private long mPlayableDuration = 0;
    private long mSeekWhenPrepared;
    private int mCurrentBufferPercentage;

    /**
     * state
     */
    private boolean mIsPrepared;
    private boolean isHwDecoder = false; //是否硬件解码
    private boolean mPauseInBackground = true; //进入后台时是否暂停
    private boolean isBackground; //是否在后台
    private boolean manualPause = false;
    private int mCurrState = IDLE;
    private int mNextState = IDLE;
    private boolean hasVideo = false;
    private boolean hasAudio = false;

    /**
     * log
     */
    private String mLogPath;
    private int mLogLevel = 0;

    /**
     * listener
     */
    private SdkStateListener sdkStateListener = new SdkStateListener();

    private Handler mHandler;

    /**
     * 网络状态广播
     */

    boolean needResumeWhenNetworkConnect;
    boolean canUse4GNetwork;
    boolean isStartCountDown = false; //是否开启了等待网络恢复倒计时
    AlertDialog mobileAlertDialog;

    NetworkChangeBroadcastReceiver receiver = new NetworkChangeBroadcastReceiver(new NetworkChangeBroadcastReceiver.NetworkChangeCallBack() {

        @Override
        public void onNetworkChanged(boolean connected, int type) {
            if (type == NetworkUtils.TYPE_MOBILE) {
                mSeekWhenPrepared = getCurrentPosition();
                pause();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("正在使用手机流量,是否继续?");
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                canUse4GNetwork = true;
                                start();
                            }
                        });
                builder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.finish();
                            }
                        });
                builder.setCancelable(false);
                if (mobileAlertDialog == null || !mobileAlertDialog.isShowing()) {
                    mobileAlertDialog = builder.show();
                }
            } else if (!connected) {
                mSeekWhenPrepared = getCurrentPosition();
            }
        }
    });

    private void openVideoIfAllowed() {
        isStartCountDown = false;
        if (!canUse4GNetwork && NetworkUtils.getNetworkType() == NetworkUtils.TYPE_MOBILE) {
            needResumeWhenNetworkConnect = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("正在使用手机流量,是否继续?");
            builder.setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canUse4GNetwork = true;
                            reOpenVideo();
                        }
                    });
            builder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //mUi.onError(isLiveStream()?"直播结束":"播放结束");
                            //needResumeWhenNetworkConnect = false;
                            mContext.finish();
                        }
                    });
            builder.setCancelable(false);

            if (mobileAlertDialog == null || !mobileAlertDialog.isShowing()) {
                mobileAlertDialog = builder.show();
            }

        } else {
            Log.d(TAG, "reOpenVideo");
            reOpenVideo();
            if (mobileAlertDialog != null && mobileAlertDialog.isShowing()) {
                mobileAlertDialog.dismiss();
                Toast.makeText(mContext, "恢复至WIFI网络", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reOpenVideo() {
        needResumeWhenNetworkConnect = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "on network validate");
                openVideo();
                mUi.showLoading(true);
            }
        });
    }

    /**
     * 处理surfaceView生命周期
     * 在此处控制进入后台时的数据存储
     */
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Log.d(TAG, "onSurfaceChanged");
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
            }
            mVideoView.onSurfaceChanged(w, h);
            if (!isHwDecoder && mPauseInBackground && mIsPrepared && mVideoView.isSizeNormal()) {
                if (isManualPaused()) {
                    pause();
                } else {
                    start();
                }
            }
            if (mMediaControlLayout != null) {
                mMediaControlLayout.show();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "onSurfaceCreated");
            mSurfaceHolder = holder;

            if (mNextState == RESUME || isBackground) {
                if (isHwDecoder) {
                    openVideo();
                    mUi.showLoading(true);
                    isBackground = false; //不在后台
                } else if (mPauseInBackground) {
                    isBackground = false; //不在后台
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "onSurfaceDestroyed");
            mSurfaceHolder = null;
            if (mMediaControlLayout != null) mMediaControlLayout.hide();
            if (mMediaPlayer != null) {
                if (isHwDecoder) {
                    mSeekWhenPrepared = getCurrentPosition();
                    if (mMediaPlayer != null) {
                        mMediaPlayer.setDisplay(null);
                        mMediaPlayer.reset();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                        mCurrState = IDLE;
                    }
                    isBackground = true;
                } else if (!mPauseInBackground) {
                    mMediaPlayer.setDisplay(null);
                    isBackground = true;
                } else {
                    pause();
                    isBackground = true;
                }
                mNextState = RESUME;
            }
        }
    };

    /**
     * 底层状态监听器
     */
    private class SdkStateListener implements NELivePlayer.OnVideoSizeChangedListener, NELivePlayer.OnPreparedListener, NELivePlayer.OnCompletionListener, NELivePlayer.OnErrorListener,
            NELivePlayer.OnBufferingUpdateListener, NELivePlayer.OnInfoListener, NELivePlayer.OnSeekCompleteListener, NELivePlayer.OnVideoParseErrorListener {

        public void onVideoSizeChanged(NELivePlayer mp, int width, int height,
                                       int sarNum, int sarDen) {
            Log.d(TAG, "onVideoSizeChanged: " + width + "x" + height + "sarNum:" + sarNum + "sarDen:" + sarDen);
            mVideoView.upDateVideoSize(mp.getVideoWidth(), mp.getVideoHeight(), sarNum, sarDen);
            if (mp.getVideoWidth() != 0 && mp.getVideoHeight() != 0)
                setVideoScalingMode(mVideoScalingMode);
        }

        public void onPrepared(NELivePlayer mp) {
            Log.d(TAG, "onPrepared");
            mCurrState = PREPARED;
            mNextState = STARTED;
            // briefly show the controlLayout
            mIsPrepared = true;

            if (isHwDecoder) {
                mUi.showLoading(false);
            }

            if (mMediaControlLayout != null) {
                mMediaControlLayout.setEnabled(true);
            }

            mVideoView.upDateVideoSize(mp.getVideoWidth(), mp.getVideoHeight(), 0, 0);

            if (mMediaPlayer != null && mVideoView.isSizeNormal()) {

                if (mSeekWhenPrepared != 0 && !isLiveStream()) {
                    seekTo(mSeekWhenPrepared);
                }
                if (!isManualPaused()) {
                    start();
                } else {
                    pause();
                }
                if (mMediaControlLayout != null) {
                    mMediaControlLayout.show();
                }
            }
            mVideoView.setVisibility(View.VISIBLE);

        }

        public void onCompletion(NELivePlayer mp) {
            LogUtil.d(TAG, "onCompletion");
            mCurrState = PLAYBACKCOMPLETED;
            if (mMediaControlLayout != null)
                mMediaControlLayout.hide();

            if (mUi.onCompletion()) {
                return;
            }

            if (mVideoView.getWindowToken() != null && mMediaType.equals(TYPE_LIVE_STREAM)) {
                // 适配Android6.0
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }

                builder.setTitle("Completed!")
                        .setMessage("播放结束！")
                        .setPositiveButton("OK", null)
                        .setCancelable(false)
                        .show();
            }
        }

        public boolean onError(NELivePlayer mp, int a, int b) {
            Log.d(TAG, "Error: " + a + "," + b);
            mCurrState = ERROR;
            if (mMediaControlLayout != null) {
                mMediaControlLayout.hide();
            }
            if (needResumeWhenNetworkConnect) {
                //若网络检查器已启动,则由检测器处理异常.
                return true;
            }
            mUi.onError(isLiveStream() ? "直播异常" : "播放异常");
            return true;
        }

        public void onBufferingUpdate(NELivePlayer mp, int percent) {
            mCurrentBufferPercentage = percent;
            mUi.onBufferingUpdate();
        }

        @Override
        public boolean onInfo(NELivePlayer mp, int what, final int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            if (mMediaPlayer != null) {
                Log.d(TAG, "mMediaPlayer != null");
                if (what == NELivePlayer.NELP_BUFFERING_START) {
                    LogUtil.d(TAG, "onInfo: NELP_BUFFERING_START");
                    mUi.showLoading(true);
                } else if (what == NELivePlayer.NELP_BUFFERING_END) {
                    LogUtil.d(TAG, "onInfo: NELP_BUFFERING_END");
                    mUi.showLoading(false);
                    DialogMaker.dismissProgressDialog();
                } else if (what == NELivePlayer.NELP_FIRST_VIDEO_RENDERED) {
                    LogUtil.d(TAG, "onInfo: NELP_FIRST_VIDEO_RENDERED");
                    hasVideo = true;
                    mUi.showLoading(false);
                    mUi.showAudioAnimate(false);
                    DialogMaker.dismissProgressDialog();
                } else if (what == NELivePlayer.NELP_FIRST_AUDIO_RENDERED) {
                    LogUtil.d(TAG, "onInfo: NELP_FIRST_AUDIO_RENDERED");
                    hasAudio = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!hasVideo) {
                                mUi.showLoading(false);
                                mUi.showAudioAnimate(true);
                            }
                        }
                    }, 500);

                }
            }
            return true;
        }

        @Override
        public void onSeekComplete(NELivePlayer mp) {
            Log.d(TAG, "onSeekComplete");
            mUi.onSeekComplete();
        }

        @Override
        public void onVideoParseError(NELivePlayer neLivePlayer) {
            Log.i(TAG, "onVideoParseError");
        }
    }

    public VideoPlayerController(Activity context, PlayerContract.PlayerUi ui, NEVideoView videoView, NEVideoControlLayout controlLayout, String videoPath, int videoScaleMode, boolean isLive, boolean isHwDecoder) {
        this.mContext = context;
        this.mHandler = new Handler(mContext.getMainLooper());
        this.mUi = ui;
        this.mVideoView = videoView;
        this.mVideoPath = videoPath;
        this.isHwDecoder = isHwDecoder;
        this.mMediaType = isLive ? TYPE_LIVE_STREAM : TYPE_VIDEO_ON_DEMAND;
        this.mBufferStrategy = isLive ? 0 : 2; //0为直播低延时模式，1为直播流畅模式，2为点播抗抖动模式
        this.isHwDecoder = isHwDecoder;
        if (isHwDecoder) {
            //硬件编码时,进后台只能暂停播放
            mPauseInBackground = true;
        }

        setMediaControlView(controlLayout, videoScaleMode);
        setSurfaceTouchListener();
        videoView.getHolder().addCallback(mSHCallback);
        ui.showLoading(true);

        receiver.registReceiver(mContext);
    }

    /**
     * 处理SurfaceView传来的事件
     */
    private void setSurfaceTouchListener() {
        mVideoView.setEventListener(new NEVideoView.EventListener() {

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (mIsPrepared &&
                        keyCode != KeyEvent.KEYCODE_BACK
                        && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                        && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                        && keyCode != KeyEvent.KEYCODE_MENU
                        && keyCode != KeyEvent.KEYCODE_CALL
                        && keyCode != KeyEvent.KEYCODE_ENDCALL
                        && mMediaPlayer != null
                        && mMediaControlLayout != null) {
                    if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                            || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                            || keyCode == KeyEvent.KEYCODE_SPACE) {
                        if (mMediaPlayer.isPlaying()) {
                            pause();
                            mMediaControlLayout.show();
                        } else {
                            if (!isManualPaused()) {
                                start();
                            }
                            mMediaControlLayout.hide();
                        }
                        return true;
                    } else {
                        toggleMediaControlLayoutVisibility();
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(MotionEvent event) {
                if (mIsPrepared && mMediaPlayer != null && mMediaControlLayout != null) {
                    toggleMediaControlLayoutVisibility();
                }
            }

            @Override
            public void onTrackballEvent(MotionEvent event) {
                if (mIsPrepared && mMediaPlayer != null && mMediaControlLayout != null) {
                    toggleMediaControlLayoutVisibility();
                }
            }
        });
    }

    public void setMediaControlView(NEVideoControlLayout controlLayout, int mVideoScalingMode) {
        if (mMediaControlLayout != null) {
            mMediaControlLayout.hide();
        }
        mMediaControlLayout = controlLayout;
        this.mVideoScalingMode = mVideoScalingMode;
        attachControlLayout();
    }

    private void attachControlLayout() {
        if (mMediaPlayer != null && mMediaControlLayout != null) {
            mMediaControlLayout.setController(this);
            View anchorView = mVideoView.getParent() instanceof View ?
                    (View) mVideoView.getParent() : mVideoView;
            mMediaControlLayout.setAnchorView(anchorView);
            mMediaControlLayout.setEnabled(mIsPrepared);
            mMediaControlLayout.show(0);
        }
    }

    private void toggleMediaControlLayoutVisibility() {
        if (mMediaControlLayout.isShowing()) {
            mMediaControlLayout.hide();
        } else {
            mMediaControlLayout.show();
        }
    }

    @Override
    public void initVideo() {
        isBackground = false; //指示是否在后台
        mUri = Uri.parse(mVideoPath);
        if (mUri != null) {
            List<String> paths = mUri.getPathSegments();
            String name = paths == null || paths.isEmpty() ? "null" : paths
                    .get(paths.size() - 1);
            mUi.setFileName(name);
        }
        mSeekWhenPrepared = 0;
        openVideoIfAllowed();
        //mVideoView.requestLayout();
        //mVideoView.invalidate();
        LogUtil.i(TAG, "open video, path=" + mVideoPath);
    }

    private void openVideo() {

        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openVideo();
                }
            }, 200);
            return;
        }

        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the framework
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release_resource();

        mIsPrepared = false;
        setLogPath();
        NEMediaPlayer neMediaPlayer = new NEMediaPlayer();
        neMediaPlayer.setLogPath(mLogLevel, mLogPath);
        neMediaPlayer.setBufferStrategy(mBufferStrategy);//设置缓冲策略，0为直播低延时，1为点播抗抖动
        neMediaPlayer.setHardwareDecoder(isHwDecoder);//设置是否开启硬件解码，0为软解，1为硬解
        neMediaPlayer.setOnPreparedListener(sdkStateListener);
        neMediaPlayer.setOnVideoSizeChangedListener(sdkStateListener);
        neMediaPlayer.setOnCompletionListener(sdkStateListener);
        neMediaPlayer.setOnErrorListener(sdkStateListener);
        neMediaPlayer.setOnBufferingUpdateListener(sdkStateListener);
        neMediaPlayer.setOnInfoListener(sdkStateListener);
        neMediaPlayer.setOnSeekCompleteListener(sdkStateListener);
        neMediaPlayer.setOnVideoParseErrorListener(sdkStateListener);
        mMediaPlayer = neMediaPlayer;
        attachControlLayout();
        int ret;
        try {
            ret = neMediaPlayer.setDataSource(mUri.toString());
            if (ret < 0) {
                if (isLiveStream()) {
                    mUi.onError("地址非法，请输入网易视频云官方地址!");
                }
                release_resource();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            sdkStateListener.onError(mMediaPlayer, -1, 0);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mCurrState = INITIALIZED;
                    mNextState = PREPARING;
                    mMediaPlayer.setPlaybackTimeout(30000);
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                    mMediaPlayer.setScreenOnWhilePlaying(true);
                    mMediaPlayer.prepareAsync(mContext);
                    mCurrState = PREPARING;
                } catch (Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            sdkStateListener.onError(mMediaPlayer, -1, 0);
                        }
                    });
                }
            }
        }).start();
    }

    public void release_resource() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrState = IDLE;
        }
    }

    @Override
    public boolean switchPauseResume() {
        if (isPlaying()) {
            pause();
            setManualPause(true);
            return true;
        } else {
            start();
            setManualPause(false);
            return false;
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            mCurrState = STARTED;
        }
        mNextState = STARTED;
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrState = PAUSED;
            }
        }
        mNextState = PAUSED;
    }

    public void setMute(boolean mute) {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.setMute(mute);
    }

    @Override
    public void seekTo(long msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public void setVideoScalingMode(int videoScalingMode) {
        mVideoView.setVideoScalingMode(videoScalingMode);
    }

    public void setManualPause(boolean paused) {
        manualPause = paused;
    }

    //设置日志文件路径
    public void setLogPath() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                mLogPath = Environment.getExternalStorageDirectory() + "/log/";
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }

    @Override
    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        return -1;
    }

    public String getVersion() {
        if (mMediaPlayer == null)
            return null;
        return mMediaPlayer.getVersion();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            long currentPosition = mMediaPlayer.getCurrentPosition();
            //硬件解码直播时,当从后台切回前台,由于重启了Player,总时间需要加上上次播放的时长
            if (isHwDecoder && isLiveStream()) {
                currentPosition += mSeekWhenPrepared;
            }
            return (int) currentPosition;
        }
        return 0;
    }

    @SuppressLint("SdCardPath")
    @Override
    public void getSnapshot() {
        if (!supportSnapShot()) return;

        NEMediaInfo mediaInfo = mMediaPlayer.getMediaInfo();
        LogUtil.d(TAG, "VideoDecoderMode = " + mediaInfo.mVideoDecoderMode);
        Log.d(TAG, "MediaPlayerName = " + mediaInfo.mMediaPlayerName);
        Log.d(TAG, "VideoStreamType = " + mediaInfo.mVideoStreamType);
        Log.d(TAG, "AudioDecoderMode = " + mediaInfo.mAudioDecoderMode);
        Log.d(TAG, "AudioStreamType = " + mediaInfo.mAudioStreamType);

        if (mediaInfo == null || mediaInfo.mVideoDecoderMode == null) {
            Toast.makeText(mContext, "暂无视频流，无法截图", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaInfo.mVideoDecoderMode.equals("MediaCodec")) {
            LogUtil.d(TAG, "================= hardware unsupport snapshot ==============");
        } else {
            Bitmap bitmap = Bitmap.createBitmap(mVideoView.getVideoWidth(), mVideoView.getVideoHeight(), Bitmap.Config.ARGB_8888);
            mMediaPlayer.getSnapshot(bitmap);
//            String picName = StorageUtil.getWritePath(
//                    mContext,
//                    "snap_image_" + StringUtil.get36UUID() + ".jpg",
//                    StorageType.TYPE_IMAGE);
            String picName = VcloudFileUtils.getScreenshotFilePath();
            File f = new File(picName);
            try {
                f.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
                if (picName.substring(picName.lastIndexOf(".") + 1, picName.length()).equals("jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                } else if (picName.substring(picName.lastIndexOf(".") + 1, picName.length()).equals("png")) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                }
                fOut.flush();
                fOut.close();
                VcloudFileUtils.sendUpdateBroadcast(picName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(mContext, "截图成功,可在相册中查看", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean supportSnapShot() {
        if (mMediaType.equals(TYPE_LOCAL_AUDIO) || isHwDecoder) {
            mUi.onError(mMediaType.equals(TYPE_LOCAL_AUDIO) ? "音频播放不支持截图！" : "硬件解码不支持截图！");
            return false;
        }
        return true;
    }

    public boolean isManualPaused() {
        return manualPause;
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public boolean isLocalAudio() {
        return mMediaType.equals(TYPE_LOCAL_AUDIO);
    }

    @Override
    public boolean isLiveStream() {
        return mMediaType.equals(TYPE_LIVE_STREAM);
    }

    @Override
    public boolean isHwDecoder() {
        return isHwDecoder;
    }

    public int getPlayableDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mPlayableDuration > 0)
                return (int) mPlayableDuration;
            mPlayableDuration = mMediaPlayer.getPlayableDuration();
            return (int) mPlayableDuration;
        }
        return -1;
    }

    public void setLogLevel(int logLevel) {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.setLogLevel(logLevel);
    }

    public void seekAndChangeUrl(long msec, String path) {
        mUri = Uri.parse(path);
        //mMediaPlayer.stop();
        stopPlayback();
        mSeekWhenPrepared = msec;
        openVideo();
        mVideoView.requestLayout();
        mVideoView.invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrState = END;
            mNextState = END;
        }
    }

    @Override
    public void onActivityResume() {

    }

    @Override
    public void onActivityPause() {
    }

    @Override
    public void onActivityDestroy() {
        release_resource();
        receiver.unregist(mContext);
        DialogMaker.dismissProgressDialog();
    }


}