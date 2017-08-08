package sochat.so.com.android.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wevey.selector.dialog.DialogInterface;
import com.wevey.selector.dialog.NormalAlertDialog;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.AttentionModel;

/**
 * Created by Lzx on 2016/12/30.
 */

public class AttentionAdapter extends ListBaseAdapter<AttentionModel> {
    private Context context;

    public AttentionAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_attention_teacher;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        AttentionModel item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_name);
        TextView tvCourseInfo = holder.getView(R.id.tv_content);
        TextView tvCourseCount = holder.getView(R.id.tv_course_count);
        TextView tvCourseAttentionCount = holder.getView(R.id.tv_attention_count);
        ImageView ivThumb = holder.getView(R.id.iv_photo);
        ImageView ivShengLue = holder.getView(R.id.iv_shenglue);

        tvName.setText(item.getName());
        tvCourseInfo.setText(item.getDetail());
        tvCourseCount.setText(item.getC_video()+"  视频");
        tvCourseAttentionCount.setText(item.getFollow()+"  关注");
        ivShengLue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NormalAlertDialog.Builder(mContext).setTitleVisible(false)
                        .setTitleText("温馨提示")
                        .setTitleTextColor(R.color.black_light)
                        .setContentText("是否取消关注？")
                        .setContentTextColor(R.color.black_light)
                        .setLeftButtonText("否")
                        .setLeftButtonTextColor(R.color.gray)
                        .setRightButtonText("是")
                        .setRightButtonTextColor(R.color.black_light)
                        .setOnclickListener(new DialogInterface.OnLeftAndRightClickListener<NormalAlertDialog>() {
                            @Override
                            public void clickLeftButton(NormalAlertDialog dialog, View view) {
                                dialog.dismiss();
                            }

                            @Override
                            public void clickRightButton(NormalAlertDialog dialog, View view) {
                                onCancelAttentionListener.cancelAttention(position);
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();

            }
        });

        if (item.getThumb()!=null){
            if (item.getThumb().startsWith("http")){
                Picasso.with(context).load(item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }

    }

    private setOnCancelAttentionListener onCancelAttentionListener;
    public interface  setOnCancelAttentionListener{
        void cancelAttention(int position);
    }
    public void setOnCancelAttentionListener(setOnCancelAttentionListener onCancelAttentionListener){
        this.onCancelAttentionListener = onCancelAttentionListener;
    }
}