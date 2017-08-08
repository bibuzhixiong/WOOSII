package sochat.so.com.android.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import sochat.so.com.android.R;
import sochat.so.com.android.adapter.PlayVedioCommitRecycleViewAdapter;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.SearchEditText;
import sochat.so.com.android.model.PlayVedioComment;
import sochat.so.com.android.model.PlayVedioCommentResult;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.net.IpResult;
import sochat.so.com.android.net.MyApiService;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.vitamio.LocalVideoBean;
import sochat.so.com.android.vitamio.Resources;
import sochat.so.com.android.vitamio.player.MyMediaController;

/**
 * Created by Administrator on 2017/3/14.
 */

public class PlayVedioActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener,MyMediaController.SetVideoURLListener {

    private ImageView ivBackCircle;

    /**
     * 列表部分
     */
    private MyMediaController mMediaController;
    private int mLayout = VideoView.VIDEO_LAYOUT_STRETCH;

    private View mContentView;
    private View mRl_PlayView;//播放器所在布局
    private View mLoadingView;// 加载中的进度条
    private VideoView mVideoView;// vitmio视频播放控件
    private RelativeLayout mTopTools;// 播放器顶部工具栏
    private ImageView mLockScreenSwitch;// 锁定或解锁屏幕控件
    private ImageView mOrientationSwitch;// 全屏或非全屏开关
    //	private ImageView mVideoShotSwitch;// 截屏开关
    private SeekBar mPlayerSeekbar;// 视频播放进度控件

    public  boolean isPlayComplete = false;// 是否播放完成
    private Resources mVideoRes;
    public  boolean isPlayPause = false;// 是否手动播放暂停
    public  boolean isFirstPlay = true;// 是否第一次播放
    public  boolean isPlayLocalVideo = false;// 是否是在播放本地视频

    private boolean mIsPrepared = false;// 是否已经准备好播放

    public PlayVedioActivity getPlayerActivity(){
        return this;
    }

