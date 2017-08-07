package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/5/12.
 */

public class AuthenticationActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.line)
    TextView line;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.main_titlelayout)
    RelativeLayout mainTitlelayout;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.phone_number)
    EditText phoneNumber;
    @Bind(R.id.verification)
    EditText verification;
    @Bind(R.id.get_code)
    TextView getCode;
    @Bind(R.id.identity)
    EditText identity;
    @Bind(R.id.submit_explain)
    TextView submitExplain;

    /**
     * 计时器
     */
    private TimeCount timeCount;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:

                    break;
                case 1:
                    MyToast.makeShortToast(AuthenticationActivity.this,"提交成功");
                    DemoHelper.setOne(1);
                    AuthenticationActivity.this.finish();
                    break;
                case 3://获取验证码失败
                    MyToast.makeShortToast(AuthenticationActivity.this,"获取验证码失败");
                    break;
                case 10://获取验证码失败
                    MyToast.makeShortToast(AuthenticationActivity.this,"已发送");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        timeCount = new TimeCount(60000, 1000);

        tvTopText.setText("实名认证");
    }

    @OnClick({R.id.iv_top_back, R.id.get_code, R.id.submit_explain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                AuthenticationActivity.this.finish();
                break;
            case R.id.get_code:
                if(TextUtils.isEmpty(phoneNumber.getText().toString().trim())){
                    MyToast.makeShortToast(AuthenticationActivity.this,"手机号码不能为空");
                }else{
                    timeCount.start();
                    getCode();
                }

                break;
            case R.id.submit_explain:
                if (TextUtils.isEmpty(name.getText().toString())){
                    MyToast.makeShortToast(AuthenticationActivity.this,"真实姓名不能为空");
                    return ;
                }else if (TextUtils.isEmpty(phoneNumber.getText().toString())){
                    MyToast.makeShortToast(AuthenticationActivity.this,"手机号不能为空");
                    return ;
                }else if (TextUtils.isEmpty(verification.getText().toString())){
                    MyToast.makeShortToast(AuthenticationActivity.this,"验证码不能为空");
                    return ;
                }else if (TextUtils.isEmpty(identity.getText().toString())){
                    MyToast.makeShortToast(AuthenticationActivity.this,"身份证号不能为空");
                    return ;
                }else{
                    handler.sendEmptyMessage(1);
                }
                break;
        }
    }

    private void getCode() {
        if(!CommonUtils.checkPhoneNumber(phoneNumber.getText().toString().trim())){
            MyToast.makeShortToast(AuthenticationActivity.this,"手机号码格式错误");
        }else{
            String url = ConfigInfo.CAPTCHA_URL+phoneNumber.getText().toString()+"&type=android";
            HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                public void onRequestComplete(String result) {
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        int code=jsonObject.getInt("Code");
                        if(code==1){
                            Log.i(ConfigInfo.TAG,"验证码："+jsonObject.getString("smscode"));
                            handler.sendEmptyMessage(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(ConfigInfo.TAG,"验证码——发送失败");
                        handler.sendEmptyMessage(3);//获取验证码失败
                    }
                }
            });
        }
    }


    // 倒数计时器
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            getCode.setClickable(true);
            getCode.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            getCode.setClickable(false);
            getCode.setText("再次发送验证码"+"("+millisUntilFinished / 1000+")");
        }
    }
}
