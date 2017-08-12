package sochat.so.com.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.MD5;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.NoScrollViewPager;
import sochat.so.com.android.customview.PopupMenuUtil;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.UpdateJPush;
import sochat.so.com.android.fragment.AnswerFragment;
import sochat.so.com.android.fragment.HomepageFragment;
import sochat.so.com.android.fragment.MyFragment;
import sochat.so.com.android.fragment.SchoolFragment;
import sochat.so.com.android.jpush.ExampleUtil;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.im.Preferences;
import sochat.so.com.android.live.im.UserPreferences;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.version.UpdateManager;

/**
 * Created by Administrator on 2017/2/18.
 */

public class MyMainActivity extends BaseActivity implements View.OnClickListener{
    /**
     * 软件更新
     */
    private UpdateManager manager;

    //这里是底部的绿色图标
    private ImageView ivVRCenter;
    private NoScrollViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private RadioButton rbAnswer;
    private RadioButton rbVr;
    private RadioButton rbSchool;
    private RadioButton rbMarkering;
    private RadioButton rbMy;
    private List<Fragment>fragments ;
    private ViewPagerAdapter viewPagerAdapter;
    private HomepageFragment marketingFragment;
    private SchoolFragment schoolFragment;
    //    private VrFragment vrFragment;
    private AnswerFragment answerFragment;
    private MyFragment myFragment;

    private ProgressDialog progressDialog = new ProgressDialog();

    private MyToast toast;

