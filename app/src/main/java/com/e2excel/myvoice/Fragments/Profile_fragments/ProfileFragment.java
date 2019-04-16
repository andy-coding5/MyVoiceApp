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

import com.e2excel.myvoice.Fragments.SettingsFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


        /*
        now we call the update token function but here not for
        only updating the token, also we will get all the information that
        we have to display over the profile fragment fields
        */

        update_token();
    }

    //update token function with some changes
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

                    username_title.setText("Hi " + response.body().getData().getFirstName().toString().trim() + ", ");

                    first_name_tv.setText(response.body().getData().getFirstName().toString().trim());
                    last_name_tv.setText(response.body().getData().getLastName().toString().trim());
                    email_tv.setText(pref.getString("email", null));
                    country_tv.setText(response.body().getData().getProfile().getCountry().toString().trim());
                    state_tv.setText(response.body().getData().getProfile().getState().toString().trim());
                    city_tv.setText(response.body().getData().getProfile().getCity().toString().trim());
                    zip_tv.setText(response.body().getData().getProfile().getZipcode().toString().trim());

                    education_tv.setText(response.body().getData().getProfile().getEducation().toString().trim());
                    gender_tv.setText(response.body().getData().getProfile().getGender().toString().trim());
                    dob_tv.setText(response.body().getData().getProfile().getDob().toString().trim());
                    income_tv.setText(response.body().getData().getProfile().getIncome().toString().trim());


                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
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
