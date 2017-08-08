package sochat.so.com.android.adapter;

/**
 * Created by Administrator on 2017/5/6.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;

/**
 * 搜索历史的adapter
 * 这里直接用内部类的形式
 */

public class DeleteHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<String> lists;
    private LayoutInflater inflater;

    public DeleteHistoryAdapter(Context context,List<String>lists){
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    public void setlist(ArrayList<String> lists){
        this.lists = lists;
        Log.i(ConfigInfo.TAG,"setlist(ArrayList<String> lists)");
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        Log.i(ConfigInfo.TAG,"getCount()"+lists.size());
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
        ViewHolder holder ;
//        Log.i(ConfigInfo.TAG,"position :"+lists.size());
//        Log.i(ConfigInfo.TAG,"position :"+position);
        if (convertView ==null){
            convertView = inflater.inflate(R.layout.item_search_history,null);
            holder = new ViewHolder();
            holder.tvSearchName = (TextView) convertView.findViewById(R.id.tv_search_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvSearchName.setText(lists.get(position).trim());

        return convertView;
    }

    class ViewHolder{
        private TextView tvSearchName;
    }


}