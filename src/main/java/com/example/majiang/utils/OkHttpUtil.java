package com.example.majiang.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS).build();

    public static String post(String url, String json) throws IOException {
        System.out.println("OkHttp Post URL: " + url);
        System.out.println("OkHttp Post JSON: " + json);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println("OkHttp Post Response: " + JSONObject.toJSONString(response));
            String repStr = response.body().string();
            System.out.println("OkHttp Post rspStr: " + repStr);
            return repStr;
        }
    }

    public static String get(String url) throws IOException {
        System.out.println("OkHttp Get URL: " + url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String repStr = response.body().string();
            System.out.println("OkHttp Get Response: " + repStr);
            return repStr;
        }
    }
}
