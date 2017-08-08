package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.ChooseCityActivity;
import sochat.so.com.android.activity.SearchTeacherActivity;
import sochat.so.com.android.adapter.help_adapter.CourseTitleAdapter;
import sochat.so.com.android.adapter.help_adapter.CourseTitleAdapterMore;
import sochat.so.com.android.customview.BasePopuWindow;
import sochat.so.com.android.customview.ExamplePagerAdapter;
import sochat.so.com.android.eventbus.BackCityEvent;
import sochat.so.com.android.model.CourseModel;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.CourseTitleJson2Model;
import sochat.so.com.android.utils.CourseTitleJson2Model1;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/2/20.
 */

public class SchoolFragment extends BaseFragment implements View.OnClickListener,CourseTitleAdapter.OnClickItemToUpDate{
    private View view;
    private Activity mActivity;
    /**
     * 申明ViewPager
     */
    private ViewPager mViewPager;
    //    private ViewpagerIndicatorOver mIndicator;
    private List<Fragment>fragments;
    /**
     * 头部的标题
     */
//    private TextView tvTopTitle;
    /**
     * 头部右侧的搜索按钮
     */
    private EditText ivSearch;
    /**
     * 头部左侧的城市
     */
    private TextView tvCity;
    /**
     * 加号
     */
    private TextView tvJiahao;
    /**
     * ViewPager的帮助类，用来设置ViewPager的数量
     */
    private ExamplePagerAdapter mExamplePagerAdapter;

    private  MagicIndicator magicIndicator ;
    private CommonNavigator commonNavigator;

    /**
     * popwindow上面的recyclerview
     */
    private RecyclerView mTitleRecyclerView;
    //更多课程的RecyclerView
    private RecyclerView mMoreTitleRecyclerView;
    private CourseTitleAdapter mTitleAdapter;
    //更多课程的adapter
    private CourseTitleAdapterMore mMoreTitleAdapter;
    private List<String>titlelists;
    private TextView tvDeit;


    /**
     * 用来存放头部标题的数组
     */
    private List<String> mDataList ;
    private List<String> mDataListId ;

    private SchoolFragment thisFragment;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://
                    break;

                case 1://进行popwindow的数据的更新
                    mTitleAdapter.setLists(CourseTitleJson2Model.getCourseTitleArrayList());
                    mMoreTitleAdapter.setLists(CourseTitleJson2Model1.getRemainCourseTitleArrayList1());

                    break;
                case 2://获得数据成功
                    break;
                case 3://获得数据失败

                    break;
                case 10:
//                    mDataList = courseList;
                    initData();
                    break;
                default:
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
        view = inflater.inflate(R.layout.fragment_school,container,false);
        EventBus.getDefault().register(this);
        thisFragment = this;
        findById();
        inits();
        initData();
        setListener();

