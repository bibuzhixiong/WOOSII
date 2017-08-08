package sochat.so.com.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sochat.so.com.android.R;
import sochat.so.com.android.activity.WoosiiEmployeeFoundTeacherActivity;
import sochat.so.com.android.adapter.help_adapter.ListBaseAdapter;
import sochat.so.com.android.adapter.help_adapter.SuperViewHolder;
import sochat.so.com.android.model.EmployeeModel;
import sochat.so.com.android.utils.CommonUtils;

/**
 * Created by Administrator on 2017/4/17.
 */

public class EmployeeAdapter  extends ListBaseAdapter<EmployeeModel> {
    private Context context;
    private Intent intent ;

    public EmployeeAdapter(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_employee;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        final EmployeeModel employee = mDataList.get(position);
        View contentView = holder.getView(R.id.swipe_content);
        TextView name = holder.getView(R.id.tv_name);
        TextView tel = holder.getView(R.id.tv_tel);
        ImageView ivPhoto = holder.getView(R.id.iv_employee_photo);
        Button btnDelete = holder.getView(R.id.btnDelete);
        Button btnUnRead = holder.getView(R.id.btnUnRead);
        Button btnTop = holder.getView(R.id.btnTop);

        name.setText(employee.getName());
        tel.setText("电话："+employee.getTel());
        if (employee.getThumb()!=null){
            if (employee.getThumb().startsWith("http")){
                Picasso.with(context).load(employee.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhoto);
            }else{
                Picasso.with(context).load(ConfigInfo.ApiUrl+employee.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.morentouxiang).into(ivPhoto);
            }
        }else{
            ivPhoto.setImageResource(R.drawable.morentouxiang);
        }

        //这句话关掉IOS阻塞式交互效果 并依次打开左滑右滑
//        ((SwipeMenuView)holder.itemView).setIos(false).setLeftSwipe(position % 2 == 0 ? true : false);

//        title.setText(getDataList().get(position).title + (position % 2 == 0 ? "我只能右滑动" : "我只能左滑动"));

        //隐藏控件
//        btnUnRead.setVisibility(position % 3 == 0 ? View.GONE : View.VISIBLE);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    //如果删除时，不使用mAdapter.notifyItemRemoved(pos)，则删除没有动画效果，
                    //且如果想让侧滑菜单同时关闭，需要同时调用 ((CstSwipeDelMenu) holder.itemView).quickClose();
                    //((CstSwipeDelMenu) holder.itemView).quickClose();
                    mOnSwipeListener.onDel(position);
                }
            }
        });
        //注意事项，设置item点击，不能对整个holder.itemView设置咯，只能对第一个子View，即原来的content设置，这算是局限性吧。
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent ==null){
                    intent = new Intent(context,WoosiiEmployeeFoundTeacherActivity.class);
                }
                intent.putExtra("employee_user_id", employee.getUser_id());
                CommonUtils.startActivity(context,intent);
            }
        });
        //置顶：
        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnSwipeListener) {
                    mOnSwipeListener.onTop(position);
                }

            }
        });
    }

    /**
     * 和Activity通信的接口
     */
    public interface onSwipeListener {
        void onDel(int pos);

        void onTop(int pos);
    }

    private onSwipeListener mOnSwipeListener;

    public void setOnDelListener(onSwipeListener mOnDelListener) {
        this.mOnSwipeListener = mOnDelListener;
    }
}
