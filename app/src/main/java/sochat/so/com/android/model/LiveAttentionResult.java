package sochat.so.com.android.model;

import java.util.List;

/**
 * Created by Administrator on 2017/8/5.
 */

public class LiveAttentionResult {
    private List<LiveAttrentionModel>child;

    @Override
    public String toString() {
        return "LiveAttentionResult{" +
                "child=" + child +
                '}';
    }

    public List<LiveAttrentionModel> getChild() {
        return child;
    }

    public void setChild(List<LiveAttrentionModel> child) {
        this.child = child;
    }

    public LiveAttentionResult(List<LiveAttrentionModel> child) {

        this.child = child;
    }
}
