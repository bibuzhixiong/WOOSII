package sochat.so.com.android.net;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by LIUYONGKUI726 on 2016-07-25.
 */
public interface MyApiService {
    @GET("index.php/Vr/Vlive/del_coll")
    Observable<IpResult> getSougu(@QueryMap Map<String, Object> maps);


    @POST("{url}")
    Observable<ResponseBody> getLLPaySmsCode(     //smssend.htm
                                                  @Path("url") String url,
                                                  @QueryMap Map<String, Object> maps);

    @POST("{url}")
    Observable<ResponseBody> checkLLPaySmsCode(     //smscheck.htm
                                                    @Path("url") String url,
                                                    @QueryMap Map<String, Object> maps);



}
