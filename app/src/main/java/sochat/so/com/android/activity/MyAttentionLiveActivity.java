package sochat.so.com.android.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.LiveAttentionAdapter;
import sochat.so.com.android.model.LiveAttentionResult;
import sochat.so.com.android.model.LiveAttrentionModel;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/3/16.
 */

public class MyAttentionLiveActivity extends BaseActivity {
    private TextView tvTopTitle;
    private ImageView ivTopBack;
    private LRecyclerView mRecyclerView = null;

    private LiveAttentionAdapter mAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private LiveAttentionResult mResult;
    private MyAttentionLiveActivity mActivity;




    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_live_attention);
        mActivity = MyAttentionLiveActivity.this;
        findById();
        inits();
        setListeners();

    }

    private void findById() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);

    }

    /**
     * 当Activity重新从后台回到前台时执行次方法
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if (lists !=null){
            lists.clear();
        }
        requestData();
    }

    private void inits() {
        lists = new ArrayList<LiveAttrentionModel>();
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopTitle.setText("我的关注");
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        //setLayoutManager must before setAdapter
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(manager);

        mAdapter = new LiveAttentionAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_4);
        mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(), Color.WHITE));

        //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
        GridItemDecoration divider = new GridItemDecoration.Builder(this)
                .setHorizontal(R.dimen.divider_height1)
                .setVertical(R.dimen.divider_height1)
                .setColorResource(R.color.white)
                .build();
//        mRecyclerView.addItemDecoration(divider);

//
//        DividerDecoration divider = new DividerDecoration.Builder(this)
//                .setHeight(R.dimen.topLineThick)
//                .setColorResource(R.color.line_gray)
//                .build();
//
//        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setHasFixedSize(true);




        //禁用下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);
        //禁用自动加载更多功能
        mRecyclerView.setLoadMoreEnabled(false);


        mRecyclerView.setLScrollListener(new LRecyclerView.LScrollListener() {

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }


            @Override
            public void onScrolled(int distanceX, int distanceY) {
            }

            @Override
            public void onScrollStateChanged(int state) {

            }

        });

//        mRecyclerView.refresh();
        requestData();

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAdapter.getDataList().size() > position) {
                    LiveAttrentionModel item = mAdapter.getDataList().get(position);
                    MyToast.makeShortToast(MyAttentionLiveActivity.this,item.getRoomid()+"");
                }
            }
        });

    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }


    private class PreviewHandler extends Handler {

        private WeakReference<MyAttentionLiveActivity> ref;

        PreviewHandler(MyAttentionLiveActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyAttentionLiveActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    mAdapter.setDataList(lists);
                    notifyDataSetChanged();
                    break;
                case -3:
                    mAdapter.setDataList(lists);
                    notifyDataSetChanged();
                    break;

                case 0:
                    MyToast.makeShortToast(mActivity,"取消关注失败");
                    break;

                case 1:
                    lists.clear();
                    requestData();
                    MyToast.makeShortToast(mActivity,"已取消关注");
                    break;

                default:
                    break;
            }
        }
    }


    private void setListeners() {
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private List<LiveAttrentionModel> lists ;

    /**
     * 请求网络
     */
    private void requestData() {
        final String url = ConfigInfo.ApiUrl+"/index.php/Api/Wylive/follow_list?user_id="+DemoHelper.getUid();
        HttpUtils.doGetAsyn(mActivity,true,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"url"+url);
                Log.i(ConfigInfo.TAG,"course_result"+result);
                Gson gson =new Gson();
                mResult = gson.fromJson(result,LiveAttentionResult.class);
                if (mResult != null){
                    lists = mResult.getChild();
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);

                }
            }
        });

    }
}
