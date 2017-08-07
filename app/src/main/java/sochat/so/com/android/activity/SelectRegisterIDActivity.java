package sochat.so.com.android.activity;

import android.content.Intent;
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
import android.widget.TextView;

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
import sochat.so.com.android.eventbus.BackCityEvent;
import sochat.so.com.android.eventbus.BackRefreshEvent;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/3/24.
 * 选择管理员的身份
 */

public class SelectRegisterIDActivity extends BaseActivity {
    @Bind(R.id.top_back_forget)
    ImageView tvTopBack;
    @Bind(R.id.main_top_text_center)
    TextView tvTopTitle;
    @Bind(R.id.main_top_text_rignt)
    TextView tvTopRight;
    @Bind(R.id.tv_teacher)
    TextView tvTeacher;
    @Bind(R.id.tv_employee)
    TextView tvEmployee;
    @Bind(R.id.tv_woosii_manager)
    TextView tvWoosiiManager;
    @Bind(R.id.et_invite)
    EditText etInvite;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.et_register_phoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.et_register_captcha)
    EditText etCaptcha;
    @Bind(R.id.tv_register_captcha)
    TextView tvCaptcha;
    @Bind(R.id.et_register_input_phonePassword)
    EditText etPassword;
    @Bind(R.id.et_register_city)
    EditText etCity;
    @Bind(R.id.tv_normal_user)
    TextView tvNormalUser;

    /**
     * 计时器
     */
    private TimeCount timeCount;


    /**
     * 吐司
     */
    private MyToast toast;
    public static SelectRegisterIDActivity registerActivity;

    private String region_id;

    private boolean isSelectId = false;
    private String type = "0";
    private String message;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
//                    /index.php/Api/User/new_register?type=1&typephone=xx&phone=xxx&password=xxx&code=xxx(管理员)

                    String url = ConfigInfo.ApiUrl + "/index.php/Api/User/new_register?type="
                            + type + "&typephone="
                            + etInvite.getText().toString().trim()
                            + "&phone=" + etPhoneNumber.getText().toString().trim()
                            + "&password=" + Utility.md5(etPassword.getText().toString().trim())
                            + "&code=" + Utility.md5(etCaptcha.getText().toString().trim())
                            + "&region_id=" + region_id;
                    Log.i(ConfigInfo.TAG, "注册成为管理员：" + url);
                    HttpUtils.doGetAsyn(null, false, url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                message = jsonObject.getString("msg");
                                Log.i(ConfigInfo.TAG, "Code:" + code + ",message:" + message);
                                if (code == 0) {//手机号被注册
                                    handler.sendEmptyMessage(4);
                                } else if (code == 1) {//失败
                                    handler.sendEmptyMessage(5);
                                } else if (code == 4) {//成功
                                    handler.sendEmptyMessage(6);
                                } else if (code == 5) {//验证码错误
                                    handler.sendEmptyMessage(7);
                                } else if (code == 6) {//验证码失效
                                    handler.sendEmptyMessage(8);
                                } else if (code == 7) {//验证身份失败
                                    handler.sendEmptyMessage(9);
                                }else if (code == 9) {//验证身份失败
                                    handler.sendEmptyMessage(20);
                                } else {
                                    handler.sendEmptyMessage(10);//失败
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(11);//异常
                            }
                        }
                    });

                    break;
                case 1:
                    toast.showShortText(registerActivity, "已发送");
                    break;
                case 2:
                    toast.makeShortToast(registerActivity, "发送验证码失败");
                    break;
                case 3:
                    toast.showShortText(registerActivity, "网络异常，请重试");
                    break;
                case 4://
                    toast.makeShortToast(registerActivity, message);
                    break;
                case 5://
                    toast.makeShortToast(registerActivity, message);
                    break;
                case 6://
                    toast.makeShortToast(registerActivity, message);
