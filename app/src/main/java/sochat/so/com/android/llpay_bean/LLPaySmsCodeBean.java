package sochat.so.com.android.llpay_bean;

/**
 * Created by Administrator on 2017/5/15.
 */

public class LLPaySmsCodeBean {
    private String ret_code;
    private String ret_msg;

    @Override
    public String toString() {
        return "LLPaySmsCodeModel{" +
                "ret_code='" + ret_code + '\'' +
                ", ret_msg='" + ret_msg + '\'' +
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

    public LLPaySmsCodeBean(String ret_code, String ret_msg) {

        this.ret_code = ret_code;
        this.ret_msg = ret_msg;
    }
}
