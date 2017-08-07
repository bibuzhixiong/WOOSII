package sochat.so.com.android.llpay_bean;

import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */

public class ProvinceResult {
    private List<ProvinceBean> province;

    public ProvinceResult() {
    }

    @Override
    public String toString() {
        return "ProvinceResult{" +
                "province=" + province +
                '}';
    }

    public List<ProvinceBean> getProvince() {
        return province;
    }

    public void setProvince(List<ProvinceBean> province) {
        this.province = province;
    }

    public ProvinceResult(List<ProvinceBean> province) {

        this.province = province;
    }
}
