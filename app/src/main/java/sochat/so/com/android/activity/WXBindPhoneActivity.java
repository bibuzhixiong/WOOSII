package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import sochat.so.com.android.app.MyApplication;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.BackCityEvent;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/2/24.
 * 注册界面
 */

public class WXBindPhoneActivity extends BaseActivity {
    @Bind(R.id.top_back_forget)
    ImageView ivTopBack;
    @Bind(R.id.main_top_text_center)
    TextView tvTitle;
    @Bind(R.id.main_top_text_rignt)
    TextView tvTopTextRignt;
    @Bind(R.id.et_register_phoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.et_register_captcha)
    EditText etCaptcha;
    @Bind(R.id.et_register_city)
    EditText etCity;
    @Bind(R.id.tv_register_captcha)
    TextView tvRegisterCaptcha;
    @Bind(R.id.et_register_input_phonePassword)
    EditText etPassword;
    @Bind(R.id.iv_register_eyes)
    ImageView ivEyes;
    @Bind(R.id.tv_register)
    TextView tvRegister;
    @Bind(R.id.rb_woosii_register_agreement)
    RadioButton rbWoosiiAgreement;
    @Bind(R.id.tv_woosii_register_user_agreement)
    TextView tvWoosiiUserAgreement;
    /**
     * 计时器
     */
    private TimeCount timeCount;
    /**
     * 是否隐藏密码
     */
    private boolean isHidePassword =false;
    /**
     * 吐司
     */
    private MyToast toast;
    public static WXBindPhoneActivity registerActivity;
    /**
     * 提示框
     */
    private ProgressDialog progressDialog = new ProgressDialog();

