package sochat.so.com.android.llpay_bean;

/**
 * Created by Administrator on 2017/5/15.
 */

public class LLPaySmsCodeBeanSign {
    private String oid_partner;
    private String sign_type;
    private String user_id;
    private String mob_bind;
    private String sign;

    @Override
    public String toString() {
        return "LLPaySmsCodeBeanSign{" +
                "oid_partner='" + oid_partner + '\'' +
                ", sign_type='" + sign_type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", mob_bind='" + mob_bind + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public LLPaySmsCodeBeanSign(String oid_partner, String sign_type, String user_id, String mob_bind, String sign) {

        this.oid_partner = oid_partner;
        this.sign_type = sign_type;
        this.user_id = user_id;
        this.mob_bind = mob_bind;
        this.sign = sign;
    }

    public String getOid_partner() {
        return oid_partner;
    }

    public void setOid_partner(String oid_partner) {
        this.oid_partner = oid_partner;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMob_bind() {
        return mob_bind;
    }

    public void setMob_bind(String mob_bind) {
        this.mob_bind = mob_bind;
    }

    public LLPaySmsCodeBeanSign() {

    }

    public LLPaySmsCodeBeanSign(String oid_partner, String sign_type, String user_id, String mob_bind) {

        this.oid_partner = oid_partner;
        this.sign_type = sign_type;
        this.user_id = user_id;
        this.mob_bind = mob_bind;
    }
}
