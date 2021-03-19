package com.lkop.qr_scanner.network;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AsyncGetJSONFromURL {

    private String host, url, app_secret;
    private OkHttpClient client = new OkHttpClient();

    public AsyncGetJSONFromURL(String[][] url_data, Map<String,String> data){

        this.host = url_data[0][0];
        this.url = url_data[0][1];
        this.app_secret = url_data[0][2];
    }

    public void run(AsyncResults delegate) {

        final AsyncResults d = delegate;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {

                ResponseBody responseBody;
                String string_body = null;

                try {
                    responseBody = response.body();

                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

//                    Headers responseHeaders = response.headers();
//                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                    }

                    string_body = responseBody.string();
//                    jsonArray = new JSONArray(responseBody.string());

                }catch(IOException | NullPointerException e){
                    d.taskResultsObject(null);
                }

                d.taskResultsObject(string_body);
            }
        });
    }
}
