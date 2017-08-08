package sochat.so.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.eventbus.UpdateUI;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpConnection;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.utils.Utils;
import sochat.so.com.android.view.BottomPushPopupWindow;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;

/**
 * Created by Administrator on 2017/3/18.
 */

public class UserSelfInfoActivity extends BaseActivity {
    @Bind(R.id.tv_user_phone)
    TextView tvUserPhone;
    @Bind(R.id.tv_user_phone_vause)
    EditText etUserPhoneVause;
    @Bind(R.id.rl_user_phone)
    RelativeLayout rlUserPhone;
    @Bind(R.id.tv_user_realname_vause)
    EditText tvUserRealnameVause;
    @Bind(R.id.rl_user_realname)
    RelativeLayout rlUserRealname;
    private MyPopupWindow popupWindow;

    @Bind(R.id.iv_top_back)
    ImageView ivTopBack;
    @Bind(R.id.tv_top_text)
    TextView tvTopTitle;
    @Bind(R.id.iv_photo_vause)
    ImageView ivPhotoVause;
    @Bind(R.id.rl_photo)
    RelativeLayout rlPhoto;
    @Bind(R.id.tv_user_name_vause)
    EditText etUserName;
    @Bind(R.id.rl_user_name)
    RelativeLayout rlUserName;
    @Bind(R.id.tv_gander_vause)
    TextView tvGander;
    @Bind(R.id.rl_gender)
    RelativeLayout rlGender;
    @Bind(R.id.et_simple_info_vause)
    EditText etSimpleInfo;
    @Bind(R.id.rl_simple_info)
    RelativeLayout rlSimpleInfo;
    @Bind(R.id.tv_commit)
    TextView tvExit;
    @Bind(R.id.rl_image)
    RelativeLayout rlImage;
    @Bind(R.id.iv_iamge)
    ImageView ivImage;
    @Bind(R.id.iv_back)
    ImageView ivBack;

    private TextView tvUp;
    private TextView tvDowp;
    private TextView tvCancel;

    private int sex =0;

    //记录用户信息
    private String name = "";
    private String gender = "";
    private String tel = "";
    private String thumb = "";
    private String detail = "";
    private String realname = "";

    private click_type clickType;
    /**
     * 提示框
     */
    private ProgressDialog progressDialog = new ProgressDialog();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case -1:
                    initLocalView();
                    break;
                case 0:
                    initView();
                    break;
                case 1:
//                    intiUserInfo();
                    DemoHelper.setDetail(etSimpleInfo.getText().toString().trim());
                    DemoHelper.setGender(tvGander.getText().toString().trim());
                    DemoHelper.setTel(etUserPhoneVause.getText().toString().trim());
                    DemoHelper.setNickName(etUserName.getText().toString().trim());
                    DemoHelper.setRealName(tvUserRealnameVause.getText().toString().trim());

