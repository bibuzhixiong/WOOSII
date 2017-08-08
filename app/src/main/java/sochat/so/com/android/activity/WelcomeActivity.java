package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.CourseModel;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * 欢迎界面
 * @author Administrator
 *
 */
@SuppressWarnings("unused")
public class WelcomeActivity extends BaseActivity implements AnimationListener,OnClickListener{

    private View view,view1;
    //logo
    private ImageView logo;
    private ImageView welecom_beijing;
    //点击按钮
    private Button button;
    //动画
    private AlphaAnimation startAnima;
    // 对应的viewPager
    private ViewPager viewPager;
    // view数组
    private List<View> viewList = new ArrayList<View>();
    //adapter
    private WelcomePageAdapter welcomeAdapter;
    //Animation
    private Animation openAnimation;



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:

                    break;
                case 10:
                    if (courseList ==null){
                        courseList.add("语文");
                        courseList.add("数学");
                        courseList.add("英语");
                        courseList.add("美术");
                        courseList.add("语文");
                    }
                    DemoHelper.setCourseTitle(courseList.toString());
                    redirectTo();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_welcome, null);
        setContentView(view);
        // 初始化控件
        initControl();
        // 初始化视图
        initView();
        // 设置监听
        setListener();
    }

    /**
     * 初始化控件
     */
    private void initControl() {
        welecom_beijing=  (ImageView) findViewById(R.id.welecom_beijing);
        logo = (ImageView) findViewById(R.id.logo);
        startAnima = new AlphaAnimation(0.3f, 1.0f);
        viewPager = (ViewPager) findViewById(R.id.W_view_pager);
        openAnimation=AnimationUtils.loadAnimation(this, R.anim.sacle);//R.anim.sacle
    }

    /**
     * 设置监听
     */
    private void setListener() {
        startAnima.setAnimationListener(this);
        openAnimation.setAnimationListener(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        if (DemoHelper.getFristStart() == 0) {
            logo.setVisibility(View.VISIBLE);
            //welecom_beijing.startAnimation(openAnimation);
            startAnima.setDuration(2000);
            welecom_beijing.startAnimation(startAnima);
        } else {
            logo.setVisibility(View.VISIBLE);
            startAnima.setDuration(2000);
            welecom_beijing.startAnimation(startAnima);
            //welecom_beijing.startAnimation(openAnimation);
        }

    }
    /**
     * 跳转界面
     */
    private void redirectTo() {
//        DemoHelper.setFristStart(1);
//        if(DemoHelper.getUid().equals("")|| DemoHelper.getFristInstall() ==false){
//            DemoHelper.setFristInstall(true);
//            Intent intent=new Intent(this, LoginActivity.class);
//            CommonUtils.startActivity(this, intent);
//        }else{
//            Intent intent=new Intent(this, MyMainActivity.class);
//            CommonUtils.startActivity(this, intent);
//        }

        Intent intent=new Intent(this, MyMainActivity.class);
        CommonUtils.startActivity(this, intent);
        finish();
    }

    public void onAnimationEnd(Animation arg0) {
        getSchoolTitle();
    }

    public void onAnimationRepeat(Animation arg0) {
    }

    public void onAnimationStart(Animation arg0) {
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.view_button:
                redirectTo();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private class WelcomePageAdapter extends PagerAdapter {

        private List<View> viewList;

        public WelcomePageAdapter(Context mContext, List<View> viewList) {
            super();
            this.viewList = viewList;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }

    }


    private ArrayList<String> courseList = new ArrayList<String>();
        private void getSchoolTitle(){
            HttpUtils.doGetAsyn(null,false, ConfigInfo.SCHOOL_COURSE_URL, handler, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    try {
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
                        Log.i(ConfigInfo.TAG,"Welcome:"+DemoHelper.getCourseTitle());
                        Log.i(ConfigInfo.TAG,"result:"+result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

}
