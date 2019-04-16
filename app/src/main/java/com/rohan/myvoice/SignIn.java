package com.rohan.myvoice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.CustomDialogs.ForgetPasswordDialogFragment;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.Forget_password_request.ForgetPasswordRequest;
import com.rohan.myvoice.pojo.SignIn.Data;
import com.rohan.myvoice.pojo.SignIn.Login;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class SignIn extends AppCompatActivity {

    private TextView email_address;
    private TextView password;
    private String email, pass;
    ApiService api;
    String api_key;
    private String LOGIN_STATUS = "NoT Initilize";
    SharedPreferences pref, pref2, email_pref;
    SharedPreferences.Editor editor, email_pref_editor;
    public static String DEVICE_ID;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //getting coustom layout for toolbar-
        //have to change the menifest file too - change theme of this activity to cutomeTheme
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pref = getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        editor = pref.edit();
        pref2 = getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        email_pref = getSharedPreferences("MYVOICE_EMAIL_PREF", MODE_PRIVATE);
        email_pref_editor = email_pref.edit();

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        email_address = findViewById(R.id.email_address);
        email_address.setText(email_pref.getString("email", ""));       //global preference : even when user unistalls the app, and reinstall that time also, it should give lastly used email address
        password = findViewById(R.id.password);

        DEVICE_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Set up progress before call
        progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    public void ForgetPassword(View view) {

        //pop up the dialog fragment
        if (!"".equals(email_address.getText().toString().trim())) {
            //call first to send otp
            call_forget_password_request();
        } else {
            Build_alert_dialog(SignIn.this, "Please Enter an Email ID.");
        }
    }

    private void call_forget_password_request() {

        Call<ForgetPasswordRequest> call = api.getForget_passwordJson(api_key, "Android",
                email_address.getText().toString().trim());

        progressDialog.show();

        call.enqueue(new Callback<ForgetPasswordRequest>() {
            @Override
            public void onResponse(Call<ForgetPasswordRequest> call, Response<ForgetPasswordRequest> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                    //Now call to this dialog frgamt to enter Otp and new password
                    Log.v("all_log", "Forget password request sent successfully");
                    //Toast.makeText(SignIn.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    ForgetPasswordDialogFragment forgetPasswordDialogFragment = new ForgetPasswordDialogFragment();
                    Bundle b = new Bundle();
                    b.putString("email", email_address.getText().toString().trim());
                    forgetPasswordDialogFragment.setArguments(b);
                    forgetPasswordDialogFragment.show(SignIn.this.getSupportFragmentManager(), "ForgetPassDialogFragment");

                } else {
                    progressDialog.dismiss();

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.v("all_log", "(in sign in)json response: " + "\n" + jObjError.toString(4));
                        /* String status = jObjError.getString("detail");
                         */
                        Log.v("all_log", "Forget passwoprd request response not received successfully : NOT success");
                        Log.v("all_log", "check if jErrObj has detail : " + jObjError.has("detail"));
                        if(jObjError.has("detail")) {
                            Build_alert_dialog(SignIn.this, jObjError.getString("detail"));
                        }
                        else if(jObjError.has("message")){
                            Build_alert_dialog(SignIn.this, jObjError.getString("message"));
                            Log.v("all_log", "error is: " + jObjError.getString("message"));
                        }
                        Log.v("all_log", "error is: " + jObjError.getString("detail"));
                        /*if (jObjError.has("detail")) {
                            //use contains because , it returns invalid token and other stuff too.!
                            // so use contains rather then ".equals"
                            if (jObjError.getString("detail").contains("Invalid token")) {
                                update_token_forget_password_request();
                            }
                        }*/
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgetPasswordRequest> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }


    //main logic of SIGN IN
    public void SignIn(View view) {
        //hide the keyboard when user press the SIgn IN button
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        email = email_address.getText().toString();
        pass = password.getText().toString();


        //validation of null
        if (email.equals("") && pass.equals("")) {
            Build_alert_dialog(SignIn.this, "Login Failed", "Credentials required");

        } else if (email.equals("")) {
            Build_alert_dialog(SignIn.this, "Login Failed", "Credentials required");
            //Toast.makeText(this, "Please Enter The Email Address", Toast.LENGTH_SHORT).show();
        } else if (pass.equals("")) {
            Build_alert_dialog(SignIn.this, "Login Failed", "Credentials required");
            //Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
        } else {
            //define the logic to get the use details and match them with the entered details, ,,,,if matched then success  ---RV

            call_api_login();       //made a function, because i am going to call it from another call also for updating the token info in shared pref. --RV

        }

    }

    private void call_api_login() {
        //Creating an object of our api interface
        ApiService api = RetroClient.getApiService();

        /**
         * Calling JSON
         */
        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/
        String FcmToken = pref2.getString("fcm_token", null);
        Call<Login> call = api.getLoginJason(email, pass, FcmToken, "Android", DEVICE_ID);

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    LOGIN_STATUS = response.body().getStatus();                 //getStatus method in POJO class
                    //Toast.makeText(SignIn.this, LOGIN_STATUS, Toast.LENGTH_SHORT).show();
                    //login successful redirect to "Getstarted activity" if not filled the personal details ..!!- RV

                    Data data = response.body().getData();
                    String country_details = data.getProfile().getCountry();
                    String uname = data.getFirstName();
                    String lastname = data.getLastName();
                    String token_response = response.body().getData().getToken();

                    //PUT LOGGED IN ENTRY INTO SHARED PREFERENCES

                    editor.putBoolean("isUserLoggedIn", true);
                    editor.putString("token", token_response);
                    editor.putString("username", uname);        //username is first name of looged in user, required in Getstarted activity :_RV_

                    //If user(after fresh install of apk), sign in directly rather signup then signIn , Then for that scenario i have to store email and pass.!
                    editor.putString("email", email);
                    editor.putString("password", pass);
                    editor.putString("isVerified", response.body().getData().getProfile().getIsVerified().toString());
                    editor.putString("IsComplete", response.body().getData().getProfile().getIsComplete().toString());

                    editor.commit();

                    email_pref_editor.putString("email", email);
                    email_pref_editor.commit();

                    //token used also in ApiService Interface

                    Boolean is_complete = response.body().getData().getProfile().getIsComplete();
                    if (!is_complete) {//checking whether the user details is filled or not

                            /*user has not filled details yet so in get started activity
                              user will fill it first*/

                        Intent i = new Intent(getApplicationContext(), GetStarted.class);

                        // Profile profile = data.getProfile().get
                        //i.putExtra("username", uname);
                        startActivity(i);


                    } else {
                        //REDIRECT USER TO THE MAIN DASHBOARD

                        Boolean isVerified = response.body().getData().getProfile().getIsVerified();
                        if (isVerified) {
                           // Toast.makeText(SignIn.this, "details filled already...redirecting to the main dashboard", Toast.LENGTH_SHORT).show();
                            editor.putString("IsComplete", "true");
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), Verification.class));
                        }


                    }
                    //end of coding of succes login
                } else {
                            /*codiing of unsuccessful login,
                            now our responce fields are changed , and status, message, data are received but in data
                            there is one field "error", not all other fields...
                             */     /* __RV__*/

                    //but but i can access the error body here.,
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String status = jObjError.getString("message");
                        String error_msg = jObjError.getJSONObject("data").getString("errors");
                        Build_alert_dialog(SignIn.this, status, error_msg);


                    } catch (Exception e) {
                        //Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


                //Build_alert_dialog(LOGIN_STATUS);
                // Toast.makeText(getApplicationContext(), LOGIN_STATUS, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "Error fetching the details from the server!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(SignIn.this, t.toString(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }


        });

        /*end of the logic*/
    }


}
