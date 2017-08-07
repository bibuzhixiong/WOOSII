package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.AttentionAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.AttentionModel;
import sochat.so.com.android.model.AttentionResult;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/3/16.
 */

public class MyAttentionActivity extends BaseActivity implements AttentionAdapter.setOnCancelAttentionListener{
    private TextView tvTopTitle;
    private ImageView ivTopBack;

    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private AttentionAdapter mAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private AttentionResult mResult;
    private MyAttentionActivity mActivity;




    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_attention);
        mActivity = MyAttentionActivity.this;
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
        pindex = 1;
        requestData();
    }

    private void inits() {
        childs = new ArrayList<AttentionModel>();
        lists = new ArrayList<AttentionModel>();
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopTitle.setText("我的关注");
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        mAdapter = new AttentionAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        //给接口赋值
        mAdapter.setOnCancelAttentionListener(this);

        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.topLineThick)
                .setColorResource(R.color.line_gray)
                .build();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        //add a HeaderView
//        final View header = LayoutInflater.from(this).inflate(R.layout.header_recyclerview,(ViewGroup)findViewById(android.R.id.content), false);
//        mLRecyclerViewAdapter.addHeaderView(header);

        /**
         * 下拉刷新的监听
         */
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mAdapter.clear();
                lists.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                pindex = 1;
                mCurrentCounter = 0;
                requestData();

            }
        });
        /**
         * 上拉加载的监听
         */
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    pindex++;
                    requestData();
                } else {
                    //the end
                    mRecyclerView.setNoMore(true);
                }
            }
        });

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

        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.black ,android.R.color.white);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.black ,android.R.color.white);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewColor(R.color.colorAccent, R.color.mainTextColor ,android.R.color.transparent);
        mRecyclerView.setFooterViewHint("拼命加载中","没有更多啦(=^ ^=)","网络不给力啊，点击再试一次吧");

//        mRecyclerView.refresh();
        requestData();

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAdapter.getDataList().size() > position) {
                    AttentionModel item = mAdapter.getDataList().get(position);
                    Intent intent = new Intent(mActivity, TeacherInfoActivity.class);
                    intent.putExtra("teacher_uid",item.getTeacher_id());
                    CommonUtils.startActivity(mActivity,intent);
                }
            }
        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
//                CourseChild item = mDataAdapter.getDataList().get(position);
//                MyToast.showShortText(mActivity, "onItemLongClick - " + item.getName());
            }
        });



    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 通过回调取消关注
     * @param position
     */
    @Override
    public void cancelAttention(int position) {
        String url = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/del_foll?user_id=" + DemoHelper.getUid() + "&teacher_id=" + mAdapter.getDataList().get(position).getTeacher_id();
        Log.i(ConfigInfo.TAG, "code1:" + url);
        HttpUtils.doGetAsyn(mActivity , true, url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {//失败
                        mHandler.sendEmptyMessage(0);
                    } else if (code == 1) {//成功
                        mHandler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private class PreviewHandler extends Handler {

        private WeakReference<MyAttentionActivity> ref;

        PreviewHandler(MyAttentionActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyAttentionActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    mCurrentCounter = activity.mAdapter.getItemCount();

                    pindex =1;
                    psize =10;
                    mAdapter.setDataList(lists);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);

                    break;
                case -3:
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    activity.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
                            requestData();
                        }
                    });

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

    private int pindex = 1;
    private int psize = 10;
    private List<AttentionModel> childs ;
    private List<AttentionModel> lists ;

    /**
     * 请求网络
     */
    private void requestData() {
        psize = 10;
//        final String url = "http://apptest.woosii.com//index.php/Vr/Vlive/school?type=1&pindex=1&psize=10";
        final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/r_foll?user_id="+ DemoHelper.getUid()+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(mActivity,true,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"url"+url);
                Log.i(ConfigInfo.TAG,"course_result"+result);
                Gson gson =new Gson();
                mResult = gson.fromJson(result,AttentionResult.class);
                if (mResult != null){
                    //获得总数
                    TOTAL_COUNTER = mResult.getCount();
                    //获得
                    childs = mResult.getChilds();
                    if (childs!=null){
                        lists.addAll(childs);
                    }
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);

                }
            }
        });

    }
}
