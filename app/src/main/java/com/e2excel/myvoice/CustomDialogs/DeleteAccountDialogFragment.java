package com.e2excel.myvoice.CustomDialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
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

import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.delete_account.DeleteAccount;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

public class DeleteAccountDialogFragment extends DialogFragment {

    ApiService api;
    String api_key;
    private ProgressDialog progressDialog;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    public static boolean ACCDELETESTATUS;


    public DeleteAccountDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        View view = inflater.inflate(R.layout.custom_dialog_delete_account, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }

        Button dialogButton_no = (Button) view.findViewById(R.id.no_btn);
        Button dialogButton_yes = (Button) view.findViewById(R.id.yes_btn);
        // if button is clicked, close the custom dialog
        dialogButton_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        dialogButton_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();

                call_delete_account();

            }
        });
        return view;
    }

    private void call_delete_account() {
        ACCDELETESTATUS = false;

        String FcmToken = pref2.getString("fcm_token", null);

        Call<DeleteAccount> call = api.getDelete_accountJson(api_key, "Token " + pref.getString("token", null), FcmToken, "Android");
        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }
        call.enqueue(new Callback<DeleteAccount>() {
            @Override
            public void onResponse(Call<DeleteAccount> call, Response<DeleteAccount> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    //dismiss();
                    Log.v("all_log", "delete_accout_reponse: success");
                    ACCDELETESTATUS = true;
                    /*DeleteAccountConfirmationDialogFragment deleteAccountConfirmationDialogFragment = new DeleteAccountConfirmationDialogFragment();
                    AppCompatActivity activity = (AppCompatActivity) getActivity();
                    deleteAccountConfirmationDialogFragment.show(activity.getSupportFragmentManager(), "deleteAccountConfirmationDialogFragment");*/
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment f = getFragmentManager().findFragmentByTag("deleteAccountDialog");
                    if (f != null) {
                        ft.remove(f);
                    }
                    ft.commit();

                    //now show the dialog fragment of delete confirmation
                    DeleteAccountConfirmationDialogFragment deleteAccountConfirmationDialogFragment = new DeleteAccountConfirmationDialogFragment();
                    //AppCompatActivity activity = (AppCompatActivity) getActivity();
                    Bundle b = new Bundle();
                    b.putString("message", response.body().getMessage().toString().trim());
                    deleteAccountConfirmationDialogFragment.setArguments(b);
                    deleteAccountConfirmationDialogFragment.show(getFragmentManager(), "deleteAccountConfirmationDialogFragment");

                } else {
                    try {
                        ACCDELETESTATUS = false;
                        dismiss();
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        Log.v("all_log", "delete_accout_reponse: NOT success");
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

                        Log.v("all_log", "message detail: " + jObjError.getString("detail"));

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<DeleteAccount> call, Throwable t) {

                progressDialog.dismiss();
                dismiss();
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

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    call_delete_account();
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
