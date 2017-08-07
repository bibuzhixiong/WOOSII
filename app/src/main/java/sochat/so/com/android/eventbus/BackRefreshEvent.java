package sochat.so.com.android.eventbus;

/**
 * Created by Administrator on 2017/3/30.
 */

public class BackRefreshEvent {
    private boolean mMsg;
    public BackRefreshEvent(boolean msg) {
        mMsg = msg;
    }
    public boolean getMsg(){
        return mMsg;
    }
}
