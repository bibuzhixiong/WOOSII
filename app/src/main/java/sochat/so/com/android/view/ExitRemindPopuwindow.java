package sochat.so.com.android.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weavey.utils.ScreenSizeUtils;

import sochat.so.com.android.R;


/**
 * Created by ude on 2017/6/14.
 */

public class ExitRemindPopuwindow extends PopupWindow {
    private View mContentView;
    private Activity mActivity;


    private TextView tv_content,tv_sure,tv_cancle;

    private OnContinueClickListener onContinueClickListener;
    private OnBackIndexClickListener onBackIndexClickListerner;
    public ExitRemindPopuwindow(Activity activity, String content,String sure,String cancle){
        mActivity=activity;

        this.tv_content = tv_content;
        this.tv_sure=tv_sure;
        setWidth((int) (ScreenSizeUtils.getInstance(mActivity).getScreenWidth()*0.8));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mContentView = LayoutInflater.from(activity).inflate(R.layout.popup_exit_remind, null);
        setContentView(mContentView);
        setFocusable(true);

        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        setOutsideTouchable(false);
        setTouchable(true);
        tv_content= (TextView) mContentView.findViewById(R.id.tv_content);
        tv_sure= (TextView) mContentView.findViewById(R.id.tv_sure);
        tv_cancle=(TextView) mContentView.findViewById(R.id.tv_cancle);
        tv_content.setText(content);
        tv_sure.setText(sure);
        tv_cancle.setText(cancle);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                lighton();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(onContinueClickListener!=null){
                    onContinueClickListener.onContinueClickListener();
                }

                dismiss();

            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


      /*  mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
    }

    private void lighton() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        mActivity.getWindow().setAttributes(lp);
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        lightoff();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        lightoff();
        super.showAtLocation(parent, gravity, x, y);
    }

    public interface OnContinueClickListener{
        public void onContinueClickListener();
    }

    public void setOnContinueClickListener(OnContinueClickListener onContinueClickListener) {
        this.onContinueClickListener = onContinueClickListener;
    }

    public interface  OnBackIndexClickListener{
        public void onBackIndexClickListener();
    }
    public void setOnBackIndexClickListener(OnBackIndexClickListener onBackIndexClickListener) {
        this.onBackIndexClickListerner = onBackIndexClickListener;
    }
}
