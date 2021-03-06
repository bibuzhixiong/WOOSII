package sochat.so.com.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/6/15.
 */

public class LLPaySMSCodeDialog extends Dialog {
    private TextView tvPayMoney;
    private ImageView ivDelete;

    private ImageView ivPassword1;
    private ImageView ivPassword2;
    private ImageView ivPassword3;
    private ImageView ivPassword4;
    private ImageView ivPassword5;
    private ImageView ivPassword6;
    private EditText etPayPaswordInVisiable;

    /**
     * 存放密码图的数组
     */
    private List<ImageView> imageViews;
    private LLPaySMSCodeDialog dialog;


    public LLPaySMSCodeDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        dialog = this;
    }

    public LLPaySMSCodeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected LLPaySMSCodeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ll_pay_sms_code);
        inits();
        listeners();
    }

    private void inits() {
        tvPayMoney = (TextView) findViewById(R.id.tv_pay_money);
        ivDelete = (ImageView) findViewById(R.id.iv_delete);

        ivPassword1 = (ImageView) findViewById(R.id.iv_password1);
        ivPassword2 = (ImageView) findViewById(R.id.iv_password2);
        ivPassword3 = (ImageView) findViewById(R.id.iv_password3);
        ivPassword4 = (ImageView) findViewById(R.id.iv_password4);
        ivPassword5 = (ImageView) findViewById(R.id.iv_password5);
        ivPassword6 = (ImageView) findViewById(R.id.iv_password6);

        etPayPaswordInVisiable = (EditText) findViewById(R.id.et_pay_password_invisible);

        //初始化存放密码图片的数组
        imageViews = new ArrayList<>();
        imageViews.add(ivPassword1);
        imageViews.add(ivPassword2);
        imageViews.add(ivPassword3);
        imageViews.add(ivPassword4);
        imageViews.add(ivPassword5);
        imageViews.add(ivPassword6);



        etPayPaswordInVisiable.addTextChangedListener(new TextWatcher() {
            private int inputstart;
            private int inputchanged;

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
                inputstart =arg0.length();
            }

            /**
             *
             * @param cs   是输入后的EditText的文本
             * @param start 输入前的位置
             * @param before 不管他，是输入前要替换的位置
             * @param count 改变的数量
             */
            @Override
            public void onTextChanged(CharSequence cs, int start, int before,int count) {
                inputchanged = cs.length();
                if (inputchanged>inputstart){
                    imageViews.get(start).setVisibility(View.VISIBLE);
                }else{
                    imageViews.get(start).setVisibility(View.INVISIBLE);
                }
                if (inputchanged == 6){
                    inputSMSCodeCallBack.inputSMSCodeCallBack(etPayPaswordInVisiable.getText().toString());
                }

            }
        });
    }

    private void listeners() {
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void setPayMoney(String money){
        if (tvPayMoney!=null){
            tvPayMoney.setText("¥"+money);
        }
    }

    public void setClearInputPassword(){
        if (etPayPaswordInVisiable!=null){
            etPayPaswordInVisiable.setText("");

            ivPassword1.setVisibility(View.INVISIBLE);
            ivPassword2.setVisibility(View.INVISIBLE);
            ivPassword3.setVisibility(View.INVISIBLE);
            ivPassword4.setVisibility(View.INVISIBLE);
            ivPassword5.setVisibility(View.INVISIBLE);
            ivPassword6.setVisibility(View.INVISIBLE);

        }
    }

    private LLPayInputSMSCodeCallBack inputSMSCodeCallBack;
    public interface LLPayInputSMSCodeCallBack {
        void inputSMSCodeCallBack(String sms_code);
    }
    public void setInputSMSCodeCallBack(LLPayInputSMSCodeCallBack inputSMSCodeCallBack){
        this.inputSMSCodeCallBack =inputSMSCodeCallBack;
    }
}
