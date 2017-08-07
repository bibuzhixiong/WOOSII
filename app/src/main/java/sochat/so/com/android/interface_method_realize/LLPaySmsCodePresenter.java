package sochat.so.com.android.interface_method_realize;

import android.app.Activity;

import org.json.JSONObject;

import java.util.Map;

import sochat.so.com.android.interface_method.LLPresenterCallBack;
import sochat.so.com.android.interface_method.ILLPaySmsCodeModel;
import sochat.so.com.android.interface_method.ILLPaySmsCodePresenter;
import sochat.so.com.android.interface_method.ILLPaySmsCodeView;
import sochat.so.com.android.interface_method.LLModelCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;

/**
 * Created by Administrator on 2017/5/13.
 */

public class LLPaySmsCodePresenter implements ILLPaySmsCodePresenter {
    private ILLPaySmsCodeModel model;
    private ILLPaySmsCodeView view;

    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new MyHashMap<>();

    public LLPaySmsCodePresenter(ILLPaySmsCodeView view) {
        this.model = new LLPaySmsCodeModel();
        this.view = view;
    }

    @Override
    public void getPresenterMothed(final Activity activity, final LLPresenterCallBack callBack) {
        model.getSmsCode(activity, view.getContent(), new LLModelCallBack() {
            @Override
            public void success(JSONObject jsonObject) {
                callBack.getJson(jsonObject);
            }

            @Override
            public void failed() {

            }
        });
    }

}
