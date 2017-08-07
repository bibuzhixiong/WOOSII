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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.eventbus.LLPayBindBankEvent;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.BaseHelper;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LLPayBindCardActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.tv_captcha)
    TextView tvCaptcha;

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_bank_card)
    EditText etBankCard;
    @Bind(R.id.et_captcha)
    EditText etCaptcha;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    /**
     * 计时器
     */
    private TimeCount timeCount;

    private String phone = "";
    private String userBankCard = "";

    private String token="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_bind_card);
        ButterKnife.bind(this);
        inits();
        setListeners();
    }

    private void setListeners() {
        textChange tc1 = new textChange();
        etPhone.addTextChangedListener(tc1);
        etBankCard.addTextChangedListener(tc1);
        etCaptcha.addTextChangedListener(tc1);
    }

    private void inits() {
        tvTopText.setText("添加银行卡");
        timeCount = new TimeCount(60000, 1000);
    }


    @OnClick({R.id.iv_top_back, R.id.tv_commit, R.id.tv_captcha})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                LLPayBindCardActivity.this.finish();
                break;
            case R.id.tv_commit:
                bind(token);
                break;
            case R.id.tv_captcha:
                timeCount.start();
                if (TextUtils.isEmpty(etPhone.getText().toString().trim())){
                    MyToast.makeShortToast(this,"请输入银行卡对应的手机号");
                }else if(TextUtils.isEmpty(etBankCard.getText().toString().trim())){
                    MyToast.makeShortToast(this,"请输入银行卡号");
            }else{
                    send();
                }
                break;
        }
    }

    private void send() {
        phone = etPhone.getText().toString().trim();
        userBankCard = etBankCard.getText().toString().trim();

        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("api_version", ConfigInfo.LL_VERSION_NO);
        addparameters.put("bind_mob", phone);
        addparameters.put("card_no", userBankCard);

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(LLPayBindCardActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_BANKCARDOPENAUTH);


        MyNetWorkUtil.getNovate(LLPayBindCardActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayBindCardActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        token = jsonObject.getString("token");
                        MyToast.makeShortToast(LLPayBindCardActivity.this,"已发送");
                    }else{
                        BaseHelper.showDialog(LLPayBindCardActivity.this, "提示", jsonObject.getString("ret_msg"),R.mipmap.aag,null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    private void bind(String token) {
        addparameters.put("token", token);
        addparameters.put("verify_code", etCaptcha.getText().toString().trim());

        parameters.clear();
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_BANKCARDAUTHVERFY);


        MyNetWorkUtil.getNovate(LLPayBindCardActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayBindCardActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"info:"+info);

                try {
                    String ret_code = jsonObject.getString("ret_code");

                    if (ret_code.equals("0000")){
//                        String token = jsonObject.getString("token");
                        LLPayBindCardActivity.this.finish();
                        MyToast.makeShortToast(LLPayBindCardActivity.this,"绑定成功");

                        EventBus.getDefault().post(new LLPayBindBankEvent());
                    }else{
                        BaseHelper.showDialog(LLPayBindCardActivity.this, "提示", jsonObject.getString("ret_msg"),R.mipmap.aag,null);
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
            boolean phone = etPhone.getText().length() > 10;
            boolean bankcard = etBankCard.getText().length() > 15;
            boolean captcha = etCaptcha.getText().length() > 5;

            if (phone&bankcard&captcha) {
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
