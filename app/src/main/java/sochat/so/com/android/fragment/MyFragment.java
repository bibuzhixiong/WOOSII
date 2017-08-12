package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.LLPayWalletActivity;
import sochat.so.com.android.activity.LoginActivity;
import sochat.so.com.android.activity.MyAttentionActivity;
import sochat.so.com.android.activity.MyCollectActivity;
import sochat.so.com.android.activity.MyManageActivity;
import sochat.so.com.android.activity.RechargeTimeActivity;
import sochat.so.com.android.activity.SettingActivity;
import sochat.so.com.android.activity.UserSelfInfoActivity;
import sochat.so.com.android.activity.VRLoginCodeActivity;
import sochat.so.com.android.activity.VRRentActivity;
import sochat.so.com.android.activity.WoosiiEmployeeFoundTeacherActivity;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.CircleImageView;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/2/20.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener{
    private View view;
    private TextView tvUserName;      //用户名
    private CircleImageView ivUserPhoto;   //头像
    private RelativeLayout rlOne;
    private RelativeLayout rlTwo;
    private RelativeLayout rlThree;
    private RelativeLayout rlFour;
    private RelativeLayout rlFive;
    private RelativeLayout rlSix;
    private RelativeLayout rlSeven;
    private RelativeLayout rlIncome;
    private RelativeLayout rlVrLogin;
    private TextView tvLoginOrRegister;
    private TextView tvIntroduceSelf;

    private TextView tvTopTitle;
    private ImageView ivSearch;
    private RelativeLayout topRelativeLayout;
    private TextView tvLine;
    private TextView tvResidueTime;
    private TextView tvMyManager;

    private String name = "";
    private String thumb = "";
    private String detail = "";
    private String longtime = "";

    //访问的次数
    private int count = 0 ;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:


                    Log.i(ConfigInfo.TAG,"thumb:"+thumb);
                    //头像
                    if (!TextUtils.isEmpty(thumb)&&thumb.length()>5){
                        Log.i(ConfigInfo.TAG,"thumb1:"+thumb);
                        Picasso.with(mActivity).load(ConfigInfo.ApiUrl+thumb).placeholder(R.drawable.zhanweifu).memoryPolicy(MemoryPolicy.NO_STORE).error(R.drawable.morentouxiang).into(ivUserPhoto);
                    }else{
                        if (!TextUtils.isEmpty(DemoHelper.getThumb())&&thumb.length()>5){
                            Log.i(ConfigInfo.TAG,"thumb2:"+thumb);
                            Picasso.with(mActivity).load(ConfigInfo.ApiUrl+DemoHelper.getThumb()).placeholder(R.drawable.zhanweifu).memoryPolicy(MemoryPolicy.NO_STORE).error(R.drawable.morentouxiang).into(ivUserPhoto);
                        }else{
                            if (!TextUtils.isEmpty(DemoHelper.getWXThumbUrl())&&thumb.length()>5){
                                Log.i(ConfigInfo.TAG,"thumb3:"+thumb);
                                Picasso.with(mActivity).load(DemoHelper.getWXThumbUrl()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivUserPhoto);
                            }else{
                                Log.i(ConfigInfo.TAG,"thumb4:"+thumb);
                                ivUserPhoto.setImageResource(R.drawable.morentouxiang);
                            }
                        }
                    }
                    //名字
                    if (!TextUtils.isEmpty(name)){
                        tvUserName.setText(name);
                    }else{
                        if (!TextUtils.isEmpty(DemoHelper.getNickName())){
                            tvUserName.setText(DemoHelper.getNickName());
                        }else{
                            if (!TextUtils.isEmpty(DemoHelper.getWXNickName())){
                                tvUserName.setText(DemoHelper.getWXNickName());
                            }else{
                                tvUserName.setText("来历不明的小沃");
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(detail)){
                        tvIntroduceSelf.setText(detail);
                    }else{
                        if (!TextUtils.isEmpty(DemoHelper.getDetail())){
                            tvIntroduceSelf.setText(DemoHelper.getDetail());
                        }else{
                            tvIntroduceSelf.setText("这个人很懒什么都没留下");
                        }
                    }

                    if (!TextUtils.isEmpty(longtime)){
                        tvResidueTime.setText(longtime+" 沃币");
                    }

                    Log.i(ConfigInfo.TAG,"真正的User_id:"+DemoHelper.getUid());

                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my,container, false);
        findById();
        inits();
        setListeners();
        return view;
    }

    private void setListeners() {
        rlOne.setOnClickListener(this);
        rlTwo.setOnClickListener(this);
        rlThree.setOnClickListener(this);
        rlFour.setOnClickListener(this);
        rlFive.setOnClickListener(this);
        rlSix.setOnClickListener(this);
        rlSeven.setOnClickListener(this);
        ivUserPhoto.setOnClickListener(this);
        tvLoginOrRegister.setOnClickListener(this);
        rlIncome.setOnClickListener(this);
        rlVrLogin.setOnClickListener(this);

    }

    @Override
    public String getFragmentName() {
        return null;
    }
    public static MyFragment newInstance(){
        MyFragment fragment = new MyFragment();
        return fragment;
    }

    private void findById() {
        rlVrLogin = (RelativeLayout) view.findViewById(R.id.ll_vr_login);
        topRelativeLayout = (RelativeLayout) view.findViewById(R.id.main_titlelayout);
        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_text);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        tvTopTitle.setText("我的");
        tvTopTitle.setTextColor(Color.WHITE);

        ivSearch.setVisibility(View.GONE);
        tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        ivUserPhoto = (CircleImageView) view.findViewById(R.id.iv_user_photo);
        rlOne = (RelativeLayout) view.findViewById(R.id.ll_my_attention);
        rlTwo = (RelativeLayout) view.findViewById(R.id.ll_collect);
        rlThree = (RelativeLayout) view.findViewById(R.id.ll_residue);
        rlFour = (RelativeLayout) view.findViewById(R.id.ll_manage);
        rlFive = (RelativeLayout) view.findViewById(R.id.ll_setting);
        rlSix = (RelativeLayout) view.findViewById(R.id.ll_recharge_center);
        rlSeven = (RelativeLayout) view.findViewById(R.id.ll_vr_machine_rent);
        rlIncome = (RelativeLayout) view.findViewById(R.id.ll_income);
        tvLoginOrRegister = (TextView) view.findViewById(R.id.login_or_register);
        tvIntroduceSelf = (TextView) view.findViewById(R.id.tv_introduce_self);
        tvLine = (TextView) view.findViewById(R.id.line);
        tvResidueTime = (TextView) view.findViewById(R.id.tv_my_residue_time);
        tvMyManager = (TextView) view.findViewById(R.id.tv_my_manage);

    }

    private void inits() {
        topRelativeLayout.setBackgroundColor(0xFF15B422);
        tvLine.setVisibility(View.GONE);
        initMyInfo();
    }


    @Override
    public void onClick(View v) {
        Intent intent ;
        switch (v.getId()){
            case R.id.ll_my_attention:
                intent = new Intent(mActivity, MyAttentionActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_collect:
                intent = new Intent(mActivity, MyCollectActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_recharge_center:
                intent = new Intent(mActivity, RechargeTimeActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_vr_machine_rent:
                intent = new Intent(mActivity, VRRentActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_residue:
                break;
            case R.id.ll_manage:
                if (DemoHelper.getUserType().equals("3")){
                    intent = new Intent(mActivity, MyManageActivity.class);
                    CommonUtils.startActivity(mActivity,intent);
                }else if (DemoHelper.getUserType().equals("2")){
                    intent = new Intent(mActivity, WoosiiEmployeeFoundTeacherActivity.class);
                    intent.putExtra("employee_user_id", DemoHelper.getUid());
                    CommonUtils.startActivity(mActivity,intent);
                }
                break;
            case R.id.ll_setting:
                intent = new Intent(mActivity, SettingActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.iv_user_photo:
                intent = new Intent(mActivity, UserSelfInfoActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.login_or_register:
                intent = new Intent(mActivity, LoginActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_income:
                intent = new Intent(mActivity, LLPayWalletActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
            case R.id.ll_vr_login:
                intent = new Intent(mActivity, VRLoginCodeActivity.class);
                CommonUtils.startActivity(mActivity,intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserBaseInfo();
        initMyInfo();
    }

    private void initMyInfo(){
        if (!TextUtils.isEmpty(DemoHelper.getUid())){
            intiUserInfo();
        }

    }

    private void intiUserInfo() {
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/user_info?phone="+DemoHelper.getPhone();
        Log.i(ConfigInfo.TAG,"intiUserInfo_url："+url);
        HttpUtils.doGetAsyn(mActivity,false,url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"intiUserInfo_result："+result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!TextUtils.isEmpty(result)){
                         name = jsonObject.getString("name");
                         detail = jsonObject.getString("detail");
                         thumb = jsonObject.getString("thumb");
                         longtime = jsonObject.getString("longtime");
//                        DemoHelper.setUid(jsonObject.getString("user_id"));


                        DemoHelper.setSchool_code(jsonObject.getInt("school_code"));


                        int honor = Integer.parseInt(jsonObject.getString("honor"));
                        //判断是否是校长
                        if (honor ==0){
                            //不是校长就先成为校长
                            becomeHeadmaster();
                        }else{
                            //如果是校长了再判断是否有校区，如果没校区那就再创建校区
                            if (jsonObject.getInt("school_code")==0){
                                createCampus();
                            }else{
                                DemoHelper.setSchool_id(jsonObject.getString("school_id"));
                            }
                        }
//                        DemoHelper.setHonor(jsonObject.getString("honor"));
                        count++;
                        handler.sendEmptyMessage(0);
                    }
                    Log.i(ConfigInfo.TAG,"result:"+result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }


    private void setUserBaseInfo(){

        if (!TextUtils.isEmpty(DemoHelper.getUid())){
            tvLoginOrRegister.setVisibility(View.GONE);
            tvIntroduceSelf.setVisibility(View.VISIBLE);
            if (DemoHelper.getUserType().equals("3")){
                rlFour.setVisibility(View.VISIBLE);
                tvMyManager.setText("小沃中心管理功能");

            }else if (DemoHelper.getUserType().equals("2")){
                rlFour.setVisibility(View.VISIBLE);
                tvMyManager.setText("小沃员工管理功能");
            }
        }else{
            ivUserPhoto.setImageResource(R.drawable.morentouxiang);
            tvUserName.setText("来历不明的小沃");
            tvLoginOrRegister.setVisibility(View.VISIBLE);
            tvIntroduceSelf.setVisibility(View.GONE);
                rlFour.setVisibility(View.GONE);
        }
    }


    //创建校区
    private void createCampus(){
    String url = ConfigInfo.ApiUrl+"/index.php/Api/SchoolManger/Hx_create_sch?act=add&sch_name=&sch_info=&area"+DemoHelper.getArea()+"&user_id="+DemoHelper.getUid();
        HttpUtils.doGetAsyn(mActivity, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("Code");
                    if (code ==1){
                    if (count<8){
                        intiUserInfo();
                    }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //成为校长
    private void becomeHeadmaster(){
        String url = ConfigInfo.ApiUrl+"/index.php/Api/User/be_schoolmaster?user_id="+DemoHelper.getUid();
        HttpUtils.doGetAsyn(mActivity, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("Code");
                    if (code ==1){
                        createCampus();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
