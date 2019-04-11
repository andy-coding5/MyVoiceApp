package com.rohan.myvoice.CustomDialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.Forget_password_request.ForgetPasswordRequest;
import com.rohan.myvoice.pojo.SignIn.Login;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordDialogFragment extends DialogFragment {


    ApiService api;
    String api_key;

    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;

    private EditText otp_tv, new_pass_tv;
    private String otp, new_pass, email;

    private ProgressDialog progressDialog;


    public ForgetPasswordDialogFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.custom_dialog_forget_password, container, false);

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        otp_tv = v.findViewById(R.id.otp);
        new_pass_tv = v.findViewById(R.id.new_pass);

        //initialize all with empty String
        otp = new String("");
        new_pass = new String("");

        email = getArguments().getString("email");

        Button dialogButton_no = (Button) v.findViewById(R.id.no_btn);//cancel
        Button dialogButton_yes = (Button) v.findViewById(R.id.yes_btn);//reset
        // if button is clicked, close the custom dialog
        //cancel button
        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("ForgetPassDialogFragment");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();

            }
        });


        //reset button
        dialogButton_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = otp_tv.getText().toString().trim();
                new_pass = new_pass_tv.getText().toString().trim();

                if (!"".equals(otp) && !"".equals(new_pass)) {

                    //first dismiss the dialoagfragment oin press YES
                  /*  FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("ForgetPassDialogFragment");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();*/

                    call_reset_password_verify();
                } else {

                    //first dismiss the dialoagfragment oin press YES

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("ForgetPassDialogFragment");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();



                }

            }
        });


        return v;
    }

    private void call_reset_password_verify() {

        Call<ForgetPasswordRequest> call = api.getForget_password_verifyJson(api_key,email,
                otp_tv.getText().toString().trim(), new_pass_tv.getText().toString().trim(), "Android");

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<ForgetPasswordRequest>() {
            @Override
            public void onResponse(Call<ForgetPasswordRequest> call, Response<ForgetPasswordRequest> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                    Toast.makeText(getActivity(), "Password reset successfully", Toast.LENGTH_SHORT).show();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("ForgetPassDialogFragment");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                        alertDialog.setCancelable(true);
                        alertDialog.setMessage(jObjError.getString("message"));
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        //Toast.makeText(getActivity(), "Error: " +jObjError.getString("detail"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        window.setLayout(820, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
       /* if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }
        */
        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token", "Token " + pref.getString("token", null));


                    call_reset_password_verify();
                    //call_api_coutry();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }

}
