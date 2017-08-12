package sochat.so.com.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import sochat.so.com.android.R;
import sochat.so.com.android.utils.CommonUtils;

/**
 * Created by Administrator on 2017/4/11.
 */

public class WoosiiVersionExplainActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.tv_version)
    TextView tv_version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_explain);
        ButterKnife.bind(this);

        tv_version.setText("沃噻教育 V"+CommonUtils.getVersionName(this));
        tvTopText.setText("说明");
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WoosiiVersionExplainActivity.this.finish();
            }
        });
    }


}