                    MyToast.makeShortToast(UserSelfInfoActivity.this, "编辑成功");
                    progressDialog.destroy();
                    UserSelfInfoActivity.this.finish();
                    break;
                case 2:
                    progressDialog.destroy();
                    MyToast.makeShortToast(UserSelfInfoActivity.this, "发送失败");
                    break;
                case 3:
                    progressDialog.destroy();
                    MyToast.makeShortToast(UserSelfInfoActivity.this, "发送异常");
                    break;
            }
        }
    };

    //判断点击的是性别还是头像
    enum click_type {
        click_gender, click_photo
    }

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;

    protected static Uri tempUri;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_self_info);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        inits();

    }

    private void inits() {
        ivTopBack.setVisibility(View.VISIBLE);
        tvTopTitle.setText("编辑资料");
        listener = new MyClick();
        popupWindow = new MyPopupWindow(this);

        intiUserInfo();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(UpdateUI update){
        intiUserInfo();
    }

    private void intiUserInfo() {
        String url = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/user_info?phone=" + DemoHelper.getPhone();
        Log.i(ConfigInfo.TAG, "intiUserInfo_url：" + url);
        HttpUtils.doGetAsyn(UserSelfInfoActivity.this, true, url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG, "intiUserInfo_result：" + result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!TextUtils.isEmpty(result)) {
                        name = jsonObject.getString("name");
                        gender = jsonObject.getString("gender");
                        tel = jsonObject.getString("tel");
                        thumb = jsonObject.getString("thumb");
                        detail = jsonObject.getString("detail");
                        realname = jsonObject.getString("real_name");

                        DemoHelper.setUid(jsonObject.getString("user_id"));

                        DemoHelper.setThumb(thumb);
                        handler.sendEmptyMessage(0);
                    }
                    Log.i(ConfigInfo.TAG, "result:" + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }
            }
        });
    }

    private void initView() {
        tvGander.setText(gender.equals("1") ? "男" : "女");

        etUserPhoneVause.setText(tel);
        etUserName.setText(name);
        tvUserRealnameVause.setText(DemoHelper.getRealName());
        Log.i(ConfigInfo.TAG,"initView_detail:"+detail);
        etSimpleInfo.setText(detail.equals("null") ? "一句话介绍你自己" : detail);
        if (!TextUtils.isEmpty(thumb)) {
            Picasso.with(UserSelfInfoActivity.this).load(ConfigInfo.ApiUrl + thumb).memoryPolicy(NO_CACHE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhotoVause);
        } else {
            if (!TextUtils.isEmpty(DemoHelper.getThumb())) {
                Picasso.with(UserSelfInfoActivity.this).load(ConfigInfo.ApiUrl + DemoHelper.getThumb()).memoryPolicy(NO_CACHE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhotoVause);
            } else {
                if (!TextUtils.isEmpty(DemoHelper.getWXThumbUrl())) {
                    Picasso.with(UserSelfInfoActivity.this).load(DemoHelper.getWXThumbUrl()).memoryPolicy(NO_CACHE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhotoVause);
                } else {
                    ivPhotoVause.setImageResource(R.drawable.morentouxiang);
                }
            }
        }
    }

    private void initLocalView() {
        tvGander.setText(DemoHelper.getGender());
        if (!TextUtils.isEmpty(DemoHelper.getNickName())) {
            etUserName.setText(DemoHelper.getNickName());
        } else {
            etUserName.setText(DemoHelper.getWXNickName());
        }
        etSimpleInfo.setText(DemoHelper.getDetail());
        etUserPhoneVause.setText(DemoHelper.getTel());
        tvUserRealnameVause.setText(DemoHelper.getRealName());
        if (!TextUtils.isEmpty(DemoHelper.getThumb())) {
            Picasso.with(UserSelfInfoActivity.this).load(ConfigInfo.ApiUrl + DemoHelper.getThumb()).memoryPolicy(NO_CACHE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhotoVause);
        } else {
            if (!TextUtils.isEmpty(DemoHelper.getWXThumbUrl())) {
                Picasso.with(UserSelfInfoActivity.this).load(DemoHelper.getWXThumbUrl()).memoryPolicy(NO_CACHE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhotoVause);
            } else {
                ivPhotoVause.setImageResource(R.drawable.morentouxiang);
            }
        }

    }


    @OnClick({R.id.iv_top_back, R.id.iv_photo_vause, R.id.rl_photo, R.id.rl_user_name, R.id.rl_gender, R.id.rl_simple_info, R.id.tv_commit, R.id.rl_user_phone, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_top_back:
                UserSelfInfoActivity.this.finish();
                break;
            case R.id.iv_photo_vause:
                clickType = click_type.click_photo;
                tvUp.setText("拍照");
                tvDowp.setText("相册");
                popupWindow.show(this);
                break;
            case R.id.rl_photo:
//                isRlVisiable =true;
//                if (photo!=null){
//                    try {
//                        ivImage.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempUri));
////                        ivImage.setImageBitmap(photo);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                rlImage.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_user_name:
                break;
            case R.id.rl_gender:
                clickType = click_type.click_gender;
                tvUp.setText("男");
                tvDowp.setText("女");
                popupWindow.show(this);
                break;
            case R.id.rl_simple_info:
                break;
            case R.id.tv_commit:
                sex = tvGander.getText().toString().trim().equals("男") ? 1 : 0;
                commitUserInfo();
                break;
            case R.id.rl_user_phone:

                break;
            case R.id.iv_back:
                rlImage.setVisibility(View.GONE);
                isRlVisiable = false;
                break;


        }
    }

    private boolean isRlVisiable;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if (isRlVisiable) {
                rlImage.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void commitUserInfo() {
        if (!TextUtils.isEmpty(etUserPhoneVause.getText().toString().trim())) {
            if (!CommonUtils.checkPhoneNumber(etUserPhoneVause.getText().toString().trim())) {
                MyToast.makeShortToast(UserSelfInfoActivity.this, "您输入的手机号码格式不正确");
                return;
            }
        }
        try {
            String url = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/update_info?nick_name="
                    + (etUserName.getText().toString().trim() != null ? URLEncoder.encode(etUserName.getText().toString().trim(), "UTF-8") : etUserName.getText().toString().trim())
                    + "&detail=" + (etSimpleInfo.getText().toString().trim() != null ? URLEncoder.encode(etSimpleInfo.getText().toString().trim(), "UTF-8") : etSimpleInfo.getText().toString().trim())
                    + "&real_name=" + (tvUserRealnameVause.getText().toString().trim() != null ? URLEncoder.encode(tvUserRealnameVause.getText().toString().trim(), "UTF-8") : tvUserRealnameVause.getText().toString().trim())
                    + "&gender=" + sex
                    + "&tel=" + etUserPhoneVause.getText().toString().trim()
                    + "&user_id=" + DemoHelper.getUid()
                    + "&thumb=" + (thumb.equals("") ? DemoHelper.getThumb().replace(ConfigInfo.ApiUrl, "") : thumb);
            Log.i(ConfigInfo.TAG, "commitUserInfo_url:" + url);
            CommonUtils.showDialogs("上传中...", this, progressDialog);
            HttpUtils.doGetAsyn(UserSelfInfoActivity.this, true, url, handler, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Log.i(ConfigInfo.TAG, "commitUserInfo_result:" + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("Code");
                        if (code == 1) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(3);
                    }


                }
            });


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    private class MyPopupWindow extends BottomPushPopupWindow<Void> {
        public MyPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void aVoid) {
            View root = View.inflate(mBottomPopupWindowContext, R.layout.popup_demo, null);
            tvUp = (TextView) root.findViewById(R.id.tv_up);
            tvDowp = (TextView) root.findViewById(R.id.tv_down);
            tvCancel = (TextView) root.findViewById(R.id.cancel);

            tvUp.setOnClickListener(listener);
            tvDowp.setOnClickListener(listener);
            tvCancel.setOnClickListener(listener);

            return root;
        }
    }


    private MyClick listener;

    private class MyClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_up:
                    if (clickType == click_type.click_photo) {
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "image.jpg"));
                        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);

                    } else {
                        gender = "1";
                        tvGander.setText("男");
                    }
                    popupWindow.dismiss();
                    break;
                case R.id.tv_down:
                    if (clickType == click_type.click_photo) {
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);

                    } else {
                        gender = "0";
                        tvGander.setText("女");
                    }
                    popupWindow.dismiss();
                    break;
                case R.id.cancel:
