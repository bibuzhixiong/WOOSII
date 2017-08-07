package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/3/15.
 */

public class TeacherCourseInfo {
    private String title;
    private String thumb_url;
    private String vedio_url;
    private String attention_count;

    public TeacherCourseInfo(String title, String thumb_url, String vedio_url, String attention_count) {
        this.title = title;
        this.thumb_url = thumb_url;
        this.vedio_url = vedio_url;
        this.attention_count = attention_count;
    }

    public TeacherCourseInfo() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getVedio_url() {
        return vedio_url;
    }

    public void setVedio_url(String vedio_url) {
        this.vedio_url = vedio_url;
    }

    public String getAttention_count() {
        return attention_count;
    }

    public void setAttention_count(String attention_count) {
        this.attention_count = attention_count;
    }

    @Override
    public String toString() {
        return "TeacherCourseInfo{" +
                "title='" + title + '\'' +
                ", thumb_url='" + thumb_url + '\'' +
                ", vedio_url='" + vedio_url + '\'' +
                ", attention_count='" + attention_count + '\'' +
                '}';
    }
}
