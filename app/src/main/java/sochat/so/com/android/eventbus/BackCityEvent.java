package sochat.so.com.android.eventbus;

/**
 * Created by Administrator on 2017/4/17.
 */

public class BackCityEvent {
    private String mMsg;
    private String region_id;

    public BackCityEvent(String mMsg,String region_id){
        this.mMsg = mMsg;
        this.region_id =region_id;
    }

    public String getMsg(){
        return mMsg;
    }
    public String getRegion_id(){
        return region_id;
    }
}
