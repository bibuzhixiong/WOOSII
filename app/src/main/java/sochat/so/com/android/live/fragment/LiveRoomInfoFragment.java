package sochat.so.com.android.live.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.squareup.picasso.Picasso;

import java.util.List;

import sochat.so.com.android.R;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.activity.LiveRoomActivity;
import sochat.so.com.android.live.base.LiveBaseFragment;
import sochat.so.com.android.live.im.adapter.MemberAdapter;


/**
 * Created by zhukkun on 1/9/17.
 */
public class LiveRoomInfoFragment extends LiveBaseFragment {

    public static final String EXTRA_IS_AUDIENCE = "is_audence";

    public static LiveRoomInfoFragment getInstance(boolean isAudience){
        LiveRoomInfoFragment fragment = new LiveRoomInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_IS_AUDIENCE, isAudience);
        fragment.setArguments(bundle);
        return fragment;
    }

    boolean isAudience;
    TextView tvOnlineCount;
    TextView tvRoomName;
    TextView tvMasterName;
    TextView tvRoomTitle;
    TextView tvLiverName;
    ImageView ivLiverPhoto;

    RecyclerView recyclerView;
    MemberAdapter memberAdapter;

    int onlineCount;

    private String creator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isAudience = getArguments().getBoolean(EXTRA_IS_AUDIENCE, true);
        return inflater.inflate( R.layout.layout_live_captrue_room_info, container, false);
//        return inflater.inflate(isAudience ? R.layout.layout_live_audience_room_info : R.layout.layout_live_captrue_room_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    private void initView() {
        ivLiverPhoto = findView(R.id.master_head);
        tvOnlineCount = findView(R.id.online_count_text);
        tvRoomName = findView(R.id.room_name);
        recyclerView = findView(R.id.rv_room_member);
        tvRoomTitle = findView(R.id.room_title);
        initRecycleView();
        tvLiverName = findView(R.id.tv_liver_name);
    }

    private void initRecycleView() {
        memberAdapter = new MemberAdapter(getContext());
        recyclerView.setAdapter(memberAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        memberAdapter.setOnItemClickListener(new MemberAdapter.ItemClickListener() {
            @Override
            public void onItemClick(ChatRoomMember member) {
                ((LiveRoomActivity)getActivity()).onMemberOperate(member);
            }
        });
    }

    public void updateMember(List<ChatRoomMember> members){
        memberAdapter.updateMember(members);
        onlineCount = members.size();
        tvOnlineCount.setText(onlineCount + "人观看直播");
    }

    public void refreshRoomTitle(String roomtitle){
        if (!TextUtils.isEmpty(roomtitle)){
            tvRoomTitle.setText(roomtitle);
        }
    }

    public void refreshRoomInfo(final ChatRoomInfo roomInfo) {
        onlineCount = roomInfo.getOnlineUserCount();
        tvOnlineCount.setText(onlineCount+"人观看直播");
        tvRoomName.setText("房间号："+roomInfo.getRoomId());

        if (!TextUtils.isEmpty(roomInfo.getName())) {
            tvRoomTitle.setText(roomInfo.getName());
        }
        //获得主播id
//        creator = roomInfo.getCreator();
        Log.d(ConfigInfo.TAG,"房间名"+roomInfo.getName());
        NIMClient.getService(ChatRoomService.class)
                .fetchRoomMembers(roomInfo.getRoomId(), MemberQueryType.NORMAL, 0, 10)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int i, List<ChatRoomMember> chatRoomMembers, Throwable throwable) {
                        for (int j=0;j<chatRoomMembers.size();j++){
                            Log.d(ConfigInfo.TAG,"这里是成员信息："+chatRoomMembers.get(0).getAccount()+","+chatRoomMembers.get(0).getAvatar());
                            if (chatRoomMembers.get(j).getAccount().equals(roomInfo.getCreator())){
                                if (TextUtils.isEmpty(roomInfo.getName())) {
                                    tvRoomTitle.setText(chatRoomMembers.get(j).getNick()+"的直播间");
                                }
                                tvLiverName.setText(chatRoomMembers.get(j).getNick());
                                creatorThumb = chatRoomMembers.get(0).getAvatar();
                                Picasso.with(getActivity()).load(chatRoomMembers.get(0).getAvatar()).placeholder(R.drawable.avatar_def).error(R.drawable.avatar_def).into(ivLiverPhoto);
                            }
                        }
                    }
                });

    }
    private String creatorThumb;
    public String getFinishCreatorThumb(){
        if (!TextUtils.isEmpty(creatorThumb))
        return creatorThumb;

        return "thumb";
    }

    public void onReceivedNotification(ChatRoomNotificationAttachment attachment) {
        ChatRoomMember chatRoomMember = new ChatRoomMember();
        if (attachment.getTargets()!=null){
            chatRoomMember.setAccount(attachment.getTargets().get(0));
            chatRoomMember.setNick(attachment.getTargetNicks().get(0));
            if(!isAudience && chatRoomMember.getAccount().equals(DemoCache.getAccount())){
                //主播的通知(主播进入房间,主播离开房间)不做处理,
                return;
            }

            switch (attachment.getType()) {
                case ChatRoomMemberIn:
                    if(memberAdapter.addMember(chatRoomMember)) {
                        tvOnlineCount.setText(++onlineCount + "人观看直播");
                    }
                    break;
                case ChatRoomMemberExit:
                case ChatRoomMemberKicked:
                    memberAdapter.removeMember(chatRoomMember);
                    tvOnlineCount.setText(--onlineCount + "人观看直播");
                    break;
                case ChatRoomMemberMuteAdd:
                    chatRoomMember.setMuted(true);
                    memberAdapter.updateSingleMember(chatRoomMember);
                    break;
                case ChatRoomMemberMuteRemove:
                    chatRoomMember.setMuted(false);
                    memberAdapter.updateSingleMember(chatRoomMember);
                    break;
            }
        }
    }

    public void updateMemberState(ChatRoomMember current_operate_member) {
        memberAdapter.updateSingleMember(current_operate_member);
    }

    public ChatRoomMember getMember(String fromAccount) {
        return memberAdapter.getMember(fromAccount);
    }
}
