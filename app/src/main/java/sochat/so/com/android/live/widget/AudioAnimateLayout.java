package sochat.so.com.android.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import sochat.so.com.android.R;


/**
 * Created by zhukkun on 1/17/17.
 */
public class AudioAnimateLayout extends RelativeLayout{

    View animateView;

    public AudioAnimateLayout(Context context) {
        super(context);
        extraInit(context);
    }

    public AudioAnimateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        extraInit(context);
    }

    private void extraInit(Context context){
        animateView = LayoutInflater.from(context).inflate(R.layout.layout_audio_animate, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        addView(animateView, params);
        setBackgroundResource(R.drawable.bg);

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getVisibility() == VISIBLE) {
                    animateView.setVisibility(View.VISIBLE);
                } else {
                    animateView.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });
    }

    public void setVisibility(int v) {
        if (getVisibility() != v) {
            super.setVisibility(v);
        }
        if (v == GONE || v == INVISIBLE) {
            animateView.setVisibility(View.INVISIBLE);
        } else if (v == VISIBLE) {
            animateView.setVisibility(View.VISIBLE);
        }
    }
}
