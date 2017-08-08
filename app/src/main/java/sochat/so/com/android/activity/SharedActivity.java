package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.SharedUtils;

/**
 * Created by Administrator on 2017/2/27.
 */

public class SharedActivity extends BaseActivity {
    @Bind(R.id.tv_share)
    TextView tvShare;
    private TextView tvTiTle;
    //分享的图片
    private String image;
    //分享的缩略图
    private String thumb;
    //分享的地址
    private String url;
    private ImageView topBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        ButterKnife.bind(this);
        inits();
        setListeners();
    }

    private void inits() {
        tvTiTle = (TextView) findViewById(R.id.tv_top_text);
        tvTiTle.setText("分享");

    }

    private void setListeners() {
        topBack = (ImageView) findViewById(R.id.iv_top_back);
        topBack.setImageResource(R.drawable.back_arrow);
        topBack.setVisibility(View.VISIBLE);
        topBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        Log.i(ConfigInfo.TAG, "分享的回调");
    }

    @OnClick(R.id.tv_share)
    public void onClick() {
        url = ConfigInfo.WOOSII_DOWNLOAD_URL+ DemoHelper.getUid();
        SharedUtils.ShareWeb(url, "这是标题", "这里是一段优美的文字，我是爱凤凤的，凤凤也爱我", SharedActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedActivity.this.finish();
    }
}
