package sochat.so.com.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.LiveAttentionResult;
import sochat.so.com.android.model.LiveAttrentionModel;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Lzx on 2016/12/30.
 */

public class LiveAttentionAdapter extends ListBaseAdapter<LiveAttrentionModel> {
     Activity context;

    public LiveAttentionAdapter(Activity context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_live_attention;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {

        final LiveAttrentionModel item = mDataList.get(position);

        ImageView sq_layout = holder.getView(R.id.sq_layout);
        ImageView iv_creator_photo = holder.getView(R.id.iv_creator_photo);

        ImageView iv_is_living = holder.getView(R.id.iv_is_living);
      final  ImageView iv_is_attention = holder.getView(R.id.iv_is_attention);

        TextView tv_person_count = holder.getView(R.id.tv_person_count);
        TextView tv_liver_name = holder.getView(R.id.tv_liver_name);
        TextView tv_is_attention = holder.getView(R.id.tv_is_attention);


        tv_liver_name.setText(item.getName());
        tv_person_count.setText(item.getPerson_count()+" 万观看");
        if (item.getThumb()!=null){
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.avatar_def).into(sq_layout);
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.avatar_def).into(iv_creator_photo);
        }else{
            sq_layout.setImageResource(R.drawable.morentouxiang);
            iv_creator_photo.setImageResource(R.drawable.morentouxiang);
        }

        if (item.getState_id()==3){
            iv_is_living.setImageResource(R.drawable.zhibozhong);
        }else{
            iv_is_living.setImageResource(R.drawable.zanweikaino);
        }

        if (item.getFoll()==0){
            iv_is_attention.setImageResource(R.drawable.jiaguanzhuhuang);
            tv_is_attention.setText("未关注");
        }else{
            iv_is_attention.setImageResource(R.drawable.zhiboyiguanzhu);
            tv_is_attention.setText("已关注");
        }

      final  Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        MyToast.makeShortToast(context,"已取消关注");
                        iv_is_attention.setImageResource(R.drawable.jiaguanzhuhuang);
                      item.setFoll(0);
                        break;
                    case 1:
                        MyToast.makeShortToast(context,"已关注");
                        iv_is_attention.setImageResource(R.drawable.zhiboyiguanzhu);
                        item.setFoll(1);
                        break;
                }
            }
        };
        iv_is_attention.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            if(item.getFoll()==1){
                final String url = ConfigInfo.ApiUrl + "index.php/Api/SchoolManger/cancel_attention?user_id=" + DemoHelper.getUid() + "&school_id=" + item.getSchool_id();
                HttpUtils.doGetAsyn(context, false, url, null, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        handler.sendEmptyMessage(0);

                    }
                });
            }else{
                final String url = ConfigInfo.ApiUrl+"index.php/Api/SchoolManger/add_attention?user_id="+ DemoHelper.getUid()+"&school_id="+item.getSchool_id();
                HttpUtils.doGetAsyn(context,false,url, null, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        }
    });




    }

}