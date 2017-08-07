package sochat.so.com.android.live.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.im.Preferences;
import sochat.so.com.android.live.im.UserPreferences;

public class WelcomeActivity extends AppCompatActivity {
    private Button mButton;
    private  int width;
    private  int height;

    private AbortableFuture<LoginInfo> loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_welcome);

        Preferences.saveUserAccount("111");
        Preferences.saveUserToken(tokenFromPassword("111woosii"));

mButton = (Button) findViewById(R.id.btn_pay);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//
//                        try {
//                            requestByCreateCampus();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
                login();

            }
        });


    }


    /**
     * ***************************************** 登录 **************************************
     */

    private void login() {
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = "111";
        final String token = tokenFromPassword("111woosii");
        // 登录
        loginRequest = NimUIKit.doLogin(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i("WS log", "login success");

                onLoginDone();

                DemoCache.setAccount(account);
                saveLoginInfo(account, token);

                // 初始化消息提醒配置
                initNotificationConfig();

                // 进入主界面
                startActivity(new Intent(WelcomeActivity.this,EnterLiveActivity.class));

                finish();
            }

            @Override
            public void onFailed(int code) {
                LogUtil.i("WS log", "login failed:"+code);
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(WelcomeActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WelcomeActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(WelcomeActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
//        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
//        if (statusBarNotificationConfig == null) {
//            statusBarNotificationConfig = DemoCache.getNotificationConfig();
//            UserPreferences.setStatusConfig(statusBarNotificationConfig);
//        }
        // 更新配置
//        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = "5db3cc39a09c4873b1b0ea4dd8f30135".equals(appKey)
                || "5db3cc39a09c4873b1b0ea4dd8f30135".equals(appKey);

        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }











    // Get方式请求
    public  void requestByGet() throws Exception {
        String path = "http://app.woosii.com/index.php/Api/SchoolManger/Hx_create_sch?user_id=111&sch_info=王双小哥&area=武汉&act=add";
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
//            byte[] data = readStream(urlConn.getInputStream());

            InputStream is = urlConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line =reader.readLine())!=null){
                sb.append(line);
            }

            String response = sb.toString();
            Log.i("WS log","response——create-compus创建校区:"+response);
            login();
        } else {

        }
        // 关闭连接
        urlConn.disconnect();
    }
    // Get方式请求
    public  void requestByCreateCampus() throws Exception {
        String ada = Utility.md5("wosii_888");
        Log.i("WS log","Utility.md5(\"wosii_888\")"+ada);
        String path = "http://app.woosii.com/index.php/Api/User/be_schoolmaster?code="+ ada+"&user_id=111";
        Log.i("WS log","code:"+path);
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
//            byte[] data = readStream(urlConn.getInputStream());

            InputStream is = urlConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line =reader.readLine())!=null){
                sb.append(line);
            }

            String response = sb.toString();
            JSONObject jsonObject = new JSONObject(response);
            Log.i("WS log","response成为校长:"+response);
            if (jsonObject.getInt("Code") ==1){
                requestByGet();
            }else {
                requestByGet();
            }

        } else {

        }
        // 关闭连接
        urlConn.disconnect();
    }












}
