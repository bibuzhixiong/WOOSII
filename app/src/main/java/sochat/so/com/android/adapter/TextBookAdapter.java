package sochat.so.com.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.eventbus.BackTextBookEvent;
import sochat.so.com.android.model.TextBookModel;
import sochat.so.com.android.utils.DemoHelper;

/**
 * Created by Administrator on 2017/4/25.
 */

public class TextBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<TextBookModel> lists;
    private Context context;

    public TextBookAdapter(List<TextBookModel> lists,Context context){
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void setLists(ArrayList<TextBookModel> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_textbook,parent,false);
        TextBookHolderView holder = new TextBookHolderView(view);
        return holder;
    }

    private int  currentPosition;
    private boolean isClick = false;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final TextBookModel item = lists.get(position);
        if (holder instanceof TextBookHolderView){
            ((TextBookHolderView) holder).tvTextBook.setText(item.getV_name());

            if (currentPosition ==position && isClick){
                Log.i(ConfigInfo.TAG,"position_isClick:"+position+isClick);
                ((TextBookHolderView) holder).tvTextBook.setBackgroundResource(R.drawable.corners_layout_textbook_pressed);
            }else{
                Log.i(ConfigInfo.TAG,"position_other:"+position);
                ((TextBookHolderView) holder).tvTextBook.setBackgroundResource(R.drawable.corners_layout_textbook_normal);
            }

            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DemoHelper.setTextBook(item.getV_name());
                        DemoHelper.setTextBook_id(item.getV_id());
                        EventBus.getDefault().post(new BackTextBookEvent(item.getV_name(),item.getV_id()));
                        isClick =true;
                        int pos = holder.getLayoutPosition();
                        Log.i(ConfigInfo.TAG,"pos:"+pos);
                        currentPosition = pos;
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                        mOnItemClickLitener.onEnableButton(true);
                        notifyDataSetChanged();

                    }
                });
            }

        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


    class TextBookHolderView extends RecyclerView.ViewHolder{
        private TextView tvTextBook;

        public TextBookHolderView(View itemView) {
            super(itemView);
            tvTextBook = (TextView) itemView.findViewById(R.id.tv_textbook);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onEnableButton(boolean isEnabled);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