    //直播聊天室相关
    private AbortableFuture<LoginInfo> loginRequest;
    //用于判断是否已经登录网易云信
    public static boolean hadLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_main);
        EventBus.getDefault().register(this);
        findById();
        inits();
        setAdapter();
        setListeners();


    }


    @Override
    protected void onResume() {
        super.onResume();
        // 检查软件更新
        if (manager != null) {
            manager.checkUpdate(progressDialog.progressDialog);
        }

        //直播聊天室相关
        if (!TextUtils.isEmpty(DemoHelper.getUid())){
            Log.i(ConfigInfo.TAG,"这里不为空");
            if (!hadLogin){
                loginChatRoom();
            }
        }

    }

    private void findById() {
        ivVRCenter = (ImageView) findViewById(R.id.iv_vr_center);
        mViewPager = (NoScrollViewPager) findViewById(R.id.main_viewpager);
        mRadioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
        rbAnswer = (RadioButton) findViewById(R.id.rb_tab_answer);
        rbVr = (RadioButton) findViewById(R.id.rb_tab_vr);
        rbSchool = (RadioButton) findViewById(R.id.rb_tab_school);
        rbMarkering = (RadioButton) findViewById(R.id.rb_tab_marketing);
        rbMy = (RadioButton) findViewById(R.id.rb_tab_my);
    }

    private void inits() {
        // 检查是否有新版本，一定要是异步的
        manager = new UpdateManager(this, false);

        marketingFragment = HomepageFragment.newInstance();
        schoolFragment = SchoolFragment.newInstance();
//        vrFragment = VrFragment.newInstance();
        answerFragment = AnswerFragment.newInstance();
        myFragment = MyFragment.newInstance();

        fragments = new ArrayList<Fragment>();
        fragments.add(marketingFragment);
        fragments.add(schoolFragment);
//        fragments.add(vrFragment);
        fragments.add(answerFragment);
        fragments.add(myFragment);

        freshAnswerFragmentView = answerFragment;

    }

    private void setAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);
    }

    private void setListeners() {
        //ViewPager改变时的监听器
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        rbMarkering.setChecked(true);
                        break;
                    case 1:
                        rbSchool.setChecked(true);
                        break;
                    case 2:
                        rbAnswer.setChecked(true);
                        freshAnswerFragmentView.freshView();
                        break;
//                    case 2:
//                        rbAnswer.setChecked(true);
//                        freshAnswerFragmentView.freshView();
//                        break;
                    case 3:
                        rbMy.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //测试提交
        //RadioGroup里面的RadioButton被选中改变时的监听器

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_tab_marketing:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_tab_school:
                        mViewPager.setCurrentItem(1);
                        break;
//                    case R.id.rb_tab_vr:
//                        PopupMenuUtil.getInstance()._show(MyMainActivity.this, rbVr);
//                        break;
                    case R.id.rb_tab_answer:
                        mViewPager.setCurrentItem(2);
                        freshAnswerFragmentView.freshView();
                        break;
                    case R.id.rb_tab_my:
                        mViewPager.setCurrentItem(3);
                        break;
                }

            }
        });

        ivVRCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenuUtil.getInstance()._show(MyMainActivity.this, rbVr);
            }
        });
        //设置极光推送的别名

        mHandler.sendMessage(mHandler.obtainMessage(1001, DemoHelper.getUid()));

    }
    //登陆成功后设置激光推送别名和标签
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(UpdateJPush update){
        //判断uid是否为空，不为空则设置极光推送的别名
        mHandler.sendMessage(mHandler.obtainMessage(1001, DemoHelper.getUid()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
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

    /**
     * 去刷新AnswerFragment里面的布局为默认状态
     */
    private setFreshAnswerFragmentView freshAnswerFragmentView;
    public interface setFreshAnswerFragmentView{
        public void freshView();
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

        if (PopupMenuUtil.getInstance()._isShowing()) {
            PopupMenuUtil.getInstance()._rlClickAction();
        } else {
            if (System.currentTimeMillis()-exitTime >2000){
                toast.makeShortToast(MyMainActivity.this,"再按一次退出应用");
                exitTime = System.currentTimeMillis();
            }else{
                // 连续点击量两次，进行应用退出的处理
                System.exit(0);
            }
        }

    }



    /**
     * ***************************************** 登录 **************************************
     */

    private void loginChatRoom() {
        DialogMaker.showProgressDialog(this, null, getString(R.string.logining), true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);

        // 云信只提供消息通道，并不包含用户资料逻辑。开发者需要在管理后台或通过服务器接口将用户帐号和token同步到云信服务器。
        // 在这里直接使用同步到云信服务器的帐号和token登录。
        // 这里为了简便起见，demo就直接使用了密码的md5作为token。
        // 如果开发者直接使用这个demo，只更改appkey，然后就登入自己的账户体系的话，需要传入同步到云信服务器的token，而不是用户密码。
        final String account = DemoHelper.getUid();
        String string = account+"woosii";
        final String token = tokenFromPassword(string);
        // 登录
        loginRequest = NimUIKit.doLogin(new LoginInfo(account, token), new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                LogUtil.i("WS log", "login success");

                onLoginDone();

                DemoCache.setAccount(account);
                saveLoginInfo(account, token);

                // 初始化消息提醒配置
//                initNotificationConfig();
                //如果登录成功，那么标记防止每次进入都会登录
                hadLogin = true ;
            }

            @Override
            public void onFailed(int code) {
                hadLogin = false;
                LogUtil.i("WS log", "login failed:"+code);
                onLoginDone();
                if (code == 302 || code == 404) {
                    Toast.makeText(MyMainActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyMainActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                hadLogin = false;
                Toast.makeText(MyMainActivity.this, R.string.login_exception, Toast.LENGTH_LONG).show();
                onLoginDone();
            }
        });
    }

    private void initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

        // 加载状态栏配置
        StatusBarNotificationConfig statusBarNotificationConfig = UserPreferences.getStatusConfig();
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig();
            UserPreferences.setStatusConfig(statusBarNotificationConfig);
        }
        // 更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig);
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    //DEMO中使用 username 作为 NIM 的account ，md5(password) 作为 token
    //开发者需要根据自己的实际情况配置自身用户系统和 NIM 用户系统的关系
    private String tokenFromPassword(String password) {
        String appKey = readAppKey(this);
        boolean isDemo = ConfigInfo.LiveAppKey.equals(appKey)
                || ConfigInfo.LiveAppKey.equals(appKey);

        return isDemo ? MD5.getStringMD5(password) : password;
    }

    private static String readAppKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //JPush设置别名
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1001:
                    Set<String> s=new HashSet<>();

//                    Logger.d(TAG, "Set alias in handler.");
                    Log.e("HHH",DemoHelper.getUid().equals("")+"---"+DemoHelper.getUid());
                    if(DemoHelper.getUid().equals("")){
                        JPushInterface.setAliasAndTags(getApplicationContext(), "", s, mAliasCallback);
                    }else{
                        s.add(DemoHelper.getUid());
                        JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, s, mAliasCallback);
                    }

                    break;
            }
        }
    };
    //JPush设置别名
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.e("Jpush",logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e("Jpush",logs);
                    //失败的话，隔60秒再设置别名
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(1001, alias), 1000 * 60);
                    } else {
                        Log.e("Jpush",logs);
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
//                    Log.e("Jpush",logs);
            }

        }

    };

}
