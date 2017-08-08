package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.activity.MoreRecommentActivity;
import sochat.so.com.android.activity.SearchTeacherActivity;
import sochat.so.com.android.activity.TextBookActivity;
import sochat.so.com.android.adapter.MostHotAdapter;
import sochat.so.com.android.adapter.MostNewAdapter;
import sochat.so.com.android.adapter.RecommendTeacherAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.DividerGridItemDecoration;
import sochat.so.com.android.customview.RecyclerBanner;
import sochat.so.com.android.model.HomepageVedioResult;
import sochat.so.com.android.model.MostHotResult;
import sochat.so.com.android.model.MostNewResult;
import sochat.so.com.android.model.RecommendTeacherModel;
import sochat.so.com.android.model.RecommendTeacherResult;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/2/20.
 * 这个是首頁
 */

public class MarketingFragment extends BaseFragment {
    @Bind(R.id.tv_jiaocai)
    TextView tvTextbook;
    @Bind(R.id.tv_news_more)
    TextView tvNewsMore;
    @Bind(R.id.tv_hot_more)
    TextView tvHotMore;
    @Bind(R.id.recyclerview_new)
    RecyclerView recyclerviewNew;
    @Bind(R.id.recyclerview_hot)
    RecyclerView recyclerviewHot;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.recyclerview_teacher)
    RecyclerView recyclerviewTeacher;
    @Bind(R.id.iv_hot)
    ImageView ivHot;
    @Bind(R.id.MyAdGallery)
    RecyclerBanner mMyAdVallery;
    private View view;
    /**
     * 最热推荐的热cycle人view的adapter
     */
    private MostHotAdapter mMostHotAdapter;
    /**
     * 最新推荐的热cycle人view的adapter
     */
    private MostNewAdapter mMostNewAdapter;
    /**
     * 推荐老师的热cycle人view的adapter
     */
    private RecommendTeacherAdapter mRecommendTeacherAdapter;
    /**
     * 最火推荐的数组
     */
    private List<TeacherCourseList> hots;
    /**
     * 最新推荐的数组
     */
    private List<TeacherCourseList> news;
    /**最火老师推荐的数组
     */
    private List<RecommendTeacherModel>teachers;
    /**
     * 用Json来解析复杂的数据，这里是一个Result
     */
    private MostHotResult mHotResult;
    private MostNewResult mNewResult;
    private RecommendTeacherResult mTeacherResult;

    /**
     * Handler用来处理异步请求网络结束后在UI线程处理结果
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mRecommendTeacherAdapter.setLists((ArrayList<RecommendTeacherModel>) teachers);
                    mMostNewAdapter.setLists((ArrayList<TeacherCourseList>) news);
                    mMostHotAdapter.setLists((ArrayList<TeacherCourseList>) hots);

                    break;
                case 1:

                    break;
                case 2:
                    mMostHotAdapter.setLists((ArrayList<TeacherCourseList>) hots);
                    break;
                case 3:

                    break;
                case 4:
                    mRecommendTeacherAdapter.setLists((ArrayList<RecommendTeacherModel>) teachers);
                    break;
                case 5:

                    break;
                case 6:
                    if (imageurls!=null&&mMyAdVallery!=null){
                        mMyAdVallery.setDatas(imageurls);
                    }
                    break;
            }
        }
    };

    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_marketing, container, false);
        ButterKnife.bind(this, view);
        initData();
        setListener();

        return view;
    }

    /**
     * 初始化数据和UI控件
     */
    private void initData() {
        tvTextbook.setText(DemoHelper.getTextBook());
        hots = new ArrayList<TeacherCourseList>();
        news = new ArrayList<TeacherCourseList>();
        teachers = new ArrayList<RecommendTeacherModel>();


        mHomepageResult = new HomepageVedioResult();

        mHotResult = new MostHotResult();
        mNewResult = new MostNewResult();
        mTeacherResult = new RecommendTeacherResult();

        mMostHotAdapter = new MostHotAdapter(hots, mActivity);
        mMostNewAdapter = new MostNewAdapter(news, mActivity);
        mRecommendTeacherAdapter = new RecommendTeacherAdapter(teachers,mActivity);
        //分割线
        recyclerviewTeacher.addItemDecoration(new DividerItemDecoration(mActivity,
                DividerItemDecoration.HORIZONTAL));

        recyclerviewNew.addItemDecoration(new DividerGridItemDecoration(mActivity));
        recyclerviewHot.addItemDecoration(new DividerGridItemDecoration(mActivity));
        //设置为网格形式的布局
        recyclerviewNew.setLayoutManager(new GridLayoutManager(mActivity, 2));
        recyclerviewHot.setLayoutManager(new GridLayoutManager(mActivity, 2));
        //横向的RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerviewTeacher.setLayoutManager(linearLayoutManager);
        //这里是为了防止RecyclerVier与ScrollView滑动产生冲突，意思是滚动时不让RecyclerView影响父布局
        recyclerviewNew.setNestedScrollingEnabled(false);
        recyclerviewHot.setNestedScrollingEnabled(false);
        recyclerviewTeacher.setNestedScrollingEnabled(false);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerviewNew.setHasFixedSize(true);
        recyclerviewHot.setHasFixedSize(true);
        recyclerviewTeacher.setHasFixedSize(true);
        recyclerviewHot.setAdapter(mMostHotAdapter);
        recyclerviewNew.setAdapter(mMostNewAdapter);
        recyclerviewTeacher.setAdapter(mRecommendTeacherAdapter);
        //获得数据
        getData();
        //获得轮播图图片地址
        getLoopImage();

    }

    /**
     * 获得轮播图的数组
     */
    private List<RecyclerBanner.BannerEntity> imageurls = new ArrayList<>();
    private void getLoopImage() {
        if (imageurls!=null&&imageurls.size()!=0){
            imageurls.clear();
        }
        String loopurl = ConfigInfo.NETWORK_CYCLE_PHOTO;
        HttpUtils.doGetAsyn(null, false, loopurl, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imageurls.add(new Entity(ConfigInfo.ApiUrl+jsonArray.getJSONObject(i).getString("Url")));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(6);
            }
        });
    }

    private class Entity implements RecyclerBanner.BannerEntity {

        String url;

        public Entity(String url) {
            this.url = url;
        }

        @Override
        public String getUrl() {
            return url;
        }
    }

    private void setListener() {

    }
    /**
     * 新建Fragment
     */
    public static MarketingFragment newInstance() {
        MarketingFragment fragment = new MarketingFragment();
        return fragment;

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 注销EventBus和解绑ButterKnife
     */
    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    /**
     * 点击事件
     */
    @OnClick({R.id.tv_jiaocai,R.id.tv_news_more,R.id.tv_hot_more, R.id.iv_search})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tv_jiaocai:
                intent = new Intent(mActivity, TextBookActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.iv_search:
                intent = new Intent(mActivity,SearchTeacherActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.tv_news_more:
                intent = new Intent(mActivity,MoreRecommentActivity.class);
                intent.putExtra("more_url",ConfigInfo.ApiUrl+ "/index.php/Vr/Vlive/new_video?type=1&user_id=");
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.tv_hot_more:
                intent = new Intent(mActivity,MoreRecommentActivity.class);
                intent.putExtra("more_url",ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/new_hot?type=1&user_id=");
                CommonUtils.startActivity(mActivity,intent);
                break;
        }
    }


    private HomepageVedioResult mHomepageResult;
    /**
     * 得到视频和推荐老师的数据
     */
    private void getData(){
        if (teachers!=null){
            teachers.clear();
        }
        if (news!=null){
            news.clear();
        }
        if (hots!=null){
            hots.clear();
        }

        String dataurl =  ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/new_hot_video?user_id="+DemoHelper.getUid()+"&type=0";
        HttpUtils.doGetAsyn(null, false, dataurl, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                if (!TextUtils.isEmpty(result)&result.length()>10){
                    android.util.Log.e("TTT",result);
                    Gson gson = new Gson();
                    mHomepageResult = gson.fromJson(result, HomepageVedioResult.class);

                    teachers = mHomepageResult.getChilds_rec();
                    news = mHomepageResult.getChilds_new();
                    hots = mHomepageResult.getChilds_hot();

                    handler.sendEmptyMessage(0);
                }else{
                    handler.sendEmptyMessage(0);
                }

            }
        });
    }

}
