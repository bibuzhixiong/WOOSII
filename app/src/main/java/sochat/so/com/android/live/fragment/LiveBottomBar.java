package sochat.so.com.android.live.fragment;

import android.content.Context;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.im.adapter.GiftAdapter;
import sochat.so.com.android.live.im.chatroom.helper.ChatRoomMemberCache;
import sochat.so.com.android.live.im.constant.GiftConstant;
import sochat.so.com.android.live.im.constant.GiftType;
import sochat.so.com.android.live.im.helper.GiftAnimation;
import sochat.so.com.android.live.im.helper.GiftCache;
import sochat.so.com.android.live.im.model.Gift;
import sochat.so.com.android.live.im.session.extension.GiftAttachment;
import sochat.so.com.android.live.im.session.extension.LikeAttachment;
import sochat.so.com.android.live.widget.PeriscopeLayout;
import sochat.so.com.android.utils.DemoHelper;
import sochat.so.com.android.utils.SharedUtils;


/**
 * Created by zhukkun on 1/6/17.
 */
public class LiveBottomBar extends RelativeLayout {

    private View btn_msg;
    private View btn_gift;
    private View btn_capture;
    private View btn_share;

    private View btn_filter;
    private View btn_music;
    private View btn_like;
    private View btn_send_gift;
    private View iv_setting;

    /**
     * 滤镜相关控件
     */
    private LinearLayout layout_filter;
    private View filter_layout_empty;


    private RelativeLayout rlBottomLayout;
    //底部的美颜布局
    private LinearLayout rl_lvjing_layout;
    //底部的设置清晰度布局
    private RelativeLayout ll_setting_layout;
    //底部的美颜布局的左边美颜
    private LinearLayout ll_beauty_layout;
    //底部的美颜布局的右边滤镜
    private LinearLayout ll_beauty_filter;
    //底部美颜的美颜功能区
    private LinearLayout ll_layout1;
    //底部美颜的滤镜功能区
    private HorizontalScrollView hs_layout;

    private TextView tvLD;
    private TextView tvSD;
    private TextView tvHD;

    private SeekBar focusSeekBar;
    private SeekBar filterSeekBar;


    private RadioButton rb_huaijiu;
    private RadioButton rb_ganjing;
    private RadioButton rb_ziran;
    private RadioButton rb_jiankang;
    private RadioButton rb_fugu;
    private RadioButton rb_wenrou;
    private RadioButton rb_meibai;


    private ViewGroup giftLayout; // 礼物布局
    private GridView giftView; // 礼物列表
    private RelativeLayout giftAnimationViewDown; // 礼物动画布局1
    private RelativeLayout giftAnimationViewUp; // 礼物动画布局2
    private GiftAdapter adapter;
    private GiftAnimation giftAnimation; // 礼物动画
    private TextView noGiftText;
    private List<Gift> giftList = new ArrayList<>(); // 礼物列表数据

    private PeriscopeLayout periscopeLayout; // 点赞爱心布局.

    //分享布局
    private RelativeLayout rl_share;
    private LinearLayout ll_share_content;
    private Button btn_http;
    private Button btn_hls;
    private Button btn_rtmp;
    private Button btn_share_cancel;

    // 发送礼物频率控制使用
    private long lastClickTime = 0;
    boolean isAudience;
    private String roomId;
    private int giftPosition = -1;


    public LiveBottomBar(Context context, boolean isAudience, String roomId) {
        super(context);
        this.isAudience = isAudience;
        this.roomId = roomId;
        int resourceId = isAudience? R.layout.layout_live_audience_bottom_bar : R.layout.layout_live_captrue_bottom_bar;
        LayoutInflater.from(context).inflate(resourceId, this, true);
        initView();
    }

    private void initView() {
        bindView();
        initGiftLayout();
        loadGift();
        clickView();
    }

