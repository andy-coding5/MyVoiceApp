package com.e2excel.myvoice.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.RecyclerViewAdapterSurveyList;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.survey_details.ProjectDatum;
import com.e2excel.myvoice.pojo.survey_details.Survey;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View v;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterSurveyList recyclerViewAdapeter;
    private List<ProjectDatum> survey_list;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView empty_textview;
    int count = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);

/*
        Toolbar mToolbar = (Toolbar) v.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
*/

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);
        empty_textview = v.findViewById(R.id.empty_view);
        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);
        Log.v("fcm_token", "Fcm_token: in homefragment activity " + pref2.getString("fcm_token", "null or empty"));

        api = RetroClient.getApiService();
        //update_token();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView t = v.findViewById(R.id.welcome_title);
        t.setText("Welcome " + pref.getString("username", "User") + "!");
        Log.d("token", "Token " + pref.getString("token", null));
        /**
         * Calling JSON
         */
        //   String t = Token.token_string;
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Home");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.INVISIBLE);

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        //select the home button (so color is now blue) and the
        /*BottomNavigationView bv = getActivity().findViewById(R.id.bottom_navigation);
        //bv.getMenu().getItem(0).setChecked(true);
        bv.setSelectedItemId(R.id.home_menu_item);*/
        BottomNavigationView mBottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        mBottomNavigationView.getMenu().findItem(R.id.home_menu_item).setChecked(true);

        //View v = mBottomNavigationView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        // BottomManuHelper.showBadge(getActivity(), mBottomNavigationView, R.id.notifications_menu_item, "7");


        survey_list = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);

        api_key = getResources().getString(R.string.APIKEY);
        recyclerViewAdapeter = new RecyclerViewAdapterSurveyList(getActivity(), survey_list);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.VISIBLE);
                Call<Survey> call = api.getSurveyJson(api_key, "Token " + pref.getString("token", null));
                //progressDialog.show();
                call.enqueue(new Callback<Survey>() {
                    @Override
                    public void onResponse(Call<Survey> call, Response<Survey> response) {
                        //progressDialog.dismiss();

                        if (response.isSuccessful() && response.body().getStatus().equals("Success")) {

                            if (response.body().getProjectData().size() == 0) {
                                recyclerView.setVisibility(View.INVISIBLE);
                                empty_textview.setVisibility(View.VISIBLE);

                            } else {
                                //survey_list.clear();
                                //survey_list = response.body().getProjectData();
                                if (survey_list != null) {
                                    survey_list = response.body().getProjectData();
                                    recyclerViewAdapeter = new RecyclerViewAdapterSurveyList(getActivity(), survey_list);
                                    recyclerView.setAdapter(recyclerViewAdapeter);
                                    // recyclerViewAdapeter = new RecyclerViewAdapterSurveyList(getActivity(), survey_list);
                                    // recyclerView.setAdapter(recyclerViewAdapeter);

                                    // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

                                } else {
                                    call_function();
                                }

                            }

                            mSwipeRefreshLayout.setRefreshing(false);


                        } else {
                            //update_token();

                            //Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                /* String status = jObjError.getString("detail");
                                 */
                                if (jObjError.getString("detail").equals("Invalid Token")) {
                                    update_token();
                                }
                                else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                    DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                    deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                }
                                if (response.body().getMessage().equals("Survey not found")) {
                                    recyclerView.setVisibility(View.INVISIBLE);

                                    empty_textview.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Survey> call, Throwable t) {
                        progressDialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                        //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
                    }
                });
            }
        });

        call_function();


    }

    private void call_function() {
        Call<Survey> call = api.getSurveyJson(api_key, "Token " + pref.getString("token", null));
        Log.d("token_detail", "used for server list: " + pref.getString("token", null));
        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }


        call.enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(Call<Survey> call, Response<Survey> response) {

                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("Success")) {
                        if (response.body().getProjectData().size() == 0) {
                            mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                            empty_textview.setVisibility(View.VISIBLE);

                        } else {
                            empty_textview.setVisibility(View.INVISIBLE);
                            survey_list = response.body().getProjectData();
                            Context context = getActivity();

                            if ((survey_list != null || survey_list.size() != 0) && context != null) {
                                recyclerViewAdapeter = new RecyclerViewAdapterSurveyList(getActivity(), survey_list);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                recyclerView.setAdapter(recyclerViewAdapeter);
                                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                                        DividerItemDecoration.VERTICAL));
                            }
                        }
                    }else {
                        recyclerView.setVisibility(View.INVISIBLE);

                        empty_textview.setVisibility(View.VISIBLE);
                    }

                } else {
                    // update_token();

                    //Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();

                            } else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        else {
                            recyclerView.setVisibility(View.INVISIBLE);

                            empty_textview.setVisibility(View.VISIBLE);
                        }
                        //Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                        //Build_alert_dialog(getApplicationContext(), "Error", status);

                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Survey> call, Throwable t) {
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
        }
*/
        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d("update_token", "login called");
        progressDialog.show();

        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    //editor = pref.edit();
                    editor.putString("token", response.body().getData().getToken());

                    editor.commit();
                    Log.d("update_token", "update token response success : " + response.body().getData().getToken());

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    //call_api_coutry();

                    call_function();


                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
