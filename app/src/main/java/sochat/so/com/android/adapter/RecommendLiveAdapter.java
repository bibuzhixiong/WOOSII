package sochat.so.com.android.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.CourseChild;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Lzx on 2016/12/30.
 */

public class RecommendLiveAdapter extends ListBaseAdapter<CourseChild> {
    private Context context;
    private MyToast toast;


    public RecommendLiveAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recommend_live;
    }


    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final CourseChild item = mDataList.get(position);

        TextView tvLiveTitle = holder.getView(R.id.tv_live_title);
        TextView tvLiveName = holder.getView(R.id.tv_live_name);
        ImageView ivLive = holder.getView(R.id.iv_live_image);

        tvLiveTitle.setText(item.getName());
        tvLiveName.setText(item.getDetail());

        if (item.getThumb()!=null){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivLive);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivLive);
            }
        }else{
            ivLive.setImageResource(R.drawable.morentouxiang);
        }
    }
}