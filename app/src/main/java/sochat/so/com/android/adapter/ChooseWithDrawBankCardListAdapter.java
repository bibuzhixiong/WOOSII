package sochat.so.com.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.model.BankCardModel;

/**
 * 银行卡列表（这是银行卡提现列表Activity）
 * Created by Administrator on 2017/5/23.
 */
public class ChooseWithDrawBankCardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BankCardModel>lists;
    private LayoutInflater inflater;
    private String flag;

    public ChooseWithDrawBankCardListAdapter(Context context, List<BankCardModel>lists,String flag) {
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
        this.flag = flag;
    }

    public void setLists(ArrayList<BankCardModel>lists){
        this.lists = (List<BankCardModel>) lists.clone();
        Log.i(ConfigInfo.TAG,"setLists:"+lists.toString());
        notifyDataSetChanged();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_ll_pay_bank_withdraw,parent,false);
        BankCardListViewHolder holder = new BankCardListViewHolder(view);

        return holder;
    }



    private boolean isFindBankICon =false;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final BankCardModel item = lists.get(position);
        if (holder instanceof BankCardListViewHolder){
//           if (LLPayRegexUtil.checkMatchChina("(北京|招商|工商|广发|华夏|招商|中国银行|中信)",item.getBank_name())){
//               ((BankCardListViewHolder) holder).rlLayout.setBackgroundResource(R.drawable.changtiaohong);
//           }else if (LLPayRegexUtil.checkMatchChina("(建设|民生|交通|浦发|兴业)",item.getBank_name())){
//               ((BankCardListViewHolder) holder).rlLayout.setBackgroundResource(R.drawable.changtiaolan);
//           }else if (LLPayRegexUtil.checkMatchChina("(农业|邮政|储蓄)",item.getBank_name())){
//               ((BankCardListViewHolder) holder).rlLayout.setBackgroundResource(R.drawable.changtiaolv);
//           }else if (LLPayRegexUtil.checkMatchChina("(广发|上海|平安|光大）",item.getBank_name())){
//               ((BankCardListViewHolder) holder).rlLayout.setBackgroundResource(R.drawable.changtiaozi);
//           }else{
//               ((BankCardListViewHolder) holder).rlLayout.setBackgroundResource(R.drawable.changtiaohong);
//           }

           if (item.getCard_no().equals(flag)){
              ((BankCardListViewHolder) holder).ivCurrentBank.setVisibility(View.VISIBLE);
           }else{
               ((BankCardListViewHolder) holder).ivCurrentBank.setVisibility(View.GONE);
           }

           for (int i = 0; i<ConfigInfo.BANK_CODE_ICON.length;i++){
               if ((ConfigInfo.BANK_CODE_ICON_STRING[i]).contains(item.getBank_code())){
                   ((BankCardListViewHolder) holder).ivBankCardIcon.setImageResource(ConfigInfo.BANK_CODE_ICON[i]);
                   isFindBankICon =true;
               }
           }
           if (!isFindBankICon){
               ((BankCardListViewHolder) holder).ivBankCardIcon.setImageResource(R.mipmap.bank_deflaut);
           }

            ((BankCardListViewHolder) holder).tvBankCardName.setText(item.getBank_name());
            if (item.getCard_type().equals("2")){
                ((BankCardListViewHolder) holder).tvBankCardType.setText("储蓄卡");
            }else if (item.getCard_type().equals("3")){
                ((BankCardListViewHolder) holder).tvBankCardType.setText("信用卡");
            }else{
                ((BankCardListViewHolder) holder).tvBankCardType.setText("银联卡");
            }

            ((BankCardListViewHolder) holder).tvBankCardLastFour.setText("尾号"+item.getCard_no());


            ((BankCardListViewHolder) holder).rlLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(position,item);
                }
            });



        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class BankCardListViewHolder extends RecyclerView.ViewHolder{
        private TextView tvBankCardName;
        private TextView tvBankCardType;
        private TextView tvBankCardLastFour;
        private RelativeLayout rlLayout;
        private ImageView ivBankCardIcon;
        private ImageView ivCurrentBank;


        public BankCardListViewHolder(View itemView) {
            super(itemView);
            tvBankCardName = (TextView) itemView.findViewById(R.id.tv_bank_name);
            tvBankCardType = (TextView) itemView.findViewById(R.id.tv_bank_type);
            tvBankCardLastFour = (TextView) itemView.findViewById(R.id.tv_bank_no_four);
            rlLayout = (RelativeLayout) itemView.findViewById(R.id.rl_layout);
            ivBankCardIcon = (ImageView) itemView.findViewById(R.id.iv_bank_icon);
            ivCurrentBank = (ImageView) itemView.findViewById(R.id.iv_current_bank);

        }
    }

    private setOnItemCilckListener listener;
   public interface setOnItemCilckListener{
       void onItemClickListener(int position, BankCardModel item);
   }
   public void setListener(setOnItemCilckListener listener){
       this.listener =listener;
   }


}
