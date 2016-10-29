package com.canice.wristbandapp.util;

import com.canice.wristbandapp.APP;
import com.canice.wristbandapp.core.net.AsyncHttpClient;
import com.canice.wristbandapp.core.net.AsyncHttpResponseHandler;
import com.canice.wristbandapp.core.net.RequestParams;

import android.content.Context;

public class HttpUtil {

    private static AsyncHttpClient client = APP.client;
    
    public static void cancel(Context context) {
        client.cancelRequests(context, true);
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(context, url, params, handler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(url, params, handler);
    }

    public static void get(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
    }
}
