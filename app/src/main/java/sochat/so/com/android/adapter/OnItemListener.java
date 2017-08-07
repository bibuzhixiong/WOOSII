package sochat.so.com.android.adapter;

import android.view.View;

public interface OnItemListener {
    void checkBoxClick(int position);
    void onItemClick(View view, int position);
    void onItemLongClick(View view, int position);
}