package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/6/24.
 */

public class SixPirtyIncomeModel {
    private String money;
    private String percent;
    private String addtime;

    @Override
    public String toString() {
        return "SixPirtyIncomeModel{" +
                "money='" + money + '\'' +
                ", precent='" + percent + '\'' +
                ", addtime='" + addtime + '\'' +
                '}';
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPrecent() {
        return percent;
    }

    public void setPrecent(String precent) {
        this.percent = precent;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public SixPirtyIncomeModel() {

    }

    public SixPirtyIncomeModel(String money, String precent, String addtime) {

        this.money = money;
        this.percent = precent;
        this.addtime = addtime;
    }
}
