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
import android.widget.TextView;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.eventbus.WXPayFinishEvent;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/4/10.
 */

public class VRRentActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.iv_deposit)
    ImageView ivDeposit;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_text1)
    TextView tvBrand;
    @Bind(R.id.tv_text2)
    TextView tvName;
    @Bind(R.id.tv_text3)
    TextView tvSystem;
    @Bind(R.id.tv_text4)
    TextView tvInfo;


    private MyToast toast;
    public final IWXAPI api = WXAPIFactory.createWXAPI(this, ConfigInfo.APP_ID,false);
    private PayReq request;
    private String unRentState;
    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow bottomPopupWindow;
    /**
     * 底部popupwindow的按钮
     */
    private TextView tvCommit;
    private TextView tvCancel;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -3:
                    buystate = 2;
                    setView(2);
                    break;
                case -2://已组用
                    buystate = 1;
                    setView(1);
                    break;
                case -1:
                    buystate = 0;
                    setView(0);
                    break;
                case 0:
                    MyToast.makeShortToast(VRRentActivity.this,res);
                    break;
                case 1:
                    skip_to_pay();
                    break;
                case 2:
                    MyToast.makeShortToast(VRRentActivity.this,res);
                    break;
                case 3:
                    MyToast.makeShortToast(VRRentActivity.this,"退租失败，请重试");
                    break;
                case 4:
                    Intent intent = new Intent(VRRentActivity.this,UnrentCheckActivity.class);
                    CommonUtils.startActivity(VRRentActivity.this,intent);

                    break;
            }
        }
    };

    private void setView(int state) {
        tvMoney.setText(money+"");
        tvBrand.setText("品牌："+brand);
        tvName.setText("产品型号："+name);
        tvSystem.setText("兼容型号："+system);
        tvInfo.setText("产品信息："+info);
        if (buystate == 1){
            tvPay.setText("立即退租");
        }else if(buystate == 0){
            tvPay.setText("立即租用");
        }else if (buystate == 2){
            tvPay.setText("审核中");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_rent);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopText.setText("VR一体机3D眼镜租用");
        //微信支付注册
        api.registerApp(ConfigInfo.APP_ID);
        request = new PayReq();
        bottomPopupWindow = new BottomPopupWindow(this);
        bottomPopupWindow1 = new BottomPopupWindow1(this);

        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(WXPayFinishEvent event){
        Intent intent = new Intent(VRRentActivity.this,RentSuccessActivity.class);
        CommonUtils.startActivity(VRRentActivity.this,intent);
        VRRentActivity.this.finish();
    }

    private int money;
    private String brand;
    private String name;
    private String system;
    private String info;
    private int buystate =0;
    private void initData() {
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/vrprice?user_id="+ DemoHelper.getUid();
        HttpUtils.doGetAsyn(VRRentActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"initData_result:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    money = jsonObject.getInt("money");
                    brand = jsonObject.getString("brand");
                    name = jsonObject.getString("name");
                    system = jsonObject.getString("system");
                    info = jsonObject.getString("info");
                    if (code ==0){//未租用
                        handler.sendEmptyMessage(-1);
                    }else if (code == 1){//已租用
                        handler.sendEmptyMessage(-2);
                    }else{//审核中
                        handler.sendEmptyMessage(-3);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.iv_top_back, R.id.tv_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                VRRentActivity.this.finish();
                break;
            case R.id.tv_pay:
                if (buystate ==1){
                    bottomPopupWindow.show(VRRentActivity.this);
                }else if (buystate ==0){
                    rent();

//                    bottomPopupWindow1.show(this);


                }else if (buystate ==2){
                    Intent intent = new Intent(VRRentActivity.this,UnrentCheckActivity.class);
                    CommonUtils.startActivity(VRRentActivity.this,intent);
                }
                break;
        }
    }

    private void unrent() {
        String unurl = ConfigInfo.ApiUrl +"/index.php/Vr/Vlive/rent?user_id="+DemoHelper.getUid();
        HttpUtils.doGetAsyn(VRRentActivity.this, true, unurl, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0){//失败
                        handler.sendEmptyMessage(3);
                    }else if (code ==1){
                        handler.sendEmptyMessage(4);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private String res;
    private String appid;
    private String partnerid;
    private String noncestr;
    private String package_;
    private String order_sn;
    private String prepayid;
    private String timestamp;
    private String sign;

    private void rent() {
        String url = ConfigInfo.ApiUrl+"/index.php/Api/Wallet/wx_pay?user_id="+DemoHelper.getUid()+"&money="+money+"&code="+ Utility.md5("wosii_888")+"&category=1";
        Log.i(ConfigInfo.TAG,"rechargeNow_url:"+url);
        HttpUtils.doGetAsyn(VRRentActivity.this, true, url, handler, new HttpUtils.CallBack() {
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
                        handler.sendEmptyMessage(0);
                    }else if (code == 1){//获取生成的订单成功
                        handler.sendEmptyMessage(1);
                    }else if (code == 2){
                        handler.sendEmptyMessage(2);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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


    private class BottomPopupWindow extends BottomPushPopupWindow<Void> {

        public BottomPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_pay,null);
            TextView tv1 = (TextView) bottom.findViewById(R.id.tv_text1);
            TextView tv2 = (TextView) bottom.findViewById(R.id.tv_text2);
            tvCommit = (TextView) bottom.findViewById(R.id.tv_commit);
            tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);
            tv1.setText("退款需审核，是否确定退款？");
            tv2.setVisibility(View.GONE);
            tvCancel.setText("取消");
            tvCommit.setText("确定");
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomPopupWindow.dismiss();
                }
            });

            tvCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unrent();
                    bottomPopupWindow.dismiss();
                }
            });
            return bottom;
        }
    }


    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow1 bottomPopupWindow1;

    private TextView tvFindPassword1;
    private TextView tvChangePassword1;
    private TextView tvTransactionDetail1;
    private TextView tvCancel1;
    private class BottomPopupWindow1 extends BottomPushPopupWindow<Void> {

        public BottomPopupWindow1(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_with_password,null);
            tvFindPassword1 = (TextView) bottom.findViewById(R.id.tv_find_password);
            tvChangePassword1 = (TextView) bottom.findViewById(R.id.tv_change_password);
            tvTransactionDetail1 = (TextView) bottom.findViewById(R.id.tv_transaction_detail);
            tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);

            tvFindPassword1.setText("连连支付");
            tvChangePassword1.setText("微信支付");

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomPopupWindow1.dismiss();
                }
            });

            tvFindPassword1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    bankPay();
                    bottomPopupWindow1.dismiss();
                }
            });
            tvChangePassword1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    rent();
                    bottomPopupWindow1.dismiss();
                }
            });
            tvTransactionDetail1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    bottomPopupWindow1.dismiss();
                }
            });



            return bottom;
        }
    }






}
