package sochat.so.com.android.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.umeng.socialize.utils.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * Asynchronous HTTP connections
 *
 * @author Greg Zavitz & Joseph Roth
 */
public class HttpConnection implements Runnable {

    public static final int DID_START = 0;
    public static final int DID_ERROR = 1;
    public static final int DID_SUCCEED = 2;

    private static final int GET = 0;
    private static final int POST = 1;
    private static final int PUT = 2;
    private static final int DELETE = 3;
    private static final int BITMAP = 4;

    private String url;
    private int method;
    private String data;

    private File uploadfile;
    private CallbackListener listener;

    private HttpClient httpClient;
    private String fileKey;

    public void create(int method, String url, String data ,File uploadfile,
                       CallbackListener listener) {
        this.method = method;
        this.url = url;
        this.data = data;
        this.uploadfile = uploadfile;
        this.listener = listener;
        ConnectionManager.getInstance().push(this);
    }

    public void get(String url) {
        create(GET, url, null, null, listener);
    }

    public void post(String url, String data,String fileKey, File uploadfile,
                     CallbackListener listener) {
        this.fileKey = fileKey;
        create(POST, url, data ,uploadfile, listener);
    }

    public void put(String url, String data) {
        create(PUT, url, data, null, listener);
    }

    public void delete(String url) {
        create(DELETE, url, null, null, listener);
    }

    public void bitmap(String url) {
        create(BITMAP, url, null, null, listener);
    }

    public interface CallbackListener {
        public void callBack(String result);
    }

    private static final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case HttpConnection.DID_START: {
                    break;
                }
                case HttpConnection.DID_SUCCEED: {
                    CallbackListener listener = (CallbackListener) message.obj;
                    Object data = message.getData();
                    if (listener != null) {
                        if (data != null) {
                            Bundle bundle = (Bundle) data;
                            String result = bundle.getString("callbackkey");
                            listener.callBack(result);
                        }
                    }
                    break;
                }
                case HttpConnection.DID_ERROR: {
                    break;
                }
            }
        }
    };

    public void run() {
        // handler.sendMessage(Message.obtain(handler,
        // HttpConnection.DID_START));
        httpClient = getHttpClient();
        try {
            HttpResponse httpResponse = null;
            switch (method) {
                case GET:
                    HttpGet httpGet = new HttpGet(url);
				/*Log.i("Gale log","---------开始上传---------");
				MultipartEntity reqEntity = new MultipartEntity();
				if (uploadfile != null) {
					FileBody bin = new FileBody(uploadfile);
					// uploadfile为请求后台的File upload;属性
					reqEntity.addPart("attach", bin);
				}
				// param为请求后台的普通参数;属性
				reqEntity.addPart("userInformation", new StringBody(data));
				httpGet.se*/
                    httpResponse = httpClient.execute(httpGet);
                    if (isHttpSuccessExecuted(httpResponse)) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        this.sendMessage(result);
                    } else {
                        this.sendMessage("fail");
                    }
                    break;
                case POST:
                    HttpPost httpPost = new HttpPost(url);
                    Log.i("Gale log","---------开始上传---------");
                    MultipartEntity reqEntity = new MultipartEntity();
                    if (uploadfile != null) {
                        FileBody bin = new FileBody(uploadfile);
                        // uploadfile为请求后台的File upload;属性
                        reqEntity.addPart(fileKey, bin);
                    }
                    // param为请求后台的普通参数;属性
                    reqEntity.addPart("userInformation", new StringBody(data));
                    Log.i("Gale log","---------上传结束---------");
                    httpPost.setEntity(reqEntity);
                    httpResponse = httpClient.execute(httpPost);
                    if (isHttpSuccessExecuted(httpResponse)) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        Log.i("Gale log","上传结果="+result);
                        this.sendMessage(result);
                    } else {
                        Log.i("Gale log","上传结果="+EntityUtils.toString(httpResponse.getEntity()));
                        this.sendMessage("fail");
                    }
                    break;
            }
        } catch (Exception e) {
            this.sendMessage("fail");
        }
        ConnectionManager.getInstance().didComplete(this);
    }

    private void sendMessage(String result) {
        Message message = Message.obtain(handler, DID_SUCCEED, listener);
        Bundle data = new Bundle();
        data.putString("callbackkey", result);
        message.setData(data);
        handler.sendMessage(message);

    }

    public static DefaultHttpClient getHttpClient() {
        HttpParams httpParams = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
        HttpConnectionParams.setSoTimeout(httpParams, 20000);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
        HttpProtocolParams.setUseExpectContinue(httpParams, true);
        // HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }

    public static boolean isHttpSuccessExecuted(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        Log.i("Gale log","statusCode="+statusCode);
        return (statusCode > 199) && (statusCode < 400);
    }

}