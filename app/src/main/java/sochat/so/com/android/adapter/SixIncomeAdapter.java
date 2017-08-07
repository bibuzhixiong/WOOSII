package sochat.so.com.android.adapter;

import android.content.Context;
import android.widget.TextView;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.SixPirtyIncomeModel;
import sochat.so.com.android.utils.DateUtil;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/6/20.
 */

public class SixIncomeAdapter extends ListBaseAdapter<SixPirtyIncomeModel> {
    private Context context;
    private MyToast toast;

    public SixIncomeAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_six_income;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        SixPirtyIncomeModel item =mDataList.get(position);
        TextView tvOrderName = holder.getView(R.id.tv_order_name);
        TextView tvIncomePrecent = holder.getView(R.id.tv_income_precent);
        TextView tvOrderTime = holder.getView(R.id.tv_pay_time);
        TextView tvOrderCash = holder.getView(R.id.tv_pay_money);

            tvOrderTime.setText(DateUtil.getSpecifiedDate(Long.parseLong(item.getAddtime()+"000")));
            tvOrderName.setText("收益");
            tvIncomePrecent.setText("("+item.getPrecent()+"%分成收益)");
            tvOrderCash.setText("+"+item.getMoney());

    }
}
