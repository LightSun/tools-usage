package com.heaven7.study.utils;

import okhttp3.*;

import java.io.IOException;

public final class OkHttpUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient sClient = new OkHttpClient();

    public static void post(String url, String json, HeaderCallback hc, StringCallback cb) {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder builder = new Request.Builder().url(url);
        if (hc != null) {
            hc.addHeader(builder);
        }
        Request request = builder
                .post(body)
                .build();
        sClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("post failed. url = " + call.request().url().toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                cb.call(str);
            }
        });
    }

    public static void get(String url, HeaderCallback hc, StringCallback cb) {
        Request.Builder builder = new Request.Builder().url(url);
        if (hc != null) {
            hc.addHeader(builder);
        }
        Request request = builder
                .get()
                .build();
        sClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("get failed. url = " + call.request().url().toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                cb.call(str);
            }
        });
    }

    public interface StringCallback {
        void call(String res);
    }
    public interface HeaderCallback {
        void addHeader(Request.Builder builder);
    }

}
