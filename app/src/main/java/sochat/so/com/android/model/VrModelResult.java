package sochat.so.com.android.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/6.
 */

public class VrModelResult {
    private int PIndex;
    private int PSize;
    private int Count;
    private int PCount;
    private ArrayList<Child> Childs;

    public VrModelResult() {
    }

    public VrModelResult(int PIndex, int PSize, int count, int PCount, ArrayList<Child> childs) {
        this.PIndex = PIndex;
        this.PSize = PSize;
        Count = count;
        this.PCount = PCount;
        Childs = childs;
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

    public ArrayList<Child> getChilds() {
        return Childs;
    }

    public void setChilds(ArrayList<Child> childs) {
        Childs = childs;
    }

    @Override
    public String toString() {
        return "VrModelResult{" +
                "PIndex=" + PIndex +
                ", PSize=" + PSize +
                ", Count=" + Count +
                ", PCount=" + PCount +
                ", Childs=" + Childs +
                '}';
    }
}
