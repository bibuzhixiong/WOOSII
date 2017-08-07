package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Map;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.TeacherInfoActivity;
import sochat.so.com.android.adapter.help_adapter.DataAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.model.CourseChild;
import sochat.so.com.android.model.CouurseInfoResult;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/3/8.
 */

public class CourseFragment extends BaseFragment {
    private View view;
    private Activity mActivity;
    private int flag;
    private String s_id;
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private DataAdapter mDataAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private CouurseInfoResult courseInfoResult;

    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
        Log.i("Gale log","这里是Fragment开始的地方");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if(bundle != null){
            flag = bundle.getInt("flag");
            s_id = bundle.getString("s_id");
            Log.i("Gale log","wodetian:"+bundle.toString());
        }
        view = inflater.inflate(R.layout.fragment_course,container,false);
        courseChilds = new ArrayList<CourseChild>();
        lists = new ArrayList<CourseChild>();
        findById();
        inits();
        setListener();
        return view;
    }

    private void setListener() {
    }

    private void findById() {

    }
private static  Map<String, Object> map;
    public static CourseFragment newInstance(int flag,String s_id){
        Bundle bundle = new Bundle();
        bundle.putInt("flag", flag);
        bundle.putString("s_id", s_id);

        if (map==null){
            map = new MyHashMap<>();
        }
        map.put(flag+"", s_id);

        JSONObject jsonObject = new JSONObject(map);
        DemoHelper.setTitle(jsonObject.toString());


        CourseFragment fragment = new CourseFragment();
        fragment.setArguments(bundle);
        return fragment;

    }

    private void inits() {
        mRecyclerView = (LRecyclerView) view.findViewById(R.id.list);

        mDataAdapter = new DataAdapter(mActivity);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);


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

        mRecyclerView.refresh();

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mDataAdapter.getDataList().size() > position) {
                    CourseChild item = mDataAdapter.getDataList().get(position);
                    Intent intent = new Intent(mActivity, TeacherInfoActivity.class);
                    intent.putExtra("teacher_uid",item.getUser_id());
                    CommonUtils.startActivity(mActivity,intent);
                }
            }
        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
            }
        });

    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<CourseFragment> ref;

        PreviewHandler(CourseFragment activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CourseFragment activity = ref.get();
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
    private List<CourseChild> courseChilds ;
    private List<CourseChild> lists ;

    @Override
    public void onResume() {
        super.onResume();
        if (lists!=null){
            lists.clear();
            mRecyclerView.refresh();
//            requestData();
        }
    }

    /**
     * 模拟请求网络
     */
    private void requestData() {
        try {
            JSONObject jsonObject = new JSONObject(DemoHelper.getTitle());
                s_id = jsonObject.getString(flag+"");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(ConfigInfo.TAG,"获得数据多少次"+s_id);
        psize = 10;
       final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/school?u_id="+DemoHelper.getUid()+"&type="+s_id+"&region_id="+DemoHelper.getVedioRegion_id()+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(mActivity,false,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"CourseFragment_request_url:"+url);
                Log.i(ConfigInfo.TAG,"course_result"+result);
                Gson gson =new Gson();
                courseInfoResult = gson.fromJson(result,CouurseInfoResult.class);
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
