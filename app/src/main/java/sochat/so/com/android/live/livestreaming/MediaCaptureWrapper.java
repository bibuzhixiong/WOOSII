package sochat.so.com.android.live.livestreaming;

import android.content.Context;
import android.hardware.Camera;
import android.os.Looper;

import com.netease.LSMediaCapture.lsLogUtil;
import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.LSMediaCapture.lsMessageHandler;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 推流参数包装类
 * Created by zhukkun on 12/9/16.
 */
public class MediaCaptureWrapper {

    public static final int HAVE_AUDIO = 0;//音频
    public static final int HAVE_VIDEO = 1;//视频
    public static final int HAVE_AV = 2;//音视频

    public static final int CAMERA_POSITION_BACK = 0;
    public static final int CAMERA_POSITION_FRONT = 1;

    public static final int LS_VIDEO_CODEC_AVC = 0;
    public static final int LS_VIDEO_CODEC_VP9 = 1;
    public static final int LS_VIDEO_CODEC_H265 = 2;

    public static final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
    public static final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;

    public static final int LS_AUDIO_CODEC_AAC = 0;
    public static final int LS_AUDIO_CODEC_SPEEX = 1;
    public static final int LS_AUDIO_CODEC_MP3 = 2;
    public static final int LS_AUDIO_CODEC_G711A = 3;
    public static final int LS_AUDIO_CODEC_G711U = 4;

    public static final int FLV = 0;
    public static final int RTMP = 1;
    public static final int RTMP_AND_FLV = 2;

    public static final int OpenQoS = 0;
    public static final int CloseQoS = 1;

    public static final int HD_WIDTH = 960;
    public static final int HD_HEIGHT = 540;
    public static final int SD_WIDTH = 640;
    public static final int SD_HEIGHT = 480;
    public static final int LD_WIDTH = 320;
    public static final int LD_HEIGHT = 240;

    private int mVideoPreviewWidth;
    private int mVideoPreviewHeight;

    private int mVideoDecodeWidth;
    private int mVideoDecodeHeight;

    private String mVideoResolution;
    private int outputStreamType = HAVE_AV;

    private PublishParam param;

