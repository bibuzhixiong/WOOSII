package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
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
 * Created by Administrator on 2017/4/26.
 */

public class RentSuccessActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_back_main)
    TextView tvBackMain;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    MyToast.makeShortToast(RentSuccessActivity.this,"租用失败，请联系客服");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_success);
        ButterKnife.bind(this);
        tvTopText.setText("完成");
        inits();
    }

    private void inits() {
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/vrwo_time?user_id="+ DemoHelper.getUid();
        HttpUtils.doGetAsyn(RentSuccessActivity.this, true, url,handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code ==1){//成功
                    handler.sendEmptyMessage(0);
                    }else{//失败
                    handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @OnClick({R.id.iv_top_back,R.id.tv_back_main})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_top_back:
                RentSuccessActivity.this.finish();
                break;
            case R.id.tv_back_main:
                RentSuccessActivity.this.finish();
                break;
        }
    }
}
