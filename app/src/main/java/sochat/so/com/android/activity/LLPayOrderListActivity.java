package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.OrderListAdapter;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.OrderDetialModel;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/6/20.
 */

public class LLPayOrderListActivity extends BaseActivity {
    private ImageView ivBack;
    private TextView tvTopTitle;

//    private ViewpagerIndicatorOver indicator;
//    private List<String> titles ;
//    private ViewPager viewpager;
//    private List<Fragment>fragments;



    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private  static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private OrderListAdapter mDataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private PreviewHandler mHandler ;
    private List<OrderDetialModel> lists ;

    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private LLPayOrderListActivity mActivity;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ll_pay_order_detial);
        findById();
        inits();
        setListener();

    }

    private void findById() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);
        ivBack = (ImageView) findViewById(R.id.iv_top_back);
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

//        indicator = (ViewpagerIndicatorOver) findViewById(R.id.viewpager_indicator);
//        viewpager = (ViewPager) findViewById(R.id.viewpager_vr);
    }

    private void inits() {
        tvTopTitle.setText("交易记录");
        //Viewpager指示器的相关设置
//        titles = Arrays.asList("分成收益","收支明细");
//        indicator.setTabItemTitle(titles);
//        indicator.setVisiableTabCount(2);
//        indicator.setColorTabNormal(0xFF333333);
//        indicator.setColorTabSelected(0xFF15B422);
//        indicator.setWidthIndicatorLine(0.5f);
//        indicator.setLineBold(5);
//
//        fragments = new ArrayList<Fragment>();
//        fragments.add(new SixPirtyIncomeFragment());
//        fragments.add(new SixPirtyIncomeFragment());
//        fragments.add(new BuyMinutesFragment());
//        indicator.setViewPager(viewpager,0);
//        indicator.setViewPagerAdapter(getSupportFragmentManager(),fragments);

        mActivity = LLPayOrderListActivity.this;
        mHandler = new PreviewHandler(this);
        lists = new ArrayList<>();

        mDataAdapter = new OrderListAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        DividerDecoration divider = new DividerDecoration.Builder(mActivity)
                .setHeight(R.dimen.topLineThin)
                .setColorResource(R.color.chunk_gray2)
                .build();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);

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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LLPayOrderListActivity.this.finish();
            }
        });
    }


    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<LLPayOrderListActivity> ref;

        PreviewHandler(LLPayOrderListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LLPayOrderListActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    mCurrentCounter = activity.mDataAdapter.getItemCount();
                    mDataAdapter.setDataList(lists);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);

                    break;
                case -3:
//                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
//                    activity.notifyDataSetChanged();
//                    activity.mRecyclerView.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
//                        @Override
//                        public void reload() {
//                            requestData();
//                        }
//                    });

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
        parameters.put("user_id", DemoHelper.getUid());
        MyNetWorkUtil.getNovate(this, ConfigInfo.YES_UID, headers, parameters, ConfigInfo.NO_CACHE, ConfigInfo.NO_COOKIE, ConfigInfo.ApiUrl);


        MyNetWorkUtil.novate.get("/index.php/Vr/Record/llpay_record", parameters, new BaseSubscriber<ResponseBody>(this) {

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String jstr = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(jstr);
                    TOTAL_COUNTER = jsonObject.getInt("count");
                    String order_array = jsonObject.getString("Childs");
                    Log.i(ConfigInfo.TAG, "订单列表:" + order_array);

                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(order_array).getAsJsonArray();
                    Gson gson = new Gson();
                    //加强for循环遍历JsonArray
                    for (JsonElement user : jsonArray) {
                        //使用GSON，直接转成Bean对象
                        OrderDetialModel orderDetialModel = gson.fromJson(user, OrderDetialModel.class);
                        lists.add(orderDetialModel);
                    }
//                    Collections.reverse(lists);//数组倒序方法
                    mHandler.sendEmptyMessage(-1);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(ConfigInfo.TAG, "Throwable:" + e.getMessage());

            }
        });
    }

    }
