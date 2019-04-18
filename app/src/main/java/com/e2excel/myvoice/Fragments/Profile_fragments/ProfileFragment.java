package com.e2excel.myvoice.Fragments.Profile_fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.Fragments.SettingsFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.user_profile_settings_page.UserProfile;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    private TextView username_title, first_name_tv, last_name_tv, email_tv, country_tv, state_tv, city_tv, zip_tv, education_tv, gender_tv, dob_tv, income_tv;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api = RetroClient.getApiService();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        PublicClass.CURRENT_FRAG = 221;

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Profile");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new SettingsFragment()).commit();
            }
        });

        TextView edit_btn = toolbar.findViewById(R.id.logout_textview);
        edit_btn.setText("Edit");
        edit_btn.setPadding(0, 5, 0, 0);
        edit_btn.setVisibility(View.VISIBLE);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when pressed the logout button
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new EditProfileFragment()).commit();

            }
        });


        username_title = v.findViewById(R.id.user_name);
        first_name_tv = v.findViewById(R.id.first_name);
        last_name_tv = v.findViewById(R.id.last_name);
        email_tv = v.findViewById(R.id.email);
        country_tv = v.findViewById(R.id.country);
        state_tv = v.findViewById(R.id.state);
        city_tv = v.findViewById(R.id.city);
        zip_tv = v.findViewById(R.id.zip);
        education_tv = v.findViewById(R.id.education);
        gender_tv = v.findViewById(R.id.gender);
        dob_tv = v.findViewById(R.id.dob);
        income_tv = v.findViewById(R.id.income);
        api_key = getResources().getString(R.string.APIKEY);

        /*
        now we call the update token function but here not for
        only updating the token, also we will get all the information that
        we have to display over the profile fragment fields
        */

        profile_call();
    }

    //update token function with some changes
    public void profile_call() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
       /* if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }
*/      Call<UserProfile> call = api.getUserProfile_json(api_key, "Token " + pref.getString("token", null));

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();

                    Log.d("token", "Token " + pref.getString("token", null));

                    username_title.setText("Hi " + pref.getString("username", "user") + ", ");

                    first_name_tv.setText(pref.getString("username", "user"));
                    last_name_tv.setText(pref.getString("lastname", " "));
                    email_tv.setText(pref.getString("email", null));
                    country_tv.setText(response.body().getData().getProfile().getCountryname().toString().trim());
                    state_tv.setText(response.body().getData().getProfile().getStatename().toString().trim());
                    city_tv.setText(response.body().getData().getProfile().getCity().toString().trim());
                    zip_tv.setText(response.body().getData().getProfile().getZipcode().toString().trim());

                    education_tv.setText(response.body().getData().getProfile().getEducationname().toString().trim());
                    gender_tv.setText(response.body().getData().getProfile().getGendername().toString().trim());
                    dob_tv.setText(response.body().getData().getProfile().getDob().toString().trim());
                    //change the date representation format from y-m-d to m-d-y

                    String date[] = dob_tv.getText().toString().trim().split("-");

                    dob_tv.setText(date[1] + "-" + date[2] + "-" + date[0]);        //now its m-d-y

                    income_tv.setText(response.body().getData().getProfile().getIncome().toString().trim());

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    //call_api_coutry();
                }
                else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */


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

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });

    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/

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
                    profile_call();
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
