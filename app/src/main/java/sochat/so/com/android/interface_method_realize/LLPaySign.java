package sochat.so.com.android.interface_method_realize;


import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Subscriber;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.interface_method.ILLPaySign;
import sochat.so.com.android.net.MyApiService;
import sochat.so.com.android.net.MyNetWorkUtil;

/**
 * Created by Administrator on 2017/5/18.
 */

public class LLPaySign {
    private static Map<String, Object> parameters = new HashMap<String, Object>();

    public static void getSign(final Activity activity, Map<String, Object> parameters,final ILLPaySign illPaySign){
        String sign;
        //添加到map准备转为json报文
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        MyNetWorkUtil.getNovate(activity, ConfigInfo.NO_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE, ConfigInfo.ApiUrl);
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);

            //将map报文转为获得json对象
            JSONObject jsonObject =new JSONObject(parameters);
            Log.i(ConfigInfo.TAG,"LLPaySign_json:"+jsonObject.toString());
            MyNetWorkUtil.novate.json("/index.php/Vr/Llpay/llsigns", jsonObject.toString(), new Subscriber<ResponseBody>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.i(ConfigInfo.TAG,"LLPaySign_onError():"+e.getMessage());
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                    try {
                        String jstr = new String(responseBody.bytes());

                        Log.i(ConfigInfo.TAG,"LLPaySign_jstr:"+jstr);
                        JSONObject jsonObject1 = new JSONObject(jstr);
                        illPaySign.getSign(jsonObject1.getString("sign"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            });
        }

    }

}
