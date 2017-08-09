package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.jdsjlzx.ItemDecoration.GridItemDecoration;
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.MyAttentionLiveActivity;
import sochat.so.com.android.adapter.RecommendLivingAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.activity.EnterLiveActivity;
import sochat.so.com.android.live.activity.LiveRoomActivity;
import sochat.so.com.android.model.CourseRecommendLiving;
import sochat.so.com.android.model.RecommentLivingModel;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * 直播的fragment
 * Created by Administrator on 2017/7/7.
 */

public class LiveFragment extends BaseFragment implements View.OnClickListener{
    private View view;

    private ImageView ivStartLive;
    private ImageView ivAttention;
    private ImageView ivPrivateMsg;
    private LinearLayout ll_start_live,ll_attention,ll_private_msg;


    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private RecommendLivingAdapter mDataAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;



    public static final int MODE_ROOM = 0;//房间号
    public static final int MODE_ADDRESS =1;//拉流地址
    private int currentMode = MODE_ROOM;
    private boolean cancelEnterRoom;

    @Override
    public String getFragmentName() {
        return "LiveFragment";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * 新建Fragment
     */
    public static LiveFragment newInstance() {
        LiveFragment fragment = new LiveFragment();
        return fragment;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_live, container, false);
        findById();
        inits();
        setlistener();
        return view;
    }

    private void findById() {
        mRecyclerView = (LRecyclerView) view.findViewById(R.id.list);
//        ivAttention = (ImageView) view.findViewById(R.id.iv_attention);
//        ivStartLive = (ImageView) view.findViewById(R.id.iv_start_live);
//        ivPrivateMsg = (ImageView) view.findViewById(R.id.iv_private_msg);

        ll_start_live = (LinearLayout) view.findViewById(R.id.ll_start_live);
        ll_attention = (LinearLayout) view.findViewById(R.id.ll_attention);
        ll_private_msg = (LinearLayout) view.findViewById(R.id.ll_private_msg);

//        ivAttention.setOnClickListener(this);
//        ivStartLive.setOnClickListener(this);
//        ivPrivateMsg.setOnClickListener(this);

        ll_start_live.setOnClickListener(this);
        ll_attention.setOnClickListener(this);
        ll_private_msg.setOnClickListener(this);
    }

    private void inits() {

        lists = new ArrayList<RecommentLivingModel>();

        GridLayoutManager manager = new GridLayoutManager(mActivity, 2);
        mRecyclerView.setLayoutManager(manager);

        mDataAdapter = new RecommendLivingAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_4);
        mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, manager.getSpanCount(), Color.WHITE));

        //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
        GridItemDecoration divider = new GridItemDecoration.Builder(mActivity)
                .setHorizontal(R.dimen.divider_height1)
                .setVertical(R.dimen.divider_height1)
                .setColorResource(R.color.white)
                .build();
        //mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
    }

    private void setlistener() {
        /**
         * 下拉刷新的监听
         */
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDataAdapter.clear();
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
        mRecyclerView.setHeaderViewColor(R.color.mianColor, R.color.black ,android.R.color.white);
        //设置底部加载颜色
        mRecyclerView.setFooterViewColor(R.color.mianColor, R.color.black ,android.R.color.white);
        //设置底部加载文字提示
        mRecyclerView.setFooterViewColor(R.color.mianColor, R.color.mainTextColor ,android.R.color.transparent);
        mRecyclerView.setFooterViewHint("拼命加载中","没有更多啦(=^ ^=)","网络不给力啊，点击再试一次吧");

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view,final int position) {
                RecommentLivingModel item;
                if (mDataAdapter.getDataList().size() > position) {
                    item = mDataAdapter.getDataList().get(position);
                    LiveRoomActivity.startAudience(getActivity(), item.getRoomid()+"",  item.getUrl(), true,item.getFoll(),item.getSchool_id());

                    //1为直播地址，1为房间号
//                    currentMode=1;
//                    if(currentMode == MODE_ROOM) {
//                        }else{
//                            LiveRoomActivity.startAudience(getActivity(),  mDataAdapter.getDataList().get(position).getRoomid() + "", mDataAdapter.getDataList().get(position).getUrl(), true);
//                        }
                }
            }
        });

    }



    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }


    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.ll_start_live:
                intent = new Intent(mActivity, EnterLiveActivity.class);
                CommonUtils.startActivity(mActivity,intent);


                break;
            case R.id.ll_attention:
                intent = new Intent(mActivity, MyAttentionLiveActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_private_msg:

                break;
        }
    }

    private class PreviewHandler extends Handler {

        private WeakReference<LiveFragment> ref;

        PreviewHandler(LiveFragment activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LiveFragment activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:

                    mCurrentCounter = activity.mDataAdapter.getItemCount();

                    pindex =1;
                    psize =10;
                    mDataAdapter.setDataList(lists);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    Log.e("TTT",lists.toString()+"进来了");
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
                default:
                    break;
            }
        }
    }

    private int pindex;
    private int psize;
    private List<RecommentLivingModel> courseChilds =new ArrayList<>();
    private List<RecommentLivingModel> lists=new ArrayList<>() ;

    private CourseRecommendLiving courseInfoResult = new CourseRecommendLiving();

    @Override
    public void onResume() {
        super.onResume();
        if (lists!=null){
            lists.clear();
//            mRecyclerView.refresh();
            requestData();
        }
    }

    /**
     * 模拟请求网络
     */
    private void requestData() {
//        /index.php/Api/Wylive/new_live?type=0&user_id=198&pindex=1&psize=2
        final String url = ConfigInfo.ApiUrl+"/index.php/Api/Wylive/new_live?user_id="+DemoHelper.getUid()+"&type="+0+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(mActivity,false,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"CourseFragment_request_url:"+url);
                Log.i(ConfigInfo.TAG,"&&course_result"+result);
                Gson gson =new Gson();
                courseInfoResult = gson.fromJson(result,CourseRecommendLiving.class);
                if (courseInfoResult != null){
                    //获得总数
                    TOTAL_COUNTER = courseInfoResult.getCount();
                    //获得
                    courseChilds = courseInfoResult.getChilds();
                    if (courseChilds!=null){
                        lists.addAll(courseChilds);
                    }
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);

                }
            }
        });

    }


}
