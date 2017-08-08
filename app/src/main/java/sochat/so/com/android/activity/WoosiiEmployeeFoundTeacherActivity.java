package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Throwable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.WoosiiEmployeeFoundTeacherAdapter;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.WoosiiEmployeeFoundTeacherModel;
import sochat.so.com.android.model.WoosiiEmployeeFoundTeacherResult;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;

/**
 * 小沃员工找的老师的列表
 * Created by Administrator on 2017/6/28.
 */

public class WoosiiEmployeeFoundTeacherActivity extends BaseActivity {
    private TextView tvTopTitle;
    private ImageView ivTopBack;
    private ImageView ivEmployeePhoto;

    private TextView tvEmployeeName;
    private TextView tvEmployeeTel;
    private TextView tvEmployeeAddress;
    private TextView tvEmployeeYesterdayIncome;
    private TextView tvEmployeeAllIncome;


    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private WoosiiEmployeeFoundTeacherAdapter mDataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private PreviewHandler mHandler ;
    private List<WoosiiEmployeeFoundTeacherModel> lists ;

    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private WoosiiEmployeeFoundTeacherActivity mActivity;
    /**
     * 通过Gson解析
     */
    private WoosiiEmployeeFoundTeacherResult mResult;
    /**
     * 头部的数据
     */
    private String topTel;
    private String topAddress;
    private String topYesterdayIncome;
    private String topAllIncome;
    private String topEmployeeName;
    private String topThumbUrl;

    private String employee_user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_woosii_employee_found_teacher);
        findById();
        inits();
        setListener();
    }

    private void findById() {
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

    }

    private void inits() {
        employee_user_id = getIntent().getStringExtra("employee_user_id");
        mActivity = WoosiiEmployeeFoundTeacherActivity.this;
        mHandler = new PreviewHandler(this);
        lists = new ArrayList<>();

        mResult = new WoosiiEmployeeFoundTeacherResult();

        mDataAdapter = new WoosiiEmployeeFoundTeacherAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
/**
 * 分割线
 */
//        DividerDecoration divider = new DividerDecoration.Builder(mActivity)
//                .setHeight(R.dimen.topLineThin)
//                .setColorResource(R.color.chunk_gray2)
//                .build();
//
//        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.mianColor, R.color.black ,android.R.color.white);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.mianColor, R.color.black ,android.R.color.white);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewColor(R.color.mianColor, R.color.mainTextColor ,android.R.color.transparent);
        mRecyclerView.setFooterViewHint("拼命加载中","没有更多啦(=^ ^=)","网络不给力啊，点击再试一次吧");


        //add a HeaderView
        final View header = LayoutInflater.from(this).inflate(R.layout.header_woosii_employee_found_teacher,(ViewGroup)findViewById(android.R.id.content), false);
        mLRecyclerViewAdapter.addHeaderView(header);

        /**
         * 初始化头布局
         */
        findHeadView();
        /**
         * 请求数据
         */
        requestData();
    }

    private void setListener() {
        /**
         * 下拉刷新的监听
         */
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDataAdapter.clear();
                lists.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
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
                    requestData();
                } else {
                    mRecyclerView.setNoMore(true);
                }
            }
        });

    }

    private void findHeadView() {
        View headView = mLRecyclerViewAdapter.getHeaderView();
        tvTopTitle = (TextView) headView.findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) headView.findViewById(R.id.iv_top_back);
        ivEmployeePhoto = (ImageView) headView.findViewById(R.id.iv_employee_photo);

        tvEmployeeName = (TextView) headView.findViewById(R.id.tv_employee_name);
        tvEmployeeTel = (TextView) headView.findViewById(R.id.tv_employee_tel);
        tvEmployeeAddress = (TextView) headView.findViewById(R.id.tv_employee_address);
        tvEmployeeYesterdayIncome = (TextView) headView.findViewById(R.id.tv_employee_yesterday_income);
        tvEmployeeAllIncome = (TextView) headView.findViewById(R.id.tv_employee_all_income);

        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WoosiiEmployeeFoundTeacherActivity.this.finish();
            }
        });
    }

    private void showEmployeeInfo(){
        tvEmployeeName.setText(topEmployeeName);
        tvEmployeeTel.setText(topTel);
        tvEmployeeAddress.setText(topAddress);

        int endOne = 5+topYesterdayIncome.length();
        tvEmployeeYesterdayIncome.setText(CommonUtils.setTextViewPartColorChange("昨日收益 "+topYesterdayIncome+" 元",5,endOne,0xFF15B422));
        int endTwo = 5+topAllIncome.length();
        tvEmployeeAllIncome.setText(CommonUtils.setTextViewPartColorChange("累计收益 "+topAllIncome+" 元",5,endTwo,0xFF15B422));


        Log.i(ConfigInfo.TAG,"这里是什么呢："+topThumbUrl);
        if (TextUtils.isEmpty(topThumbUrl)){
            ivEmployeePhoto.setImageResource(R.drawable.morentouxiang);
        }else{
            Picasso.with(this).load(ConfigInfo.ApiUrl+topThumbUrl).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivEmployeePhoto);
        }

    }


    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<WoosiiEmployeeFoundTeacherActivity> ref;

        PreviewHandler(WoosiiEmployeeFoundTeacherActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WoosiiEmployeeFoundTeacherActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    showEmployeeInfo();
                    mCurrentCounter = activity.mDataAdapter.getItemCount();
                    if (lists!=null){
                        mDataAdapter.setDataList(lists);
                        activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    }


                    break;
                case -3:
                    showEmployeeInfo();
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    activity.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
                            requestData();
                        }
                    });

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获得数据
     */
    private void requestData() {
        parameters.put("user_id", employee_user_id);
        MyNetWorkUtil.getNovate(this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);


        MyNetWorkUtil.novate.get("/index.php/Vr/Vlive/re_teacher", parameters,new BaseSubscriber<ResponseBody>(this){

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String jstr = new String(responseBody.bytes());
                    Gson gson = new Gson();
                    mResult = gson.fromJson(jstr,WoosiiEmployeeFoundTeacherResult.class);
                    topEmployeeName = mResult.getM_name();
                    topAddress = mResult.getM_region_id();
                    topTel = mResult.getM_tel();
                    topYesterdayIncome = mResult.getM_day_money();
                    topAllIncome = mResult.getM_money();
                    topThumbUrl = mResult.getM_thumb();
                    lists = mResult.getChild();
                        mHandler.sendEmptyMessage(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                topEmployeeName = "小沃员工";
                topAddress = "小沃中心";
                topTel = "***********";
                topYesterdayIncome = "0";
                topAllIncome = "0";
                topThumbUrl = "";
                mHandler.sendEmptyMessage(-3);
                Log.i(ConfigInfo.TAG,"Throwable:"+e.getMessage());

            }
        });


    }
}
