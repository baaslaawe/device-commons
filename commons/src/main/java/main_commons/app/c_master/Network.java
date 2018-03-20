package main_commons.app.c_master;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import main_commons.app.c_master.keep.NetworkApi;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class Network {

    private NetworkApi retrofit;

    private final Gson gson;
    private final String deviceId;
    private final boolean isDebugMode;

    public Network(String baseUrl, Gson gson, String deviceId, boolean isDebugMode) {
        this.gson = gson;
        this.deviceId = deviceId;
        this.isDebugMode = isDebugMode;
        this.retrofit = createRetrofit(baseUrl);
    }

    public NetworkApi getRetrofit() {
        return retrofit;
    }

    private NetworkApi createRetrofit(String serverUrl) {
        //enable logging
        HttpLoggingInterceptor interceptorLogging =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Timber.d(message);
                    }
                });
        interceptorLogging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //attach reg header to all requests
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    private static final String HEADER = "Authorization";

                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl.Builder urlBuilder = originalHttpUrl.newBuilder();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder()
                                .url(urlBuilder.build());
                        requestBuilder.addHeader(HEADER, deviceId);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
        if (isDebugMode) {
            clientBuilder.addInterceptor(interceptorLogging);
        }
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(NetworkApi.class);
    }
}
