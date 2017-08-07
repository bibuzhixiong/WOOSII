package sochat.so.com.android.live.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;

import sochat.so.com.android.R;
import sochat.so.com.android.live.utils.VcloudFileUtils;


/**
 * Created by zhukkun on 1/17/17.
 *
 * MixAudioDialog简化版
 * 通过发送广播 与 视频采集controller进行伴音控制交互.
 */
public class MixAudioLayout extends RelativeLayout {
    public static final int MUSIC_START = 1;
    public static final int MUSIC_RESUME = 2;
    public static final int MUSIC_PAUSE = 3;
    public static final int MUSIC_STOP = 4;

    public static final String EXTRA_OPERATE = "AudioMixMSG";
    public static final String EXTRA_MUSIC_PATH = "AudioMixFilePathMSG";

    public static final String AUDIO_MIX_ACTION = "AudioMix";
    private Intent mIntentAudioMix = new Intent(AUDIO_MIX_ACTION);

    private static final String[] mixAudioFileArray = new File(VcloudFileUtils.getMp3FileDir()).list();
    private String mixAudioFile;
    private Context mContext;


    View tvNoMusic, tvMusic1, tvMusic2;

    Handler mHandler;

    public MixAudioLayout(Context context){
        super(context);
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        inflate(context, R.layout.layout_mix_audio, this);
        findViews();
    }

    private void findViews() {
        tvNoMusic = findViewById(R.id.tv_no_music);
        tvMusic1 = findViewById(R.id.tv_music1);
        tvMusic2 = findViewById(R.id.tv_music2);

        if(mixAudioFileArray!=null && mixAudioFileArray.length>=1){
            tvMusic1.setVisibility(VISIBLE);
        }

        if(mixAudioFileArray!=null && mixAudioFileArray.length>=2){
            tvMusic2.setVisibility(VISIBLE);
        }

        tvNoMusic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStopBroadcast();
                tvNoMusic.setEnabled(false);
                tvMusic1.setEnabled(true);
                tvMusic2.setEnabled(true);
            }
        });

        tvMusic1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStopBroadcast();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIntentAudioMix.putExtra(EXTRA_OPERATE, MUSIC_START);
                        mixAudioFile = mixAudioFileArray[0];
                        mIntentAudioMix.putExtra(EXTRA_MUSIC_PATH, mixAudioFile);
                        mContext.sendBroadcast(mIntentAudioMix);
                    }
                },100);

                tvNoMusic.setEnabled(true);
                tvMusic1.setEnabled(false);
                tvMusic2.setEnabled(true);
            }
        });

        tvMusic2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendStopBroadcast();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIntentAudioMix.putExtra(EXTRA_OPERATE, MUSIC_START);
                        mixAudioFile = mixAudioFileArray[1];
                        mIntentAudioMix.putExtra(EXTRA_MUSIC_PATH, mixAudioFile);
                        mContext.sendBroadcast(mIntentAudioMix);
                    }
                },100);

                tvNoMusic.setEnabled(true);
                tvMusic1.setEnabled(true);
                tvMusic2.setEnabled(false);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });
    }

    private void sendStopBroadcast() {
        mIntentAudioMix.putExtra(EXTRA_OPERATE, MUSIC_STOP);
        mContext.sendBroadcast(mIntentAudioMix);
    }
}
