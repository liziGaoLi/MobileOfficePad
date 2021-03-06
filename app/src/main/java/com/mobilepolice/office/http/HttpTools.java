package com.mobilepolice.office.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpTools {
    private static OkHttpClient client;
    private static final HashMap<Class<?>, Object> interfaces = new HashMap<>();

    public static OkHttpClient okHttpClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            builder.connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS);
            client = builder.build();
        }
        return client;
    }

    public static String login(String token) throws IOException {


        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "strBill=" + token);
        Request request = new Request.Builder()
                .url("http://192.168.20.230:8081/uas/sso/singlesignoncontrol/checkbill.do")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = okHttpClient().newCall(request).execute();
        return response.body().string();
    }

    public static <T> T build(Class<? extends T> iInterface ) {
        T temp = (T) interfaces.get(iInterface);
        if (temp == null) {
            temp = new Retrofit.Builder()
                    .baseUrl("http://192.168.20.228:7098/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient())
                    .build()
                    .create(iInterface);
            interfaces.put(iInterface, temp);
        }
        return temp;
    }

}
