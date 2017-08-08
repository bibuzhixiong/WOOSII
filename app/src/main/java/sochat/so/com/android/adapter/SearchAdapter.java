package sochat.so.com.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.SearchBean;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SearchAdapter extends ListBaseAdapter<SearchBean> {
    private Context context;

    public SearchAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_search_teacher;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        SearchBean item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvCourseInfo = holder.getView(R.id.tv_content);
        TextView tvCourseCount = holder.getView(R.id.tv_course_count);
        TextView tvCourseAttentionCount = holder.getView(R.id.tv_attention_count);
        ImageView ivThumb = holder.getView(R.id.iv_photo);
        ImageView ivAttention = holder.getView(R.id.iv_attention);

        ivAttention.setVisibility(View.GONE);
        tvName.setText(item.getName());
        tvCourseInfo.setText(item.getDetail());
        tvCourseCount.setText(item.getC_video());
        tvCourseAttentionCount.setText(item.getFollow());

        if (item.getThumb()!=null){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }
    }
}
