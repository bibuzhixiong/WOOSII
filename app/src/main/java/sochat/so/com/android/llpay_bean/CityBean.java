package sochat.so.com.android.llpay_bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class CityBean {
    private String name ;
    private String encoding ;
    private List<String>area;

    @Override
    public String toString() {
        return this.name;
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

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public CityBean() {

    }
}
