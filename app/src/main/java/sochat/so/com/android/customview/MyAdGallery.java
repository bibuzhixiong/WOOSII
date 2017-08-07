package sochat.so.com.android.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sochat.so.com.android.R;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

//import android.util.Log;

/**
 * 无缝轮播
 */
@SuppressWarnings("deprecation")
public class MyAdGallery extends Gallery implements AdapterView.OnItemClickListener,
		AdapterView.OnItemSelectedListener, OnTouchListener {
	/** 显示的Activity */
	private Context mContext;
	/** 条目单击事件接口 */
	private MyOnItemClickListener mMyOnItemClickListener;
	/** 图片切换时间 */
	private int mSwitchTime;
	/** 自动滚动的定时器 */
	private Timer mTimer;
	/** 圆点容器 */
	private LinearLayout mOvalLayout;
	/** 当前选中的数组索引 */
	private int curIndex = 0;
	/** 上次选中的数组索引 */
	private int oldIndex = 0;
	/** 圆点选中时的背景ID */
	private int mFocusedId;
	/** 圆点正常时的背景ID */
	private int mNormalId;
	/** 图片资源ID组 */
	private int[] mAdsId;
	/** 图片网络路径数组 */
	private String[] mUris;
	/** ImageView组 */
	List<ImageView> listImgs;
	public MyAdGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyAdGallery(Context context) {
		super(context);
	}

	public MyAdGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * @param context
	 *            显示的Activity ,不能为null
	 * @param mris
	 *            图片的网络路径数组 ,为空时 加载 adsId
	 * @param adsId
	 *            图片组资源ID ,测试用
	 * @param switchTime
	 *            图片切换时间 写0 为不自动切换
	 * @param ovalLayout
	 *            圆点容器 ,可为空
	 * @param focusedId
	 *            圆点选中时的背景ID,圆点容器可为空写0
	 * @param normalId
	 *            圆点正常时的背景ID,圆点容器为空写0
	 */
	public void start(Context context, String[] mris, int[] adsId,
			int switchTime, LinearLayout ovalLayout, int focusedId, int normalId) {
		this.mContext = context;
		this.mUris = mris;
		this.mAdsId = adsId;
		this.mSwitchTime = switchTime;
		this.mOvalLayout = ovalLayout;
		this.mFocusedId = focusedId;
		this.mNormalId = normalId;
		ininImages();// 初始化图片组
		setAdapter(new AdAdapter());
		this.setOnItemClickListener(this);
		this.setOnTouchListener(this);
		this.setOnItemSelectedListener(this);
		this.setSoundEffectsEnabled(false);
		this.setAnimationDuration(700); // 动画时间
		this.setUnselectedAlpha(1); // 未选中项目的透明度
		// 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
		setSpacing(0);
		// 取靠近中间 图片数组的整倍数
		setSelection((getCount() / 2 / listImgs.size()) * listImgs.size()); // 默认选中中间位置为起始位置
		setFocusableInTouchMode(true);
		initOvalLayout();// 初始化圆点
		startTimer();// 开始自动滚动任务
	}

	/** 初始化图片组 */
	private void ininImages() {
		listImgs = new ArrayList<ImageView>(); // 图片组
		int len = mUris != null ? mUris.length : mAdsId.length;
		for (int i = 0; i < len; i++) {
			ImageView imageview = new ImageView(mContext); // 实例化ImageView的对象
			imageview.setScaleType(ImageView.ScaleType.FIT_XY); // 设置缩放方式
			imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			if (mUris == null) {// 本地加载图片
				imageview.setImageResource(mAdsId[i]); // 为ImageView设置要显示的图片
			} else {
				// 网络加载图片
				try {
					Picasso.with(getContext()).load(mUris[i]).memoryPolicy(NO_CACHE, NO_STORE).placeholder(R.drawable.zhanweifu).error(R.drawable.jiazaicuowu).into(imageview);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			listImgs.add(imageview);
		}
	}

	/**
	 * 初始化圆点
	 */
	private void initOvalLayout() {
		if (mOvalLayout != null && listImgs.size() < 2) {// 如果只有一第图时不显示圆点容器
			mOvalLayout.getLayoutParams().height = 0;
		} else if (mOvalLayout != null) {
			// 圆点的大小是 圆点窗口的 70%;
			int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.7);
			// 圆点的左右外边距是 圆点窗口的 50%;
			int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.4);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Ovalheight, Ovalheight);
			layoutParams.setMargins(Ovalmargin, 0, Ovalmargin, 0);
			for (int i = 0; i < listImgs.size(); i++) {
				View v = new View(mContext); // 员点
				v.setLayoutParams(layoutParams);
				v.setBackgroundResource(mNormalId);
				mOvalLayout.addView(v);
			}
			// 选中第一个
			mOvalLayout.getChildAt(0).setBackgroundResource(mFocusedId);
		}
	}

	/**
	 * 无限循环适配器 
	 */
	class AdAdapter extends BaseAdapter {
		public int getCount() {
			if (listImgs.size() < 2)// 如果只有一张图时不滚动
				return listImgs.size();
			return Integer.MAX_VALUE;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			return listImgs.get(position % (listImgs.size()>0?listImgs.size():1)); // 返回ImageView
		}
		public Object getItem(int position) {
			return null;
		}
		public long getItemId(int position) {
			return position;
		}
	}


	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else { // 检查是否往右滑动
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;

	}

	/**
	 * 检查是否往左滑动 
	 */
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > (e1.getX() + 50);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			startTimer();// 开始自动滚动任务
		} else {
			stopTimer();// 停止自动滚动任务
		}
		return false;
	}

	/**
	 * 图片切换事件 
	 */
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		curIndex = position % listImgs.size();
		if (mOvalLayout != null && listImgs.size() > 1) { // 切换圆点
			if(mOvalLayout.getChildAt(oldIndex) !=null){
				mOvalLayout.getChildAt(oldIndex).setBackgroundResource(mNormalId); // 圆点取消
				mOvalLayout.getChildAt(curIndex).setBackgroundResource(mFocusedId);// 圆点选中
				oldIndex = curIndex;
			}
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/**
	 * 项目点击事件 
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (mMyOnItemClickListener != null) {
			mMyOnItemClickListener.onItemClick(curIndex);
		}
	}

	/** 设置项目点击事件监听器 */
	public void setMyOnItemClickListener(MyOnItemClickListener listener) {
		mMyOnItemClickListener = listener;
	}

	/** 项目点击事件监听器接口 */
	public interface MyOnItemClickListener {
		/**
		 * @param curIndex
		 *            //当前条目在数组中的下标
		 */
		void onItemClick(int curIndex);
	}

	/** 停止自动滚动任务 */
	public void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	/** 开始自动滚动任务 图片大于1张才滚动 */
	public void startTimer() {
		if (mTimer == null && listImgs.size() > 1 && mSwitchTime > 0) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				public void run() {
					handler.sendMessage(handler.obtainMessage(1));
				}
			}, mSwitchTime, mSwitchTime);
		}
	}


	/** 处理定时滚动任务 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 不包含spacing会导致onKeyDown()失效!!!
			// 失效onKeyDown()前先调用onScroll(null,1,0)可处理
			onScroll(null, null, 1, 0);
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
	};
	
	public List<ImageView> getListImgs() {
		return listImgs;
	}
	
}
