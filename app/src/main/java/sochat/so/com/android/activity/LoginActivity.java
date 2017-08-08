package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.RegisterSkipEvent;
import sochat.so.com.android.eventbus.UpdateJPush;
import sochat.so.com.android.eventbus.UpdateUI;
import sochat.so.com.android.model.WeiXinBean;
import sochat.so.com.android.model.WeixinUserInfoBean;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/2/24.
 * 登录界面
 */

public class LoginActivity extends BaseActivity {
    @Bind(R.id.top_back_forget)
    ImageView ivArrow;
    @Bind(R.id.main_top_text_center)
    TextView mainTopTextCenter;
    @Bind(R.id.main_top_text_rignt)
    TextView mainTopTextRignt;
    @Bind(R.id.et_login_phoneNumber)
    EditText etPhoneUsername;
    @Bind(R.id.et_login_phonePassword)
    EditText etInputPassword;
    @Bind(R.id.tv_login_forget_password)
    TextView tvLoginForgetPassword;
    @Bind(R.id.rb_login_remember_password)
    TextView rbRemenberPassword;
    @Bind(R.id.tv_login)
    TextView tvLogin;
    @Bind(R.id.rb_woosii_login_agreement)
    TextView rbWoosiiLoginAgreement;
    @Bind(R.id.iv_if_select)
    ImageView ivIfSelect;
    @Bind(R.id.iv_if_select_remember)
    ImageView ivIfSelectRemember;
    @Bind(R.id.woosii_login_user_agreement)
    TextView woosiiLoginUserAgreement;
    @Bind(R.id.ll_text_threeLogin)
    LinearLayout llTextThreeLogin;
    @Bind(R.id.login_weixin_login)
    ImageView loginWeixinLogin;

