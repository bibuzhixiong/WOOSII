package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import sochat.so.com.android.activity.PanoramaWebViewActivity;
import sochat.so.com.android.adapter.PanoramaAdapter;
import sochat.so.com.android.model.PanoramaModel;
import sochat.so.com.android.model.PanoramaResult;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/7/12.
 */

public class PanoramaFragment extends BaseFragment {
    private View view;

    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private PanoramaAdapter mDataAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    //当前的页数
    private int pindex = 1;
    private List<PanoramaModel> childs ;
    private List<PanoramaModel> lists ;

    private PanoramaResult Result = new PanoramaResult();



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public String getFragmentName() {
        return "PanoramaFragment";
    }

    /**
     * 新建Fragment
     */
    public static PanoramaFragment newInstance() {
        PanoramaFragment fragment = new PanoramaFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_panorage,container,false);
        findById();
        inits();
        setlistener();
        return view;
    }

    private void findById() {
        mRecyclerView = (LRecyclerView) view.findViewById(R.id.list);
    }

    private void inits() {
        childs = new ArrayList<>();
        lists = new ArrayList<>();

        mDataAdapter = new PanoramaAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);
        //setLayoutManager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager( 2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //防止item位置互换
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(layoutManager);


//        int spacing = getResources().getDimensionPixelSize(R.dimen.dp_4);
//        mRecyclerView.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, layoutManager.getSpanCount(), Color.WHITE));

        //根据需要选择使用GridItemDecoration还是SpacesItemDecoration
      /*  GridItemDecoration divider = new GridItemDecoration.Builder(mActivity)
                .setHorizontal(R.dimen.divider_height1)
                .setVertical(R.dimen.divider_height1)
                .setColorResource(R.color.white)
                .build();*/
        //mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        if (lists!=null){
//            lists.clear();
////            mRecyclerView.refresh();
//            requestData();
//        }
//    }



    private void setlistener() {
        /**
         * 下拉刷新的监听
         */
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDataAdapter.clear();
                if (lists!=null){
                    lists.clear();
                }
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
            public void onItemClick(View view, int position) {
                if (mDataAdapter.getDataList().size() > position) {
                    PanoramaModel item = mDataAdapter.getDataList().get(position);
                    Intent intent = new Intent(mActivity, PanoramaWebViewActivity.class);
                    intent.putExtra("panorama_url",item.getAddr_url());
                    intent.putExtra("panorama_author",item.getAuthor());
                    intent.putExtra("panorama_poetry",item.getPoetry());
                    intent.putExtra("panorama_detailed",item.getDetailed());
                    CommonUtils.startActivity(mActivity,intent);
                }
            }
        });

        requestData();
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<PanoramaFragment> ref;

        PreviewHandler(PanoramaFragment activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PanoramaFragment activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:

                    mCurrentCounter = activity.mDataAdapter.getItemCount();

                    pindex =1;
                    mDataAdapter.setDataList(lists);
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
                default:
                    break;
            }
        }
    }

    /**
     * 模拟请求网络
     */
    private void requestData() {
        final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/photo?pindex="+pindex+"&psize=10";
        HttpUtils.doGetAsyn(mActivity,false,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"CourseFragment_request_url:"+url);
                Log.i(ConfigInfo.TAG,"course_result"+result);
                Gson gson =new Gson();
                Result = gson.fromJson(result,PanoramaResult.class);
                if (Result != null){
                    //获得总数
                    TOTAL_COUNTER = Result.getCount();
                    //获得
                    childs = Result.getChild();
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
