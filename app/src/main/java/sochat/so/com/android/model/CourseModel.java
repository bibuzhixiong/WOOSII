package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/21.
 */

public class CourseModel {
    private String s_id;
    private String s_name;

    @Override
    public String toString() {
        return "CourseModel{" +
                "s_id='" + s_id + '\'' +
                ", s_name='" + s_name + '\'' +
                '}';
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public CourseModel() {

    }

    public CourseModel(String s_id, String s_name) {

        this.s_id = s_id;
        this.s_name = s_name;
    }
}
