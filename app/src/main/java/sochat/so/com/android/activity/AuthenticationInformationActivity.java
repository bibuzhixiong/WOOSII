package sochat.so.com.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.interface_method.ILLPaySmsCodeView;
import sochat.so.com.android.interface_method.JsonCallBack;
import sochat.so.com.android.interface_method.LLPresenterCallBack;
import sochat.so.com.android.interface_method_realize.LLPaySmsCodePresenter;
import sochat.so.com.android.llpay_bean.CityBean;
import sochat.so.com.android.llpay_bean.GetJsonDataUtil;
import sochat.so.com.android.llpay_bean.ProvinceBean;
import sochat.so.com.android.llpay_bean.ProvinceResult;
import sochat.so.com.android.llpay_util.MyHashMap;
import sochat.so.com.android.llpay_util.RSAUtil;
import sochat.so.com.android.net.MyNetWorkUtil;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/5/16.
 */

public class AuthenticationInformationActivity extends BaseActivity implements ILLPaySmsCodeView{
    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopText;

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_id)
    EditText etID;
    @Bind(R.id.et_pay_password)
    EditText etPayPassword;
    @Bind(R.id.et_ensure_pay_password)
    EditText etEnsurePayPassword;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.et_captcha)
    EditText etCaptcha;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.tv_id_time)
    TextView tvIDTime;
    @Bind(R.id.tv_captcha)
    TextView tvCaptcha;
    @Bind(R.id.tv_commit)
    TextView tvCommit;

