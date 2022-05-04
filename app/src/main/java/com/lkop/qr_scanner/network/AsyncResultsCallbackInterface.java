package com.lkop.qr_scanner.network;

public interface AsyncResultsCallbackInterface {

    void onSuccess(String json_string);

    void onFailure();

}
