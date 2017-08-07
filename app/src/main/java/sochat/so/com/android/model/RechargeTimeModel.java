package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/4/12.
 */

public class RechargeTimeModel {
    private int time;
    private String money;
    private String name;

    @Override
    public String toString() {
        return "RechargeTimeModel{" +
                "time=" + time +
                ", money='" + money + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RechargeTimeModel(int time, String money, String name) {

        this.time = time;
        this.money = money;
        this.name = name;
    }
}
