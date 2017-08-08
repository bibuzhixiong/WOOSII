package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.google.gson.Gson;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Throwable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.MyCollectAdapter;
import sochat.so.com.android.adapter.OnItemListener;
import sochat.so.com.android.model.MyCollectResult;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.net.IpResult;
import sochat.so.com.android.net.MyApiService;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;


/**
 * Created by Administrator on 2017/3/16.
 */

public class MyCollectActivity extends BaseActivity {
    private TextView tvTopTitle;
    private ImageView ivTopBack;
    private ImageView ivDelete;
    private TextView tvCancel;
    private TextView tvDelete;
    private TextView tvAll;
    private RelativeLayout rlBg;

    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 1000;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private MyCollectAdapter mAdapter = null;

    /**
     * 自定义Handler,用来处理在子线程访问网络后更新UI
     */
    private PreviewHandler mHandler = new PreviewHandler(MyCollectActivity.this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    /**
     * 用于获得收藏列表的信息
     */
    private MyCollectResult Result;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_collect);
        findById();
        inits();
        setListeners();


        initRecyclerView();
        recyclerViewListeners();


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


    private void initRecyclerView() {
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        mAdapter = new MyCollectAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
    }

    private void recyclerViewListeners() {
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                pindex =1;
                mAdapter.clear();
                lists.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                requestData();

            }
        });

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

        mRecyclerView.refresh();

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mAdapter.getCheckBoxVisiable()){
                }else{
                    if (mAdapter.getDataList().size() > position) {
                        TeacherCourseList item = mAdapter.getDataList().get(position);
                        Intent intent ;
                        if (item.getVr()==0){
                            intent = new Intent(MyCollectActivity.this,PlayVedioActivity.class);
                        }else{
                            intent = new Intent(MyCollectActivity.this,PlayVedioActivity.class);
                        }
                        intent.putExtra("vedio_info",item);
                        CommonUtils.startActivity(MyCollectActivity.this,intent);
                    }
                }
            }

        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
