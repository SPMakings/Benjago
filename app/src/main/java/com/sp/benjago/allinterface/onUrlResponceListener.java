package com.sp.benjago.allinterface;

/**
 * Created by apple on 16/05/16.
 */
public interface onUrlResponceListener {
    void onSuccess(String statusCode, String result);

    void onError(String error);
}
