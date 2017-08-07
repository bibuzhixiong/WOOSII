package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17.
 */

public class CouurseInfoResult {
    private int PIndex;
    private int PSize;
    private int Count;
    private int PCount;
    private List<CourseChild>Childs;

    @Override
    public String toString() {
        return "CouurseInfoResult{" +
                "PIndex=" + PIndex +
                ", PSize=" + PSize +
                ", Count=" + Count +
                ", PCount=" + PCount +
                ", Childs=" + Childs +
                '}';
    }

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

    public List<CourseChild> getChilds() {
        return Childs;
    }

    public void setChilds(List<CourseChild> childs) {
        Childs = childs;
    }

    public CouurseInfoResult() {

    }

    public CouurseInfoResult(int PIndex, int PSize, int count, int PCount, List<CourseChild> childs) {

        this.PIndex = PIndex;
        this.PSize = PSize;
        Count = count;
        this.PCount = PCount;
        Childs = childs;
    }
}
