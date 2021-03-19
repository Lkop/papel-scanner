package com.lkop.qr_scanner.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AsyncGetMinVersion {

    private final OkHttpClient client = new OkHttpClient();

    public void run() {

        Request request = new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) {

                ResponseBody responseBody = null;

                try {
                    responseBody = response.body();

                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    Log.d("TTTT", responseBody.string());
                    System.out.println(responseBody.string());
                }catch(Exception e){

                }
            }
        });
    }
}
