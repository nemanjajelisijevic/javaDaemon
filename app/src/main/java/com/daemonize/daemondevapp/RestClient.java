package com.daemonize.daemondevapp;

import android.util.Log;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


@Daemonize(eager = true)
public class RestClient {

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    private String baseUrl = "";

    public RestClient(){}

    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T get(String url, Class<T> tClass) {
        Log.d(Thread.currentThread().getName(),"URL: "  + baseUrl + url);
        Request request = new Request.Builder()
            .url(baseUrl + url)
            .build();
        String response;
        try {
            response = client.newCall(request).execute().body().string();
            Log.d(Thread.currentThread().getName(), response);
            return gson.fromJson(response, tClass);
        } catch (IOException e) {
            Log.e(Thread.currentThread().getName(), Log.getStackTraceString(e));
        }

        return null;//TODO fuck this
    }

    @CallingThread
    public <T, K> T post(String url, K entity, Class<T> tClass) throws IllegalAccessException, InstantiationException {

        Log.d(Thread.currentThread().getName(),"URL: "  + baseUrl + url);

        Field[] fields = entity.getClass().getDeclaredFields();
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Field field : fields) {
            Object value = field.get(entity);
            if (value == null) {
                continue;
            }
            requestBodyBuilder.addFormDataPart(
                    field.getName(),
                    value.toString()
            );
        }

        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder()
                .url(baseUrl + url)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(requestBody)
                .build();

        String response;

        try {
            response = client.newCall(request).execute().body().string();
            Log.d(Thread.currentThread().getName(), "RESPONSE POST: " + response);
            return gson.fromJson(response, tClass);
        } catch (IOException e) {
            Log.e(Thread.currentThread().getName(), Log.getStackTraceString(e));
        }

        return null;
    }

    public <K, T> T put (String url, K entity, Class<T> tClass) {
        Log.d(Thread.currentThread().getName(),"URL: "  + baseUrl + url);


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");//TODO unhardcode
        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(entity));

        Request request = new Request.Builder()
                .url(baseUrl + url)
                .method("POST", RequestBody.create(null, new byte[0]))
                .put(requestBody)
                .build();

        String response;

        try {
            response = client.newCall(request).execute().body().string();
            Log.d(Thread.currentThread().getName(), "RESPONSE PUT: " + response);
            return gson.fromJson(response, tClass);
        } catch (IOException e) {
            Log.e(Thread.currentThread().getName(), Log.getStackTraceString(e));
        }

        return null;
    }



}
