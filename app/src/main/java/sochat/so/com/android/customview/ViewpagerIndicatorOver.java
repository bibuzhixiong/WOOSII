package sochat.so.com.android.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import sochat.so.com.android.R;

/**
 * 使用这个指示器时一定要注意在属性文件添加可见的tab属性，并将它在XML文件进行配置
 * 设置颜色时使用十六进制,修改指示器下划线的颜色必须在这个类里面手动进行修改
 * Created by Administrator on 2017/1/6.
 */

public class ViewpagerIndicatorOver extends LinearLayout {
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 构造图形
     */
    private Path mPath;
    /**
     * 线条高度
     */
    private int mLineWidth;
    /**
     * 线条宽度
     */
    private int mLineHeight;
    /**
     * 初始化偏移位置
     */
    private int mInitTranslationX;
    /**
     * 偏移位置
     */
    private int mTranslationX;
    /**
     * 控制下划线的长度,这里是通过把每个tab的宽度求出来后，再乘以百分数来计算
     */
    private  float RADIO_TRIANGLR_WIDTH =1/2F;

    /**
     *  新建一个ViewPager对象
     */
    private ViewPager mViewPager;
    /**
     *  tab数量
     */
    private int mTabVisibleCount;
    /**
     *  默认tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 4 ;
    private List<String> mTitles;
    private ViewPager viewpager;
    /**
     * tab正常时的颜色
     */
    private  int COLOR_TEXT_NOEMAL = 0xFF000000;
    private  int COLOR_TEXT_HIGHLIFHT = 0xFF15B422;
    private  int COLOR_INDICATOR_LINE = 0xFF15B422;

    public ViewpagerIndicatorOver(Context context) {
        this(context,null);
    }

    public ViewpagerIndicatorOver(Context context, AttributeSet attrs) {
        super(context, attrs);
        //可见tab数量
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewpagerIndicatorOver);
        mTabVisibleCount =a.getInt(R.styleable.ViewpagerIndicatorOver_visiable_tab_count,COUNT_DEFAULT_TAB);
        if(mTabVisibleCount<0){
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        //释放
        a.recycle();

        //初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(COLOR_INDICATOR_LINE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public ViewpagerIndicatorOver(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //设置tab正常时的颜色
    public void setColorTabNormal(int color){
        COLOR_TEXT_NOEMAL = color;
    }
    //设置tab被选中时高亮颜色
    public void setColorTabSelected(int color){
        COLOR_TEXT_HIGHLIFHT = color;
    }
    //设置指示器下面横线颜色
    public void setColorIndicatorLine(int color){
        COLOR_INDICATOR_LINE = color;
    }
    //设置指示器下面横线颜色
    public void setWidthIndicatorLine(float present){
        RADIO_TRIANGLR_WIDTH = present;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX+mTranslationX,getHeight()-3);
        canvas.drawPath(mPath,mPaint);

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 控件宽高发生变化时就会回调此方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLineWidth = (int) (w/mTabVisibleCount*RADIO_TRIANGLR_WIDTH);
        mInitTranslationX = w/mTabVisibleCount/2 - mLineWidth/2;
        initLine();//初始化线条
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if(cCount == 0) return ;
        for(int i =0;i<cCount;i++){
            View view = getChildAt(i);
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth()/mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    /**
     * 屏幕宽度
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 初始化直线
     */
    private int bold = 3;
    public void setLineBold(int bold){
        this.bold = bold;
    }

    private void initLine() {
        mLineHeight = bold;//线条的高度
        mPath = new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mLineWidth,0);
        mPath.lineTo(mLineWidth,-mLineHeight);
        mPath.lineTo(0,-mLineHeight);
        mPath.close();
    }

    /**
     * 指示器跟随手指移动
     * @param position
     * @param positionOffset
     */
    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth()/mTabVisibleCount;
        mTranslationX = (int) (tabWidth*(position+positionOffset));

        //容器移动，在tab
        if(position >=(mTabVisibleCount-2)&&positionOffset>0&&getChildCount()>mTabVisibleCount){
            if(mTabVisibleCount!=1){
                this.scrollTo((position-(mTabVisibleCount-2))*tabWidth+(int)(tabWidth*positionOffset),0);
            }else{
                this.scrollTo(position*tabWidth+(int)(tabWidth*positionOffset),0);
            }
        }
        //重绘的方法
        invalidate();
    }
    public void setTabItemTitle(List<String> titles){
        if(titles!=null&&titles.size()>0){
            //在设置tab之前移除所有的内部View
            this.removeAllViews();
            mTitles =titles;
            for(String title:mTitles){
                //增加tab的View
                addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }

    /**
     * 设置可见的tab数量
     * @param count
     */
    public void setVisiableTabCount(int count){
        mTabVisibleCount = count;
    }

    /**
     * 这里是添加tab文字
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth()/mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setTextColor(COLOR_TEXT_NOEMAL);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 因为在指示器内占用了ViewPager的监听接口，所以定义一个接口供外部调用
     */
    public interface PageChangeListener{
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void onPageSelected(int position);
        public void onPageScrollStateChanged(int position);
    }

    public PageChangeListener mListenr;
    public void setOnPageChangeListener(PageChangeListener mListenr){
        this.mListenr = mListenr;
    }



    public void setViewPager (ViewPager viewpager , int pos){
        this.viewpager = viewpager;
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position,positionOffset);
                if(mListenr!=null){
                    mListenr.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mListenr!=null){
                    mListenr.onPageSelected(position);
                }
                highLightText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mListenr!=null){
                    mListenr.onPageScrollStateChanged(state);
                }
            }
        });
        viewpager.setCurrentItem(pos);
        highLightText(pos);
    }
    private void resetTextViewColor(){
        for(int i =0;i<getChildCount();i++){
            View view = getChildAt(i);
            if(view instanceof TextView){
                ((TextView) view).setTextColor(COLOR_TEXT_NOEMAL);
            }
        }
    }

    //设置文本高亮颜色
    private void highLightText(int pos){
        resetTextViewColor();
        View view = getChildAt(pos);
        if(view instanceof TextView){
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIFHT);
        }
    }

    //设置点击事件
    private void setItemClickEvent(){
        int cCount = getChildCount();
        for(int i=0;i<cCount;i++){
            final int j =i;
            View view =getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewpager.setCurrentItem(j);
                }
            });
        }
    }

    private List<Fragment> fragments;

    private Adapter adapter;
    public void setViewPagerAdapter(FragmentManager getSupportChildFragmentManager, List<Fragment> fragments){
        this.fragments = fragments;
        adapter = new Adapter(getSupportChildFragmentManager);
        viewpager.setAdapter(adapter);
    }

    private class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
