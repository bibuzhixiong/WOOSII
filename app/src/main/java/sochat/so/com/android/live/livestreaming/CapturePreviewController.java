package sochat.so.com.android.live.livestreaming;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.netease.LSMediaCapture.Statistics;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.vcloud.video.effect.VideoEffect;
import com.netease.vcloud.video.render.NeteaseView;

import java.io.ByteArrayOutputStream;

import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.utils.NetworkMonitor;
import sochat.so.com.android.live.utils.NetworkUtils;
import sochat.so.com.android.live.utils.VcloudFileUtils;
import sochat.so.com.android.live.widget.MixAudioDialog;
import sochat.so.com.android.live.widget.MixAudioLayout;
import sochat.so.com.android.live.widget.NetWorkInfoDialog;

import static com.netease.LSMediaCapture.lsMediaCapture.StreamType.AUDIO;

/**
 * Created by zhukkun on 12/9/16.
 */
public class CapturePreviewController extends CapturePreviewContract.CapturePreviewControllerBase implements lsMessageHandler {

    /**
     * LOG 标志
     */
    public static final String TAG = "PreviewController";

    public static final String EXTRA_PARAMS = "extra_params";

    /**
     * Activity的上下文
     */
    private Activity mContext;

    /**
     * 上个Activity传递参数的包装类
     */
    private PublishParam mPublishParam;

    /**
     * 直播地址
     */
    private String mliveStreamingURL;

    /**
     * 开关状态记录
     */
    private boolean mUseFilter; //滤镜 是否开启
    private boolean mWaterMarkOn = false; //水印 是否开启
    private boolean mGraffitiOn; //涂鸦 是否开启
    private boolean openMirror; //镜像 是否开启
    private boolean openFlash; //闪关灯 是否开启
    private boolean openAudio = true; //音频流 是否开启
    private boolean openVideo = true; //视频流 是否开始
    private boolean canUse4GNetwork = false; //4G网络下是否直播

    public boolean isfrontOrBack = false;

    /**
     * 直播状态记录
     */
    private boolean m_tryToStopLiveStreaming;
    private boolean m_startVideoCamera;
    private boolean m_liveStreamingOn;
    private boolean m_liveStreamingInit;
    private boolean m_liveStreamingInitFinished;

    /**
     * 伴音相关
     */
    private AudioManager mAudioManager;
    private MixAudioMsgReceiver mixAudioMsgReceiver;
    private String mMixAudioFilePath = null;
    private int mMixAudioState = 4;  //1为开始 2为继续 3为暂停 4为停止
    private int mMixAudioVolume = 2; //强度 1~10

    /**
     * 水印相关
     */
    public static final float PERCENT_OF_WATERMARK = 0.35f; // 水印显示占屏幕宽度比

    /**
     * 应用层对底层直播参数包含类
     */
    private MediaCaptureWrapper captureWrapper = null;

    /**
     * SDK层 直播控制器
     */
    private lsMediaCapture mLSMediaCapture = null;

    /**
     * SDK传递的错误消息时间
     */
    private long mLastAudioProcessErrorAlertTime;
    private long mLastVideoProcessErrorAlertTime;

    /**
     * 广播Intent
     */
    private Intent mIntentLiveStreamingStopFinished = new Intent("LiveStreamingStopFinished");
    private Intent mNetInfoIntent = new Intent(NetWorkInfoDialog.NETINFO_ACTION);

    /**
     * 网络数据包装类
     */
    private Statistics mStatistics = null;

    Handler mainHandler;

    /**
     * 子线程
     */
    HandlerThread subThread;
    Handler subHandler;

    /**
     * 音视频数据处理器
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case MSG_GET_STATICS_INFO:{
                    Bundle bundle = message.getData();
                    int videoFrameRate = bundle.getInt("FR");
                    int videoBitrate = bundle.getInt("VBR");
                    int audioBitrate = bundle.getInt("ABR");
                    int totalRealBitrate = bundle.getInt("TBR");

                    try {
                        if (mNetInfoIntent != null) {
                            mNetInfoIntent.putExtra("videoFrameRate", videoFrameRate);
                            mNetInfoIntent.putExtra("videoBitRate", videoBitrate);
                            mNetInfoIntent.putExtra("audioBitRate", audioBitrate);
                            mNetInfoIntent.putExtra("totalRealBitrate", totalRealBitrate);

                            if (captureWrapper.isHD()) {
                                mNetInfoIntent.putExtra("resolution", 1);
                            } else if (captureWrapper.isSD()) {
                                mNetInfoIntent.putExtra("resolution", 2);
                            } else if (captureWrapper.isLD()) {
                                mNetInfoIntent.putExtra("resolution", 3);
                            }
                        }
                    }
                    catch (IllegalStateException e) {

                    }
                    mContext.sendBroadcast(mNetInfoIntent);
                }
                break;
                case MSG_BAD_NETWORK_DETECT:{
                    showToast("网络较差");
                }
                break;
                case MSG_URL_FORMAT_NOT_RIGHT:{
                    showToast("推流url格式不正确");
                }
                break;
            }
            return false;
        }
    });


    /**
     * 伴音相关 广播接收器
     */
    private class MixAudioMsgReceiver extends BroadcastReceiver {