    /**
     * 城市的id
     */
    private String region_id;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    String url = ConfigInfo.WXBIND_URL+DemoHelper.getUid()
                            +"&phone="+etPhoneNumber.getText().toString().trim()
                            +"&password="+ Utility.md5(etPassword.getText().toString().trim())
                            +"&vcode="+Utility.md5(etCaptcha.getText().toString().trim())
                            +"&region_id="+region_id;
                    Log.i(ConfigInfo.TAG,"微信开始绑定url:"+url);
                    HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                Log.i(ConfigInfo.TAG,"微信开始绑定:"+result);
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("type");
                                if (code ==1){//参数错误
                                    handler.sendEmptyMessage(15);
                                }else if (code == 3) {//出错
                                    handler.sendEmptyMessage(1);
                                } else if (code == 4) {//成功
                                    handler.sendEmptyMessage(2);
                                }  else if (code == 5) {//失败
                                    handler.sendEmptyMessage(13);
                                }else if (code == 6){//验证码错误
                                    handler.sendEmptyMessage(4);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(5);
                            }
                        }
                    });

                    break;
                case 1://绑定失败
                    toast.makeShortToast(registerActivity,"绑定出错");
                    WXBindPhoneActivity.this.finish();
                    break;
                case 2://绑定成功
                    DemoHelper.setPhone(etPhoneNumber.getText().toString().trim());
                    toast.makeShortToast(registerActivity,"绑定成功");
                    try {
                        String wxBindUrl = ConfigInfo.ApiUrl+"index.php/Api/User/auth_login?nickname="
                                +(DemoHelper.getWXNickName()!=null? URLEncoder.encode(DemoHelper.getWXNickName(),"UTF-8"):DemoHelper.getWXNickName())
                                +"&source="+DemoHelper.getWXSource()
                                +"&aite_id="+DemoHelper.getWXAite_id()
                                +"&open_id="+DemoHelper.getWXOpen_id()
                                +"&type=android";

                        Log.i(ConfigInfo.TAG,"url:"+wxBindUrl);
                        HttpUtils.doGetAsyn(null, false, wxBindUrl, handler, new HttpUtils.CallBack() {
                            @Override
                            public void onRequestComplete(String result) {
                                try {
                                    Log.i(ConfigInfo.TAG,"微信绑定成功登录:"+result);
                                    JSONObject jsonObject = new JSONObject(result);
                                    int code = jsonObject.getInt("Code");
                                    if (code ==1){//绑定成功
                                        String uid = jsonObject.getString("Uid");
                                        String HXpassword = jsonObject.getString("hxpassword");
                                        String HXusername = jsonObject.getString("hxusername");
                                        String phone = jsonObject.getString("phone");

                                        DemoHelper.setPhone(phone);
                                        DemoHelper.setUid(uid);
                                        DemoHelper.setHXpassword(HXpassword);
                                        DemoHelper.setHXusername(HXusername);
                                        handler.sendEmptyMessage(11);
                                    }else if (code ==3){//没有绑定
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


                    break;
                case 4://验证码错误
                    toast.makeShortToast(registerActivity,"验证码错误");
                    break;
                case 5://绑定失败，请重试
                    toast.makeShortToast(registerActivity,"绑定失败，请重试");
                    break;
                case 3://获取验证码失败
                    toast.makeShortToast(registerActivity,"获取验证码失败");
                    break;
                case 10:
                    toast.makeShortToast(registerActivity,"已发送");
                    break;
                case 11:
                    toast.makeShortToast(registerActivity,"绑定手机号成功");
                    Intent intent = new Intent(WXBindPhoneActivity.this,MyMainActivity.class);
                    CommonUtils.startActivity(WXBindPhoneActivity.this,intent);
                    WXBindPhoneActivity.this.finish();
                    break;
                case 12:
                    toast.makeShortToast(registerActivity,"绑定失败，请重试");
                    break;
                case 13:
                    toast.makeShortToast(registerActivity,"绑定失败，请重试");
                    break;
                case 15:
                    toast.makeShortToast(registerActivity,"参数错误");
                    break;
            }
            progressDialog.destroy();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_bind_phone);
        ButterKnife.bind(this);
        MyApplication.Create(this);
        EventBus.getDefault().register(this);
        timeCount = new TimeCount(60000, 1000);
        registerActivity = this;
        //初始化
        inits();
        setListener();

    }

    private void inits() {
        tvTitle.setText("绑定手机号");
        tvTopTextRignt.setText("登录");
        tvTopTextRignt.setTextColor(0xFF000000);
        tvTopTextRignt.setVisibility(View.GONE);
        //初始化计时器
        timeCount = new TimeCount(60000, 1000);

//        //判断之前是否已勾选了阅读woosii协议（这里是判断如果第一次登录勾选了协议了，那经后的过程中就不用用户在勾选了 ）
//        if(DemoHelper.getDefaultAgreeAgreement()==false){
//            tvRegister.setEnabled(false);
//            rbWoosiiAgreement.setChecked(false);
//        }else{
//            tvRegister.setEnabled(true);
//            rbWoosiiAgreement.setChecked(true);
//        }
    }

    private void setListener() {
        findViewById(R.id.top_back_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivity.finish();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCity(BackCityEvent event){
        etCity.setText(event.getMsg());
        region_id = event.getRegion_id();
    }

    @OnClick({R.id.main_top_text_rignt, R.id.et_register_city,R.id.tv_register_captcha, R.id.iv_register_eyes, R.id.tv_register, R.id.rb_woosii_register_agreement, R.id.tv_woosii_register_user_agreement})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.main_top_text_rignt:
                registerActivity.finish();
                break;
            case R.id.tv_register_captcha:
                if(TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())){
                    MyToast.makeShortToast(WXBindPhoneActivity.this,"手机号码不能为空");
                }else{
                    timeCount.start();
                    getCaptcha();
                }
                break;
            case R.id.iv_register_eyes:
                if(isHidePassword == false){
                    isHidePassword = true;
                    ivEyes.setImageResource(R.drawable.open_eye);
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.getText().toString().length());
                }else{
                    isHidePassword =false;
                    ivEyes.setImageResource(R.drawable.close_eye);
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.getText().toString().length());
                }
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.rb_woosii_register_agreement:
                if(DemoHelper.getDefaultAgreeAgreement() ==false){
                    rbWoosiiAgreement.setChecked(true);
                    DemoHelper.setDefaultAgreeAgreement(true);
                    tvRegister.setEnabled(true);
                }else{
                    DemoHelper.setDefaultAgreeAgreement(false);
                    rbWoosiiAgreement.setChecked(false);
                    tvRegister.setEnabled(false);
                }
                break;
            case R.id.tv_woosii_register_user_agreement:
                intent=new Intent(WXBindPhoneActivity.this, WosiiUserProblem.class);
                CommonUtils.startActivity(WXBindPhoneActivity.this, intent);
                break;
            case R.id.et_register_city:
                intent=new Intent(WXBindPhoneActivity.this, ChooseCityActivity.class);
                CommonUtils.startActivity(WXBindPhoneActivity.this, intent);
                break;
        }
    }

    private void getCaptcha() {
            if(!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())){
                toast.makeShortToast(registerActivity,"手机号码格式错误");
            }else{
                String url = ConfigInfo.CAPTCHA_URL+etPhoneNumber.getText().toString()+"&type=android";
                HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                    public void onRequestComplete(String result) {
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            int code=jsonObject.getInt("Code");
                            if(code==1){
                                Log.i(ConfigInfo.TAG,"验证码："+jsonObject.getString("smscode"));
                                handler.sendEmptyMessage(10);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(ConfigInfo.TAG,"验证码——发送失败");
                            handler.sendEmptyMessage(3);//获取验证码失败
                        }
                    }
                });
            }
    }

    private void register() {
        CommonUtils.showDialogs("加载中...", this, progressDialog);
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            toast.makeShortToast(registerActivity,"账号不能为空");
            progressDialog.destroy();
            return;
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            toast.makeShortToast(registerActivity,"密码不能为空");
            progressDialog.destroy();
            return;
        }else if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())){
            toast.makeShortToast(registerActivity,"请输入正确的手机号");
            progressDialog.destroy();
            return;
        }else if(!CommonUtils.checkPhonePassword(etPassword.getText().toString().trim())){
            toast.makeShortToast(registerActivity,"密码只能是数字或字母组合");
            progressDialog.destroy();
            return;
        }else if (etCaptcha.getText().toString().isEmpty()) {
            toast.makeShortToast(registerActivity,"请输入验证码");
            progressDialog.destroy();
            return ;
        }else if(TextUtils.isEmpty(etCity.getText().toString().trim())){
            toast.makeShortToast(registerActivity,"选择城市不能为空");
            progressDialog.destroy();
            return;
        }else {
//            md5Encode = Utility.md5(etCaptcha.getText().toString());
            handler.sendEmptyMessage(0);//去登陆
        }

    }


    // 倒数计时器
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tvRegisterCaptcha.setClickable(true);
            tvRegisterCaptcha.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            tvRegisterCaptcha.setClickable(false);
            tvRegisterCaptcha.setText("再次发送验证码"+"("+millisUntilFinished / 1000+")");
        }
    }

}
