package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/12.
 */

public class RechargeTimeResult {
    private int min;
    private List<RechargeTimeModel> childs;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public List<RechargeTimeModel> getChilds() {
        return childs;
    }

    public void setChilds(List<RechargeTimeModel> childs) {
        childs = childs;
    }

    public RechargeTimeResult(int min, List<RechargeTimeModel> childs) {
        this.min = min;
        childs = childs;
    }

    @Override
    public String toString() {
        return "RechargeTimeResult{" +
                "min=" + min +
                ", Childs=" + childs +
                '}';
    }
}
