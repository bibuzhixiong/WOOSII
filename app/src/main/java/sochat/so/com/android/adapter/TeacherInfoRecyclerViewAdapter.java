package sochat.so.com.android.adapter;

import android.app.Activity;
import android.content.Context;
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
import sochat.so.com.android.model.TeacherCourseInfo;
import sochat.so.com.android.model.TeacherInfoModel;

/**
 * Created by Administrator on 2017/3/15.
 */

public class TeacherInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TeacherCourseInfo> teacherCourseInfoList;
    private Context context;
    private LayoutInflater inflater;
    private TeacherInfoModel teacherInfoModel;
    public TeacherInfoRecyclerViewAdapter(List<TeacherCourseInfo>teacherCourseInfoList,TeacherInfoModel teacherInfoModel,Context context) {
        this.teacherCourseInfoList = teacherCourseInfoList;
        this.teacherInfoModel = teacherInfoModel;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 判断是否是头部
     */
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    /**
     *头部的布局
     */
    private View mHeaderView;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    /**
     * 判断加载布局的内容
     */
    @Override
    public int getItemViewType(int position) {
        if(position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    public void setList(ArrayList<TeacherCourseInfo> teacherCourseInfoList){
        this.teacherCourseInfoList = (List<TeacherCourseInfo>) teacherCourseInfoList.clone();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //头部介绍部分
        if(mHeaderView !=null&&viewType ==TYPE_HEADER) return new HeaderHolder(mHeaderView);

        View view;
        view =inflater.inflate(R.layout.item_teacher_info_course,viewGroup,false);
        TeacherInfoHolder holder = new TeacherInfoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) ==TYPE_HEADER) {
            if(viewHolder instanceof HeaderHolder){
                ((HeaderHolder) viewHolder).tvName.setText(teacherInfoModel.getName());
                ((HeaderHolder) viewHolder).tvVedioCount.setText(teacherInfoModel.getVedioCount());
                ((HeaderHolder) viewHolder).tvAttentionCount.setText(teacherInfoModel.getAttentionCount());
                ((HeaderHolder) viewHolder).tvcontent.setText(teacherInfoModel.getContent());

                ((HeaderHolder) viewHolder).ivTopBack.setOnClickListener(myClickListener);
                ((HeaderHolder) viewHolder).ivTopShare.setOnClickListener(myClickListener);
                if (teacherInfoModel.getUrl().isEmpty()){
                    ((HeaderHolder) viewHolder).ivUserPhoto.setImageResource(R.drawable.morentouxiang);
                }else{
                    Picasso.with(context).load(teacherInfoModel.getUrl()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(((HeaderHolder) viewHolder).ivUserPhoto);
                }
            }

        }
        if (getItemViewType(position) ==TYPE_NORMAL) {
            final int pos = getRealPosition(viewHolder);
            final TeacherCourseInfo teacherCourseInfo = teacherCourseInfoList.get(pos);

            if(viewHolder instanceof TeacherInfoHolder){
                ((TeacherInfoHolder) viewHolder).tvTitle.setText(teacherCourseInfo.getTitle());
                ((TeacherInfoHolder) viewHolder).tvAttentionCount.setText(teacherCourseInfo.getAttention_count());
                if (teacherCourseInfo.getThumb_url().isEmpty()){
                    ((TeacherInfoHolder) viewHolder).ivCourseThumb.setImageResource(R.drawable.morentouxiang);
                }else{
                    Picasso.with(context).load(teacherCourseInfo.getThumb_url()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(((TeacherInfoHolder) viewHolder).ivCourseThumb);
                }
            }
        }

    }


    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
        int position = holder.getPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView==null?teacherCourseInfoList.size():teacherCourseInfoList.size()+1;
    }


    class TeacherInfoHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private ImageView ivCourseThumb;
        private TextView tvAttentionCount;
        public TeacherInfoHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_course_name);
            tvAttentionCount = (TextView) itemView.findViewById(R.id.tv_attention_count);
            ivCourseThumb = (ImageView) itemView.findViewById(R.id.iv_course_thumb);
        }
    }
    class HeaderHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private ImageView ivUserPhoto;
        private ImageView ivAttention;
        private ImageView ivTopBack;
        private ImageView ivTopShare;
        private TextView tvAttentionCount;
        private TextView tvVedioCount;
        private TextView tvcontent;
        public HeaderHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvAttentionCount = (TextView) itemView.findViewById(R.id.tv_attention_count);
            tvVedioCount = (TextView) itemView.findViewById(R.id.tv_vedio_count);
            tvcontent = (TextView) itemView.findViewById(R.id.tv_info_content);
            ivUserPhoto = (ImageView) itemView.findViewById(R.id.iv_user_photo);
            ivAttention = (ImageView) itemView.findViewById(R.id.iv_attention);
            ivTopBack = (ImageView) itemView.findViewById(R.id.iv_top_back);
            ivTopShare = (ImageView) itemView.findViewById(R.id.iv_share);
        }
    }


    private MyClickListener myClickListener = new MyClickListener();
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_top_back:
                    if(Activity.class.isInstance(context))
                    {
                        // 转化为activity，然后finish就行了
                        Activity activity = (Activity)context;
                        activity.finish();
                    }
                    break;
                case R.id.iv_share:

                    break;
            }

        }
    }


}
