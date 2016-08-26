package com.sp.benjago.application;

import android.app.Application;
import android.location.Location;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.sp.benjago.helper.Logger;

import okhttp3.CookieJar;

/**
 * Created by Saikat's Mac on 13/03/16.
 */

public class BenjagoApplication extends Application {


    private RequestQueue mRequestQueue;
    private static BenjagoApplication mInstance;
    public static final String TAG = BenjagoApplication.class
            .getSimpleName();
    public Location LAST_KNOWN_LOCATION = null;
    private static CookieJar cookieJar = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    public static synchronized BenjagoApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public Location getLAST_KNOWN_LOCATION() {
        return LAST_KNOWN_LOCATION;
    }

    public void setLAST_KNOWN_LOCATION(Location LAST_KNOWN_LOCATION) {
        this.LAST_KNOWN_LOCATION = LAST_KNOWN_LOCATION;
    }


    public CookieJar getCurrentCookie() {
        if (cookieJar == null) {
            cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        }
        return cookieJar;
    }
}