    /**
     * 视频地址
     */
    private String url;

    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER ;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private PlayVedioCommitRecycleViewAdapter mAdapter = null;
    private PreviewHandler mHandler = new PreviewHandler(PlayVedioActivity.this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private PlayVedioCommentResult Result;

    private SearchEditText etComment;
    /**
     * 头部布局的控件
     */
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvSkipDetail;
    private ImageView ivLike;
    private ImageView ivShare;

    private RelativeLayout rlVedioLayout;
    private TextView tvVedioName;
    private TextView tvVedioPlayNumber;
    private TextView tvVedioAddTime;
    private TextView tvVedioInfo;
    private ImageButton ibVedioClose;

    /**
     * 界面显示相关的数据
     */
    private int pindex =1;
    private int psize =10;
    private List<PlayVedioComment> playVedioComments ;
    private List<PlayVedioComment> lists;
    private String vedio_name;
    private String vedio_content;
    private String vedio_click;
    private String vedio_id;
    private String vedio_addtime;
    private String user_id;
    private int type;
    private int vip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //去掉状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(getApplicationContext());
        Vitamio.isInitialized(PlayVedioActivity.this);

        Log.i(ConfigInfo.TAG,"Vitamio.getLibraryPath()："+Vitamio.getLibraryPath());
        Log.i(ConfigInfo.TAG,"Vitamio.getVitamioPackage()："+Vitamio.getLibraryPath());
        mContentView = View.inflate(this, R.layout.activity_play_vedio, null);
        setContentView(mContentView);
        initData();
        findById();
        initPlayer();


        mAdapter = new PlayVedioCommitRecycleViewAdapter(this);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        //add a HeaderView
        final View header = LayoutInflater.from(this).inflate(R.layout.header_play_vedio_comment,(ViewGroup)findViewById(android.R.id.content), false);
        mLRecyclerViewAdapter.addHeaderView(header);

        /**
         * 初始化头布局
         */
        findHeadView();


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
                if (mAdapter.getDataList().size() > position) {
                    PlayVedioComment item = mAdapter.getDataList().get(position);
//                    MyToast.showShortText(getApplicationContext(), item.getName());
//                    mAdapter.remove(position);
                }

            }

        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                PlayVedioComment item = mAdapter.getDataList().get(position);
//                MyToast.showShortText(getApplicationContext(), "onItemLongClick - " + item.getName());
            }
        });

    }

    private void initPlayer() {
//        int videoType = getIntent().getIntExtra("videoType", -1);
        int videoType = 0;


        if(videoType == 0){
            Resources res = new Resources(0,vedio_name,url,"124551545",vedio_content);

            //在线视频
//            mVideoRes = (Resources) getIntent()
//                    .getParcelableExtra("video");
            mVideoRes = res;
            mVideoRes.setTitle(res.title);
            mVideoRes.setDescription("");
            mVideoRes.setLink(res.link);


        }else if(videoType == 1){
            //本地视频
            isPlayLocalVideo = true;
            LocalVideoBean video = (LocalVideoBean) getIntent()
                    .getParcelableExtra("video");
            mVideoRes = new Resources();
            mVideoRes.setTitle(video.title);
            mVideoRes.setDescription("");
            mVideoRes.setLink(video.path);
            Log.e("zh","本地视频:");
            Log.e("zh","video.title:" + video.title);
            Log.e("zh","video.title:" + video.path);
        }

        ResetVideoPlayer();
        mMediaController.setSetVideoURLListener(this);

        //控制播放器开始播放视频
        mMediaController.doPauseResume();
    }

    @Override
    public void setVideoURI() {
        if(isPlayLocalVideo == true){

            //播放本地视频
            mVideoView.setVideoURI(Uri.parse(mVideoRes.getLink()));
            mMediaController.getPlayerCenterPlaySwitch().setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
            return;
        }else{

            if(mVideoRes.getLink().equals("")||mVideoRes.getLink()==null){
                Toast.makeText(PlayVedioActivity.this, "该视频暂时无法播放！", Toast.LENGTH_SHORT).show();

            }else{
                //播放网络视频
                mVideoView.setVideoPath(mVideoRes.getLink());
                mMediaController.getPlayerCenterPlaySwitch().setVisibility(View.GONE);
                mLoadingView.setVisibility(View.VISIBLE);
                return;
            }

        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ResetVideoPlayer();
        super.onConfigurationChanged(newConfig);
    }

    private void ResetVideoPlayer(){
        // 设置显示名称
        mMediaController.initControllerTools(this, mContentView, mVideoView,this);
        mMediaController.setMediaPlayer(mVideoView);
        mMediaController.setFileName(mVideoRes.getVideoTitle());

        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            SetfullScreen(PlayVedioActivity.this, false);
            mMediaController.isLockedTools = false;

            //显示头部返回键
            ivBackCircle.setVisibility(View.VISIBLE);

            mTopTools.setVisibility(View.GONE);
            mLockScreenSwitch.setVisibility(View.GONE);
//			mVideoCenterSwitch.setVisibility(View.GONE);
            mOrientationSwitch.setImageResource(R.drawable.player_fill);
            mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(PlayVedioActivity.this, 200)));
            if (mVideoView != null && mIsPrepared){
				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, 0);
//				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
//				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                mVideoView.setVideoLayout(-1, 0);
            }
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            //隐藏头部返回键
            ivBackCircle.setVisibility(View.GONE);

            SetfullScreen(PlayVedioActivity.this,true);
            mTopTools.setVisibility(View.VISIBLE);
            mLockScreenSwitch.setVisibility(View.VISIBLE);
