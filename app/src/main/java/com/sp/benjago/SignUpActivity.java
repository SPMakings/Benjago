package com.sp.benjago;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.sp.benjago.adapter.AddresHelperAdapter;
import com.sp.benjago.application.BenjagoApplication;
import com.sp.benjago.constants.BenjagoConstants;
import com.sp.benjago.helper.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, emailID, password, confirmPassword, phoneNumber;
    TextView dateOfBirth, address;
    CheckBox termsCondition;
    RadioGroup genderGroup;
    boolean isMale = true;
    private final String TAG = "SignUpActivity";
    ProgressBar pBar;

    //------address suggestion

    EditText addressBox = null;
    RecyclerView suggAddress = null;

    //------------final location and PlaceID

    private String PLACE_ID = "";
    private JSONObject locationFinal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("SignUp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //-----------------
        firstName = (EditText) findViewById(R.id.fst_name);
        lastName = (EditText) findViewById(R.id.lst_name);
        emailID = (EditText) findViewById(R.id.email);
        phoneNumber = (EditText) findViewById(R.id.phone_no);

        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.cnfrm_password);

        dateOfBirth = (TextView) findViewById(R.id.dob);
        address = (TextView) findViewById(R.id.address);

        genderGroup = (RadioGroup) findViewById(R.id.gender_group);
        termsCondition = (CheckBox) findViewById(R.id.accept);
        pBar = (ProgressBar) findViewById(R.id.progressBar_signup);


        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Logger.showMessage("Geder", " : " + checkedId);
                if (("" + checkedId).equalsIgnoreCase("2131492982")) {//male
                    isMale = false;
                }
            }
        });


        address.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addressSuggestionDialog();
            }
        });

        findViewById(R.id.lets_go).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (firstName.getText().toString().trim().equalsIgnoreCase("")) {
                    firstName.setError("Enter First Name");
                } else {
                    if (lastName.getText().toString().trim().equalsIgnoreCase("")) {
                        lastName.setError("Enter Last Name");
                    } else {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailID.getText().toString().trim()).matches()) {
                            if (password.getText().toString().trim().equalsIgnoreCase("")) {
                                password.setError("Enter password");
                            } else {
                                if (confirmPassword.getText().toString().trim().equalsIgnoreCase("")) {
                                    confirmPassword.setError("Enter confirm password");
                                } else {
                                    if (!password.getText().toString().trim().equalsIgnoreCase(confirmPassword.getText().toString().trim())) {
                                        confirmPassword.setError("Passord is not matching with confirm password");
                                    } else {
                                        if (address.getText().toString().trim().equalsIgnoreCase("Address")) {
                                            Toast.makeText(getApplicationContext(), "Please enter your address", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (termsCondition.isChecked() == true) {
                                                v.setEnabled(false);
                                                makeMeSignIN(BenjagoConstants.API_DOMAIN_API + "inscription-eleve/");
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "You need to accept terms and conditions", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            emailID.setError("Invalid Email ID");
                        }
                    }
                }
            }
        });


        dateOfBirth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                View dialoglayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_date_picker, null);
                final DatePicker dPicker = (DatePicker) dialoglayout.findViewById(R.id.date_picker);
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tempMonth;
                        if ((dPicker.getMonth() + 1) > 9) {
                            tempMonth = "" + (dPicker.getMonth() + 1);
                        } else {
                            tempMonth = "0" + (dPicker.getMonth() + 1);
                        }
                        String tempYear;
                        if (dPicker.getDayOfMonth() > 9) {
                            tempYear = "" + dPicker.getDayOfMonth();
                        } else {
                            tempYear = "0" + dPicker.getDayOfMonth();
                        }
                        dateOfBirth.setText("" + dPicker.getYear() + "-" + tempMonth + "-" + tempYear);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dateOfBirth.setText("Birth date");
                    }
                });
                builder.setView(dialoglayout);
                builder.show();
            }
        });


        //-\--------------address suggestion
