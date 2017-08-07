package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RecommendTeacherModel {
  private String user_id;
  private String thumb;
  private String real_name;

    public RecommendTeacherModel() {
    }

    public RecommendTeacherModel(String user_id, String thumb, String real_name) {
        this.user_id = user_id;
        this.thumb = thumb;
        this.real_name = real_name;
    }

    @Override
    public String toString() {
        return "RecommendTeacherModel{" +
                "user_id='" + user_id + '\'' +
                ", thumb='" + thumb + '\'' +
                ", real_name='" + real_name + '\'' +
                '}';
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

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }
}
