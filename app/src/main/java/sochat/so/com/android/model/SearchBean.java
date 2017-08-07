package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SearchBean {
    private String name;
    private String detail;
    private String thumb;
    private String user_id;
    private String c_video;
    private String follow;

    public SearchBean(String name, String detail, String thumb, String user_id, String c_video, String follow) {
        this.name = name;
        this.detail = detail;
        this.thumb = thumb;
        this.user_id = user_id;
        this.c_video = c_video;
        this.follow = follow;
    }

    @Override
    public String toString() {
        return "SearchBean{" +
                "name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", thumb='" + thumb + '\'' +
                ", user_id='" + user_id + '\'' +
                ", c_video='" + c_video + '\'' +
                ", follow='" + follow + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getC_video() {
        return c_video;
    }

    public void setC_video(String c_video) {
        this.c_video = c_video;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public SearchBean() {

    }
}
