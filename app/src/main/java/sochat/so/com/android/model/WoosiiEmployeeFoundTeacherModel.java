package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/6/28.
 */

public class WoosiiEmployeeFoundTeacherModel {
    private String user_id;
    private String thumb;
    private String name;
    private String money;
    private String day_money;
    private String info;

    @Override
    public String toString() {
        return "WoosiiEmployeeFoundTeacherModel{" +
                "user_id='" + user_id + '\'' +
                ", thumb='" + thumb + '\'' +
                ", name='" + name + '\'' +
                ", money='" + money + '\'' +
                ", day_money='" + day_money + '\'' +
                ", info='" + info + '\'' +
                '}';
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDay_money() {
        return day_money;
    }

    public void setDay_money(String day_money) {
        this.day_money = day_money;
    }

    public WoosiiEmployeeFoundTeacherModel(String user_id, String thumb, String name, String money, String day_money) {

        this.user_id = user_id;
        this.thumb = thumb;
        this.name = name;
        this.money = money;
        this.day_money = day_money;
    }
}
