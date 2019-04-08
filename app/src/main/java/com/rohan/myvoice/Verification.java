package com.rohan.myvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.resent_otp.Data;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class Verification extends AppCompatActivity {

    private EditText e1, e2, e3, e4, e5, e6;
    private TextView title_tv;

    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        title_tv = findViewById(R.id.title);

        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        e4 = findViewById(R.id.e4);
        e5 = findViewById(R.id.e5);
        e6 = findViewById(R.id.e6);

        // Set up progress before call
        progressDialog = new ProgressDialog(Verification.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        e1.addTextChangedListener(new GenericTextWatcher(e1));
        e2.addTextChangedListener(new GenericTextWatcher(e2));
        e3.addTextChangedListener(new GenericTextWatcher(e3));
        e4.addTextChangedListener(new GenericTextWatcher(e4));
        e5.addTextChangedListener(new GenericTextWatcher(e5));
        e6.addTextChangedListener(new GenericTextWatcher(e6));

        e1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });

        e2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });
        e5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });

        e6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    // this is for backspace
                    clear_all();
                }
                return false;
            }
        });


        /*///change the title text
    title_tv.setText();*/

        String email = pref.getString("email", " ");
        title_tv.setText("We just sent a verification code \n to " + email + ".");


    }

    public void submit_otp_request(final View view) {
        String otp = e1.getText().toString().trim() +
                e2.getText().toString().trim() +
                e3.getText().toString().trim() +
                e4.getText().toString().trim() +
                e5.getText().toString().trim() +
                e6.getText().toString().trim();


        Call<Data> call = api.getSubmit_otp_request(api_key, "Token " + pref.getString("token", null), otp, "Android");
        progressDialog.show();
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    editor.putString("isVerified", "true");
                    editor.commit();
                    startActivity(new Intent(Verification.this, Dashboard.class));

                } else {
                    try {

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.v("otp", jObjError.toString(4));


                        Log.v("otp", "'if contain detail' : " + jObjError.has("detail"));


                        if (jObjError.has("detail")) {
                            if (jObjError.getJSONObject("detail").equals("Invalid Token")) {
                                update_token_submit_otp(view);

                            }
                        } else {
                            String status = jObjError.getString("message");

                            Log.v("otp", "'submit_otp' message: " + status);
                            //String error_msg = jObjError.getJSONObject("data").getString("errors");
                            Build_alert_dialog(Verification.this, status);
                        }

                    } catch (Exception e) {
                        //Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

                progressDialog.dismiss();
            }
        });


    }

    public void resend_otp_request(final View view) {
        clear_all();
        hide_keyboard();

        String FcmToken = PublicClass.FCM_TOKEN;
        String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Call<Data> call = api.getresend_otp_request(api_key, "Token ll" + pref.getString("token", null), FcmToken, device_id, "Android");
        progressDialog.show();
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                    Log.v("otp", "resend request message: " + response.body().getMessage());
                    Toast.makeText(Verification.this, response.body().getMessage().toString(), Toast.LENGTH_SHORT).show();

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.v("otp", jObjError.toString(4));

                        Log.v("otp", "resent otp; has details: " + jObjError.has("detail"));

                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_reset_otp(view);

                            }
                        } else {
                            String status = jObjError.getString("message");

                            Log.v("otp", "'resend_otp' message: " + status);
                            //String error_msg = jObjError.getJSONObject("data").getString("errors");
                            Build_alert_dialog(Verification.this, status);
                        }
                    } catch (Exception e) {
                        Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

                progressDialog.dismiss();
            }
        });
    }

    private void update_token_submit_otp(final View view) {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token", "Token " + pref.getString("token", null));

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    //call_api_coutry();
                    submit_otp_request(view);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    private void update_token_reset_otp(final View view) {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token", "Token " + pref.getString("token", null));

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    //call_api_coutry();
                    submit_otp_request(view);
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
    }

    class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {

                case R.id.e1:
                    if (text.length() == 1)
                        e2.requestFocus();
                    break;
                case R.id.e2:
                    if (text.length() == 1)
                        e3.requestFocus();
                    else if (text.length() == 0)
                        e1.requestFocus();
                    break;
                case R.id.e3:
                    if (text.length() == 1)
                        e4.requestFocus();
                    else if (text.length() == 0)
                        e2.requestFocus();
                    break;
                case R.id.e4:
                    if (text.length() == 1)
                        e5.requestFocus();
                    else if (text.length() == 0)
                        e3.requestFocus();
                    break;
                case R.id.e5:
                    if (text.length() == 1)
                        e6.requestFocus();
                    else if (text.length() == 0)
                        e4.requestFocus();
                    break;
                case R.id.e6:
                    if (text.length() == 1) {
                        hide_keyboard();

                    } else if (text.length() == 0)
                        e5.requestFocus();
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub


        }


    }

    public void clear_all() {
        //backspace pressed
        e1.getText().clear();
        e2.getText().clear();
        e3.getText().clear();
        e4.getText().clear();
        e5.getText().clear();
        e6.getText().clear();

        e1.requestFocus();

    }

    private void hide_keyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Verification.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
