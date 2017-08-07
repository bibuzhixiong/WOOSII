package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */

public class PanoramaResult {

    private List<PanoramaModel> child;
    private int PIndex;
    private int PSize;
    private int PCount;
    private int Count;

    public PanoramaResult() {
    }

    @Override
    public String toString() {
        return "PanoramaResult{" +
                "child=" + child +
                ", PIndex=" + PIndex +
                ", PSize=" + PSize +
                ", PCount=" + PCount +
                ", Count='" + Count + '\'' +
                '}';
    }

    public List<PanoramaModel> getChild() {
        return child;
    }

    public void setChild(List<PanoramaModel> child) {
        this.child = child;
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

    public int getPCount() {
        return PCount;
    }

    public void setPCount(int PCount) {
        this.PCount = PCount;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
