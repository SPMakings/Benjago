package com.sp.benjago;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sp.benjago.fragments.LogInFragment;
import com.sp.benjago.fragments.SplashFragments;

public class SplashScreen extends AppCompatActivity {


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    boolean isFirstTime = true;
    final String TAG = "SplashScreen";


    SharedPreferences sPrefresnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPrefresnce = getSharedPreferences("Benjago", MODE_PRIVATE);

        if (sPrefresnce.getBoolean("FST_TIME", false) == false) {
            setContentView(R.layout.activity_splash_screen);

            getSupportActionBar().hide();

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_holder, new SplashFragments());
            fragmentTransaction.commit();

//            SharedPreferences.Editor edit = sPrefresnce.edit();
//            edit.putBoolean("FST_TIME", true);
//            edit.commit();

        } else {
            Intent i = new Intent(SplashScreen.this, GateWayActivity.class);
            startActivity(i);
            finish();
        }

    }

    public boolean isFirstTime() {
        return isFirstTime;
    }


    public void setIsFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public void openLoginPage() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, new LogInFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void openSignUpPage() {
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_holder, new SignUp());
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

        Intent i = new Intent(SplashScreen.this, SignUpActivity.class);
        startActivity(i);
    }


//    public class UpdateAccountDetailsWithImage extends AsyncTask<Void, Void, Void> {
//
//        String result = "";
//
//        public UpdateAccountDetailsWithImage() {
//            super();
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//
//            try {
//                //OkHttpClient client = new OkHttpClient();
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .cookieJar(new CookieJar() {
//                            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
//
//                            @Override
//                            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                                cookieStore.put(url, cookies);
//                            }
//
//                            @Override
//                            public List<Cookie> loadForRequest(HttpUrl url) {
//                                List<Cookie> cookies = cookieStore.get(url);
//                                return cookies != null ? cookies : new ArrayList<Cookie>();
//                            }
//                        })
//                        .build();
//                MultipartBody.Builder mBilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//                mBilder.addFormDataPart("conditions_generales", "true");
//
////                mBilder.addFormDataPart(
////                        "user_image",
////                        file.getName().toString(),
////                        RequestBody.create(MediaType.parse("image/*"), file)
////                );
////                CookieManager cookieManager = new CookieManager();
////                CookieHandler.setDefault(cookieManager);
////                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
//
//
//
//                MultipartBody requestBody = mBilder.build();
//                okhttp3.Request request = new okhttp3.Request.Builder()
//                        .url("URL")
//                        .post(requestBody)
//                        .build();
//                okhttp3.Response response = client.newCall(request).execute();
//                result = response.body().string();
//                Logger.showMessage(TAG, "Benjago : " + result);
//
//            } catch (Exception ex) {
//                Logger.showMessage(TAG, "Image upload Exception : " + ex.toString());
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
////            if (exception.equalsIgnoreCase("")) {
////
////            } else {
////                Snackbar.make(findViewById(android.R.id.content), "Failed to upload image...Try again!", Snackbar.LENGTH_LONG)
////                        .setActionTextColor(Color.RED)
////                        .show();
////            }
//        }
//    }

}
