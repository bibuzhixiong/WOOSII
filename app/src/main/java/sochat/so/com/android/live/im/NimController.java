package sochat.so.com.android.live.im;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.chatroom.model.MemberOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sochat.so.com.android.live.im.chatroom.helper.ChatRoomMemberCache;


/**
 * Created by zhukkun on 1/5/17.
 */
public class NimController extends NimContract.BaseNimController{

    public final static String EXTRA_ROOM_ID = "ROOM_ID";
    private static final String TAG = "nimcontroller";
    private static final int FETCH_ONLINE_PEOPLE_COUNTS_DELTA = 20 * 1000; //请求人员列表接口 需要控制请求频率, 60S 超过10次 会被服务端禁止访问60S

    private Timer timer;
    private Context context;
    private NimContract.Ui ui;

    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    public NimController(Context context, NimContract.Ui ui) {
        this.context = context;
        this.ui = ui;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        roomId = intent.getStringExtra(EXTRA_ROOM_ID);
        // 注册监听
        registerObservers(true);
        // 登录聊天室
        enterRoom();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        clearChatRoom();
    }

    @Override
    public void kickMember(ChatRoomMember chatRoomMember) {
        Map<String, Object> reason = new HashMap<>();
        reason.put("reason", "kick");
        NIMClient.getService(ChatRoomService.class)
                .kickMember(roomId, chatRoomMember.getAccount(), reason)
                .setCallback(new RequestCallback<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        ui.showTextToast("踢人操作成功");
                    }

                    @Override
                    public void onFailed(int i) {
                        ui.showTextToast("踢人操作失败");
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }

    @Override
    public void muteMember(ChatRoomMember roomMember) {

        MemberOption option = new MemberOption(roomId, roomMember.getAccount());
        NIMClient.getService(ChatRoomService.class)
                .markChatRoomMutedList(!roomMember.isMuted(), option)
                .setCallback(new RequestCallback<ChatRoomMember>() {

                    @Override
                    public void onSuccess(ChatRoomMember chatRoomMember) {
                        ui.showTextToast("禁言操作成功");
                    }

                    @Override
                    public void onFailed(int i) {
                        ui.showTextToast("禁言操作失败");
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
    }

    private void enterRoom() {
                DialogMaker.showProgressDialog(context, null, "", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (enterRequest != null) {
                    enterRequest.abort();
                    onLoginDone();
                    ui.finish();
                }
            }
        }).setCanceledOnTouchOutside(false);
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                roomInfo = result.getRoomInfo();
                roomInfo.setOnlineUserCount(roomInfo.getOnlineUserCount()-1); //不包括主播自己
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomMemberCache.getInstance().saveMyMember(member);
                ui.onEnterChatRoomSuc(roomId);
                ui.refreshRoomInfo(roomInfo);
                fetchOnlineCount();//开启人数轮询
            }

            @Override
            public void onFailed(int code) {
                // test
                LogUtil.ui("enter chat room failed, callback code=" + code);

                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    ui.onChatRoomFinished("你已被拉入黑名单，不能再进入");
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    ui.onChatRoomFinished("聊天室不存在");
                } else {
                    ui.onChatRoomFinished("进入聊天室失败, 请尝试重新登录");
                }
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                //Toast.makeText(ChatRoomActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                ui.finish();
            }
        });
    }

    private void fetchOnlineCount() {
        if (timer == null) {
            timer = new Timer();
        }
        //开始一个定时任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getTotalOnlineMember();
            }
        }, 0, FETCH_ONLINE_PEOPLE_COUNTS_DELTA);
    }

    boolean hasReceivedNormalMember = false;
    boolean hasReceivedGuestMember = false;

    List<ChatRoomMember> totalMemberList = new ArrayList<>();

    private void getTotalOnlineMember() {
        hasReceivedGuestMember = false;
        hasReceivedNormalMember = false;
        totalMemberList.clear();

        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.ONLINE_NORMAL, 0, 0)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                        if(result!=null) {
                            //人员列表中不显示主播
                            {
                                ChatRoomMember creator = null;
                                for (int i = 0; i < result.size(); i++) {
                                    if (result.get(i).getAccount().equals(roomInfo.getCreator())) {
                                        creator = result.get(i);
                                        break;
                                    }
                                }
                                if (creator != null) {
                                    result.remove(creator);
                                }
                            }

                            hasReceivedNormalMember = true;
                            totalMemberList.addAll(result);
                            if(hasReceivedNormalMember && hasReceivedGuestMember){
                                ui.refreshRoomMember(totalMemberList);
                            }
                        }
                    }
                });

        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, MemberQueryType.GUEST, 0, 0)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                        if(result!=null){
                            hasReceivedGuestMember = true;
                            totalMemberList.addAll(result);
                            if(hasReceivedNormalMember && hasReceivedGuestMember){
                                ui.refreshRoomMember(totalMemberList);
                            }
                        }
                    }
                });
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (!chatRoomStatusChangeData.roomId.equals(roomId)) {
                return;
            }
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
                int code = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
                LogUtil.d(TAG, "chat room enter error code:" + code);
                if (code != ResponseCode.RES_ECONNECTION) {
                    //Toast.makeText(ChatRoomActivity.this, "未登录,code=" + code, Toast.LENGTH_LONG).show();
                }
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
                //Toast.makeText(ChatRoomActivity.this, R.string.net_broken, Toast.LENGTH_SHORT).show();
            }

            LogUtil.i(TAG, "chat room online status changed to " + chatRoomStatusChangeData.status.name());
        }
    };


    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            //Toast.makeText(ChatRoomActivity.this, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_SHORT).show();
            clearChatRoom();

            switch (chatRoomKickOutEvent.getReason()){
                case CHAT_ROOM_INVALID:
                    ui.onChatRoomFinished("直播已结束");
                    break;
                case KICK_OUT_BY_MANAGER:
                    ui.onChatRoomFinished("您已被踢出直播间");
                    break;
                case KICK_OUT_BY_CONFLICT_LOGIN:
                    ui.onChatRoomFinished("您的账号被顶,请重新登录");
                    break;
                default:
                    ui.onChatRoomFinished("直播结束");
                    break;
            }

        }
    };


    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }
}
