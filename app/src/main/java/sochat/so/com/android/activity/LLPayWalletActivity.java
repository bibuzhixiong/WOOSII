package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LLPayWalletActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.iv_search)
    ImageView ivSetting;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;

    @Bind(R.id.tv_income_yesterday)
    TextView tvIncomeYesterday;
    @Bind(R.id.tv_income_all)
    TextView tvIncomeAll;
    @Bind(R.id.ll_all_income_click)
    LinearLayout llAllIncomeClick;
    @Bind(R.id.tv_bank_card)
    TextView tvBankCard;
    @Bind(R.id.tv_order_search)
    TextView tvOrderSearch;
    @Bind(R.id.tv_cash_now)
    TextView tvCashNow;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    //实名认证状态
    private String kyc_status ="";

    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow bottomPopupWindow;

    //woosii钱包
    private String woosii_yesterday_money ="";
    private String woosii_money ="";

    /**
     * 用户总余额
     */
    private String balance=" ";
    private String cashout_amt=" ";
    private String freeze_balance=" ";
    /**
     * 总金额
     */
    private String allCash ="0.00";



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tvIncomeYesterday.setText(allCash);
                    tvIncomeAll.setText("昨日收益："+woosii_yesterday_money+" 元");
                break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_wallet);
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
        ivSetting.setVisibility(View.VISIBLE);
        ivSetting.setImageResource(R.drawable.xuanxiang);
        bottomPopupWindow = new BottomPopupWindow(this);
        tvTopText.setText("我的钱包");
        headers.put("Accept", "application/json");
        initWoosiiWallet();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initWoosiiWallet();
    }


    private void initWoosiiWallet() {
        String url = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/profit?user_id=" + DemoHelper.getUid();
        Log.i(ConfigInfo.TAG, "IncomeToCashActivity_url:" + url);
        HttpUtils.doGetAsyn(LLPayWalletActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.i(ConfigInfo.TAG, "IncomeToCashActivity_result:" + result);
                    woosii_money = jsonObject.getString("money");
                    allCash = jsonObject.getString("money");
                    woosii_yesterday_money = jsonObject.getString("day_money");
                    double woosii_cash = Double.parseDouble(woosii_money);
                    handler.sendEmptyMessage(0);
                    initWallet(woosii_cash);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initWallet(final double woosii_cash) {
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_SINGLEUSERQUERY);


        MyNetWorkUtil.getNovate(LLPayWalletActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayWalletActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"初始化钱包info:"+info);
                try {

                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        kyc_status = jsonObject.getString("kyc_status");
                        balance =jsonObject.getString("balance");//连连钱包总金额
                        cashout_amt =jsonObject.getString("cashout_amt");//可提现金额
                        freeze_balance =jsonObject.getString("freeze_balance");//冻结金额
                        Double ll_pay_cash = Double.parseDouble(balance);
                        allCash = (woosii_cash+ll_pay_cash)+"";
                        handler.sendEmptyMessage(0);
                    }else if (ret_code.equals("3007")){
                        kyc_status = "0";
                    }else{
                        MyToast.showShortText(LLPayWalletActivity.this,jsonObject.getString("ret_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick({R.id.iv_top_back, R.id.tv_bank_card,R.id.tv_order_search, R.id.tv_cash_now,R.id.iv_search,R.id.ll_all_income_click})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_top_back:
                LLPayWalletActivity.this.finish();
                break;
            case R.id.iv_search:
                bottomPopupWindow.show(this);
                break;
            case R.id.ll_all_income_click:
                intent = new Intent(LLPayWalletActivity.this,SixPartyIncomeActivity.class);
                CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                break;
            case R.id.tv_bank_card:
                    intent = new Intent(LLPayWalletActivity.this,LLPayBankActivity.class);
                    intent.putExtra("kyc_status",kyc_status);
                    CommonUtils.startActivity(LLPayWalletActivity.this,intent);

                break;
            case R.id.tv_order_search:
                if (!kyc_status.equals("0")){
                    intent  = new Intent(LLPayWalletActivity.this,LLPayOrderListActivity.class);
                    CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                }else{
                    intent  = new Intent(LLPayWalletActivity.this,AuthenticationInformationActivity.class);
                    CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                }
                break;
            case R.id.tv_cash_now:
                if (!kyc_status.equals("0")){
                    intent  = new Intent(LLPayWalletActivity.this,IncomeToCashActivity.class);
                    CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                }else{
                    intent  = new Intent(LLPayWalletActivity.this,AuthenticationInformationActivity.class);
                    CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                }
                break;
        }
    }

    private TextView tvFindPassword;
    private TextView tvChangePassword;
    private TextView tvTransactionDetail;
    private TextView tvCancel;
    private class BottomPopupWindow extends BottomPushPopupWindow<Void> {

        public BottomPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_ll_pay_wallet,null);
            tvFindPassword = (TextView) bottom.findViewById(R.id.tv_find_password);
            tvChangePassword = (TextView) bottom.findViewById(R.id.tv_change_password);
            tvTransactionDetail = (TextView) bottom.findViewById(R.id.tv_transaction_detail);
            tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomPopupWindow.dismiss();
                }
            });

            tvFindPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent ;
                    if (!kyc_status.equals("0")){
                        intent  = new Intent(LLPayWalletActivity.this,LLPayFindPayPasswordActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }else{
                        intent  = new Intent(LLPayWalletActivity.this,AuthenticationInformationActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }

                    bottomPopupWindow.dismiss();
                }
            });
            tvChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent ;
                    if (!kyc_status.equals("0")){
                        intent  = new Intent(LLPayWalletActivity.this,LLPayChangePayPasswordActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }else{
                        intent  = new Intent(LLPayWalletActivity.this,AuthenticationInformationActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }

                    bottomPopupWindow.dismiss();
                }
            });
            tvTransactionDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent ;
                    if (!kyc_status.equals("0")){
                        intent  = new Intent(LLPayWalletActivity.this,LLPayChangeBindPhoneActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }else{
                        intent  = new Intent(LLPayWalletActivity.this,AuthenticationInformationActivity.class);
                        CommonUtils.startActivity(LLPayWalletActivity.this,intent);
                    }

                    bottomPopupWindow.dismiss();
                }
            });
            return bottom;
        }
    }


}
