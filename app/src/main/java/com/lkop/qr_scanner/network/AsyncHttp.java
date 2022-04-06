package com.lkop.qr_scanner.network;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lkop.qr_scanner.constants.URLs;
import com.lkop.qr_scanner.utils.HttpParametersUtils;
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

public class AsyncHttp {

    private final OkHttpClient client = new OkHttpClient();

    public void get(String[] url_data, Map<String,String> parameters, AsyncResultsCallbackInterface callback) {
        Request request = new Request.Builder()
                .url(HttpParametersUtils.createUrlWithParameters(url_data, parameters))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onSuccess(response.body().string());

//                try (ResponseBody responseBody = response.body()) {
//                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                    Headers responseHeaders = response.headers();
//                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
//                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//                    }
//
//                    System.out.println(responseBody.string());
//                }
            }
        });
    }

    public void post(String[] url_data, Map<String,String> parameters, AsyncResultsCallbackInterface callback) {
        String json_payload = "";
        try {
            if(url_data[0].equals(URLs.UOP)) {
                parameters.put("appSecret", url_data[2]);
            }
            ObjectMapper mapper = new ObjectMapper();
            json_payload = mapper.writeValueAsString(parameters);
        }catch(IOException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json_payload);
        Request request = new Request.Builder()
                .url(url_data[1])
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }
}
