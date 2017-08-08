package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.MoreRecommendAdapter;
import sochat.so.com.android.model.MostNewResult;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/5/8.
 */

public class MoreRecommentActivity extends BaseActivity {
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;
    @Bind(R.id.list)
    RecyclerView mRecyclerView;


    private MoreRecommendAdapter mAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(MoreRecommentActivity.this);
    private MoreRecommentActivity mActivity;

    private List<TeacherCourseList>lists;
    private MyOnClickListener listener;
    private String more_url ="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_recommend);
        ButterKnife.bind(this);
        mActivity = MoreRecommentActivity.this;
        inits();
        setlisteners();
    }

    private void setlisteners() {
        listener = new MyOnClickListener();
        ivTopBack.setOnClickListener(listener);
    }

    private void inits() {
        Intent intent = getIntent();
        more_url = intent.getStringExtra("more_url");

        tvTopText.setText("更多推荐");
        lists = new ArrayList<TeacherCourseList>();
        mNewResult = new MostNewResult();
        mAdapter = new MoreRecommendAdapter(mActivity,lists);

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManage = new GridLayoutManager(this, 2);
        layoutManage.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position%5==0)
                    return 2;
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManage);
        mRecyclerView.setAdapter(mAdapter);

        requestData();

    }



    private class PreviewHandler extends Handler {

        private WeakReference<MoreRecommentActivity> ref;

        PreviewHandler(MoreRecommentActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MoreRecommentActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    mAdapter.setLists(lists);

                    break;
                case -3:

                    break;

                case 0:
                    break;

            }
        }
    }


    private List<TeacherCourseList> childs ;
    private MostNewResult mNewResult ;
    /**
     * 请求网络
     */
    private void requestData() {
        String url = more_url+DemoHelper.getUid();
        HttpUtils.doGetAsyn(null, false, url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG, "news_result:" + result);
                if (result.length()>10){
                    Gson gson = new Gson();
                    mNewResult = gson.fromJson(result, MostNewResult.class);
                    lists = mNewResult.getChilds();
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);
                }

            }
        });

    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_top_back:
                    MoreRecommentActivity.this.finish();
                    break;
            }

        }
    }
}
