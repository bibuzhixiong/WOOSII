package sochat.so.com.android.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sochat.so.com.android.R;
import sochat.so.com.android.activity.MyMainActivity;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.AnswerRecordImageView;
import sochat.so.com.android.customview.FiveLine;
import sochat.so.com.android.customview.NoScrollViewPager;
import sochat.so.com.android.customview.WaveView;
import sochat.so.com.android.utils.HttpUtils;

/**
 * Created by Administrator on 2017/2/20.
 */

public class AnswerFragment extends BaseFragment implements AnswerRecordImageView.setOnNormalState,AnswerRecordImageView.setOnSpeakState,AnswerRecordImageView.setOnListenedState,AnswerRecordImageView.setNoScrollViewPager,MyMainActivity.setFreshAnswerFragmentView {
    @Bind(R.id.state_normal)
    LinearLayout llStateNormal;
    @Bind(R.id.ivspeaking)
    ImageView ivSpeaking;
    @Bind(R.id.tvspeaking)
    TextView tvSpeaking;
    @Bind(R.id.state_speaking)
    LinearLayout llStateSpeaking;
    @Bind(R.id.ivlistened)
    ImageView ivListened;
    @Bind(R.id.tvlistened)
    TextView tvListened;
    @Bind(R.id.state_listened)
    LinearLayout llSateListened;
    @Bind(R.id.iv_voice)
    AnswerRecordImageView ivVoice;
    @Bind(R.id.tvAbove)
    TextView tvBottomAbove;
    @Bind(R.id.tvBelow)
    TextView tvBottomBelow;
    @Bind(R.id.loadView)
    FiveLine loadView;


    /**
     * Fragment的View
     */
    private View view;
    private TextView tvTip;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_SPEAKING = 1;
    public static final int STATE_LISTENED = 2;
    private int currentState ;
    private String answer;
    private TextView tvTopTitle;
    private ImageView ivSearch;

    private NoScrollViewPager viewPager;
    /**
     * 水波背景控件
     */
    private WaveView wvView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    tvListened.setText(answer);
                    break;
                case 1:
                    Toast.makeText(mActivity,"很抱歉，获取答案失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 2:

                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }


    @Override
    public String getFragmentName() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_answer, container, false);
        ButterKnife.bind(this, view);

        inits();
        setListeners();

        return view;
    }

    private void inits() {
        tvTopTitle = (TextView) view.findViewById(R.id.tv_top_text);
        ivSearch = (ImageView) view.findViewById(R.id.iv_search);
        tvTopTitle.setText("答案");
        ivSearch.setVisibility(View.GONE);


        viewPager = (NoScrollViewPager) mActivity.findViewById(R.id.main_viewpager);
        //初始化默认界面文字
        tvTip = (TextView) view.findViewById(R.id.tv_tip);
        //水波背景控件初始化
        wvView = (WaveView) view.findViewById(R.id.tvwave);
        //初始化现实界面的状态，每次进入都让其在默认提示界面
        ivVoice.setActivity(mActivity);
        ivVoice.setNormalStateListener(AnswerFragment.this);
        ivVoice.setSpeakStateListener(AnswerFragment.this);
        ivVoice.setListenedStateListener(AnswerFragment.this);
        ivVoice.setNoScrollListener(AnswerFragment.this);

        Log.i(ConfigInfo.TAG,ivVoice.toString());
    }

    private void setListeners() {

//        ivVoice.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                llStateNormal.setVisibility(View.GONE);
//                llStateSpeaking.setVisibility(View.VISIBLE);
//                llSateListened.setVisibility(View.GONE);
//
//                return false;
//            }
//        });

    }

    public static AnswerFragment newInstance() {
        AnswerFragment fragment = new AnswerFragment();
        return fragment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.ivspeaking, R.id.ivlistened, R.id.iv_voice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivspeaking:

                break;
            case R.id.ivlistened:

                break;
            case R.id.iv_voice:

                break;

        }
    }

    @Override
    public void normal() {
        wvView.stop();
        now();
        showAnim();

        viewPager.setNoScroll(false);

    }

    @Override
    public void speak() {
        wvView.start();
        ing();
        viewPager.setNoScroll(true);
    }

    @Override
    public void listened() {
        wvView.stop();
        ed();
        viewPager.setNoScroll(false);
    }

    private void now(){
        if (llStateNormal!=null||llStateNormal!=null||llStateNormal!=null){
            llStateNormal.setVisibility(View.VISIBLE);
            llSateListened.setVisibility(View.GONE);
            llStateSpeaking.setVisibility(View.GONE);
        }
    }

    private void ing(){
        if (llStateNormal!=null||llStateNormal!=null||llStateNormal!=null) {
            llStateNormal.setVisibility(View.GONE);
            llStateSpeaking.setVisibility(View.VISIBLE);
            llSateListened.setVisibility(View.GONE);
        }
    }

    private void ed(){
        if (llStateNormal!=null||llStateNormal!=null||llStateNormal!=null) {
            llStateNormal.setVisibility(View.GONE);
            llStateSpeaking.setVisibility(View.GONE);
            llSateListened.setVisibility(View.VISIBLE);
            //得到答案
            getAnswer();
        }
    }

    private void getAnswer() {
        HttpUtils.doGetAsyn(null,false, ConfigInfo.ANSWER_URL, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Log.i(ConfigInfo.TAG,"getAnswer():"+result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    answer = jsonObject.getString("answer");
                    handler.sendEmptyMessage(0);//成功获得答案，去控件显示
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);//失败
                }
            }
        });

    }

    private void showAnim() {
        PropertyValuesHolder propertyHolder = PropertyValuesHolder.ofFloat("alpha",1f,0f,1f);
        ObjectAnimator.ofPropertyValuesHolder(tvTip,propertyHolder).setDuration(1000).start();
    }

    @Override
    public void noScroll() {
        viewPager.setNoScroll(true);
        tvListened.setText("");
    }

    @Override
    public void freshView() {
        now();
    }
}