    private lsMediaCapture mLSMediaCapture = null;
//    private lsMediaCapture.LSLiveStreamingParaCtx mLSLiveStreamingParaCtx = null;
    public lsMediaCapture.LiveStreamingPara mLiveStreamingPara;
    public MediaCaptureWrapper(Context context, lsMessageHandler messageHandler, PublishParam param){

        this.param =param ;
        mVideoResolution = param.definition;

        if(!param.openVideo){
            outputStreamType = HAVE_AUDIO;
        }

        //获取摄像头支持的分辨率信息，根据这个信息，用户可以选择合适的视频preview size和encode size
        //!!!!注意，用户在设置视频采集分辨率和编码分辨率的时候必须预先获取摄像头支持的采集分辨率，并设置摄像头支持的采集分辨率，否则会造成不可预知的错误，导致直播推流失败和SDK崩溃
        //用户可以采用下面的方法(getCameraSupportSize)，获取摄像头支持的采集分辨率列表
        //获取分辨率之后，用户可以按照下表选择性设置采集分辨率和编码分辨率(注意：一定要Android设备支持该种采集分辨率才可以设置，否则会造成不可预知的错误，导致直播推流失败和SDK崩溃)

        //采集分辨率     编码分辨率     建议码率
        //1280x720     1280x720     1500kbps
        //1280x720     960x540      800kbps
        //960x720      960x720      1000kbps
        //960x720      960x540      800kbps
        //960x540      960x540      800kbps
        //640x480      640x480      600kbps
        //640x480      640x360      500kbps
        //320x240      320x240      250kbps
        //320x240      320x180      200kbps


        /*用户需要分别检查前后摄像头支持的分辨率，设置前后摄像头都支持的分辨率，保障在切换摄像头的过程中不会出错
        List<Camera.Size> backCameraSupportSize = CameraHelper.staticGetCameraSupportSize(CAMERA_POSITION_BACK);
        List<Camera.Size> frontCameraSupportSize = CameraHelper.staticGetCameraSupportSize(CAMERA_POSITION_FRONT);

        if (backCameraSupportSize != null && backCameraSupportSize.size() != 0) {
			for (Camera.Size size : backCameraSupportSize) {
				Log.i("CameraTest", "backCameraSupportSize " + " is " + size.width + "x" + size.height);
			}
		}

        if (frontCameraSupportSize != null && frontCameraSupportSize.size() != 0) {
			for (Camera.Size size : frontCameraSupportSize) {
				Log.i("CameraTest", "frontCameraSupportSize " + " is " + size.width + "x" + size.height);
			}
		}*/

        if(mVideoResolution.equals("HD")) {
            //Demo高清档编码分辨率采用960 * 540.此处为设置预览分辨率,首先检测相机是否支持960*540预览,若不支持则预览分辨率设置为主流手机皆支持的1280*720分辨率
            if(CameraHelper.isSupportSize(HD_WIDTH, HD_HEIGHT)){//检测相机是否支持 960 * 540预览
                //高清档 预览分辨率960*540, 与编码分辨率960*540 大小一致
                mVideoPreviewWidth = HD_WIDTH;
                mVideoPreviewHeight = HD_HEIGHT;
            }else{
                //高清档 预览分辨率1280*720, 与编码分辨率960*540 比例一致
                mVideoPreviewWidth = 1280;
                mVideoPreviewHeight = 720;
            }
        }
        else if(mVideoResolution.equals("SD")){
            mVideoPreviewWidth = SD_WIDTH;
            mVideoPreviewHeight = SD_HEIGHT;
        }
        else {
            mVideoPreviewWidth = LD_WIDTH;
            mVideoPreviewHeight = LD_HEIGHT;
        }
        //1、创建直播实例
        lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
        lsMediaCapturePara.setContext(context.getApplicationContext()); //设置SDK上下文（建议使用ApplicationContext）
        lsMediaCapturePara.setMessageHandler(messageHandler); //设置SDK消息回调
        lsMediaCapturePara.setLogLevel(lsLogUtil.LogLevel.INFO); //日志级别
        lsMediaCapturePara.setUploadLog(param.uploadLog);//是否上传SDK日志
        mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara);

//        lsMediaCapture.LsMediaCapturePara lsMediaCapturePara = new lsMediaCapture.LsMediaCapturePara();
//        lsMediaCapturePara.Context = context.getApplicationContext();
//        lsMediaCapturePara.lsMessageHandler = messageHandler;
//        lsMediaCapturePara.videoPreviewWidth = mVideoPreviewWidth;
//        lsMediaCapturePara.videoPreviewHeight = mVideoPreviewHeight;
//        lsMediaCapturePara.useFilter = param.useFilter;
//        lsMediaCapturePara.logLevel = lsLogUtil.LogLevel.INFO;  //设置日志级别
//        lsMediaCapturePara.uploadLog = true; //是否上传SDK日志
//        mLSMediaCapture = new lsMediaCapture(lsMediaCapturePara, param.pushUrl);
        //2、设置直播参数
        mLiveStreamingPara = new lsMediaCapture.LiveStreamingPara();
        mLiveStreamingPara.setStreamType(param.streamType); // 推流类型 AV、AUDIO、VIDEO
        mLiveStreamingPara.setFormatType(param.formatType); // 推流格式 RTMP、MP4、RTMP_AND_MP4
        mLiveStreamingPara.setRecordPath(param.recordPath);//formatType 为 MP4 或 RTMP_AND_MP4 时有效
        mLiveStreamingPara.setQosOn(param.qosEnable);
//        paraSet();
    }

    //音视频参数设置
