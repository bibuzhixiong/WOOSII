package sochat.so.com.android.customview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by hackware on 2016/9/10.
 */

public class ExamplePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ExamplePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }



    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

// onPositionSelectedListenter.setSelectedCurrentPosition(position);
//    private OnPositionSelectedListenter onPositionSelectedListenter;
//    public interface OnPositionSelectedListenter{
//        void setSelectedCurrentPosition(int position);
//    }
//    public void setOnPositionSelectedListenter(OnPositionSelectedListenter onPositionSelectedListenter){
//        this.onPositionSelectedListenter =onPositionSelectedListenter;
//    }


}
