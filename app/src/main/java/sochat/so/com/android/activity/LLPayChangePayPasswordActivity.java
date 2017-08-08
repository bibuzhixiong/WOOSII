package sochat.so.com.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
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
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.MyToast;

/**
 * 修改密码Activity
 * Created by Administrator on 2017/6/16.
 */

public class LLPayChangePayPasswordActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_commit)
    TextView tvCommit;

    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_ensure_new_password)
    EditText etEnsureNewPassword;
    @Bind(R.id.et_source_passworde)
    EditText etSourcePassword;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_change_pay_password);
        ButterKnife.bind(this);
        inits();
        setListeners();
    }

    private void inits() {
        tvTopText.setText("修改支付密码");
    }

    private void setListeners() {
        textChange tc1 = new textChange();
        etSourcePassword.addTextChangedListener(tc1);
        etNewPassword.addTextChangedListener(tc1);
        etEnsureNewPassword.addTextChangedListener(tc1);
    }

    @OnClick({R.id.iv_top_back, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                LLPayChangePayPasswordActivity.this.finish();
                break;
            case R.id.tv_commit:
                changePassword();
                break;
        }
    }

    private void changePassword() {

        addparameters.clear();
        parameters.clear();

        if (!etNewPassword.getText().toString().equals(etEnsureNewPassword.getText().toString())){
            MyToast.makeShortToast(LLPayChangePayPasswordActivity.this,"两次输入密码不同");
            return ;
        }

        String sourcePassword ="";
        String newPassword = "";
        try {
            sourcePassword = RSAUtil.encrypt(etSourcePassword.getText().toString(), ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC);
            newPassword = RSAUtil.encrypt(etEnsureNewPassword.getText().toString(), ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("pwd_pay", sourcePassword);
        addparameters.put("pwd_pay_new", newPassword);

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(LLPayChangePayPasswordActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_PAYPWDMODIFY);

        MyNetWorkUtil.getNovate(LLPayChangePayPasswordActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayChangePayPasswordActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"修改密码info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        LLPayChangePayPasswordActivity.this.finish();
                        MyToast.makeShortToast(LLPayChangePayPasswordActivity.this,"修改密码成功");
                    }else{
                        MyToast.makeShortToast(LLPayChangePayPasswordActivity.this,jsonObject.getString("ret_msg"));
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
            boolean etSource = etSourcePassword.getText().length() == 6;
            boolean newPassword = etNewPassword.getText().length() == 6;
            boolean ensureNewPassword = etEnsureNewPassword.getText().length() == 6;

            if (etSource&newPassword&ensureNewPassword) {
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

}
