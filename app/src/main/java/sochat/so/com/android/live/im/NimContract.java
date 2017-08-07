package sochat.so.com.android.live.im;

import android.content.Intent;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;

import java.util.List;

/**
 * Created by zhukkun on 1/5/17.
 */
public class NimContract {

    public  interface Ui{
        void finish();

        void onEnterChatRoomSuc(String roomId);

        void refreshRoomInfo(ChatRoomInfo roomInfo);

        void refreshRoomMember(List<ChatRoomMember> result);

        void dismissMemberOperateLayout();

        void onChatRoomFinished(String reason);

        void showTextToast(String text);
    }


    public static abstract class BaseNimController{

        public abstract void onHandleIntent(Intent intent);

        public abstract void onDestroy();

        public abstract void logoutChatRoom();

        public abstract void kickMember(ChatRoomMember current_operate_member);

        public abstract void muteMember(ChatRoomMember current_operate_member);
    }
}
