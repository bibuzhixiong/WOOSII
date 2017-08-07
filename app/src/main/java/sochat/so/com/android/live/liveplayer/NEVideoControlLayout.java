/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2015 netease
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sochat.so.com.android.live.liveplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Locale;

import sochat.so.com.android.R;


public class NEVideoControlLayout extends FrameLayout {
    private static final String TAG = "NEControlLayout";
    private static final int DEFAULT_TIMEOUT = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private Context mContext;

    private View mAnchor;
    private View mRoot;
    private PopupWindow mWindow;
    private TextView mEndTime, mCurrentTime;
    private ProgressBar mProgress;
    private ImageView mPauseButton;
    private ImageView mSetPlayerScaleButton;
    private ImageView mSnapshotButton;
    private ImageView mMuteButton;

    private long mDuration;
    private int mAnimStyle;

    private boolean isShowing;
    private boolean isDragging;
    private boolean usInstantSeeking = true;
    private boolean isFromXml;
    private boolean isMute;
    private boolean isFullScreen = false;

    private Runnable lastRunnable;

    /**
     * 监听控制栏的显示状态
     */
    ChangeVisibleListener mChangeVisibleListener;

    public interface ChangeVisibleListener{
        void onShown();
        void onHidden();
    }

    private Handler mHandler = new MyHandler(this);

    static class MyHandler extends Handler{

        WeakReference<NEVideoControlLayout> reference;

        public MyHandler(NEVideoControlLayout reference){
            this.reference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            NEVideoControlLayout controlLayout = reference.get();
            long pos;
            switch (msg.what) {
                case FADE_OUT:
                    controlLayout.hide();
                    break;
                case SHOW_PROGRESS:
                    pos =  controlLayout.setProgress();
                    if (!controlLayout.isDragging()  &&  controlLayout.isShowing()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        controlLayout.updatePausePlay();
                    }
                    break;
            }
        }
    }

    /**
     * 控制器
     */
    private PlayerContract.MediaPlayControllerBase controller;

    /**
     * 缩放模式
     */
    private int mVideoScalingMode = VideoConstant.VIDEO_SCALING_MODE_FIT;

