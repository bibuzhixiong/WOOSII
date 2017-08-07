package sochat.so.com.android.adapter.help_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.model.CourseModel1;
import sochat.so.com.android.utils.CourseTitleJson2Model1;

/**
 * Created by Administrator on 2017/4/25.
 */

public class CourseTitleAdapterMore extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<CourseModel1> lists;
    private Context context;

    public CourseTitleAdapterMore(List<CourseModel1> lists, Context context){
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);

    }

    public void setLists(List<CourseModel1> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_popwindow_course_title,parent,false);
        MoreHolderView holder = new MoreHolderView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final CourseModel1 item = lists.get(position);
        if (holder instanceof MoreHolderView){
            ((MoreHolderView) holder).tvTextBook.setText(item.getS_name());

            ((MoreHolderView) holder).tvTextBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CourseTitleJson2Model1.setAddCourseTitleArrayList1(item.getS_id(),item.getS_name());
                    mOnItemClickLitener.onItemClick(item.getS_id(),item.getS_name());
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    class MoreHolderView extends RecyclerView.ViewHolder{
        private TextView tvTextBook;

        public MoreHolderView(View itemView) {
            super(itemView);
            tvTextBook = (TextView) itemView.findViewById(R.id.tv_textbook);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(String s_id, String s_name);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


}
