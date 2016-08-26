package com.sp.benjago.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.sp.benjago.GateWayActivity;
import com.sp.benjago.R;
import com.sp.benjago.allinterface.onUrlResponceListener;
import com.sp.benjago.helper.Logger;
import com.sp.benjago.helper.OkHttepest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Saikat's Mac on 13/03/16.
 */

public class LogInFragment extends Fragment {


    EditText user_email, user_password;
    protected final String TAG = "LogInFragment";
    ProgressBar pBar;
    View loginClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user_email = (EditText) view.findViewById(R.id.email);
        user_password = (EditText) view.findViewById(R.id.password);
        pBar = (ProgressBar) view.findViewById(R.id.progressBar);
        loginClick = view.findViewById(R.id.lets_go);
        loginClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(user_email.getText().toString().trim()).matches()) {
                    if (user_password.getText().toString().trim().equalsIgnoreCase("")) {
                        user_email.setError("Enter Password.");
                    } else {
                        pBar.setVisibility(View.VISIBLE);
                        loginClick.setEnabled(false);
                        logMeIn();
                    }
                } else {
                    user_email.setError("Invalid email");
                }
            }
        });


    }


    public void logMeIn() {
        new OkHttepest().logMeIN(user_email.getText().toString().trim(), user_password.getText().toString().trim(), new onUrlResponceListener() {
            @Override
            public void onSuccess(final String statusCode, final String result) {
                Logger.showMessage("SaikatsTest", "statusCode : " + statusCode + "\n result : " + result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setVisibility(View.GONE);
                        loginClick.setEnabled(true);
                        if (statusCode.equals("200")) {
                            Intent i = new Intent(getActivity(), GateWayActivity.class);
                            startActivity(i);
                            getActivity().finish();
                        } else {
                            String json = trimMessage(result, "error");
                            Logger.showMessage(TAG, json);
                            if (json != null)
                                displayMessage(json);
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                Logger.showMessage("SaikatsTest", "error : " + error);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pBar.setVisibility(View.GONE);
                        displayMessage(error);
                    }
                });
            }
        });


//        StringRequest request = new StringRequest(Request.Method.POST, URL_, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Logger.showMessage(TAG, response);
//                Logger.showMessage(TAG, "statusCode : " + mStatusCode);
//                pBar.setVisibility(View.GONE);
//                loginClick.setEnabled(true);
//                Intent i = new Intent(getActivity(), LandingPage.class);
//                startActivity(i);
//                getActivity().finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Log.e("onErrorResponse", error.toString());
//                loginClick.setEnabled(true);
//                pBar.setVisibility(View.GONE);
//                String json = null;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//                    json = new String(response.data);
//                    json = trimMessage(json, "error");
//                    Logger.showMessage(TAG, json);
//                    if (json != null)
//                        displayMessage(json);
//                }
//            }
//        }) {
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                if (response != null) {
//                    mStatusCode = response.statusCode;
//                }
//                return super.parseNetworkResponse(response);
//            }
//
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("email", user_email.getText().toString().trim());
//                params.put("password", user_password.getText().toString().trim());
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        BenjagoApplication.getInstance().addToRequestQueue(request);
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public String trimMessage(String json, String key) {
        String trimmedString = null;
        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return json;
        }
        return trimmedString;
    }


    public void displayMessage(String toastString) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder
                .setMessage(toastString)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
