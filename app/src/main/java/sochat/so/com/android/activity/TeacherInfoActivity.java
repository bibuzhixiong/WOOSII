package sochat.so.com.android.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.TeacherInfoAdapter;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.model.TeacherInfoResult;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/3/15.
 */

public class TeacherInfoActivity extends BaseActivity {
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER = 1000;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private TeacherInfoAdapter mAdapter = null;

    private PreviewHandler mHandler = new PreviewHandler(TeacherInfoActivity.this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private TeacherInfoResult teacherInfoResult;
    //头部信息
    /**
     * 老师名字
     */
    private String name;
    /**
     * 身份号码
     */
    private String user_id;
    /**
     * 头像缩略图地址
     */
    private String thumb;
    /**
     * 老师简介
     */
    private String detail;
    /**
     * 视频数量
     */
    private String count;
    /**
     * 观看数量
     */
    private String follow;
    /**
     * 视频id
     */
    private String cu_id;
    /**
     * 视频简介
     */
    private String info;
    /**
     * 是否被关注
     */
    private int code;
    /**
     * 点击支付的时间
     */
    private int paytime;

    private RelativeLayout relativeLayout;

    //老师背景图
     private ImageView ivBackground;

    /**
     * 底部弹出显示是否购买视频
     */
    private BottomPopupWindow bottomPopupWindow;
    /**
     * 视频时长
     */
    private int longtime;
    /**
     * 视频id
     */
    private String pay_cu_id;
    /**
     * 底部popupwindow的按钮
     */
    private TextView tvCommit;
    private TextView tvCancel;
    /**
     * 视频对象
     */
    private TeacherCourseList item =null;


    private TextView tvName ;
    private TextView tvAttentionCount ;
    private TextView tvVedioCount ;
    private TextView tvContent ;
    private TextView tvTopTitle ;
    private ImageView ivThumb;
    private ImageView ivTopBack;
    private ImageView ivTopShare;
    private ImageView ivAttention;
    private MyToast toast;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        relativeLayout = (RelativeLayout) findViewById(R.id.ll_layout);
        bottomPopupWindow = new BottomPopupWindow(this);
        courseChilds = new ArrayList<TeacherCourseList>();
        lists = new ArrayList<TeacherCourseList>();
        Intent intent = getIntent();
        uid = intent.getStringExtra("teacher_uid");


        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        mAdapter = new TeacherInfoAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        //add a HeaderView
        final View header = LayoutInflater.from(this).inflate(R.layout.header_teacher_info_immersion,(ViewGroup)findViewById(android.R.id.content), false);
        mLRecyclerViewAdapter.addHeaderView(header);

        /**
         * 初始化头布局
         */
        findHeadView();


        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                pindex =1;
//                mAdapter.clear();
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
                if (mAdapter.getDataList().size() > position) {

                    item = mAdapter.getDataList().get(position);
                    int vip = item.getVip();
                    pay_cu_id = item.getCu_id();
                    int free = item.getFree();
                    Intent intent ;
                    if (item.getVr()==0){
                        intent = new Intent(TeacherInfoActivity.this,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(TeacherInfoActivity.this,PlayVedioActivity.class);
                    }
                    if (vip ==0){//免费视频

                        intent.putExtra("vedio_info",item);
                        CommonUtils.startActivity(TeacherInfoActivity.this,intent);
                    }else if (vip ==1){//收费视频
                        if (free ==0){
                            paytime = item.getLongtime();
                            bottomPopupWindow.show(TeacherInfoActivity.this);
                        }else if(free ==1){
                            intent.putExtra("vedio_info",item);
                            CommonUtils.startActivity(TeacherInfoActivity.this,intent);
                        }


                    }
                }
            }

        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                TeacherCourseList item = mAdapter.getDataList().get(position);
//                MyToast.showShortText(getApplicationContext(), "onItemLongClick - " + item.getName());
            }
        });

    }


    private class BottomPopupWindow extends BottomPushPopupWindow<Void>{

        public BottomPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_pay,null);
            tvCommit = (TextView) bottom.findViewById(R.id.tv_commit);
            tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);
            return bottom;
        }
    }

    private void findHeadView() {
        View headView = mLRecyclerViewAdapter.getHeaderView();
        tvName = (TextView) headView.findViewById(R.id.tv_user_name);
        tvAttentionCount = (TextView) headView.findViewById(R.id.tv_attention_count);
        tvVedioCount = (TextView) headView.findViewById(R.id.tv_vedio_count);
        tvContent = (TextView) headView.findViewById(R.id.tv_info_content);
        tvTopTitle = (TextView) headView.findViewById(R.id.tv_top_text);
        ivThumb = (ImageView) headView.findViewById(R.id.iv_user_photo);
        ivTopBack = (ImageView) headView.findViewById(R.id.iv_top_back);
        ivTopShare = (ImageView) headView.findViewById(R.id.iv_share);
        ivAttention = (ImageView) headView.findViewById(R.id.iv_attention);

        //老师背景图
        ivBackground = (ImageView) headView.findViewById(R.id.iv_background);

        //点击监听器
        listener = new MyClick();

        tvCommit.setOnClickListener(listener);
        tvCancel.setOnClickListener(listener);

        ivTopShare.setOnClickListener(listener);
        ivTopBack.setOnClickListener(listener);
        ivAttention.setOnClickListener(listener);

        tvTopTitle.setVisibility(View.GONE);
    }
    private MyClick listener;
    class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_top_back:
                    TeacherInfoActivity.this.finish();
                    break;
                case R.id.iv_share:

                    break;

                case R.id.iv_attention:
                    String attentionurl = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/add_attention?user_id="+ DemoHelper.getUid()+"&teacher_id="+uid;
                    HttpUtils.doGetAsyn(TeacherInfoActivity.this, true, attentionurl, mHandler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {//失败
                                    mHandler.sendEmptyMessage(0);
                                } else if (code == 1) {//成功
                                    mHandler.sendEmptyMessage(1);
                                } else if (code == 2) {
                                    mHandler.sendEmptyMessage(2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case R.id.tv_commit:
                    toPayVedio();
                    bottomPopupWindow.dismiss();
                    break;
                case R.id.tv_cancel:
                    bottomPopupWindow.dismiss();
                    break;
            }
        }
    }

    private void toPayVedio() {
        String payUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/dividedSystem?user_id="+ DemoHelper.getUid()+"&cu_id="+pay_cu_id+"&region_id="+DemoHelper.getRegion_id()+"&time="+paytime;
//        String payUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/vip?user_id="+DemoHelper.getUid()+"&cu_id="+pay_cu_id;
        HttpUtils.doGetAsyn(TeacherInfoActivity.this, true, payUrl, mHandler, new HttpUtils.CallBack() {
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

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private class PreviewHandler extends Handler {

        private WeakReference<TeacherInfoActivity> ref;

        PreviewHandler(TeacherInfoActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final TeacherInfoActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    //只能在UI线程更新UI
                    initView();

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
                case 0:
                    toast.makeShortToast(TeacherInfoActivity.this,"关注失败");
                    break;
                case 1:
                    ivAttention.setImageResource(R.drawable.yiguanzhu);
                    toast.makeShortToast(TeacherInfoActivity.this,"关注成功");
                    break;
                case 2:
                    toast.makeShortToast(TeacherInfoActivity.this,"已经关注");
                    break;
                case 3:
                    toast.makeShortToast(TeacherInfoActivity.this,"购买失败，请稍后重试");
                    bottomPopupWindow.dismiss();
                    break;
                case 4:
                    Intent intent ;
                    if (item.getVr()==0){
                        intent = new Intent(TeacherInfoActivity.this,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(TeacherInfoActivity.this,PlayVedioActivity.class);
                    }
                    intent.putExtra("vedio_info",item);
                    CommonUtils.startActivity(TeacherInfoActivity.this,intent);
                    toast.makeShortToast(TeacherInfoActivity.this,"购买成功");
                    bottomPopupWindow.dismiss();
                    break;
                case 5:
                    toast.makeShortToast(TeacherInfoActivity.this,"您的时间余额不足，请先去充值");
                    bottomPopupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        pindex =1;
        if (mLRecyclerViewAdapter !=null){
            mLRecyclerViewAdapter.notifyDataSetChanged();
        }
        mCurrentCounter = 0;
        if (lists !=null){
            lists.clear();
        }
        requestData();
    }

    private String uid;
    private int pindex =1;
    private int psize =10;
    private List<TeacherCourseList> courseChilds ;
    private List<TeacherCourseList> lists;




    /**
     * 请求网络
     */
    private void requestData() {
       final String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/teacher_info?uid="+DemoHelper.getUid()+"&user_id="+uid+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(TeacherInfoActivity.this,false,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"url"+url);
                Gson gson =new Gson();
                teacherInfoResult = gson.fromJson(result,TeacherInfoResult.class);
                if (teacherInfoResult != null){
                    courseChilds = teacherInfoResult.getChilds();
                    name = teacherInfoResult.getName();
                    thumb = teacherInfoResult.getThumb();
                    count = teacherInfoResult.getCount();
                    detail = teacherInfoResult.getDetail();
                    follow = teacherInfoResult.getFollow();
                    TOTAL_COUNTER = teacherInfoResult.getCoun();
                    code = teacherInfoResult.getCode();
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


    private void initView() {
        tvContent.setText(detail);
        tvAttentionCount.setText(follow+" 关注");
        tvVedioCount.setText(count+" 视频");
        tvName.setText(name);
        if (thumb!=null){
            if (thumb.startsWith("http")){
                Picasso.with(this).load(thumb).transform(new BlurTransformation(this)).placeholder(R.drawable.zhanweifu).error(R.drawable.beijingtu).into(ivBackground);
                Picasso.with(this).load(thumb).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivThumb);
            }else{
                Picasso.with(this).load(ConfigInfo.ApiUrl+thumb).transform(new BlurTransformation(this)).error(R.drawable.beijingtu).into(ivBackground);
                Picasso.with(this).load(ConfigInfo.ApiUrl+thumb).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }
        if (code ==0){
            ivAttention.setImageResource(R.drawable.guanzhu);
        }else{
            ivAttention.setImageResource(R.drawable.yiguanzhu);
        }


//        ivBackground.setImageBitmap(rsBlur(this,((BitmapDrawable)ivThumb.getDrawable()).getBitmap(),20));

    }


//    private static Bitmap rsBlur(Context context, Bitmap source, int radius){
//
//        Bitmap inputBmp = source;        //(1)
//        RenderScript renderScript =  RenderScript.create(context);
//        //(2)
//        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);        final Allocation output = Allocation.createTyped(renderScript,input.getType());        //(3)
//        // Load up an instance of the specific script that we want to use.
//        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));        //(4)
//        scriptIntrinsicBlur.setInput(input);        //(5)
//        // Set the blur radius
//        scriptIntrinsicBlur.setRadius(radius);        //(6)
//        // Start the ScriptIntrinisicBlur
//        scriptIntrinsicBlur.forEach(output);        //(7)
//        // Copy the output to the blurred bitmap
//        output.copyTo(inputBmp);        //(8)
//        renderScript.destroy();    return inputBmp;
//    }


    /**
     * 模糊
     * @author jia
     *
     */
    public class BlurTransformation implements Transformation {

        RenderScript rs;

        public BlurTransformation(Context context) {
            super();
            rs = RenderScript.create(context);
        }

        @SuppressLint("NewApi")
        @Override
        public Bitmap transform(Bitmap bitmap) {
            // 创建一个Bitmap作为最后处理的效果Bitmap
            Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // 分配内存
            Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(rs, input.getType());

            // 根据我们想使用的配置加载一个实例
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // 设置模糊半径
            script.setRadius(10);

            //开始操作
            script.forEach(output);

            // 将结果copy到blurredBitmap中
            output.copyTo(blurredBitmap);

            //释放资源
            bitmap.recycle();

            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blur";
        }
    }
}
