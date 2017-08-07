package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RecommendTeacherResult {
    private List<RecommendTeacherModel>Childs;

    public RecommendTeacherResult() {
    }

    @Override
    public String toString() {
        return "RecommendTeacherResult{" +
                "Childs=" + Childs +
                '}';
    }

    public List<RecommendTeacherModel> getChilds() {
        return Childs;
    }

    public void setChilds(List<RecommendTeacherModel> childs) {
        Childs = childs;
    }
}
