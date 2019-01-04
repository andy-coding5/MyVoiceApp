package com.rohan.myvoice;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.input.InputManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.pojo.SignIn.Login;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void SignIn(View view) {
        //hide the keyboard when user press the SIgn IN button
        InputMethodManager inputManager  = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE) ;
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        email = email_address.getText().toString();
        pass = password.getText().toString();

        if (email.equals("") && pass.equals("")) {
            Build_alert_dialog("Please Enter The Email Address and Password");

        } else if (email.equals("")) {
            Build_alert_dialog("Please Enter The Email Address");
            //Toast.makeText(this, "Please Enter The Email Address", Toast.LENGTH_SHORT).show();
        } else if (pass.equals("")) {
            Build_alert_dialog("Please Enter The Password");
            //Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
        } else {
            //define the logic to get the use details and match them with the entered details, ,,,,if matched then success  ---RV

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

                        //Build_alert_dialog(LOGIN_STATUS);
                        Toast.makeText(getApplicationContext(), LOGIN_STATUS, Toast.LENGTH_SHORT).show();
                    } else {
                        LOGIN_STATUS = "Invalid Username and/or Password";
                        Build_alert_dialog(LOGIN_STATUS);
                        //Toast.makeText(getApplicationContext(), LOGIN_STATUS, Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getApplicationContext(), LOGIN_STATUS, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {

                }


            });
        }

    }


    //define alert box , message is in argument of calling func tion ... code reusability -- by RV

    private void Build_alert_dialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignIn.this);
        alertDialog.setTitle("Alert");
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
}
