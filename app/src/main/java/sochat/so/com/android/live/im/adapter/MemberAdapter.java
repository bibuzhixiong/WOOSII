package sochat.so.com.android.live.im.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.live.im.widget.CircleImageView;


/**
 * Created by zhukkun on 1/11/17.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<ChatRoomMember> data = new ArrayList<>();
    private Context context;
    ItemClickListener itemClickListener;

    public MemberAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_chat_member, null);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, final int position) {
//        ImageLoader.getInstance().displayImage(data.get(position).getAvatar(), holder.circleImageView);
        if (!TextUtils.isEmpty(data.get(position).getAvatar())){
            Picasso.with(context).load(data.get(position).getAvatar()).placeholder(R.drawable.zhanweifu).error(R.drawable.avatar_def).into(holder.circleImageView);
        }


        holder.tv_name.setText(data.get(position).getNick());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(data.get(position));
                }
            }
        });
        if(data.get(position).isMuted()) {
            holder.view_forbidden.setVisibility(View.VISIBLE);
        }else{
            holder.view_forbidden.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateMember(List<ChatRoomMember> members) {
        if(data == null){
            data = new ArrayList<>();
        }
        data.clear();

        //排序 将禁言用户排在人员列表前部分
        List<ChatRoomMember> normalMembers = new ArrayList<>();
        List<ChatRoomMember> muteMembers = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            if(members.get(i).isMuted()){
                muteMembers.add(members.get(i));
            }else{
                normalMembers.add(members.get(i));
            }
        }
        data.addAll(muteMembers);
        data.addAll(normalMembers);

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public boolean addMember(ChatRoomMember chatRoomMember) {
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).getAccount().equals(chatRoomMember.getAccount())){
                return false;
            }
        }
        data.add(chatRoomMember);
        notifyDataSetChanged();
        return true;
    }

    public void removeMember(ChatRoomMember chatRoomMember) {
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).getAccount().equals(chatRoomMember.getAccount())){
                data.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void updateSingleMember(ChatRoomMember chatRoomMember) {
        for (int i = 0; i < data.size(); i++) {
            ChatRoomMember current = data.get(i);
            if(current.getAccount().equals(chatRoomMember.getAccount())){
                current.setMuted(chatRoomMember.isMuted());
                data.remove(current);
                if(chatRoomMember.isMuted()) {
                    data.add(0, current);
                }else{
                    data.add(current);
                }
                notifyDataSetChanged();
                break;
            }
        }
    }

    public ChatRoomMember getMember(String fromAccount) {
        if(data == null) return null;
        for (int i = 0; i < data.size(); i++) {
            if(data.get(i).getAccount().equals(fromAccount)){
                return data.get(i);
            }
        }
        return null;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        View view_forbidden;
        TextView tv_name;

        public MemberViewHolder(View itemView) {
            super(itemView);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.circle_image_view);
            view_forbidden = itemView.findViewById(R.id.iv_forbidden);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface ItemClickListener{
        void onItemClick(ChatRoomMember member);
    }
}