//                    MyToast.showShortText(UserSelfInfoActivity.this, "取消");
                    popupWindow.dismiss();

                    break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    private Bitmap photo;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     * @param
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
//            photo = Utils.toRoundBitmap(photo, tempUri); // 这个时候的图片已经被处理成圆形的了
            ivPhotoVause.setImageBitmap(photo);
            uploadPic(photo);
        }
    }


    private static ProgressDialog dialog = new ProgressDialog();

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了

        String imagePath = Utils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath + "");
        if (imagePath != null) {
//产生临时上传的图片文件
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                String fileDir = Environment.getExternalStorageDirectory().getPath();
//                String path = fileDir+"/sochat/tmp";
//                File dir = new File(path);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
            File file = new File(imagePath);//将要保存图片的路径
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                photo.compress(Bitmap.CompressFormat.JPEG, 30, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //显示加载框
            CommonUtils.showDialogs("上传头像中...", UserSelfInfoActivity.this, dialog);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("filename", "user");
                jsonObject.put("user_id", DemoHelper.getUid());
                //默认以param=的方式去提交
                new HttpConnection().post(ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/up_img", jsonObject.toString(), "attach", file, callbackListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        }

    }

    private boolean updata = false;
    private HttpConnection.CallbackListener callbackListener = new HttpConnection.CallbackListener() {
        public void callBack(String result) {
            dialog.destroy();
            System.out.println("----------上传结果：------------" + result);
            if (result != null && !result.equals("")) {
                try {
                    JSONObject json_result = new JSONObject(result);
                    Message message = new Message();
                    message.what = json_result.getInt("Code");
                    if (message.what == 1) {
                        thumb = json_result.getString("thumb");
                        DemoHelper.setThumb(json_result.getString("thumb"));
                        updata = true;
//                        message.what=2;
                        Log.i(ConfigInfo.TAG, "thumb:" + thumb);
                    }
//                    message.obj=json_result.getString("Res");
//                    handler.sendMessage(message);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Log.i("处理上传用户头像时出错:", e1.getMessage());
                }
            }
        }
    };



}

