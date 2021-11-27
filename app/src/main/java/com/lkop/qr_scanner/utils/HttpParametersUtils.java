package com.lkop.qr_scanner.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public final class HttpParametersUtils {

    private HttpParametersUtils() {

    }

    public static String createUrlWithParameters(String[] url_data, Map<String, String> parameters) {
        String url_params = url_data[1];
        if(parameters != null && !parameters.isEmpty()) {
            url_params += "?";
            if (url_data[0].equals("uop")) {
                url_params += "appSecret=" + url_data[2];
            }
            try {
                StringBuilder sb = new StringBuilder();
                for(HashMap.Entry<String, String> e : parameters.entrySet()){
                    if(sb.length() > 0){
                        sb.append('&');
                    }
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
                }
                url_params += sb.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return url_params;
    }

}