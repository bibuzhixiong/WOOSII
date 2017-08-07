package sochat.so.com.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import sochat.so.com.android.R;
import sochat.so.com.android.activity.PlayVedioActivity;
import sochat.so.com.android.activity.TeacherInfoActivity;
import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.customview.CircleImageView;
import sochat.so.com.android.customview.MyAdGallery;
import sochat.so.com.android.model.Child;
import sochat.so.com.android.utils.CommonUtils;


/**
 * Created by Administrator on 2017/2/20.
 */

public class MyMarketingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Child>childList;
    private LayoutInflater inflater;
    private Context context;

    /**
     * 视频的地址、名字、简介
     */
    private String vedioUrl;
    private String vedioTitle;
    private String vedioContent;

    // bitmap列表
    private String[] bitmap;
    private MyAdGallery mMyAdGallery;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    int[] bitmapId = {R.drawable.zhanweifu,R.drawable.zhanweifu,R.drawable.zhanweifu,R.drawable.zhanweifu};
                    mMyAdGallery.start(context, bitmap, bitmapId, 2000, null, 0, 0);
                    break;
            }

        }
    };

    /**
     * 判断是否是头部
     */
//    public static final int TYPE_HEADER = 0;暂时不要了
//    public static final int TYPE_NORMAL = 1;暂时不要了

    /**
     *头部的布局
     */
//    private View mHeaderView;
//
//    public void setHeaderView(View headerView) {
//        mHeaderView = headerView;
//        notifyItemInserted(0);
//    }
//
//    public View getHeaderView() {
//        return mHeaderView;
//    }

    /**
     * 判断加载布局的内容，头部轮播图的东西，现在暂时不要了
     * @param childList
     * @param context
     */
//    @Override
//    public int getItemViewType(int position) {
//        if(mHeaderView == null) return TYPE_NORMAL;
//        if(position == 0) return TYPE_HEADER;
//        return TYPE_NORMAL;
//    }

    public MyMarketingRecyclerViewAdapter(List<Child> childList, Context context) {
        this.context = context;
        this.childList = childList;
        this.inflater = LayoutInflater.from(context);
        Log.i(ConfigInfo.TAG,childList.toString());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        if(mHeaderView !=null&&viewType ==TYPE_HEADER) return new HeaderHolder(mHeaderView);头部轮播图的内容

//        View view = inflater.inflate(R.layout.itemvr_recycleview,viewGroup, false);
//        VrViewHolder holder= new VrViewHolder(view);

        View view = inflater.inflate(R.layout.item_videoview,viewGroup, false);
        MyViewHolder holder= new MyViewHolder(view);

        return holder;
    }

    public void setList(ArrayList<Child> childList) {
        this.childList = (ArrayList<Child>) childList.clone();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        MyOnClickListener mListener = new MyOnClickListener();
        /**
         * 头部轮播图的东西，现在暂时不要了
         */
//        if (getItemViewType(position) ==TYPE_HEADER) {
//            if(viewHolder instanceof HeaderHolder){
//                // 滚动图片id
//                getBitmap(((HeaderHolder) viewHolder).myAdGallery);
//            }
//            return ;
//        }

//        final int pos = getRealPosition(viewHolder);头部轮播图的东西，现在暂时不要了
//        final Child child = childList.get(pos);头部轮播图的东西，现在暂时不要了
        final Child child = childList.get(position);
//        if(viewHolder instanceof VrViewHolder){
//            ((VrViewHolder) viewHolder).tvTitle.setText(child.getName());
//            ((VrViewHolder) viewHolder).tvContent.setText(child.getInfo());
//            Picasso.with(context).load(ConfigInfo.ApiUrl+child.getThumb()).placeholder(R.drawable.zhanweifu).error(R.drawable.liuyifei).into(((VrViewHolder) viewHolder).ivCover);
//
//            ((VrViewHolder) viewHolder).ivCover.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//        }
        if (viewHolder instanceof MyViewHolder) {
            ((MyViewHolder)viewHolder).jcVideoPlayer.setUp(child.getUrl(), JCVideoPlayer.SCREEN_LAYOUT_LIST,"");
//            if (child.getThumb().trim().length() == 0){
//
//            }else{
//                Picasso.with(((MyViewHolder)viewHolder).jcVideoPlayer.getContext())
//                        .load(ConfigInfo.ApiUrl+child.getThumb())
//                        .placeholder(R.drawable.zhanweifu)
//                        .error(R.drawable.jiazaicuowu)
//                        .into(((MyViewHolder)viewHolder).jcVideoPlayer.thumbImageView);
//            }

            vedioUrl = child.getUrl();
            vedioTitle = child.getName();
            vedioContent = child.getInfo();

            ((MyViewHolder)viewHolder).tvTitle.setText(child.getName());
//            ((MyViewHolder)viewHolder).tvContent.setText(child.getInfo());
            if (child.getIcon() !=null&&child.getIcon().trim().length() != 0){
                Picasso.with(context).load(ConfigInfo.ApiUrl+child.getIcon()).placeholder(R.drawable.zhanweifu)
                        .error(R.drawable.default_user_photo).into(((MyViewHolder)viewHolder).ivUserPhoto);
            }else{
                Picasso.with(context).load(R.drawable.default_user_photo).into(((MyViewHolder)viewHolder).ivUserPhoto);
            }

            ((MyViewHolder) viewHolder).tvName.setText(child.getNick_name());
            ((MyViewHolder) viewHolder).tvTime.setText(child.getAdd_time());

            ((MyViewHolder) viewHolder).ivUserPhoto.setOnClickListener(mListener);
            ((MyViewHolder) viewHolder).ivComment.setOnClickListener(mListener);
            ((MyViewHolder) viewHolder).llLayout.setOnClickListener(mListener);

        }
    }

    /**
     * 获取网络图片（轮播图）
     */
