package com.rohan.myvoice.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.MainActivity;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.update_profile.UpdateProfile;
import com.rohan.myvoice.pojo.user_profile_settings_page.UserProfile;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    private TextView profile_tv, allow_notification_ans, account_verification_ans;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        //mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();
        api_key = getResources().getString(R.string.APIKEY);

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Settings");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.INVISIBLE);

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setText("Logout");
        logout_btn.setVisibility(View.VISIBLE);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to logout?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //when pressed the logout button
                        dialog.dismiss();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getActivity(), MainActivity.class));

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });


        profile_tv = v.findViewById(R.id.profile);
        profile_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new ProfileFragment()).commit();
            }
        });

        PublicClass.CURRENT_FRAG = 22;

        allow_notification_ans = v.findViewById(R.id.allow_notification_ans);
        account_verification_ans = v.findViewById(R.id.account_verification_ans);

        call_user_profile();


        allow_notification_ans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String FcmToken = PublicClass.FCM_TOKEN;
                final String device_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Allow Push Notifications?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        if (!allow_notification_ans.getText().toString().toLowerCase().equals("yes")) {
                            Call<UpdateProfile> call = api.getPushUpdateJson(api_key, "Token " + pref.getString("token", null),
                                    FcmToken, device_id, "Android", 1);
                            progressDialog.show();
                            call.enqueue(new Callback<UpdateProfile>() {
                                @Override
                                public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                                    if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                                        progressDialog.dismiss();
                                        dialog.cancel();
                                        allow_notification_ans.setText("Yes");
                                    } else {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(Call<UpdateProfile> call, Throwable t) {
                                    progressDialog.dismiss();
                                }
                            });
                        }

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (!allow_notification_ans.getText().toString().toLowerCase().equals("no")) {
                            Call<UpdateProfile> call = api.getPushUpdateJson(api_key, "Token " + pref.getString("token", null),
                                    FcmToken, device_id, "Android", 0);
                            progressDialog.show();
                            call.enqueue(new Callback<UpdateProfile>() {
                                @Override
                                public void onResponse(Call<UpdateProfile> call, Response<UpdateProfile> response) {
                                    if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                                        progressDialog.dismiss();
                                        dialog.cancel();
                                        allow_notification_ans.setText("No");
                                    } else {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(Call<UpdateProfile> call, Throwable t) {
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }

    private void call_user_profile() {

        Call<UserProfile> call = api.getUserProfile_json(api_key, "Token " + pref.getString("token", null));
        progressDialog.show();

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {

                    PublicClass.isNotificationAllowed = response.body().getData().getProfile().getIsPushnotification();

                    String isPushNotification = response.body().getData().getProfile().getIsPushnotification() ? "Yes" : "No";
                    allow_notification_ans.setText(isPushNotification);
                    account_verification_ans.setText(Boolean.toString(response.body().getData().getProfile().getIsVerified()).trim());

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */


                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            call_user_profile();
                        }

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

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
                } else {
                    //but but i can access the error body here.,
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        String status = jObjError.getString("message");
                        String error_msg = jObjError.getJSONObject("data").getString("errors");
                        Build_alert_dialog(getActivity(), status, error_msg);

                    } catch (Exception e) {
                        // Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
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
