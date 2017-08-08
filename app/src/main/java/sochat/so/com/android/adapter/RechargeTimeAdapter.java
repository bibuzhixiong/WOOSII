package sochat.so.com.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.model.RechargeTimeModel;

/**
 * Created by Administrator on 2017/4/12.
 */

public class RechargeTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<RechargeTimeModel> lists;
    private LayoutInflater inflater;

    public RechargeTimeAdapter(Context context,List<RechargeTimeModel> lists) {
        this.context = context ;
        this.lists =lists;
        inflater = LayoutInflater.from(context);
    }
    public void setList(ArrayList<RechargeTimeModel> childList) {
        this.lists = (ArrayList<RechargeTimeModel>) childList.clone();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recharge,parent,false);
        RechargeViewHolder holder = new RechargeViewHolder(view);
        return holder;
    }

    private int  currentPosition;
    private boolean isClick = false;

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        RechargeTimeModel recharge = lists.get(position);
        if (holder instanceof RechargeViewHolder){
            ((RechargeViewHolder) holder).tvTime.setText(recharge.getTime()+"沃币");
            ((RechargeViewHolder) holder).tvMoney.setText("售价"+recharge.getMoney()+"元");
            Log.i(ConfigInfo.TAG,"position:"+position);

            if (currentPosition ==position && isClick){
                Log.i(ConfigInfo.TAG,"position_isClick:"+position+isClick);
                ((RechargeViewHolder) holder).llLayout.setBackgroundResource(R.drawable.corners_layout_green);
                ((RechargeViewHolder) holder).tvMoney.setTextColor(0xFF15B422);
                ((RechargeViewHolder) holder).tvTime.setTextColor(0xFF15B422);
            }else{
                Log.i(ConfigInfo.TAG,"position_other:"+position);
                ((RechargeViewHolder) holder).llLayout.setBackgroundResource(R.drawable.corners_layout);
                ((RechargeViewHolder) holder).tvTime.setTextColor(0xFF333333);
                ((RechargeViewHolder) holder).tvMoney.setTextColor(0xFF999999);
            }

            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onEnableButton(boolean isEnabled);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    class RechargeViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTime;
        private TextView tvMoney;
        private LinearLayout llLayout;
        public RechargeViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money);
            llLayout = (LinearLayout) itemView.findViewById(R.id.ll_layout);
        }
    }

}
