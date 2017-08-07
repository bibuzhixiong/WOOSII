package sochat.so.com.android.net;

/**
 * Created by Tamic on 2016-06-03.
 */
public class IpResult {
    @Override
    public String toString() {
        return "IpResult{" +
                "code='" + code + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public IpResult(int code) {

        this.code = code;
    }

    private int code ;
}
