package sochat.so.com.android.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 这里重写一个GridView的作用是因为如果不重写里面的onMeasure（）方法会导致整个GridView只有一行数据，这里是重新度量整个
 * GridView的大小，让GridView根据实际的大写来进行重新创建。
 * Created by Administrator on 2017/5/8.
 */

public class MyGridView extends GridView {
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 这里是重新度量整个GridView的尺寸，不加的话会只显示一行数据
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}