    /**
     用来记录是否同意woosii协议内容的状态
     */
    private boolean isAgreeWoosiiAgreement = false;
    /**
     * 用来记录是否记住密码
     */
    private boolean isRememberPassword = false;
    /**
     * 微信API
     */
    public static IWXAPI api;
    //微信WX_CODE
    public static String WX_CODE = "";
    public static int WX_BACK_CODE = -5;
    private String nickname = "";
    private String source = "";
    private String aite_id = "";
    private String open_id="";
    private String headimgurl;
    /**
     * 线程
     */
    public Thread thread;
    /**
     * 时间
     */
    private long exitTime = 0;
    /**
     * 微信登陆
     */
    public static boolean isWXLogin = false;
    public static LoginActivity loginActivity;
    /**
     * 提示框
     */
    private ProgressDialog progressDialog=new ProgressDialog();
    private MyToast toast;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what){
                //手机登录
                case 0:
                    String url = ConfigInfo.LOGIN_URL+etPhoneUsername.getText().toString().trim()+"&password="+ Utility.md5(etInputPassword.getText().toString().trim());
                    Log.i(ConfigInfo.TAG,"手机登录url="+url);
                    HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                Log.i(ConfigInfo.TAG,"code_"+code);
                                if(code ==4){
                                    DemoHelper.setUid(jsonObject.getString("user_id"));
                                    DemoHelper.setUserType(jsonObject.getString("user_type"));
                                    DemoHelper.setRegion_id(jsonObject.getString("region_id"));
                                    DemoHelper.setArea(jsonObject.getString("region_name"));
                                    Log.i(ConfigInfo.TAG,"handler"+2);
                                handler.sendEmptyMessage(2 );//登录成功进行跳转
                                }else if (code == 3){//号码不存在,请注册
                                    Log.i(ConfigInfo.TAG,"handler"+14);
                                handler.sendEmptyMessage(14);
                                }else if (code == 5){//请输入正确的密码
                                    Log.i(ConfigInfo.TAG,"handler"+3);
                                    handler.sendEmptyMessage(3);
                                }else{
                                    Log.i(ConfigInfo.TAG,"handler"+4);
                                    handler.sendEmptyMessage(4);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
//                                handler.sendEmptyMessage(4);
                            }
                        }
                    });
                    progressDialog.destroy();
                    break;
                //微信登录
                case 1:
                    break;
                case 2:
                    DemoHelper.setPhone(etPhoneUsername.getText().toString().trim());
                    toast.makeShortToast(LoginActivity.this,"登录成功");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new UpdateUI());//这里用不着
                            EventBus.getDefault().post(new UpdateJPush());
                            loginActivity.finish();
                        }
                    },2000);//登录成功后，过两秒将Activity关闭掉

                    break;

                case 3:
                    toast.makeShortToast(LoginActivity.this,"账号密码不匹配");
                    break;

                case 4:
                    toast.makeShortToast(LoginActivity.this,"登录异常，请重试");
                    break;
                case 5:
                    toast.makeShortToast(LoginActivity.this,"微信登录异常，请重试");
                    break;
                case 6:
                    toast.makeShortToast(LoginActivity.this,"微信授权失败，请重试");
                    break;
                case 7:
                    toast.makeShortToast(LoginActivity.this,"微信授权失败，请重试");
                    break;
                case 8:
                    toast.makeShortToast(LoginActivity.this,"微信授权失败，请重试");
                    break;
                case 9:
                    toGetUid();
                break;
                case 10:
                    toast.makeShortToast(LoginActivity.this,"微信授权取消");
                    break;
                case 11://现在开始判断是否绑定
                    intent = new Intent(LoginActivity.this,WXBindPhoneActivity.class);
                    CommonUtils.startActivity(LoginActivity.this,intent);
                    break;
                case 12://微信登陆绑定了，保存信息，然后开始跳转登录
                    toast.makeShortToast(LoginActivity.this,"登录成功");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new UpdateUI());
                            loginActivity.finish();
                        }
                    },2000);//登录成功后，过两秒将Activity关闭掉

                    break;
                case 14:
                    toast.makeShortToast(LoginActivity.this,"号码不存在,请注册");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        mShareAPI = UMShareAPI.get( this );
        init();
        setListeners();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FromRegisterToLogin(RegisterSkipEvent event){
        String username = event.getUsername();
        String password = event.getPassword();
        etPhoneUsername.setText(username);
        etInputPassword.setText(password);
        phonelogin();

    }

    private void init() {
        ivArrow = (ImageView) findViewById(R.id.top_back_forget);
//        api = WXAPIFactory.createWXAPI(this, ConfigInfo.APP_ID, true);
        loginActivity=this;

        //隐藏顶部返回键
        ivArrow.setVisibility(View.VISIBLE);
        mainTopTextRignt.setTextColor(0xFF000000);
    }

    private void setListeners() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        mShareAPI.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //跳转到忘记密码界面的回调
            case 0:
                if(resultCode ==RESULT_OK){
                    Bundle  bundle = data.getBundleExtra("data_return_ForgetPasswordActivity");
                    etPhoneUsername.setText(bundle.getString("usr")+"");
                    etInputPassword.setText(bundle.getString("pwd")+"");
                }
                etPhoneUsername.setSelection(etPhoneUsername.getText().toString().length());
                etInputPassword.setSelection(etInputPassword.getText().toString().length());
                break;
            //跳转到注册界面的回调
            case 1:
                if(resultCode ==RESULT_OK){
                    Bundle  bundle = data.getBundleExtra("data_return_RegisterInterface");
                    etPhoneUsername.setText(bundle.getString("usr")+"");
                    etInputPassword.setText(bundle.getString("pwd")+"");
                }
                etPhoneUsername.setSelection(etPhoneUsername.getText().toString().length());
                etInputPassword.setSelection(etInputPassword.getText().toString().length());
                break;
        }
    }

    @OnClick({R.id.top_back_forget, R.id.tv_login_forget_password, R.id.rb_login_remember_password, R.id.tv_login, R.id.rb_woosii_login_agreement, R.id.woosii_login_user_agreement, R.id.login_weixin_login,R.id.main_top_text_rignt})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.top_back_forget:
                LoginActivity.this.finish();
                break;
            case R.id.main_top_text_rignt:
                intent = new Intent(LoginActivity.this,SelectRegisterIDActivity.class);
