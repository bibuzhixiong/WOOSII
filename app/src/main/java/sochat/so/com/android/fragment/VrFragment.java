package sochat.so.com.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/2/20.
 */

public class VrFragment extends BaseFragment implements View.OnClickListener{
    private View view;
    private Activity mActivity;
    private TextView tvTopTitle;
    private ImageView ivSearch;
//    /**
//     * RecycleView
//     */
//    private RecyclerView mRecyclerView;
//    /**
//     * 下拉刷新的组件
//     */
//    private PtrClassicFrameLayout mPtrClassicFrameLayout;
//    /**
//     * 下拉刷新的Adapter
//     */
//    private RecyclerAdapterWithHF ptrAdapter;
//
//    /**
//     * RecycleView的Adapter
//     */
//    private MyMarketingRecyclerViewAdapter vrAdapter;
//
//    private ArrayList<VrModel>vrModels;
//    private ArrayList<Child>childs;
//    private VrModelResult vrModelResult;
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0://下拉刷新
//                    vrAdapter.setList(childs);
//                    if (mPtrClassicFrameLayout != null) {
//                        mPtrClassicFrameLayout.refreshComplete();
//                        mPtrClassicFrameLayout.setLoadMoreEnable(true);
//                    }
//                    break;
//
//                case 1://上拉加载
//
//                    break;
//                case 2://获得数据成功
//                    vrAdapter.setList(childs);
//                    break;
//                case 3://获得数据失败
//
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

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
        view = inflater.inflate(R.layout.fragment_vr,container,false);
//        findById();
//        initData();
//        setListener();

        return view;
    }

//    private void findById() {
//        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_text);
//        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.test_recycler_view);
//        mPtrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.test_recycler_view_frame);
//
//    }
//
//    private void initData() {
//        tvTopTitle.setText("VR");
//        ivSearch.setVisibility(View.GONE);
//        //获得列表数据
//        getData();
//
//        mRecyclerView.setHasFixedSize(true);
//        //设置布局管理器
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
////        设置Item增加、移除动画
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        //添加分割线
////        mRecyclerView.addItemDecoration(new DividerItemDecoration(
////        getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
//        vrAdapter = new MyMarketingRecyclerViewAdapter(childs,mActivity);
////        setHeader(mRecyclerView);
//        ptrAdapter = new RecyclerAdapterWithHF(vrAdapter);
//        mRecyclerView.setAdapter(ptrAdapter);
//
//    }
//
////    private void setHeader(RecyclerView mRecyclerView) {
////        View header = LayoutInflater.from(mActivity).inflate(R.layout.header_recyclerview,mRecyclerView,false);
////        vrAdapter.setHeaderView(header);
////    }
//
//    private void getData() {
//        vrModels = new ArrayList<VrModel>();
//        childs = new ArrayList<Child>();
//
//        HttpUtils.doGetAsyn(ConfigInfo.VR_VIDEO_URL, handler, new HttpUtils.CallBack() {
//            @Override
//            public void onRequestComplete(String result) {
//                if (result!=null){
//                    Gson gson = new Gson();
//                    vrModelResult = gson.fromJson(result, VrModelResult.class);
//                    childs = vrModelResult.getChilds();
//                    handler.sendEmptyMessage(2);//成功获得数据
//                }else{
//                    handler.sendEmptyMessage(3);//获得数据失败
//                }
//
//            }
//        });
//    }
//
//    private void setListener() {
//        tvTopTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(mActivity, U3DActivity.class);
//                CommonUtils.startActivity(mActivity,intent);
//            }
//        });
//
//// 下拉刷新
//        mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        vrModels.clear();
//                        getData();
//                        handler.sendEmptyMessage(0);
//                    }
//                }, 2000);
//            }
//        });
//        // 上拉加载
//        mPtrClassicFrameLayout.setLoadMoreHandler(new PtrFrameLayout.LoadMoreHandler() {
//            @Override
//            public void loadMore() {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    }
//                }, 1000);
//            }
//        });
//    }
//
//
//    public static VrFragment newInstance(){
//        VrFragment fragment = new VrFragment();
//        return fragment;
//
//    }
//
    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }

}
