package sochat.so.com.android.live.im.sever;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.common.http.NimHttpClient;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sochat.so.com.android.config.ConfigInfo;
import sochat.so.com.android.live.DemoCache;
import sochat.so.com.android.live.activity.EnterAudienceActivity;
import sochat.so.com.android.live.entity.AddVideoResponseEntity;
import sochat.so.com.android.live.entity.RoomInfoEntity;
import sochat.so.com.android.live.entity.TranscodeResponseEntity;
import sochat.so.com.android.live.entity.VideoInfoEntity;
import sochat.so.com.android.utils.DemoHelper;


/**
 * 与应用服务器通信
 */
public class DemoServerHttpClient {
    private static final String TAG = "ContactHttpClient";


//    public static final String API_SERVER = "http://app.woosii.com"; //线上
    public static final String API_SERVER = "http://vcloud.163.com/appdemo"; //线上
    //public static final String API_SERVER = "http://106.2.44.145:8181/appdemo"; //测试
//    public static final String API_SERVER = "http://106.2.44.63/appdemo"; //开发
    public static final String TEST_HOST = "vcloud.163.com"; //用于 ping ip地址,测试网络是否连通

    // code
    private static final int RESULT_CODE_SUCCESS = 200;

    // api
    private static final String API_NAME_REGISTER = "/user/reg";
    private static final String API_LOGIN = "/user/login";
    private static final String API_LOGOUT = "/user/logout";
    private static final String API_CREATE_ROOM = "/room/create";
//    private static final String API_CREATE_ROOM = "/index.php/Api/Wylive/channel";
    private static final String API_ENTER_ROOM = "/room/enter";
    private static final String API_LEAVE_ROOM = "/room/leave";
    //点播相关 api
    private static final String API_VIDEO_INFO_GET = "/vod/videoinfoget";
    private static final String API_VIDEO_ADD = "/vod/videoadd";
    private static final String API_VIDEO_DELETE = "/vod/videodelete";
    private static final String API_VIDEO_TRANS_STATE = "/vod/videotranscodestatus";

    // header
    private static final String HEADER_KEY_APP_KEY = "appkey";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_USER_AGENT = "User-Agent";

    // request login
    private static final String REQUEST_USER_NAME = "username";
    private static final String REQUEST_NICK_NAME = "nickname";
    private static final String REQUEST_PASSWORD = "password";

    //request room
    public static final String REQUEST_SID = "sid";
    public static final String REQUEST_ROOM_ID = "roomid";
    public static final String REQUEST_PULL_URL = "pullUrl";
    public static final String CID = "cid";
    public static final String REQUEST_DEVICE_ID = "deviceid";

    //request vod
    public static final String VID = "vid";
    public static final String FILE_NAME = "name";
    public static final String VIDEO_FORMAT = "format";
    public static final String TYPE = "type";

    // result
    private static final String RESULT_KEY_CODE = "code";
    private static final String RESULT_KEY_ERROR_MSG = "msg";

    private static final String RESULT_KEY_RET = "ret";
    private static final String RESULT_KEY_TOKEN = "token";
    private static final String RESULT_KEY_VOD_TOKEN = "vodtoken";
    private static final String RESULT_KEY_SID = "sid";

    public static final String RESULT_ROOMID = "roomid";
    public static final String RESULT_PUSH_URL = "pushUrl";
    public static final String RESULT_RTMP_URL = "rtmpPullUrl";
    public static final String RESULT_HLS_URL = "hlsPullUrl";
    public static final String RESULT_HTTP_URL = "httpPullUrl";
    private static final String RESULT_OWNER = "owner";
    public static final String RESULT_STATUS = "status";

    private static final String RESULT_KEY_COMPLETE_TIME = "completeTime";
    private static final String RESULT_KEY_CREATE_TIME = "createTime";
    private static final String RESULT_KEY_ORIG_URL = "origUrl";
    private static final String RESULT_KEY_UPDATE_TIME = "updateTime";
    private static final String RESULT_KEY_SNAPSHOT_URL = "snapshotUrl";
    private static final String RESULT_KEY_DOWNLOAD_ORIG_URL = "downloadOrigUrl";
    private static final String RESULT_KEY_WIDTH = "width";
    private static final String RESULT_KEY_HEIGHT = "height";
    private static final String RESULT_KEY_VIDEO_NAME = "videoName";
    private static final String RESULT_KEY_TYPE_NAME = "typeName";
    private static final String RESULT_KEY_DURATION = "duration";
    private static final String RESULT_KEY_DESCRIPTION = "description";
    private static final String RESULT_KEY_INITIAL_SIZE = "initialSize";

