package sochat.so.com.android.live.liveplayer;

/**
 * Created by zhukkun on 12/20/16.
 */
public class PlayerContract {

    public interface PlayerUi{

        /**
         * 缓存进度更新
         */
        void onBufferingUpdate();

        /**
         * 完成进度拖动
         */
        void onSeekComplete();

        /**
         * 推流结束
         * @return 客户端是否处理了
         */
        boolean onCompletion();

        /**
         * 直播异常
         * @return  客户端是否处理了
         */
        boolean onError(String errorInfo);

        /**
         * 设置文件名
         * @param name
         */
        void setFileName(String name);

        /**
         * 控制Loading的显示
         * @param show
         */
        void showLoading(boolean show);

        /**
         * 显示纯音频动画
         * @param b
         */
        void showAudioAnimate(boolean b);

    }

    public interface MediaPlayControllerBase {

        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        /**
         * 获取缓存进度
         * @return
         */
        int getBufferPercentage();

        /**
         * 跳转播放进度
         * @param pos
         */
        void seekTo(long pos);

        /**
         * 是否正在播放
         */
        boolean isPlaying();

        /**
         * 切换 暂停/开始 状态
         * @return  return true if is playing
         */
        boolean switchPauseResume();

        /**
         * 手动设置暂停
         */
        void setManualPause(boolean paused);

        /**
         * 设置缩放模式
         * @param videoScalingMode
         */
        void setVideoScalingMode(int videoScalingMode);

        /**
         * 控制静音
         * @param mute
         */
        void setMute(boolean mute);

        /**
         * 初始化视频
         */
        void initVideo();

        /**
         * 与Activity同生命周期
         */
        void onActivityResume();

        /**
         * 与Activity同生命周期
         */
        void onActivityPause();

        /**
         * 与Activity同生命周期
         */
        void onActivityDestroy();

        /**
         * 截图
         */
        void getSnapshot();

        /**
         * 是否为音频
         * @return
         */
        boolean isLocalAudio();

        /**
         * 是否为直播
         * @return
         */
        boolean isLiveStream();

        /**
         * 是否为硬件解码
         * @return
         */
        boolean isHwDecoder();

    }
}
