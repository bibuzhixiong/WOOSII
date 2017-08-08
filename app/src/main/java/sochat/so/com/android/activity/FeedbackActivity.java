package sochat.so.com.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sochat.so.com.android.R;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/2/27.
 */

public class FeedbackActivity extends BaseActivity implements View.OnClickListener{
    private EditText etContent;
//    private TextView tvAdd;
    private EditText etContact;
    private TextView tvCommit;
    private TextView tvTopText;
    private ImageView ivTopBack;
    private Toast toast;
    private ProgressDialog progressDialog=new ProgressDialog();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    CommonUtils.showDialogs("正在加载...",FeedbackActivity.this,progressDialog);
                    String url = null;
                    try {
                        url = ConfigInfo.ANSWER_FEEDBACK+"content="+(etContent.getText().toString()!=null? URLEncoder.encode(etContent.getText().toString(),"UTF-8"):etContent.getText().toString())+"&phone="+etContact.getText().toString();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(ConfigInfo.TAG,"url:"+url);
                    HttpUtils.doGetAsyn(null,false,url, handler, new HttpUtils.CallBack() {
                            @Override
                            public void onRequestComplete(String result) {
                                Log.i(ConfigInfo.TAG,"result:"+result);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    int code = jsonObject.getInt("code");
                                    if(code == 0){//参数不完整
                                    handler.sendEmptyMessage(1);
                                    }else if(code ==1){//成功
                                        handler.sendEmptyMessage(2);
                                    }else {//提交反馈失败，请重试
                                       handler.sendEmptyMessage(3);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(4);
                                }
                            }
                        });
                    break;

                case 1:
                    toast.makeText(FeedbackActivity.this,"您没有提交任何内容",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    etContact.setText("");
                    etContent.setText("");
                    toast.makeText(FeedbackActivity.this,"提交反馈成功",Toast.LENGTH_SHORT).show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FeedbackActivity.this.finish();
                        }
                    },2000);
                    break;
                case 3:
                    toast.makeText(FeedbackActivity.this,"提交失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    toast.makeText(FeedbackActivity.this,"提交失败，请检查您的网络",Toast.LENGTH_SHORT).show();
                    break;
            }
            progressDialog.destroy();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        findById();
        inits();
        setListeners();
    }

    private void findById() {
        tvTopText = (TextView) findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        etContent = (EditText) findViewById(R.id.et_content);
        etContact = (EditText) findViewById(R.id.et_contact);
//        tvAdd = (TextView) findViewById(R.id.tv_add);
        tvCommit = (TextView) findViewById(R.id.tv_commit);
    }

    //当点击EditText外部时隐藏软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopText.setText("意见反馈");
        toast= new Toast(FeedbackActivity.this);
    }

    private void setListeners() {
//        tvAdd.setOnClickListener(this);
        tvCommit.setOnClickListener(this);
        ivTopBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_commit:
                feedback();

                break;
            case R.id.iv_top_back:
                FeedbackActivity.this.finish();
                break;
        }
    }

    private void feedback() {
        if(TextUtils.isEmpty(etContent.getText().toString().trim())){
            toast.makeText(FeedbackActivity.this,"请输入您要反馈的内容",Toast.LENGTH_SHORT).show();
            return ;
        }else{
            handler.sendEmptyMessage(0);
        }
    }
}
