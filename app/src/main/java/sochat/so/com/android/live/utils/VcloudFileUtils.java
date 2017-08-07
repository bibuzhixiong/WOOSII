package sochat.so.com.android.live.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.livestreaming.CapturePreviewController;
import sochat.so.com.android.live.livestreaming.MediaCaptureWrapper;


/**
 * Created by zhukkun on 12/9/16.
 */
public class VcloudFileUtils {

    /*用户配置 ---------------*/
    /**可更改为用户的应用名  根目录下/vcloud文件夹 **/
    private static final String FILE_DIR_NAME= "vcloud";
    /**请用户将伴音文件拷贝至 assets文件夹 中 ASSET_FILEDIR_MIX_AUDIO 目录下**/
    private static final String ASSET_FILEDIR_MIX_AUDIO = "mixAudio";
    /**请用户将水印文件拷贝至 assets文件夹 中 ASSET_FILEDIR_WATERMARK 目录下**/
    private static final String ASSET_FILEDIR_WATERMARK = "waterMark";
    /**请用户将涂鸦文件拷贝至 assets文件夹 中 ASSET_FILEDIR_GRAFFITI 目录下**/
    private static final String ASSET_FILEDIR_GRAFFITI = "graffiti";
    /*用户配置 -----------------*/

    private static final String SCREENSHOT_FILE_DIR = "Screenshots"; //截屏文件夹
    private static final String MP3_FILE_DIR = "mp3"; //伴音相关文件夹

    private static final String GRAFFITI_FILE_NAME = "graffiti.bmp";
    private static final String SCREENSHOT_FILE_NAME_SUFFIX = "ScreenShot.jpg";//视频截图文件名

    private static final String WATER_MARK_FILE_NAME = "watermark";

    private static final String WATER_MARK_HD_SUFFIX="_hd.png";
    private static final String WATER_MARK_SD_SUFFIX ="_sd.png";
    private static final String WATER_MARK_LD_SUFFIX ="_ld.png";

    private static String mVcloudFileDirPath; //vcloud 文件目录
    private static String mMp3FileDir;
    private static String mScreenShotFileDir;
    private static String mWaterMarkFilePathPre;
    private static String mGraffitiFilePath;
    private static boolean hasInit; //若为初始化,路径可能未设置,故报错

    private static VcloudFileUtils instance;
    private Context mContext;

    /**
     *
     * @param context 注意,请使用getApplicationContext(),防止单例造成内存泄露
     * @return
     */
    public static VcloudFileUtils getInstance(Context context){
        if (instance == null) {
            synchronized (VcloudFileUtils.class) {
                if (instance == null) {
                    instance = new VcloudFileUtils(context);
                }
            }
        }
        return instance;
    }

    private VcloudFileUtils(Context context){
        mContext = context;
        String state = Environment.getExternalStorageState();
        File fileDir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //fileDir = context.getExternalFilesDir(null);
        } else {
            //fileDir = context.getFilesDir();
        }
        //保存至根目录下 通常为 sdcard
        if (fileDir == null) {
            fileDir = Environment.getExternalStorageDirectory();
        }
        if(mVcloudFileDirPath == null) {
            mVcloudFileDirPath = Environment.getExternalStorageDirectory().getPath() + File.separator + FILE_DIR_NAME + File.separator;
        }

        mMp3FileDir = mVcloudFileDirPath + MP3_FILE_DIR;
        createDir(mMp3FileDir);
        mScreenShotFileDir = mVcloudFileDirPath + SCREENSHOT_FILE_DIR;
        createDir(mScreenShotFileDir);