//    @Bind(R.id.iv_front)
//    ImageView ivFront;
//    @Bind(R.id.iv_back)
//    ImageView ivBack;

    @Bind(R.id.sp_professional)
    Spinner spProfessional;

    private Thread thread;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<CityBean>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    //签名的参数
    private Map<String, Object> sign_parameters = new MyHashMap<String, Object>();

    private String user_name = "";
    private String user_id = "";
    private String user_id_time = "";
    private String user_pay = "";
    private String user_ensure_pay = "";
    private String user_professional = "";
    private String province_code = "";
    private String city_code = "";
    private String area_code = "";
    private String address = "";
    private String phone = "";
    private String captcha = "";

    private String token_sms = "";
    private String token_check = "";

    /**
     * 计时器
     */
    private TimeCount timeCount;

    //记录职业是否被选择
    private boolean isProfessionalSelected = false;

    private LLPaySmsCodePresenter presenter;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOAD_DATA:
                    break;
                case MSG_LOAD_SUCCESS:
                    ShowPickerView();
                    break;
                case 3:
                    Intent intent = new Intent(AuthenticationInformationActivity.this,LLPayBindCardActivity.class);
                    CommonUtils.startActivity(AuthenticationInformationActivity.this,intent);
                    AuthenticationInformationActivity.this.finish();
                    break;
                case 4:
                    ShowPickerView();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication_information);
        ButterKnife.bind(this);
        inits();
        setListeners();

        //初始化时间选择器
        initCustomTimePicker();

    }

    private void setListeners() {
        spProfessional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

//                String[] professional = getResources().getStringArray(R.array.professional);
                if (pos<=9){
                    if (pos ==0){
                        isProfessionalSelected = false;
                    }
                    isProfessionalSelected = true;
                    user_professional = "0"+pos;
                }else{
                    isProfessionalSelected = true;
                    user_professional = pos+"";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        //为EditText绑定监听器

        textChange tc1 = new textChange();

        etName.addTextChangedListener(tc1);
        etID.addTextChangedListener(tc1);
        etAddress.addTextChangedListener(tc1);
        etEnsurePayPassword.addTextChangedListener(tc1);
        etPayPassword.addTextChangedListener(tc1);

    }

    private void inits() {
        timeCount = new TimeCount(30000, 1000);
        tvTopText.setText("认证信息");
        presenter = new LLPaySmsCodePresenter(this);
    }

    private JSONObject jsonObject1 = new JSONObject();
    @OnClick({R.id.iv_top_back, R.id.tv_commit, R.id.tv_id_time, R.id.tv_city,R.id.tv_captcha})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                AuthenticationInformationActivity.this.finish();
                break;
            case R.id.tv_captcha:
                timeCount.start();
                presenter.getPresenterMothed(this, new LLPresenterCallBack() {
                    @Override
                    public void getJson(JSONObject jsonObject) {
                        jsonObject1 =jsonObject;
                        try {
                             token_sms = jsonObject.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.tv_city:
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 写子线程中的操作,解析省市区数据
                        initJsonData();
                    }
                });
                thread.start();
                break;
            case R.id.tv_id_time:
                if (pvCustomTime != null){
                    pvCustomTime.show(); //弹出自定义时间选择器
                }
                break;
            case R.id.tv_commit:
                //&!TextUtils.isEmpty(tvCity.getText().toString().trim())&isProfessionalSelected
                if (!isProfessionalSelected){
                    Toast.makeText(this,"请选择职业",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(tvCity.getText().toString().trim())){
                    Toast.makeText(this,"请选择所在地区",Toast.LENGTH_SHORT).show();
                }else{
                    //先向服务器获得签名
                    checkphone();
                }
                break;
        }
    }
    private Map<String, Object> parameters = new MyHashMap<String, Object>();
    private void checkphone() {
        sign_parameters.clear();
        parameters.clear();
        sign_parameters.put("oid_partner", ConfigInfo.QUICK_WALLET_OID_PARTNER);
        sign_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        sign_parameters.put("user_id", DemoHelper.getUid());
        sign_parameters.put("token",token_sms);
        sign_parameters.put("verify_code", etCaptcha.getText().toString().trim());

        parameters.put("data",sign_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_SMSCHECK);

        MyNetWorkUtil.getNovate(AuthenticationInformationActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(AuthenticationInformationActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
                    Log.i(ConfigInfo.TAG,"jsonObject:"+jsonObject);
                    if (jsonObject.getString("ret_code").equals("0000")){
                        token_check = jsonObject.getString("token");
                        Log.i(ConfigInfo.TAG,"checkphone_token:"+token_check);
                        commit();
                    }else{
                        MyToast.makeShortToast(AuthenticationInformationActivity.this,jsonObject.getString("ret_msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        LLPaySign.getSign(AuthenticationInformationActivity.this, sign_parameters,new ILLPaySign() {
//            @Override
//            public void getSign(String sign) {
//                //获得签名后访问连连接口
//                Log.i(ConfigInfo.TAG,"checkphone()_sign:"+sign);
//                ll_getTkoen(sign);
//
//            }
//        });


    }

    private void commit() {
        user_name =etName.getText().toString().trim();
        address = etAddress.getText().toString().trim();
        user_id = etID.getText().toString().trim();
        user_pay = etPayPassword.getText().toString().trim();
        user_ensure_pay = etEnsurePayPassword.getText().toString().trim();

        if (!user_pay.equals(user_ensure_pay)){
            Toast.makeText(AuthenticationInformationActivity.this,"两次输入密码不同",Toast.LENGTH_SHORT).show();
            return;
        }

        //对支付密码加密
        String password = null;
        try {
            password = RSAUtil.encrypt(etPayPassword.getText().toString().trim(), ConfigInfo.RSA_PASSWORD_ENCRYPT_PUBLIC);
            Log.i(ConfigInfo.TAG,"加密:"+password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sign_parameters.clear();
        parameters.clear();
        sign_parameters.put("oid_partner",ConfigInfo.QUICK_WALLET_OID_PARTNER);
        sign_parameters.put("sign_type",ConfigInfo.SIGN_TYPE_RSA);
        sign_parameters.put("user_id", DemoHelper.getUid());
        sign_parameters.put("type_expidcard", "0");
        sign_parameters.put("name_user",user_name);
        sign_parameters.put("no_idcard",user_id);
        sign_parameters.put("exp_idcard",user_id_time);
        sign_parameters.put("pwd_pay",password);
        sign_parameters.put("addr_conn",address);
        sign_parameters.put("oid_job",user_professional);
        sign_parameters.put("addr_pro",province_code);
        sign_parameters.put("addr_city",city_code);
        sign_parameters.put("addr_dist",area_code);
        sign_parameters.put("token",token_check);

        parameters.put("risk_item", CommonUtils.getConstructRiskItem(AuthenticationInformationActivity.this));
        parameters.put("data",sign_parameters.toString());
        parameters.put("url",ConfigInfo.LL_Pay_OPERUSER);
        MyNetWorkUtil.getNovate(AuthenticationInformationActivity.this, ConfigInfo.YES_UID,headers,parameters,ConfigInfo.NO_CACHE,ConfigInfo.NO_COOKIE,ConfigInfo.ApiUrl);
        MyNetWorkUtil.getMyMothed(AuthenticationInformationActivity.this, "/index.php/Vr/Lianlianpay/pub_fun", parameters, new JsonCallBack() {
            @Override
            public void backJson(JSONObject jsonObject) {
                try {
//                    Log.i(ConfigInfo.TAG,"请求报文:"+jsonObject.getString("test"));
                    String ret_msg = jsonObject.getString("ret_msg");
                    String ret_code = jsonObject.getString("ret_code");
                    if (ret_code.equals("0000")){
                        handler.sendEmptyMessage(3);
                        MyToast.makeShortToast(AuthenticationInformationActivity.this,"正在提交");
                    }else{
                        MyToast.makeShortToast(AuthenticationInformationActivity.this,ret_msg);
                    }

                    Log.i(ConfigInfo.TAG,"my_ret_msg:"+ret_msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private Map<String, String> headers = new HashMap<>();
    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()+"; "+
                        options2Items.get(options1).get(options2).getName()+"; "+
                        options3Items.get(options1).get(options2).get(options3);

                province_code =options1Items.get(options1).getEncoding();
                        city_code=options2Items.get(options1).get(options2).getEncoding();
                                area_code=options2Items.get(options1).get(options2).getEncoding();
                tvCity.setText(tx);

            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }


    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据
        ArrayList<ProvinceBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
//        Log.i(ConfigInfo.TAG,"options1Items:"+options1Items);


        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<CityBean> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCity().size(); c++){//遍历该省份的所有城市
                CityBean CityName = jsonBean.get(i).getCity().get(c);
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCity().get(c) == null
                        ||jsonBean.get(i).getCity().get(c).getArea().size()==0) {
                    City_AreaList.add(null);
                }else {

                    for (int d=0; d < jsonBean.get(i).getCity().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCity().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
//            Log.i(ConfigInfo.TAG,"options2Items:"+options2Items);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
//            Log.i(ConfigInfo.TAG,"options3Items:"+options3Items);

        }

        handler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    private ProvinceResult mResult;
    public ArrayList<ProvinceBean> parseData(String result) {//Gson 解析
        mResult = new ProvinceResult();
        ArrayList<ProvinceBean> detail = new ArrayList<>();

        try {
            Gson gson = new Gson();
            mResult = gson.fromJson(result,ProvinceResult.class);
//            Log.i(ConfigInfo.TAG,"mResult:"+mResult.toString());
            detail = (ArrayList<ProvinceBean>) mResult.getProvince();
//            Log.i(ConfigInfo.TAG,"detail:"+detail);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    @Override
    public String getContent() {
        return etPhone.getText().toString().trim();
    }


    //EditText监听器

    class textChange implements TextWatcher {

        @Override

        public void afterTextChanged(Editable arg0) {

        }

        @Override

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,

                                      int arg3) {

        }

        @Override

        public void onTextChanged(CharSequence cs, int start, int before,

                                  int count) {

            boolean Sign1 = etName.getText().length() > 0;
            boolean Sign2 = etID.getText().length() > 0;
            boolean Sign3 = etPayPassword.getText().length() > 0;
            boolean Sign4 = etEnsurePayPassword.getText().length() > 0;
            boolean Sign5 = etAddress.getText().length() > 0;

            if (Sign1&Sign2&Sign3&Sign4&Sign5) {
                tvCommit.setEnabled(true);
                tvCommit.setBackgroundResource(R.drawable.selector_login_or_register_pressed);

            }
            else {
                //在layout文件中，对Button的text属性应预先设置默认值，否则刚打开程序的时候Button是无显示的
                tvCommit.setEnabled(false);
                tvCommit.setBackgroundResource(R.color.line_gray);
            }
        }
    }

    private TimePickerView pvCustomTime;
    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2017, 4, 20);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2117, 5, 20);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tvIDTime.setText(getTime(date));
                user_id_time = getTimePass(date);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
               /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();

    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
    private String getTimePass(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }


    // 倒数计时器
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            tvCaptcha.setClickable(true);
            tvCaptcha.setText("再次发送");
        }

        public void onTick(long millisUntilFinished) {
            tvCaptcha.setClickable(false);
            tvCaptcha.setText("再次发送验证码"+"("+millisUntilFinished / 1000+")");
        }
    }

}
