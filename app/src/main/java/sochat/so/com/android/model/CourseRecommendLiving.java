package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/7/28.
 */

public class CourseRecommendLiving {
    private int PIndex;
    private int PSize;
    private int Count;
    private int PCount;
    private List<RecommentLivingModel> Childs;

    public int getPIndex() {
        return PIndex;
    }

    public void setPIndex(int PIndex) {
        this.PIndex = PIndex;
    }

    public int getPSize() {
        return PSize;
    }

    public void setPSize(int PSize) {
        this.PSize = PSize;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public int getPCount() {
        return PCount;
    }

    public void setPCount(int PCount) {
        this.PCount = PCount;
    }

    public List<RecommentLivingModel> getChilds() {
        return Childs;
    }

    public void setChilds(List<RecommentLivingModel> childs) {
        Childs = childs;
    }

    @Override
    public String toString() {
        return "CourseRecommendLiving{" +
                "PIndex=" + PIndex +
                ", PSize=" + PSize +
                ", Count=" + Count +
                ", PCount=" + PCount +
                ", Childs=" + Childs +
                '}';
    }
}
