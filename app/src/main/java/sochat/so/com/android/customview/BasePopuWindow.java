package sochat.so.com.android.customview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by Administrator on 2017/6/5.
 */

public abstract class BasePopuWindow extends PopupWindow implements View.OnKeyListener {

    private View rootview;

    public BasePopuWindow(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootview = getRootView(inflater);
        setContentView(rootview);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        rootview.setOnKeyListener(this);
        this.setOutsideTouchable(false); //允许在外点击消失

        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        this.getBackground().setAlpha(0);

//        rootview.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                int top = rootview.getTop();
//                int Bottom = rootview.getBottom();
//                int left = rootview.getLeft();
//                int right = rootview.getRight();
//                int y = (int) event.getY();
//                int x = (int) event.getX();
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (y < top || y > Bottom) {
//                            dismiss();
//                        }
//                        if (x < left || x > right) {
//                            dismiss();
//                        }
//                        break;
//                }
//
//                return true;
//            }
//        });



    }

    protected abstract View getRootView(LayoutInflater inflater);

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK && this.isShowing()){
            this.dismiss();
            return true;
        }
        return false;
    }
}
