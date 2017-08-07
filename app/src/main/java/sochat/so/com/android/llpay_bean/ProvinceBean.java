package sochat.so.com.android.llpay_bean;

import com.bigkoo.pickerview.model.IPickerViewData;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class ProvinceBean implements IPickerViewData {
    private String name ;
    private String encoding ;
    private List<CityBean> city;

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "name='" + name + '\'' +
                ", encoding='" + encoding + '\'' +
                ", city=" + city +
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

    public List<CityBean> getCity() {
        return city;
    }

    public void setCity(List<CityBean> city) {
        this.city = city;
    }

    public ProvinceBean() {

    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。

    @Override
    public String getPickerViewText() {
        return this.name;
    }
}
