package sochat.so.com.android.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/4/12.
 */

public class MyRadioButton extends LinearLayout {
    private Context context;
    private View view;
    private TextView tvTime;
    private TextView tvMoney;
    private RadioButton mRadioButton;
    private LinearLayout llLayout;
    public MyRadioButton(Context context) {
        this(context, null);
    }

    public MyRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.item_recharge,this,true);

        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvMoney = (TextView) view.findViewById(R.id.tv_money);
//        mRadioButton = (RadioButton) view.findViewById(R.id.mRadioButton);
        llLayout = (LinearLayout) view.findViewById(R.id.ll_layout);

        mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setLayoutBackground(b);
                setTextViewColor(b);
            }
        });

    }

    public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextView(int time,int money){
        tvTime.setText(time+"沃币");
        tvMoney.setText("售价 "+money);
    }

    public void setLayoutBackground(boolean color){
        if (color){
            llLayout.setBackgroundResource(R.drawable.corners_layout_green);
        }else{
            llLayout.setBackgroundResource(R.drawable.corners_layout);
        }

    }

    public void setTextViewColor(boolean color){
        if (color){
           tvMoney.setTextColor(0xFF15B422);
           tvTime.setTextColor(0xFF15B422);
        }else{
            tvMoney.setTextColor(0xFF999999);
            tvTime.setTextColor(0xFF333333);
        }
    }



}
