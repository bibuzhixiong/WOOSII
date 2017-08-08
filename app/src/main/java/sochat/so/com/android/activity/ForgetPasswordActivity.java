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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sobase.rtiai.util.common.Utility;
import sochat.so.com.android.R;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/2/24.
 */

public class ForgetPasswordActivity extends BaseActivity {
    @Bind(R.id.top_back_forget)
    ImageView topBack;
    @Bind(R.id.main_top_text_center)
    TextView tvTopTextCenter;
    @Bind(R.id.main_top_text_rignt)
    TextView tvTopTextRignt;
    @Bind(R.id.et_forget_phoneNumber)
    EditText etPhoneNumber;
    @Bind(R.id.et_forget_captcha)
    EditText etCaptcha;
    @Bind(R.id.tv_forget_captcha)
    TextView tvCaptcha;
    @Bind(R.id.et_forget_newPassword)
    EditText etNewPassword;
    @Bind(R.id.tv_commit)
    TextView tvCommit;
    @Bind(R.id.et_forget_newPassword_Commit)
    EditText etForgetNewPasswordCommit;
    /**
     * 计时器
     */
    private TimeCount timeCount;
    /**
     * 吐司
     */
    private MyToast toast;
    /**
     * 提示框
     */
    private ProgressDialog progressDialog = new ProgressDialog();

    private ForgetPasswordActivity forgetPasswordActivity;

    /**
     * Handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String url = ConfigInfo.FORGET_PASSWORD_URL
                            + etPhoneNumber.getText().toString().trim()
                            + "&password=" + Utility.md5(etNewPassword.getText().toString().trim())
                            + "&code=" + Utility.md5(etCaptcha.getText().toString().trim());
                    HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {//手机号为空
                                    handler.sendEmptyMessage(1);
                                } else if (code == 1) {//验证码错误
                                    handler.sendEmptyMessage(2);
                                } else if (code == 2) {//请输入新密码
                                    handler.sendEmptyMessage(4);
                                } else if (code == 3) {//成功
                                    handler.sendEmptyMessage(5);
                                } else if (code == 4) {//失败
                                    handler.sendEmptyMessage(6);
                                } else if (code == 5) {//手机号未注册
                                    handler.sendEmptyMessage(7);
                                } else if (code == 6) {//验证码失效
                                    handler.sendEmptyMessage(8);
                                } else {
                                    handler.sendEmptyMessage(9);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    break;

                case 1:
                    toast.makeShortToast(forgetPasswordActivity, "手机号为空");
                    break;

                case 2:
                    toast.makeShortToast(forgetPasswordActivity, "验证码错误");
                    break;
                case 3:
                    toast.makeShortToast(forgetPasswordActivity, "获取验证码失败");
                    break;
                case 4:
                    toast.makeShortToast(forgetPasswordActivity, "请输入新密码");
                    break;

                case 5:
                    toast.makeShortToast(forgetPasswordActivity, "修改密码成功");
                    Bundle bundle = new Bundle();
                    bundle.putString("usr", etPhoneNumber.getText().toString().trim());
                    bundle.putString("pwd", etNewPassword.getText().toString().trim());
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                    intent.putExtra("data_return_ForgetPasswordActivity", bundle);
                    setResult(RESULT_OK, intent);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ForgetPasswordActivity.this.finish();
                        }
                    }, 2000);
                    finish();
                    break;
                case 6:
                    toast.makeShortToast(forgetPasswordActivity, "修改密码失败");
                    break;
                case 7:
                    toast.makeShortToast(forgetPasswordActivity, "该手机号未注册");
                    break;

                case 8:
                    toast.makeShortToast(forgetPasswordActivity, "验证码失效");
                    break;
                case 9:
                    toast.makeShortToast(forgetPasswordActivity, "修改密码失败");
                    break;
                case 11:
                    toast.makeShortToast(forgetPasswordActivity, "已发送");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        forgetPasswordActivity = this;
        inits();
    }

    private void inits() {
        Intent intent = getIntent();

        timeCount = new TimeCount(60000, 1000);
        topBack.setVisibility(View.VISIBLE);
        tvTopTextRignt.setVisibility(View.GONE);
        tvTopTextCenter.setText(intent.getStringExtra("to_change_title"));


    }

    @OnClick({R.id.top_back_forget, R.id.tv_forget_captcha, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top_back_forget:
                forgetPasswordActivity.finish();
                break;
            case R.id.tv_forget_captcha:
                if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
                    toast.makeShortToast(ForgetPasswordActivity.this, "手机号码不能为空");
                } else {
                    getCaptcha();
                }
                break;
            case R.id.tv_commit:
                forget();
                break;
        }
    }

    /**
     * 忘记密码
     */
    private void forget() {
        CommonUtils.showDialogs("加载中...", this, progressDialog);
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "账号不能为空");
            progressDialog.destroy();
            return;
        } else if (TextUtils.isEmpty(etNewPassword.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "密码不能为空");
            progressDialog.destroy();
            return;
        } else if (TextUtils.isEmpty(etForgetNewPasswordCommit.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "确认新密码不能为空");
            progressDialog.destroy();
            return;
        } else if (!etNewPassword.getText().toString().trim().equals(etForgetNewPasswordCommit.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "两次输入密码不同");
            progressDialog.destroy();
            return;
        } else if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "请输入正确的手机号");
            progressDialog.destroy();
            return;
        } else if (!CommonUtils.checkPhonePassword(etNewPassword.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "密码只能是数字或字母组合");
            progressDialog.destroy();
            return;
        } else if (etCaptcha.getText().toString().isEmpty()) {
            toast.makeShortToast(forgetPasswordActivity, "请输入验证码");
            progressDialog.destroy();
            return;
        } else {
//            md5Encode = Utility.md5(etCaptcha.getText().toString());
            handler.sendEmptyMessage(0);//去登陆
        }
    }

    /**
     * 获取验证码
     */
    private void getCaptcha() {
        if (!CommonUtils.checkPhoneNumber(etPhoneNumber.getText().toString().trim())) {
            toast.makeShortToast(forgetPasswordActivity, "手机号码格式错误");
        } else {
            timeCount.start();
            String url = ConfigInfo.CAPTCHA_URL + etPhoneNumber.getText().toString() + "&type=android";
            HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                public void onRequestComplete(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("Code");
                        if (code == 1) {
                            Log.i(ConfigInfo.TAG,"ForgetPassword_code:"+jsonObject.getString("smscode"));
                            handler.sendEmptyMessage(11);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
            tvCaptcha.setClickable(true);
            tvCaptcha.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            tvCaptcha.setClickable(false);
            tvCaptcha.setText("再次发送验证码" + "(" + millisUntilFinished / 1000 + ")");
        }
    }

}
