package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/6/10.
 */

public class SearchResult {
    private List<SearchBean> Child;
    private List<TeacherCourseList> Childs;

    @Override
    public String toString() {
        return "SearchResult{" +
                "Child=" + Child +
                ", Childs=" + Childs +
                '}';
    }

    public List<SearchBean> getChild() {
        return Child;
    }

    public void setChild(List<SearchBean> child) {
        Child = child;
    }

    public List<TeacherCourseList> getChilds() {
        return Childs;
    }

    public void setChilds(List<TeacherCourseList> childs) {
        Childs = childs;
    }

    public SearchResult(List<SearchBean> child, List<TeacherCourseList> childs) {

        Child = child;
        Childs = childs;
    }
}