//                    if (DemoHelper.getOne() ==0){
//
//                        Intent intent = new Intent(SelectRegisterIDActivity.this,AuthenticationActivity.class);
//                        CommonUtils.startActivity(SelectRegisterIDActivity.this,intent);
//                        SelectRegisterIDActivity.this.finish();
//                    }else{
//
//                    }

                    login();

                    break;
                case 7://
                    toast.makeShortToast(registerActivity, message);
                    break;
                case 8://
                    toast.makeShortToast(registerActivity, message);
                    break;
                case 9://
                    toast.makeShortToast(registerActivity, message);
                    break;
                case 10://
                    toast.makeShortToast(registerActivity, "发送验证码失败");
                    break;
                case 11://
                    toast.makeShortToast(registerActivity, "注册失败");
                    break;
                case 12://
                    DemoHelper.setPhone(etPhoneNumber.getText().toString().trim());
                    toast.makeShortToast(registerActivity, "登录成功");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EventBus.getDefault().post(new BackRefreshEvent(true));
                            Intent intent = new Intent(SelectRegisterIDActivity.this, MyMainActivity.class);
                            CommonUtils.startActivity(SelectRegisterIDActivity.this, intent);
                        }
                    }, 2000);

                    break;
                case 13://
                    toast.makeShortToast(registerActivity, "跳转登录失败");
                    break;
                case 14://
                    toast.makeShortToast(registerActivity, "号码不存在,请注册");
                    break;
                case 15://
                    toast.makeShortToast(registerActivity, "请输入正确的密码");
                    break;
                case 20://
                    toast.makeShortToast(registerActivity, "该地区小沃中心已存在");
                    break;
            }
        }
    };

    private void login() {
        String url = ConfigInfo.LOGIN_URL + etPhoneNumber.getText().toString().trim() + "&password=" + Utility.md5(etPassword.getText().toString().trim());
        Log.i(ConfigInfo.TAG, "手机登录url=" + url);
        HttpUtils.doGetAsyn(null, false, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 4) {
                        DemoHelper.setUid(jsonObject.getString("user_id"));
                        DemoHelper.setUserType(jsonObject.getString("user_type"));
                        DemoHelper.setRegion_id(jsonObject.getString("region_id"));
                        DemoHelper.setArea(jsonObject.getString("region_name"));
                        handler.sendEmptyMessage(12);//登录成功进行跳转
                    } else if (code == 3) {//号码不存在,请注册
                        handler.sendEmptyMessage(14);
                    } else if (code == 5) {//请输入正确的密码
                        handler.sendEmptyMessage(15);
                    } else {
                        handler.sendEmptyMessage(13);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }

            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_register_id);
        ButterKnife.bind(this);
        registerActivity = this;
        EventBus.getDefault().register(this);
        inits();
    }

    private void inits() {
        timeCount = new TimeCount(60000, 1000);
        tvTopTitle.setText("请选择您的身份");
        tvTopRight.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCity(BackCityEvent event) {
        etCity.setText(event.getMsg());
        region_id = event.getRegion_id();
    }

    @OnClick({R.id.top_back_forget, R.id.et_register_city,R.id.tv_normal_user, R.id.tv_teacher, R.id.tv_employee, R.id.tv_woosii_manager, R.id.tv_commit, R.id.tv_register_captcha})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.top_back_forget:
                SelectRegisterIDActivity.this.finish();
                break;
            case R.id.tv_normal_user:
                isSelectId = true;
                setInitView();
                tvNormalUser.setTextColor(0xFFFFFFFF);
                tvNormalUser.setBackgroundResource(R.drawable.circle_corner_bg_other);
                type = "0";
                DemoHelper.setUserType(type);
                break;
            case R.id.tv_teacher:
                isSelectId = true;
                setInitView();
                tvTeacher.setTextColor(0xFFFFFFFF);
                tvTeacher.setBackgroundResource(R.drawable.circle_corner_bg_other);
                type = "1";
                DemoHelper.setUserType(type);
                break;
            case R.id.tv_employee:
                isSelectId = true;
                setInitView();
                tvEmployee.setTextColor(0xFFFFFFFF);
                tvEmployee.setBackgroundResource(R.drawable.circle_corner_bg_other);
                type = "2";
                DemoHelper.setUserType(type);
                break;
            case R.id.tv_woosii_manager:
                isSelectId = true;
                setInitView();
                tvWoosiiManager.setTextColor(0xFFFFFFFF);
                tvWoosiiManager.setBackgroundResource(R.drawable.circle_corner_bg_other);
                type = "3";
                DemoHelper.setUserType(type);
                break;
            case R.id.tv_register_captcha:
                if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
                    MyToast.makeShortToast(this, "手机号码不能为空");
                } else {
                    timeCount.start();
                    getCaptcha();
                }
                break;
            case R.id.tv_commit:
                verifyId();