//                startActivityForResult(intent,1);
                CommonUtils.startActivity(LoginActivity.this,intent);
                break;
            case R.id.tv_login_forget_password:
                intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                intent.putExtra("to_change_title","忘记密码");
                startActivityForResult(intent,0);
                CommonUtils.startActivity(LoginActivity.this,intent);
                break;
            case R.id.rb_login_remember_password:
                if(!isRememberPassword){
                    ivIfSelectRemember.setImageResource(R.drawable.read_woosii_agreement);
                    DemoHelper.setRemenberPassword(true);
                    isRememberPassword = false;
                }else{
                    ivIfSelectRemember.setImageResource(R.drawable.unread_woosii_agreement);
                    DemoHelper.setRemenberPassword(false);
                    isRememberPassword = true;
                }
                break;
            case R.id.tv_login:
                phonelogin();
                break;
            case R.id.rb_woosii_login_agreement:
                if(!isAgreeWoosiiAgreement){
                    DemoHelper.setDefaultAgreeAgreement(true);
                    ivIfSelect.setImageResource(R.drawable.read_woosii_agreement);
                    tvLogin.setEnabled(true);
                    isAgreeWoosiiAgreement = true;
                }else{
                    DemoHelper.setDefaultAgreeAgreement(false);
                    ivIfSelect.setImageResource(R.drawable.unread_woosii_agreement);
                    tvLogin.setEnabled(false);
                    isAgreeWoosiiAgreement = false;
                }
                break;

            case R.id.iv_if_select:
                if(!isAgreeWoosiiAgreement){
                    DemoHelper.setDefaultAgreeAgreement(true);
                    ivIfSelect.setImageResource(R.drawable.read_woosii_agreement);
                    tvLogin.setEnabled(true);
                }else{
                    DemoHelper.setDefaultAgreeAgreement(false);
                    ivIfSelect.setImageResource(R.drawable.unread_woosii_agreement);
                    tvLogin.setEnabled(false);
                }
                break;
            case R.id.woosii_login_user_agreement:
                intent=new Intent(LoginActivity.this, WosiiUserProblem.class);
                CommonUtils.startActivity(LoginActivity.this, intent);
                break;
            case R.id.login_weixin_login:
                loginWX();
                break;
        }
    }

    private void toGetUid() {
        try {
            String wxBindUrl = ConfigInfo.ApiUrl+"index.php/Api/User/auth_login?nickname="
                    +(nickname!=null? URLEncoder.encode(nickname,"UTF-8"):nickname)
                    +"&source="+source
                    +"&aite_id="+aite_id
                    +"&open_id="+open_id
                    +"&type=android";

            Log.i(ConfigInfo.TAG,"wxBindUrl:"+wxBindUrl);
            HttpUtils.doGetAsyn(null, false, wxBindUrl, handler, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    try {
                        Log.i(ConfigInfo.TAG,"toGetUid:"+result);
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("Code");
                        String uid = jsonObject.getString("Uid");
                        DemoHelper.setUid(uid);
                        if (code ==3){//没有绑定
                            handler.sendEmptyMessage(11);
                        }else if (code ==1){//已经绑定,保存信息跳转回主界面
                            String HXpassword = jsonObject.getString("hxpassword");
                            String HXusername = jsonObject.getString("hxusername");
                            String phone = jsonObject.getString("phone");
                            DemoHelper.setPhone(phone);
                            DemoHelper.setHXpassword(HXpassword);
                            DemoHelper.setHXusername(HXusername);
                            handler.sendEmptyMessage(12);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机登录
     */
    private void phonelogin() {
        if(!CommonUtils.checkPhoneNumber(etPhoneUsername.getText().toString().trim()) ==true){
            MyToast.showShortText(getApplicationContext(), "请输入正确的手机号");
//            toast.makeText(this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!CommonUtils.checkPhonePassword(etInputPassword.getText().toString().trim()) ==true){
            toast.makeShortToast(LoginActivity.this,"请输入正确的密码");
            return;
        }

        CommonUtils.showDialogs("加载中...",this, progressDialog);

        handler.sendEmptyMessage(0);//跳转去访问登录接口

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isWXLogin){
            loadWXUserInfo();
        }
        if(WX_BACK_CODE!=0){
            progressDialog.destroy();
        }
    }


//    private UMShareAPI mShareAPI;



private void loadWXUserInfo() {
    thread=new Thread(new Runnable() {
        public void run() {
            String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + ConfigInfo.APP_ID + "&secret=" + ConfigInfo.AppSecret + "&code=" + WX_CODE + "&grant_type=authorization_code";
            Log.i("Gale log", "accessTokenUrl="+accessTokenUrl);
            //String tokenResult = HttpUtil.httpsGet(accessTokenUrl);
            String tokenResult = HttpUtils.doGet(accessTokenUrl, handler);
            Log.i("Gale log", "tokenResult="+tokenResult);
            //tokenResult存在则获取acess_token，openid，然后再次请求http，获得用户的信息
            if (null != tokenResult) {
                try {
                    JSONObject object=new JSONObject(tokenResult);
                    WeiXinBean bean =new WeiXinBean();
                    bean.setAccess_token(object.getString("access_token"));
                    bean.setExpires_in(object.getInt("expires_in"));
                    bean.setOpenid(object.getString("openid"));
                    bean.setRefresh_token(object.getString("refresh_token"));
                    bean.setScope(object.getString("scope"));
                    //获取用户信息的json字符串
                    String userUrl ="https://api.weixin.qq.com/sns/userinfo?access_token="+bean.getAccess_token()+"&openid="+bean.getOpenid();
                    HttpUtils.doGetAsyn(null,false,userUrl, handler, new HttpUtils.CallBack() {
                        public void onRequestComplete(String result) {
                            Log.i("Gale log","微信登录="+result );
                            try {
                                //解析成bean
                                JSONObject jsonObject=new JSONObject(result);
                                WeixinUserInfoBean userInfoBean =new WeixinUserInfoBean();
                                userInfoBean.setCity(jsonObject.getString("city"));
                                userInfoBean.setCountry(jsonObject.getString("country"));
                                userInfoBean.setHeadimgurl(jsonObject.getString("headimgurl"));
                                userInfoBean.setNickname(jsonObject.getString("nickname"));
                                userInfoBean.setProvince(jsonObject.getString("province"));
                                userInfoBean.setOpenid(jsonObject.getString("openid"));
                                userInfoBean.setUnionid(jsonObject.getString("unionid"));
                                userInfoBean.setSex(jsonObject.getInt("sex"));
                                if(userInfoBean !=null){
                                    //微信的昵称和头像
                                    nickname = userInfoBean.getNickname();
                                    //Log.i("Gale log","用户名="+nickname);
                                    source="weixin";
                                    aite_id=userInfoBean.getUnionid();
                                    open_id=userInfoBean.getOpenid();
                                    headimgurl = userInfoBean.getHeadimgurl();
                                    DemoHelper.setWXThumbUrl(headimgurl);
                                DemoHelper.setWXNickName(nickname);
                                DemoHelper.setWXAite_id(aite_id);
                                DemoHelper.setWXOpen_id(open_id);
                                DemoHelper.setWXSource(source);

                                    handler.sendEmptyMessage(9);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(6);//授权失败
                            }
                            progressDialog.destroy();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });
    thread.start();
    isWXLogin = false;
}

    public void loginWX()
    {
        //注册到微信
        api = WXAPIFactory.createWXAPI(this,ConfigInfo.APP_ID,true);
        api.registerApp(ConfigInfo.APP_ID);

//        登录过程
        isWXLogin = true;
        CommonUtils.showDialogs("加载中...", this, progressDialog);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        api.sendReq(req);
    }
}
