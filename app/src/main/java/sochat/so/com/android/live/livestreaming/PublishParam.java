package sochat.so.com.android.live.livestreaming;

import com.netease.LSMediaCapture.lsMediaCapture;
import com.netease.vcloud.video.effect.VideoEffect;

import java.io.Serializable;

import static com.netease.LSMediaCapture.lsMediaCapture.FormatType.RTMP;
import static com.netease.LSMediaCapture.lsMediaCapture.StreamType.AV;

/**
 * Created by zhukkun on 12/20/16.
 */
public class PublishParam implements Serializable {
    public String pushUrl = null;
    public String definition = "SD"; // HD: 高清   SD: 标清    LD: 流畅
    public boolean useFilter = true; //是否开启滤镜
    public boolean faceBeauty = false; //是否开启美颜
    public boolean openVideo = true;
    public boolean openAudio = true;


    public String roomtitle;


    lsMediaCapture.StreamType streamType = AV;  // 推流类型
    lsMediaCapture.FormatType formatType = RTMP; // 推流格式
    String recordPath; //文件录制地址，当formatType 为 FLV 或 RTMP_AND_FLV 时有效
    lsMediaCapture.VideoQuality videoQuality = lsMediaCapture.VideoQuality.HIGH; //清晰度
    boolean isScale_16x9 = false; //是否强制16:9
    VideoEffect.FilterType filterType = VideoEffect.FilterType.clean; //滤镜类型
    boolean frontCamera = true; //是否默认前置摄像头
    boolean watermark = false; //是否添加水印
    boolean qosEnable = true;  //是否开启QOS
    boolean graffitiOn = false; //是否添加涂鸦
    boolean uploadLog = true; //是否上传SDK运行日志

}
