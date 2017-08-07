package sochat.so.com.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.OrderListAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.OrderDetialModel;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/6/24.
 */

public class SixPirtyIncomeFragment extends BaseFragment {
    private View view;
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private OrderListAdapter mDataAdapter = null;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private PreviewHandler mHandler ;
    private List<OrderDetialModel> lists ;

    private Map<String, Object> addparameters = new MyHashMap<String, Object>();
    //参数
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();


    @Override
    public String getFragmentName() {
        return "SixPirtyIncomeFragment";
    }

    private Activity mActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_six_pirty_income,container, false);
        findById();
        inits();
        setListener();
        return view;
    }

    private void findById() {
        mRecyclerView = (LRecyclerView) view.findViewById(R.id.list);
    }

    private void inits() {
        mHandler = new PreviewHandler(this);
        lists = new ArrayList<>();

        mDataAdapter = new OrderListAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        DividerDecoration divider = new DividerDecoration.Builder(mActivity)
                .setHeight(R.dimen.topLineThin)
                .setColorResource(R.color.chunk_gray2)
                .build();
        mRecyclerView.addItemDecoration(divider);

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

//        mRecyclerView.refresh();
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


    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<SixPirtyIncomeFragment> ref;

        PreviewHandler(SixPirtyIncomeFragment activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SixPirtyIncomeFragment activity = ref.get();
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
//        String day_end = DateUtil.getOldDate(-1);
//        String day_start = DateUtil.getOldDate(-29);
//
//        addparameters.clear();
//        parameters.clear();
//
//        addparameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
//        addparameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
//        addparameters.put("user_id", DemoHelper.getUid());
//        addparameters.put("dt_start", day_start);
//        addparameters.put("dt_end", day_end);
//
//        parameters.put("data",addparameters.toString());
//        parameters.put("url",ConfigInfo.LL_Pay_USERPAYMENT);

        parameters.put("user_id", DemoHelper.getUid());
        MyNetWorkUtil.getNovate(mActivity, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(mActivity, "/index.php/Vr/Record/six_record", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                String info =jsonObject.toString();
                try {
                        TOTAL_COUNTER = Integer.parseInt(jsonObject.getString("count"));
                        String order_array = jsonObject.getString("Childs");
                        Log.i(ConfigInfo.TAG,"订单列表:"+order_array);

                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(order_array).getAsJsonArray();
                        Gson gson = new Gson();
                        //加强for循环遍历JsonArray
                        for (JsonElement user : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            OrderDetialModel bankCardModel = gson.fromJson(user, OrderDetialModel.class);
                            lists.add(bankCardModel);
                        }
                        Collections.reverse(lists);
                        mHandler.sendEmptyMessage(-1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