//    public void getBitmap(MyAdGallery myAdGallery){
//        mMyAdGallery = myAdGallery;
//        String url = ConfigInfo.NETWORK_CYCLE_PHOTO;
//        Log.i("Gale log", "获取网络图片=" + url);
//        HttpUtils.doGetAsyn(url, handler, new HttpUtils.CallBack() {
//            public void onRequestComplete(String result) {
//                //Log.i("Gale log", "获取网络图片=" + result);
//                try {
//                    JSONArray jsonArray = new JSONArray(result);
//                    bitmap = new String[jsonArray.length()];
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        bitmap[i] = ConfigInfo.ApiUrl+jsonArray.getJSONObject(i).getString("Url");
//                    }
//                    handler.sendEmptyMessage(0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }

/**
 * 头部轮播图的东西，现在暂时不要了
 */
//    public int getRealPosition(RecyclerView.ViewHolder holder) {
//        int position = holder.getLayoutPosition();
//        return mHeaderView == null ? position : position - 1;
//    }

    /**
     * 获得item的数量
     * @return
     */
    /**
     * 头部轮播图的东西，现在暂时不要了
     */
    @Override
    public int getItemCount() {
        return childList.size();
//        return mHeaderView==null?childList.size():childList.size()+1;头部轮播图的东西，现在暂时不要了
    }




    class VrViewHolder extends RecyclerView.ViewHolder{
        View view;
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView ivCover;
//        private VideoView videoView;

        public VrViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//            tvContent = (TextView) view.findViewById(R.id.tvContent);
//            ivCover = (ImageView) view.findViewById(R.id.ivConver);
//            videoView = (VideoView) view.findViewById(R.id.surface_view);
        }
    }

    /**
     * 头部轮播图的东西，现在暂时不要了
     */
//    class HeaderHolder extends RecyclerView.ViewHolder{
//        public MyAdGallery myAdGallery;
//
//        public HeaderHolder(View itemView) {
//            super(itemView);
//            myAdGallery = (MyAdGallery) itemView.findViewById(R.id.MyAdGallery);
//        }
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        JCVideoPlayerStandard jcVideoPlayer;
        private TextView tvTitle;
//        private TextView tvContent;
        private CircleImageView ivUserPhoto;
        private TextView tvName;
        private TextView tvTime;
        private ImageView ivComment;
        private ImageView ivLike;
        private TextView tvAttentionCount;
        private TextView tvLikeCount;
        private TextView tvCommentCount;
        private LinearLayout llLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            jcVideoPlayer = (JCVideoPlayerStandard) itemView.findViewById(R.id.videoplayer);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivUserPhoto = (CircleImageView) itemView.findViewById(R.id.ivAuthorPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivComment = (ImageView) itemView.findViewById(R.id.ivComment);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLove);
            tvAttentionCount = (TextView) itemView.findViewById(R.id.tv_attention_count);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tv_like_count);
            tvCommentCount = (TextView) itemView.findViewById(R.id.tv_comment_count);
            llLayout = (LinearLayout) itemView.findViewById(R.id.ll_layout);
        }
    }

    class MyOnClickListener implements View.OnClickListener{
        Intent intent;

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ivAuthorPhoto:
                    intent = new Intent(context,TeacherInfoActivity.class);
                    CommonUtils.startActivity(context,intent);
                    break;
                case R.id.ivComment:
                    intent = new Intent(context,PlayVedioActivity.class);
                    intent.putExtra("vedio_url",vedioUrl);
                    intent.putExtra("vedio_title",vedioTitle);
                    intent.putExtra("vedio_content",vedioContent);
                    CommonUtils.startActivity(context,intent);
                    break;
                case R.id.ivLove:

                    break;
                case R.id.ll_layout:
                    intent = new Intent(context,PlayVedioActivity.class);
                    intent.putExtra("vedio_url",vedioUrl);
                    intent.putExtra("vedio_title",vedioTitle);
                    intent.putExtra("vedio_content",vedioContent);
                    CommonUtils.startActivity(context,intent);
                    break;
            }

        }
    }

}