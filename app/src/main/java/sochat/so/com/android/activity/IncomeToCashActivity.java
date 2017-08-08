package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.dialog.LLPayWithdrawDialog;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.ChooseBankWithDrawEvent;
import sochat.so.com.android.interface_method.ILLPayGetBankListPresenter;
import sochat.so.com.android.interface_method.ILLPayGetBankListView;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.interface_method_realize.LLPayGetBankListPresenter;
import sochat.so.com.android.llpay_util.BaseHelper;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.model.BankCardModel;
import sochat.so.com.android.model.WeiXinBean;
import sochat.so.com.android.model.WeixinUserInfoBean;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DateUtil;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.DialogCallBack;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/4/17.
 */

public class IncomeToCashActivity extends BaseActivity implements ILLPayGetBankListView,LLPayWithdrawDialog.LLPayInputPasswordCallBack {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.et_input_number)
    EditText etInputNumber;
    @Bind(R.id.tv_show_money)
    TextView tvShowMoney;
    @Bind(R.id.tv_freeze_balance)
    TextView tvFreezeBalance;
    @Bind(R.id.iv_bank_icon)
    ImageView ivBankIcon;
    @Bind(R.id.tv_current_bank__name)
    TextView tvCurrentBankName;
    @Bind(R.id.ll_layout)
    LinearLayout llLayout;
    @Bind(R.id.tv_roll_out_now)
    TextView tvRollOutNow;

    private ProgressDialog progressDialog = new ProgressDialog();
    //微信API
    public static IWXAPI api;
    //线程
    public Thread thread;
    //微信WX_CODE
    public static String WX_CODE = "";
    public static int WX_BACK_CODE = -5;
    private String nickname = "";
    private String open_id="";
    public static IncomeToCashActivity incomeToCashActivity;
    private Message message = new Message();
    /**
     * 微信登陆
     */
    public static boolean isWXLogin = false;
    /**
     * 用户微信没绑定银行卡提示
     */
    private  NormalAlertDialog.Builder dialog;
    /**
     * 体现到银行卡
     */
    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();


    /**
     * 用户总余额
     */
    private String cashout_amt=" ";
    private String freeze_balance=" ";


    /**
     * 连连支付的获得银行卡列表的presenter
     */
    private ILLPayGetBankListPresenter getBankListPresenter;

    private BankCardModel payBankWitndraw = new BankCardModel();
    /**
     * 输入支付密码的dialog
     */
    private LLPayWithdrawDialog withdrawDialog;



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
                case 1://商户号把钱转到用户钱包成功
                    tvShowMoney.setText("可提现金额："+cashout_amt+"元");
                    tvFreezeBalance.setText("冻结金额："+freeze_balance+"元");

                    break;
                case 10:
                    String url = ConfigInfo.ApiUrl+"index.php/api/Wallet/wx_withdrawals?openid="+open_id+"&user_id="+DemoHelper.getUid()+"&money="+etInputNumber.getText().toString().trim();
                    Log.i(ConfigInfo.TAG,"wxToCash_url:"+url);
                    HttpUtils.doGetAsyn(IncomeToCashActivity.this, true, url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);

                                Log.i(ConfigInfo.TAG,"wxToCash_result:"+result);
                                message.obj = jsonObject.getString("Res");
                                message.what = 0;
                                if (jsonObject.getInt("Code")==0){
                                    initData();
                                    handler.sendEmptyMessage(12);
                                }else if (jsonObject.getInt("Code")==1){
                                    // 失败
                                    message.what = 11;
                                }else if (jsonObject.getInt("Code")==2){//微信没有绑定银行卡
                                    // 错误信息
                                    message.what = 13;
                                }else if (jsonObject.getInt("Code")==3){
                                    // 错误信息
                                    message.what = 11;
                                }
                                handler.sendMessage(message);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    break;
                case 11:
                    etInputNumber.setText("");
                    MyToast.makeShortToast(IncomeToCashActivity.this,message.obj.toString());
                    break;
                case 12:
                    etInputNumber.setText("");
                    break;
                case 13:
                    wxTip();
                    etInputNumber.setText("");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_to_cash);
        ButterKnife.bind(this);
        EventBus.getDefault().register(IncomeToCashActivity.this);
        incomeToCashActivity = this;
        inits();
    }

    private void inits() {
        tvTopText.setText("收益提现");
        //这里是通过回调得到银行卡列表(mvp开发模式)
        getBankListPresenter = new LLPayGetBankListPresenter(IncomeToCashActivity.this);
        getBankListPresenter.getBankList(IncomeToCashActivity.this);
        initData();

        etInputNumber.addTextChangedListener(new TextWatcher() {
            /**
             *  这里的charSequence表示改变之前的内容，通常start和count组合，
             *  可以在charSequence中读取本次改变字段中被改变的内容。而after表示改变后新的内容的数量。
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            /**
             *这里的charSequence表示改变之后的内容，通常start和count组合，
             * 可以在charSequence中读取本次改变字段中新的内容。而before表示被改变的内容的数量。
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!TextUtils.isEmpty(charSequence)){
                    tvRollOutNow.setEnabled(true);
                    tvRollOutNow.setBackgroundResource(R.drawable.selector_login_or_register_pressed);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)){
                    tvRollOutNow.setEnabled(false);
                    tvRollOutNow.setBackgroundResource(R.color.line_gray);
                }
            }
        });


        llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payBankWitndraw!=null){
                    Intent intent = new Intent(IncomeToCashActivity.this,LLPayChooseBankWithdrawActivity.class);
                    intent.putExtra("current_bank",payBankWitndraw.getCard_no());
                    CommonUtils.startActivity(IncomeToCashActivity.this,intent);
                }else {
                    CommonUtils.showTipDialog(IncomeToCashActivity.this, true, "提示", "您还没有绑定银行卡", "立即绑定", "取消", false, new DialogCallBack() {
                        @Override
                        public void left() {
                            Intent intent = new Intent(IncomeToCashActivity.this,LLPayBankActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void right() {
                            IncomeToCashActivity.this.finish();

                        }

                        @Override
                        public void edittext(String edittext) {

                        }
                    });
                }
            }
        });


    }

    private void initData() {
        String url = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/profit?user_id=" + DemoHelper.getUid();
        Log.i(ConfigInfo.TAG, "IncomeToCashActivity_url:" + url);
        HttpUtils.doGetAsyn(IncomeToCashActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    Log.i(ConfigInfo.TAG, "IncomeToCashActivity_result:" + result);
                    String money = jsonObject.getString("money");

                    double woosii_cash = Double.parseDouble(money);
                    Log.i(ConfigInfo.TAG, "woosii_cash:" + woosii_cash);

                    if (woosii_cash>0){
                        //从沃噻钱包到连连钱包
                        getOrder(money);
                    }else{
                        getUserWalletInfo();
                    }

//                    handler.sendEmptyMessage(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.iv_top_back, R.id.tv_roll_out_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                IncomeToCashActivity.this.finish();
                break;
            case R.id.tv_roll_out_now:
                //体现到银行卡
                withdrawDialog = new LLPayWithdrawDialog(IncomeToCashActivity.this );
                withdrawDialog.setCanceledOnTouchOutside(false);
                withdrawDialog.show();
                withdrawDialog.setPayMoney(etInputNumber.getText().toString());
                withdrawDialog.setInputPasswordCallBack(IncomeToCashActivity.this);
                //提现到微信
//                loginWX();
                break;
        }

    }


    //从沃噻服务器得到订单信息(从沃噻钱包到LL钱包)
    private void getOrder(final String money){
        addparameters.clear();
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("money_order",money);

        MyNetWorkUtil.getNovate(IncomeToCashActivity.this, ConfigInfo.YES_UID,headers,addparameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.postMyMothed(IncomeToCashActivity.this, "index.php/Vr/Lianlianpay/withdrawals_order", addparameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
                    Log.i(ConfigInfo.TAG,"getOrder():"+jsonObject.toString());
                    if (jsonObject.getInt("ret_code")==0){
                        String no_order = jsonObject.getString("no_order");
                        incomeToBank(no_order,money);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    //从沃噻服务器得到订单信息(从LL钱包到银行卡)
    private void getWithdrawOrder(final String password){
        addparameters.clear();
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("money_order",etInputNumber.getText().toString());

        MyNetWorkUtil.getNovate(IncomeToCashActivity.this, ConfigInfo.YES_UID,headers,addparameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.postMyMothed(IncomeToCashActivity.this, "index.php/Vr/Lianlianpay/banks_order", addparameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
                    Log.i(ConfigInfo.TAG,"体现到银行卡getWithdrawOrder():"+jsonObject.toString());
                    if (jsonObject.getInt("ret_code")==0){
                        String no_order = jsonObject.getString("no_order");
                        fromWalletToBank(no_order,password);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 这里是体现到绑定银行卡的过程(从沃噻钱包到连连钱包)
     */
    private void incomeToBank(final String no_order,String money) {
        CommonUtils.showDialogs("加载中...", this, progressDialog);
        addparameters.clear();
        parameters.clear();
        String timeString = DateUtil.getCurentDate();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("col_userid", DemoHelper.getUid());

        addparameters.put("money_order", money);
        addparameters.put("dt_order", timeString);
        addparameters.put("no_order", no_order);
        addparameters.put("notify_url", ConfigInfo.OID_TO_WALLET_NOTIFY_URL);

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(IncomeToCashActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_TRADERPAYMENT);


        MyNetWorkUtil.getNovate(IncomeToCashActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(IncomeToCashActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"incomeToBank——info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){//这里是钱已经从沃噻商户号到用户钱包了
                    //更新界面显示的数据
                    getUserWalletInfo();

                        progressDialog.destroy();
                    }else{
                        CommonUtils.showSingleDialog(IncomeToCashActivity.this, "提示", jsonObject.getString("ret_msg"), "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                            etInputNumber.setText("");
                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });
                        progressDialog.destroy();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 这里是当钱已经从商户号打到用户的钱包里面后进行数据的刷新
     */
    private void getUserWalletInfo() {
        addparameters.clear();
        parameters.clear();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());

        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_SINGLEUSERQUERY);


        MyNetWorkUtil.getNovate(IncomeToCashActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(IncomeToCashActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"getUserWalletInfo()_info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        cashout_amt =jsonObject.getString("cashout_amt");//可提现金额
                        freeze_balance =jsonObject.getString("freeze_balance");//冻结金额
                    handler.sendEmptyMessage(1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fromWalletToBank(String no_order,String password) {
        withNOBank();

        CommonUtils.showDialogs("加载中...", this, progressDialog);

        addparameters.clear();
        parameters.clear();

        String timeString = DateUtil.getCurentDate();

        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        addparameters.put("user_id", DemoHelper.getUid());
        addparameters.put("money_order", etInputNumber.getText().toString());
        addparameters.put("dt_order", timeString);
        addparameters.put("no_order", no_order);
        addparameters.put("no_agree", payBankWitndraw.getNo_agree());
        try {
            password =RSAUtil.encrypt(password,ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC);

        } catch (Exception e) {
            e.printStackTrace();
        }
        addparameters.put("pwd_pay", password);
        addparameters.put("info_order", "提现到银行卡");
        addparameters.put("notify_url", ConfigInfo.WALLET_TO_BANK_NOTIFY_URL);
        addparameters.put("api_version", "1.2");
        addparameters.put("paytime_type", "1");

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(IncomeToCashActivity.this));
        parameters.put("data",addparameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_CASHOUTCOMBINEAPPLY);


        MyNetWorkUtil.getNovate(IncomeToCashActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(IncomeToCashActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"从钱包提现到银行卡info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        progressDialog.destroy();
                        if (withdrawDialog!=null){
                            withdrawDialog.dismiss();
                        }
                        //提现成功后再刷新界面
                        getUserWalletInfo();
                        etInputNumber.setText("");
                        CommonUtils.showSingleDialog(IncomeToCashActivity.this, "提示", "支付成功", "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {//在这里处理点击事件

                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });

                    }else{
                        //提现失败后再刷新界面
                        getUserWalletInfo();
                        BaseHelper.showDialog(IncomeToCashActivity.this, "提示", jsonObject.getString("ret_msg"),R.mipmap.aag,null);
                        if (withdrawDialog!=null){
                            withdrawDialog.setClearInputPassword();
                        }
                        progressDialog.destroy();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 这里是体现的到微信的过程
     */
    private void loginWX() {
        //注册到微信
        api = WXAPIFactory.createWXAPI(this,ConfigInfo.APP_ID,true);
        api.registerApp(ConfigInfo.APP_ID);

        isWXLogin =true;

        CommonUtils.showDialogs("加载中...", this, progressDialog);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        api.sendReq(req);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWXUserInfo();
        progressDialog.destroy();
    }

    /**
     * 登录微信
     */
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
                                        open_id=userInfoBean.getOpenid();
                                        DemoHelper.setWXOpen_id(open_id);
                                        handler.sendEmptyMessage(10);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(11);//授权失败
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
    }


    /**
     * 微信实名认证提示
     */
    private void wxTip(){
        //提示是否登录
        dialog = new NormalAlertDialog.Builder(this);

        dialog.setTitleVisible(true)
                .setSingleMode(true)
                .setTitleText("温馨提示")
                .setSingleButtonText("确定")
                .setSingleButtonTextColor(R.color.black)
                .setTitleTextColor(R.color.black)
                .setContentText("提现失败，您的微信没有绑定银行卡进行实名认证")
                .setContentTextColor(R.color.black_light)
                .setSingleListener(new DialogInterface.OnSingleClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickSingleButton(NormalAlertDialog dialog, View view) {
                        dialog.dismiss();
                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.build().show();
    }


    @Override
    public void getBankList(final List<BankCardModel> bankCardLists) {
    Log.i(ConfigInfo.TAG,"这里是通过MVP得到银行卡的信息："+bankCardLists.toString());
        if (bankCardLists!=null&&bankCardLists.size()>0){
            refreshCurrentBank(bankCardLists.get(0));
        }else{
            payBankWitndraw = new BankCardModel();
            withNOBank();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void chooseBankWithDraw(ChooseBankWithDrawEvent event){
        refreshCurrentBank(event.getItem());
    }

    /**
     * 这里是更新当前要提现的银行卡
     * @param item
     */
    private void refreshCurrentBank(BankCardModel item){
        //这里进行提现银行卡的赋值处理
        payBankWitndraw = item;
        boolean isFindBankICon = false;
        for (int i = 0; i<ConfigInfo.BANK_CODE_ICON.length;i++){
            if ((ConfigInfo.BANK_CODE_ICON_STRING[i]).contains(item.getBank_code())){
                ivBankIcon.setImageResource(ConfigInfo.BANK_CODE_ICON[i]);
                isFindBankICon = true;
            }
        }

        if (!isFindBankICon){
            ivBankIcon.setImageResource(R.mipmap.bank_deflaut);
        }
        tvCurrentBankName.setText(item.getBank_name()+"("+item.getCard_no()+")");
    }

    /**
     * 这里是输入密码完成后的回调
     * @param pay_password
     */
    @Override
    public void inputPasswordCallBack(String pay_password) {
        getWithdrawOrder(pay_password);
    }

    /**
     * 对没有银行卡的处理
     */
    private void withNOBank(){
        if (TextUtils.isEmpty(payBankWitndraw.getNo_agree())){
            CommonUtils.showTipDialog(IncomeToCashActivity.this, true, "提示", "您还未绑定银行卡", "立即绑定", "取消", false,  new DialogCallBack() {
                @Override
                public void left() {
                    Intent intent = new Intent(IncomeToCashActivity.this,LLPayBankActivity.class);
                    CommonUtils.startActivity(IncomeToCashActivity.this,intent);
                }

                @Override
                public void right() {

                }

                @Override
                public void edittext(String edittext) {

                }
            });
            return ;
        }
    }
}
