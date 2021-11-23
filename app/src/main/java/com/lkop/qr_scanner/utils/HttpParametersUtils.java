package com.lkop.qr_scanner.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public final class HttpParametersUtils {

    private HttpParametersUtils() {

    }

    public static String createGetParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for(HashMap.Entry<String, String> e : parameters.entrySet()){
            if(sb.length() > 0){
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

}