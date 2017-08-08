package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.RechargeTimeAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.dialog.LLPayBankDialog;
import sochat.so.com.android.dialog.LLPayChooseBankDialog;
import sochat.so.com.android.dialog.LLPaySMSCodeDialog;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.LLPayBindBankEvent;
import sochat.so.com.android.eventbus.WXPayFinishEvent;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.BaseHelper;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.model.BankCardModel;
import sochat.so.com.android.model.RechargeTimeModel;
import sochat.so.com.android.model.RechargeTimeResult;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DateUtil;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.DialogCallBack;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/4/10.
 */

public class RechargeTimeActivity extends BaseActivity implements LLPayChooseBankDialog.LLPayInputPasswordCallBack,LLPayBankDialog.setOnThisDialogFinishCallBack,LLPaySMSCodeDialog.LLPayInputSMSCodeCallBack {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_remain_time)
    TextView tvRemainTime;
    @Bind(R.id.tv_minute_text)
    TextView tvMinuteText;
    @Bind(R.id.iv_recharge_image)
    ImageView ivRechargeImage;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.tv_rechare_now)
    TextView tvRechareNow;
    private RechargeTimeActivity mActivity;
    private RechargeTimeAdapter mAdapter =null;
    private PreviewHandler mHandler =new PreviewHandler(RechargeTimeActivity.this);
    private RechargeTimeResult Result;
    private RecyclerView mRecyclerView;

    private List<RechargeTimeModel> lists ;
    //银行卡列表
    private List<BankCardModel>bankCardLists;
    private int remainTime;
    private String money;
    private int time;

    public final IWXAPI api = WXAPIFactory.createWXAPI(this, ConfigInfo.APP_ID,false);
    private PayReq request;

    /**
     * 弹窗提示
     */
    private static NormalAlertDialog.Builder dialog;

    /**
     * 加载框
     */
    private ProgressDialog progressDialog=new ProgressDialog();
    private LLPayChooseBankDialog bankChoosedDialog;
    private LLPayBankDialog bankDialog;

    //被选择支付的银行卡对象
    private BankCardModel choosedPayBank = new BankCardModel();

    /**
     * 输入支付密码的dialog
     */
    private LLPaySMSCodeDialog smsCodeDialog;
    private String smstoken;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_time);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mActivity = RechargeTimeActivity.this;
        inits();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(WXPayFinishEvent event){

        Intent intent = new Intent(RechargeTimeActivity.this,RechargeResultActivity.class);
        Log.i(ConfigInfo.TAG,"time:"+time);
        intent.putExtra("recharge_time",time+"");
        CommonUtils.startActivity(RechargeTimeActivity.this,intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindBankSuccessEvent(LLPayBindBankEvent event){
        getBankData();
    }


    private void inits() {
        bottomPopupWindow = new BottomPopupWindow(this);
        headers.put("Accept", "application/json");
        //初始化弹窗
        dialog = new NormalAlertDialog.Builder(mActivity);

        api.registerApp(ConfigInfo.APP_ID);
        request = new PayReq();

        ivTopBack.setVisibility(View.VISIBLE);
        tvTopText.setText("充值购买");
        lists = new ArrayList<RechargeTimeModel>();
        //初始化银行卡列表集合
        bankCardLists = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new RechargeTimeAdapter(this,lists);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mAdapter.setOnItemClickLitener(new RechargeTimeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (lists.size() > position) {
                    RechargeTimeModel item = lists.get(position);
                    money = item.getMoney();
                    time = item.getTime();

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onEnableButton(boolean isEnabled) {
                tvRechareNow.setEnabled(isEnabled);
                tvRechareNow.setBackgroundResource(R.drawable.selector_login_or_register_pressed);
            }
        });
        //获取用户剩余分钟数
        requestData();
        //获得银行卡数据
        getBankData();
    }


    @OnClick({R.id.iv_top_back,R.id.tv_rechare_now})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_top_back:
                RechargeTimeActivity.this.finish();
                break;
            case R.id.tv_rechare_now:
                getOrder();
//                bottomPopupWindow.show(this);
                break;
        }
    }


    /**
     * 微信支付
     * start
     */
    private String res;
    private String appid;
    private String partnerid;
    private String noncestr;
    private String package_;
    private String order_sn;
    private String prepayid;
    private String timestamp;
    private String sign;

    private void rechargeNow() {
        String url = ConfigInfo.ApiUrl+"/index.php/Api/Wallet/wx_pay?user_id="+DemoHelper.getUid()+"&money="+money+"&code="+ Utility.md5("wosii_888")+"&category="+0;
        Log.i(ConfigInfo.TAG,"rechargeNow_url:"+url);
        HttpUtils.doGetAsyn(mActivity, true, url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"rechargeNow_result:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("Code");
                    res = jsonObject.getString("Res");
                    appid = jsonObject.getString("appid");
                    partnerid = jsonObject.getString("partnerid");
                    noncestr = jsonObject.getString("noncestr");
                    package_ = jsonObject.getString("package");
                    order_sn = jsonObject.getString("order_sn");
                    prepayid = jsonObject.getString("prepayid");
                    timestamp = jsonObject.getString("timestamp");
                    sign = jsonObject.getString("sign");

                    if (code ==0){
                        mHandler.sendEmptyMessage(0);
                    }else if (code == 1){//获取生成的订单成功
                        mHandler.sendEmptyMessage(1);
                    }else if (code == 2){
                        mHandler.sendEmptyMessage(2);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    /**
     * 微信支付
     * end
     */

    /**
     * 银行卡支付
     * start
     ************************************************************************************************************************************************
     */


    //银行卡支付
    private Map<String, Object> order_parameters = new MyHashMap<String, Object>();
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();
    private String money_order="";
    private String no_order="";
    private String ret_msg="";
    private String name_goods="";

    //从沃噻服务器得到订单信息
    private void getOrder(){
        order_parameters.clear();
        order_parameters.put("user_id", DemoHelper.getUid());
        order_parameters.put("money_order",money);
        order_parameters.put("name_goods", time+"沃币");
        order_parameters.put("hourlong", time);
        order_parameters.put("code", Utility.md5("wosii_888"));
        order_parameters.put("category", "0");

        MyNetWorkUtil.getNovate(RechargeTimeActivity.this, ConfigInfo.YES_UID,headers,order_parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.postMyMothed(RechargeTimeActivity.this, "/index.php/Vr/Lianlianpay/payment_order", order_parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
                    Log.i(ConfigInfo.TAG,"getOrder():"+jsonObject.toString());
                    if (jsonObject.getInt("ret_code")==0){
                        money_order = jsonObject.getString("money_order");
                        no_order = jsonObject.getString("no_order");
                        ret_msg = jsonObject.getString("ret_msg");
                        name_goods = jsonObject.getString("name_goods");
                        mHandler.sendEmptyMessage(3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void start_pay(String pay_password){
        parameters.clear();
        order_parameters.clear();

        String timeString = DateUtil.getCurentDate();

        order_parameters.put("oid_partner",ConfigInfo.QUICK_WALLET_OID_PARTNER);
        order_parameters.put("busi_partner","101001");
        order_parameters.put("no_order",no_order);
        order_parameters.put("dt_order",timeString);
        order_parameters.put("api_version","1.1");
        order_parameters.put("no_agree",choosedPayBank.getNo_agree());//银行卡编码
        order_parameters.put("pay_type","2");//借记卡快捷支付
        try {
            order_parameters.put("pwd_pay", RSAUtil.encrypt(pay_password,ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC));//支付密码
        } catch (Exception e) {
            e.printStackTrace();
        }
        order_parameters.put("name_goods",name_goods+"沃币");
        order_parameters.put("notify_url",ConfigInfo.NOTIFY_URL);
        order_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        order_parameters.put("user_id",DemoHelper.getUid());
        order_parameters.put("money_order",money_order);
        order_parameters.put("flag_modify","0");

        Log.i(ConfigInfo.TAG,"打印order_parameters:"+order_parameters.toString());
        parameters.put("risk_item",CommonUtils.getConstructRiskItem(this));
        parameters.put("data",order_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_BANKCARDPREPAY);

        MyNetWorkUtil.getNovate(RechargeTimeActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(RechargeTimeActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                progressDialog.destroy();

                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"访问LLPay4.1接口:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    String ret_msg = jsonObject.getString("ret_msg");
                    if (ConfigInfo.RET_CODE_SUCCESS.equals(ret_code)) {
                        bankChoosedDialog.dismiss();
                        if (jsonObject.getString("sms_flag").equals("0")){//这里是不发送短信的情况
                            //这里才是真正的支付成功，得到ret_code为0000的情况也可能是出于支付进行中
                            if (jsonObject.getString("result_pay").equals(ConfigInfo.PAY_SUCCESS)){
                                CommonUtils.showSingleDialog(RechargeTimeActivity.this, "提示", "支付成功", "确定", new DialogCallBack() {
                                    @Override
                                    public void left() {

                                    }
                                    @Override
                                    public void right() {//在这里处理点击事件
                                        requestData();
                                    }

                                    @Override
                                    public void edittext(String edittext) {

                                    }
                                });
                            }
                        }else{
                            smstoken = jsonObject.getString("token");
                            smsCodeDialog = new LLPaySMSCodeDialog(RechargeTimeActivity.this);
                            smsCodeDialog.setCanceledOnTouchOutside(false);
                            smsCodeDialog.show();
                            smsCodeDialog.setInputSMSCodeCallBack(RechargeTimeActivity.this);
                        }
                    }else if(ConfigInfo.RET_CODE_PASSWORD_ERROR.equals(ret_code)){
                        CommonUtils.showTipDialog(RechargeTimeActivity.this, true, "提示", "支付密码错误", "忘记密码", "重试", false, new DialogCallBack() {
                            @Override
                            public void left() {
                                bankChoosedDialog.setClearInputPassword();
                                Intent intent = new Intent(RechargeTimeActivity.this,LLPayFindPayPasswordActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void right() {
                            bankChoosedDialog.setClearInputPassword();
                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });

                    } else if (ConfigInfo.RET_CODE_USER_NO_AUTH.equals(ret_code)){
                        llpayDialog();
                    }else{
                        // TODO 失败
                        BaseHelper.showDialog(RechargeTimeActivity.this, "提示", ret_msg,R.mipmap.aag,null);
                        bankChoosedDialog.setClearInputPassword();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * end
     ************************************************************************************************************************************************
     */

    private void notifyDataSetChanged() {
        mAdapter.setList((ArrayList<RechargeTimeModel>) lists);
    }


    /**
     * 输入密码的回调，这里是密码输入完成才会触发
     */
    @Override
    public void inputPasswordCallBack(String pay_password) {
        CommonUtils.showDialogs("正在支付中...",this, progressDialog);
        start_pay(pay_password);
    }
    //当第二个dialog消失的时候回调显示第一个dialog
    @Override
    public void showFirstDialog() {
        bankChoosedDialog.show();
    }
    //当第二个dialog选择了银行卡的时候更新第一个dialog的银行卡的信息
    @Override
    public void showChoosedBank(BankCardModel item) {
        choosedPayBank = item;
        defaultPayBankModel(choosedPayBank);
    }
    //这里是输入短信的回调
    @Override
    public void inputSMSCodeCallBack(String sms_code) {
        smsCodePay(sms_code);
    }

    /**
     * 这里是需要短信验证码才能支付的情况
     * @param sms_code
     */
    private void smsCodePay(String sms_code) {
        parameters.clear();
        order_parameters.clear();
        order_parameters.put("oid_partner",ConfigInfo.QUICK_WALLET_OID_PARTNER);
        order_parameters.put("token",smstoken);
        order_parameters.put("no_order",no_order);
        order_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        order_parameters.put("money_order",money_order);
        order_parameters.put("verify_code",sms_code);

        parameters.put("data",order_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_BANKCARDEPAY);

        MyNetWorkUtil.getNovate(RechargeTimeActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(RechargeTimeActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"通过短信验证码进行支付:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        CommonUtils.showSingleDialog(RechargeTimeActivity.this, "提示", "支付成功", "确定", new DialogCallBack() {
                            @Override
                            public void left() {

                            }
                            @Override
                            public void right() {//在这里处理点击事件
                                requestData();
                            }

                            @Override
                            public void edittext(String edittext) {

                            }
                        });
                    }else{
                        BaseHelper.showDialog(RechargeTimeActivity.this, "提示", ret_msg,R.mipmap.aag,null);
                        bankChoosedDialog.setClearInputPassword();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class PreviewHandler extends Handler{
        private WeakReference<RechargeTimeActivity>ref;
        private PreviewHandler(RechargeTimeActivity activity){
            ref = new WeakReference<RechargeTimeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final RechargeTimeActivity activity = ref.get();
            if (activity ==null||mActivity.isFinishing()){
                return ;
            }
            switch (msg.what) {
                case -1:
                    tvRemainTime.setText(remainTime+"");
                    activity.notifyDataSetChanged();
                    break;
                case -3:
                    activity.notifyDataSetChanged();

                    break;

                case 0:
                    MyToast.makeShortToast(mActivity,res);
                    break;
                case 1:
                    skip_to_pay();
                    break;
                case 2:
                    MyToast.makeShortToast(mActivity,res);
                    break;
                case 3://显示银行卡支付的dialog
                    bankChoosedDialog = new LLPayChooseBankDialog(RechargeTimeActivity.this,onClickListener);
                    bankChoosedDialog.setCanceledOnTouchOutside(false);
                    bankChoosedDialog.show();
                    bankChoosedDialog.setInputPasswordCallBack(RechargeTimeActivity.this);
                    //这里是设置当前支付的银行卡的显示
                    defaultPayBankModel(choosedPayBank);
                    break;
            }
        }
    }

    private void defaultPayBankModel(BankCardModel item){
        boolean isFindBankICon =false;
        if(item!=null){
            for (int i = 0; i<ConfigInfo.BANK_CODE_ICON.length;i++){
                if ((ConfigInfo.BANK_CODE_ICON_STRING[i]).contains(item.getBank_code())){
                    bankChoosedDialog.setbankIconImage(ConfigInfo.BANK_CODE_ICON[i]);
                    isFindBankICon = true;
                }
            }

            if (!isFindBankICon){
                bankChoosedDialog.setbankIconImage(R.mipmap.bank_deflaut);
            }
            bankChoosedDialog.setGoodesName(time+"沃币");
            bankChoosedDialog.setPayMoney(""+money);
            bankChoosedDialog.setBankChoosedName(item.getBank_name()+"("+item.getCard_no()+")");


        }
    }

    private void skip_to_pay() {
        api.registerApp(ConfigInfo.APP_ID);

        request.appId = appid;

        request.partnerId = partnerid;

        request.prepayId= prepayid;

        request.packageValue = package_;

        request.nonceStr= noncestr;

        request.timeStamp= timestamp;

        request.sign= sign;

        api.sendReq(request);

    }

    /**
     * 请求网络,获得分钟数——money列表
     */
    private void requestData() {
        if (lists !=null){
            lists.clear();
        }
        final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/time_list?user_id="+ DemoHelper.getUid();
        HttpUtils.doGetAsyn(mActivity,true,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"url"+url);
                Log.i(ConfigInfo.TAG,"course_result"+result);
                Gson gson =new Gson();
                Result = gson.fromJson(result,RechargeTimeResult.class);
                lists = Result.getChilds();
                remainTime = Result.getMin();
                if (lists != null){
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);
                }
            }
        });

    }

    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow bottomPopupWindow;

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
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_with_password,null);
            tvFindPassword = (TextView) bottom.findViewById(R.id.tv_find_password);
            tvChangePassword = (TextView) bottom.findViewById(R.id.tv_change_password);
            tvTransactionDetail = (TextView) bottom.findViewById(R.id.tv_transaction_detail);
            tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);

            tvFindPassword.setText("连连支付");
            tvChangePassword.setText("微信支付");

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomPopupWindow.dismiss();
                }
            });

            tvFindPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getOrder();

                    bottomPopupWindow.dismiss();
                }
            });
            tvChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rechargeNow();
                    bottomPopupWindow.dismiss();
                }
            });
            tvTransactionDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomPopupWindow.dismiss();
                }
            });



            return bottom;
        }
    }

    private void getBankData() {
        if (bankCardLists!=null){
            bankCardLists.clear();
        }
        order_parameters.clear();
        parameters.clear();
        order_parameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        order_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        order_parameters.put("user_id", DemoHelper.getUid());
        order_parameters.put("offset", 1);
        parameters.put("data",order_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_USERBANKCARD);


        MyNetWorkUtil.getNovate(RechargeTimeActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(RechargeTimeActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                Log.i(ConfigInfo.TAG,"getBankData_info:"+info);
                try {
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        String bank_array = jsonObject.getString("agreement_list");
                        Log.i(ConfigInfo.TAG,"RechargeTimeActivity_getBankData_bank_array:"+bank_array);

                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(bank_array).getAsJsonArray();
                        Gson gson = new Gson();
                        //加强for循环遍历JsonArray
                        for (JsonElement user : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            BankCardModel bankCardModel = gson.fromJson(user, BankCardModel.class);
                            bankCardLists.add(bankCardModel);
                        }

                        if (bankDialog!=null){
                            bankDialog.reFreshBankList(bankCardLists);
                        }

                        //默认选择第一张银行卡进行支付
                        if (bankCardLists.size()>0){
                            choosedPayBank =bankCardLists.get(0);
                        }

                    }else if (ret_code.equals("3007")){
                        llpayDialog();
                    }else{
                        BaseHelper.showDialog(RechargeTimeActivity.this, "提示", jsonObject.getString("ret_msg"),R.mipmap.aag,null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_delete :

                    bankChoosedDialog.dismiss();
                    break;
                case R.id.ll_pay_layout:
                    bankDialog = new LLPayBankDialog(RechargeTimeActivity.this,bankCardLists);
                    bankDialog.setOnThisDialogFinishCallBackListener(RechargeTimeActivity.this);
                    bankDialog.setCanceledOnTouchOutside(false);
                    bankDialog.show();
                    bankChoosedDialog.hide();
                    break;
                case R.id.btn_pay:
                    break;
            }
        }
    };


/**
 * 没有进行钱包实名认证的Dialog
 */
private void llpayDialog(){
    CommonUtils.showTipDialog(RechargeTimeActivity.this, true, "提示", "您还未绑定进行钱包实名认证", "立即认证", "取消", false,  new DialogCallBack() {
        @Override
        public void left() {
            Intent intent = new Intent(RechargeTimeActivity.this,AuthenticationInformationActivity.class);
            CommonUtils.startActivity(RechargeTimeActivity.this,intent);
        }

        @Override
        public void right() {

        }

        @Override
        public void edittext(String edittext) {

        }
    });
    }

}
