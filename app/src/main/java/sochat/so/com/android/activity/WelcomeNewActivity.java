package sochat.so.com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/4/25.
 */

public class WelcomeNewActivity extends BaseActivity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    break;
                case 10:
//                    DemoHelper.setCourseTitle(courseList.toString());
                    break;
            }

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootview = View.inflate(this, R.layout.activity_welcome_new, null);
        Log.i(ConfigInfo.TAG,"System.currentTimeMillis():"+System.currentTimeMillis());
        setContentView(rootview);
        getSchoolTitle();
        //初始化渐变动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_alpha);
        //设置动画监听器
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当监听到动画结束时，开始跳转到MainActivity中去
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        redirectTo();
                    }
                });
            }
        });

        //开始播放动画
        rootview.startAnimation(animation);
    }


    /**
     * 跳转界面
     */
    private void redirectTo() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(WelcomeNewActivity.this, MyMainActivity.class);
                CommonUtils.startActivity(WelcomeNewActivity.this, intent);
                finish();
            }
        },1000);

    }

    private ArrayList<String> courseList = new ArrayList<String>();
    private void getSchoolTitle(){
        HttpUtils.doGetAsyn(null,false, ConfigInfo.SCHOOL_COURSE_URL, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    if (!TextUtils.isEmpty(result)&&result.length()>4){
                        DemoHelper.setCourseTitle(result);
                        JSONArray array = new JSONArray(result);
                        JSONArray array1 = new JSONArray();
                        if (TextUtils.isEmpty(DemoHelper.getCourseTitleShow())){
                            if (array.length()>8){
                                for (int i=0;i<8;i++){
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    array1.put(jsonObject);
                                }
                            }else{
                                for (int i=0;i<array.length();i++){
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    array1.put(jsonObject);
                                }
                            }
                            DemoHelper.setCourseTitleShow(array1.toString());

                        }

                    }
                    Log.i(ConfigInfo.TAG,"Welcome:"+DemoHelper.getCourseTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(10);
                }
            }
        });
    }
}
