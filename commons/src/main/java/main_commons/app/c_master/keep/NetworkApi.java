package main_commons.app.c_master.keep;

import android.support.annotation.NonNull;

import java.util.Map;

import okhttp3.ResponseBody;
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
    Call<ResponseBody> makePost(@NonNull @Url String url,
                                @NonNull @FieldMap Map<String, String> fields);

    @GET
    Call<ResponseBody> makeGet(@NonNull @Url String url,
                               @NonNull @QueryMap Map<String, String> queries);

}
