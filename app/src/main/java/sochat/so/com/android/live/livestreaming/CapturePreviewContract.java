package sochat.so.com.android.live.livestreaming;

import android.content.Intent;
import android.view.View;

import com.netease.vcloud.video.effect.VideoEffect;
import com.netease.vcloud.video.render.NeteaseView;


/**
 * Created by zhukkun on 12/9/16.
 */
public class CapturePreviewContract {

    public interface CapturePreviewUi{

        /**
         * 设置直播开始按钮, 是否可点击
         */
        void setStartBtnClickable(boolean clickable);

        /**
         * 根据传入的参数, 调整Ui控件可见性
         */
        void checkInitVisible(PublishParam mPublishParam);

        /**
         * 设置显示的SurfaceView
         * @param hasFilter 是否带滤镜功能
         */
        void setSurfaceView(boolean hasFilter);

        /**
         * 设置预览画面 大小
         * @param hasFilter 是否有滤镜
         */
        void setPreviewSize(boolean hasFilter, int previewWidth, int previewHeight);

        /**
         * 获取正在显示的SurfaceView
         */
        View getDisplaySurfaceView(boolean hasFilter);

        /**
         * 正常开始直播
         */
        void onStartLivingFinished();

        /**
         * 正常停止直播
         */
        void onStopLivingFinished();

        /**
         * 设置audio按钮状态
         * @param isPlay 是否正在开启
         */
        void setAudioBtnState(boolean isPlay);

        /**
         * 设置Video按钮状态
         * @param isPlay 是否正在开启
         */
        void setVideoBtnState(boolean isPlay);

        /**
         * 设置滤镜强度控制栏可见性
         * @param visible
         */
        void setFilterSeekBarVisible(boolean visible);

        /**
         * 显示声波动画
         * @param b
         */
        void showAudioAnimate(boolean b);

        /**
         * 网络断开
         */
        void onDisconnect();

        /**
         * 正常结束直播
         */
        void normalFinish();

        /**
         * 直播准备中
         */
        void onStartInit();

        /**
         * 相机权限错误
         */
        void onCameraPermissionError();

        /**
         * 音频权限错误
         */
        void onAudioPermissionError();

        void setCameraFocus();
    }

    public static abstract class CapturePreviewControllerBase {

        CapturePreviewUi ui;

        /**
         * 处理参数
         */
        public abstract void handleIntent(Intent intent, NeteaseView neteaseView);

        /**
         * 与Activity同生命周期
         */
        public abstract void onResume();

        /**
         * 与Activity同生命周期
         */
        public abstract void onPause();

        /**
         * 与Activity同生命周期
         */
        public abstract void onDestroy();

        /**
         * 开关闪光灯
         */
        public abstract void switchFlash();

        /**
         * 音频开关
         */
        public abstract void switchAudio();

        /**
         * 视频开关
         */
        public abstract void switchVideo();

        /**
         * 截屏
         */
        public abstract void screenShot();

        /**
         * 镜像开关
         */
        public abstract void switchMirror();

        /**
         * 直播开关
         */
        public abstract void liveStartStop();

        /**
         * 切换摄像头
         */
        public abstract void switchCam();

        /**
         * 尝试停止直播
         */
        public abstract void tryToStopLivingStreaming();

        /**
         * 切换滤镜
         */
        public abstract void switchFilterTo(VideoEffect.FilterType var);

        /**
         * 设置变焦
         * @param progress
         */
        public abstract void setZoom(int progress);

        /**
         * 设置滤镜强度
         * @param progress
         */
        public abstract void setFilterStrength(int progress);
        /**
         * 设置磨皮强度
         * @param progress
         */
        public abstract void setBeautyLevel(int progress);

        /**
         * 设置是否使用4G进行直播
         * @param able
         */
        public abstract void canUse4GNetwork(boolean able);

        /**
         * 聚焦
         */
        public abstract void setCameraFocus() ;
    }
}
