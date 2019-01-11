package com.rohan.myvoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
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
    private String LOGIN_STATUS = "NoT Initilize";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //getting coustom layout for toolbar-
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

        email_address = findViewById(R.id.email_address);
        password = findViewById(R.id.password);


    }

    public void ForgetPassword(View view) {
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
        Call<Login> call = api.getLoginJason(email, pass);

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {

                if (response.isSuccessful()) {
                    LOGIN_STATUS = response.body().getStatus();                 //getStatus method in POJO class
                    Toast.makeText(SignIn.this, LOGIN_STATUS, Toast.LENGTH_SHORT).show();
                    //login successful redirect to "Getstarted activity" if not filled the personal details ..!!- RV

                    Data data = response.body().getData();
                    String country_details = data.getProfile().getCountry();
                    String uname = data.getFirstName();
                    String token_response = response.body().getData().getToken();

                    //PUT LOGGED IN ENTRY INTO SHARED PREFERENCES
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isUserLoggedIn", true);
                    editor.putString("token", token_response);
                    editor.putString("username", uname);
                    editor.commit();

                    //token used also in ApiService Interface
                    Token.token_string = response.body().getData().getToken();


                    if (country_details == null) {//checking whether the user details is filled or not

                            /*user has not filled details yet so in get started activity
                              user will fill it first*/

                        Intent i = new Intent(getApplicationContext(), GetStarted.class);

                        // Profile profile = data.getProfile().get
                        //i.putExtra("username", uname);
                        startActivity(i);


                    } else {
                        //REDIRECT USER TO THE MAIN DASHBOARD


                    }
                    //coding of succes login
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
                        Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


                //Build_alert_dialog(LOGIN_STATUS);
                // Toast.makeText(getApplicationContext(), LOGIN_STATUS, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error fetching the details from the server!", Toast.LENGTH_SHORT).show();
            }


        });

        /*end of the logic*/
    }


}
