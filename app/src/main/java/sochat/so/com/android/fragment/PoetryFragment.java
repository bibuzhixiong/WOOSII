package sochat.so.com.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sochat.so.com.android.R;

/**
 * 直播的fragment
 * Created by Administrator on 2017/7/7.
 */

public class PoetryFragment extends BaseFragment {
    private View view;
    private String content;
    private int flag;
    private TextView tvContent;

    @Override
    public String getFragmentName() {
        return "PoetryFragment";
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * 新建Fragment
     */
    public static PoetryFragment newInstance(String content,int flag) {
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        bundle.putInt("flag", flag);

        PoetryFragment fragment = new PoetryFragment();
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if(bundle != null){
            content = bundle.getString("content");
            flag = bundle.getInt("flag");
        }

        view = inflater.inflate(R.layout.fragment_poetry_relative, container, false);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        if (flag==1){
            tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
        }else{
            tvContent.setGravity(Gravity.START);
        }
        //
        String addspacing = content.replace("s","        ");
        tvContent.setText(addspacing.replace('n','\n'));
        return view;
    }



}
