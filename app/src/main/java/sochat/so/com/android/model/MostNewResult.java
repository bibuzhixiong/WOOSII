package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MostNewResult {
    private List<TeacherCourseList>Childs;

    @Override
    public String toString() {
        return "MostNewResult{" +
                "Childs=" + Childs +
                '}';
    }

    public MostNewResult() {
    }

    public List<TeacherCourseList> getChilds() {
        return Childs;
    }

    public void setChilds(List<TeacherCourseList> childs) {
        Childs = childs;
    }

    public MostNewResult(List<TeacherCourseList> childs) {

        Childs = childs;
    }
}
