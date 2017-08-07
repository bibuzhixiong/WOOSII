package sochat.so.com.android.live.base;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import sochat.so.com.android.R;
import sochat.so.com.android.live.DemoCache;


/**
 * Created by zhukkun on 1/5/17.
 */
public abstract class LiveBaseActivity extends AppCompatActivity {

    private static Handler handler;

    public String TAG =  this.getClass().getSimpleName();

    protected abstract void handleIntent(Intent intent);
    protected abstract int getContentView();
    protected abstract void initView();


    private int myCurTheme;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //沉浸式状态栏的处理方法1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            myCurTheme = R.style.StatusBarTheme;
        }else{
            myCurTheme = R.style.StatusBarTheme1;
        }
        setTheme(myCurTheme);
        //沉浸式状态栏的处理方法2(这种方法只能修改状态栏的背景色，不是真正的沉浸式)
//        StatusBarCompat.compat(this, getResources().getColor(R.color.transparent));


        handleIntent(getIntent());
        setContentView(getContentView());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoCache.setVisibleActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DemoCache.setVisibleActivity(null);
    }

    protected final Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getMainLooper());
        }
        return handler;
    }

    protected <T extends View> T findView(int id){
        return (T)findViewById(id);
    }

    private Toast mToast;
    public void showToast(final String text){
        if(mToast == null){
            mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        }
        if(Thread.currentThread() != Looper.getMainLooper().getThread()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToast.setText(text);
                    mToast.show();
                }
            });
        }else {
            mToast.setText(text);
            mToast.show();
        }
    }

    public void showConfirmDialog(String title, String message, DialogInterface.OnClickListener okEvent, DialogInterface.OnClickListener cancelEvent){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定",
                okEvent);
        builder.setNegativeButton("取消",
                cancelEvent);
        builder.show();
    }
}
