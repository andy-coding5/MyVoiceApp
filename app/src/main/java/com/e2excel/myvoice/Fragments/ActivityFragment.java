package com.e2excel.myvoice.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2excel.myvoice.ActivityListAdapter;
import com.e2excel.myvoice.Activity_tab_obj;
import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.activity_details.Activities;
import com.e2excel.myvoice.pojo.activity_details.AnswerDatum;
import com.e2excel.myvoice.pojo.survey_details.Survey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends Fragment {
    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    private TextView username, no_ans, not_answers_tv;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private StickyListHeadersListView stickyList;

    public ActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_activity, container, false);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api = RetroClient.getApiService();


        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Activity");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.INVISIBLE);

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        //select the home button (so color is now blue) and the
       /* BottomNavigationView bv = getActivity().findViewById(R.id.bottom_navigation);
        //bv.getMenu().getItem(0).setChecked(true);
        bv.setSelectedItemId(R.id.activity_menu_item);*/

        BottomNavigationView mBottomNavigationView=(BottomNavigationView)getActivity().findViewById(R.id.bottom_navigation);
        mBottomNavigationView.getMenu().findItem(R.id.activity_menu_item).setChecked(true);

        username = v.findViewById(R.id.user_name);
        no_ans = v.findViewById(R.id.no_of_answers);
        no_ans.setText("You have answered 0  questions");        //You have answered 7 questions
        stickyList = v.findViewById(R.id.list);

        not_answers_tv = v.findViewById(R.id.no_ansers_textview);
        not_answers_tv.setVisibility(View.INVISIBLE);

        api_key = getResources().getString(R.string.APIKEY);

        username.setText(pref.getString("username", "user") + ", ");      //ex: rohan,

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Call<Survey> call = api.getSurveyJson(api_key, "Token " + pref.getString("token", null));
                //progressDialog.show();
                call.enqueue(new Callback<Survey>() {
                    @Override
                    public void onResponse(Call<Survey> call, Response<Survey> response) {
                        //progressDialog.dismiss();

                        if (response.isSuccessful() && response.body().getStatus().equals("Success")) {
                            call_activity_list();
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

                                else {
                                    progressDialog.dismiss();
                                    mSwipeRefreshLayout.setRefreshing(false);
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
                    }
                });
            }
        });

        call_activity_list();

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
        Log.d("update_token", "login called");
        if(!((Activity) getActivity()).isFinishing())
        {
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
                    Log.d("update_token", "update token response success : " + response.body().getData().getToken());

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    call_activity_list();
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

    public void call_activity_list() {
        Call<Activities> call = api.getActivityJson(api_key, "Token " + pref.getString("token", null));

        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<Activities>() {
            @Override
            public void onResponse(Call<Activities> call, Response<Activities> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    String ans_count = response.body().getAnswerCount().toString();
                    no_ans.setText("You have answered " + ans_count + " questions");        //You have answered 7 questions

                    int size = response.body().getAnswerData().size();      //no. of surveys

                    //survey title array for heading grouping logic,,,send it to adapter class
                    String[] survey_titles = new String[size];

                    List<AnswerDatum> answer_data = response.body().getAnswerData();

                    Activity_tab_obj[] ob_ary = new Activity_tab_obj[response.body().getAnswerCount()];

                    String title, company;
                    int k = 0;
                    for (int i = 0; i < size; i++) {

                        title = answer_data.get(i).getTitle();
                        company = answer_data.get(i).getCompany();
                        for (int j = 0; j < answer_data.get(i).getQuestions().size(); j++) {
                            ob_ary[k] = new Activity_tab_obj();
                            ob_ary[k].setSurvey_title(title);
                            ob_ary[k].setCompany(company);
                            ob_ary[k].setQuestion(answer_data.get(i).getQuestions().get(j).getData().get(0).getQuestionText());
                            ob_ary[k].setAns(answer_data.get(i).getQuestions().get(j).getData().get(0).getAnswerString());
                            ob_ary[k].setLogical_survey_title(String.valueOf(i) + title);   //for ex, is two survey has name google google then from here titles would be 1google 2google
                            k++;
                        }
                    }

                    Context context = getActivity();

                    if ((ob_ary != null || ob_ary.length != 0) && context != null) {
                        ActivityListAdapter adapter = new ActivityListAdapter(context, ob_ary);
                        stickyList.setAdapter(adapter);

                        stickyList.setDivider(null);
                    }

                    if (response.body().getAnswerCount() == 0) {
                        not_answers_tv.setVisibility(View.VISIBLE);
                    }


                } else {
                    Log.v("test", "in else");
                    try {
                        JSONObject jObjError = null;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /* String status = jObjError.getString("detail");
                         */

                        /*if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                            call_activity_list();
                        } else {
                            progressDialog.dismiss();
                        }*/


                        if (jObjError.has("detail")) {

                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        } else {
                            String status = null;

                            status = jObjError.getString("message");

                            not_answers_tv.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(getActivity(), jObjError.toString(), Toast.LENGTH_LONG).show();

                    //Build_alert_dialog(getApplicationContext(), "Error", status);

                }
            }

            @Override
            public void onFailure(Call<Activities> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }
}
