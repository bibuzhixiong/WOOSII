package sochat.so.com.android.utils;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.Log;
import com.umeng.socialize.utils.ShareBoardlistener;

import sochat.so.com.android.R;

/**
 * Created by Administrator on 2017/2/28.
 */

public class SharedUtils {

    /**
     * 分享的回调
     */
    public static UMShareListener getUMShareListener(final Context context) {
         UMShareListener umShareListener = new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //分享开始的回调
            }
            @Override
            public void onResult(SHARE_MEDIA platform) {
                Log.d("plat","platform"+platform);

                Toast.makeText(context, "微信" + " 分享成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                Toast.makeText(context,platform + " 分享失败", Toast.LENGTH_SHORT).show();
                if(t!=null){
                    Log.d("throw","throw:"+t.getMessage());
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(context,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        };

        return umShareListener;
    }


    //    分享图文：
    public static void ShareImage(String image,String thumb,Context context){
        UMImage pic = new UMImage(context, R.drawable.ic_launcher);
        pic.setThumb(new UMImage(context,R.drawable.liuyifei));
        new ShareAction((Activity) context).withMedia(pic).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(SharedUtils.getUMShareListener(context)).withText("你是我的眼").open();}

    //分享图文

    public static void ShareImageText(final String thumb_img, final String image,final String title, final String content,final Context context){
        new ShareAction((Activity) context).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        UMImage pic = new UMImage(context,image);//要发送的图片
                        pic.setThumb(new UMImage(context,thumb_img));  //缩略图
                        new ShareAction((Activity) context).withMedia(pic).setPlatform(share_media).setCallback(SharedUtils.getUMShareListener(context)).withText(content).share();
                    }
                }).open();

    }


    //分享链接
//    public static void ShareWeb2(final String thumb_img, final String title, final String content,final Context context){
//        new ShareAction((Activity) context).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
//                .setShareboardclickCallback(new ShareBoardlistener() {
//                    @Override
//                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//                        UMImage thumb = new UMImage(context,thumb_img);
//                        UMWeb web = new UMWeb(thumb_img);
//                        web.setTitle(title);//标题
//                        web.setThumb(thumb);//缩略图
//                        web.setDescription(content);//描述内容
//
//                        new ShareAction((Activity) context).withMedia(web).setPlatform(share_media).setCallback(SharedUtils.getUMShareListener(context)).share();
//                    }
//                }).open();
//    }

    private static ShareBoardConfig config = new ShareBoardConfig();//新建ShareBoardConfig


    //分享链接2
    public static void ShareWeb(final String thumb_img, final String title, final String content,final Context context){
        UMImage thumb = new UMImage(context,thumb_img);
        UMWeb web = new UMWeb(thumb_img);
        web.setTitle(title);//标题
        web.setThumb(thumb);//缩略图
        web.setDescription(content);//描述内容
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(false);
        config.setTitleVisibility(false);
        config.setIndicatorVisibility(false);

        new ShareAction((Activity) context).withMedia(web).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(SharedUtils.getUMShareListener(context)).open(config);
    }




}
