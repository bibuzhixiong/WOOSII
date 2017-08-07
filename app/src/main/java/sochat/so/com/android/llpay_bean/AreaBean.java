package sochat.so.com.android.llpay_bean;

/**
 * Created by Administrator on 2017/5/18.
 */

public class AreaBean {
   private String name ;
    private String encoding ;

    @Override
    public String toString() {
        return "AreaBean{" +
                "name='" + name + '\'' +
                ", encoding='" + encoding + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public AreaBean() {

    }
}
