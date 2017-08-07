package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/4/10.
 */

public class UnrentCheckActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.iv_shenqing_tuizu)
    ImageView ivShenqingTuizu;
    @Bind(R.id.line1)
    View line1;
    @Bind(R.id.tv_shenqing_tuizu)
    TextView tvShenqingTuizu;
    @Bind(R.id.line2)
    View line2;
    @Bind(R.id.iv_tuizu_shenhe)
    ImageView ivTuizuShenhe;
    @Bind(R.id.line3)
    View line3;
    @Bind(R.id.tv_tuizu_shenhe)
    TextView tvTuizuShenhe;
    @Bind(R.id.tv_tuizu_shenhe_time_year_mouth_day)
    TextView tvTuizuShenheTimeYearMouthDay;
    @Bind(R.id.tv_tuizu_shenhe_time_second)
    TextView tvTuizuShenheTimeSecond;
    @Bind(R.id.iv_tuizu_success)
    ImageView ivTuizuSuccess;
    @Bind(R.id.line4)
    View line4;
    @Bind(R.id.tv_tuizu_success)
    TextView tvTuizuSuccess;
    @Bind(R.id.tv_tuizu_success_time_year_mouth_day)
    TextView tvTuizuSuccessTimeYearMouthDay;
    @Bind(R.id.tv_tuizu_success_time_second)
    TextView tvTuizuSuccessTimeSecond;

    private String unRentState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unrent_check);
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
        Intent intent = getIntent();
//        unRentState = intent.getStringExtra("unrent_state");
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopText.setText("退租审核");
    }

    @OnClick(R.id.iv_top_back)
    public void onViewClicked(View view) {
        UnrentCheckActivity.this.finish();
    }
}
