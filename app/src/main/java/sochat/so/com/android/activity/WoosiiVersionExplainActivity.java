package sochat.so.com.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/4/11.
 */

public class WoosiiVersionExplainActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_explain);
        ButterKnife.bind(this);
        tvTopText.setText("说明");
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WoosiiVersionExplainActivity.this.finish();
            }
        });
    }
}
