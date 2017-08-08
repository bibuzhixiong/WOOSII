package sochat.so.com.android.version;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import sochat.so.com.android.R;
import sochat.so.com.android.dialog.UpdateDialog;
import sochat.so.com.android.utils.HttpUtils;
import sochat.so.com.android.utils.MyToast;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager {
	/* 下载中 */
	//private static final int DOWNLOAD = 1;
	/* 已经是最新版本 */
	private static final int IS_NEW = 3;
	/* 不是最新版本 */
	private static final int NOT_NEW = 4;
	/* 是否正在更新*/
	private static boolean isLoading = false;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 保存解析的XML信息 */
	HashMap<String, String> mHashMap;
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/* 是否取消更新 */
	private boolean cancelUpdate = false;

	private Context mContext;
	
	private NotificationCompat.Builder mBuilder;
	
	private Dialog mDownloadDialog;
	private Dialog progressDialog;

	private int length = 0; // 用来保存更新的apk的文件字节大小

	private String update_content; // 用来保存更新的内容

	private boolean is_new=false; // 是否提示已经是最新版本了
	
	private int mProgress=0;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private MyToast toast ;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOT_NEW:
				if(!isLoading){
					isLoading=true;
					showNoticeDialog();
				}
				break;
			case IS_NEW:
				toast.makeShortToast(mContext,mContext.getResources().getString(R.string.soft_update_no));
				break;
			case DOWNLOAD_FINISH:
				isLoading=false;
				mBuilder.setContentTitle("下载完成");
				// 安装文件
				installApk();
				mNotificationManager.cancelAll();
				break;
			case ConfigInfo.CONNECT_ERROR:
				if (progressDialog != null) {
					progressDialog.dismiss();
					toast.makeShortToast(mContext,msg.obj.toString());
				}
				break;
			default:
				break;
			}
		};
	};
	
	

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mBuilder.setContentTitle("下载中");
			setNotify(msg.what);
		}
	};
	
	public UpdateManager(Context context, boolean b) {
		this.mContext = context;
		this.is_new=b;
		initNotify();
	}

	/**
	 * 检测软件更新
	 */
	public void checkUpdate(final Dialog progressDialog) {
		this.progressDialog = progressDialog;
		HttpUtils.doGetAsyn(null,false,ConfigInfo.CHECK_UPDATE_URL, mHandler,
				new HttpUtils.CallBack() {
					public void onRequestComplete(String str_xml) {
						if (str_xml != null && !str_xml.equals("")) {
							InputStream inStream = null;
							try {
								inStream = new ByteArrayInputStream(str_xml.getBytes("UTF-8"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							if (isUpdate(inStream)) {
								// 消除提示对话框
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								// 不是最新版本要更新
								mHandler.sendEmptyMessage(NOT_NEW);

							} else {
								if (progressDialog != null) {
									progressDialog.dismiss();
									if(is_new){
										// 是最新版本，不用更新
										mHandler.sendEmptyMessage(IS_NEW);
									}
								}

							}
						}
					}
				});
	}
	/** 初始化通知栏 */
	@SuppressWarnings("static-access")
	@SuppressLint("InlinedApi")
	private void initNotify() {
		mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setContentIntent(getDefalutIntent(0))
				// .setNumber(number)//显示数量
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				// Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
				.setDefaults(Notification.DEFAULT_LIGHTS)
				// requires VIBRATE permission
				.setSmallIcon(R.drawable.share_icon);
	}
	
	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:  
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT  
	 * 点击去除： Notification.FLAG_AUTO_CANCEL 
	 */
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(mContext, 1, new Intent(), flags);
		return pendingIntent;
	}
	/**
	 * 检查软件是否有更新版本
	 * 
	 * @return
	 */
	private boolean isUpdate(InputStream inStream) {
		// 获取当前软件版本
		int versionCode = getVersionCode(mContext);
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != mHashMap) {
			int serviceCode = 0;
			if (mHashMap.get("version") != null) {
				serviceCode = Integer.valueOf(mHashMap.get("version"));
			}
			if (mHashMap.get("size") != null) {
				length = Integer.valueOf(mHashMap.get("size"));// 设置要下载的更新文件的大小
			}
			if (mHashMap.get("update_content_" + serviceCode) != null) {
				update_content = mHashMap.get("update_content_" + serviceCode)
						.toString();// 设置更新的内容
			}
			Log.i("Gale log", "serviceCode=" + serviceCode);
			// 版本判断
			if (serviceCode > versionCode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			String packageName = context.getPackageName();
			versionCode = context.getPackageManager().getPackageInfo(
					packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 显示软件更新对话框
	 */
	private void showNoticeDialog() {
		// 第一种对话框
		DialogInterface.OnClickListener positiveListen = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 显示下载对话框
				showDownloadDialog();
			}
		};
		DialogInterface.OnClickListener negativeListen = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		UpdateDialog.showAlertDialogWithTwo(mContext,
				mContext.getString(R.string.soft_update_title), update_content,
				mContext.getString(R.string.soft_update_updatebtn),
				mContext.getString(R.string.soft_update_later), positiveListen,
				negativeListen);
		// 第二种对话框,构造对话框
		/*
		 * AlertDialog.Builder builder = new Builder(mContext);
		 * builder.setTitle(R.string.soft_update_title);
		 * //builder.setMessage(R.string.soft_update_info);
		 * builder.setMessage(update_content); // 更新
		 * builder.setPositiveButton(R.string.soft_update_updatebtn, new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * dialog.dismiss(); // 显示下载对话框 showDownloadDialog(); } }); // 稍后更新
		 * builder.setNegativeButton(R.string.soft_update_later, new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * dialog.dismiss(); } }); Dialog noticeDialog = builder.create();
		 * noticeDialog.show();
		 */
	}

	/**
	 * 显示软件下载对话框
	 */
	@SuppressLint("InflateParams")
	private void showDownloadDialog() {
		// 现在文件
		downloadApk();
	}

	/** Notification的ID */
	int notifyId = 102;
	/** Notification管理 */
	public NotificationManager mNotificationManager;

	/** 设置下载进度 */
	public void setNotify(int progress) {
		mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条
		mNotificationManager.notify(notifyId, mBuilder.build());
	}
	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new downloadApkThread().start();
	}

	/**
	 * 下载文件线程
	 * 
	 * @author coolszy
	 * @date 2012-4-26
	 * @blog http://blog.92coding.com
	 */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()+ "/";
					mSavePath = sdpath + "download";
					Log.i("Gale log","apk地址:"+mSavePath);
					URL url = new URL(mHashMap.get("url"));
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					// 设置通用的请求属性
					conn.setRequestProperty("Accept-Encoding", "identity");
					// 获取文件大小
					conn.connect();
					// 创建输入流
					InputStream is = conn.getInputStream();
					Log.i("Gale log", "is="+is.available());
					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int numread = is.read(buf);
						count += numread;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						if(progress>mProgress){
							mProgress=progress;
							Log.i("Gale log", "progress="+progress);
							// 更新进度
							//mHandler.sendEmptyMessage(DOWNLOAD);
							handler.sendEmptyMessage(progress);
						}
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				Message handle_msg = new Message();
				handle_msg.what = Integer.valueOf(ConfigInfo.CONNECT_ERROR);
				handle_msg.obj = "更新版本失败,原因：" + e.getCause().getMessage();
				mHandler.sendMessage(handle_msg);
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				Message handle_msg = new Message();
				handle_msg.what = Integer.valueOf(ConfigInfo.CONNECT_ERROR);
				handle_msg.obj = "更新版本失败,原因：无法获取服务器文件。";
				mHandler.sendMessage(handle_msg);
				e.printStackTrace();
			} catch (IOException e) {
				Message handle_msg = new Message();
				handle_msg.what = Integer.valueOf(ConfigInfo.CONNECT_ERROR);
				String msg = e.getCause().toString();
				handle_msg.obj = "更新版本失败,原因：" + msg;
				mHandler.sendMessage(handle_msg);
				e.printStackTrace();
			}
			// 取消下载对话框显示
//			mDownloadDialog.dismiss();
		}
	};

	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		// 取消preference的内容
		SharedPreferences preferences = mContext.getSharedPreferences(
				SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstEnter", true);
		editor.commit();
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
