/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2015 netease
 * @auther biwei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sochat.so.com.android.live.liveplayer;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class NEVideoView extends SurfaceView {
    private static final String TAG = "NEVideoView";

    private int mVideoWidth;
    private int mVideoHeight;
    private int mPixelSarNum;
    private int mPixelSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private static Context mContext;

    EventListener listener;

    interface EventListener {

        boolean onKeyDown(int keyCode, KeyEvent event);

        void onTouchEvent(MotionEvent event);

        void onTrackballEvent(MotionEvent event);
    }

    public NEVideoView(Context context) {
        super(context);
        NEVideoView.mContext = context;
        initVideoView();
    }

    public NEVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        NEVideoView.mContext = context;
        initVideoView();
    }

    public NEVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        NEVideoView.mContext = context;
        initVideoView();
    }

    private void initVideoView() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
//        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float aspectRatio = (float) (mVideoWidth) / mVideoHeight;
        }

         int width=getDefaultSize(mVideoWidth, widthMeasureSpec);
         int height=getDefaultSize(mVideoHeight, heightMeasureSpec);
        Rect rect = new Rect();
        this.getWindowVisibleDisplayFrame(rect);//获取状态栏高度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕分辨率
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { //new
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            width = metrics.widthPixels;
            height = metrics.heightPixels ;//- rect.top
        } else { //old
            try {
                Method mRawWidth = Display.class.getMethod("getRawWidth");
                Method mRawHeight = Display.class.getMethod("getRawHeight");
                width = (Integer) mRawWidth.invoke(display);
                height = (Integer) mRawHeight.invoke(display) - rect.top;
            } catch (NoSuchMethodException e) {
                DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                width = dm.widthPixels;
                height = dm.heightPixels - rect.top;
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        int videoWidth=getDefaultSize(mVideoWidth, heightMeasureSpec);
        int videoHeight=getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float aspectRatio = (float) (mVideoWidth) / mVideoHeight;

        float winRatio = (float) width / height;
        if(winRatio > aspectRatio){
            videoWidth= width;
            videoHeight = (int) (width/aspectRatio);
        }else{
            videoWidth = (int)(height * aspectRatio);
            videoHeight = height;
        }
        }

       /* if (mVideoWidth > 0 && mVideoHeight > 0) {
            if ( mVideoWidth * height  > width * mVideoHeight ) {
                //Log.i("@@@", "image too tall, correcting");
                //height = width * mVideoHeight / mVideoWidth;
            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
                //Log.i("@@@", "image too wide, correcting");
                //width = height * mVideoWidth / mVideoHeight;
            } else {
                //Log.i("@@@", "aspect ratio is correct: " +
                        //width+"/"+height+"="+
                        //mVideoWidth+"/"+mVideoHeight);
            }
        }*/
        Log.e("YYY",videoWidth+"--"+videoHeight);
        setMeasuredDimension(videoWidth, videoHeight);
    }

    public void setVideoScalingMode(int videoScalingMode) {
        //Log.d("zhukun", "in setVideoMode myVideoWidth:" + mVideoWidth +"height"+ mVideoHeight);
        LayoutParams layPara = getLayoutParams();
        int winWidth = 0;
        int winHeight = 0;
        Rect rect = new Rect();
        this.getWindowVisibleDisplayFrame(rect);//获取状态栏高度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay(); //获取屏幕分辨率
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { //new
            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            winWidth = metrics.widthPixels;
            winHeight = metrics.heightPixels ;//- rect.top
        } else { //old
            try {
                Method mRawWidth = Display.class.getMethod("getRawWidth");
                Method mRawHeight = Display.class.getMethod("getRawHeight");
                winWidth = (Integer) mRawWidth.invoke(display);
                winHeight = (Integer) mRawHeight.invoke(display) - rect.top;
            } catch (NoSuchMethodException e) {
                DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                winWidth = dm.widthPixels;
                winHeight = dm.heightPixels - rect.top;
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        float winRatio = (float) winWidth / winHeight;
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            float aspectRatio = (float) (mVideoWidth) / mVideoHeight;
            if (mPixelSarNum > 0 && mPixelSarDen > 0)
                aspectRatio = aspectRatio * mPixelSarNum / mPixelSarDen;
            mSurfaceHeight = mVideoHeight;
            mSurfaceWidth = mVideoWidth;

            if (VideoConstant.VIDEO_SCALING_MODE_NONE == videoScalingMode && mSurfaceWidth < winWidth && mSurfaceHeight < winHeight) {
                layPara.width = (int) (mSurfaceHeight * aspectRatio);
                layPara.height = mSurfaceHeight;
            } else if (VideoConstant.VIDEO_SCALING_MODE_FIT == videoScalingMode) { // 按宽度计算
                if (winRatio < aspectRatio) {
                    layPara.width  = winWidth;
                    layPara.height = (int)(winWidth / aspectRatio);
                }
                else {
                    layPara.width  = (int)(aspectRatio * winHeight);
                    layPara.height = winHeight;
                }
            } else if (VideoConstant.VIDEO_SCALING_MODE_FILL_SCALE == videoScalingMode){ //全屏拉伸裁剪
               if(winRatio > aspectRatio){
                   layPara.width = winWidth;
                   layPara.height = (int) (winWidth/aspectRatio);
               }else{
                   layPara.width = (int)(winHeight * aspectRatio);
                   layPara.height = winHeight;
               }
            }
            else if (VideoConstant.VIDEO_SCALING_MODE_FILL_BLACK == videoScalingMode) { // 全屏留黑边，信息全保留
                if (winRatio > aspectRatio) {
                    layPara.width  = (int)(winHeight * aspectRatio);
                    layPara.height = winHeight;
                }
                else {
                    layPara.width  = winWidth;
                    layPara.height = (int)(winWidth / aspectRatio);
                }
            } else {
                if (winRatio < aspectRatio) {
                    layPara.width  = winWidth;
                    layPara.height = (int)(winWidth / aspectRatio);
                }
                else {
                    layPara.width  = (int)(aspectRatio * winHeight);
                    layPara.height = winHeight;
                }
            }
            Log.e("YYY","layPara.width:"+layPara.width+"  layPara.height:"+layPara.height+"  mSurfaceWidth:"+mSurfaceWidth+"  mSurfaceHeight:"+mSurfaceHeight);
            setLayoutParams(layPara);
//            getHolder().setKeepScreenOn(false);
            getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(listener != null){
            if(listener.onKeyDown(keyCode, event)){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if(listener != null){
            listener.onTrackballEvent(event);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(listener !=null ){
            listener.onTouchEvent(event);
        }
        return false;
    }

    public void upDateVideoSize(int videoWidth, int videoHeight, int pixelSarNum, int pixelSarDen){

        if(videoWidth!=0 && videoHeight!=0){
            this.mVideoWidth = videoWidth;
            this.mVideoHeight = videoHeight;
        }

        if(pixelSarNum!=0 && pixelSarDen!=0){
            this.mPixelSarNum = pixelSarNum;
            this.mPixelSarDen = pixelSarDen;
        }
    }

    public int getSurfaceWidth(){
        return this.mSurfaceWidth;
    }

    public int getSurfaceHeight(){
        return this.mSurfaceHeight;
    }

    public void onSurfaceChanged(int w, int h) {
        this.mSurfaceWidth = w;
        this.mSurfaceHeight = h;
    }

    public boolean isSizeNormal() {
        return mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setEventListener(EventListener lis){
        this.listener = lis;
    }
}
