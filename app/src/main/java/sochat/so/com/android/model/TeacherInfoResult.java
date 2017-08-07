package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/20.
 */

public class TeacherInfoResult {
    private String PIndex;
    private String PSize;
    private int Coun;
    private String PCount;

    private String name;
    private String user_id;
    private String thumb;
    private String detail;
    private String count;
    private String follow;
    private List<TeacherCourseList> Childs;
    private int code;

    @Override
    public String toString() {
        return "TeacherInfoResult{" +
                "PIndex='" + PIndex + '\'' +
                ", PSize='" + PSize + '\'' +
                ", Coun=" + Coun +
                ", PCount='" + PCount + '\'' +
                ", name='" + name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", thumb='" + thumb + '\'' +
                ", detail='" + detail + '\'' +
                ", count='" + count + '\'' +
                ", follow='" + follow + '\'' +
                ", Childs=" + Childs +
                ", code=" + code +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPIndex() {
        return PIndex;
    }

    public void setPIndex(String PIndex) {
        this.PIndex = PIndex;
    }

    public String getPSize() {
        return PSize;
    }

    public void setPSize(String PSize) {
        this.PSize = PSize;
    }

    public int getCoun() {
        return Coun;
    }

    public void setCoun(int coun) {
        Coun = coun;
    }

    public String getPCount() {
        return PCount;
    }

    public void setPCount(String PCount) {
        this.PCount = PCount;
    }

    public TeacherInfoResult(String name, String user_id, String thumb, String detail, String count, String follow, List<TeacherCourseList> childs) {
        this.name = name;
        this.user_id = user_id;
        this.thumb = thumb;
        this.detail = detail;
        this.count = count;
        this.follow = follow;
        Childs = childs;
    }

    public TeacherInfoResult() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public List<TeacherCourseList> getChilds() {
        return Childs;
    }

    public void setChilds(List<TeacherCourseList> childs) {
        Childs = childs;
    }
}