//    public void paraSet(){
//
//        //创建参数实例
//        mLSLiveStreamingParaCtx = mLSMediaCapture.new LSLiveStreamingParaCtx();
//        mLSLiveStreamingParaCtx.eHaraWareEncType = mLSLiveStreamingParaCtx.new HardWareEncEnable();
//        mLSLiveStreamingParaCtx.eOutFormatType = mLSLiveStreamingParaCtx.new OutputFormatType();
//        mLSLiveStreamingParaCtx.eOutStreamType = mLSLiveStreamingParaCtx.new OutputStreamType();
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx = mLSLiveStreamingParaCtx.new LSAudioParaCtx();
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec = mLSLiveStreamingParaCtx.sLSAudioParaCtx.new LSAudioCodecType();
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx = mLSLiveStreamingParaCtx.new LSVideoParaCtx();
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new LSVideoCodecType();
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraPosition();
//        //mLSLiveStreamingParaCtx.sLSVideoParaCtx.interfaceOrientation = mLSLiveStreamingParaCtx.sLSVideoParaCtx.new CameraOrientation();
//        mLSLiveStreamingParaCtx.sLSQoSParaCtx = mLSLiveStreamingParaCtx.new LSQoSParaCtx();
//
//        //设置摄像头信息，并开始本地视频预览
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition = CAMERA_POSITION_FRONT;//默认前置摄像头，用户可以根据需要调整
//
//        //输出格式：视频、音频和音视频
//        mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType = outputStreamType;//默认音视频推流
//
//        //输出封装格式
//        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatType = RTMP; //默认仅推流
//
//        //输出FLV文件的路径和名称
//        mLSLiveStreamingParaCtx.eOutFormatType.outputFormatFileName = "/sdcard/media.flv";//当outputFormatType为FLV或者RTMP_AND_FLV时有效
//
//        //音频编码参数配置
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.samplerate = 44100;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.bitrate = 64000;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.frameSize = 2048;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.channelConfig = AudioFormat.CHANNEL_IN_MONO;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec.audioCODECType = LS_AUDIO_CODEC_AAC;
//
//        //硬件编码参数设置
//        mLSLiveStreamingParaCtx.eHaraWareEncType.hardWareEncEnable = false;//默认关闭硬件编码
//
//        //网络QoS开关
//        mLSLiveStreamingParaCtx.sLSQoSParaCtx.qosType = OpenQoS; //默认开启Qos
//
//        //视频编码参数配置，视频码率可以由用户任意设置，视频分辨率按照如下表格设置
//        //采集分辨率     编码分辨率     建议码率
//        //1280x720     1280x720     1500kbps
//        //1280x720     960x540      800kbps
//        //960x720      960x720      1000kbps
//        //960x720      960x540      800kbps
//        //960x540      960x540      800kbps
//        //640x480      640x480      600kbps
//        //640x480      640x360      500kbps
//        //320x240      320x240      250kbps
//        //320x240      320x180      200kbps
//
//        //如下是编码分辨率等信息的设置
//        if(mVideoResolution.equals("HD")) {
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 800000;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//            mVideoDecodeWidth = HD_WIDTH;
//            mVideoDecodeHeight = HD_HEIGHT;
//        }
//        else if(mVideoResolution.equals("SD")) {
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 20;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 600000;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//            mVideoDecodeWidth = SD_WIDTH;
//            mVideoDecodeHeight = SD_HEIGHT;
//        }
//        else {
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.fps = 15;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.bitrate = 250000;
//            mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec.videoCODECType = LS_VIDEO_CODEC_AVC;
//            mVideoDecodeWidth = LD_WIDTH;
//            mVideoDecodeHeight = LD_HEIGHT;
//        }
//
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.width = mVideoDecodeWidth;
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.height = mVideoDecodeHeight;
//    }
//
//    public lsMediaCapture.LSLiveStreamingParaCtx getmLSLiveStreamingParaCtx() {
//        return mLSLiveStreamingParaCtx;
//    }
//
    public lsMediaCapture getmLSMediaCapture() {
        return mLSMediaCapture;
    }

    public boolean isVideo(){
        return param.openVideo;
//        return mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_AV || mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType == HAVE_VIDEO;
    }

    public boolean isAudio(){
        return param.openAudio;
//        return mLSLiveStreamingParaCtx.eOutStreamType.outputStreamType== HAVE_AUDIO;
    }

    public int getmVideoPreviewWidth() {
        return mVideoPreviewWidth;
    }

    public int getmVideoPreviewHeight() {
        return mVideoPreviewHeight;
    }

    public int getmVideoDecodeWidth(){
        return mVideoDecodeWidth;
    }

    public int getmVideoDecodeHeight(){
        return mVideoDecodeHeight;
    }