    private void bindView() {
        // 点赞的爱心布局
        periscopeLayout = findView(R.id.periscope);
        rl_share = findView(R.id.share_layout);
        ll_share_content = findView(R.id.ll_share_content);
        btn_http = findView(R.id.btn_share_http);
        btn_hls  = findView(R.id.btn_share_hls);
        btn_rtmp = findView(R.id.btn_share_rtmp);
        btn_share_cancel = findView(R.id.btn_share_cancel);
        if(isAudience) {
            btn_msg = findView(R.id.audience_message);
            btn_gift = findView(R.id.audience_gift);
            btn_capture = findView(R.id.audience_screen_btn);
            btn_share = findView(R.id.audience_share);
            btn_like = findView(R.id.audience_like);
        }else{
            btn_gift = findView(R.id.live_gift);
            btn_msg = findView(R.id.live_message);
            btn_filter = findView(R.id.live_filter);
            btn_music = findView(R.id.live_music_btn);
            btn_capture = findView(R.id.live_screen_btn);
            btn_share = findView(R.id.live_share);

            iv_setting =findView(R.id.iv_setting);

            layout_filter = findView(R.id.ll_filter_operate);
            filter_layout_empty = findView(R.id.filter_layout_empty);



            rl_lvjing_layout = (LinearLayout) findViewById(R.id.rl_lvjing_layout);
            ll_setting_layout = (RelativeLayout) findViewById(R.id.ll_setting_layout);
            ll_layout1 = (LinearLayout) findViewById(R.id.ll_layout1);
            ll_beauty_layout = (LinearLayout) findViewById(R.id.ll_beauty_layout);
            ll_beauty_filter = (LinearLayout) findViewById(R.id.ll_beauty_filter);

            tvLD = (TextView) findViewById(R.id.tv_LD);
            tvSD = (TextView) findViewById(R.id.tv_SD);
            tvHD = (TextView) findViewById(R.id.tv_HD);

            hs_layout = (HorizontalScrollView) findViewById(R.id.hs_layout);
            rb_huaijiu = (RadioButton) findViewById(R.id.rb_huaijiu);
            rb_ganjing = (RadioButton) findViewById(R.id.rb_ganjing);
            rb_ziran = (RadioButton) findViewById(R.id.rb_ziran);
            rb_jiankang = (RadioButton) findViewById(R.id.rb_jiankang);
            rb_fugu = (RadioButton) findViewById(R.id.rb_fugu);
            rb_wenrou = (RadioButton) findViewById(R.id.rb_wenrou);
            rb_meibai = (RadioButton) findViewById(R.id.rb_meibai);

            focusSeekBar = (SeekBar) findViewById(R.id.live_seekbar_focus);
            filterSeekBar = (SeekBar) findViewById(R.id.live_seekbar_filter);
        }
    }

