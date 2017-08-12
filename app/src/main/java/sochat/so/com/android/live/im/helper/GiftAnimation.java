package sochat.so.com.android.live.im.helper;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Queue;

import sochat.so.com.android.R;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.im.constant.GiftConstant;
import sochat.so.com.android.live.im.session.extension.GiftAttachment;


/**
 * 礼物动画
 * Created by hzxuwen on 2016/4/1.
 */
public class GiftAnimation {
    private final int SHOW_HIDE_ANIMATOR_DURATION = 500;
    private final int ANIMATION_STAY_DURATION = 1000;

    private boolean upFree = true;
    private boolean downFree = true;

    private ViewGroup upView;
    private ViewGroup downView;
    private AnimatorSet upAnimatorSet;
    private AnimatorSet downAnimatorSet;

    private Queue<ChatRoomMessage> cache = new LinkedList<>();

    //这个对象实际上是LiveRoomActivity的对象
    private Context context;

    public GiftAnimation(Context context,ViewGroup downView, ViewGroup upView) {
        this.context =context;
        this.upView = upView;
        this.downView = downView;
        this.upAnimatorSet = buildAnimationSet(upView);
        this.downAnimatorSet = buildAnimationSet(downView);
    }

    // 收到礼物，等待显示动画
    public void showGiftAnimation(final ChatRoomMessage message) {
        cache.add(message);
        checkAndStart();
    }

    private void checkAndStart() {
        if(!upFree && !downFree) {
            return;
        }

        if(downFree) {
            startAnimation(downView, downAnimatorSet);
        } else {
            startAnimation(upView, upAnimatorSet);
        }
    }

    // 开始礼物动画
    private void startAnimation(ViewGroup target, AnimatorSet set) {
        ChatRoomMessage message = cache.poll();
        if(message == null) {
            return;
        }

        // 更新状态
        onAnimationStart(target);

        // 更新礼物视图
        updateView(message, target);

        // 执行动画组
        target.setAlpha(1f);
        target.setVisibility(View.VISIBLE);
        set.start();
    }

    private void onAnimationStart(final ViewGroup target) {
        if(target == upView) {
            upFree = false;
        } else if(target == downView) {
            downFree = false;
        }
    }

    private void onAnimationCompleted(final ViewGroup target) {
        if(target == upView) {
            upFree = true;
        } else if(target == downView) {
            downFree = true;
        }

        checkAndStart();
    }

    /**
     * ********************* 属性动画 *********************
     */

    private AnimatorSet buildAnimationSet(final ViewGroup target){
        ObjectAnimator show = buildShowAnimator(target, SHOW_HIDE_ANIMATOR_DURATION);
        ObjectAnimator hide = buildHideAnimator(target, SHOW_HIDE_ANIMATOR_DURATION);
        hide.setStartDelay(ANIMATION_STAY_DURATION);

        AnimatorSet set = new AnimatorSet();
        set.setTarget(target);
        set.playSequentially(show, hide);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationCompleted(target);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return set;
    }

    private ObjectAnimator buildShowAnimator(final View target, long duration) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(target, "translationX", -300.0F, 0.0F)
                .setDuration(duration);
        translationX.setInterpolator(new OvershootInterpolator());

        return translationX;
    }

    private ObjectAnimator buildHideAnimator(final View target, long duration) {
        return ObjectAnimator.ofFloat(target, View.ALPHA, 1f, 0.0f)
                .setDuration(duration);
    }

    /**
     * ********************* 更新礼物信息 *********************
     */

    private String thumb;

    public void showThumb(String thumb){
        this.thumb =thumb;
    }

    private void updateView(final ChatRoomMessage message, ViewGroup root) {
        // senderName
        TextView audienceNameText = (TextView) root.findViewById(R.id.audience_name);
        ImageView iv_present_gift_person = (ImageView) root.findViewById(R.id.iv_present_gift_person);

        if (message.getChatRoomMessageExtension() != null) {
            Log.d(ConfigInfo.TAG,"这里是空的，哥哥");//主播
            audienceNameText.setText(message.getChatRoomMessageExtension().getSenderNick());
            Picasso.with(context).load(message.getChatRoomMessageExtension().getSenderAvatar()).error(R.drawable.avatar_def).into(iv_present_gift_person);
        } else {
            Log.d(ConfigInfo.TAG,"这里是空的，弟弟");//观众
            audienceNameText.setText(DemoCache.getUserInfo() == null ? DemoCache.getAccount() : DemoCache.getUserInfo().getName());
            Picasso.with(context).load(thumb).error(R.drawable.avatar_def).into(iv_present_gift_person);
        }

        // gift name & image
        GiftAttachment attachment = (GiftAttachment) message.getAttachment();
        TextView giftText = (TextView) root.findViewById(R.id.gift_name);
        giftText.setText(GiftConstant.titles[attachment.getGiftType().getValue()]);

        ImageView giftImage = (ImageView) root.findViewById(R.id.gift_image);
        giftImage.setImageResource(GiftConstant.images[attachment.getGiftType().getValue()]);


    }
}
