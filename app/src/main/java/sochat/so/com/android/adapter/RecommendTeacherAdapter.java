package sochat.so.com.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.TeacherInfoActivity;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.RecommendTeacherModel;
import sochat.so.com.android.utils.CommonUtils;

/**
 * Created by Administrator on 2017/4/25.
 */

public class RecommendTeacherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<RecommendTeacherModel>lists;
    private Context context;

    public RecommendTeacherAdapter(List<RecommendTeacherModel>lists, Context context){
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);

    }

    public void setLists(ArrayList<RecommendTeacherModel>lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recomment_teacher,parent, false);
        RecommendTeacherViewHolder holder= new RecommendTeacherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecommendTeacherModel item = lists.get(position);
    if (holder instanceof RecommendTeacherViewHolder){
        ((RecommendTeacherViewHolder) holder).tvTeacher.setText(item.getReal_name());

        if (item.getThumb()!=null&&item.getThumb().length()>3){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(((RecommendTeacherViewHolder) holder).ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(((RecommendTeacherViewHolder) holder).ivThumb);
            }
        }else{
            ((RecommendTeacherViewHolder) holder).ivThumb.setImageResource(R.drawable.app_icon);
        }
((RecommendTeacherViewHolder) holder).ivThumb.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, TeacherInfoActivity.class);
        intent.putExtra("teacher_uid",item.getUser_id());
        CommonUtils.startActivity(context,intent);
    }
});

    }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    class RecommendTeacherViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivThumb;
        private TextView tvTeacher;

        public RecommendTeacherViewHolder(View itemView) {
            super(itemView);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_teacher_photo);
            tvTeacher = (TextView) itemView.findViewById(R.id.tv_teacher_name);
        }
    }

}