    // 初始化礼物布局
    protected void initGiftLayout() {
        giftLayout = findView(R.id.gift_layout);
        giftView = findView(R.id.gift_grid_view);

        giftAnimationViewDown = findView(R.id.gift_animation_view);
        giftAnimationViewUp = findView(R.id.gift_animation_view_up);

        giftAnimation = new GiftAnimation(giftAnimationViewDown, giftAnimationViewUp);

        giftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                giftLayout.setVisibility(View.GONE);
                giftPosition = -1;
            }
        });

        if(isAudience) {
            btn_send_gift = findView(R.id.send_gift_btn);
            btn_send_gift.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendGift();
                }
            });

            adapter = new GiftAdapter(getContext());
            giftView.setAdapter(adapter);

            giftView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    giftPosition = position;
                }
            });
        }else{
            adapter = new GiftAdapter(giftList, getContext());
            giftView.setAdapter(adapter);
            noGiftText = findView(R.id.no_gift_tip);
        }
    }

    private void clickView() {
        btn_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                rl_share.setVisibility(VISIBLE);
                String url = ConfigInfo.ApiUrl+"/index.php/Api/Wylive/live_share?id="+ DemoHelper.getLive_id();
                SharedUtils.ShareWeb(url, "沃噻教育", "沃噻教育欢迎您前来观看直播", getContext());
            }
        });

        rl_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_share.setVisibility(GONE);
            }
        });

        ll_share_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //空方法 用于拦截点击事件
            }
        });

        btn_http.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(DemoCache.getRoomInfoEntity().getHttpPullUrl());
                rl_share.setVisibility(GONE);
            }
        });

        btn_hls.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(DemoCache.getRoomInfoEntity().getHlsPullUrl());
                rl_share.setVisibility(GONE);
            }
        });

        btn_rtmp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(DemoCache.getRoomInfoEntity().getRtmpPullUrl());
                rl_share.setVisibility(GONE);
            }
        });

        btn_share_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_share.setVisibility(GONE);
            }
        });

        btn_gift.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftLayout();
            }
        });

        if(isAudience){
            btn_like.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isFastClick()) {
                        periscopeLayout.addHeart();
                        sendLike();
                    }
                }
            });
        }else{
            iv_setting.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_filter.setVisibility(View.VISIBLE);
                    ll_setting_layout.setVisibility(View.VISIBLE);
                    rl_lvjing_layout.setVisibility(View.GONE);
                }
            });


            btn_filter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_filter.setVisibility(View.VISIBLE);
                    ll_setting_layout.setVisibility(View.GONE);
                    rl_lvjing_layout.setVisibility(View.VISIBLE);
                }
            });

            filter_layout_empty.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_filter.setVisibility(View.GONE);
                }
            });

        }
    }

    private void shareUrl(String url) {
        try {
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(url);
            Toast.makeText(getContext(), getContext().getString(R.string.share_tip), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 更新收到礼物的数量
    private boolean updateGiftCount(GiftType type) {
        for (Gift gift : giftList) {
            if (type == gift.getGiftType()) {
                gift.setCount(gift.getCount() + 1);
                return true;
            }
        }
        return false;
    }

    public void updateGiftList(GiftType type) {
        if (!updateGiftCount(type)) {
            giftList.add(new Gift(type, GiftConstant.titles[type.getValue()], 1, GiftConstant.images[type.getValue()]));
        }
        adapter.notifyDataSetChanged();
        GiftCache.getInstance().saveGift(roomId, type.getValue());
    }

    // 取出缓存的礼物
    private void loadGift() {
        Map gifts = GiftCache.getInstance().getGift(roomId);
        if (gifts == null) {
            return;
        }
        Iterator<Map.Entry<Integer, Integer>> it = gifts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            int type = entry.getKey();
            int count = entry.getValue();
            giftList.add(new Gift(GiftType.typeOfValue(type), GiftConstant.titles[type], count, GiftConstant.images[type]));
        }
    }

    // 显示礼物列表
    private void showGiftLayout() {
        giftLayout.setVisibility(View.VISIBLE);
        if(!isAudience){
            if(adapter.getCount() == 0){
                // 暂无礼物
                noGiftText.setVisibility(View.VISIBLE);
            }else{
                noGiftText.setVisibility(View.GONE);
            }
        }
    }

    // 发送礼物
    private void sendGift() {
        if (giftPosition == -1) {
            Toast.makeText(getContext(), "请选择礼物", Toast.LENGTH_SHORT).show();
            return;
        }
        giftLayout.setVisibility(View.GONE);
        GiftAttachment attachment = new GiftAttachment(GiftType.typeOfValue(giftPosition), 1);
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(roomId, attachment);
        setMemberType(message);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_CHATROOM_MUTED) {
                            Toast.makeText(DemoCache.getContext(), "用户被禁言,无法发送礼物", Toast.LENGTH_SHORT).show();
                        } else if (code == ResponseCode.RES_CHATROOM_ROOM_MUTED) {
                            Toast.makeText(DemoCache.getContext(), "全体禁言,无法发送礼物", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DemoCache.getContext(), "消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(DemoCache.getContext(), "消息发送失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        giftAnimation.showGiftAnimation(message);
        giftPosition = -1; // 发送完毕，置空
    }

    // 显示礼物动画
    public void showGiftAnimation(ChatRoomMessage msg) {
        giftAnimation.showGiftAnimation(msg);
    }

    public void setMsgClickListener(OnClickListener onClickListener) {
        btn_msg.setOnClickListener(onClickListener);
    }

    public void setCaptureClickListener(OnClickListener onClickListener) {
        btn_capture.setOnClickListener(onClickListener);
    }

    public void setMusicClickListener(OnClickListener onClickListener) {
        if(!isAudience){
            btn_music.setOnClickListener(onClickListener);
        }
    }

    /*************************
     * 点赞爱心
     ********************************/

    // 发送点赞爱心
    private void sendLike() {
        LikeAttachment attachment = new LikeAttachment();
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(roomId, attachment);
        setMemberType(message);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_CHATROOM_MUTED) {
                            Toast.makeText(DemoCache.getContext(), "用户被禁言,无法点赞", Toast.LENGTH_SHORT).show();
                        } else if (code == ResponseCode.RES_CHATROOM_ROOM_MUTED) {
                            Toast.makeText(DemoCache.getContext(), "全体禁言,无法点赞", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DemoCache.getContext(), "消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(DemoCache.getContext(), "消息发送失败！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 发送爱心频率控制
    private boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        long time = currentTime - lastClickTime;
        if (time > 0 && time < 1000) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }

    private void setMemberType(ChatRoomMessage message) {
        Map<String, Object> ext = new HashMap<>();
        ChatRoomMember chatRoomMember = ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount());
        if (chatRoomMember != null && chatRoomMember.getMemberType() != null) {
            ext.put("type", chatRoomMember.getMemberType().getValue());
            message.setRemoteExtension(ext);
        }
    }

    public void addHeart() {
        if(periscopeLayout!=null){
            periscopeLayout.addHeart();
        }
    }

    public View getFilterView(){
        return btn_filter;
    }

    public View getrl_lvjing_layout() {
        return rl_lvjing_layout;
    }
    public View getll_setting_layout() {
        return ll_setting_layout;
    }
    public View getll_layout1() {
        return ll_layout1;
    }
    public View getll_beauty_layout() {
        return ll_beauty_layout;
    }
    public View getll_beauty_filter() {
        return ll_beauty_filter;
    }
    public View gettvLD() {
        return tvLD;
    }
    public View gettvSD() {
        return tvSD;
    }
    public View gettvHD() {
        return tvHD;
    }
    public View geths_layout() {
        return hs_layout;
    }
    public View getrb_huaijiu() {
        return rb_huaijiu;
    }
    public View getrb_ganjing() {
        return rb_ganjing;
    }
    public View getrb_ziran() {
        return rb_ziran;
    }
    public View getrb_jiankang() {
        return rb_jiankang;
    }
    public View getrb_fugu() {
        return rb_fugu;
    }
    public View getrb_wenrou() {
        return rb_wenrou;
    }
    public View getrb_meibai() {
        return rb_meibai;
    }
    public SeekBar getfocusSeekBar() {
        return focusSeekBar;
    }
    public SeekBar getfilterSeekBar() {
        return filterSeekBar;
    }


    public View getCaptureView(){
        return btn_capture;
    }

    protected <T extends View> T findView(int id){
        return (T)findViewById(id);
    }

}
