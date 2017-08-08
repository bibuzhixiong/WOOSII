package sochat.so.com.android.adapter.help_adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import sochat.so.com.android.R;
import sochat.so.com.android.model.CourseChild;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Lzx on 2016/12/30.
 */

public class DataAdapter extends ListBaseAdapter<CourseChild> {
    private Context context;
    private MyToast toast;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    toast.makeShortToast(context,"关注失败");
                    break;
                case 1:
                    toast.makeShortToast(context,"关注成功");
                    break;
                case 2:
                    toast.makeShortToast(context,"已经关注");
                    break;
            }
        }
    };

    public DataAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_middle_school;
    }


    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final CourseChild item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvCourseInfo = holder.getView(R.id.tv_content);
        TextView tvCourseCount = holder.getView(R.id.tv_course_count);
        TextView tvCourseAttentionCount = holder.getView(R.id.tv_attention_count);
        ImageView ivThumb = holder.getView(R.id.iv_photo);
        final ImageView ivAttention = holder.getView(R.id.iv_attention);

        if (item.getCode()==0){
            ivAttention.setImageResource(R.drawable.guanzhu);
        }else{
            ivAttention.setImageResource(R.drawable.yiguanzhu);
        }
        ivAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url  = ConfigInfo.ApiUrl + "/index.php/Vr/Vlive/add_attention?user_id=" + DemoHelper.getUid() + "&teacher_id=" + item.getUser_id();
                    Log.i(ConfigInfo.TAG, "code0:" + url);
                    HttpUtils.doGetAsyn((Activity) context, true, url, handler, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {//失败
                                    handler.sendEmptyMessage(0);
                                } else if (code == 1) {//成功,这里面是不能用来更新UI的
                                    ivAttention.post(new Runnable(){
                                        @Override
                                        public void run() {
                                            ivAttention.setImageResource(R.drawable.yiguanzhu);
                                        }

                                    });
                                    handler.sendEmptyMessage(1);
                                } else if (code == 2) {
                                    handler.sendEmptyMessage(2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

        });
//        Log.i(ConfigInfo.TAG,"DataAdapter_getDataList().toString():"+getDataList().toString());
        tvName.setText(item.getName());
        tvCourseInfo.setText(item.getDetail());
        tvCourseCount.setText(item.getC_video()+"  视频");
        tvCourseAttentionCount.setText(item.getFollow()+"  关注");

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