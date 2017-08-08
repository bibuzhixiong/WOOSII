package sochat.so.com.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.eventbus.BackCityEvent;
import sochat.so.com.android.model.CityModel;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    protected Context mContext;
    protected List<CityModel> mDatas;
    protected LayoutInflater mInflater;
    private int type;

    public CityAdapter(Context mContext, List<CityModel> mDatas,int type) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
        this.type = type;
    }

    public List<CityModel> getDatas() {
        return mDatas;
    }

    public CityAdapter setDatas(List<CityModel> datas) {
        mDatas = datas;
        Log.i(ConfigInfo.TAG,"城市的Adapter_mDatas>toString:"+mDatas.size());
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CityModel city = mDatas.get(position);
        Log.i(ConfigInfo.TAG,"城市的Adapter:"+city.getCity()+";   "+city.getRegion_id());
        Log.i(ConfigInfo.TAG,"城市的Adapter_position:"+position);
        holder.tvCity.setText(city.getCity());
//        if (position ==0){
//            holder.content.setVisibility(View.GONE);
//        }
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type ==0){
                    //这里是记下用户注册的城市
                    DemoHelper.setArea(city.getCity());
                    DemoHelper.setRegion_id(city.getRegion_id());
                    Log.i(ConfigInfo.TAG,"这里是点击的城市里面："+city.toString());
                }
                Log.i(ConfigInfo.TAG,"这里是点击的城市外面："+city.toString());

                //这里记下用户选择观看视频的地区的ID
                DemoHelper.setVedioRegion_id(city.getRegion_id());
                DemoHelper.setVedioArea(city.getCity());
                EventBus.getDefault().post(new BackCityEvent(city.getCity(),city.getRegion_id()));

                finishActivity.finishActivity();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            content = itemView.findViewById(R.id.content);
        }
    }

    private setFinishActivity finishActivity;
    public interface setFinishActivity{
        void finishActivity();
    }
    public void setListeners(setFinishActivity finishActivity){
        this.finishActivity = finishActivity;
    }

}