    /**
     * xml方式创建
     * @param context
     * @param attrs
     */
    public NEVideoControlLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        isFromXml = true;
        mContext = context;
    }

    /**
     * java方式创建
     * @param context
     */
    public NEVideoControlLayout(Context context) {
        super(context);
        mContext = context;
        initFloatingWindow();
    }

    /**
     * 从XML加载完所有子视图后调用。初始化控制视图
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    /**
     * 初始化浮窗
     */
    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    /**
     * 设置controlLayout 绑定到一个视图上。例如可以是一个VideoView对象，或者是activity的主视图。
     * @param view 可见时绑定的视图
     */
    public void setAnchorView(View view) {
        mAnchor = view;
        if (!isFromXml) {
            removeAllViews();
            mRoot = createControlLayout();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The ControlLayout.
     */
    protected View createControlLayout() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mediacontrol_layout, this);
    }

    /**
     * 设置事件、绑定控件和设置默认值
     * @param v
     */
    private void initControllerView(View v) {
        blindView(v);
        clickView();
        if (mProgress instanceof SeekBar) {
            SeekBar seeker = (SeekBar) mProgress;
            seeker.setOnSeekBarChangeListener(mSeekListener);
            seeker.setThumbOffset(1);
        }
        mProgress.setMax(1000);

        if(controller.isLocalAudio()) {
        	mSetPlayerScaleButton.setVisibility(View.INVISIBLE); //播放音乐不需要设置显示模式，该按钮不显示
        	mSnapshotButton.setVisibility(View.INVISIBLE);       //播放音乐不需要截图，该按钮不显示
        	show();
        }

        if (controller.isHwDecoder()) {
        	mSnapshotButton.setVisibility(View.GONE); //硬件解码不支持截图，该按钮不显示
        }
    }

    /**
     * 绑定UI
     * @param v
     */
    private void blindView(View v) {
        mPauseButton = (ImageView) v.findViewById(R.id.mediacontroller_play_pause); //播放暂停按钮
        mSetPlayerScaleButton = (ImageView) v.findViewById(R.id.video_player_scale);  //画面显示模式按钮
        mSnapshotButton = (ImageView) v.findViewById(R.id.snapShot);  //截图按钮
        mMuteButton = (ImageView) v.findViewById(R.id.video_player_mute);  //静音按钮
        mProgress = (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);  //进度条
        mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total); //总时长
        mCurrentTime = (TextView) v.findViewById(R.id.mediacontroller_time_current); //当前播放位置
    }

    /**
     * 设置事件
     */
    private void clickView() {
        mPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePausePlay(controller.switchPauseResume());
                show(DEFAULT_TIMEOUT);
            }
        });

        mSetPlayerScaleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    mVideoScalingMode = VideoConstant.VIDEO_SCALING_MODE_NONE;
                    mSetPlayerScaleButton.setImageResource(R.drawable.ic_screen_landscape);
                    mCurrentTime.setVisibility(View.GONE);
                    mEndTime.setVisibility(View.GONE);
                    isFullScreen = false;
                    if (((Activity)mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                } else {
                    mVideoScalingMode = VideoConstant.VIDEO_SCALING_MODE_FILL_BLACK;
                    mSetPlayerScaleButton.setImageResource(R.drawable.ic_screen_portrail);
                    mCurrentTime.setVisibility(View.VISIBLE);
                    mEndTime.setVisibility(View.VISIBLE);
                    isFullScreen = true;
                    if (((Activity)mContext).getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }

                try {
                    controller.setVideoScalingMode(mVideoScalingMode);
                } catch (NumberFormatException e) {

                }
                show(DEFAULT_TIMEOUT);
            }
        });

        mSnapshotButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.getSnapshot();
                show(DEFAULT_TIMEOUT);
            }
        });

        mMuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMute) {
                    mMuteButton.setImageResource(R.drawable.nemediacontroller_mute02);
                    isMute = false;
                } else {
                    mMuteButton.setImageResource(R.drawable.nemediacontroller_mute01);
                    isMute = true;
                }
                controller.setMute(isMute);
                show(DEFAULT_TIMEOUT);
            }
        });
    }

    /**
     * 设置控制器
     */
    public void setController(PlayerContract.MediaPlayControllerBase controller) {
        this.controller = controller;
        updatePausePlay();
    }

    /**
     * 显示controlLayout。默认显示3秒后自动隐藏。
     */
    public void show() {
        show(DEFAULT_TIMEOUT);
    }

    /**
     * 显示控制栏, 在timeout事件后消失
     * @param timeout  为0时不自动消失
     */
    @SuppressLint({ "InlinedApi", "NewApi" })
    public void show(int timeout) {
        if (!isShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }

            if (isFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1],
                        location[0] + mAnchor.getWidth(), location[1]
                        + mAnchor.getHeight());

                mWindow.setAnimationStyle(mAnimStyle);
                mWindow.showAtLocation(mAnchor, Gravity.BOTTOM, anchorRect.left, 0);
            }
            isShowing = true;

            if(mChangeVisibleListener != null){
                mChangeVisibleListener.onShown();
            }

        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    @SuppressLint({ "InlinedApi", "NewApi" })
    //隐藏MediaController。
    public void hide() {
        if (mAnchor == null)
            return;
        if (isShowing) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (isFromXml)
                    setVisibility(View.GONE);
                else
                    mWindow.dismiss();
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "MediaController already removed");
            }
            isShowing = false;

            if(mChangeVisibleListener != null){
                mChangeVisibleListener.onHidden();
            }
        }
    }

    public void setChangeVisibleListener(ChangeVisibleListener lis){
        mChangeVisibleListener = lis;
    }

    private long setProgress() {
        if (controller == null || isDragging)
            return 0;

        int position = controller.getCurrentPosition();
        int duration = controller.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = controller.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null && mDuration > 0)
            mEndTime.setText(stringForTime(mDuration));
        else if(mEndTime != null) {
            mEndTime.setText("--:--:--");
        }
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private static String stringForTime(long position) {
        int totalSeconds = (int) ((position / 1000.0)+0.5);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updatePausePlay(){
        if(controller!=null) {
            updatePausePlay(controller.isPlaying());
        }
    }

    private void updatePausePlay(boolean isPlaying) {
        if (mRoot == null || mPauseButton == null)
            return;
        if (isPlaying) {
            mPauseButton.setImageResource(R.drawable.nemediacontroller_play);
        }
        else {
            mPauseButton.setImageResource(R.drawable.nemediacontroller_pause);
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            isDragging = true;

            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (controller.isLiveStream()) {
                return;
            }

            if (!fromuser)
                return;

            final long newposition = (mDuration * progress) / 1000;
            String time = stringForTime(newposition);
            if (usInstantSeeking) {
                mHandler.removeCallbacks(lastRunnable);
                lastRunnable = new Runnable() {
                    @Override
                    public void run() {
                        controller.seekTo(newposition);
                    }
                };
                mHandler.postDelayed(lastRunnable, 200);
            }

            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            if (controller.isLiveStream()) {
                AlertDialog alertDialog;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setTitle("注意");
                alertDialogBuilder.setMessage("直播不支持seek操作");
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                mProgress.setProgress(0);
                //return;
            }
            if (!controller.isLiveStream()) {
                if (!usInstantSeeking)
                    controller.seekTo((mDuration * bar.getProgress()) / 1000);
            }

            show(DEFAULT_TIMEOUT);
            mHandler.removeMessages(SHOW_PROGRESS);
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null) {
            mPauseButton.setEnabled(enabled);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        if (mSetPlayerScaleButton != null) {
            mSetPlayerScaleButton.setEnabled(enabled);
        }
        if (mSnapshotButton != null) {
            mSnapshotButton.setEnabled(enabled);
        }
        if (mMuteButton != null) {
            mMuteButton.setEnabled(enabled);
        }

        super.setEnabled(enabled);
    }

    public boolean isShowing(){
        return this.isShowing;
    }

    public boolean isDragging() {return this.isDragging;}

}
