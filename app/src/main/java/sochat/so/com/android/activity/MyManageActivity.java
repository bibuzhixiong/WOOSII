package sochat.so.com.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.EmployeeAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.EmployeeModel;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.SampleHeader;

/**
 * Created by Administrator on 2017/3/16.
 */

public class MyManageActivity extends BaseActivity {
    private TextView tvTopTitle;
    private ImageView ivTopBack;


    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 1000;

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private EmployeeAdapter mDataAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private boolean isRefresh = false;
    private List<EmployeeModel>employeeLists;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_manage);
        findById();
        inits();
        setListeners();

    }

    private void findById() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_text);
        ivTopBack = (ImageView) findViewById(R.id.iv_top_back);

    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopTitle.setText("员工列表");
        employeeLists = new ArrayList<EmployeeModel>();

        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        mDataAdapter = new EmployeeAdapter(this);
        mDataAdapter.setDataList(employeeLists);
        mDataAdapter.setOnDelListener(new EmployeeAdapter.onSwipeListener() {
            @Override
            public void onDel(int pos) {
//                mDataAdapter.getDataList().remove(pos);
//                mDataAdapter.notifyItemRemoved(pos);//推荐用这个
//                if(pos != (mDataAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略
//                    mDataAdapter.notifyItemRangeChanged(pos, mDataAdapter.getDataList().size() - pos);
//                }
                //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                Log.i(ConfigInfo.TAG,"iten_pos:"+pos);
                delEmployee(mDataAdapter.getDataList().get(pos).getUser_id());
            }



            @Override
            public void onTop(int pos) {//置顶功能有bug，后续解决
                EmployeeModel itemModel = mDataAdapter.getDataList().get(pos);

                mDataAdapter.getDataList().remove(pos);
                mDataAdapter.notifyItemRemoved(pos);
                mDataAdapter.getDataList().add(0, itemModel);
                mDataAdapter.notifyItemInserted(0);


                if(pos != (mDataAdapter.getDataList().size())){ // 如果移除的是最后一个，忽略
                    mDataAdapter.notifyItemRangeChanged(0, mDataAdapter.getDataList().size() - 1,"jdsjlzx");
                }

                mRecyclerView.scrollToPosition(0);

            }
        });
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        //设置头部加载颜色
        mRecyclerView.setHeaderViewColor(R.color.colorAccent, R.color.black ,android.R.color.white);

        mLRecyclerViewAdapter.addHeaderView(new SampleHeader(this));

        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataAdapter.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                isRefresh = true;
                requestData();
            }
        });

        mRecyclerView.refresh();


    }
    private void delEmployee(String employeeId) {
        String del = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/del_swo?user_id="+DemoHelper.getUid()+"&emp="+employeeId;
        HttpUtils.doGetAsyn(MyManageActivity.this, true, del, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"delEmployee:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code == 0){//0失败
                        mHandler.sendEmptyMessage(0);
                    }else{//success
                        mHandler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(0);
                }
            }
        });

    }

    private void requestData() {
        if (employeeLists.size() !=0){
            employeeLists.clear();
        }
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/swo_list?user_id="+ DemoHelper.getUid();
        Log.i(ConfigInfo.TAG,"url:"+url);
        HttpUtils.doGetAsyn(MyManageActivity.this, true, url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                if (result.length()>3){
                    Log.i(ConfigInfo.TAG,"employeeLists_result:"+result);
                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(result).getAsJsonArray();
                    Gson gson = new Gson();
                    //加强for循环遍历JsonArray
                    for (JsonElement user : jsonArray) {
                        //使用GSON，直接转成Bean对象
                        EmployeeModel employee = gson.fromJson(user, EmployeeModel.class);
                        employeeLists.add(employee);
                    }
                    Log.i(ConfigInfo.TAG,"employeeLists:"+employeeLists.toString());
                    TOTAL_COUNTER = employeeLists.size();
                    mCurrentCounter = employeeLists.size();
                    mHandler.sendEmptyMessage(-1);
                }else{
                    Log.i(ConfigInfo.TAG,"employeeLists_result++++++:"+result);
                    TOTAL_COUNTER = 0;
                    mCurrentCounter = 0;
                    mHandler.sendEmptyMessage(-2);
                }
            }
        });

    }

    private void setListeners() {
        ivTopBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MyManageActivity.this.finish();
            }
        });

    }


    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<MyManageActivity> ref;

        PreviewHandler(MyManageActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MyManageActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:
                    if(activity.isRefresh){
                        activity.mDataAdapter.clear();
                        mCurrentCounter = 0;
                    }
                    mDataAdapter.setDataList(employeeLists);
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    break;
                case -2:
                    Log.i(ConfigInfo.TAG,"这里是-2");
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    break;
                case -3:
                    activity.mRecyclerView.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    break;

                case 0:
                    MyToast.makeShortToast(MyManageActivity.this,"删除失败，请重试");
                    break;
                case 1:
                    mDataAdapter.clear();
                    mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                    mCurrentCounter = 0;
                    requestData();
                    break;

                default:
                    break;
            }
        }
    }
}