        return view;
    }

    private void intpopwindow() {
        //初始化编辑文字
        tvDeit = (TextView) view.findViewById(R.id.tv_edit);
        tvDeit.setOnClickListener(this);

        //popwindow的recyclerview
        mTitleRecyclerView = (RecyclerView) view.findViewById(R.id.title_recyclerview);
        mTitleAdapter = new CourseTitleAdapter(CourseTitleJson2Model.getCourseTitleArrayList(),mActivity,currentPosition);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(mActivity,4);
        mTitleRecyclerView.setLayoutManager(linearLayoutManager);
        mTitleRecyclerView.setHasFixedSize(true);
        mTitleRecyclerView.setAdapter(mTitleAdapter);

        mMoreTitleAdapter = new CourseTitleAdapterMore(CourseTitleJson2Model1.getRemainCourseTitleArrayList1(),mActivity);
        mMoreTitleRecyclerView = (RecyclerView) view.findViewById(R.id.more_recyclerview);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(mActivity,4);
        mMoreTitleRecyclerView.setLayoutManager(linearLayoutManager1);
        mMoreTitleRecyclerView.setHasFixedSize(true);
        mMoreTitleRecyclerView.setAdapter(mMoreTitleAdapter);

        mTitleAdapter.setOnClickItemToUpDate(thisFragment);

        mMoreTitleAdapter.setOnItemClickLitener(new CourseTitleAdapterMore.OnItemClickLitener() {
            @Override
            public void onItemClick(String s_id,String s_name) {
                handler.sendEmptyMessage(1);
            }
        });

    }

    /**
     * 初始化基本界面
     */
    private void inits() {
//        tvTopTitle.setText("中小学");
        ivSearch.setVisibility(View.VISIBLE);
        tvCity.setText(DemoHelper.getVedioArea());

        //popwindow的数据集合
        titlelists = new ArrayList<>();

        magicIndicator = (MagicIndicator) view.findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.parseColor("#FFFFFF"));
        commonNavigator = new CommonNavigator(mActivity);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    /**
     * FindById
     */
    private void findById() {
//        mIndicator = (ViewpagerIndicatorOver) view.findViewById(R.id.viewpager_indicator);
//        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_text);
        mViewPager = (ViewPager) view.findViewById(R.id.school_viewPager);
        ivSearch = (EditText) view.findViewById(R.id.iv_search);
        tvCity = (TextView) view.findViewById(R.id.tv_city);
        tvJiahao = (TextView) view.findViewById(R.id.jiahao);

    }
    /**
     * 监听器
     */
    private void setListener() {
        ivSearch.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvJiahao.setOnClickListener(this);
    }
    /**
     * 初始化数据，获得头部标题
     */
    private void initData() {
        mDataList = new ArrayList<>();
        mDataListId = new ArrayList<>();
        //首先在本地拿数据，那不到数据就通过网络请求
        if(!DemoHelper.getCourseTitle().isEmpty()){
            if (mDataList!=null){
                mDataList.clear();
            }
            if (mDataListId!=null){
                mDataListId.clear();
            }

           for(CourseModel title:CourseTitleJson2Model.getCourseTitleArrayList(DemoHelper.getCourseTitleShow())){
               Log.i("Gale log","title:"+title.toString());
               mDataList.add(title.getS_name());
               mDataListId.add(title.getS_id());
           }
            setData();
        }else{
            getSchoolTitle();
        }
    }
    /**
     * 这里是设置数据给ViewPager的Fragment，通过titles的数量来新建多少个Fragment
     */
    private void setData(){
        fragments = new ArrayList<Fragment>();
        if (fragments!=null){
            fragments.clear();
        }

        if (mDataList!=null&&mDataList.size()>0){
            for (int i =0; i<mDataList.size();i++){
                CourseFragment fragment = CourseFragment.newInstance(i,mDataListId.get(i));
                fragments.add(fragment);
            }
            mExamplePagerAdapter =new ExamplePagerAdapter(getChildFragmentManager(),fragments);

            mViewPager.setAdapter(mExamplePagerAdapter);
            initMagicIndicator();
        }


    }


    /**
     * 用来指定当前的title的位置
     */
    private int currentPosition = 0;

    /**
     * 这里是对头部指示器的数据初始化
     */
    private void initMagicIndicator() {
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return fragments == null ? 0 : fragments.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#FF333333"));
                simplePagerTitleView.setSelectedColor(0xFF15B422);
//                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                        currentPosition = index;

                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#15B422"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    public static SchoolFragment newInstance(){
        SchoolFragment fragment = new SchoolFragment();
        return fragment;

    }
    /**
     * 这里是用EventBus来得到用户选择的城市
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setTvCity(BackCityEvent event){
        tvCity.setText(DemoHelper.getVedioArea());
    }
    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.iv_search:
                intent = new Intent(mActivity,SearchTeacherActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.tv_city:
                intent = new Intent(mActivity,ChooseCityActivity.class);
                intent.putExtra("skip_to_choose_city",1);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.jiahao:
                popupWindowPuls = new SchoolFragmentPopupWindowPuls(mActivity);
                popupWindowPuls.setAnimationStyle(R.style.anim_popwindow);
//                mTitleAdapter.setLists(CourseTitleJson2Model.getCourseTitleArrayList(mActivity,DemoHelper.getCourseTitleShow()));
//                mMoreTitleAdapter.setLists(CourseTitleJson2Model.getRemainCourseTitleArrayList(mActivity));
                popupWindowPuls.showAsDropDown(tvCity);

                break;
            case R.id.tv_edit:
                if (isEdit){
                    tvDeit.setTextColor(0xFF666666);
                    tvDeit.setText("编辑");
                    isEdit = false;
                }else{
                    tvDeit.setTextColor(0xFF15B422);
                    tvDeit.setText("完成");
                    isEdit = true;
                }
                mTitleAdapter.updateRecyclerView(isEdit);
                handler.sendEmptyMessage(1);

                break;
        }
    }

    private boolean isEdit = false;

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
    /**
     * 这里是通过网络请求来获得头部标题的数据
     */
    private ArrayList<String> courseList = new ArrayList<String>();
    private void getSchoolTitle(){
        HttpUtils.doGetAsyn(null,false, ConfigInfo.SCHOOL_COURSE_URL, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {

                    /**老方法
                    if (!TextUtils.isEmpty(result)){
                        //Json的解析类对象
                        JsonParser parser = new JsonParser();
                        //将JSON的String 转成一个JsonArray对象
                        JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                        Gson gson = new Gson();
                        //加强for循环遍历JsonArray
                        for (JsonElement user : jsonArray) {
                            //使用GSON，直接转成Bean对象
                            CourseModel city = gson.fromJson(user, CourseModel.class);
                            courseList.add(city.getS_name());
                        }
                        handler.sendEmptyMessage(10);
                    }
                     */

                    //新方法
                    if (!TextUtils.isEmpty(result)&&result.length()>4){
                        DemoHelper.setCourseTitle(result);
                        JSONArray array = new JSONArray(result);
                        JSONArray array1 = new JSONArray();
                        if (TextUtils.isEmpty(DemoHelper.getCourseTitleShow())){
                            for (int i=0;i<8;i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                array1.put(jsonObject);
                            }
                            DemoHelper.setCourseTitleShow(array1.toString());
                        }
                        handler.sendEmptyMessage(10);

                    }






                    Log.i(ConfigInfo.TAG,"Welcome:"+DemoHelper.getCourseTitle());
                    Log.i(ConfigInfo.TAG,"result:"+result);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(10);
                }
            }
        });
    }


    private SchoolFragmentPopupWindowPuls popupWindowPuls;


    public class SchoolFragmentPopupWindowPuls extends BasePopuWindow{
        private Context context;

        public SchoolFragmentPopupWindowPuls(Context context) {
            super(context);
            context = mActivity;
            //初始化popwindow的recyclerview
            intpopwindow();
        }

        @Override
        protected View getRootView(LayoutInflater inflater) {
            view = inflater.inflate(R.layout.popupwindow_shchool_fragment_puls,null);
            view.findViewById(R.id.iv_up).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return view;
        }

        @Override
        public void dismiss() {
            super.dismiss();
            initData();
            mViewPager.setCurrentItem(currentPosition);
        }
    }

    /**
     * 点击popwindow里面的title返回到相应的CourseFragment
     * @param position
     */
    @Override
    public void setCurrentTitle(int position) {
//        mViewPager.setCurrentItem(position);//这一句不需要，因为在执行dismiss()方法时这个语句也会执行一次
        currentPosition = position;
        popupWindowPuls.dismiss();
    }

    @Override
    public void updateTitle() {
//        mMoreTitleAdapter.setLists(CourseTitleJson2Model1.getRemainCourseTitleArrayList1());
        handler.sendEmptyMessage(1);
    }

}
