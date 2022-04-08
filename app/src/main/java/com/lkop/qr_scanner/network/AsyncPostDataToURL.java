package com.lkop.qr_scanner.network;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lkop.qr_scanner.constants.URLs;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AsyncPostDataToURL {

    private String host, url, app_secret;
    private Map<String,String> data;
    private String post_json;
    private OkHttpClient client = new OkHttpClient();

    public AsyncPostDataToURL(String[][] url_data, Map<String,String> data) {
        this.host = url_data[0][0];
        this.url = url_data[0][1];
        this.app_secret = url_data[0][2];

        try {
            ObjectMapper mapper = new ObjectMapper();

            post_json = mapper.writeValueAsString(data);

            //UOP site add appSecret
            if(host.equals(URLs.UOP)) {
                JsonNode json_node = mapper.readTree(post_json);
                ((ObjectNode)json_node).put("appSecret", app_secret);

                post_json = mapper.writeValueAsString(json_node);

                //Debug //Print json
                Log.d("JJJJ", post_json);
            }
        }catch(IOException e) {

        }
    }

    public void run(final AsyncResults delegate) {

        final AsyncResults d = delegate;

        /*
        //OLD CODE
        //get params
        MultipartBody.Builder mpb = new MultipartBody.Builder();

        mpb.setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            mpb.addFormDataPart(entry.getKey(), entry.getValue());
        }

        RequestBody requestBody = mpb.build();
        */

        //////////////////////////////////////////////////////////////////

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(post_json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("JJJJ", "err");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ResponseBody responseBody;
                String string_body = null;
                try {
                    responseBody = response.body();

                    if (!(response.code()==200))
                        throw new IOException("NOT OK" + response);

                        //if (!response.isSuccessful())
                    //    throw new IOException("Unexpected code " + response);

                    string_body = responseBody.string();

                    //Debug //ResponseBody string
                    //Log.d("JJJJ", string_body);

                }catch(IOException | NullPointerException e){
                    d.taskResultsObject(null);
                }

                //Callback Request
                d.taskResultsObject(string_body);
            }
        });
    }
}
