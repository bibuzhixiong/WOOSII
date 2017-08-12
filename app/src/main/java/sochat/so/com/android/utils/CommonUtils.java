package sochat.so.com.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.wevey.selector.dialog.MDEditDialog;
import com.wevey.selector.dialog.NormalAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sochat.so.com.android.R;
import sochat.so.com.android.dialog.ProgressDialog;
import sochat.so.com.android.llpay_util.RiskParameterUtil;
import sochat.so.com.android.view.ExitRemindPopuwindow;

/**
 * Created by Administrator on 2017/2/22.
 */

public class CommonUtils {
    /**
     * 启动一个activity   这里通过.overridePendingTransition（）函数来进行Activity跳转的动画
     *
     * @param context
     * @param intent
     */

    public static String S_ID ="0";


    public static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.activity_open,
//                R.anim.activity_close);
    }

    /**
     * 验证手机号码是否合格
     *
     * @param phoneNumber 手机号码
     * @return boolean
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
//        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
//		Pattern pattern = Pattern.compile("^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-4678])\\d{8}$");
        Pattern pattern = Pattern.compile("^1[34578]([0-9]){9}");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * 验证手机密码的格式，只能是4-23个数字和字母组成
     * @param phonePassword
     * @return
     */
    public static  boolean checkPhonePassword(String phonePassword){
        Pattern pattern = Pattern.compile("^\\w{6,23}");
        Matcher matcher = pattern.matcher(phonePassword);
        return matcher.matches();
    }

    /**
     * 创建指定数量的随机字符串
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandom(boolean numberFlag, int length){
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }

    /**
     * AES加密密码
     */
    public static String encryption(String encrypt){
        try {
            return AESUtil.encrypt(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * AES加密解密
     */
    public static String decrypt(String decrypt){
        try {
            return AESUtil.decrypt(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载框
     *
     * @param str
     * @param mActivity
     * @param dialog
     */
    public static void showDialogs(String str, Context mActivity,
                                   ProgressDialog dialog) {
        try {
            if (!(dialog.progressDialog != null && dialog.progressDialog
                    .isShowing())) {
            } else {
                dialog.destroy();
            }
            // 显示加载框
            DisplayMetrics metric = new DisplayMetrics();
            ((Activity) mActivity).getWindowManager().getDefaultDisplay()
                    .getMetrics(metric);
            int height = metric.heightPixels; // 屏幕高度（像素）
            dialog.showDialog((Activity) mActivity, (int) (height * 0.40), str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context mActivity ,String string,Toast toast){
        if (toast ==null){
            toast = new Toast(mActivity);
        }
        toast.makeText(mActivity,string,Toast.LENGTH_SHORT).show();

    }

//    private static NormalAlertDialog.Builder dialog;

    public static void NeedLogin(final Activity mActivity, boolean isNeedUid, final LoginCallBack loginCallBack){
        if (isNeedUid){
            if (TextUtils.isEmpty(DemoHelper.getUid())){
                showTipDialog(mActivity, true, "温馨提示", "您还没有登录，跳转登录？", "取消", "登录", false, new DialogCallBack() {
                    @Override
                    public void left() {
                        loginCallBack.cancel();
                    }

                    @Override
                    public void right() {
                        loginCallBack.send();
                    }

                    @Override
                    public void edittext(String left) {

                    }

                });
//                //提示是否登录
//                dialog = new NormalAlertDialog.Builder(mActivity);
//
//                dialog.setTitleVisible(true)
//                        .setTitleText("温馨提示")
//                        .setTitleTextColor(R.color.black)
//                        .setContentText("您还没有登录，跳转登录？")
//                        .setContentTextColor(R.color.black_light)
//                        .setLeftButtonText("取消")
//                        .setLeftButtonTextColor(R.color.gray)
//                        .setRightButtonText("登录")
//                        .setRightButtonTextColor(R.color.black_light)
//                        .setOnclickListener(new com.wevey.selector.dialog.DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
//                            @Override
//                            public void clickLeftButton(NormalAlertDialog dialog, View view) {
//                                loginCallBack.cancel();
//                                dialog.dismiss();
//                            }
//
//                            @Override
//                            public void clickRightButton(NormalAlertDialog dialog, View view) {
//                                loginCallBack.send();
//                                dialog.dismiss();
//                            }
//                        });
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.build().show();
            }else{
                loginCallBack.comeon();
            }
        }else{
            loginCallBack.comeon();
        }
    }
    private DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

    //风控参数
    private static RiskParameterUtil riskParameter;

    public static String getConstructRiskItem(Activity activity) {
        JSONObject mRiskItem = new JSONObject();
        try {
            mRiskItem.put("frms_client_chnl", "APP");
            mRiskItem.put("frms_ip_addr", RiskParameterUtil.getIpAddr());
            mRiskItem.put("frms_imei", RiskParameterUtil.getPhoneIMEI(activity));
            mRiskItem.put("frms_mechine_id", RiskParameterUtil.getDeviceMachineId(activity));
            mRiskItem.put("frms_mac_addr", RiskParameterUtil.getMacAddress(activity));
            return mRiskItem.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void showTipDialog(Activity context, boolean isVisibleTitle, String title, String content, String left, String right, boolean canceledOnTouchOutside, final DialogCallBack callBack){
//        NormalAlertDialog.Builder dialog = new NormalAlertDialog.Builder(context);

        new NormalAlertDialog.Builder(context).setTitleVisible(isVisibleTitle)
                .setTitleText(title)
                .setTitleTextColor(R.color.black)
                .setContentText(content)
                .setContentTextColor(R.color.black_light)
                .setLeftButtonText(left)
                .setLeftButtonTextColor(R.color.gray)
                .setRightButtonText(right)
                .setRightButtonTextColor(R.color.black_light)
                .setCanceledOnTouchOutside(canceledOnTouchOutside)
                .setOnclickListener(new com.wevey.selector.dialog.DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickLeftButton(NormalAlertDialog dialog, View view) {
                        callBack.left();
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(NormalAlertDialog dialog, View view) {
                        callBack.right();
                        dialog.dismiss();
                    }
                }).build().show();
//        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
//        dialog.build().show();
    }


    public static void showEditTextDialog(Activity context, String title, int MaxLength,String hint, String left, String right, final DialogCallBack callBack){
        new MDEditDialog.Builder(context).setTitleVisible(true)
                .setTitleText(title)
                .setTitleTextSize(20)
                .setTitleTextColor(R.color.black_light)
                .setContentText("")
                .setContentTextSize(18)
                .setMaxLength(MaxLength)
                .setHintText(hint)
                .setMaxLines(1)
                .setContentTextColor(R.color.colorPrimary)
                .setButtonTextSize(14)
                .setLeftButtonTextColor(R.color.gray)
                .setLeftButtonText(left)
                .setRightButtonTextColor(R.color.black_light)
                .setRightButtonText(right)
                .setLineColor(R.color.colorPrimary)
                .setInputTpye(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .setOnclickListener(new com.wevey.selector.dialog.DialogInterface.OnLeftAndRightClickListener<MDEditDialog>
                        () {

                    @Override
                    public void clickLeftButton(MDEditDialog dialog, View view) {
                        dialog.dismiss();
                    }

                    @Override
                    public void clickRightButton(MDEditDialog dialog, View view) {
                        callBack.edittext(dialog.getEditTextContent());
                        dialog.dismiss();
                    }
                })
                .setMinHeight(0.3f)
                .setWidth(0.8f)
                .build()
                .show();
    }


    public static void showSingleDialog(Activity activity, String title, String content, String button, final DialogCallBack callBack){
        new NormalAlertDialog.Builder(activity).setHeight(0.23f)  //屏幕高度*0.23
                .setWidth(0.65f)  //屏幕宽度*0.65
                .setTitleVisible(true).setTitleText(title)
                .setTitleTextColor(R.color.black_light)
                .setContentText(content)
                .setContentTextColor(R.color.black_light)
                .setSingleMode(true).setSingleButtonText(button)
                .setSingleButtonTextColor(R.color.colorAccent)
                .setCanceledOnTouchOutside(true)
                .setSingleListener(new com.wevey.selector.dialog.DialogInterface.OnSingleClickListener<NormalAlertDialog>() {
                    @Override
                    public void clickSingleButton(NormalAlertDialog dialog, View view) {
                        callBack.right();
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    /**
     * 对TextView中间部分文字颜色的控制
     */
    public static SpannableStringBuilder setTextViewPartColorChange(String content,int start,int end,int color){
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(color);
        builder.setSpan(redSpan, start,end , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }


    public static void showRegisterRemindPopuwindow(Activity context, ExitRemindPopuwindow popuwindow, String content, String sure, String cancle, View view, final CallBack callBack){
        if(popuwindow!=null){
            popuwindow.dismiss();
        }
        popuwindow=new ExitRemindPopuwindow(context,content,sure,cancle);
        popuwindow.setOnContinueClickListener(new ExitRemindPopuwindow.OnContinueClickListener() {
            @Override
            public void onContinueClickListener() {
                callBack.callback();
            }
        });
        popuwindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }


    //版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

}
