package sochat.so.com.android.view;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import sochat.so.com.android.R;

/**
 * BottomPushPopupWindow的简单例子
 * 
 * @author y
 */
public class EditInfoPopupWindow extends BottomPushPopupWindow<Void> {

    public EditInfoPopupWindow(Context context) {
        super(context, null);
    }

    @Override
    protected View generateCustomView(Void data) {
        View root = View.inflate(mBottomPopupWindowContext, R.layout.popup_demo, null);
        View up = root.findViewById(R.id.tv_up);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Toast.makeText(mBottomPopupWindowContext, "男", Toast.LENGTH_SHORT).show();
            }
        });
        View down = root.findViewById(R.id.tv_down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Toast.makeText(mBottomPopupWindowContext, "女", Toast.LENGTH_SHORT).show();
            }
        });
        View cancelView = root.findViewById(R.id.cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return root;
    }
}