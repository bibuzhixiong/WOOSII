package sochat.so.com.android.llpay_bean;

/**
 * Created by Administrator on 2017/5/18.
 */

public class Sign {
    private String sign;

    @Override
    public String toString() {
        return this.sign;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Sign(String sign) {

        this.sign = sign;
    }
}
