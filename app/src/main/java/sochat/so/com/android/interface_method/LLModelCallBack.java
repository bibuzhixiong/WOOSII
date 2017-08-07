package sochat.so.com.android.interface_method;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/15.
 */

public interface LLModelCallBack {
    void success(JSONObject jsonObject);
    void failed();
}
