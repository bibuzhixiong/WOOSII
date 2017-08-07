package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public class HomepageVedioResult {
    private List<RecommendTeacherModel>Childs_rec;
    private List<TeacherCourseList>Childs_new;
    private List<TeacherCourseList>Childs_hot;

    @Override
    public String toString() {
        return "HomepageVedioResult{" +
                "Childs_rec=" + Childs_rec +
                ", Childs_new=" + Childs_new +
                ", Childs_hot=" + Childs_hot +
                '}';
    }

    public List<RecommendTeacherModel> getChilds_rec() {
        return Childs_rec;
    }

    public void setChilds_rec(List<RecommendTeacherModel> childs_rec) {
        Childs_rec = childs_rec;
    }

    public List<TeacherCourseList> getChilds_new() {
        return Childs_new;
    }

    public void setChilds_new(List<TeacherCourseList> childs_new) {
        Childs_new = childs_new;
    }

    public List<TeacherCourseList> getChilds_hot() {
        return Childs_hot;
    }

    public void setChilds_hot(List<TeacherCourseList> childs_hot) {
        Childs_hot = childs_hot;
    }
}