//        addressBox.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                InstiworkApplication.getInstance().cancelPendingRequests("LocationAddress");
//                Location_Suggession(addressBox.getText().toString().trim());
//            }
//        });
    }


    //--------------Signup JSON Handeling

    public void makeMeSignIN(final String URL_) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.showMessage(TAG, response);
                //findViewById(R.id.lets_go).setEnabled(true);
                pBar.setVisibility(View.GONE);
                Intent i = new Intent(SignUpActivity.this, LandingPage.class);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pBar.setVisibility(View.GONE);
                findViewById(R.id.lets_go).setEnabled(true);
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    json = trimMessage(json, "error");
                    Logger.showMessage(TAG, json);
                    if (json != null)
                        displayMessage(json);
                }
            }
        }) {
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                if (response != null) {
//                }
//                return super.parseNetworkResponse(response);
//            }

            @Override
            protected Map<String, String> getParams() {

                //http://benjago.be:9020/submit-inscription-eleve/?
                // prenom=Android&
                // nom=Dev&
                // email=android@dev.com&
                // telephone=1234567890&
                // sexe=H&
                // password=123456&
                // langues=en&
                // date_de_naissance=1990-10-29&
                // conditions_generales=true
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("email", emailID.getText().toString().trim());
                    params.put("password", password.getText().toString().trim());
                    params.put("prenom", URLEncoder.encode(firstName.getText().toString().trim(), "UTF-8"));
                    params.put("langues", "en");
                    params.put("nom", lastName.getText().toString().trim());
                    params.put("date_de_naissance", dateOfBirth.getText().toString().trim());
                    params.put("telephone", phoneNumber.getText().toString().trim());

                    params.put("lat", locationFinal.getString("lat"));
                    params.put("lng", locationFinal.getString("lng"));
                    params.put("address_text", address.getText().toString().trim());
                    params.put("placeid", PLACE_ID);

                    if (isMale) {
                        params.put("sexe", "H");
                        Logger.showMessage(TAG, "H");
                    } else {
                        params.put("sexe", "F");
                        Logger.showMessage(TAG, "F");
                    }
                    params.put("conditions_generales", "" + termsCondition.isChecked());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        BenjagoApplication.getInstance().addToRequestQueue(request);
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(SignUpActivity.this);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder
                .setMessage(Html.fromHtml(toastString))
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void addressSuggestionDialog() {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_address_taker, null);
        final Dialog mBottomSheetDialog = new Dialog(SignUpActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        addressBox = (EditText) view.findViewById(R.id.add_box);
        suggAddress = (RecyclerView) view.findViewById(R.id.add_suggestion);
        suggAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                BenjagoApplication.getInstance().cancelPendingRequests("LocationAddress");
                Location_Suggession(addressBox.getText().toString().trim(), mBottomSheetDialog);
            }
        });

        view.findViewById(R.id.close_me).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                address.setText("Address");
                mBottomSheetDialog.dismiss();
            }
        });

    }

    public void Location_Suggession(final String Char, final Dialog mBottomSheetDialog) {
        try {
            final String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(Char, "UTF-8") + "&key=AIzaSyCd2Hc0DEdARGxV6_EHbWkQxssVY67hgio";//AIzaSyBYWD7Jcqv6JwOhVtk_4uPZjAv7lzcdMww";

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, URL,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Logger.showMessage(TAG, URL);
                            Logger.showMessage(TAG, response.toString());
                            try {
                                if (response.getString("status").equalsIgnoreCase("OK")) {
                                    JSONArray jArray = response.getJSONArray("results");

                                    AddresHelperAdapter adapter = new AddresHelperAdapter(SignUpActivity.this, jArray, new AddresHelperAdapter.onClickAdapterItem() {
                                        @Override
                                        public void onClick(JSONObject location, String resultAddress, String resultPlaceID) {
                                            Logger.showMessage(TAG, location.toString());
                                            Logger.showMessage(TAG, resultAddress.toString());
                                            Logger.showMessage(TAG, resultPlaceID.toString());
                                            locationFinal = location;
                                            PLACE_ID = resultPlaceID;
                                            mBottomSheetDialog.dismiss();
                                            address.setText(resultAddress);
                                        }
                                    });
                                    suggAddress.setAdapter(adapter);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Logger.showMessage(TAG, e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.showMessage(TAG, "Error: " + error.getMessage());
                }
            });
            BenjagoApplication.getInstance().addToRequestQueue(jsonObjReq, "LocationAddress");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public void setAddress(final JSONObject resultObj) {
//        Logger.showMessage(TAG, resultObj.toString());
//    }

}
