package sochat.so.com.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.model.BankCardModel;

/**
 * Created by Administrator on 2017/6/16.
 */

public class LLPayBankListAdapter extends BaseAdapter {
    private Context context;
    private List<BankCardModel>lists;
    private LayoutInflater inflater;

    public LLPayBankListAdapter(Context context,List<BankCardModel>lists) {
        this.context = context;
        this.lists =lists;
        inflater = LayoutInflater.from(context);
    }

    public void setLists(List<BankCardModel>lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BankCardModel item= lists.get(position);
        BankViewHolder holder =null;
        if (convertView==null){
            holder = new BankViewHolder();
            convertView =inflater.inflate(R.layout.item_ll_pay_bank_list,null);

           holder.tvBankName = (TextView) convertView.findViewById(R.id.tv_bank_name);
            holder.ivBankIcon = (ImageView) convertView.findViewById(R.id.iv_bank_icon);
            holder.llLayout = (LinearLayout) convertView.findViewById(R.id.ll_pay_layout);
            convertView.setTag(holder);
        }else {
            holder = (BankViewHolder) convertView.getTag();
        }

        holder.tvBankName.setText(item.getBank_name()+"("+item.getCard_no()+")");

        boolean isFindBankICon =false;
            for (int i = 0; i < ConfigInfo.BANK_CODE_ICON.length; i++) {
                if ((ConfigInfo.BANK_CODE_ICON_STRING[i]).contains(item.getBank_code())) {
                    holder.ivBankIcon.setImageResource(ConfigInfo.BANK_CODE_ICON[i]);
                    isFindBankICon = true;
                }

            if (!isFindBankICon) {
                holder.ivBankIcon.setImageResource(R.mipmap.bank_deflaut);
            }
        }

        holder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnChoosedBankCallBack.choosedBankCallBack(item);
            }
        });


        return convertView;
    }



    class BankViewHolder{
        TextView tvBankName;
        ImageView ivBankIcon;
        LinearLayout llLayout;

    }


    private setOnChoosedBankCallBack setOnChoosedBankCallBack;
    public interface setOnChoosedBankCallBack{
        void choosedBankCallBack(BankCardModel item);
    }

    public void setChoosedBankListener(setOnChoosedBankCallBack setOnChoosedBankCallBack){
        this.setOnChoosedBankCallBack =setOnChoosedBankCallBack;
    }

}
