package com.sp.benjago.helper;

import android.os.AsyncTask;

import com.sp.benjago.allinterface.onUrlResponceListener;
import com.sp.benjago.application.BenjagoApplication;
import com.sp.benjago.constants.BenjagoConstants;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

/**
 * Created by Saikat's Pakira on 15/05/16.
 */

public class OkHttepest {

    private final String TAG = "";
    private String URL = "";
    private String result = "";
    private String code = "";
    private String exception = "";

    onUrlResponceListener callback = null;


    public void logMeIN(final String email, final String password, onUrlResponceListener callback) {
        this.callback = callback;
        (new LogMeIn(email, password)).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }


    public void getAllMeetings(onUrlResponceListener callback) {
        this.callback = callback;
        (new GetUserMeeting()).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private class LogMeIn extends AsyncTask<Void, Void, Void> {

        private String email = "", password = "";

        public LogMeIn(final String email, final String password) {
            super();
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            exception = "";
            URL = BenjagoConstants.API_DOMAIN_API + "login/";
            Logger.showMessage(TAG, "URL : " + URL);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(BenjagoApplication.getInstance().getCurrentCookie()).build();
                MultipartBody.Builder mBilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                mBilder.addFormDataPart("email", email);
                mBilder.addFormDataPart("password", password);

                MultipartBody requestBody = mBilder.build();
                okhttp3.Request request = new okhttp3.Request.Builder().header("Content-Type", "application/x-www-form-urlencoded")
                        .url(URL)
                        .post(requestBody)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();
                code = "" + response.code();
                Logger.showMessage(TAG, "Image upload result : " + result);
                Logger.showMessage(TAG, "Image upload result : " + response.code());

            } catch (Exception ex) {
                exception = ex.toString();
                Logger.showMessage(TAG, "Image upload Exception : " + ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception.equals("")) {
                callback.onSuccess(code, result);
            } else {
                callback.onError(exception);
            }
        }
    }


    private class GetUserMeeting extends AsyncTask<Void, Void, Void> {

        public GetUserMeeting() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            exception = "";
            URL = BenjagoConstants.API_DOMAIN_API + "get-meetings";
            Logger.showMessage(TAG, "URL : " + URL);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                OkHttpClient client = new OkHttpClient.Builder().cookieJar(BenjagoApplication.getInstance().getCurrentCookie()).build();
//                MultipartBody.Builder mBilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//                mBilder.addFormDataPart("email", email);
//                mBilder.addFormDataPart("password", password);
//
//                MultipartBody requestBody = mBilder.build();
                okhttp3.Request request = new okhttp3.Request.Builder().header("Content-Type", "application/x-www-form-urlencoded")
                        .url(URL)
                        .get()
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                result = response.body().string();
                code = "" + response.code();
                Logger.showMessage(TAG, "Image upload result : " + result);
                Logger.showMessage(TAG, "Image upload result : " + response.code());

            } catch (Exception ex) {
                exception = ex.toString();
                Logger.showMessage(TAG, "Image upload Exception : " + ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (exception.equals("")) {
                callback.onSuccess(code, result);
            } else {
                callback.onError(exception);
            }
        }
    }


}
