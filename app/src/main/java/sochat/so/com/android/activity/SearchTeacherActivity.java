package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.DeleteHistoryAdapter;
import sochat.so.com.android.adapter.HotSearchAdapter;
import sochat.so.com.android.adapter.SearchAdapter;
import sochat.so.com.android.adapter.help_adapter.SearchShowVedioAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.SearchEditText;
import sochat.so.com.android.model.SearchBean;
import sochat.so.com.android.model.SearchResult;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/3/15.
 */

public class SearchTeacherActivity extends BaseActivity {
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 0;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;
    //显示视频的RecyclerView
    private LRecyclerView mVedioRecyclerView = null;

    private SearchAdapter mDataAdapter = null;
    //显示视频的mDataAdapter
    private SearchShowVedioAdapter mSearchShowVedioAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private LRecyclerViewAdapter mVedioLRecyclerViewAdapter = null;
    private SearchTeacherActivity mActivity;


    @Bind(R.id.et_search)
    SearchEditText etSearch;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_no)
    TextView tvNo;
    @Bind(R.id.gv_history)
    GridView gvHistory;
    @Bind(R.id.gv_hot_search)
    GridView gvHotSearch;
    @Bind(R.id.iv_delete_history)
    ImageView ivDeleteHistory;
    @Bind(R.id.sv_search_result)
    ScrollView svSearchShow;

    private DeleteHistoryAdapter mDeleteHistoryAdapter;
    private HotSearchAdapter mHotSearchAdapter;
    //历史数据
    private List<String>deletes ;
    private  List<String>historys ;
    private List<String>hotSearch;

    //搜索得到的数据
    private SearchResult mSearchResult;
    private List<SearchBean>searchTeachers;
    private List<TeacherCourseList>searchVedios;


    /**
     * 视频id
     */
    private String pay_cu_id;

    /**
     * 点击支付的时间
     */
    private int paytime;
    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow bottomPopupWindow;
    /**
     * 底部popupwindow的按钮
     */
    private TextView tvCommitPop;
    private TextView tvCancelPop;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_teacher);
        ButterKnife.bind(this);
        mActivity = SearchTeacherActivity.this;
        inits();
        setListeners();
    }

    private void setListeners() {
        //按回车键开始执行的方法
        etSearch.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                if(TextUtils.isEmpty(etSearch.getText().toString().trim())){
                    MyToast.makeShortToast(mActivity,"请输入搜索内容");
                }else{
                    historys.clear();
                    historys.add(etSearch.getText().toString().trim());
                    if (!TextUtils.isEmpty(DemoHelper.getSearchHistory())){
                        String[] history = DemoHelper.getSearchHistory().substring(1,DemoHelper.getSearchHistory().length()-1).split(",");
                        for (int i=0;i<history.length;i++){
                            String current = history[i];
                            historys.add(current);
                        }
                    }
                    mDeleteHistoryAdapter.setlist((ArrayList<String>) historys);

                    DemoHelper.setSearchHistory(historys.toString());
                    requestData();
                }
            }
        });

        gvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSearch.setIconLeft(true);
                etSearch.setText((CharSequence) mDeleteHistoryAdapter.getItem(position));
                Log.i(ConfigInfo.TAG,"etSearch.getText().toString().trim():"+etSearch.getText().toString().trim());
                requestData();

            }
        });

        gvHotSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSearch.setIconLeft(true);
                etSearch.setText((CharSequence) mHotSearchAdapter.getItem(position));
                requestData();
            }
        });


        tvCommitPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPayVedio();
                bottomPopupWindow.dismiss();
            }
        });
        tvCancelPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomPopupWindow.dismiss();
            }
        });

    }

    @OnClick({R.id.tv_cancel,R.id.iv_delete_history})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
                //点击取消按钮
                clickcancel();
                break;
            case R.id.iv_delete_history:
                DemoHelper.setSearchHistory("");
                deletes.clear();
                mDeleteHistoryAdapter.setlist((ArrayList<String>) deletes);
                break;
        }

    }

    private void clickcancel() {
        if (!TextUtils.isEmpty(etSearch.getText().toString())||lists.size()>=1){
            svSearchShow.setVisibility(View.GONE);
            tvNo.setVisibility(View.GONE);
            etSearch.setText("");
            if (lists!=null){
                lists.clear();
            }
            mDataAdapter.clear();
        }else{
            SearchTeacherActivity.this.finish();
        }
    }

    private void inits() {
        bottomPopupWindow = new BottomPopupWindow(this);
        lists = new ArrayList<SearchBean>();
        //初始化搜索的List
        searchTeachers = new ArrayList<>();
        searchVedios = new ArrayList<>();
        Log.i(ConfigInfo.TAG,"demohelper:"+DemoHelper.getSearchHistory());
        //辅助删除历史的数组
        historys = new ArrayList<String>();
        //删除历史
        deletes = new ArrayList<String>();
        //热门搜索
        hotSearch = new ArrayList<String>();

        //历史的搜索数据是存本地的，这里从本地获取
        initHistoryList();

        svSearchShow = (ScrollView) findViewById(R.id.sv_search_result);
        mRecyclerView = (LRecyclerView) findViewById(R.id.list);
        mVedioRecyclerView = (LRecyclerView) findViewById(R.id.list_vedio);
        //在初始状态隐藏列表防止用户直接手动刷新
        svSearchShow.setVisibility(View.GONE);

        mDataAdapter = new SearchAdapter(mActivity);
        mSearchShowVedioAdapter = new SearchShowVedioAdapter(mActivity);

        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mVedioLRecyclerViewAdapter = new LRecyclerViewAdapter(mSearchShowVedioAdapter);
        mVedioRecyclerView.setAdapter(mVedioLRecyclerViewAdapter);




        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.topLineThick)
                .setColorResource(R.color.line_gray)
                .build();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setPullRefreshEnabled(false);


        mVedioRecyclerView.setHasFixedSize(true);

        mVedioRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mVedioRecyclerView.setPullRefreshEnabled(false);

        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mDataAdapter.getDataList().size() > position) {
                    SearchBean item = mDataAdapter.getDataList().get(position);
                    Intent intent = new Intent(mActivity, TeacherInfoActivity.class);
                    intent.putExtra("teacher_uid",item.getUser_id());
                    CommonUtils.startActivity(mActivity,intent);
                    SearchTeacherActivity.this.finish();
                }
            }
        });


        mVedioLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mSearchShowVedioAdapter.getDataList().size() > position) {
                    item = mSearchShowVedioAdapter.getDataList().get(position);
                    int vip = item.getVip();
                    pay_cu_id = item.getCu_id();
                    int free = item.getFree();
                    //判断是否为VR视频
                    Intent intent ;
                    if (item.getVr()==0){
                        intent = new Intent(SearchTeacherActivity.this,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(SearchTeacherActivity.this,PlayVRVedioActivity.class);
                    }

                    if (vip ==0){//免费视频
                        intent.putExtra("vedio_info",item);
                        CommonUtils.startActivity(SearchTeacherActivity.this,intent);
                    }else if (vip ==1){//收费视频
                        if (free ==0){
                            paytime = item.getLongtime();
                            bottomPopupWindow.show(SearchTeacherActivity.this);
                        }else if(free ==1){
                            intent.putExtra("vedio_info",item);
                            CommonUtils.startActivity(SearchTeacherActivity.this,intent);
                        }


                    }
                }
            }
        });


        mDeleteHistoryAdapter = new DeleteHistoryAdapter(SearchTeacherActivity.this,deletes);
        gvHistory.setAdapter(mDeleteHistoryAdapter);

        mHotSearchAdapter = new HotSearchAdapter(SearchTeacherActivity.this,hotSearch);
        gvHotSearch.setAdapter(mHotSearchAdapter);

        getHotSearch();


    }

    private TeacherCourseList item =null;

    /**
     * 这里得到的是推荐的数据
     */
    private void getHotSearch() {
        String hotUrl = ConfigInfo.ApiUrl +"/index.php/Vr/Vlive/hot_search";
        HttpUtils.doGetAsyn(SearchTeacherActivity.this, false, hotUrl, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    Log.i(ConfigInfo.TAG,"getHotSearch_result"+result);
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i=0;i<jsonArray.length();i++){
                        hotSearch.add(jsonArray.getString(i));
                    }
                    mHandler.sendEmptyMessage(1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initHistoryList() {
        if (!TextUtils.isEmpty(DemoHelper.getSearchHistory())) {
            String[] history = DemoHelper.getSearchHistory().substring(1, DemoHelper.getSearchHistory().length() - 1).split(",");
            for (int i = 0; i < history.length; i++) {
                deletes.add(history[i]);
            }
        }
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
        mVedioLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<SearchTeacherActivity> ref;

        PreviewHandler(SearchTeacherActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final SearchTeacherActivity activity = ref.get();
            if (activity == null || mActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    tvNo.setVisibility(View.GONE);
                    svSearchShow.setVisibility(View.VISIBLE);
                    if (lists!=null){
                        if (lists.size()==0){
//                            MyToast.makeShortToast(SearchTeacherActivity.this,"没有搜到任何老师");
                        }
                        mDataAdapter.setDataList(lists);
                    }

                    if (searchVedios!=null){
                        mSearchShowVedioAdapter.setDataList(searchVedios);
                    }
                    activity.notifyDataSetChanged();
                    break;
                case -3:
                    tvNo.setVisibility(View.VISIBLE);
                    mDataAdapter.setDataList(lists);
                    activity.notifyDataSetChanged();
                    break;
                case 1:
                    mHotSearchAdapter.setlist((ArrayList<String>) hotSearch);
                    break;
                case 3:
                    MyToast.makeShortToast(SearchTeacherActivity.this,"购买失败，请稍后重试");
                    bottomPopupWindow.dismiss();
                    break;
                case 4:
                    Intent intent ;
                    if (item.getVr()==0){
                        intent = new Intent(SearchTeacherActivity.this,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(SearchTeacherActivity.this,PlayVRVedioActivity.class);
                    }
                    intent.putExtra("vedio_info",item);
                    CommonUtils.startActivity(SearchTeacherActivity.this,intent);
                    MyToast.makeShortToast(SearchTeacherActivity.this,"购买成功");
                    bottomPopupWindow.dismiss();
                    break;
                case 5:
                    MyToast.makeShortToast(SearchTeacherActivity.this,"您的时间余额不足，请先去充值");
                    bottomPopupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private List<SearchBean> lists ;

    /**
     * 模拟请求网络
     */
    private void requestData() {
        final String url;
        try {
            url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/search?name="+ URLEncoder.encode(etSearch.getText().toString().trim(),"UTF-8");
            HttpUtils.doGetAsyn(mActivity,false,url, mHandler, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Log.i(ConfigInfo.TAG,"requestData____url"+url);
                    Log.i(ConfigInfo.TAG,"requestData____result"+result);
                    Gson gson =new Gson();
                    if (!TextUtils.isEmpty(result)&&result.length()>10){
                        mSearchResult = gson.fromJson(result,SearchResult.class);
                        lists = mSearchResult.getChild();
                        searchVedios = mSearchResult.getChilds();

//                        lists = gson.fromJson(result,new TypeToken<List<SearchBean>>(){}.getType());
                        //获得总数
//                        TOTAL_COUNTER = lists.size();
                        mHandler.sendEmptyMessage(-1);
                    }else{
                        mHandler.sendEmptyMessage(-3);
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    /**
     * 支付相关的popupwindow
     */
    private class BottomPopupWindow extends BottomPushPopupWindow<Void> {

        public BottomPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_pay,null);
            tvCommitPop = (TextView) bottom.findViewById(R.id.tv_commit);
            tvCancelPop = (TextView) bottom.findViewById(R.id.tv_cancel);
            return bottom;
        }
    }

    /**
     * 调用支付的接口
     */
    private void toPayVedio() {
        String payUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/dividedSystem?user_id="+ DemoHelper.getUid()+"&cu_id="+pay_cu_id+"&region_id="+DemoHelper.getRegion_id()+"&time="+paytime;
//        String payUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/vip?user_id="+DemoHelper.getUid()+"&cu_id="+pay_cu_id;
        HttpUtils.doGetAsyn(SearchTeacherActivity.this, true, payUrl, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code ==0){//失败
                        mHandler.sendEmptyMessage(3);
                    }else if (code ==1){//成功
                        mHandler.sendEmptyMessage(4);
                    }else if(code ==2){//时间不足
                        mHandler.sendEmptyMessage(5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            clickcancel();

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
