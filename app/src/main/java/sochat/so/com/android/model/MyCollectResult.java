package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/3/22.
 */

public class MyCollectResult {
    private String PIndex;
    private String PSize;
    private int Count;
    private String PCount;
    private List<TeacherCourseList>Childs;

    public MyCollectResult(String PIndex, String PSize, int count, String PCount, List<TeacherCourseList> childs) {
        this.PIndex = PIndex;
        this.PSize = PSize;
        Count = count;
        this.PCount = PCount;
        Childs = childs;
    }

    @Override
    public String toString() {
        return "MyCollectResult{" +
                "PIndex='" + PIndex + '\'' +
                ", PSize='" + PSize + '\'' +
                ", Count='" + Count + '\'' +
                ", PCount='" + PCount + '\'' +
                ", Childs=" + Childs +
                '}';
    }

    public String getPIndex() {
        return PIndex;
    }

    public void setPIndex(String PIndex) {
        this.PIndex = PIndex;
    }

    public String getPSize() {
        return PSize;
    }

    public void setPSize(String PSize) {
        this.PSize = PSize;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getPCount() {
        return PCount;
    }

    public void setPCount(String PCount) {
        this.PCount = PCount;
    }

    public List<TeacherCourseList> getChilds() {
        return Childs;
    }

    public void setChilds(List<TeacherCourseList> childs) {
        Childs = childs;
    }

    public MyCollectResult() {

    }
}
