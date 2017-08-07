package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/17.
 */

public class EmployeeModel {

    private String name ;
    private String tel ;
    private String thumb ;
    private String user_id ;

    @Override
    public String toString() {
        return "EmployeeModel{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", thumb='" + thumb + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public EmployeeModel() {

    }

    public EmployeeModel(String name, String tel) {

        this.name = name;
        this.tel = tel;
    }
}
