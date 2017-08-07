package sochat.so.com.android.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.fragment.PoetryFragment;

/**
 * Created by Administrator on 2017/7/12.
 */

public class PanoramaWebViewActivity extends BaseActivity implements View.OnClickListener{
    private WebView webView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private  String webview_url;
    private  String author;
    private  String detailed;
    private  String poetry;



    private TextView tvCircleOne;
    private TextView tvCircleTwo;
    private TextView tvCircleThree;
    private TextView tvTitle;
    private ImageView ivCancel;
    private ImageView ivDetail;
    private ImageView ivBack;
    private RelativeLayout rlLayout;
    private ViewPager mViewPager;


    private ViewPagerAdapter adapter;
    private List<Fragment>fragments;


    private boolean clickormove = true;//点击或拖动，点击为true，拖动为false
    private int downX, downY;//按下时的X，Y坐标
    private boolean hasMeasured = false;//ViewTree是否已被测量过，是为true，否为false
    private View content;//界面的ViewTree
    private int screenWidth,screenHeight;//ViewTree的宽和高


    private int height ;
    private int statusBarHeight ;
    private ValueAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置窗口风格为进度条
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_panorama_webview);
        webview_url = getIntent().getStringExtra("panorama_url");
        poetry = getIntent().getStringExtra("panorama_poetry");
        author = getIntent().getStringExtra("panorama_author");
        detailed = getIntent().getStringExtra("panorama_detailed");

        //获取屏幕相关信息
        WindowManager wm = this.getWindowManager();
        height = wm.getDefaultDisplay().getHeight();
        statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }


        //可拖动的图片
        dragview();

        findById();
        inits();

    }

    private void findById() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        webView = (WebView) findViewById(R.id.web0);

        rlLayout = (RelativeLayout) findViewById(R.id.rl_Layout);

        tvCircleOne = (TextView) findViewById(R.id.circle_one);
        tvCircleTwo = (TextView) findViewById(R.id.circle_two);
        tvCircleThree = (TextView) findViewById(R.id.circle_three);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivCancel = (ImageView) findViewById(R.id.tv_cancel);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        ivBack.setOnClickListener(this);
        ivCancel.setOnClickListener(this);

    }

    private void inits() {
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);          //支持缩放
        settings.setBuiltInZoomControls(true);  //启用内置缩放装置
        settings.setJavaScriptEnabled(true);    //启用JS脚本
        settings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            //当点击链接时,希望覆盖而不是打开新窗口
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);  //加载新的url
                return true;    //返回true,代表事件已处理,事件流到此终止
            }
        });

        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        webView.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            //当WebView进度改变时更新窗口进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //Activity的进度范围在0到10000之间,所以这里要乘以100
                PanoramaWebViewActivity.this.setProgress(newProgress * 100);
            }
        });


        webView.loadUrl(webview_url); //加载"http://720yun.com/t/wlz2a8d2lwuny73ntq"
        webView.requestFocus(); //获取焦点


        fragments = new ArrayList<>();
        for (int i=0;i<3;i++){
            if (i==0){
                fragments.add(PoetryFragment.newInstance(poetry,1));
            }else if (i==1){
                fragments.add(PoetryFragment.newInstance(author,2));
            }else if (i==2){
                fragments.add(PoetryFragment.newInstance(detailed,3));
            }
        }

        adapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(adapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {



                switch (position){
                    case 0:
                        tvTitle.setText("诗词正文");
                        tvCircleOne.setBackgroundResource(R.drawable.small_circle_write);
                        tvCircleTwo.setBackgroundResource(R.drawable.small_circle_gery_write);
                        tvCircleThree.setBackgroundResource(R.drawable.small_circle_gery_write);
                        break;
                    case 1:
                        tvTitle.setText("作者介绍");
                        tvCircleOne.setBackgroundResource(R.drawable.small_circle_gery_write);
                        tvCircleTwo.setBackgroundResource(R.drawable.small_circle_write);
                        tvCircleThree.setBackgroundResource(R.drawable.small_circle_gery_write);
                        break;
                    case 2:
                        tvTitle.setText("诗词解读");
                        tvCircleOne.setBackgroundResource(R.drawable.small_circle_gery_write);
                        tvCircleTwo.setBackgroundResource(R.drawable.small_circle_gery_write);
                        tvCircleThree.setBackgroundResource(R.drawable.small_circle_write);
                        break;

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }



    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                PanoramaWebViewActivity.this.finish();
            }
        },2000);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_back:
                webView.loadUrl("about:blank");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PanoramaWebViewActivity.this.finish();
                    }
                },1000);
                break;
            case R.id.tv_cancel:
                animator = ValueAnimator.ofFloat( height - rlLayout.getHeight(), height);
                animator.setTarget(rlLayout);
                animator.setDuration(500).start();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        rlLayout.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });

                break;
        }
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter{
        private List<Fragment>fragments;
        private FragmentManager fm;

        public ViewPagerAdapter(FragmentManager fm,List<Fragment>fragments)
        {
            super(fm);
            this.fragments =fragments;
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }


    private void dragview() {
        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View

        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        ViewTreeObserver vto = content.getViewTreeObserver();//获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(!hasMeasured)
                {
                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。
                }
                return true;//如果返回false，界面将为空。
            }
        });
        ivDetail = (ImageView) findViewById(R.id.iv_detail);
        ivDetail.setOnTouchListener(new View.OnTouchListener() {//设置按钮被触摸的时间
            int lastX, lastY; // 记录移动的最后的位置
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;

                    case MotionEvent.ACTION_MOVE: // 拖动事件
                        int dx = (int) event.getRawX() - lastX;//位移量X
                        int dy = (int) event.getRawY() - lastY;//位移量Y
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {

                            left = 0;
                            right = left + v.getWidth();

                        }
                        if (right > screenWidth) {

                            right = screenWidth;
                            left = right - v.getWidth();

                        }
                        if (top < 0) {

                            top = 0;
                            bottom = top + v.getHeight();

                        }
                        if (bottom > screenHeight) {

                            bottom = screenHeight;
                            top = bottom - v.getHeight();

                        }
                        v.layout(left, top, right, bottom);//按钮重画
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP: // 弹起事件
                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5)

                            clickormove = false;

                        else

                            clickormove = true;

                        break;

                }
                return false;

            }

        });
        ivDetail.setOnClickListener(new View.OnClickListener() {//设置按钮被点击的监听器

            @Override
            public void onClick(View arg0) {
                ValueAnimator animator;
                if (clickormove)
                    rlLayout.setVisibility(View.VISIBLE);
                animator = ValueAnimator.ofFloat( height,height - rlLayout.getHeight()- statusBarHeight);
                animator.setTarget(rlLayout);
                animator.setDuration(500).start();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        rlLayout.setTranslationY((Float) animation.getAnimatedValue());
                    }
                });
            }
        });
    }


}
