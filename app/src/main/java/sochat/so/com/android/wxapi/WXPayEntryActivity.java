package sochat.so.com.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.BaseActivity;
import sochat.so.com.android.app.MyApplication;
import sochat.so.com.android.eventbus.WXPayFinishEvent;

import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.ERR_OK;

/**
 * 微信支付
 * @author Administrator
 *
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, ConfigInfo.APP_ID,false);
        api.handleIntent(getIntent(), this);
        Log.i(ConfigInfo.TAG,"微信支付回调开始——onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    public void onReq(BaseReq req) {
        Log.i(ConfigInfo.TAG,"微信支付回调开始——req");
    }

    public void onResp(BaseResp resp) {
        Intent intent;
        Log.i(ConfigInfo.TAG,"resp:"+resp.errCode);
        Log.i(ConfigInfo.TAG,"微信支付回调开始");
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode){
                case ERR_OK:
                    EventBus.getDefault().post(new WXPayFinishEvent());
//                intent = new Intent(WXPayEntryActivity.this, RechargeResultActivity.class);
//                    CommonUtils.startActivity(WXPayEntryActivity.this,intent);
                    break;
            }
            MyApplication.errCode=resp.errCode;
            Log.i(ConfigInfo.TAG,"微信支付回调成功");
        }
        finish();
    }
}