package sochat.so.com.android.live.entity;

/**
 * Created by zhukkun on 3/8/17.
 */
public class AddVideoResponseEntity {

    int transjobstatus; //转码状态, 0为转码提交成功, 否则转码提交失败
    int videoCount; //成功上传的数量,根据此字段来告诉客户端还可上传多少个视频
    long vid;
    VideoInfoEntity videoInfoEntity;

    public int getTransjobstatus() {
        return transjobstatus;
    }

    public void setTransjobstatus(int transjobstatus) {
        this.transjobstatus = transjobstatus;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }


    public VideoInfoEntity getVideoInfoEntity() {
        return videoInfoEntity;
    }

    public void setVideoInfoEntity(VideoInfoEntity videoInfoEntity) {
        this.videoInfoEntity = videoInfoEntity;
    }
}
