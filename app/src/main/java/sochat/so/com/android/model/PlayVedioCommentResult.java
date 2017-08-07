package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/21.
 */

public class PlayVedioCommentResult {
    private int PIndex;
    private int PSize;
    private int Count;
    private int PCount;
    private List<PlayVedioComment>Childs;

    @Override
    public String toString() {
        return "PlayVedioCommentResult{" +
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

    public List<PlayVedioComment> getChilds() {
        return Childs;
    }

    public void setChilds(List<PlayVedioComment> childs) {
        Childs = childs;
    }

    public PlayVedioCommentResult() {

    }

    public PlayVedioCommentResult(int PIndex, int PSize, int count, int PCount, List<PlayVedioComment> childs) {

        this.PIndex = PIndex;
        this.PSize = PSize;
        Count = count;
        this.PCount = PCount;
        Childs = childs;
    }
}