        mWaterMarkFilePathPre = mVcloudFileDirPath  + WATER_MARK_FILE_NAME;
        mGraffitiFilePath = mVcloudFileDirPath  + GRAFFITI_FILE_NAME;
    }

    private static void createDir(String dir) {
        File file = new File(dir);
        if (file.exists() && !file.isDirectory())
            file.delete();
        file.mkdirs();
    }

    public void init() {
        if(!hasInit) {
            copyAsset2FileDir(ASSET_FILEDIR_MIX_AUDIO, mMp3FileDir);
            handleWaterMark();
            copyAsset2FilePath(ASSET_FILEDIR_GRAFFITI, mGraffitiFilePath);
            hasInit = true;
        }
    }

    //注意,此处是根据竖屏推流,得出水印图片的宽度,横屏推流需要改变方法
    public void handleWaterMark(){
        AssetManager assetManager = mContext.getAssets();
        String[] waterMarkFiles = null;
        try {
            waterMarkFiles = assetManager.list(ASSET_FILEDIR_WATERMARK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(waterMarkFiles == null) return;
        for (String filename: waterMarkFiles) {
            if(filename.endsWith(".png")||filename.endsWith(".jpg")){
                try {
                    InputStream in = assetManager.open(ASSET_FILEDIR_WATERMARK+ "/" + filename);
                    if(in!=null){
                        Bitmap bitmap = BitmapFactory.decodeStream(in);

                        for (int quality = 1; quality <= 3 ; quality++) {
                            String outFilePath = getWaterMarkFilePath(quality);
                            int previewWidth = MediaCaptureWrapper.SD_HEIGHT;
                            switch (quality){
                                case 1:
                                    previewWidth = MediaCaptureWrapper.LD_HEIGHT;
                                    break;
                                case 2:
                                    previewWidth = MediaCaptureWrapper.SD_HEIGHT;
                                    break;
                                case 3:
                                    previewWidth = MediaCaptureWrapper.HD_HEIGHT;
                                    break;
                            }
                            int outWidth = (int)(previewWidth * CapturePreviewController.PERCENT_OF_WATERMARK);
                            int originWidth = bitmap.getWidth();
                            int originHeight = bitmap.getHeight();
                            int outHeight = (int) (originHeight* outWidth/(float)originWidth);
                            if(outWidth%2!=0) outWidth++; //水印图片的像素必须是偶数
                            if(outHeight%2!=0) outHeight++;
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
                            File outFile = new File(outFilePath);
                            if(outFile.exists()){
                                outFile.delete();
                            }
                            FileOutputStream out =new FileOutputStream(outFile);
                            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        }
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拷贝assets文件 至应用目录
     * @param assetFileName such as "waterMark", "mixAudio", "graffiti"
     */
    private void copyAsset2FileDir(String assetFileName, String fileDir) {

        AssetManager assetManager = mContext.getAssets();

        String[] files = null;

        try {
            files = assetManager.list(assetFileName);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if(files == null) return;
        for (String filename : files) {
            try {
                InputStream in = assetManager.open(assetFileName + "/" + filename);
                File outFile = new File(fileDir, filename);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file", e);
            }
        }
    }

    /**
     * 拷贝assets文件 至文件路径
     * @param assetFileName such as "waterMark", "mixAudio", "graffiti"
     */
    private void copyAsset2FilePath(String assetFileName, String outFilePath) {
        AssetManager assetManager = mContext.getAssets();

        String[] files = null;

        try {
            files = assetManager.list(assetFileName);
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if(files == null) return;
        for (String filename : files) {
            try {
                InputStream in = assetManager.open(assetFileName + "/" + filename);
                File outFile = new File(outFilePath);
                FileOutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file", e);
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //获取截屏图像的数据
    public static boolean getScreenShotByteBuffer(byte[] screenShotByteBuffer) {
        boolean shotSuc = true;
        FileOutputStream outStream = null;

        String screenShotFilePath = getScreenshotFilePath();
        try {
            if (screenShotFilePath != null) {
                outStream = new FileOutputStream(String.format(screenShotFilePath));
                outStream.write(screenShotByteBuffer);
                outStream.close();
                sendUpdateBroadcast(screenShotFilePath);
            } else {
                shotSuc = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            shotSuc = false;
        } catch (IOException e) {
            e.printStackTrace();
            shotSuc = false;
        }
        return shotSuc;
    }

    public static void sendUpdateBroadcast(String screenShotFilePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(screenShotFilePath));
        intent.setData(uri);
        DemoCache.getContext().sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
    }

    public static String getScreenshotFileDir(){
        if(!hasInit) throw new NullPointerException("VcloudFileUilts must init first");
        return mScreenShotFileDir;
    }

    public static String getScreenshotFilePath(){
        return mScreenShotFileDir+  File.separator + System.currentTimeMillis() + SCREENSHOT_FILE_NAME_SUFFIX;
    }

    public static String getMp3FileDir(){
        if(!hasInit) throw new NullPointerException("VcloudFileUilts must init first");
        return mMp3FileDir;
    }

    public static String getGraffitiFilePath(){
        if(!hasInit) throw new NullPointerException("VcloudFileUilts must init first");
        return mGraffitiFilePath;
    }

    /**
     *
     * @param quality 3hd, 2sd, 1ld
     * @return
     */
    public static String getWaterMarkFilePath(int quality){
        switch (quality){
            case 1: //ld
                return mWaterMarkFilePathPre + WATER_MARK_LD_SUFFIX;
            case 2: //sd
                return mWaterMarkFilePathPre + WATER_MARK_SD_SUFFIX;
            case 3: //hd
                return mWaterMarkFilePathPre + WATER_MARK_HD_SUFFIX;
        }
        return null;
    }
}
