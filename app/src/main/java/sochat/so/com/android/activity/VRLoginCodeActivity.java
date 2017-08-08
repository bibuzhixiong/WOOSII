package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/4/21.
 */

public class VRLoginCodeActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_one)
    TextView tvOne;
    @Bind(R.id.tv_two)
    TextView tvTwo;
    @Bind(R.id.tv_three)
    TextView tvThree;
    @Bind(R.id.tv_four)
    TextView tvFour;
    @Bind(R.id.tv_five)
    TextView tvFive;
    @Bind(R.id.tv_six)
    TextView tvSix;
    @Bind(R.id.tv_repeat)
    TextView tvRepeat;
    @Bind(R.id.tv_show)
    TextView tvShow;

    private MyToast toast;
    private String vr_code;
    private TimeCount timeCount;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                tvOne.setText(vr_code.charAt(0)+"");
                tvTwo.setText(vr_code.charAt(1)+"");
                tvThree.setText(vr_code.charAt(2)+"");
                tvFour.setText(vr_code.charAt(3)+"");
                tvFive.setText(vr_code.charAt(4)+"");
                tvSix.setText(vr_code.charAt(5)+"");
                    break;
                case 1:
                    toast.makeShortToast(VRLoginCodeActivity.this,"获得登录码失败，请重新发送");
                    break;
                case 2:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_login_code);
        ButterKnife.bind(this);
        inits();
        initData();
    }

    private void initData() {
        String url = ConfigInfo.ApiUrl+"/index.php/Api/User/vr_code?user_id="+ DemoHelper.getUid();
        HttpUtils.doGetAsyn(VRLoginCodeActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    vr_code =jsonObject.getString("vr_code");
                    if (!TextUtils.isEmpty(vr_code)){
                        handler.sendEmptyMessage(0);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void inits() {
        tvTopText.setText("VR一体机登录验证码");
        timeCount = new TimeCount(30000,1000);
        timeCount.start();
    }

    @OnClick({R.id.iv_top_back, R.id.tv_repeat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                VRLoginCodeActivity.this.finish();
                break;
            case R.id.tv_repeat:
                initData();
                tvRepeat.setVisibility(View.GONE);
                tvShow.setVisibility(View.VISIBLE);
                timeCount.start();
                break;
        }
    }

    private class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        tvShow.setText(millisUntilFinished / 1000+"秒后 重新发送验证码");
        }

        @Override
        public void onFinish() {
        tvRepeat.setVisibility(View.VISIBLE);
            tvShow.setVisibility(View.GONE);
        }
    }

}