    public interface DemoServerHttpCallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String errorMsg);
    }

    private static DemoServerHttpClient instance;

    public static synchronized DemoServerHttpClient getInstance() {
        if (instance == null) {
            instance = new DemoServerHttpClient();
        }

        return instance;
    }

    private DemoServerHttpClient() {
        NimHttpClient.getInstance().init(DemoCache.getContext());
    }

    /**
     * 向应用服务器创建账号（注册账号）
     * 由应用服务器调用WEB SDK接口将新注册的用户数据同步到云信服务器
     */
    public void register(String account, String nickName, String password, final DemoServerHttpCallback<Void> callback) {
        String url = API_SERVER + API_NAME_REGISTER;
        //password = MD5.getStringMD5(password);
        try {
            nickName = URLEncoder.encode(nickName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, String> headers = new HashMap<>(1);
        String appKey = readAppKey();
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
//        headers.put(HEADER_USER_AGENT, "nim_demo_android");
//        headers.put(HEADER_KEY_APP_KEY, appKey);

        StringBuilder body = new StringBuilder();
        body.append(REQUEST_USER_NAME).append("=").append(account.toLowerCase()).append("&")
                .append(REQUEST_NICK_NAME).append("=").append(nickName).append("&")
                .append(REQUEST_PASSWORD).append("=").append(password);

        NimHttpClient.getInstance().execute(url, headers, body.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                    return;
                }

                try {
                    JSONObject resObj = JSONObject.parseObject(response);
                    int resCode = resObj.getIntValue(RESULT_KEY_CODE);
                    if (resCode == RESULT_CODE_SUCCESS) {
                        callback.onSuccess(null);
                    } else {
                        String error = resObj.getString(RESULT_KEY_ERROR_MSG);
                        callback.onFailed(resCode, error);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                }
            }
        });
    }


    /**
     * 向应用服务器创建账号（注册账号）
     * 由应用服务器调用WEB SDK接口将新注册的用户数据同步到云信服务器
     */
    public void login(String account, String password, final DemoServerHttpCallback<Void> callback) {
        String url = API_SERVER + API_LOGIN;

        Map<String, String> headers = new HashMap<>(1);
        String appKey = readAppKey();
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");
//        headers.put(HEADER_USER_AGENT, "nim_demo_android");
//        headers.put(HEADER_KEY_APP_KEY, appKey);

        StringBuilder body = new StringBuilder();
        body.append(REQUEST_USER_NAME).append("=").append(account.toLowerCase()).append("&")
                .append(REQUEST_PASSWORD).append("=").append(password);

        NimHttpClient.getInstance().execute(url, headers, body.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                    return;
                }

                try {
                    JSONObject resObj = JSONObject.parseObject(response);
                    int resCode = resObj.getIntValue(RESULT_KEY_CODE);
                    if (resCode == RESULT_CODE_SUCCESS) {
                        JSONObject retObj = resObj.getJSONObject(RESULT_KEY_RET);
                        DemoCache.setAccount(resObj.getString(REQUEST_USER_NAME));
                        DemoCache.setToken(retObj.getString(RESULT_KEY_TOKEN));
                        DemoCache.setVodtoken(retObj.getString(RESULT_KEY_VOD_TOKEN));
                        DemoCache.setSid(retObj.getString(RESULT_KEY_SID));
                        callback.onSuccess(null);
                    } else {
                        String error = resObj.getString(RESULT_KEY_ERROR_MSG);
                        callback.onFailed(resCode, error);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                } catch (Exception e){
                    callback.onFailed(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 向应用服务器发送登出请求
     */
    public void logout(){
        String url = API_SERVER + API_LOGOUT;

        Map<String, String> headers = new HashMap<>(1);
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");

        StringBuilder body = new StringBuilder();
        body.append(REQUEST_SID).append("=").append(DemoCache.getSid());

        NimHttpClient.getInstance().execute(url, headers, body.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                //登出不保证是否成功
            }
        });
    }

    /**
     * 主播创建房间接口
     * @param context
     * @param callback
     */
    public void createRoom(Activity context, final DemoServerHttpCallback<RoomInfoEntity> callback){
        String url = ConfigInfo.ApiUrl+"/index.php/Api/Wylive/channel?school_id="+DemoHelper.getSchool_id()+"&user_id="+ DemoHelper.getUid();

        Map<String, String> headers = new HashMap<>(1);
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");

        NimHttpClient.getInstance().execute(url, headers, null, false,new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                    return;
                }
                try {
                    JSONObject resObj = JSONObject.parseObject(response);

                    RoomInfoEntity roomInfoEntity = new RoomInfoEntity();

                    if (!TextUtils.isEmpty(resObj.getInteger("roomid")+"")){
                        roomInfoEntity.setRoomid(resObj.getInteger("roomid"));
                        roomInfoEntity.setPushUrl(resObj.getString("push_url"));
                        roomInfoEntity.setRoomtitle(resObj.getString("nick_name"));

                        DemoHelper.setLive_id(resObj.getString("live_id"));

                        callback.onSuccess(roomInfoEntity);
                    }else{
                        callback.onFailed(555, "小哥失败了");
                    }


                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                } catch (Exception e){
                    callback.onFailed(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 观众获取房间信息接口
     * @param mode
     * @param address
     * @param callback
     */
    public void getRoomInfo(int mode, String address, final DemoServerHttpCallback<RoomInfoEntity> callback){
        String url = API_SERVER + API_ENTER_ROOM;

        Map<String, String> headers = new HashMap<>(1);
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");

        StringBuilder body = new StringBuilder();
        body.append(REQUEST_SID).append("=").append(DemoCache.getSid()).append("&");
        if(mode == EnterAudienceActivity.MODE_ROOM){
            body.append(REQUEST_ROOM_ID);
        }else{
            body.append(REQUEST_PULL_URL);
        }
        body.append("=").append(address);

        NimHttpClient.getInstance().execute(url, headers, body.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    if (callback != null) {
                        callback.onFailed(code, exception != null ? exception.getMessage() : "null");
                    }
                    return;
                }
                try {
                    JSONObject resObj = JSONObject.parseObject(response);
                    int resCode = resObj.getIntValue(RESULT_KEY_CODE);
                    if (resCode == RESULT_CODE_SUCCESS) {
                        JSONObject retObj = resObj.getJSONObject(RESULT_KEY_RET);
                        RoomInfoEntity roomInfoEntity = new RoomInfoEntity();
                        roomInfoEntity.setRoomid(retObj.getInteger(RESULT_ROOMID));
                        roomInfoEntity.setPushUrl(retObj.getString(RESULT_PUSH_URL));
                        roomInfoEntity.setRtmpPullUrl(retObj.getString(RESULT_RTMP_URL));
                        roomInfoEntity.setHlsPullUrl(retObj.getString(RESULT_HLS_URL));
                        roomInfoEntity.setHttpPullUrl(retObj.getString(RESULT_HTTP_URL));
                        roomInfoEntity.setOwner(retObj.getString(RESULT_OWNER));
                        roomInfoEntity.setStatus(retObj.getInteger(RESULT_STATUS));
                        callback.onSuccess(roomInfoEntity);
                    } else {
                        String error = resObj.getString(RESULT_KEY_ERROR_MSG);
                        callback.onFailed(resCode, error);
                    }
                } catch (JSONException e) {
                    callback.onFailed(-1, e.getMessage());
                } catch (Exception e){
                    callback.onFailed(-1, e.getMessage());
                }
            }
        });
    }

    /**
     * 主播退出直播时,调用该接口,通知解散聊天室
     */
    public void anchorLeave(String roomId, final DemoServerHttpCallback<Void> callback){
//        String url = API_SERVER + API_LEAVE_ROOM;
        String url = ConfigInfo.ApiUrl+"index.php/Api/Wylive/close_video?school_id="+DemoHelper.getSchool_id();
        Map<String, String> headers = new HashMap<>(1);
        headers.put(HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded; charset=utf-8");

        StringBuilder body = new StringBuilder();
        body.append(REQUEST_SID).append("=").append(DemoCache.getSid()).append("&").append(REQUEST_ROOM_ID)
        .append("=").append(roomId);

        NimHttpClient.getInstance().execute(url, headers, body.toString(), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable exception) {
                if (code != 200 || exception != null) {
                    LogUtil.e(TAG, "register failed : code = " + code + ", errorMsg = "
                            + (exception != null ? exception.getMessage() : "null"));
                    callback.onFailed(code, exception.getMessage());
                    return;
                }
                try {
                    JSONObject resObj = JSONObject.parseObject(response);
                    int resCode = resObj.getIntValue(RESULT_KEY_CODE);
                    //得到数据
                    if (resCode == 1) {
                        callback.onSuccess(null);
                    } else {
                        String error = resObj.getString(RESULT_KEY_ERROR_MSG);
                        callback.onFailed(resCode, error);
                    }
                } catch (JSONException e) {
                } catch (Exception e){
                }
            }
        });

    }

    /**
     * 添加上传的视频ID，客户端上传成功后须调用
     * 服务端返回成功,即通知点播服务器开始转码
     * @param vid 	视频ID
     * @param name  视频文件名
     * @param type  视频类型(0:普通点播视频, 1:短视频)
     * @param callback
     */
    public void addVideo(long vid, String name, int type, final DemoServerHttpCallback<AddVideoResponseEntity> callback){
        String url = API_SERVER + API_VIDEO_ADD;
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_SID, DemoCache.getSid());
        body.put(VID, vid);
        body.put(FILE_NAME, name);
        body.put(TYPE, type);

        NimHttpClient.getInstance().execute(url, getParamsString(body), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {

                if(code!=200 || e!=null){
                    callback.onFailed(code, e!=null? e.getMessage() : "error code :" + code);
                    return;
                }

                try{
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    int resCode = jsonObject.getIntValue(RESULT_KEY_CODE);
                    if(resCode == 200) {
                        JSONObject retObj = jsonObject.getJSONObject(RESULT_KEY_RET);
                        AddVideoResponseEntity entity = new AddVideoResponseEntity();
                        entity.setTransjobstatus(retObj.getInteger("transjobstatus"));
                        entity.setVideoCount(retObj.getInteger("videoCount"));
                        JSONObject videoInfoJson = retObj.getJSONObject("videoinfo");
                        VideoInfoEntity videoInfoEntity = new VideoInfoEntity();
                        videoInfoEntity.setCompleteTime(videoInfoJson.getLongValue(RESULT_KEY_COMPLETE_TIME));
                        videoInfoEntity.setCreateTime(videoInfoJson.getLongValue(RESULT_KEY_CREATE_TIME));
                        videoInfoEntity.setOrigUrl(videoInfoJson.getString(RESULT_KEY_ORIG_URL));
                        videoInfoEntity.setSnapshotUrl(videoInfoJson.getString(RESULT_KEY_SNAPSHOT_URL));
                        videoInfoEntity.setUpdateTime(videoInfoJson.getLongValue(RESULT_KEY_UPDATE_TIME));
                        videoInfoEntity.setVid(videoInfoJson.getLongValue(VID));
                        videoInfoEntity.setDescription(videoInfoJson.getString(RESULT_KEY_DESCRIPTION));
                        videoInfoEntity.setTypeName(videoInfoJson.getString(RESULT_KEY_TYPE_NAME));
                        videoInfoEntity.setVideoName(videoInfoJson.getString(RESULT_KEY_VIDEO_NAME));
                        videoInfoEntity.setDuration(videoInfoJson.getLong(RESULT_KEY_DURATION));
                        videoInfoEntity.setInitialSize(videoInfoJson.getLong(RESULT_KEY_INITIAL_SIZE));
                        entity.setVideoInfoEntity(videoInfoEntity);
                        callback.onSuccess(entity);
                    }else{
                        callback.onFailed(resCode, jsonObject.getString(RESULT_KEY_ERROR_MSG));
                    }
                } catch (Exception exception){
                    callback.onFailed(-1, exception.getMessage());
                }
            }
        });
    }

    /**
     *
     * 删除已上传视频
     * 参数只有vid，则删除原视频及其转码视频，该视频所有信息均删除
     * 参数除vid还有format，则只删除单个转码视频
     * @param vid 视频ID
     * @param format 视频转码格式（1表示标清mp4，2表示高清mp4，3表示超清mp4，4表示标清flv，5表示高清flv，6表示超清flv，7表示标清hls，8表示高清hls，9表示超清hls）
     */
    public void videoDelete(long vid, @Nullable Integer format, final DemoServerHttpCallback<Void> callback){
        String url = API_SERVER + API_VIDEO_DELETE;
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_SID, DemoCache.getSid());
        body.put(VID, vid);
        if(format!=null){
            body.put(VIDEO_FORMAT, format);
        }

        NimHttpClient.getInstance().execute(url, getParamsString(body), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                if(code!=200 || e!=null){
                    callback.onFailed(code, e!=null? e.getMessage() : "error code :" + code);
                    return;
                }

                try{
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    int resCode = jsonObject.getIntValue(RESULT_KEY_CODE);
                    if(resCode == 200 || resCode == 1644){
                        //{"code":1644,"msg":"视频不存在"} 多端登录删除同一视频的情况
                        callback.onSuccess(null);
                    }else{
                        callback.onFailed(resCode, jsonObject.getString(RESULT_KEY_ERROR_MSG));
                    }
                } catch (Exception exception){
                    callback.onFailed(-1, exception.getMessage());
                }

            }
        });
    }

    /**
     * 获取所有已上传视频的信息。
     * 如果有vid参数，则只返回该视频信息，否则返回该用户所有视频信息。
     * @param vid 	视频ID
     * @param type 视频类型(0:普通点播视频, 1:短视频)
     */
    public void videoInfoGet(@Nullable Long vid, int type, final DemoServerHttpCallback<List<VideoInfoEntity>> callback){
        String url = API_SERVER + API_VIDEO_INFO_GET;
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_SID, DemoCache.getSid());
        if(vid != null){
            body.put(VID, vid);
        }
        body.put(TYPE, type);

        NimHttpClient.getInstance().execute(url, getParamsString(body), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                if(code!=200 || e!=null){
                    callback.onFailed(code, e!=null? e.getMessage() : "error code :" + code);
                    return;
                }
                try {
                    JSONObject jsonObj = JSONObject.parseObject(response);
                    int resCode = jsonObj.getIntValue(RESULT_KEY_CODE);
                    if(resCode== 200) {
                        JSONObject retObj = jsonObj.getJSONObject(RESULT_KEY_RET);
                        List<VideoInfoEntity> list = JSON.parseArray(retObj.get("list").toString(), VideoInfoEntity.class);
                        callback.onSuccess(list);
                    }else{
                        callback.onFailed(resCode, jsonObj.getString(RESULT_KEY_ERROR_MSG));
                    }
                }catch (Exception e1){
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }

    /**
     * 获取某上传视频的转码状态。
     * 客户端在上传成功后，调用应用服务端视频ID添加接口，客户端收到200返回码后则认为该视频已处于转码中。（服务端会发起转码请求）
     * 对于转码状态，客户端每隔1分钟向服务端发起该接口请求，获取状态。
     * @param vidList
     * @param callback
     */
    public void videoTransCodeStatus(List<Long> vidList, final DemoServerHttpCallback<List<TranscodeResponseEntity>> callback){
        String url = API_SERVER + API_VIDEO_TRANS_STATE;
        Map<String, Object> body = new HashMap<>();
        body.put(REQUEST_SID, DemoCache.getSid());
        body.put(VID, vidList);

        NimHttpClient.getInstance().execute(url, getParamsString(body), new NimHttpClient.NimHttpCallback() {
            @Override
            public void onResponse(String response, int code, Throwable e) {
                if(code!=200 || e!=null){
                    callback.onFailed(code, e!=null? e.getMessage() : "error code :" + code);
                    return;
                }
                try {

                    JSONObject jsonObject = JSONObject.parseObject(response);
                    int resCode = jsonObject.getIntValue(RESULT_KEY_CODE);
                    if(resCode == 200) {
                        JSONObject retObj = jsonObject.getJSONObject(RESULT_KEY_RET);
                        List<TranscodeResponseEntity> list = JSON.parseArray(retObj.get("list").toString(), TranscodeResponseEntity.class);
                        callback.onSuccess(list);
                    }else{
                        callback.onFailed(resCode, jsonObject.getString(RESULT_KEY_ERROR_MSG));
                    }
                }catch (Exception e1){
                    callback.onFailed(-1, e1.getMessage());
                }
            }
        });
    }

    public static String getParamsString(Map<String, Object> maps){
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Map.Entry entry : maps.entrySet()) {
            if(first) {
                first = false;
            }else{
                builder.append("&");
            }

            builder.append(entry.getKey())
                   .append("=");
            if(entry.getValue() instanceof List) {
                boolean innerFirst = true;
                for (Object object: (List)entry.getValue()) {
                    if(innerFirst){
                        innerFirst = false;
                    }else{
                        builder.append(",");
                    }
                    builder.append(object);
                }
            }else{
                builder.append(entry.getValue());
            }
        }

        return builder.toString();
    }



    public static String readAppKey() {
        try {
            ApplicationInfo appInfo = DemoCache.getContext().getPackageManager()
                    .getApplicationInfo(DemoCache.getContext().getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                return appInfo.metaData.getString("com.netease.nim.appKey");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
