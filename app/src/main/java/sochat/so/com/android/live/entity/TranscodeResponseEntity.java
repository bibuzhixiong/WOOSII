package sochat.so.com.android.live.entity;

/**
 * Created by zhukkun on 3/8/17.
 */
public class TranscodeResponseEntity {
    int transcodestatus;
    long vid;

    public int getTranscodestatus() {
        return transcodestatus;
    }

    public void setTranscodestatus(int transcodestatus) {
        this.transcodestatus = transcodestatus;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }


    @Override
    public String toString() {
        return "vid:" + vid +",transcodestatus:" + transcodestatus;
    }
}
