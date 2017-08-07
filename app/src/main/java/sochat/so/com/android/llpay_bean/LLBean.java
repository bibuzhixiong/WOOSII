package sochat.so.com.android.llpay_bean;

/**
 * Created by Administrator on 2017/5/22.
 */

public class LLBean {
    private String ret_code;
    private String ret_msg;
    private String token;
    private String sign_type;

    @Override
    public String toString() {
        return "LLBean{" +
                "ret_code='" + ret_code + '\'' +
                ", ret_msg='" + ret_msg + '\'' +
                ", token='" + token + '\'' +
                ", sign_type='" + sign_type + '\'' +
                '}';
    }

    public String getRet_code() {
        return ret_code;
    }

    public void setRet_code(String ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
}
