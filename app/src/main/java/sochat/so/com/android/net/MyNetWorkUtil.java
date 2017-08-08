package sochat.so.com.android.net;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Novate;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import sochat.so.com.android.activity.LoginActivity;
import sochat.so.com.android.activity.MyMainActivity;
import sochat.so.com.android.activity.PlayVedioActivity;
import sochat.so.com.android.activity.TeacherInfoActivity;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.LoginCallBack;

/**
 * Created by Administrator on 2017/5/15.
 */

public class MyNetWorkUtil {
    public static Novate novate;
    public static MyApiService myAPI;

    public MyNetWorkUtil() {
    }

    public static MyApiService getMyApiService(Novate novate){
        if (myAPI ==null){
            myAPI = novate.create(MyApiService.class);
        }
        return myAPI;
    }

    public static void getNovate(final Activity activity, boolean ifNeedUid, final Map<String, String> headers, Map<String, Object> parameters, final boolean ifCache, final boolean ifCookie, final String BaseApi){
        CommonUtils.NeedLogin(activity, ifNeedUid, new LoginCallBack() {
            @Override
            public void cancel() {
                if (activity instanceof MyMainActivity ||activity instanceof PlayVedioActivity ||activity instanceof TeacherInfoActivity){

                }else{
                    activity.finish();
                }
            }

            @Override
            public void send() {
                Intent intent = new Intent(activity, LoginActivity.class);
                CommonUtils.startActivity(activity,intent);
            }

            @Override
            public void comeon() {
                novate = new Novate.Builder(activity)
                        .addHeader(headers)
                        .connectTimeout(10)
                        .addCookie(ifCookie)
                        .addCache(ifCache)
                        .baseUrl(BaseApi)
                        .addLog(true)
                        .build();

            }
        });
    }


    public static JSONObject getMyMothed(final Activity activity, String url, Map<String, Object> parameters, final JsonCallBack callBack){
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);

            //将map报文转为获得json对象
            JSONObject jsonObject =new JSONObject(parameters);
            MyNetWorkUtil.novate.get("/index.php/Vr/Lianlianpay/pub_fun", parameters,new BaseSubscriber<ResponseBody>(activity){

                @Override
                public void onNext(ResponseBody responseBody) {
                    try {
                        String jstr = new String(responseBody.bytes());

                        JSONObject jsonObject1 = new JSONObject(jstr);
                        callBack.backJson(jsonObject1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.i(ConfigInfo.TAG,"Throwable:"+e.getMessage());

                }
            });
        }

        return null;
    }


    public static JSONObject postMyMothed(final Activity activity, String url, Map<String, Object> parameters, final JsonCallBack callBack){
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);

            //将map报文转为获得json对象
            JSONObject jsonObject =new JSONObject(parameters);
            novate.post(url, parameters, new BaseSubscriber<ResponseBody>(activity) {

                @Override
                public void onError(Throwable e) {
                    Log.e("OkHttp", e.getMessage());
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(ResponseBody responseBody) {
                        try {
                        String jstr = new String(responseBody.bytes());
                            Log.i(ConfigInfo.TAG,"MyNetWorkUtil——postMyMothed："+jstr);
                            JSONObject jsonObject1 = new JSONObject(jstr);
                        callBack.backJson(jsonObject1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }

        return null;
    }



}
