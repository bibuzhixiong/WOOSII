package sochat.so.com.android.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.PanoramaModel;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/6/20.
 */

public class PanoramaAdapter extends ListBaseAdapter<PanoramaModel> {
    private Context context;
    private MyToast toast;

    public PanoramaAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_panorama;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        PanoramaModel item =mDataList.get(position);
        ImageView ivPanorama = holder.getView(R.id.iv_panorama);
        TextView tvPanoramaName = holder.getView(R.id.tv_panorama_name);
        TextView tvPanoramaInfo = holder.getView(R.id.tv_panorama_info);

        tvPanoramaName.setText(item.getPanorama_name());
        tvPanoramaInfo.setText(item.getPanorama_info());
        if (item.getImage_url()!=null&item.getImage_url().length()>5){
            Picasso.with(context).load(ConfigInfo.ApiUrl+item.getImage_url()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivPanorama);
        }else{
            ivPanorama.setImageResource(R.drawable.app_icon);
        }

    }
}
