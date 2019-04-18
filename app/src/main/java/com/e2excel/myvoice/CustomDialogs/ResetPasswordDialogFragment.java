package com.e2excel.myvoice.CustomDialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
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

import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.reset_password.ResetPassword;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

public class ResetPasswordDialogFragment extends DialogFragment {

    ApiService api;
    String api_key;

    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;

    EditText old_pass_tv, new_pass_tv, confirm_pass_tv;

    private ProgressDialog progressDialog;

    String old_pass, new_pass, confirm_pass;


    public ResetPasswordDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.custom_dialog_reset_password, container, false);

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


        old_pass_tv = view.findViewById(R.id.old_pass);
        new_pass_tv = view.findViewById(R.id.new_pass);
        confirm_pass_tv = view.findViewById(R.id.confirm_pass);

        //initialize all with empty String
        old_pass = new String("");
        new_pass = new String("");
        confirm_pass = new String("");


        Button dialogButton_no = (Button) view.findViewById(R.id.no_btn);//cancel
        Button dialogButton_yes = (Button) view.findViewById(R.id.yes_btn);//reset
        // if button is clicked, close the custom dialog
        //cancel button
        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });


        //reset button
        dialogButton_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_pass = old_pass_tv.getText().toString().trim();
                new_pass = new_pass_tv.getText().toString().trim();
                confirm_pass = confirm_pass_tv.getText().toString().trim();


                if (!"".equals(old_pass) && !"".equals(new_pass) && !"".equals(confirm_pass)) {

                    if(new_pass.equals(confirm_pass)) {
                        call_reset_password();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("New Password and Re-entered passwords don't match!");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();

                        // Show the Alert Dialog box
                        if (!((Activity) getActivity()).isFinishing()) {
                            //show dialog
                            alertDialog.show();
                        }
                    }
                }
            }
        });
        return view;
    }

    private void call_reset_password() {
        Call<ResetPassword> call = api.getReset_passwordJson(api_key, "Token " + pref.getString("token", null), "Android",
                old_pass, new_pass, confirm_pass);

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(Call<ResetPassword> call, Response<ResetPassword> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("resetPassDialogFragment");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();

                    Toast.makeText(getActivity(), response.body().getMessage()
                            , Toast.LENGTH_SHORT).show();

                    Log.v("all_log", "reset pass response:  successful");
                } else {
                    try {

                        //dismiss the reset password dialogfragment first
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment f = getFragmentManager().findFragmentByTag("resetPassDialogFragment");
                        if (f != null) {
                            ft.remove(f);
                        }
                        ft.commit();


                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        Log.v("all_log", "RESET password response : NOT success");
                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        else if (jObjError.has("message")) {
                            Build_alert_dialog(getActivity(), jObjError.getString("message"));
                        }
                        // Log.v("all_log", "message detail: " + jObjError.getString("detail"));

                        else if (jObjError.has("data")) {
                            String error_msg = jObjError.getString("message");
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(error_msg);
                            builder.setCancelable(true);

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            // Create the Alert dialog
                            AlertDialog alertDialog = builder.create();

                            // Show the Alert Dialog box
                            if (!((Activity) getActivity()).isFinishing()) {
                                //show dialog
                                alertDialog.show();
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<ResetPassword> call, Throwable t) {
                progressDialog.dismiss();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("resetPassDialogFragment");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        window.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);
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


                    call_reset_password();
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
