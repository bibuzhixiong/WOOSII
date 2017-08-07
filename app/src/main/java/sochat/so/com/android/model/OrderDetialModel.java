package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/6/26.
 */

public class OrderDetialModel {
    private String money_order;
    private String addtime;
    private String type;
    private String no_order;

    @Override
    public String toString() {
        return "OrderDetialModel{" +
                "money_order='" + money_order + '\'' +
                ", addtime='" + addtime + '\'' +
                ", type='" + type + '\'' +
                ", no_order='" + no_order + '\'' +
                '}';
    }

    public String getMoney_order() {
        return money_order;
    }

    public void setMoney_order(String money_order) {
        this.money_order = money_order;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNo_order() {
        return no_order;
    }

    public void setNo_order(String no_order) {
        this.no_order = no_order;
    }

    public OrderDetialModel(String money_order, String addtime, String type, String no_order) {

        this.money_order = money_order;
        this.addtime = addtime;
        this.type = type;
        this.no_order = no_order;
    }
}
