package sochat.so.com.android.live.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zhukkun on 1/17/17.
 */
public class AudioAnimateView extends ImageView {

    AnimationDrawable animationDrawable;


    public AudioAnimateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        animationDrawable = (AnimationDrawable) getBackground();
    }

    public AudioAnimateView(Context context) {
        super(context);
        animationDrawable = (AnimationDrawable) getBackground();
    }

    public void setVisibility(int v) {
        // let's be nice with the UI thread
        if (v == GONE || v == INVISIBLE) {
            if (animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
        } else if (v == VISIBLE) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
            });
        }
        if (getVisibility() != v) {
            super.setVisibility(v);
        }
    }
}
