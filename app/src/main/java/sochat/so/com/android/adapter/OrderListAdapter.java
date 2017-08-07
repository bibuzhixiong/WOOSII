package sochat.so.com.android.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.OrderDetialModel;
import sochat.so.com.android.utils.DateUtil;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/6/20.
 */

public class OrderListAdapter extends ListBaseAdapter<OrderDetialModel> {
    private Context context;
    private MyToast toast;

    public OrderListAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_order_list;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        OrderDetialModel item =mDataList.get(position);
        ImageView ivreChargeOrReflect = holder.getView(R.id.iv_recharge_or_reflect);
        TextView tvOrderName = holder.getView(R.id.tv_order_name);
        TextView tvOrderTime = holder.getView(R.id.tv_pay_time);
        TextView tvOrderCash = holder.getView(R.id.tv_pay_money);

        if (item.getType().equals("3")){
            tvOrderName.setText("提现成功");
            ivreChargeOrReflect.setImageResource(R.drawable.jiaoyitixian);
            tvOrderCash.setText("￥"+item.getMoney_order());
            tvOrderCash.setTextColor(0xFF15B422);
        }else{
            tvOrderName.setText("充值成功");
            ivreChargeOrReflect.setImageResource(R.drawable.jiaoyichongzhi);
            tvOrderCash.setText("￥"+item.getMoney_order());
            tvOrderCash.setTextColor(0xFF333333);
        }

            tvOrderTime.setText(DateUtil.getSpecifiedDate(Long.parseLong(item.getAddtime()+"000")));
    }
}
