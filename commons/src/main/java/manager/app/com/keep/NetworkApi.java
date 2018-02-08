package manager.app.com.keep;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface NetworkApi {

    /**
     * device
     */
    @FormUrlEncoded
    @POST("device")
    Call<Object> register(@Field("push_token") String fcmToken,
                          @Field("os_version") String osVersion);

    @FormUrlEncoded
    @POST
    Call<String> makePost(@Url String url,
                          @FieldMap Map<String, String> fields);

    @GET
    Call<String> makeGet(@Url String url,
                         @QueryMap Map<String, String> queries);

}