//                SelectRegisterIDActivity.this.finish();
                break;
            case R.id.et_register_city:
                Intent intent = new Intent(SelectRegisterIDActivity.this, ChooseCityActivity.class);
                CommonUtils.startActivity(SelectRegisterIDActivity.this, intent);
                break;
        }
    }

    private void verifyId() {
        if (!isSelectId) {
            toast.showShortText(this, "请至少选择一个身份");
        } else {
            if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
                toast.showShortText(this, "请输入手机号");
            } else {
                if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())) {
                    toast.showShortText(this, "请输入正确的手机号");
                } else {
                    if (TextUtils.isEmpty(etCaptcha.getText().toString().trim())) {
                        toast.showShortText(this, "请输入验证码");
                    } else {
                        if (TextUtils.isEmpty(etPassword.getText().toString().trim())) {
                            toast.showShortText(this, "请输入密码");
                        } else {
                            if (!CommonUtils.checkPhonePassword(etPassword.getText().toString().trim())) {
                                toast.showShortText(this, "密码只能是6位数字和字母组合");
                            } else {
                                if (TextUtils.isEmpty(etCity.getText().toString().trim())) {
                                    toast.showShortText(this, "选择城市不能为空");
                                } else {
                                    if (Integer.parseInt(type) == 0||Integer.parseInt(type) == 1|| !TextUtils.isEmpty(etInvite.getText().toString().trim())) {
                                        handler.sendEmptyMessage(0);
                                    } else {
                                        toast.showShortText(this, "请输入邀请码");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setInitView() {
        tvNormalUser.setTextColor(0xFF15B422);
        tvNormalUser.setBackgroundResource(R.drawable.circle_corner_bg);

        tvTeacher.setTextColor(0xFF15B422);
        tvTeacher.setBackgroundResource(R.drawable.circle_corner_bg);

        tvWoosiiManager.setTextColor(0xFF15B422);
        tvWoosiiManager.setBackgroundResource(R.drawable.circle_corner_bg);

        tvEmployee.setTextColor(0xFF15B422);
        tvEmployee.setBackgroundResource(R.drawable.circle_corner_bg);

    }

    private void getCaptcha() {
        try {
            if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())) {
                toast.makeShortToast(registerActivity, "手机号码格式错误");
            } else {
                String url = ConfigInfo.CAPTCHA_URL + etPhoneNumber.getText().toString() + "&type=android";
                HttpUtils.doGetAsyn(null, false, url, handler, new HttpUtils.CallBack() {
                    public void onRequestComplete(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int code = jsonObject.getInt("Code");
                            if (code == 1) {
                                Log.i(ConfigInfo.TAG, "验证码：" + jsonObject.getString("smscode"));
                                handler.sendEmptyMessage(1);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(3);//获取验证码失败
                        }
                    }
                });
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    // 倒数计时器
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tvCaptcha.setClickable(true);
            tvCaptcha.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            tvCaptcha.setClickable(false);
            tvCaptcha.setText("再次发送验证码" + "(" + millisUntilFinished / 1000 + ")");
        }
    }

}
