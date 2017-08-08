package sochat.so.com.android.customview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.utils.CommonUtils;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Administrator on 2017/3/4.
 */

public class AnswerRecordImageView extends Button {
    private static final int MIN_INTERVAL_TIME = 700; // 录音最短时间
    private static final int MAX_INTERVAL_TIME = 60000; // 录音最长时间
    private long mStartTime;
    private TimeThread mThread;
    private Activity mActivity;
    private Toast toast;
    private AnswerDialog answerDialog;
    /**
     * 手机震动的控件
     */
    private Vibrator vibrator;

    public AnswerRecordImageView(Context context) {
        super(context,null);
    }

    public AnswerRecordImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerRecordImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setActivity(Activity mActivity){
        this.mActivity = mActivity;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                noScrollViewPager.noScroll();
                initlization();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getY() < -50) {
                    cancel();
                } else {
                    finish();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //做一些UI提示
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                    }
                },1000);
                break;
        }
        return true;
    }

    private void initlization() {
        vibrator = (Vibrator) mActivity.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(30);
        //点击时就开始去改变界面
        onSpeakState.speak();

        Log.i(ConfigInfo.TAG,this.toString());
        mStartTime = System.currentTimeMillis();
        mThread = new TimeThread();

    }

    private void cancel() {
        mThread.exit();
        handler.sendEmptyMessage(2);//手指上滑，取消说话

    }

    private void finish() {
        long intervalTime = System.currentTimeMillis() - mStartTime;
        if (intervalTime <MIN_INTERVAL_TIME){//说话时间太短了，取消说话
            handler.sendEmptyMessage(0);
            return ;
        }
        onListenedState.listened();
    }

    private class TimeThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }
        @Override
        public void run() {
            while (running) {
                try {
//                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (System.currentTimeMillis() - mStartTime >= MAX_INTERVAL_TIME) {
                    // 如果超过最长录音时间
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }


    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    onNormalState.normal();//时间太短，取消问答
                    CommonUtils.showToast(mActivity,"说话时间太短，请再说一遍",toast);

//                    Toast.makeText(mActivity, "说话时间太短，请再说一遍", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    onNormalState.normal();//时间太长，取消问答
                    CommonUtils.showToast(mActivity,"说话时间太长了",toast);
//                    Toast.makeText(mActivity, "说话时间太长了", Toast.LENGTH_SHORT).show();
                    break;
                case 2://手指上滑，取消说话
                    onNormalState.normal();
                    CommonUtils.showToast(mActivity,"取消问答",toast);
//                    Toast.makeText(mActivity, "取消问答", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    /**
     * 下面是三个回调的方法，分别是：正常默认状态的回调、正在说话的回调、说话完成的回调
     */
    private  setOnNormalState onNormalState;
    public interface  setOnNormalState{
        public  void normal();
    }
    public void setNormalStateListener(setOnNormalState onNormalState){
        this.onNormalState = onNormalState;
    }

    private  setOnSpeakState onSpeakState;
    public interface  setOnSpeakState{
        public  void speak();
    }
    public void setSpeakStateListener(setOnSpeakState onSpeakState){
        this.onSpeakState = onSpeakState;
    }

    private  setOnListenedState onListenedState;
    public interface  setOnListenedState{
        public  void listened();
    }
    public void setListenedStateListener(setOnListenedState onListenedState){
        this.onListenedState = onListenedState;
    }

    /**
     * 按下按钮时就让ViewPager不能滑动
     */
    private setNoScrollViewPager noScrollViewPager;
    public interface setNoScrollViewPager{
        public void noScroll();
    }
    public void setNoScrollListener(setNoScrollViewPager noScrollViewPager){
        this.noScrollViewPager = noScrollViewPager;
    }


}