//                CollectVedioModel item = mAdapter.getDataList().get(position);
//                MyToast.showShortText(getApplicationContext(), "onItemLongClick - " + item.getName());
            }
        });


        mAdapter.setOnItemListener(new OnItemListener() {
            @Override
            public void checkBoxClick(int position) {
                //已经有Item被选择,执行添加或删除操作
                addOrRemove(position);
            }

            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    private void findById() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        ivDelete = (ImageView) findViewById(R.id.iv_search);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        tvAll = (TextView) findViewById(R.id.tv_all);

        rlBg = (RelativeLayout) findViewById(R.id.rl_bg);

    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.VISIBLE);
        tvTopTitle.setText("我的收藏");
        ivDelete.setImageResource(R.drawable.shanchu);
        tvCancel.setVisibility(View.GONE);

        courseChilds = new ArrayList<TeacherCourseList>();
        lists = new ArrayList<TeacherCourseList>();
        positionSet =  new HashSet<>();
    }

    private void setListeners() {
    MyClick listener = new MyClick();
        ivTopBack.setOnClickListener(listener);
        ivDelete.setOnClickListener(listener);
        tvCancel.setOnClickListener(listener);
        ivTopBack.setOnClickListener(listener);
        ivTopBack.setOnClickListener(listener);
        tvDelete.setOnClickListener(listener);
        tvAll.setOnClickListener(listener);

    }

    //记录选择的Item
    private HashSet<Integer> positionSet;

    private void addOrRemove(int position) {
        if(positionSet.contains(position)) {
            // 如果包含，则撤销选择
            Log.e("----","remove");
            positionSet.remove(position);
        } else {
            // 如果不包含，则添加
            Log.e("----","add");
            positionSet.add(position);
        }

    }


    private MyClick listener;
    class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_top_back:
                    MyCollectActivity.this.finish();
                    break;
                case R.id.tv_cancel:
                    tvCancel.setVisibility(View.GONE);
                    ivDelete.setVisibility(View.VISIBLE);
                    rlBg.setVisibility(View.GONE);
                    mAdapter.setCheckBoxVisiable(false);
                    notifyDataSetChanged();
                    break;
                case R.id.iv_search:
                    tvCancel.setClickable(true);
                    tvCancel.setVisibility(View.VISIBLE);
                    rlBg.setVisibility(View.VISIBLE);
                    ivDelete.setVisibility(View.GONE);
                    mAdapter.setCheckBoxVisiable(true);
                    notifyDataSetChanged();
                    break;
                case R.id.tv_delete:
                    deleteCollect();
                    break;
                case R.id.tv_all:
                    setAll();
                    break;
            }
        }
    }

    private boolean isSelectAll;
    private void setAll() {
        if(!isSelectAll) {
            isSelectAll = true;
            for (int i = 0; i < mAdapter.getDataList().size(); i++) {
                mAdapter.getDataList().get(i).setSelect(true);
                positionSet.add(i);
            }
            notifyDataSetChanged();
        } else {
            isSelectAll = false;
            for (int i = 0; i < mAdapter.getDataList().size(); i++) {
                mAdapter.getDataList().get(i).setSelect(false);
            }
            positionSet.clear();
            notifyDataSetChanged();
        }
    }


    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();



    private ArrayList<String>count ;
    //删除选择的item
    private void deleteCollect() {
        lists.clear();
        count = new ArrayList<String>();
        for (Integer integer : positionSet) {
            count.add(mAdapter.getItem(integer).getCu_id());
        }


        String array = "{\"user_id\":"+DemoHelper.getUid()+","+"\"cu_id\":"+count.toString()+"}";
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/del_coll?arr="+array;
        Log.i(ConfigInfo.TAG,"我就是url:"+url.toString());

        headers.put("Accept", "application/json");
        parameters.clear();
        parameters.put("arr", array);
        MyNetWorkUtil.getNovate(MyCollectActivity.this,ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_CACHE,ConfigInfo.ApiUrl);
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);
            MyNetWorkUtil.novate.call(MyNetWorkUtil.myAPI.getSougu(parameters), new BaseSubscriber<IpResult>(MyCollectActivity.this) {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(IpResult result) {
                    //请求成功
                    Log.i(ConfigInfo.TAG,"000000"+result.toString());
                    requestData();
                    positionSet.clear();
                }
            });

        }



//        novate = new Novate.Builder(this)
//                .addHeader(headers)
//                .connectTimeout(10)
//                .addCookie(false)
//                .addCache(false)
//                .baseUrl(ConfigInfo.ApiUrl)
//                .addLog(true)
//                .build();
//        MyApiService myAPI = novate.create(MyApiService.class);
//
//        novate.call(myAPI.getSougu(parameters),
//                new BaseSubscriber<IpResult>(MyCollectActivity.this) {
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i(ConfigInfo.TAG,"111111"+e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(IpResult result) {
//
//                    }
//                });


    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<MyCollectActivity> ref;

        PreviewHandler(MyCollectActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyCollectActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    //只能在UI线程更新UI

                    mCurrentCounter = activity.mAdapter.getItemCount();
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

                case  1 :
                    MyToast.makeShortToast(MyCollectActivity.this,"删除收藏失败，请重试");
                    break ;
                case  2 :
                    MyToast.makeShortToast(MyCollectActivity.this,"删除数据异常，请重试");
                    break ;
                default:
                    break;
            }
        }
    }


    private int pindex =1;
    private int psize =10;
    private List<TeacherCourseList> courseChilds ;
    private List<TeacherCourseList> lists;

    /**
     * 请求网络
     */
    private void requestData() {

        final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/r_coll?user_id="+ DemoHelper.getUid()+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(MyCollectActivity.this,true,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"url"+url);
                Gson gson =new Gson();
                Result = gson.fromJson(result,MyCollectResult.class);
                TOTAL_COUNTER = Result.getCount();
                if (Result != null){
                    courseChilds = Result.getChilds();
                    if (courseChilds ==null){
                        lists.clear();
                    }
                    if (courseChilds !=null){
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
