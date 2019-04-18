package com.e2excel.myvoice.CustomDialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e2excel.myvoice.GetStarted;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.SignUp;
import com.e2excel.myvoice.pojo.Register.Register;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

public class SIgnUp_confirm_password_dialogFragment extends DialogFragment {

    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2, email_pref;
    private SharedPreferences.Editor editor, email_pref_editor;
    private ProgressDialog progressDialog;

    public SIgnUp_confirm_password_dialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.custom_dialog_sign_up_confirm_password, container, false);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", MODE_PRIVATE);

        email_pref = this.getActivity().getSharedPreferences("MYVOICE_EMAIL_PREF", MODE_PRIVATE);
        email_pref_editor = email_pref.edit();

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }

        Button dialogButton_cancel = (Button) view.findViewById(R.id.no_btn);
        Button dialogButton_confirm = (Button) view.findViewById(R.id.confirm_btn);

        final EditText confirm_pass = view.findViewById(R.id.confirm_pass);
        // if button is clicked, close the custom dialog
        dialogButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("signUpConfirmPassword");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();


            }
        });

        dialogButton_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = getArguments();

                if (confirm_pass.getText().toString().trim().equals(b.getString("pass"))) {

                    SignUp_process(b.getString("firstname"), b.getString("lastname"), b.getString("email_id"), b.getString("pass"));
                } else {
                    Build_alert_dialog(getActivity(), "password mismatch");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("signUpConfirmPassword");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();
                }


            }
        });
        return view;
    }

    public void SignUp_process(String firstname, String lastname, String email_id, String pass) {
        //take data from all the fields
        final String f_name, l_name, mail, passwrd;
        f_name = firstname;
        l_name = lastname;
        mail = email_id;
        passwrd = pass;

        if ((f_name == null || l_name == null || email_id == null || pass == null) || f_name.equals("") || l_name.equals("") || mail.equals("") || passwrd.equals("")) {
            Build_alert_dialog(getActivity(), "ALERT", "Please enter all the details!");
        } else {
            //Creating an object of our api interface
            ApiService api = RetroClient.getApiService();

            /**
             * Calling JSON
             */
            Call<Register> call = api.getRegisterJason(mail, passwrd, f_name, l_name);

            progressDialog.show();
            //waiting for response
            call.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Call<Register> call, Response<Register> response) {

                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body().getStatus().equals("Success")) {

                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment f = getFragmentManager().findFragmentByTag("signUpConfirmPassword");
                        if (f != null) {
                            ft.remove(f);
                        }
                        ft.commit();
                        String temp = response.body().getMessage();
                        editor.putString("email", mail);
                        editor.putString("password", passwrd);
                        editor.putString("username", f_name);
                        editor.putString("lastname", l_name);

                        editor.putString("token", response.body().getData().getToken());
                        editor.putBoolean("isUserLoggedIn", true);
                        editor.putString("isVerified", "false");
                        editor.putString("IsComplete", "false");
                        editor.commit();

                        email_pref_editor.putString("email", mail);
                        email_pref_editor.commit();

                        //Toast.makeText(getActivity(), temp, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getActivity(), GetStarted.class);

                        getActivity().startActivity(i);

                    } else {
                        try {

                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            Fragment f = getFragmentManager().findFragmentByTag("signUpConfirmPassword");
                            if (f != null) {
                                ft.remove(f);
                            }
                            ft.commit();
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            //String status = jObjError.getString("status");
                            String msg = jObjError.getString("message");
                            //String error_msg = jObjError.getJSONObject("data").getString("errors");
                            Build_alert_dialog(getActivity(), msg);

                        } catch (Exception e) {
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Register> call, Throwable t) {
                    //Toast.makeText(getActivity(), "Error fetching the details from the server!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        window.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

    }


}
