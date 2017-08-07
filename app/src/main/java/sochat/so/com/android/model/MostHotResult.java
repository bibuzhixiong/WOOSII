package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MostHotResult {
    private List<TeacherCourseList>Childs;

    @Override
    public String toString() {
        return "MostNewResult{" +
                "Childs=" + Childs +
                '}';
    }

    public List<TeacherCourseList> getChilds() {
        return Childs;
    }

    public void setChilds(List<TeacherCourseList> childs) {
        Childs = childs;
    }

    public MostHotResult() {
    }

    public MostHotResult(List<TeacherCourseList> childs) {

        Childs = childs;
    }
}
