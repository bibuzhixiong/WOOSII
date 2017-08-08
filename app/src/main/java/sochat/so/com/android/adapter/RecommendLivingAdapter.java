package sochat.so.com.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.RecommentLivingModel;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/7/28.
 */

public class RecommendLivingAdapter extends ListBaseAdapter<RecommentLivingModel> {
    private Context context;
    private MyToast toast;


    public RecommendLivingAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recommend_living;
    }


    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final RecommentLivingModel item = mDataList.get(position);

        TextView tvLiveTitle = holder.getView(R.id.tv_live_title);
        TextView tvLiveName = holder.getView(R.id.tv_live_name);
//        TextView tvLiveLooknum = holder.getView(R.id.tv_live_looknum);

        ImageView ivLive = holder.getView(R.id.iv_live_image);

        if (!TextUtils.isEmpty(item.getTheme())){
            tvLiveTitle.setText(item.getTheme());
        }else{
            tvLiveTitle.setText(item.getName()+"的直播间");

        }

        tvLiveName.setText("主讲人："+item.getName());
//        tvLiveLooknum.setText(item.getUsercount()+"人");

        if (item.getThumb()!=null){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivLive);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.liuyifei).into(ivLive);
            }
        }else{
            ivLive.setImageResource(R.drawable.morentouxiang);
        }
    }
}