package com.rohan.myvoice;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rohan.myvoice.pojo.Register.Register;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

public class SignUp extends AppCompatActivity {


    private Dialog dialog;
    private TextView first_name, last_name, email_id, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        dialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);


        first_name = findViewById(R.id.f_name);
        last_name = findViewById(R.id.l_name);
        email_id = findViewById(R.id.email_id);
        pass = findViewById(R.id.password);


    }


    //sign_up process function --RV
    public void SignUp_process(View view) {
        //take data from all the fields
        final String f_name, l_name, mail, passwrd;
        f_name = first_name.getText().toString().trim();
        l_name = last_name.getText().toString().trim();
        mail = email_id.getText().toString();
        passwrd = pass.getText().toString();

        if (f_name.equals("") || l_name.equals("") || mail.equals("") || passwrd.equals("")) {
            Build_alert_dialog(this, "ALERT", "Please enter all the details!");
        } else {
            //Creating an object of our api interface
            ApiService api = RetroClient.getApiService();

            /**
             * Calling JSON
             */
            Call<Register> call = api.getRegisterJason(mail, passwrd, f_name, l_name);

            //waiting for response
            call.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Call<Register> call, Response<Register> response) {

                    if (response.isSuccessful()) {


                        String temp = response.body().getMessage();
                        write_shared_pref(mail, passwrd);
                        Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();


                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String status = jObjError.getString("status");
                            String msg = jObjError.getString("message");
                            //String error_msg = jObjError.getJSONObject("data").getString("errors");
                            Build_alert_dialog(SignUp.this, status, msg);


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }


                }

                @Override
                public void onFailure(Call<Register> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error fetching the details from the server!", Toast.LENGTH_SHORT).show();


                }
            });

        }
    }

    private void write_shared_pref(String email, String pass) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("email", email);
        editor.putString("password", pass);
        editor.commit();
    }


    //TERMS OF USE BUTTON DIALOG POPUP
    public void TermsAndConditions(View view) {

        TextView pop_up_text;
        Button agree_btn;

        dialog.setContentView(R.layout.popup_view_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        agree_btn = dialog.findViewById(R.id.agree_button);
        pop_up_text = dialog.findViewById(R.id.pop_up_text);

        pop_up_text.setText(Html.fromHtml("<pre><b>General App Usage</b><br>" +
                "<br>" +
                "Last Revised: May 16, 2018<br>" +
                "<br>" +
                "Welcome to www.lorem-ipsum.info. This site is provided as a service to our visitors and may be used for informational purposes only. Because the Terms and Conditions contain legal obligations, please read them carefully.<br>" +
                "<br>" +
                "<b>1. YOUR AGREEMENT</b><br>" +
                "<br>" +
                "By using this Site, you agree to be bound by, and to comply with, these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use this site.<br>" +
                "<br>" +
                "PLEASE NOTE: We reserve the right, at our sole discretion, to change, modify or otherwise alter these Terms and Conditions at any time. Unless otherwise indicated, amendments will become effective immediately. Please review these Terms and Conditions periodically. Your continued use of the Site following the posting of changes and/or modifications will constitute your acceptance of the revised Terms and Conditions and the reasonableness of these standards for notice of changes. For your information, this page was last updated as of the date at the top of these terms and conditions.<br>" +
                "<br>" +
                "<b>2. PRIVACY</b><br>" +
                "<br>" +
                "Please review our Privacy Policy, which also governs your visit to this Site, to understand our practices.<br>" +
                "<br>" +
                "<b>3. LINKED SITES</b><br>" +
                "<br>" +
                "This Site may contain links to other independent third-party Web sites (\"Linked Sites”). These Linked Sites are provided solely as a convenience to our visitors. Such Linked Sites are not under our control, and we are not responsible for and does not endorse the content of such Linked Sites, including any information or materials contained on such Linked Sites. You will need to make your own independent judgment regarding your interaction with these Linked Sites.<br>" +
                "<br>" +
                "<b>4. FORWARD LOOKING STATEMENTS</b><br>" +
                "<br>" +
                "All materials reproduced on this site speak as of the original date of publication or filing. The fact that a document is available on this site does not mean that the information contained in such document has not been modified or superseded by events or by a subsequent document or filing. We have no duty or policy to update any information or statements contained on this site and, therefore, such information or statements should not be relied upon as being current as of the date you access this site.<br></pre>"));
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


}
