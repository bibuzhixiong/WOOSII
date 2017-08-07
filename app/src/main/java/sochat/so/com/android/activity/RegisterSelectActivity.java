package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sochat.so.com.android.R;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/3/24.
 * 选择是否是管理员注册
 */

public class RegisterSelectActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvGeneral;
    private TextView tvManager;
    private ImageView ivTopBack;
    private TextView tvTopTitle;
    private TextView tvTopRight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_select);
        findById();
        inits();
        setListeners();

    }

    private void findById() {
        tvGeneral = (TextView) findViewById(R.id.tv_general);
        tvManager = (TextView) findViewById(R.id.tv_manager);

        tvTopTitle = (TextView) findViewById(R.id.main_top_text_center);
        tvTopRight = (TextView) findViewById(R.id.main_top_text_rignt);
        ivTopBack = (ImageView) findViewById(R.id.top_back_forget);


    }

    private void inits() {
        tvTopTitle.setText("选择注册身份");
        tvTopRight.setVisibility(View.GONE);

    }

    private void setListeners() {
        tvGeneral.setOnClickListener(this);
        tvManager.setOnClickListener(this);
        ivTopBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.top_back_forget:
                RegisterSelectActivity.this.finish();
                break;
            case R.id.tv_general:
                setInitView();
                tvGeneral.setTextColor(0xFFFFFFFF);
                tvGeneral.setBackgroundResource(R.drawable.circle_corner_text_bg_other);
                DemoHelper.setUserType("0");
                intent = new Intent(RegisterSelectActivity.this,RegisterActivity.class);
                CommonUtils.startActivity(RegisterSelectActivity.this,intent);
                RegisterSelectActivity.this.finish();
                break;
            case R.id.tv_manager:
                setInitView();
                tvManager.setTextColor(0xFFFFFFFF);
                tvManager.setBackgroundResource(R.drawable.circle_corner_text_bg_other);
                intent = new Intent(RegisterSelectActivity.this,SelectRegisterIDActivity.class);
                CommonUtils.startActivity(RegisterSelectActivity.this,intent);
                RegisterSelectActivity.this.finish();
                break;

        }
    }

    private void setInitView(){
        tvGeneral.setTextColor(0xFF15B422);
        tvGeneral.setBackgroundResource(R.drawable.circle_corner_text_bg);
        tvManager.setTextColor(0xFF15B422);
        tvManager.setBackgroundResource(R.drawable.circle_corner_text_bg);
    }



}
