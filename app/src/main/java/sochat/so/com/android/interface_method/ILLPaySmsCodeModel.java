package sochat.so.com.android.interface_method;

import android.app.Activity;

/**
 * Created by Administrator on 2017/5/13.
 */

public interface ILLPaySmsCodeModel {
    void getSmsCode(Activity activity, String smscode, LLModelCallBack callBack);
}
