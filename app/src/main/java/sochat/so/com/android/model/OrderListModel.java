package sochat.so.com.android.model;

/**
 * Created by Administrator on 2017/6/20.
 */

public class OrderListModel {
    private String amt_bal;//记录总条数
    private String amt_inoccur;//收看金额
    private String date_acct;//账务日期
    private String amt_outoccur;//付款金额
    private String dt_billtrans;//交易时间
    private String flag_dc;//资金存取标识 0：入账  1：出账
    private String oid_biz;//业务类型
    private String oid_paybill;//

    public OrderListModel() {
    }

    public OrderListModel(String amt_bal, String amt_inoccur, String date_acct, String amt_outoccur, String dt_billtrans, String flag_dc, String oid_biz, String oid_paybill) {
        this.amt_bal = amt_bal;
        this.amt_inoccur = amt_inoccur;
        this.date_acct = date_acct;
        this.amt_outoccur = amt_outoccur;
        this.dt_billtrans = dt_billtrans;
        this.flag_dc = flag_dc;
        this.oid_biz = oid_biz;
        this.oid_paybill = oid_paybill;
    }

    public String getAmt_bal() {
        return amt_bal;
    }

    public void setAmt_bal(String amt_bal) {
        this.amt_bal = amt_bal;
    }

    public String getAmt_inoccur() {
        return amt_inoccur;
    }

    public void setAmt_inoccur(String amt_inoccur) {
        this.amt_inoccur = amt_inoccur;
    }

    public String getDate_acct() {
        return date_acct;
    }

    public void setDate_acct(String date_acct) {
        this.date_acct = date_acct;
    }

    public String getAmt_outoccur() {
        return amt_outoccur;
    }

    public void setAmt_outoccur(String amt_outoccur) {
        this.amt_outoccur = amt_outoccur;
    }

    public String getDt_billtrans() {
        return dt_billtrans;
    }

    public void setDt_billtrans(String dt_billtrans) {
        this.dt_billtrans = dt_billtrans;
    }

    public String getFlag_dc() {
        return flag_dc;
    }

    public void setFlag_dc(String flag_dc) {
        this.flag_dc = flag_dc;
    }

    public String getOid_biz() {
        return oid_biz;
    }

    public void setOid_biz(String oid_biz) {
        this.oid_biz = oid_biz;
    }

    public String getOid_paybill() {
        return oid_paybill;
    }

    public void setOid_paybill(String oid_paybill) {
        this.oid_paybill = oid_paybill;
    }

    @Override
    public String toString() {
        return "OrderListModel{" +
                "amt_bal='" + amt_bal + '\'' +
                ", amt_inoccur='" + amt_inoccur + '\'' +
                ", date_acct='" + date_acct + '\'' +
                ", amt_outoccur='" + amt_outoccur + '\'' +
                ", dt_billtrans='" + dt_billtrans + '\'' +
                ", flag_dc='" + flag_dc + '\'' +
                ", oid_biz='" + oid_biz + '\'' +
                ", oid_paybill='" + oid_paybill + '\'' +
                '}';
    }
}
