package sochat.so.com.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.PlayVedioComment;

/**
 * Created by Administrator on 2017/2/20.
 */

public class PlayVedioCommitRecycleViewAdapter extends ListBaseAdapter<PlayVedioComment> {
    private Context context;

    public PlayVedioCommitRecycleViewAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_play_vedio_comment;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        PlayVedioComment item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvTime = holder.getView(R.id.tv_time);
        TextView tvContent = holder.getView(R.id.tv_content);
        ImageView ivThumb = holder.getView(R.id.iv_user_photo);

        tvName.setText(item.getName());
        tvTime.setText(item.getAddtime());
        tvContent.setText(item.getContent());

        if (!TextUtils.isEmpty(item.getThumb())&&item.getThumb().length()>3){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.morentouxiang).error(R.drawable.morentouxiang).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }
    }
}