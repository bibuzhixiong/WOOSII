package sochat.so.com.android.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/3/6.
 */

public class AnswerDialog {

    private static final String TAG = "DialogManager";
    private Dialog mDialog;
    private ImageView mIcon;
//    private ImageView mVoice;
    private TextView mLabel;

    private Context mContext;

    public AnswerDialog(Context mContext) {
        this.mContext = mContext;
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
    }

    /**
     * 显示对话框
     */
    public void showRecordeingDialog() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.answerdialog, null);
        mDialog.setContentView(view);

        mIcon = (ImageView) mDialog.findViewById(R.id.main_recorder_dialog_icon);
        mLabel = (TextView) mDialog.findViewById(R.id.main_recorder_dialog_label);
        mDialog.show();
    }

    /**
     * 正在录制提示
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
//            mVoice.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.answer_listening);
            mLabel.setText(R.string.str_dialog_listening);
        }
    }

    /**
     * 取消录制对话框提示
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.answer_cancel);
            mLabel.setText(R.string.str_dialog_want_cancel);
            mLabel.setBackgroundColor(Color.parseColor("#66FF0000"));
        }
    }

    /**
     * 录音时间过短提示
     */
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.answer_cancel);
            mLabel.setText(R.string.str_dialog_time_short);
        }
    }

    /**
     * 取消对话框
     */
    public void dismissDialog() {
        if (mDialog != null ) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 显示音量大小
     */
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            int resId = mContext.getResources().getIdentifier("v" + level, "mipmap", mContext.getPackageName());
//            mVoice.setImageResource(resId);
        }
    }
}
