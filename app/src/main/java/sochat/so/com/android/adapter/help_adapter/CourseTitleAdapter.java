package sochat.so.com.android.adapter.help_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.CourseModel;
import sochat.so.com.android.utils.CourseTitleJson2Model;

/**
 * Created by Administrator on 2017/4/25.
 */

public class CourseTitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<CourseModel> courselists;
    private Context context;
    private boolean isEdit =false;
    private String currentTitle ="";

    public CourseTitleAdapter(List<CourseModel> courselists, Context context,int currentPosition){
        this.context = context;
        this.courselists = courselists;
        inflater = LayoutInflater.from(context);
        currentTitle = courselists.get(currentPosition).getS_name();

    }

    /**
     * 更新数据
     * @param courselists
     */
    public void setLists(List<CourseModel> courselists){
        this.courselists = courselists;
        Log.i(ConfigInfo.TAG,"notifyDataSetChanged()是否会改变courselists长度:"+courselists.size());
        notifyDataSetChanged();
    }

    /**
     * 更新RecyclerView
     */
    public void updateRecyclerView(boolean isEdit){
        this.isEdit = isEdit;
//        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_popwindow_course_title,parent,false);
        TextBookHolderView holder = new TextBookHolderView(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final CourseModel item = courselists.get(position);
        if (holder instanceof TextBookHolderView){
            ((TextBookHolderView) holder).tvTextBook.setText(item.getS_name());
            ((TextBookHolderView) holder).tvTextBook.setTextColor(0xFF333333);


            if (isEdit){
                if (currentTitle.equals(item.getS_name())){
                    ((TextBookHolderView) holder).ivDelete.setVisibility(View.GONE);
                    ((TextBookHolderView) holder).tvTextBook.setTextColor(0xFF15B422);
                }else{
                    ((TextBookHolderView) holder).ivDelete.setVisibility(View.VISIBLE);
                    //在编辑状态才能产生点击事件进行删除
                    ((TextBookHolderView) holder).tvTextBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CourseTitleJson2Model.setRemoveCourseTitleArrayList(item.getS_name());
                            onClickItemToUpDate.updateTitle();
                        }
                    });
                }

            }else{
                if (currentTitle.equals(item.getS_name())){
                    ((TextBookHolderView) holder).tvTextBook.setTextColor(0xFF15B422);
                }
                ((TextBookHolderView) holder).ivDelete.setVisibility(View.GONE);
                //在非编辑状态才能产生点击事件进行跳转
                ((TextBookHolderView) holder).tvTextBook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickItemToUpDate.setCurrentTitle(position);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return courselists.size();
    }


    class TextBookHolderView extends RecyclerView.ViewHolder{
        private TextView tvTextBook;
        private ImageView ivDelete;

        public TextBookHolderView(View itemView) {
            super(itemView);
            tvTextBook = (TextView) itemView.findViewById(R.id.tv_textbook);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
        }
    }

    private OnClickItemToUpDate onClickItemToUpDate;
    public interface OnClickItemToUpDate{
        void setCurrentTitle(int position);
        void updateTitle();
    }
    public void setOnClickItemToUpDate(OnClickItemToUpDate onClickItemToUpDate){
        this.onClickItemToUpDate =onClickItemToUpDate;
    }

}
