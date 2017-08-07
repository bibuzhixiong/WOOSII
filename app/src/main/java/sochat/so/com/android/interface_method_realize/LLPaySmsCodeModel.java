package sochat.so.com.android.interface_method_realize;

import android.app.Activity;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.interface_method.ILLPaySmsCodeModel;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.interface_method.LLModelCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/5/13.
 */

public class LLPaySmsCodeModel implements ILLPaySmsCodeModel {
    //验证码的参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    //签名的参数
    private Map<String, Object> sign_parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new MyHashMap<>();
//    private LLPaySmsCodeBeanSign llSign;


    @Override
    public void getSmsCode(final Activity activity, final String mob_bind, final LLModelCallBack callBack) {

//        Map<String ,String>headers = new MyHashMap<>();
//        Map<String ,Object>parameters = new MyHashMap<>();
//        Map<String ,Object> sign_parameters = new MyHashMap<>();
        headers.put("Accept", "application/json");

        sign_parameters.put("oid_partner",ConfigInfo.QUICK_WALLET_OID_PARTNER);
        sign_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        sign_parameters.put("user_id", DemoHelper.getUid());
        sign_parameters.put("mob_bind", mob_bind);

        Log.i(ConfigInfo.TAG,"李杰:"+sign_parameters.toString());
        parameters.put("data",sign_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_SMSSEND);

        Log.i(ConfigInfo.TAG,"parameters:"+parameters.toString());

                MyNetWorkUtil.getNovate(activity, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
                MyNetWorkUtil.getMyMothed(activity, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
                    @Override
                    public void backJson(JSONObject jsonObject) {
                        callBack.success(jsonObject);
                    }
                });

            }
}
