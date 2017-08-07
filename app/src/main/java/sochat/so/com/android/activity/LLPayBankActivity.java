package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.BankCardListAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.BankCardModel;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/5/20.
 */

public class LLPayBankActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.tv_certification)
    TextView tvCertification;
    @Bind(R.id.ll_no_card)
    LinearLayout llNoCard;
    @Bind(R.id.rv_bank_card)
    RecyclerView mRecyclerView;

    private TextView tvUp;
    private TextView tvDowp;
    private TextView tvCancel;
    //底部的弹窗
    private MyPopupWindow popupWindow;


    private String kyc_status = "";

    //银行编号
    private String no_agree = "";


    private BankCardListAdapter mAdapter;
    private List<BankCardModel>lists;


    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_bank);
        ButterKnife.bind(this);
        inits();
    }
    private void inits() {
        tvTopText.setText("银行卡");
        //初始化底部的popwindow
        listener = new MyClick();
        popupWindow = new MyPopupWindow(this);

        kyc_status = getIntent().getStringExtra("kyc_status");

        headers.put("Accept", "application/json");
        initBankCard();

        userAuthentication();

        if (mAdapter!=null){
            mAdapter.setListener(new BankCardListAdapter.setOnItemCilckListener() {
                @Override
                public void onItemClickListener(int position) {
                    if (lists.size()>position){
                        BankCardModel item = lists.get(position);
                        no_agree = item.getNo_agree();
                        popupWindow.show(LLPayBankActivity.this);
                    }
                }
            });
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getBankData();
    }

    private void getBankData() {
        if (lists!=null){
            lists.clear();
        }
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("offset", 1);

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_USERBANKCARD);


        MyNetWorkUtil.getNovate(LLPayBankActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayBankActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"getBankData_info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        String bank_array = jsonObject.getString("agreement_list");
                        Log.i(ConfigInfo.TAG,"getBankData_bank_array:"+bank_array);

                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(bank_array).getAsJsonArray();
                        Gson gson = new Gson();
                        //加强for循环遍历JsonArray
                        for (JsonElement user : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            BankCardModel bankCardModel = gson.fromJson(user, BankCardModel.class);
                            lists.add(bankCardModel);
                        }

                        if (mAdapter!=null){
                            mAdapter.setLists((ArrayList<BankCardModel>) lists);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBankCard() {
        if (!kyc_status.equals("0")){
            llNoCard.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            setRecyclerView();
            getBankData();

        }else{
            llNoCard.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setRecyclerView() {
        lists = new ArrayList<BankCardModel>();
        mAdapter = new BankCardListAdapter(LLPayBankActivity.this,lists);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

    }

    @OnClick({R.id.iv_top_back, R.id.tv_commit, R.id.tv_certification})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_top_back:
                LLPayBankActivity.this.finish();
                break;
            case R.id.tv_certification:

                intent = new Intent(LLPayBankActivity.this,AuthenticationInformationActivity.class);
                CommonUtils.startActivity(LLPayBankActivity.this,intent);

                break;
            case R.id.tv_commit:
                intent = new Intent(LLPayBankActivity.this,LLPayBindCardActivity.class);
                CommonUtils.startActivity(LLPayBankActivity.this,intent);
                break;
        }
    }




    //实名认证，这里是开户的实名认证，和钱包的开户有区别！
    private void userAuthentication(){
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("check_auth", 1);

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_AUTHUSER);


        MyNetWorkUtil.getNovate(LLPayBankActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(LLPayBankActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"userAuthentication:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        getBankData();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private MyClick listener;

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_up:
//                    popupWindow.dismiss();
                    break;
                case R.id.tv_down:
                    Intent intent = new Intent(LLPayBankActivity.this,LLPayUnBindBankActivity.class);
                    intent.putExtra("no_agree",no_agree);
                    CommonUtils.startActivity(LLPayBankActivity.this,intent);
                    popupWindow.dismiss();
                    break;
                case R.id.cancel:
                    popupWindow.dismiss();
                    break;
            }
        }
    }

    private class MyPopupWindow extends BottomPushPopupWindow<Void> {
        public MyPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void aVoid) {
            View root = View.inflate(mBottomPopupWindowContext, R.layout.popup_demo, null);
            tvUp = (TextView) root.findViewById(R.id.tv_up);
            tvDowp = (TextView) root.findViewById(R.id.tv_down);
            tvCancel = (TextView) root.findViewById(R.id.cancel);
            tvUp.setTextColor(Color.BLACK);
            tvDowp.setTextColor(Color.RED);
            tvCancel.setTextColor(Color.BLACK);

            tvUp.setText("您是否要解绑此张银行卡");
            tvDowp.setText("解除绑定");

            tvUp.setOnClickListener(listener);
            tvDowp.setOnClickListener(listener);
            tvCancel.setOnClickListener(listener);

            return root;
        }
    }

}
