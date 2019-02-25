package com.rohan.myvoice.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.R;
import com.rohan.myvoice.RecyclerViewAdapeter;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.personal_info_1;
import com.rohan.myvoice.personal_info_2;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.survey_details.ProjectDatum;
import com.rohan.myvoice.pojo.survey_details.Survey;

import org.json.JSONObject;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View v;
    private RecyclerView recyclerView;
    private RecyclerViewAdapeter recyclerViewAdapeter;
    private List<ProjectDatum> survey_list;
    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);


        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Fetching Data");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();
        Log.d("token","Token " + pref.getString("token", null));
        update_token();

        /**
         * Calling JSON
         */
        //   String t = Token.token_string;

        api_key = getResources().getString(R.string.APIKEY);


        Call<Survey> call = api.getSurveyJson(api_key, "Token " + pref.getString("token", null));

        progressDialog.show();

        call.enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(Call<Survey> call, Response<Survey> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().getStatus().equals("Success")) {
                    survey_list = response.body().getProjectData();
                    recyclerView = v.findViewById(R.id.recyclerView);
                    recyclerViewAdapeter = new RecyclerViewAdapeter(getContext(), survey_list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(recyclerViewAdapeter);

                } else {
                    update_token();

                    Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Survey> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });


    }

    public void update_token() {
        //pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toast.makeText(getActivity(), "email from pref: " + pref.getString("email", "not fatched from pref"), Toast.LENGTH_SHORT).show();
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null), pref.getString("fcm_token", null),
                "android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("token","Token " + pref.getString("token", null));

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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });


    }
}
