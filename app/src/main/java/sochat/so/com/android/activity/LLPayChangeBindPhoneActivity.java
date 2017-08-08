package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.BaseHelper;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.DialogCallBack;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/6/23.
 */

public class LLPayChangeBindPhoneActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;

    @Bind(R.id.tv_captcha)
    TextView tvCaptcha;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.et_old_phone)
    EditText etOldPhone;
    @Bind(R.id.et_sms_code)
    EditText etSmsCode;
    @Bind(R.id.et_pay_password)
    EditText etPayPassword;
    @Bind(R.id.et_new_phone)
    EditText etNewPhone;

    /**
     * 计时器
     */
    private TimeCount timeCount;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();
    //这里是发送短信获得验证码得到的的token
    private String smsToken;

    /**
     * 提示框
     */
    private ProgressDialog progressDialog = new ProgressDialog();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_change_bind_phone);
        ButterKnife.bind(this);
        inits();
        setListeners();
    }

    private void setListeners() {
        textChange tc1 = new textChange();
        etOldPhone.addTextChangedListener(tc1);
        etNewPhone.addTextChangedListener(tc1);
        etSmsCode.addTextChangedListener(tc1);
        etPayPassword.addTextChangedListener(tc1);
    }

    private void inits() {
        tvTopText.setText("修改绑定手机号");
        timeCount = new TimeCount(60000, 1000);
    }



    //点击事件
    @OnClick({R.id.iv_top_back, R.id.tv_captcha,R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                LLPayChangeBindPhoneActivity.this.finish();
                break;
            case R.id.tv_captcha:
                if (TextUtils.isEmpty(etOldPhone.getText().toString().trim())){
                    MyToast.makeShortToast(this,"请输入原绑定的手机号");
                }else if(TextUtils.isEmpty(etPayPassword.getText().toString().trim())){
                    MyToast.makeShortToast(this,"请输入支付密码");
                }else{
                    timeCount.start();
                    functionOne();
                }
                break;
            case R.id.tv_commit:
                //这里是验证短信验证码的接口
                CommonUtils.showDialogs("上传中...", this, progressDialog);
                functionTwo(smsToken);
                break;
        }
    }

    private void functionOne() {
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type", ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        try {
            addparameters.put("pwd_pay", RSAUtil.encrypt(etPayPassword.getText().toString(), ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC));
        } catch (Exception e) {
            e.printStackTrace();
        }

        addparameters.put("mob_bind", etOldPhone.getText().toString());
        addparameters.put("flag_check", "1");

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(LLPayChangeBindPhoneActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_PWDAUTH);


        MyNetWorkUtil.getNovate(LLPayChangeBindPhoneActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayChangeBindPhoneActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                Log.i(ConfigInfo.TAG,"修改绑定手机号："+jsonObject.toString());
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        smsToken = jsonObject.getString("token");
                        MyToast.makeShortToast(LLPayChangeBindPhoneActivity.this,"已发送");
                    }else{
                        MyToast.makeShortToast(LLPayChangeBindPhoneActivity.this,jsonObject.getString("ret_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void functionTwo(String token) {
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());

        addparameters.put("token", token);
        addparameters.put("verify_code", etSmsCode.getText().toString());

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_SMSCHECK);


        MyNetWorkUtil.getNovate(LLPayChangeBindPhoneActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayChangeBindPhoneActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        functionThree(jsonObject.getString("token"));
                        MyToast.makeShortToast(LLPayChangeBindPhoneActivity.this,"已发送");
                    }else{
                        progressDialog.destroy();
                        MyToast.makeShortToast(LLPayChangeBindPhoneActivity.this,jsonObject.getString("ret_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void functionThree(String token) {
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());

        addparameters.put("token", token);
        addparameters.put("mob_bind", etNewPhone.getText().toString());

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(LLPayChangeBindPhoneActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_MODIFYUSERMOB);


        MyNetWorkUtil.getNovate(LLPayChangeBindPhoneActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayChangeBindPhoneActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                progressDialog.destroy();
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        CommonUtils.showSingleDialog(LLPayChangeBindPhoneActivity.this, "温馨提示", "修改绑定手机号成功", "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                //这里是执行的方法
                                LLPayChangeBindPhoneActivity.this.finish();
                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });
                    }else{
                        BaseHelper.showDialog(LLPayChangeBindPhoneActivity.this, "提示", jsonObject.getString("ret_msg"),R.mipmap.aag,null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //EditText监听器

    class textChange implements TextWatcher {

        @Override

        public void afterTextChanged(Editable arg0) {

        }

        @Override

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

        }

        @Override

        public void onTextChanged(CharSequence cs, int start, int before,int count) {
            boolean newphone = etNewPhone.getText().length()>10;
            boolean oldphone = etOldPhone.getText().length() >10;
            boolean smscode = etSmsCode.getText().length() > 5;
            boolean paypassword = etPayPassword.getText().length() > 5;

            if (newphone&oldphone&smscode&paypassword) {
                tvCommit.setEnabled(true);
                tvCommit.setBackgroundResource(R.drawable.selector_login_or_register_pressed);
            }
            else {
                //在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
                tvCommit.setEnabled(false);
                tvCommit.setBackgroundResource(R.color.line_gray);
            }
        }
    }

    // 倒数计时器
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tvCaptcha.setClickable(true);
            tvCaptcha.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            tvCaptcha.setClickable(false);
            tvCaptcha.setText("再次发送验证码"+"("+millisUntilFinished / 1000+")");
        }
    }


}
