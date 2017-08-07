package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import sochat.so.com.android.R;
import sochat.so.com.android.eventbus.UpdateJPush;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DataCleanManager;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/2/27.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvChangePassword;
    private TextView tvWithWoosii;
    private TextView tvWithFeedback;
    private TextView tvExit;
    private TextView tvTitle;
    private ImageView ivTopBack;
    private RelativeLayout rlRemoveCache;
    private TextView tvCacheSize;
    private RelativeLayout rlCurrentVersion;
    private TextView tvCurrentVersion;
    private MyPopupWindow popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findById();
        inits();
        setListeners();

    }

    private void findById() {
        tvTitle = (TextView) findViewById(R.id.tv_top_text);
        tvChangePassword = (TextView) findViewById(R.id.tv_change_password);
        tvWithWoosii = (TextView) findViewById(R.id.tv_with_woosii);
        tvWithFeedback = (TextView) findViewById(R.id.tv_with_feedback);
        tvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
        tvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
        tvExit = (TextView) findViewById(R.id.tv_exit);

        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        rlRemoveCache = (RelativeLayout) findViewById(R.id.rl_remove_cache);
        rlCurrentVersion = (RelativeLayout) findViewById(R.id.rl_current_version);


    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        tvTitle.setText("设置");
        setCache();
        listener = new MyClick();
        popupWindow = new MyPopupWindow(this);
    }

    private void setListeners() {
        tvChangePassword.setOnClickListener(this);
        tvWithWoosii.setOnClickListener(this);
        tvWithFeedback.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        ivTopBack.setOnClickListener(this);
        rlRemoveCache.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.iv_top_back:
                finish();

                break;
            case R.id.tv_change_password:
                intent = new Intent(SettingActivity.this,ForgetPasswordActivity.class);
                intent.putExtra("to_change_title","修改密码");
                CommonUtils.startActivity(SettingActivity.this,intent);
                break;
            case R.id.tv_with_woosii:
                intent = new Intent(SettingActivity.this,WithWoosiiActivity.class);
                CommonUtils.startActivity(SettingActivity.this,intent);
                break;
            case R.id.tv_with_feedback:
                intent = new Intent(SettingActivity.this,FeedbackActivity.class);
                CommonUtils.startActivity(SettingActivity.this,intent);
                break;
            case R.id.tv_exit:
                DemoHelper.clear();
                EventBus.getDefault().post(new UpdateJPush());
                SettingActivity.this.finish();
                break;
            case R.id.rl_remove_cache:
                popupWindow.show(this);
                break;
        }
    }

    /**
     * 缓存
     */
    private void setCache() {
        try {
            tvCacheSize.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
            tvCacheSize.setText("0M");
        }
    }

    private TextView tvtitle;
    private TextView tvEnsure;
    private TextView tvCancel;
    private class MyPopupWindow extends BottomPushPopupWindow<Void> {
        public MyPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void aVoid) {
            View root = View.inflate(mBottomPopupWindowContext, R.layout.popup_cache, null);
            tvtitle = (TextView) root.findViewById(R.id.tv_up);
            tvEnsure = (TextView) root.findViewById(R.id.tv_down);
            tvCancel = (TextView) root.findViewById(R.id.cancel);

            tvtitle.setOnClickListener(listener);
            tvEnsure.setOnClickListener(listener);
            tvCancel.setOnClickListener(listener);

            return root;
        }
    }
    private MyClick listener;

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_up:
//                    popupWindow.dismiss();
                    break;
                case R.id.tv_down:
                    DataCleanManager.clearAllCache(SettingActivity.this);
                    setCache();
                    popupWindow.dismiss();
                    break;
                case R.id.cancel:
                    popupWindow.dismiss();

                    break;
            }
        }
    }




}
