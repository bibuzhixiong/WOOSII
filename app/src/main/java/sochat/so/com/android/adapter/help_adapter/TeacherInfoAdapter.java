package sochat.so.com.android.adapter.help_adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.TeacherCourseList;

/**
 * Created by Lzx on 2016/12/30.
 */

public class TeacherInfoAdapter extends ListBaseAdapter<TeacherCourseList> {
    private Context context;

    public TeacherInfoAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_teacher_info_course;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        TeacherCourseList item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_course_name);
        TextView tvFree = holder.getView(R.id.tv_free);
        TextView tvTimeLong = holder.getView(R.id.tv_time_long);
        TextView tvAttentionCount = holder.getView(R.id.tv_attention_count);
        ImageView ivThumb = holder.getView(R.id.iv_course_thumb);
        ImageView ivJingping = holder.getView(R.id.iv_jingping);

        tvName.setText(item.getV_name());
        tvAttentionCount.setText(item.getClick_count()+"观看");

        if (item.getVip() == 0){//免费
            ivJingping.setVisibility(View.INVISIBLE);
            tvFree.setVisibility(View.VISIBLE);
            tvTimeLong.setVisibility(View.INVISIBLE);
        }else if (item.getVip() ==1){//付费
            ivJingping.setVisibility(View.VISIBLE);
            tvFree.setVisibility(View.INVISIBLE);
            tvTimeLong.setVisibility(View.VISIBLE);
            if (item.getFree() ==0){
                tvTimeLong.setText("¥"+item.getLongtime()+"沃币");
            }else{
                tvTimeLong.setText("已付费");
            }
        }

        if (item.getV_thumb()!=null){
            if (item.getV_thumb().startsWith("http")){
                Picasso.with(context).load(item.getV_thumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getV_thumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }
    }
}