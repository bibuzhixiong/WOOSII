package sochat.so.com.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.CourseChild;
import sochat.so.com.android.model.CouurseInfoResult;
import sochat.so.com.android.utils.HttpUtils;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Administrator on 2017/3/13.
 */

public class SchoolRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<CourseChild>teachers;
    private LayoutInflater inflater;
    private Context context;
    private int type;
    private int pindex;
    private int psize;
    /**
     * 课程的标识
     */
    private int flag;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    refreshLists();
                    break;
            }
        }
    };

    private CouurseInfoResult courseInfoResult;
    private void getData() {
        type = flag+1;
        pindex = 1;
        psize = 10;
        String url = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/school?type="+type+"&pindex="+pindex+"&psize="+psize;
        HttpUtils.doGetAsyn((Activity) context,false,url, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                Gson gson =new Gson();
                courseInfoResult = gson.fromJson(result,CouurseInfoResult.class);
                teachers = courseInfoResult.getChilds();
                handler.sendEmptyMessage(0);
            }
        });
    }


    public SchoolRecyclerViewAdapter(int flag,Context context){
        this.context = context;
        this.flag =flag;
        inflater = LayoutInflater.from(context);
        teachers = new ArrayList<CourseChild>();
        Log.i(ConfigInfo.TAG,"这里是构造方法:"+teachers.toString());
        getData();
    }

    public void refreshLists(){
//        this.teachers = (List<CourseChild>) teachers.clone();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_middle_school,viewGroup,false);
        SchoolViewHolder holder = new SchoolViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        CourseChild teacher = teachers.get(i);

        if (viewHolder instanceof SchoolViewHolder){
            ((SchoolViewHolder) viewHolder).tvName.setText(teacher.getName());
            ((SchoolViewHolder) viewHolder).tvContent.setText(teacher.getDetail());
            ((SchoolViewHolder) viewHolder).tvCourseCount.setText(teacher.getC_video());
//            ((SchoolViewHolder) viewHolder).tvAttentionCount.setText(teacher.getAttentionCount());
            if(teacher.getThumb().startsWith("http")){
                Picasso.with(context).load(teacher.getThumb()).memoryPolicy(NO_CACHE, NO_STORE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(((SchoolViewHolder) viewHolder).ivPhoto);
                Log.i(ConfigInfo.TAG,"地址："+ConfigInfo.ApiUrl+teacher.getThumb());
            }
            Picasso.with(context).load(ConfigInfo.ApiUrl+teacher.getThumb()).memoryPolicy(NO_CACHE, NO_STORE).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(((SchoolViewHolder) viewHolder).ivPhoto);
            Log.i(ConfigInfo.TAG,"teacher_url:"+teacher.getThumb());
        }



    }

    @Override
    public int getItemCount() {
        if (teachers ==null){
            return 0;
        }
        return teachers.size();//刚进来时这里的teachers为Null???????????????????????????????
    }


    class SchoolViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivPhoto;
        private TextView tvName;
        private TextView tvContent;
        private TextView tvCourseCount;
        private TextView tvAttentionCount;
        private ImageView ivAttention;


        public SchoolViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvCourseCount = (TextView) itemView.findViewById(R.id.tv_course_count);
            tvAttentionCount = (TextView) itemView.findViewById(R.id.tv_attention_count);
            ivAttention = (ImageView) itemView.findViewById(R.id.iv_attention);

        }
    }
}
