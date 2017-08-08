package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * 充值时间结果
 *
 * 充值时间成功，进行推荐课程的显示
 */

public class RechargeResultActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_recharge_time)
    TextView tvRechargeTime;
//    @Bind(R.id.list)
//    LRecyclerView list;

    private MyToast tosat;
    private String res;
    private String time;

//    /Vr/Vlive/hot_video?user_id=920&pindex=1&psize=5


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tosat.makeShortToast(RechargeResultActivity.this,res);
                    break;
                    case 1:
                        tvRechargeTime.setText(time);
                        tosat.makeShortToast(RechargeResultActivity.this,res);
                    break;
                case 2:
                    tosat.makeShortToast(RechargeResultActivity.this,res);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_result);
        ButterKnife.bind(this);
        inits();

    }

    private void inits() {
        tvTopText.setText("充值结果");
        Intent intent = getIntent();
        time = intent.getStringExtra("recharge_time");
        Log.i(ConfigInfo.TAG,"recharge_time:"+time);
        goToRechargeTime();

    }

    private void goToRechargeTime() {
        String url= ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/wo_time?user_id="+ DemoHelper.getUid()+"&time="+time;
        Log.i(ConfigInfo.TAG,"url:"+url);
        HttpUtils.doGetAsyn(RechargeResultActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"result:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    String min = jsonObject.getString("min");
                    res = jsonObject.getString("msg");
                    if (code ==0){//失败
                        handler.sendEmptyMessage(0);
                    }else if (code ==1){//成功
                        handler.sendEmptyMessage(1);
                    }else{
                        handler.sendEmptyMessage(2);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.iv_top_back)
    public void onViewClicked() {
        RechargeResultActivity.this.finish();
    }



}