//			mVideoCenterSwitch.setVisibility(View.GONE);
            mOrientationSwitch.setImageResource(R.drawable.player_btn_scale);
            mRl_PlayView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            if (mVideoView != null && mIsPrepared){
//				mVideoView.setVideoLayout(mLayout, 0);
//				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                mVideoView.setVideoLayout(-1, 0);
            }
        }
        mVideoView.requestFocus();
    }


    /**
     * 设置是否进入全屏
     * @param activity
     * @param enable
     */
    public void SetfullScreen(Activity activity, boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            hideVirtualButtons();
        } else {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * 隐藏虚拟导航键，使用于api 19+
     */
    @TargetApi(11)
    private void hideVirtualButtons() {
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * 播放器状态变化
     */
    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
        switch (arg1) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                // 开始缓存，暂停播放
                if (isPlaying()) {
                    //System.out.println("zh::::MEDIA_INFO_BUFFERING_START");
                    stopPlayer();
                }
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                // 缓存完成，继续播放
                // System.out.println("zh::::MEDIA_INFO_BUFFERING_ENDMEDIA_INFO_BUFFERING_END");
                if(!isPlayPause){
                    startPlayer();
                    isPlayPause = false;
                }
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                // 显示 下载速度
                // System.out.println("zh::::901");
                if(!isPlayPause){
                    startPlayer();
                    isPlayPause = false;
                }
                mLoadingView.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    /**
     * //在视频预处理完成后调用。在视频预处理完成后被调用。此时视频的宽度、高度、宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mIsPrepared = true;
    }

    /**
     * 播放完成
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {
        mMediaController.updatePausePlay();
        mPlayerSeekbar.setProgress(mPlayerSeekbar.getMax());
        isPlayComplete = true;
        mMediaController.mHandler.removeMessages(2);
        mMediaController.getPlayerCenterPlaySwitch().setVisibility(View.VISIBLE);
    }

    /**
     * 在异步操作调用过程中发生错误时调用。例如视频打开失败。
     */
    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        mLoadingView.setVisibility(View.GONE);
        //		mTv_NoPlay.setVisibility(View.VISIBLE);
        return false;
    }

    /**
     * 在网络视频流缓冲变化时调用。
     * @param arg0
     * @param arg1
     */
    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
//		mTv_NoPlay.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }

    /**
     * 在进度条拖动操作完成后调用。
     */
    @Override
    public void onSeekComplete(MediaPlayer arg0) {
        mMediaController.mHandler.sendEmptyMessage(2);
    }

    @Override
    protected void onDestroy() {
        if(mMediaController != null && mMediaController.mHandler != null){
            //播放器所在界面销毁后，停止更新进度条
            mMediaController.mHandler.removeMessages(2);
        }
        super.onDestroy();
    }

    /**
     * 停止播放
     */
    private void stopPlayer() {
        if (mVideoView != null && mVideoView.isPlaying()){
            mVideoView.pause();
        }
    }

    private boolean isFirstIn = true;
    /**
     * 开始播放
     */
    private void startPlayer() {
        if (mVideoView != null && ! mVideoView.isPlaying()){
            mVideoView.requestFocus();
            mVideoView.start();
            mMediaController.updatePausePlay();
            if(isFirstIn){
                mMediaController.mHandler.sendEmptyMessage(2);
                mMediaController.mHandler.sendMessageDelayed(mMediaController.mHandler.obtainMessage(1), 3000);
                isFirstIn = false;
                isFirstPlay = false;
            }
        }
    }

    /**
     * 播放器是否正在播放
     * @return
     */
    private boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    /**
     * dp转px
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 显示非WIFI网络播放视频提示框
     */
    public void ShowSetNetDialog() {
//		Toast.makeText(getActivity(), "当前已是最新版.", Toast.LENGTH_SHORT).show();
        //要用 android.support.v7.app.AlertDialog 并且设置主题
        AlertDialog dialog = new  AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("您正在使用非WIFI网络播放视频，会消耗手机流量，如执意使用请在设置内打开开关！")
                .setPositiveButton("确定", null)
                .create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindowManager().getDefaultDisplay().getWidth() * 5 / 6 ;
        //	params.height = 200 ;
        dialog.getWindow().setAttributes(params);
    }

    /**
     * 显示设置网络对话框
     */
    public void ShowCheckNetDialog() {
//		Toast.makeText(getActivity(), "当前已是最新版.", Toast.LENGTH_SHORT).show();
        //要用 android.support.v7.app.AlertDialog 并且设置主题
        AlertDialog dialog = new  AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("网络不给力，请检查您的网络！")
                .setPositiveButton("确定", null)
                .create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getWindowManager().getDefaultDisplay().getWidth() * 5 / 6 ;
        //	params.height = 200 ;
        dialog.getWindow().setAttributes(params);
    }

    private void initData() {
//        isLive = getIntent().getBooleanExtra("isLive",false);
//        url = "http://baobab.wdjcdn.com/14564977406580.mp4";
        TeacherCourseList teacherCourse = (TeacherCourseList) getIntent().getSerializableExtra("vedio_info");

//        url = getIntent().getStringExtra("vedio_url");
//        vedio_content = getIntent().getStringExtra("vedio_info");
//        vedio_id = getIntent().getStringExtra("vedio_id");
//        vedio_name = getIntent().getStringExtra("vedio_name");
//        user_id = getIntent().getStringExtra("user_id");

        url = teacherCourse.getUrl();
        vedio_content = teacherCourse.getInfo();
        vedio_id = teacherCourse.getCu_id();
        vedio_name = teacherCourse.getV_name();
        vedio_addtime = teacherCourse.getAddtime();
        vedio_click = teacherCourse.getClick_count();
//        user_id = getIntent().getStringExtra("user_id");
        vip = teacherCourse.getVip();
        type = teacherCourse.getType();

        clickNumber();
    }


    private void findById() {
        ivBackCircle = (ImageView) findViewById(R.id.iv_back_circle);
        ivBackCircle.setOnClickListener(this);

        rlVedioLayout = (RelativeLayout) findViewById(R.id.rl_vedio_layout);
        tvVedioName = (TextView) findViewById(R.id.tv_vedio_name);
        tvVedioInfo = (TextView) findViewById(R.id.tv_vedio_info);
        tvVedioPlayNumber = (TextView) findViewById(R.id.tv_vedio_play_number);
        tvVedioAddTime = (TextView) findViewById(R.id.tv_addtime);
        ibVedioClose = (ImageButton) findViewById(R.id.ibClose);


        rlVedioLayout.setVisibility(View.GONE);
        tvVedioName.setText("名称："+vedio_name);
        tvVedioPlayNumber.setText("播放："+vedio_click+" 次播放");
        tvVedioAddTime.setText("时间："+vedio_addtime);
        tvVedioInfo.setText(vedio_content);

        ibVedioClose.setOnClickListener(this);


        mRecyclerView = (LRecyclerView) findViewById(R.id.list);
        etComment = (SearchEditText) findViewById(R.id.et_comment);
        lists = new ArrayList<PlayVedioComment>();
        playVedioComments = new ArrayList<PlayVedioComment>();

        mMediaController = (MyMediaController) findViewById(R.id.MyMediaController);
        mVideoView = (VideoView) findViewById(R.id.vitamio_videoview);
        mLoadingView = findViewById(R.id.video_loading);
        mRl_PlayView = findViewById(R.id.id_ViewLayout);

        mTopTools = (RelativeLayout)findViewById(R.id.player_top_bar);
        mLockScreenSwitch = (ImageView)findViewById(R.id.player_iv_lock_screen);
        mOrientationSwitch = (ImageView)findViewById(R.id.orientation_switch);
        mPlayerSeekbar = (SeekBar)findViewById(R.id.player_seekbar);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnSeekCompleteListener(this);


        //按回车键开始评论
        etComment.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(etComment.getText().toString().trim())){
                    MyToast.makeShortToast(PlayVedioActivity.this,"评论为空");
                }else{
                    comment();
                }
            }
        });

    }

    private void comment() {
//        /index.php/Vr/Vlive/school_com?user_id=920&vedio_id=18&content=xx
        String comment = null;
        try {
            comment = (etComment.getText().toString()!=null? URLEncoder.encode(etComment.getText().toString(),"UTF-8"):etComment.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/school_com?user_id="+DemoHelper.getUid()+"&vedio_id="+vedio_id+"&content="+comment;
        Log.i(ConfigInfo.TAG,"PlayVedioActivity_url:"+url);
        HttpUtils.doGetAsyn(PlayVedioActivity.this, true, url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"PlayVedioActivity_result:"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code ==1){//成功
                        mHandler.sendEmptyMessage(4);
                    }else{//失败
                        mHandler.sendEmptyMessage(5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibClose:
                rlVedioLayout.setVisibility(View.GONE);
                break;
            case R.id.iv_back_circle:
                PlayVedioActivity.this.finish();
                break;
        }

    }
    /**
     * 退出应用
     */
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        // 必须要加后面的ACTION_DOWN的判断，不然会加载两次
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getRepeatCount() == 0) {
                int mCurrentOrientation = getResources().getConfiguration().orientation;
                // 用再按一次退出程序的方法来提示
                if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
//                    if(view !=null){
//                        view.setVisibility(View.VISIBLE);
//                    }
                    return false;
                }else{
                    finish();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }


    /****************************************************************************************************************/


    private void findHeadView() {
        View headView = mLRecyclerViewAdapter.getHeaderView();
        tvContent = (TextView) headView.findViewById(R.id.tv_content);
        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
        tvSkipDetail = (TextView) headView.findViewById(R.id.tv_skip_detail);
        ivLike = (ImageView) headView.findViewById(R.id.iv_like);
        ivShare = (ImageView) headView.findViewById(R.id.iv_share);

        if (type ==1){
            ivLike.setImageResource(R.drawable.like_red);
        }else{
            ivLike.setImageResource(R.drawable.like);
        }
        //点击监听器
        listener = new MyClick();
        tvSkipDetail.setOnClickListener(listener);
        ivLike.setOnClickListener(listener);
        ivShare.setOnClickListener(listener);
    }
    private MyClick listener;
    class MyClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_like:
                    Log.i(ConfigInfo.TAG,"type"+type);
                   if (type ==1){
                       cancelCollect();
                   }else{
                       collect();
                   }

                    break;

                case R.id.iv_share:
                    break;
                case R.id.tv_skip_detail:
                    rlVedioLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private Map<String, Object> parameters = new HashMap<String, Object>();
    private Map<String, String> headers = new HashMap<>();

    private ArrayList<String>count ;
    private void cancelCollect() {

        count = new ArrayList<String>();
        count.add(vedio_id);
        String array = "{\"user_id\":"+DemoHelper.getUid()+","+"\"cu_id\":"+count.toString()+"}";
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/del_coll?arr="+array;
        Log.i(ConfigInfo.TAG,"我就是url:"+url.toString());


        parameters.clear();
        parameters.put("arr", array);
        headers.put("Accept", "application/json");

        MyNetWorkUtil.getNovate(PlayVedioActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_CACHE,ConfigInfo.ApiUrl);
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);
            MyNetWorkUtil.novate.call(MyNetWorkUtil.myAPI.getSougu(parameters), new BaseSubscriber<IpResult>(PlayVedioActivity.this) {
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(IpResult result) {
                    //请求成功
                    Log.i(ConfigInfo.TAG,"000000"+result.toString());
                    int code =result.getCode();
                    if (code ==0){//失败
                        mHandler.sendEmptyMessage(0);
                    }else if (code ==1){//成功
                        mHandler.sendEmptyMessage(1);
                    }
                }
            });

        }
    }

    private void collect() {
        String collectUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/collection?user_id="+DemoHelper.getUid()+"&cu_id="+vedio_id;
        Log.i(ConfigInfo.TAG,"collectUrl:"+collectUrl);
        HttpUtils.doGetAsyn(PlayVedioActivity.this,true,collectUrl, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code ==0){//失败
                        mHandler.sendEmptyMessage(2);
                    }else if (code ==1){//成功
                        mHandler.sendEmptyMessage(3);
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

    private void addItems(ArrayList<PlayVedioComment> list) {

        mAdapter.addAll(list);
        mCurrentCounter += list.size();

    }

    private class PreviewHandler extends Handler {

        private WeakReference<PlayVedioActivity> ref;

        PreviewHandler(PlayVedioActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PlayVedioActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            switch (msg.what) {

                case -1:
                    //只能在UI线程更新UI
                    initView();
                    etComment.setText("");
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
                    MyToast.makeShortToast(PlayVedioActivity.this,"取消收藏失败");
                    break;
                case 1:
                    type = 0;
                    ivLike.setImageResource(R.drawable.like);
                    break;
                case 2://收藏失败
                    MyToast.makeShortToast(PlayVedioActivity.this,"添加收藏失败");
                    break;
                case 3://收藏成功
//                    MyToast.makeShortToast(PlayVedioActivity.this,"已收藏");
                    type = 1;
                    ivLike.setImageResource(R.drawable.like_red);
                    break;
                case 4://评论成功
                    MyToast.makeShortToast(PlayVedioActivity.this,"评论成功");
                    mCurrentCounter = 0;
//                    mRecyclerView.refresh();
                    lists.clear();
                    requestData();
                    break;
                case 5://评论失败
                    MyToast.makeShortToast(PlayVedioActivity.this,"评论失败");
                    break;

                default:
                    break;
            }
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (lists!=null){
            lists.clear();
        }
        requestData();
    }

    /**
     * 请求网络
     */
    private void requestData() {
        psize = 10;
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/r_school_com?cu_id="+vedio_id+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn(PlayVedioActivity.this,false,url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"result:"+result);
                Gson gson =new Gson();
                Result = gson.fromJson(result,PlayVedioCommentResult.class);
                if (Result != null){
                    playVedioComments = Result.getChilds();
                    TOTAL_COUNTER = Result.getCount();
                    if (playVedioComments !=null){
                            lists.addAll(playVedioComments);
                        }
                    mHandler.sendEmptyMessage(-1);
                }else{
                    mHandler.sendEmptyMessage(-3);

                }
            }
        });
    }

    private void initView() {
        tvContent.setText(vedio_content);
        tvTitle.setText(vedio_name);
    }


    private void clickNumber(){
        String click_number_url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/sub_click?cu_id="+vedio_id;
        HttpUtils.doGetAsyn(PlayVedioActivity.this, false, click_number_url, mHandler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
//                    int code = jsonObject.getInt("code");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
