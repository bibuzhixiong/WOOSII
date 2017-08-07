package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.CityAdapter;
import sochat.so.com.android.adapter.HeaderRecyclerAndFooterWrapperAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.SearchEditText;
import sochat.so.com.android.model.CityModel;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/4/17.
 */

public class ChooseCityActivity extends BaseActivity implements View.OnClickListener,CityAdapter.setFinishActivity {
    private ImageView ivTopBack;
    private SearchEditText etSearch;

    private RecyclerView mRv;
    private CityAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private LinearLayoutManager mManager;
    private List<CityModel> mDatas;

    private SuspensionDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;
    private static final String INDEX_STRING_TOP = "↑";


    //这里是判断跳转的来源，只有从课外课界面跳进来的才用添加头部
    private int type=0;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mAdapter.setDatas(mDatas);
                    mAdapter.notifyDataSetChanged();

                    mIndexBar.setmSourceDatas(mDatas)//设置数据
                            .invalidate();
                    mDecoration.setmDatas(mDatas);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        findById();
        inits();
        setListeners();
    }

    private void findById() {
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);
        etSearch = (SearchEditText) findViewById(R.id.tv_top_edittext);
        mRv = (RecyclerView) findViewById(R.id.rv);
    }

    private void inits() {
//        intent.putExtra("skip_to_choose_city",1);
        type = getIntent().getIntExtra("skip_to_choose_city",0);


        mDatas = new ArrayList<CityModel>();
        mRv.setLayoutManager(mManager = new LinearLayoutManager(this));

        mAdapter = new CityAdapter(this, mDatas,type);
        mAdapter.setListeners(this);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, mDatas).setColorTitleBg(0xFFF6F6F6).setColorTitleFont(0xFF15B422));
        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(new DividerItemDecoration(ChooseCityActivity.this, DividerItemDecoration.VERTICAL));

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);//设置RecyclerView的LayoutManager

        requestData();
    }

    private void setListeners() {
        ivTopBack.setOnClickListener(this);
        //按回车键开始执行的方法
        etSearch.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                if(TextUtils.isEmpty(etSearch.getText().toString().trim())){
                    MyToast.makeShortToast(ChooseCityActivity.this,"请输入搜索内容");
                }else{
                    searchCity();
                }
            }
        });

    }

    private void searchCity() {
        mDatas.clear();
        String searchCity = "";
        try {
            searchCity = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/search_city?name="+(etSearch.getText().toString().trim()!=null? URLEncoder.encode(etSearch.getText().toString().trim(),"UTF-8"):etSearch.getText().toString().trim());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpUtils.doGetAsyn(null, false, searchCity, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    Log.i(ConfigInfo.TAG,"searchCity_result"+result.toString());
                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                    Gson gson = new Gson();
                    //加强for循环遍历JsonArray
                    for (JsonElement user : jsonArray) {
                        //使用GSON，直接转成Bean对象
                        CityModel city = gson.fromJson(user, CityModel.class);
                        mDatas.add(city);
                    }
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 获得数据源
     */
    private void requestData() {
        mDatas.clear();
        if (type==1){
            mDatas.add((CityModel) new CityModel("0","全国").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
        }
                String url = ConfigInfo.ApiUrl+"/index.php/Api/AreaManager/select_acity";
                HttpUtils.doGetAsyn(null, false, url, handler, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        try {
                            //Json的解析类对象
                            JsonParser parser = new JsonParser();
                            //将JSON的String 转成一个JsonArray对象
                            JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                            Gson gson = new Gson();
                            //加强for循环遍历JsonArray
                            for (JsonElement user : jsonArray) {
                                //使用GSON，直接转成Bean对象
                                CityModel city = gson.fromJson(user, CityModel.class);
                                mDatas.add(city);
                            }
                            handler.sendEmptyMessage(0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_top_back:
                ChooseCityActivity.this.finish();
                break;
        }
    }

    @Override
    public void finishActivity() {
        ChooseCityActivity.this.finish();
    }
}
