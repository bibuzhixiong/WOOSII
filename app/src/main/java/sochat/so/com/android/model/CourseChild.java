package sochat.so.com.android.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/17.
 */

public class CourseChild implements Serializable{
    private String thumb;
    private String c_video;
    private String name;
    private String detail;
    private String follow;
    private String user_id;
    private int code;

    @Override
    public String toString() {
        return "CourseChild{" +
                "thumb='" + thumb + '\'' +
                ", c_video='" + c_video + '\'' +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", follow='" + follow + '\'' +
                ", user_id='" + user_id + '\'' +
                ", code='" + code + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CourseChild() {
    }

    public CourseChild(String thumb, String c_video, String name, String detail, String follow, String user_id) {
        this.thumb = thumb;
        this.c_video = c_video;
        this.name = name;
        this.detail = detail;
        this.follow = follow;
        this.user_id = user_id;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getC_video() {
        return c_video;
    }

    public void setC_video(String c_video) {
        this.c_video = c_video;
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

    public CourseChild(String thumb) {

        this.thumb = thumb;
    }

    public CourseChild(String thumb, String c_video, String name, String detail) {

        this.thumb = thumb;
        this.c_video = c_video;
        this.name = name;
        this.detail = detail;
    }
}
