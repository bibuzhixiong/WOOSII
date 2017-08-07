package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.customview.ExamplePagerAdapter;
import sochat.so.com.android.customview.ScaleTransitionPagerTitleView;

/**
 * Created by Administrator on 2017/7/7.
 */

public class HomepageFragment extends BaseFragment {
    private View view;

    private List<Fragment>fragments;
    private ViewPager mViewPager;
    /**
     * ViewPager的帮助类，用来设置ViewPager的数量
     */
    private ExamplePagerAdapter mExamplePagerAdapter;

    private MagicIndicator magicIndicator ;
    private CommonNavigator commonNavigator;
    private ArrayList<String> titlelists = new ArrayList<>();

    @Override
    public String getFragmentName() {
        return "HomepageFragment";
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * 新建Fragment
     */
    public static HomepageFragment newInstance() {
        HomepageFragment fragment = new HomepageFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage,container,false);
        findById();
        inits();
        return view;
    }

    private void findById() {
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }
    private void inits() {
        if (titlelists!=null){
            titlelists.clear();
            titlelists.add("视频");
            titlelists.add("360全景");
            titlelists.add("直播");
        }

        magicIndicator = (MagicIndicator) view.findViewById(R.id.magic_indicator);
        commonNavigator = new CommonNavigator(mActivity);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fragments = new ArrayList<>();
        fragments.add(new MarketingFragment());
        fragments.add(new PanoramaFragment());
        fragments.add(new LiveFragment());

        mExamplePagerAdapter =new ExamplePagerAdapter(getChildFragmentManager(),fragments);

        mViewPager.setAdapter(mExamplePagerAdapter);
        initMagicIndicator();
    }
    /**
     * 这里是对头部指示器的数据初始化
     */
    private void initMagicIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator) view.findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(0xFFF6F6F6);
        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titlelists == null ? 0 : titlelists.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titlelists.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(0xFF15B422);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
//                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
//                indicator.setColors(Color.parseColor("#15B422"), Color.parseColor("#15B422"));
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 4));
                indicator.setLineWidth(UIUtil.dip2px(context, 10));
                indicator.setRoundRadius(UIUtil.dip2px(context, 2));
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#00c853"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

}