//    public int getCameraPosition() {
//        return mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition.cameraPosition;
//    }

    public boolean isHD() {
        return mVideoResolution.equals("HD");
    }

    public boolean isSD() {
        return mVideoResolution.equals("SD");
    }

    public boolean isLD() {
        return mVideoResolution.equals("LD");
    }

//    public void paraRelease() {
//        mLSLiveStreamingParaCtx.eHaraWareEncType = null;
//        mLSLiveStreamingParaCtx.eOutFormatType = null;
//        mLSLiveStreamingParaCtx.eOutStreamType = null;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx.codec = null;
//        mLSLiveStreamingParaCtx.sLSAudioParaCtx = null;
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.codec = null;
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx.cameraPosition = null;
//        mLSLiveStreamingParaCtx.sLSVideoParaCtx = null;
//        mLSLiveStreamingParaCtx = null;
//    }

    public static class CameraHelper {

        public static CameraHelper instance;
        private static List<Camera.Size> backCameraSupportSize;
        private static List<Camera.Size> frontCameraSupportSize;

        //查询摄像头支持的采集分辨率信息相关变量
        private Thread mCameraThread;
        private Looper mCameraLooper;
        private int mCameraID = CAMERA_POSITION_BACK;//默认查询的是后置摄像头
        private Camera mCamera;

        private CameraHelper(){

        }

        public static CameraHelper getInstance(){
            if(instance == null){
                instance = new CameraHelper();
            }
            return instance;
        }

        //查询Android摄像头支持的采样分辨率相关方法（1）
        public void openCamera(final int cameraID) {
            final Semaphore lock = new Semaphore(0);
            final RuntimeException[] exception = new RuntimeException[1];
            mCameraThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    mCameraLooper = Looper.myLooper();
                    try {
                        mCamera = Camera.open(cameraID);
                    } catch (RuntimeException e) {
                        exception[0] = e;
                    } finally {
                        lock.release();
                        Looper.loop();
                    }
                }
            });
            mCameraThread.start();
            lock.acquireUninterruptibly();
        }

        //查询Android摄像头支持的采样分辨率相关方法（2）
        public void lockCamera() {
            try {
                mCamera.reconnect();
            } catch (Exception e) {
            }
        }

        //查询Android摄像头支持的采样分辨率相关方法（3）
        public void releaseCamera() {
            if (mCamera != null) {
                lockCamera();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

        //查询Android摄像头支持的采样分辨率相关方法（4）
        public List<Camera.Size> getCameraSupportSize(int cameraID) {
            openCamera(cameraID);
            if (mCamera != null) {
                Camera.Parameters param = mCamera.getParameters();
                List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
                releaseCamera();
                return previewSizes;
            }
            return null;
        }

        public static List<Camera.Size> staticGetCameraSupportSize(int mCameraID){
            return getInstance().getCameraSupportSize(mCameraID);
        }

        /**
         * 是否支持特定的预览尺寸
         * @param width
         * @param height
         * @return
         */
        public static boolean isSupportSize(int width, int height) {
            if(backCameraSupportSize == null){
                backCameraSupportSize = staticGetCameraSupportSize(CAMERA_POSITION_BACK);
            }
            if(frontCameraSupportSize == null){
                frontCameraSupportSize = staticGetCameraSupportSize(CAMERA_POSITION_FRONT);
            }

            boolean frontSupport= false, backSupport = false;

            for (int i = 0; i < backCameraSupportSize.size(); i++) {
                Camera.Size size = backCameraSupportSize.get(i);
                if(size.width == width && size.height == height){
                    backSupport = true;
                }
            }

            for (int i = 0; i < frontCameraSupportSize.size(); i++) {
                Camera.Size size = frontCameraSupportSize.get(i);
                if(size.width == width && size.height == height){
                    frontSupport = true;
                }
            }
            return backSupport && frontSupport;
        }
    }
}
