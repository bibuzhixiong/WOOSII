package sochat.so.com.android.live.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.netease.nim.uikit.common.fragment.TFragment;

/**
 * Created by zhukkun on 1/5/17.
 */
public class LiveBaseFragment extends TFragment {

    public final String Tag = this.getClass().getSimpleName();

    protected View findViewById(int id) {
        return getView().findViewById(id);
    }

    private Toast mToast;

    protected void showToast(final String text) {
        if (mToast == null) {
            mToast = Toast.makeText(getContext().getApplicationContext(), text, Toast.LENGTH_LONG);
        }
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mToast.setText(text);
                    mToast.show();
                }
            });
        } else {
            mToast.setText(text);
            mToast.show();
        }
    }

    protected void showConfirmDialog(String title, String message, DialogInterface.OnClickListener okEvent, DialogInterface.OnClickListener cancelEvent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定",
                okEvent);
        if (cancelEvent != null) {
            builder.setNegativeButton("取消",
                    cancelEvent);
        }
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Tag, "onResume " + this);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(Tag, "onPause " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Tag, "onDestroy " + this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "onCreate " +  this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(Tag, "onViewCreated " + this);
    }
}
