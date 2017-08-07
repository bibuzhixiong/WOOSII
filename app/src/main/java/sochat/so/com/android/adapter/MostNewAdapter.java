package sochat.so.com.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.PlayVedioActivity;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.TeacherCourseList;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;
import sochat.so.com.android.view.BottomPushPopupWindow;

/**
 * Created by Administrator on 2017/4/25.
 */

public class MostNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<TeacherCourseList>lists;
    private Context context;

    private String pay_cu_id;
    private TeacherCourseList currentClickItem;
    private int time;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    MyToast.makeShortToast(context,"购买失败，请稍后重试");
                    bottomPopupWindow.dismiss();
                    break;
                case 1:
                    Intent intent ;
                    if (currentClickItem.getVr()==0){
                        intent = new Intent(context,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(context,PlayVedioActivity.class);
                    }
                    intent.putExtra("vedio_info",currentClickItem);
                    CommonUtils.startActivity(context,intent);
                    MyToast.makeShortToast(context,"购买成功");
                    bottomPopupWindow.dismiss();
                    break;
                case 2:
                    MyToast.makeShortToast(context,"您的时间余额不足，请先去充值");
                    bottomPopupWindow.dismiss();
                    break;
            }
        }
    };

    public MostNewAdapter(List<TeacherCourseList>lists, Context context){
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);

        bottomPopupWindow = new BottomPopupWindow(context);

    }

    public void setLists(ArrayList<TeacherCourseList> lists){
        this.lists = lists;
        Log.i(ConfigInfo.TAG, "最新:" + lists.toString());
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_mosthot,parent, false);
        MostHotViewHolder holder= new MostHotViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TeacherCourseList item = lists.get(position);
        if (holder instanceof MostHotViewHolder){
            ((MostHotViewHolder) holder).tvTeacher.setText("讲师："+item.getReal_name());
            ((MostHotViewHolder) holder).tvTitle.setText(item.getV_name());
//            ((MostHotViewHolder) holder).tvTimeLong.setText(item.getLongtime());
            if (!TextUtils.isEmpty(item.getV_thumb())){
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getV_thumb()).error(R.drawable.jiazaicuowu).into(((MostHotViewHolder) holder).ivThumb);
            }else{
                ((MostHotViewHolder) holder).ivThumb.setImageResource(R.drawable.app_icon);
            }
            if (item.getVip() ==0){//免费
                ((MostHotViewHolder) holder).ivJingPin.setVisibility(View.INVISIBLE);
                ((MostHotViewHolder) holder).tvTimeLong.setVisibility(View.INVISIBLE);
                ((MostHotViewHolder) holder).tvFree.setVisibility(View.VISIBLE);
            }else{//付费
                ((MostHotViewHolder) holder).ivJingPin.setVisibility(View.VISIBLE);
                ((MostHotViewHolder) holder).tvTimeLong.setVisibility(View.VISIBLE);
                ((MostHotViewHolder) holder).tvFree.setVisibility(View.INVISIBLE);
                if (item.getFree() ==0){
                    ((MostHotViewHolder) holder).tvTimeLong.setText("¥"+item.getLongtime()+"沃币");
                }else{
                    ((MostHotViewHolder) holder).tvTimeLong.setText("已付费");
                }
            }

            ((MostHotViewHolder) holder).ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("TTT",item+"---Mark");
                    currentClickItem = item;
                    Intent intent ;
                    if (item.getVr()==0){
                        intent = new Intent(context,PlayVedioActivity.class);
                    }else{
                        intent = new Intent(context,PlayVedioActivity.class);
                    }
                    pay_cu_id = currentClickItem.getCu_id();
                    time = currentClickItem.getLongtime();

                    Log.i(ConfigInfo.TAG,"MostHotAdapter_getvip:"+currentClickItem.getVip());
                    if (item.getVip() ==0){//免费
                        intent.putExtra("vedio_info",currentClickItem);
                        CommonUtils.startActivity(context,intent);
                    }else{//付费
                        Log.i(ConfigInfo.TAG,"MostHotAdapter_getFree:"+currentClickItem.getFree());

                        if (item.getFree() ==0){
                            bottomPopupWindow.show((Activity) context);
                        }else if(item.getFree() ==1){
                            intent.putExtra("vedio_info",currentClickItem);
                            CommonUtils.startActivity(context,intent);
                        }
                    }
                }
            });






        }
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    class MostHotViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivThumb;
        private TextView tvTitle;
        private TextView tvTeacher;
        private ImageView ivJingPin;
        private TextView tvTimeLong;
        private TextView tvFree;

        public MostHotViewHolder(View itemView) {
            super(itemView);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            ivJingPin = (ImageView) itemView.findViewById(R.id.iv_jingping);
            tvTeacher = (TextView) itemView.findViewById(R.id.tv_teacher);
            tvTimeLong = (TextView) itemView.findViewById(R.id.tv_time_long);
            tvFree = (TextView) itemView.findViewById(R.id.tv_free);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    private BottomPopupWindow bottomPopupWindow;

    private class BottomPopupWindow extends BottomPushPopupWindow<Void> {

        public BottomPopupWindow(Context context) {
            super(context, null);
        }

        @Override
        protected View generateCustomView(Void o) {
            View bottom = View.inflate(mBottomPopupWindowContext,R.layout.bottom_layout_pay,null);
            TextView tvCommit = (TextView) bottom.findViewById(R.id.tv_commit);
            TextView tvCancel = (TextView) bottom.findViewById(R.id.tv_cancel);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomPopupWindow.dismiss();
                }
            });


            tvCommit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toPayVedio();
                    bottomPopupWindow.dismiss();
                }
            });

            return bottom;
        }
    }

    private void toPayVedio() {
        String payUrl = ConfigInfo.ApiUrl+"/index.php/Vr/Vlive/dividedSystem?user_id="+ DemoHelper.getUid()+"&cu_id="+pay_cu_id+"&region_id="+DemoHelper.getRegion_id()+"&time="+time;
        HttpUtils.doGetAsyn((Activity) context, true, payUrl, handler, new HttpUtils.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    Log.i(ConfigInfo.TAG,"这里是Code:"+result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = jsonObject.getInt("code");
                    if (code ==0){//失败
                        handler.sendEmptyMessage(0);
                    }else if (code ==1){//成功
                        handler.sendEmptyMessage(1);
                    }else if(code ==2){//时间不足
                        handler.sendEmptyMessage(2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
