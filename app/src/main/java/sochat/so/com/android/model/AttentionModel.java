package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/8.
 */

public class AttentionModel {
    private String thumb;
    private String name;
    private String teacher_id;
    private String detail;
    private String follow;
    private String c_video;

    @Override
    public String toString() {
        return "AttentionModel{" +
                "thumb='" + thumb + '\'' +
                ", name='" + name + '\'' +
                ", teacher_id='" + teacher_id + '\'' +
                ", detail='" + detail + '\'' +
                ", follow='" + follow + '\'' +
                ", c_video='" + c_video + '\'' +
                '}';
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getC_video() {
        return c_video;
    }

    public void setC_video(String c_video) {
        this.c_video = c_video;
    }

    public AttentionModel() {

    }

    public AttentionModel(String thumb, String name, String teacher_id, String detail, String follow, String c_video) {

        this.thumb = thumb;
        this.name = name;
        this.teacher_id = teacher_id;
        this.detail = detail;
        this.follow = follow;
        this.c_video = c_video;
    }
}
