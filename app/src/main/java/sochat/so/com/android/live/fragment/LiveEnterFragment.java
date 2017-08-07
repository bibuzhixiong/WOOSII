package sochat.so.com.android.live.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sochat.so.com.android.R;
import sochat.so.com.android.live.activity.EnterAudienceActivity;
import sochat.so.com.android.live.activity.EnterLiveActivity;
import sochat.so.com.android.live.base.LiveBaseFragment;


/**
 * Created by zhukkun on 3/6/17.
 */
public class LiveEnterFragment extends LiveBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_enter, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView(R.id.live_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterLiveActivity.start(getContext());
            }
        });

        findView(R.id.audience_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterAudienceActivity.start(getContext());
            }
        });
    }

}
