package sochat.so.com.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;
import com.tamic.novate.BaseSubscriber;
import com.tamic.novate.Throwable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * Created by Administrator on 2017/7/22.
 */

public class PlayVRVedioActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = PlayVRVedioActivity.class.getSimpleName();

    /**
     * Preserve the video's state when rotating the phone.
     */
    private static final String STATE_IS_PAUSED = "isPaused";
    private static final String STATE_PROGRESS_TIME = "progressTime";
    /**
     * The video duration doesn't need to be preserved, but it is saved in this example. This allows
     * the seekBar to be configured during {@link #onRestoreInstanceState(Bundle)} rather than waiting
     * for the video to be reloaded and analyzed. This avoid UI jank.
     */
    private static final String STATE_VIDEO_DURATION = "videoDuration";

    /**
     * Arbitrary constants and variable to track load status. In this example, this variable should
     * only be accessed on the UI thread. In a real app, this variable would be code that performs
     * some UI actions when the video is fully loaded.
     */
    public static final int LOAD_VIDEO_STATUS_UNKNOWN = 0;
    public static final int LOAD_VIDEO_STATUS_SUCCESS = 1;
    public static final int LOAD_VIDEO_STATUS_ERROR = 2;

    private int loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;

    /** Tracks the file to be loaded across the lifetime of this app. **/
    private Uri fileUri;

    /** Configuration information for the video. **/
    private VrVideoView.Options videoOptions = new VrVideoView.Options();

    private VideoLoaderTask backgroundVideoLoaderTask;

    /**
     * The video view and its custom UI elements.
     */
    protected VrVideoView videoWidgetView;

    /**
     * Seeking UI & progress indicator. The seekBar's progress value represents milliseconds in the
     * video.
     */
    private SeekBar seekBar;
    private TextView statusText;

    private ImageButton volumeToggle;
    private boolean isMuted;



    //控制器上的暂停播放按钮
    private ImageView player_play_switch;
    //屏幕中央的按钮
    private ImageView player_center_switch;
    private TextView player_current_time;
    private TextView player_total_time;


    private TeacherCourseList teacherCourse ;

    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER ;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private static final int REQUEST_COUNT = 10;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;

    private LRecyclerView mRecyclerView = null;

    private PlayVedioCommitRecycleViewAdapter mAdapter = null;
    private PreviewHandler mHandler = new PreviewHandler(PlayVRVedioActivity.this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private PlayVedioCommentResult Result;
    private SearchEditText etComment;
    /**
     * 头部布局的控件
     */
    private ImageView ivBackCircle;
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

    /**
     * By default, the video will start playing as soon as it is loaded. This can be changed by using
     * {@link VrVideoView#pauseVideo()} after loading the video.
     */
    private boolean isPaused = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_vr_vedio);
        //设置屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        teacherCourse = (TeacherCourseList) getIntent().getSerializableExtra("vedio_info");

        Log.d(ConfigInfo.TAG,"打印一下："+teacherCourse.toString());

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener());
        statusText = (TextView) findViewById(R.id.status_text);


        player_play_switch = (ImageView) findViewById(R.id.player_play_switch);
        player_center_switch = (ImageView) findViewById(R.id.player_center_switch);
        player_current_time = (TextView) findViewById(R.id.player_current_time);
        player_total_time = (TextView) findViewById(R.id.player_total_time);


        player_play_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickPlay();
            }
        });
        player_center_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickPlay();
            }
        });


        // Bind input and output objects for the view.
        videoWidgetView = (VrVideoView) findViewById(R.id.video_view);



        videoWidgetView.setInfoButtonEnabled(false);


        videoWidgetView.setEventListener(new ActivityEventListener());

        volumeToggle = (ImageButton) findViewById(R.id.volume_toggle);
        volumeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsMuted(!isMuted);
            }
        });

        loadVideoStatus = LOAD_VIDEO_STATUS_UNKNOWN;

        // Initial launch of the app or an Activity recreation due to rotation.
        handleIntent(getIntent());



        initData();
        findById();

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
                }

            }

        });

        mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                PlayVedioComment item = mAdapter.getDataList().get(position);
            }
        });

    }

    /**
     * Called when the Activity is already running and it's given a new intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(TAG, this.hashCode() + ".onNewIntent()");
        // Save the intent. This allows the getIntent() call in onCreate() to use this new Intent during
        // future invocations.
        setIntent(intent);
        // Load the new video.
        handleIntent(intent);
    }

    /**
     * 播放暂停切换
     */
    private void performClickPlay() {
        Log.d(ConfigInfo.TAG,"一直在执行");
        if (!isPaused) {
            videoWidgetView.pauseVideo();
            player_play_switch.setImageResource(R.mipmap.player_mediacontroller_play);
            //正在播放就隐藏这个按钮
            player_center_switch.setVisibility(View.GONE);
            isPaused = true;
        } else {
            videoWidgetView.playVideo();
            //暂停播放就显示这个按钮
            player_center_switch.setVisibility(View.VISIBLE);
            player_play_switch.setImageResource(R.mipmap.player_mediacontroller_pause);
            isPaused = false;
        }
    }



    public int getLoadVideoStatus() {
        return loadVideoStatus;
    }

    private void setIsMuted(boolean isMuted) {
        this.isMuted = isMuted;
        volumeToggle.setImageResource(isMuted ? R.drawable.volume_off : R.drawable.volume_on);
        videoWidgetView.setVolume(isMuted ? 0.0f : 1.0f);
    }

    public boolean isMuted() {
        return isMuted;
    }

    /**
     * Load custom videos based on the Intent or load the default video. See the Javadoc for this
     * class for information on generating a custom intent via adb.
     */
    private void handleIntent(Intent intent) {
        // Determine if the Intent contains a file to load.
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Log.i(TAG, "ACTION_VIEW Intent received");

            fileUri = intent.getData();
            if (fileUri == null) {
                Log.w(TAG, "No data uri specified. Use \"-d /path/filename\".");
            } else {
                Log.i(TAG, "Using file " + fileUri.toString());
            }

            videoOptions.inputFormat = intent.getIntExtra("inputFormat", VrVideoView.Options.FORMAT_DEFAULT);
            videoOptions.inputType = intent.getIntExtra("inputType", VrVideoView.Options.TYPE_MONO);
        } else {
            Log.i(TAG, "Intent is not ACTION_VIEW. Using the default video.");
            fileUri = null;
        }

        // Load the bitmap in a background thread to avoid blocking the UI thread. This operation can
        // take 100s of milliseconds.
        if (backgroundVideoLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundVideoLoaderTask.cancel(true);
        }
        backgroundVideoLoaderTask = new VideoLoaderTask();
        backgroundVideoLoaderTask.execute(Pair.create(fileUri, videoOptions));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(STATE_PROGRESS_TIME, videoWidgetView.getCurrentPosition());
        savedInstanceState.putLong(STATE_VIDEO_DURATION, videoWidgetView.getDuration());
        savedInstanceState.putBoolean(STATE_IS_PAUSED, isPaused);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        long progressTime = savedInstanceState.getLong(STATE_PROGRESS_TIME);
        videoWidgetView.seekTo(progressTime);
        seekBar.setMax((int) savedInstanceState.getLong(STATE_VIDEO_DURATION));
        seekBar.setProgress((int) progressTime);

        isPaused = savedInstanceState.getBoolean(STATE_IS_PAUSED);
        if (isPaused) {
            videoWidgetView.pauseVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Prevent the view from rendering continuously when in the background.
        videoWidgetView.pauseRendering();
        // If the video is playing when onPause() is called, the default behavior will be to pause
        // the video and keep it paused when onResume() is called.
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the 3D rendering.
        videoWidgetView.resumeRendering();
        // Update the text to account for the paused video in onPause().
        updateStatusText();
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        videoWidgetView.shutdown();
        super.onDestroy();
    }

    private void togglePause() {
        if (isPaused) {
            videoWidgetView.playVideo();
        } else {
            videoWidgetView.pauseVideo();
        }
        isPaused = !isPaused;
        updateStatusText();
    }

    private void updateStatusText() {

        Log.d(ConfigInfo.TAG,"WWWWWWWWWWWWWWWWWWWWWW");
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。

        player_total_time.setText(formatter.format(videoWidgetView.getDuration()));
        player_current_time.setText(formatter.format(videoWidgetView.getCurrentPosition()));
        if (isPaused){
            //暂停状态隐藏中心的控件
            player_center_switch.setVisibility(View.VISIBLE);
            player_play_switch.setImageResource(R.mipmap.player_mediacontroller_play);
        }else{
            //播放状态显示中心的控件
            player_center_switch.setVisibility(View.GONE);
            player_play_switch.setImageResource(R.mipmap.player_mediacontroller_pause);
        }
    }

    /**
     * When the user manipulates the seek bar, update the video position.
     */
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoWidgetView.seekTo(progress);
                updateStatusText();
            } // else this was from the ActivityEventHandler.onNewFrame()'s seekBar.setProgress update.
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrVideoEventListener {
        /**
         * Called by video widget on the UI thread when it's done loading the video.
         */
        @Override
        public void onLoadSuccess() {
            Log.i(TAG, "Successfully loaded video " + videoWidgetView.getDuration());
            loadVideoStatus = LOAD_VIDEO_STATUS_SUCCESS;
            seekBar.setMax((int) videoWidgetView.getDuration());
            updateStatusText();
        }

        /**
         * Called by video widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            // An error here is normally due to being unable to decode the video format.
            loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
            Toast.makeText(
                    PlayVRVedioActivity.this, "Error loading video: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading video: " + errorMessage);
        }

        @Override
        public void onClick() {
            togglePause();
        }

        /**
         * Update the UI every frame.
         */
        @Override
        public void onNewFrame() {
            updateStatusText();
            seekBar.setProgress((int) videoWidgetView.getCurrentPosition());
        }

        /**
         * Make the video play in a loop. This method could also be used to move to the next video in
         * a playlist.
         */
        @Override
        public void onCompletion() {
            videoWidgetView.seekTo(0);
        }
    }

    /**
     * Helper class to manage threading.
     */
    class VideoLoaderTask extends AsyncTask<Pair<Uri, VrVideoView.Options>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(final Pair<Uri, VrVideoView.Options>... fileInformation) {
            try {
                if (fileInformation == null || fileInformation.length < 1
                        || fileInformation[0] == null || fileInformation[0].first == null) {
                    // No intent was specified, so we default to playing the local stereo-over-under video.
                    final VrVideoView.Options options = new VrVideoView.Options();
                    options.inputType = VrVideoView.Options.TYPE_MONO;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Uri uri = Uri.parse(teacherCourse.getUrl());

                                videoWidgetView.loadVideo(uri,options);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                videoWidgetView.loadVideo(fileInformation[0].first, fileInformation[0].second);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {
                // An error here is normally due to being unable to locate the file.
                loadVideoStatus = LOAD_VIDEO_STATUS_ERROR;
                // Since this is a background thread, we need to switch to the main thread to show a toast.
                videoWidgetView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayVRVedioActivity.this, "Error opening file. ", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "Could not open video: " + e);
            }

            return true;
        }
    }

    /**
     * 评论和视频详情的部分
     */

    private void initData() {

        vedio_content = teacherCourse.getInfo();
        vedio_id = teacherCourse.getCu_id();
        vedio_name = teacherCourse.getV_name();
        vedio_addtime = teacherCourse.getAddtime();
        vedio_click = teacherCourse.getClick_count();
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

        //按回车键开始评论
        etComment.setOnSearchClickListener(new SearchEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                mRecyclerView.setVisibility(View.VISIBLE);
                if(TextUtils.isEmpty(etComment.getText().toString().trim())){
                    MyToast.makeShortToast(PlayVRVedioActivity.this,"评论为空");
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
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/school_com?user_id="+ DemoHelper.getUid()+"&vedio_id="+vedio_id+"&content="+comment;
        Log.i(ConfigInfo.TAG,"PlayVedioActivity_url:"+url);
        HttpUtils.doGetAsyn(PlayVRVedioActivity.this, true, url, mHandler, new HttpUtils.CallBack() {
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
                PlayVRVedioActivity.this.finish();
                break;
        }

    }

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

        MyNetWorkUtil.getNovate(PlayVRVedioActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_CACHE,ConfigInfo.ApiUrl);
        if (MyNetWorkUtil.novate!=null){
            MyNetWorkUtil.myAPI = MyNetWorkUtil.novate.create(MyApiService.class);
            MyNetWorkUtil.novate.call(MyNetWorkUtil.myAPI.getSougu(parameters), new BaseSubscriber<IpResult>(PlayVRVedioActivity.this) {
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
        HttpUtils.doGetAsyn(PlayVRVedioActivity.this,true,collectUrl, mHandler, new HttpUtils.CallBack() {
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

        private WeakReference<PlayVRVedioActivity> ref;

        PreviewHandler(PlayVRVedioActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PlayVRVedioActivity activity = ref.get();
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
                    MyToast.makeShortToast(PlayVRVedioActivity.this,"取消收藏失败");
                    break;
                case 1:
                    type = 0;
                    ivLike.setImageResource(R.drawable.like);
                    break;
                case 2://收藏失败
                    MyToast.makeShortToast(PlayVRVedioActivity.this,"添加收藏失败");
                    break;
                case 3://收藏成功
//                    MyToast.makeShortToast(PlayVedioActivity.this,"已收藏");
                    type = 1;
                    ivLike.setImageResource(R.drawable.like_red);
                    break;
                case 4://评论成功
                    MyToast.makeShortToast(PlayVRVedioActivity.this,"评论成功");
                    mCurrentCounter = 0;
//                    mRecyclerView.refresh();
                    lists.clear();
                    requestData();
                    break;
                case 5://评论失败
                    MyToast.makeShortToast(PlayVRVedioActivity.this,"评论失败");
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
        HttpUtils.doGetAsyn(PlayVRVedioActivity.this,false,url, mHandler, new HttpUtils.CallBack() {
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
        HttpUtils.doGetAsyn(PlayVRVedioActivity.this, false, click_number_url, mHandler, new HttpUtils.CallBack() {
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
