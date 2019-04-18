package com.e2excel.myvoice.CustomDialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.update_profile.UpdateProfile;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

public class AllowNotificationDialogFragment extends DialogFragment {
    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;


    public AllowNotificationDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        View view = inflater.inflate(R.layout.custom_dialog_allow_notification, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }

        Button dialogButton_no = (Button) view.findViewById(R.id.no_btn);
        Button dialogButton_OK = (Button) view.findViewById(R.id.yes_btn);


        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("notificationDialogFragment");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();


                call_allow_notification(0);

            }
        });

        dialogButton_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("notificationDialogFragment");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();


                call_allow_notification(1);

            }
        });
        return view;
    }

    private void call_allow_notification(final int operation_code) {
        final String FcmToken = pref2.getString("fcm_token", null);
        final String device_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getFragmentManager().findFragmentByTag("notificationDialogFragment");
        if (f != null) {
            ft.remove(f);
        }
        ft.commit();

        //call for setting the preference of allowing push notifications

        Call<UpdateProfile> call = api.getPushUpdateJson(api_key, "Token " + pref.getString("token", null),
                FcmToken, device_id, "Android", operation_code);
        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();

        }
        call.enqueue(new Callback<UpdateProfile>() {
            @Override
            public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                    progressDialog.dismiss();
                    // View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, null);
                    // TextView t = view.findViewById(R.id.allow_notification_ans);
                    PublicClass.isNotificationAllowed = (operation_code == 1) ? true : false;
                    //getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    Intent i = new Intent();
                    i.putExtra("status", PublicClass.isNotificationAllowed);
                   // TextView t = v.findViewById(R.id.allow_notification_ans);
                   // t.setText(PublicClass.isNotificationAllowed == true ? "Yes" : "No");
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                } else {
                    progressDialog.dismiss();
                    try {

                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        Log.v("all_log", "allow_notification response: NOT success");
                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token(operation_code);

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        else if (jObjError.has("message")) {
                            Build_alert_dialog(getActivity(), jObjError.getString("message"));
                        }
                        ///Log.v("all_log", "message detail: " + jObjError.getString("detail"));

                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onFailure(Call<UpdateProfile> call, Throwable t) {
                progressDialog.dismiss();
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

    public void update_token(final int operation_code) {
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

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    call_allow_notification(operation_code);
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
