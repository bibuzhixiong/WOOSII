package sochat.so.com.android.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * 用户协议
 */
public class WosiiUserProblem extends BaseActivity implements OnClickListener{
	private ImageView topback;
	private TextView tittleText;
	private WebView mwebView;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.score_webview);
		// 初始化控件
		findById();
		// 初始化视图
		initData();
		// 设置监听
		setListener();
	}

	private void findById() {
		tittleText = (TextView) findViewById(R.id.tv_top_text);
		topback = (ImageView) findViewById(R.id.iv_top_back);
		topback.setVisibility(View.VISIBLE);
		mwebView = (WebView) findViewById(R.id.score_webView);
	}

	private void initData() {
		tittleText.setText("用户协议");
		  //设置支持缩放  
//		mwebView.setBuiltInZoomControls(true);  
        //加载需要显示的网页
		mwebView.loadUrl(ConfigInfo.WOOSII_AGREEMENT_URL);
	}

	private void setListener() {
		topback.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_top_back:
			finish();
			break;
		default:
			break;
		}
	}
}