        public void register(){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(MixAudioDialog.AUDIO_MIX_ACTION);
            intentFilter.addAction(MixAudioDialog.AUDIO_MIX_VOLUME_ACTION);
            mContext.registerReceiver(this, intentFilter);
        }

        public void unregister(){
            try {
                mContext.unregisterReceiver(this);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MixAudioDialog.AUDIO_MIX_ACTION)){
                int audioMixMsg = intent.getIntExtra(MixAudioLayout.EXTRA_OPERATE, 0);
                mMixAudioFilePath = VcloudFileUtils.getMp3FileDir()+ "/" + intent.getStringExtra(MixAudioLayout.EXTRA_MUSIC_PATH);

                //伴音开关的控制
                if(audioMixMsg == MixAudioLayout.MUSIC_START)
                {
                    if(mMixAudioFilePath.isEmpty())
                        return;

                    if(mLSMediaCapture != null) {
                        mLSMediaCapture.startPlayMusic(mMixAudioFilePath, true);
                    }
                }
                else if (audioMixMsg == MixAudioLayout.MUSIC_RESUME)
                {
                    if(mLSMediaCapture != null){
                        mLSMediaCapture.resumePlayMusic();
                    }
                }
                else if(audioMixMsg == MixAudioLayout.MUSIC_PAUSE)
                {
                    if(mLSMediaCapture != null){
                        mLSMediaCapture.pausePlayMusic();
                    }
                }
                else if(audioMixMsg == MixAudioLayout.MUSIC_STOP)
                {
                    if(mLSMediaCapture != null){
                        mLSMediaCapture.stopPlayMusic();
                    }
                }
                mMixAudioState = audioMixMsg;
            }
            else if(action.equals(MixAudioDialog.AUDIO_MIX_VOLUME_ACTION)){   //    伴音音量
                int audioMixVolumeMsg = intent.getIntExtra("AudioMixVolumeMSG", 0);
                mMixAudioVolume = audioMixVolumeMsg;
                //伴音音量的控制
                int maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                int streamVolume = audioMixVolumeMsg*maxStreamVolume/10;
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 1);
            }

        }
    }

    /**
     * 构造函数
     * @param context Activity
     * @param ui 实现UI接口的Activity
     */
    public CapturePreviewController(Activity context, CapturePreviewContract.CapturePreviewUi ui){
        mContext = context;
        this.ui = ui;

        mainHandler = new Handler(Looper.getMainLooper());
        subThread = new HandlerThread("subThread");
        subThread.start();
        subHandler = new Handler(subThread.getLooper());
    }

    /**
     * 处理参数
     * @param intent
     */
    @Override
    public void handleIntent(Intent intent, NeteaseView neteaseView) {
        mPublishParam = (PublishParam)intent.getSerializableExtra(EXTRA_PARAMS);
        if(mPublishParam == null){
            showToast("未传递参数,无法正常初始化");
            return;
        }
        mliveStreamingURL = mPublishParam.pushUrl;
        mUseFilter = mPublishParam.useFilter;
        //美颜 是否开启,需在开启相机后才能设置,故在handlePreViewCallBack()方法中执行.
        ui.checkInitVisible(mPublishParam);
        init(neteaseView);
    }


    /**
     * 直播初始化
     */
    private void init(NeteaseView neteaseView) {
        m_liveStreamingOn = false;
        m_tryToStopLiveStreaming = false;

        //step1. 创建直播实例 初始化参数
        captureWrapper = new MediaCaptureWrapper(mContext, CapturePreviewController.this, mPublishParam);
        mLSMediaCapture = captureWrapper.getmLSMediaCapture();

        //step2. 根据是否有滤镜选择显示不同的surfaceView
//        ui.setSurfaceView(mUseFilter && captureWrapper.isVideo());


//        step3. 设置视频预览参数
//        ui.setPreviewSize(mUseFilter && captureWrapper.isVideo(), captureWrapper.getmVideoPreviewWidth(), captureWrapper.getmVideoPreviewHeight());
        if(mPublishParam.streamType != AUDIO){ //开启预览画面
            boolean frontCamera = mPublishParam.frontCamera; // 是否前置摄像头
            boolean mScale_16x9 = mPublishParam.isScale_16x9; //是否强制16:9
            lsMediaCapture.VideoQuality videoQuality = mPublishParam.videoQuality; //视频模板（SUPER_HIGH 1280*720、SUPER 960*540、HIGH 640*480、MEDIUM 480*360、LOW 352*288）
            mLSMediaCapture.startVideoPreview(neteaseView,frontCamera,mUseFilter,videoQuality,mScale_16x9);
        }

        m_startVideoCamera = true;
        if(mUseFilter){ //demo中默认设置为干净滤镜
            mLSMediaCapture.setBeautyLevel(5); //磨皮强度为5,共5档，0为关闭
            mLSMediaCapture.setFilterStrength(0.5f); //滤镜强度
            mLSMediaCapture.setFilterType(mPublishParam.filterType);
        }

//        step3. 设置视频预览参数
//        ui.setPreviewSize(mUseFilter && captureWrapper.isVideo(), captureWrapper.getmVideoPreviewWidth(), captureWrapper.getmVideoPreviewHeight());
//
//        if(captureWrapper.isAudio()){
//            ui.showAudioAnimate(true);
//        }
//
//        //step4. 开启直播预览
//        if(mLSMediaCapture != null && captureWrapper.isVideo()) {
//            if(mUseFilter){
//                mLSMediaCapture.startVideoPreviewOpenGL((NeteaseGLSurfaceView)ui.getDisplaySurfaceView(mUseFilter), captureWrapper.getCameraPosition());
//                m_startVideoCamera = true;
//            } else{
//                mLSMediaCapture.startVideoPreview((NeteaseSurfaceView)ui.getDisplaySurfaceView(mUseFilter), captureWrapper.getCameraPosition());
//                m_startVideoCamera = true;
//            }
//        }

        //伴音相关初始化
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mixAudioMsgReceiver = new MixAudioMsgReceiver();
        mixAudioMsgReceiver.register();
        //自定义视频采集初始化
        //customVideoInit();

    }

    /**
     * 自定义视频采集初始化
     */
    private void customVideoInit() {

        //[示例代码] 设置自定义视频采集类型
        //mLSMediaCapture.setSourceType(lsMediaCapture.SourceType.CustomVideo);
        //自定义输入默认是横屏，正的yuv数据
        //【示例代码】结束

        //【示例代码 customVideo】设置自定义视频采集逻辑 （自定义视频采集逻辑不要调用startPreview，也不用初始化surfaceView）
//		new Thread() {  //视频采集线程
//			@Override
//			public void run() {
//				while (m_liveStreamingOn) {
//					try {
//						int width = 1024;
//						int height = 768;
//						FileInputStream in = new FileInputStream("/sdcard/dump_1024_768.yuv");
//						int len = width * height * 3 / 2;
//						byte buffer[] = new byte[len];
//						int count;
//						while ((count = in.read(buffer)) != -1) {
//							if (len == count) {
//								mLSMediaCapture.sendCustomYUVData(buffer,width,height);
//							} else {
//								break;
//							}
//							sleep(50, 0);
//						}
//						in.close();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
        //【示例代码】结束

        //【示例代码2】设置自定义音频采集逻辑（音频采样位宽必须是16）
//		new Thread() {  //音频采集线程
//            @Override
//            public void run() {
//                while (m_liveStreamingOn) {
//                    try {
//                        FileInputStream in = new FileInputStream("/sdcard/dump.pcm");
//                        int len = 2048;
//                        byte buffer[] = new byte[len];
//                        int count;
//                        while ((count = in.read(buffer)) != -1) {
//                            if (len == count) {
//                                mLSMediaCapture.sendCustomPCMData(buffer);
//                            } else {
//                                break;
//                            }
//                            sleep(20, 0);
//                        }
//                        in.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
        //【示例代码】结束
    }

    long clickTime = 0;

    private Thread mThread;
    /**
     * 直播开关
     */
    @Override
    public void liveStartStop() {
        long time = System.currentTimeMillis();
        if (time - clickTime < 1000) {
            showToast("请勿点击过快.");
            return;
        }
        clickTime = time;
        ui.setStartBtnClickable(false);

        if (!m_liveStreamingOn) {
            //8、初始化直播推流
            if(mThread != null){
                showToast("正在开启直播，请稍后。。。");
                return;
            }
            ui.onStartInit();
            mThread = new Thread(){
                public void run() { //正常网络下initLiveStream 1、2s就可完成，当网络很差时initLiveStream可能会消耗5-10s，因此另起线程防止UI卡住
                    if (mLSMediaCapture != null) {
//                        m_liveStreamingInitFinished = mLSMediaCapture.initLiveStream(captureWrapper.getmLSLiveStreamingParaCtx());
                        m_liveStreamingInitFinished = mLSMediaCapture.initLiveStream(captureWrapper.mLiveStreamingPara,mliveStreamingURL);
                    }
                    if (m_liveStreamingInitFinished) {
                        startAV();
                    } else {
                        showToast("直播开启失败, 正在退出当前界面。。。");
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ui.normalFinish();
                            }
                        }, 5000);
                    }
                    mThread = null;
                }
            };
            mThread.start();
        }else{
            mLSMediaCapture.stopLiveStreaming();
        }
    }

    private int getQuality(){
        if(captureWrapper.isLD()){
            return 1;
        }else if(captureWrapper.isSD()){
            return 2;
        }else if(captureWrapper.isHD()){
            return 3;
        }
        return 2;
    }
    /**
     * 开始直播
     */
    private void startAV(){
        if(mLSMediaCapture != null && m_liveStreamingInitFinished) {

           Log.e(ConfigInfo.TAG," mLSMediaCapture.getSDKVersion():"+ mLSMediaCapture.getSDKVersion());

            //5、设置视频水印参数（可选）水印图片的像素值必须为偶数,否则会崩溃
//            if(mWaterMarkOn && (captureWrapper.isVideo())){
//                int mWaterMarkPosX = (int) (captureWrapper.getmVideoDecodeHeight() * ( 0.95 - PERCENT_OF_WATERMARK));//视频水印坐标(X)
//                int mWaterMarkPosY = 10;//视频水印坐标(Y)
//                mLSMediaCapture.setWaterMarkPara(VcloudFileUtils.getWaterMarkFilePath(getQuality()), mWaterMarkPosX, mWaterMarkPosY);
//            }
            /**
             * 这里就不设置水印了
             */
//            if(mWaterMarkOn){
//                //8、设置视频水印参数（可选）
//                addWaterMark();
//                //9、设置视频动态水印参数（可选）
//                addDynamicWaterMark();
//            }

            //6、开始直播
            mLSMediaCapture.startLiveStreaming();
            m_liveStreamingOn = true;

            //7、设置视频涂鸦参数（可选）
//            if(mGraffitiOn && (captureWrapper.isVideo())) {
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream(VcloudFileUtils.getGraffitiFilePath());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                int mWidth = bitmap.getWidth();
//                int mHeight = bitmap.getHeight();
//                int[] mIntArray = new int[mWidth * mHeight];
//
//                // Copy pixel data from the Bitmap into the 'intArray' array
//                bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
//
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    Log.e(TAG, "error : ", e);
//                }
//                int mGraffitiPosX = 0, mGraffitiPosY = 0;
//                mLSMediaCapture.setGraffitiPara(mIntArray, mWidth, mHeight, mGraffitiPosX, mGraffitiPosY, mGraffitiOn);
//            }
        }
    }

    /**
     * 重启直播
     */
    public void reStartLive(){
        if(mLSMediaCapture!=null){
            //todo 修改为stop 再start
            mLSMediaCapture.restartLiveStream();
        }
    }

    /**
     * 音频流开关
     */
    @Override
    public void switchAudio() {
        openAudio = !openAudio;
        if(openAudio){
            mLSMediaCapture.resumeAudioLiveStream();
        }else{
            mLSMediaCapture.pauseAudioLiveStream();
        }
        //showToast((openAudio?"开启":"关闭") + "音频流");
        ui.setAudioBtnState(openAudio);
    }

    /**
     * 视频流开关
     */
    @Override
    public void switchVideo() {
        openVideo = !openVideo;
        if(openVideo){
            mLSMediaCapture.resumeVideoLiveStream();
        }else{
            mLSMediaCapture.pauseVideoLiveStream();
        }
        showToast((openVideo?"开启":"关闭") + "视频流");
        ui.setVideoBtnState(openVideo);
    }

    /**
     * 开关闪光灯
     */
    @Override
    public void switchFlash() {
        openFlash = !openFlash;
        if(mLSMediaCapture != null) {
            mLSMediaCapture.setCameraFlashPara(openFlash);
        }
        showToast((openFlash?"开启":"关闭") + "闪关灯");
    }

    /**
     * 截屏
     */
    @Override
    public void screenShot() {
        if(mLSMediaCapture != null){
            if(captureWrapper.isVideo()) {
                mLSMediaCapture.enableScreenShot();
            }else{
                showToast("音频推流不支持截图");
            }
        }
    }

    /**
     * 镜像开关
     */
    @Override
    public void switchMirror() {
        openMirror = !openMirror;
        if(mLSMediaCapture != null) {
            mLSMediaCapture.setVideoMirror(openMirror);
            showToast((openMirror?"开启":"关闭") + "镜像");
        }
    }

    /**
     * 切换摄像头
     */
    @Override
    public void switchCam() {
        isfrontOrBack = true ;
        if(mLSMediaCapture != null) {
            mLSMediaCapture.switchCamera();
        }
    }

    /**
     * 切换滤镜
     */
    @Override
    public void switchFilterTo(VideoEffect.FilterType var1) {
        if (mLSMediaCapture != null) {
            mLSMediaCapture.setFilterType(var1);
        }
    }

    /**
     * 防止用户快速拖拽,且缩放为耗时操作,故起Runnable在子线程中运行
     */
    class ZoomRunnable implements Runnable{

        int mInitProgress; //初始时的进度

        public ZoomRunnable(int mInitProgress){
            this.mInitProgress = mInitProgress;
        }

        @Override
        public void run() {
            if(latestProgress != mInitProgress){
                return;  //丢弃非最新的Runnable
            }
            int targetValue = (int) (mInitProgress * maxCameraZoomValue / 100f);
            if (mLSMediaCapture.getCameraZoomValue() < targetValue) {
                while (mLSMediaCapture.getCameraZoomValue() < targetValue) {
                    if(latestProgress != mInitProgress) return; //执行过程中,又有新的progress,故放弃执行当前Runnable
                    mLSMediaCapture.setCameraZoomPara(1);  /**这里是摄像头的缩放，里面的参数是缩放的参数,这里给的1是我随便给的——王双小哥标注*/
                }
            } else if (mLSMediaCapture.getCameraZoomValue() > targetValue) {
                while (mLSMediaCapture.getCameraZoomValue() > targetValue) {
                    if(latestProgress != mInitProgress) return; //执行过程中,又有新的progress,故放弃执行当前Runnable
                    mLSMediaCapture.setCameraZoomPara(1);
                }
            }
        }
    }

    int maxCameraZoomValue;
    int latestProgress; //最新的进度

    @Override
    public void setZoom(final int progress) {
        if(maxCameraZoomValue == 0) maxCameraZoomValue = mLSMediaCapture.getCameraMaxZoomValue();
        latestProgress = progress;
        subHandler.post(new ZoomRunnable(progress));
    }



    @Override
    public void setFilterStrength(int progress) {
        if(mLSMediaCapture != null){
            float param;
            param = (float)progress/100;
            mLSMediaCapture.setFilterStrength(param);
        }
    }

    @Override
    public void setBeautyLevel(int progress) {
        if(mLSMediaCapture != null){
            int param;
            param = progress/20;
            mLSMediaCapture.setBeautyLevel(param);
        }
    }

    @Override
    public void canUse4GNetwork(boolean able) {
        canUse4GNetwork = able;
    }

    @Override
    public void setCameraFocus() {
        if(mLSMediaCapture != null) {
            mLSMediaCapture.setCameraFocus();
        }
    }

    /**
     * 与Activity同生命周期
     */
    @Override
    public void onResume() {
        if (mLSMediaCapture != null && m_liveStreamingOn) {
            if (captureWrapper.isVideo()) {
                //关闭推流固定图像，正常推流
                mLSMediaCapture.resumeVideoEncode();
            } else if (captureWrapper.isAudio()) {
                //关闭推流静音帧
                mLSMediaCapture.resumeAudioEncode();
            }
        }
    }

    /**
     * 与Activity同生命周期
     */
    @Override
    public void onPause() {
        if(mLSMediaCapture != null) {
            if(!m_tryToStopLiveStreaming && m_liveStreamingOn)
            {
                if(captureWrapper.isVideo()) {
                    //推最后一帧图像
                    mLSMediaCapture.backgroundVideoEncode();
                }
                else if(captureWrapper.isAudio())
                {
                    //释放音频采集资源
                    mLSMediaCapture.backgroundAudioEncode();
                }
            }
        }
    }

    /**
     * 与Activity同生命周期
     */
    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (m_liveStreamingInit) {
            m_liveStreamingInit = false;
        }
        if (mLSMediaCapture != null){
            mLSMediaCapture.stopPlayMusic();
        }
        mixAudioMsgReceiver.unregister();

        //停止直播调用相关API接口
        if(mLSMediaCapture != null && m_liveStreamingOn) {

            //停止直播，释放资源
            mLSMediaCapture.stopLiveStreaming();

            //如果音视频或者单独视频直播，需要关闭视频预览
            if(m_startVideoCamera)
            {
                mLSMediaCapture.stopVideoPreview();
                mLSMediaCapture.destroyVideoPreview();
            }

            //反初始化推流实例，当它与stopLiveStreaming连续调用时，参数为false
            mLSMediaCapture.uninitLsMediaCapture(false);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 2);
            mContext.sendBroadcast(mIntentLiveStreamingStopFinished);
        }
        else if(mLSMediaCapture != null && m_startVideoCamera)
        {
            mLSMediaCapture.stopVideoPreview();
            mLSMediaCapture.destroyVideoPreview();

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            mLSMediaCapture.uninitLsMediaCapture(true);
            mLSMediaCapture = null;

            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            mContext.sendBroadcast(mIntentLiveStreamingStopFinished);
        }
        else if(!m_liveStreamingInitFinished) {
            mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
            mContext.sendBroadcast(mIntentLiveStreamingStopFinished);

            //反初始化推流实例，当它不与stopLiveStreaming连续调用时，参数为true
            if(mLSMediaCapture != null) {
                mLSMediaCapture.uninitLsMediaCapture(true);
            }
        }

        if(m_liveStreamingOn) {
            m_liveStreamingOn = false;
        }
    }

    /**
     * 尝试停止直播
     */
    @Override
    public void tryToStopLivingStreaming() {
        m_tryToStopLiveStreaming = true;
    }

    /**
     * 处理SDK抛上来的异常和事件，用户需要在这里监听各种消息，进行相应的处理。
     * 例如监听断网消息，用户根据断网消息进行直播重连
     * 注意：直播重连请使用restartLiveStream，在网络带宽较低导致发送码率帧率降低时，可以调用这个接口重启直播，改善直播质量
     * 在网络断掉的时候（用户可以监听 MSG_RTMP_ URL_ERROR 和 MSG_BAD_NETWORK_DETECT ），用户不可以立即调用改接口，而是应该在网络重新连接以后，主动调用这个接口。
     * 如果在网络没有重新连接便调用这个接口，直播将不会重启
     * @param msg 抛出的消息
     * @param object 消息附带的信息
     */
    @Override
    public void handleMessage(final int msg, final Object object) {
        //由于sdk可能由子线程触发事件,故传递至主线程进行操作
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                handleMessageInMainThread(msg, object);
            }
        });
    }

    private void handleMessageInMainThread(int msg, Object object) {
        switch (msg) {
            case MSG_INIT_LIVESTREAMING_OUTFILE_ERROR://初始化直播出错
            case MSG_INIT_LIVESTREAMING_VIDEO_ERROR:
            case MSG_INIT_LIVESTREAMING_AUDIO_ERROR:
            {
                showToast("初始化直播出错");
                break;
            }
            case MSG_START_LIVESTREAMING_ERROR://开始直播出错
            {
                break;
            }
            case MSG_STOP_LIVESTREAMING_ERROR://停止直播出错
            {
                if(m_liveStreamingOn)
                {
                    showToast("MSG_STOP_LIVESTREAMING_ERROR  停止直播出错");
                }
                break;
            }
            case MSG_AUDIO_PROCESS_ERROR://音频处理出错
            {
                if(m_liveStreamingOn && System.currentTimeMillis() - mLastAudioProcessErrorAlertTime >= 10000)
                {
                    showToast("音频处理出错");
                    mLastAudioProcessErrorAlertTime = System.currentTimeMillis();
                }

                break;
            }
            case MSG_VIDEO_PROCESS_ERROR://视频处理出错
            {
                if(m_liveStreamingOn && System.currentTimeMillis() - mLastVideoProcessErrorAlertTime >= 10000)
                {
                    showToast("视频处理出错");
                    mLastVideoProcessErrorAlertTime = System.currentTimeMillis();
                }
                break;
            }
            case MSG_START_PREVIEW_ERROR://视频预览出错，可能是获取不到camera的使用权限
            {
                Log.i(TAG, "test: in handleMessage, MSG_START_PREVIEW_ERROR");
                //showToast("无法打开相机，可能没有相关的权限");
                ui.onCameraPermissionError();
                break;
            }
            case MSG_AUDIO_RECORD_ERROR://音频采集出错，获取不到麦克风的使用权限
            {
                //showToast("无法开启录音，可能没有相关的权限");
                ui.onAudioPermissionError();
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_RECORD_ERROR");
                break;
            }
            case MSG_RTMP_URL_ERROR://断网消息
            {
                Log.i(TAG, "test: in handleMessage, MSG_RTMP_URL_ERROR");
                showToast("RTMP_URL_ERROR 推流已断开");
                onRtmpUrlError();
                break;
            }
            case MSG_URL_NOT_AUTH://直播URL非法，URL格式不符合视频云要求
            {
                showToast("MSG_URL_NOT_AUTH  直播地址不合法");
                break;
            }
            case MSG_SEND_STATICS_LOG_ERROR://发送统计信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_ERROR");
                break;
            }
            case MSG_SEND_HEARTBEAT_LOG_ERROR://发送心跳信息出错
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_HEARTBEAT_LOG_ERROR");
                break;
            }
            case MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR://音频采集参数不支持
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_SAMPLE_RATE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR://音频参数不支持
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_PARAMETER_NOT_SUPPORT_BY_HARDWARE_ERROR");
                break;
            }
            case MSG_NEW_AUDIORECORD_INSTANCE_ERROR://音频实例初始化出错
            {
                Log.i(TAG, "test: in handleMessage, MSG_NEW_AUDIORECORD_INSTANCE_ERROR");
                break;
            }
            case MSG_AUDIO_START_RECORDING_ERROR://音频采集出错
            {
                Log.i(TAG, "test: in handleMessage, MSG_AUDIO_START_RECORDING_ERROR");
                break;
            }
            case MSG_QOS_TO_STOP_LIVESTREAMING://网络QoS极差，视频码率档次降到最低
            {
                showToast("MSG_QOS_TO_STOP_LIVESTREAMING");
                Log.i(TAG, "test: in handleMessage, MSG_QOS_TO_STOP_LIVESTREAMING");
                break;
            }
            case MSG_HW_VIDEO_PACKET_ERROR://视频硬件编码出错反馈消息
            {
                showToast("视频硬件编码出错");
                break;
            }
            case MSG_WATERMARK_INIT_ERROR://视频水印操作初始化出错
            {
                break;
            }
            case MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR://视频水印图像超出原始视频出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PIC_OUT_OF_VIDEO_ERROR");
                break;
            }
            case MSG_WATERMARK_PARA_ERROR://视频水印参数设置出错
            {
                //Log.i(TAG, "test: in handleMessage: MSG_WATERMARK_PARA_ERROR");
                break;
            }
            case MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR://camera采集分辨率不支持
            {
                //Log.i(TAG, "test: in handleMessage: MSG_CAMERA_PREVIEW_SIZE_NOT_SUPPORT_ERROR");
                break;
            }
            case MSG_START_PREVIEW_FINISHED://camera采集预览完成
            {
                Log.i(TAG, "test: MSG_START_PREVIEW_FINISHED");
                //处理美颜与闪关灯是否开启等设置
                if(captureWrapper.isVideo()) {
                    handlePreViewCallBack();
                }
                break;
            }
            case MSG_START_LIVESTREAMING_FINISHED://开始直播完成
            {
                Log.i(TAG, "test: MSG_START_LIVESTREAMING_FINISHED");
                handleStartLiveCallBack();
                break;
            }
            case MSG_STOP_LIVESTREAMING_FINISHED://停止直播完成
            {
                Log.i(TAG, "test: MSG_STOP_LIVESTREAMING_FINISHED");
                m_liveStreamingOn = false;
                ui.setStartBtnClickable(true);
                ui.onStopLivingFinished();
                {
                    mIntentLiveStreamingStopFinished.putExtra("LiveStreamingStopFinished", 1);
                    mContext.sendBroadcast(mIntentLiveStreamingStopFinished);
                }
                break;
            }
            case MSG_STOP_VIDEO_CAPTURE_FINISHED:
            {
                Log.i(TAG, "test: in handleMessage: MSG_STOP_VIDEO_CAPTURE_FINISHED");
                break;
            }
            case MSG_STOP_AUDIO_CAPTURE_FINISHED:
            {
                Log.i(TAG, "test: in handleMessage: MSG_STOP_AUDIO_CAPTURE_FINISHED");
                break;
            }
            case MSG_SWITCH_CAMERA_FINISHED://切换摄像头完成
            {
                int cameraId = (Integer) object;//切换之后的camera id
                break;
            }
            case MSG_SEND_STATICS_LOG_FINISHED://发送统计信息完成
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SEND_STATICS_LOG_FINISHED");
                break;
            }
            case MSG_SERVER_COMMAND_STOP_LIVESTREAMING://服务器下发停止直播的消息反馈，暂时不使用
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SERVER_COMMAND_STOP_LIVESTREAMING");
                break;
            }
            case MSG_GET_STATICS_INFO://获取统计信息的反馈消息
            {
                Message message = Message.obtain(mHandler, MSG_GET_STATICS_INFO);
                mStatistics = (Statistics) object;

                Bundle bundle = new Bundle();
                bundle.putInt("FR", mStatistics.videoEncodeFrameRate);
                bundle.putInt("VBR", mStatistics.videoEncodeBitRate);
                bundle.putInt("ABR", mStatistics.audioEncodeBitRate);
                bundle.putInt("TBR", mStatistics.totalRealSendBitRate);
                message.setData(bundle);

                if(mHandler != null) {
                    mHandler.sendMessage(message);
                }
                break;
            }
            case MSG_BAD_NETWORK_DETECT://如果连续一段时间（10s）实际推流数据为0，会反馈这个错误消息
            {
                Log.e(TAG, "MSG_BAD_NETWORK_DETECT");
                Message m = Message.obtain(mHandler, MSG_BAD_NETWORK_DETECT);
                mHandler.sendMessage(m);
                break;
            }
            case MSG_SCREENSHOT_FINISHED://视频截图完成后的消息反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SCREENSHOT_FINISHED, buffer is " + (byte[]) object);
                boolean shotSuc = false;
                //如果object是一个bitmap对象，那就转为byte[]
                if (object instanceof Bitmap){
                    Bitmap object1 = (Bitmap) object;
                    ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
                    object1.compress(Bitmap.CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                    object1.recycle();//自由选择是否进行回收
                    byte[] result = output.toByteArray();//转换成功了
                    shotSuc = VcloudFileUtils.getScreenShotByteBuffer(result);
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    shotSuc = VcloudFileUtils.getScreenShotByteBuffer((byte[]) object);
                }
                showToast(shotSuc? "截图成功,可在相册中查看": "截图失败");
                break;
            }
            case MSG_SET_CAMERA_ID_ERROR://设置camera出错（对于只有一个摄像头的设备，如果调用了不存在的摄像头，会反馈这个错误消息）
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_CAMERA_ID_ERROR");
                break;
            }
            case MSG_SET_GRAFFITI_ERROR://设置涂鸦出错消息反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_SET_GRAFFITI_ERROR");
                break;
            }
            case MSG_MIX_AUDIO_FINISHED://伴音一首MP3歌曲结束后的反馈
            {
                //Log.i(TAG, "test: in handleMessage, MSG_MIX_AUDIO_FINISHED");
                break;
            }
            case MSG_URL_FORMAT_NOT_RIGHT://推流url格式不正确
            {
                Log.e(TAG, "MSG_URL_FORMAT_NOT_RIGHT");
                Message m = Message.obtain(mHandler, MSG_URL_FORMAT_NOT_RIGHT);
                mHandler.sendMessage(m);
                break;
            }
            case MSG_URL_IS_EMPTY://推流url为空
            {
                //Log.i(TAG, "test: in handleMessage, MSG_URL_IS_EMPTY");
                break;
            }
