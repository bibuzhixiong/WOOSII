package sochat.so.com.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.WoosiiEmployeeFoundTeacherModel;
import sochat.so.com.android.utils.CommonUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * Created by Administrator on 2017/6/20.
 */

public class WoosiiEmployeeFoundTeacherAdapter extends ListBaseAdapter<WoosiiEmployeeFoundTeacherModel> {
    private Context context;
    private MyToast toast;

    public WoosiiEmployeeFoundTeacherAdapter(Context context) {
        super(context);
        this.context =context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_woosii_employee_found_teacher;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        WoosiiEmployeeFoundTeacherModel item =mDataList.get(position);
        TextView tvTeacherName = holder.getView(R.id.tv_teacher_name);
        TextView tvTeacherInfo = holder.getView(R.id.tv_teacher_info);
        TextView tvYesterdayIncome = holder.getView(R.id.tv_yesterday_income);
        TextView tvAllIncome = holder.getView(R.id.tv_all_income);
        ImageView ivTeacherPhoto = holder.getView(R.id.iv_teacher_photo);
        int endOne = 5+item.getDay_money().length();
        tvYesterdayIncome.setText(CommonUtils.setTextViewPartColorChange("昨日收益 "+item.getDay_money()+" 元",5,endOne,0xFF15B422));
        int endTwo = 5+item.getMoney().length();
        tvAllIncome.setText(CommonUtils.setTextViewPartColorChange("累计收益 "+item.getMoney()+" 元",5,endTwo,0xFF333333));
        tvTeacherInfo.setText(item.getInfo());
        tvTeacherName.setText(item.getName());

        if (TextUtils.isEmpty(item.getThumb())){
           ivTeacherPhoto.setImageResource(R.drawable.morentouxiang);
        }else{
            Picasso.with(context).load(ConfigInfo.ApiUrl+item.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivTeacherPhoto);
        }
    }
}
