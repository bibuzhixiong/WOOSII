package sochat.so.com.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import sochat.so.com.android.R;
import sochat.so.com.android.utils.MyToast;


/**
 * U3d播放界面
 * Created by Administrator on 2017/1/6.
 */

public class U3DActivity extends UnityPlayerActivity {

    private static MyMainActivity mymainActivity;
    private static U3DActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u3d);
        /**
//        setContentView( mUnityPlayer.getView() );
        //获取显示Unity视图的父控件
        LinearLayout mParent=(LinearLayout)findViewById(R.id.UnityView);
        //获取Unity视图
        View mView=mUnityPlayer.getView();
        //将Unity视图添加到Android视图中
        mParent.addView(mView);
        UnityPlayer.UnitySendMessage("World", "ReceiveMessage", "ws");

        mymainActivity =  new MyMainActivity();
        _this = U3DActivity.this;

        */
        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);


    }

    public static U3DActivity getInstance(){
        return _this;
    }

    public static Intent _2dActicity = null;

    @Override

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

    }

    public void StartActivity(String name)

    {

        _2dActicity = new Intent(this, MyMainActivity.class);

        _2dActicity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(_2dActicity);

    }

    public static void GoToMainActivity(Activity activity)
    {
        Log.i("Gale log","GoToMainActivity,name=");
        Intent intent = new Intent(activity, MyMainActivity.class);
        activity.startActivity(intent);

    }

//    /**
//     * 按键点击事件
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            onDestroy();
//        }
//        return true;
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        UnityPlayer.UnitySendMessage("Manager", "Unload", "");
//        mUnityPlayer.quit();
//    }

    @Override
    public void finish() {
        super.finish();
        if(mUnityPlayer!=null){
            mUnityPlayer.quit();
        }
    }

    // 定义一个long型变量，用于判断两次点击的间隔
    private long exitTime;
    // 实现返回键的点击事件
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exit(); // 在这里进行点击判断
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {

            if (System.currentTimeMillis()-exitTime >2000){
                MyToast.makeShortToast(U3DActivity.this,"再按一次退出VR");
                exitTime = System.currentTimeMillis();
            }else{
                // 连续点击量两次，进行应用退出的处理
                this.finish();
            }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }
}
