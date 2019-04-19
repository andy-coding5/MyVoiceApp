package com.e2excel.myvoice.Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.RecyclerViewAdapterQuestionList;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.survey_questions_list.QuestionDatum;
import com.e2excel.myvoice.pojo.survey_questions_list.QuestionList;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsListFragment extends Fragment {

    View v;
    private ImageView logo;
    private TextView question_title, empty_textview;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterQuestionList recyclerViewAdapeter;

    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;

    private ImageView no_survey_imageview;

    private List<QuestionDatum> question_list;

    public QuestionsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_questions_list, container, false);
        logo = v.findViewById(R.id.company_logo);
        question_title = v.findViewById(R.id.question_text);

        mSwipeRefreshLayout = v.findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);
        empty_textview = v.findViewById(R.id.empty_view);


        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);

        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set the value of CURRENT_FRAG to 1
        PublicClass.CURRENT_FRAG = 1;

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        TextView t = toolbar.findViewById(R.id.title_text);
        t.setText("Questions");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new HomeFragment()).commit();
            }
        });

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);
        no_survey_imageview = v.findViewById(R.id.no_survey_img);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api_key = getResources().getString(R.string.APIKEY);

        api = RetroClient.getApiService();

        Glide.with(getActivity()).load(PublicClass.survey_logo).into(logo);

        question_title.setText(PublicClass.survey_text);
        recyclerViewAdapeter = new RecyclerViewAdapterQuestionList(getActivity(), question_list);

        call_question_fun();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call<QuestionList> call = api.getSurveyQuestionsListJson(api_key, "Token " + pref.getString("token", null), PublicClass.survey_id);

                //progressDialog.show();
                //empty_textview.setVisibility(View.INVISIBLE);
                no_survey_imageview.setVisibility(View.INVISIBLE);

                call.enqueue(new Callback<QuestionList>() {
                    @Override
                    public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                        //progressDialog.dismiss();

                        if (response.isSuccessful() && response.body().getStatus().equals("Success")) {

                            if (response.body().getQuestionCount().toString().equals("0")) {

                                recyclerView.setVisibility(View.INVISIBLE);
                                //empty_textview.setVisibility(View.VISIBLE);
                                no_survey_imageview.setVisibility(View.VISIBLE);

                            } else {
                                if (question_list != null) {
                                    question_list = response.body().getQuestionData();
                                    recyclerViewAdapeter = new RecyclerViewAdapterQuestionList(getActivity(), question_list);
                                    recyclerView.setAdapter(recyclerViewAdapeter);
                                    //recyclerViewAdapeter.notifyDataSetChanged();
                                } else {
                                    call_question_fun();
                                }
                            }
                            mSwipeRefreshLayout.setRefreshing(false);

                        } else {
                            //update_token();

                            Toast.makeText(getActivity(), "response not received", Toast.LENGTH_SHORT).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                /* String status = jObjError.getString("detail");
                                 */
                                if (jObjError.getString("message").equals("Questions not found")) {
                                    //empty_textview.setVisibility(View.VISIBLE);
                                    no_survey_imageview.setVisibility(View.VISIBLE);

                                    recyclerView.setVisibility(View.INVISIBLE);
                                }

                                if (jObjError.has("detail")) {
                                    if (jObjError.getString("detail").equals("Invalid Token")) {
                                        update_token();

                                    }
                                    else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                        DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                        deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                    }
                                }
                            } catch (Exception e) {
                                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<QuestionList> call, Throwable t) {
                        progressDialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void call_question_fun() {
        Call<QuestionList> call = api.getSurveyQuestionsListJson(api_key, "Token " + pref.getString("token", null), PublicClass.survey_id);

        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<QuestionList>() {
            @Override
            public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().getStatus().equals("Success")) {
                    if (response.body().getQuestionCount().equals("0")) {
                        mSwipeRefreshLayout.setVisibility(View.INVISIBLE);
                        //empty_textview.setVisibility(View.VISIBLE);
                        no_survey_imageview.setVisibility(View.VISIBLE);
                    } else {
                        //empty_textview.setVisibility(View.INVISIBLE);
                        no_survey_imageview.setVisibility(View.INVISIBLE);
                        question_list = response.body().getQuestionData();
                        recyclerView = v.findViewById(R.id.recyclerView);
                        recyclerViewAdapeter = new RecyclerViewAdapterQuestionList(getActivity(), question_list);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(recyclerViewAdapeter);
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        if (jObjError.getString("message").equals("Questions not found")) {
                            // empty_textview.setVisibility(View.VISIBLE);
                            no_survey_imageview.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }

                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token();

                            }
                            else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        mSwipeRefreshLayout.setRefreshing(false);

                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionList> call, Throwable t) {
                progressDialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                //Build_alert_dialog(getActivity(), "Connection Error", "Please Check You Internet Connection");
            }
        });
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
                pref2.getString("fcm_token",null),
                "Android", Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID));

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
                    Log.d("token", "Token " + pref.getString("token", null));

                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                    }
                    //call_api_coutry();
                    call_question_fun();
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
