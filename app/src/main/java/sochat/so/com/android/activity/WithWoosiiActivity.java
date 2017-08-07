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
import sochat.so.com.android.utils.CommonUtils;

/**
 * Created by Administrator on 2017/4/11.
 */

public class WithWoosiiActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_version_update)
    TextView tvVersionUpdate;
    @Bind(R.id.tv_version_explain)
    TextView tvVersionExplain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_woosii);
        ButterKnife.bind(this);
        inits();
    }

    private void inits() {
    tvTopText.setText("关于沃噻");
    }

    @OnClick({R.id.iv_top_back, R.id.tv_version_update, R.id.tv_version_explain})
    public void onViewClicked(View view) {
        Intent intent ;
        switch (view.getId()) {
            case R.id.iv_top_back:
                WithWoosiiActivity.this.finish();
                break;
            case R.id.tv_version_update:
                break;
            case R.id.tv_version_explain:
                intent = new Intent(WithWoosiiActivity.this,WoosiiVersionExplainActivity.class);
                CommonUtils.startActivity(WithWoosiiActivity.this,intent);
                break;
        }
    }
}
