package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/5/23.
 */

public class BankCardModel {
    //银行卡编号，用来记录是什么卡，显示图片用的
    private String bank_code;
    //银行卡名字
    private String bank_name;
    //绑定的电话
    private String bind_mobile;
    //银行卡后四位
    private String card_no;
    //银行卡类型
    private String card_type;
    //银通协议编号
    private String no_agree;

    public BankCardModel() {
        this.bank_code = "暂无";
        this.bank_name = "您未绑定银行卡";
        this.bind_mobile = "";
        this.card_no = "请到钱包界面绑定";
        this.card_type = "";
        this.no_agree = "";
    }

    @Override
    public String toString() {
        return "BankCardModel{" +
                "bank_code='" + bank_code + '\'' +
                ", bank_name='" + bank_name + '\'' +
                ", bind_mobile='" + bind_mobile + '\'' +
                ", card_no='" + card_no + '\'' +
                ", card_type='" + card_type + '\'' +
                ", no_agree='" + no_agree + '\'' +
                '}';
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBind_mobile() {
        return bind_mobile;
    }

    public void setBind_mobile(String bind_mobile) {
        this.bind_mobile = bind_mobile;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getNo_agree() {
        return no_agree;
    }

    public void setNo_agree(String no_agree) {
        this.no_agree = no_agree;
    }
}
