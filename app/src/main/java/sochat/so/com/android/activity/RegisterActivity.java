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
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.app.MyApplication;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.BackCityEvent;
import sochat.so.com.android.eventbus.RegisterSkipEvent;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/2/24.
 * 注册界面
 */

public class RegisterActivity extends BaseActivity {
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
    private Toast toast;
    public static RegisterActivity registerActivity;
    /**
     * 提示框
     */
    private ProgressDialog progressDialog = new ProgressDialog();

    /**
     * 城市的id号
     */
    private String region_id;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://进行注册/index.php/Api/User/new_register?type=0&phone=xxx&password=xxx&code=xxx
                    String url = ConfigInfo.REGISTER_URL+DemoHelper.getUserType()
                            +"&phone="+etPhoneNumber.getText().toString().trim()
                            +"&password="+ Utility.md5(etPassword.getText().toString().trim())
                            +"&code="+Utility.md5(etCaptcha.getText().toString().trim())
                            +"&region_id="+region_id;
                    HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {//此手机号码已被注册
                                    handler.sendEmptyMessage(6);
                                } else if (code == 1) {//注册失败
                                    handler.sendEmptyMessage(2);
                                }  else if (code == 4) {//注册成功
                                    handler.sendEmptyMessage(1);
                                } else if (code == 5) {//验证码错误
                                    handler.sendEmptyMessage(4);
                                } else if (code == 6) {//验证码失效
                                    handler.sendEmptyMessage(5);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(7);
                            }
                        }
                    });

                    break;
                case 1://注册成功
                    toast.makeText(registerActivity,"注册成功",Toast.LENGTH_SHORT).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("usr", etPhoneNumber.getText().toString().trim());
//                    bundle.putString("pwd", etPassword.getText().toString().trim());
//                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
//                    intent.putExtra("data_return_RegisterInterface",bundle);
//                    setResult(RESULT_OK,intent);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            RegisterActivity.this.finish();
//                        }
//                    },2000);
//                    Intent intent = new Intent(RegisterActivity.this,MyMainActivity.class);
//                    CommonUtils.startActivity(RegisterActivity.this,intent);
                    EventBus.getDefault().post(new RegisterSkipEvent(etPhoneNumber.getText().toString().trim(),etPassword.getText().toString().trim()));

                    RegisterActivity.this.finish();
                    break;
                case 2://注册失败
                    toast.makeText(registerActivity,"注册失败",Toast.LENGTH_SHORT).show();
                    break;
                case 3://获取验证码失败
                    toast.makeText(registerActivity,"获取验证码失败",Toast.LENGTH_SHORT).show();
                    break;
                case 4://验证码错误
                    toast.makeText(registerActivity,"验证码错误",Toast.LENGTH_SHORT).show();
                    break;
                case 5://验证码失效
                    toast.makeText(registerActivity,"验证码失效",Toast.LENGTH_SHORT).show();
                    break;
                case 6://此手机号码已被注册
                    toast.makeText(registerActivity,"此手机号码已被注册",Toast.LENGTH_SHORT).show();
                    break;
                case 7://注册失败
                    toast.makeText(registerActivity,"注册失败",Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    toast.makeText(registerActivity,"已发送",Toast.LENGTH_SHORT).show();
                    break;
            }
            progressDialog.destroy();
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        MyApplication.Create(this);
        registerActivity = this;
        EventBus.getDefault().register(this);
        //初始化
        inits();
        setListener();
    }

    private void inits() {
        tvTitle.setText("注册");
        tvTopTextRignt.setText("登录");
        tvTopTextRignt.setTextColor(0xFF000000);
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
                    MyToast.makeShortToast(RegisterActivity.this,"手机号码不能为空");
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
                intent=new Intent(RegisterActivity.this, WosiiUserProblem.class);
                CommonUtils.startActivity(RegisterActivity.this, intent);
                break;
            case R.id.et_register_city:
                intent=new Intent(RegisterActivity.this, ChooseCityActivity.class);
                CommonUtils.startActivity(RegisterActivity.this, intent);
                break;
        }
    }

    private void getCaptcha() {
            if(!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())){
                toast.makeText(registerActivity,"手机号码格式错误",Toast.LENGTH_SHORT).show();
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
            toast.makeText(registerActivity,"账号不能为空",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return;
        } else if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            toast.makeText(registerActivity,"密码不能为空",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return;
        }else if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())){
            toast.makeText(registerActivity,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return;
        }else if (CommonUtils.checkPhoneNumber(etCity.getText().toString().trim())){
            toast.makeText(registerActivity,"请选择城市",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return;
        }else if(!CommonUtils.checkPhonePassword(etPassword.getText().toString().trim())){
            toast.makeText(registerActivity,"密码只能是数字或字母组合",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return;
        }else if (etCaptcha.getText().toString().isEmpty()) {
            toast.makeText(registerActivity,"请输入验证码",Toast.LENGTH_SHORT).show();
            progressDialog.destroy();
            return ;
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
