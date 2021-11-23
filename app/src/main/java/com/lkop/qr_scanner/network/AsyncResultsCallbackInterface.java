package com.lkop.qr_scanner.network;

import org.json.JSONObject;

public interface AsyncResultsCallbackInterface {

    void onSuccess(String json_string);

    void onFailure(Throwable t);

}
