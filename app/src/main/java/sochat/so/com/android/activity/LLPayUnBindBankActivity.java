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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.DialogCallBack;

/**
 * Created by Administrator on 2017/6/17.
 */

public class LLPayUnBindBankActivity extends BaseActivity {
    private ImageView ivPassword1;
    private ImageView ivPassword2;
    private ImageView ivPassword3;
    private ImageView ivPassword4;
    private ImageView ivPassword5;
    private ImageView ivPassword6;
    private EditText etPayPaswordInVisiable;

    private ImageView ivBack;
    private TextView tvTopTitle;

    /**
     * 存放密码图的数组
     */
    private List<ImageView> imageViews;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    //获得要解绑的银行卡好的no_agree
    private String no_agree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_unbind_bank);
        no_agree = getIntent().getStringExtra("no_agree");
        inits();
        listeners();
    }

    private void inits() {
        ivBack = (ImageView) findViewById(R.id.iv_top_back);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);

        tvTopTitle.setText("解绑银行卡");

        ivPassword1 = (ImageView) findViewById(R.id.iv_password1);
        ivPassword2 = (ImageView) findViewById(R.id.iv_password2);
        ivPassword3 = (ImageView) findViewById(R.id.iv_password3);
        ivPassword4 = (ImageView) findViewById(R.id.iv_password4);
        ivPassword5 = (ImageView) findViewById(R.id.iv_password5);
        ivPassword6 = (ImageView) findViewById(R.id.iv_password6);

        etPayPaswordInVisiable = (EditText) findViewById(R.id.et_pay_password_invisible);

        //初始化存放密码图片的数组
        imageViews = new ArrayList<>();
        imageViews.add(ivPassword1);
        imageViews.add(ivPassword2);
        imageViews.add(ivPassword3);
        imageViews.add(ivPassword4);
        imageViews.add(ivPassword5);
        imageViews.add(ivPassword6);


        etPayPaswordInVisiable.findFocus();
        etPayPaswordInVisiable.addTextChangedListener(new TextWatcher() {
            private int inputstart;
            private int inputchanged;

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
                inputstart =arg0.length();
            }

            /**
             *
             * @param cs   是输入后的EditText的文本
             * @param start 输入前的位置
             * @param before 不管他，是输入前要替换的位置
             * @param count 改变的数量
             */
            @Override
            public void onTextChanged(CharSequence cs, int start, int before,int count) {
                inputchanged = cs.length();
                if (inputchanged>inputstart){
                    imageViews.get(start).setVisibility(View.VISIBLE);
                }else{
                    imageViews.get(start).setVisibility(View.INVISIBLE);
                }
                if (inputchanged == 6){
                    unBindBankCard(etPayPaswordInVisiable.getText().toString());
                }

            }
        });
    }


    /**
     * 提示框
     */
    private ProgressDialog progressDialog=new ProgressDialog();

    //解绑银行卡
    private void unBindBankCard(String password) {
        CommonUtils.showDialogs("加载中...",this, progressDialog);

        addparameters.clear();
        parameters.clear();

        try {
            password = RSAUtil.encrypt(password, ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC);
            Log.i(ConfigInfo.TAG,"加密:"+password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("no_agree", no_agree);
        addparameters.put("pwd_pay", password);
        addparameters.put("type_user", "0");

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_BANKCARDUNBIND);
        MyNetWorkUtil.getNovate(LLPayUnBindBankActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayUnBindBankActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"unBindBankCard_info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        CommonUtils.showSingleDialog(LLPayUnBindBankActivity.this, "提示", "解绑成功", "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {

                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });
                        //解绑成功
                        progressDialog.destroy();
                    }else{
                        CommonUtils.showSingleDialog(LLPayUnBindBankActivity.this, "提示", jsonObject.getString("ret_msg"), "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                setClearInputPassword();
                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });
                        //解绑失败
                        progressDialog.destroy();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //解绑失败
                    progressDialog.destroy();
                }
            }
        });

    }

    private void listeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLPayUnBindBankActivity.this.finish();
            }
        });

    }

    public void setClearInputPassword(){
        if (etPayPaswordInVisiable!=null){
            etPayPaswordInVisiable.setText("");

            ivPassword1.setVisibility(View.INVISIBLE);
            ivPassword2.setVisibility(View.INVISIBLE);
            ivPassword3.setVisibility(View.INVISIBLE);
            ivPassword4.setVisibility(View.INVISIBLE);
            ivPassword5.setVisibility(View.INVISIBLE);
            ivPassword6.setVisibility(View.INVISIBLE);

        }
    }
}