//            case MSG_SPEED_CALC_SUCCESS:
//            {
//                Log.e(TAG, "MSG_SPEED_CALC_SUCCESS");
//                Message m = Message.obtain(mHandler, MSG_SPEED_CALC_SUCCESS);
//                m.obj = object;
//                mHandler.sendMessage(m);
//                break;
//            }
//            case MSG_SPEED_CALC_FAIL:
//            {
//                Log.e(TAG, "MSG_SPEED_CALC_FAIL");
//                Message m = Message.obtain(mHandler, MSG_SPEED_CALC_FAIL);
//                mHandler.sendMessage(m);
//                break;
//            }
        }
    }


    boolean showConfirmDialog = false;
    boolean hasReconnected = false;
    /**
     * 处理SDK上报的网络中断
     */
    private void onRtmpUrlError() {
        hasReconnected = false;
        DialogMaker.showProgressDialog(mContext, "网络重连中")
        .setCanceledOnTouchOutside(false);

        NetworkMonitor monitor = new NetworkMonitor(new NetworkMonitor.NetworkListener() {

            @Override
            public void onNetworkChanged(final NetworkMonitor monitor, boolean connected, int newType) {
                if(!hasReconnected && connected) {
                    if(!canUse4GNetwork && newType == NetworkUtils.TYPE_MOBILE){
                        if(!showConfirmDialog) {
                            showConfirmDialog(null, "正在使用手机流量,是否继续?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    canUse4GNetwork = true;
                                    hasReconnected = true;
                                    reStartLive();
                                    monitor.stop();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ui.normalFinish();
                                }
                            });
                            showConfirmDialog = true;
                        }
                    }else {
                        hasReconnected = true;
                        reStartLive();
                        monitor.stop();
                    }
                }
            }
        });

        monitor.startMonitorDuration(10000, new Runnable() {
            @Override
            public void run() {
                DialogMaker.dismissProgressDialog();
                if(!hasReconnected){
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConfirmDialog = false;
                            ui.onDisconnect();
                        }
                    });
                }
            }
        });

    }


    boolean hasHandleStartLiveCallBack = false;

    /**
     * 处理开始直播完成回调
     */
    private void handleStartLiveCallBack() {
        showToast("直播已开始");
        m_liveStreamingOn = true;
        ui.setStartBtnClickable(true);
        ui.onStartLivingFinished();

        if(!hasHandleStartLiveCallBack){
            hasHandleStartLiveCallBack = true;
            if(!mPublishParam.openAudio){
                //参数配置时,未开启音频推流,则初始化时,手动关闭音频推流
                switchAudio();
            }
        }
    }

    boolean hasHandlePreViewCallBack = false;

    /**
     * 只处理首次预览完成时的回调, 进行参数设置
     */
    private void handlePreViewCallBack() {

        if(!hasHandlePreViewCallBack){
            //设置是否美颜
            if(mPublishParam.faceBeauty){
                switchFilterTo(VideoEffect.FilterType.clean);
                ui.setFilterSeekBarVisible(true);
            }
            hasHandlePreViewCallBack = true;
        }
    }


    private Toast mToast;
    private void showToast(final String text){
        try {
            if (mToast == null) {
                mToast = Toast.makeText(mContext.getApplicationContext(), text, Toast.LENGTH_LONG);
            }
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mToast.setText(text);
                        mToast.show();
                    }
                });
            } else {
                mToast.setText(text);
                mToast.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showConfirmDialog(final String title, final String message, final DialogInterface.OnClickListener okEvent, final DialogInterface.OnClickListener cancelEvent){
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton("确定",
                        okEvent);
                builder.setNegativeButton("取消",
                        cancelEvent);
                builder.show();
            }
        });

    }


    public void setCaptureFormat(lsMediaCapture.VideoQuality var){
        mLSMediaCapture.changeCaptureFormat(var,true);
    }
}
