package sochat.so.com.android.model;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * Created by Administrator on 2017/4/17.
 */

public class CityModel extends BaseIndexPinyinBean {
    private String region_id ;
    private String city;//城市名字
    private boolean isTop;//是否是最上面的 不需要被转化成拼音的

    public CityModel() {
    }

    public CityModel(String region_id, String city) {
        this.region_id = region_id;
        this.city = city;
    }

    public CityModel(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public CityModel setCity(String city) {
        this.city = city;
        return this;
    }

    @Override
    public String toString() {
        return "CityModel{" +
                "region_id='" + region_id + '\'' +
                ", city='" + city + '\'' +
                ", isTop=" + isTop +
                '}';
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public boolean isTop() {
        return isTop;
    }

    public CityModel setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public String getTarget() {
        return city;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
