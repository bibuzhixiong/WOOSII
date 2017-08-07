package sochat.so.com.android.adapter;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.TeacherCourseList;


/**
 * Created by Administrator on 2017/3/21.
 */

public class MyCollectAdapter extends ListBaseAdapter<TeacherCourseList> {
    private Context context;
    /**
     * 用来标记被选中的item
     */
    private boolean visiable ;

    public MyCollectAdapter(Context context) {
        super(context);
        this.context =context;
        visiable =false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_my_collect;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        final TeacherCourseList item = mDataList.get(position);

        TextView tvName = holder.getView(R.id.tv_course_name);
        ImageView ivThumb = holder.getView(R.id.iv_course_thumb);
        final CheckBox cbDelete = holder.getView(R.id.cb_is_delete);
        RelativeLayout rlBg = holder.getView(R.id.rl_bg);

        tvName.setText(item.getV_name());

        if (item.getV_thumb()!=null){
            if (item.getV_thumb().startsWith("http")){
                Picasso.with(context).load(item.getV_thumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+item.getV_thumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(ivThumb);
            }
        }else{
            ivThumb.setImageResource(R.drawable.morentouxiang);
        }

        if (visiable){
            cbDelete.setVisibility(View.VISIBLE);
        }else{
            cbDelete.setVisibility(View.GONE);
        }

//        if (!cbDelete.isChecked()){
//            count.add(mDataList.get(position).getCu_id());
//        }

        cbDelete.setChecked(item.isSelect());
        cbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setSelect(cbDelete.isChecked());
                mOnItemListener.checkBoxClick(position);
            }
        });



    }

    public void setCheckBoxVisiable(boolean visiable){
        this.visiable = visiable;
    }
    public boolean getCheckBoxVisiable(){
        return visiable;
    }

//    private ArrayList<String> count;
//    public ArrayList<String> getDeleteItemPosition(){
//        return count;
//    }


    private OnItemListener mOnItemListener;

    public void setOnItemListener(OnItemListener onItemListener){
        this.mOnItemListener = onItemListener;
    }



    public void remove(TeacherCourseList collectVedioModel){
        mDataList.remove(collectVedioModel);
    }

    public TeacherCourseList getItem(int pos){
        return mDataList.get(pos);
    }
}