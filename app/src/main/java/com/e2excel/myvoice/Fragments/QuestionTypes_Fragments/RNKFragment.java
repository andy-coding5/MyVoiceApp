package com.e2excel.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.Fragments.QuestionsListFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;
import com.e2excel.myvoice.RecyclerViewAdapter_RankOrder;
import com.e2excel.myvoice.Retrofit.ApiService;
import com.e2excel.myvoice.Retrofit.RetroClient;
import com.e2excel.myvoice.pojo.Response.response;
import com.e2excel.myvoice.pojo.SignIn.Login;
import com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.Data;
import com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.Option;
import com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.e2excel.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class RNKFragment extends Fragment {

    private static String MEDIA = "false";
    View v;
    private String q_id, q_text;
    private ProgressDialog progressDialog;

    private TextView que;
    private FrameLayout frameLayout;
    private ImageView imageView;
    private WebView webView;

    private Button submit_button;

    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;

    RecyclerView recyclerView;
    RecyclerViewAdapter_RankOrder mAdapter;
    List<Option> stringArrayList;
    ItemTouchHelper touchHelper;

    private Data data;


    public RNKFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_ro, container, false);


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        TextView t = toolbar.findViewById(R.id.title_text);
        t.setText("Question");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new QuestionsListFragment()).commit();
            }
        });

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        que = v.findViewById(R.id.full_que);
        frameLayout = v.findViewById(R.id.frame_view);
        imageView = v.findViewById(R.id.image_view);
        webView = v.findViewById(R.id.web_view);

        //option_list_view = v.findViewById(R.id.options_list);


        //intially visibility is gone
        frameLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);

        recyclerView = v.findViewById(R.id.recyclerView);

        submit_button = v.findViewById(R.id.submit_btn);

        Bundle bundle = this.getArguments();
        q_id = bundle.get("q_id").toString();
        //q_text = bundle.get("q_text").toString();

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);

        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //when ever the question detail frag is there i increses the value more then 1
        PublicClass.CURRENT_FRAG = 2;

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();
        pref2 = this.getActivity().getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);

        api_key = getResources().getString(R.string.APIKEY);

        api = RetroClient.getApiService();

        //que.setText(q_text);          //q_text

        load_question();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
               submit_call();

            }
        });
    }

    private void submit_call() {
        //ANs is -> current.getText();
        Log.v("rnk", "Item Order When Submit");

        JSONArray ja = new JSONArray();
        for (Option op : stringArrayList) {
            JSONObject jo = new JSONObject();

            try {
                jo.put("Key", op.getKey());
                jo.put("Value", op.getValue());
                ja.put(jo);
                Log.v("final_json", ja.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.v("rnk", "Option key: " + op.getKey() + " Option value: " + op.getValue() + "\n");


        }


        if (ja.length() > 0) {

            Call<response> call1 = api.getMCQResponseJson(api_key, "Token " + pref.getString("token", null),
                    data.getAttributeID().toString(), data.getQuestionID().toString(),
                    data.getParentID().toString(), ja,
                    "Android", PublicClass.MainParentID.trim());

            if(!((Activity) getActivity()).isFinishing())
            {
                //show dialog
                progressDialog.show();
            }

            call1.enqueue(new Callback<response>() {
                @Override
                public void onResponse(Call<response> call, Response<response> response) {
                    if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                        progressDialog.dismiss();
                        Toast toast = new Toast(getActivity());
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0,20);
                        LayoutInflater inflater = getActivity().getLayoutInflater();



                        View toastLayout = inflater.inflate(R.layout.custom_toast,
                                null);

                        TextView  view1=(TextView)toastLayout.findViewById(R.id.toast_text);
                        view1.setText("Answer submitted successfully");

                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(toastLayout);
                        toast.show();

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());

                            if (jObjError.has("detail")) {
                                if (jObjError.getString("detail").equals("Invalid Token")) {
                                    update_token_submit();

                                }
                                else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                    DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                    deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                                }
                            }
                        } catch (Exception e) {

                        }
                        //if IsNext = No
                        if ("No".equals(response.body().getIsNext())) {
                            progressDialog.dismiss();
                            Log.v("test", "form RNK: response.body().getIsNext()" + response.body().getIsNext());
                            getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new QuestionsListFragment()).commit();

                        } else {
                            progressDialog.dismiss();
                            //if IsNext = Yes
                            //there are children question(s)...we got id and and question type from the response.
                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            Fragment myFragment = null;
                            String q_type = String.valueOf(response.body().getQuestionType());
                            Log.v("test", "from MCQ: response.body().getIsNext()-(before switch)" + String.valueOf(response.body().getQuestionType()));

                            switch (q_type) {
                                case "SCQ": {
                                    myFragment = new SCQFragment();
                                    break;
                                }
                                case "MCQ": {
                                    myFragment = new MCQFragment();
                                    break;
                                }
                                case "OTT": {
                                    myFragment = new OTTFragment();
                                    break;
                                }
                                case "SCL": {
                                    myFragment = new SCLFragment();
                                    break;
                                }
                                case "RNK": {
                                    myFragment = new RNKFragment();
                                    break;
                                }
                                case "OTN": {
                                    myFragment = new OTNFragment();
                                    break;
                                }
                            }
                            Bundle b = new Bundle();
                            //b.putString("q_text", mdata.get(getPosition()).getQuestionText());
                            b.putString("q_id", String.valueOf(response.body().getQuestionID()));
                            myFragment.setArguments(b);

                            Log.v("test", "fro RNK: redirect to the new fragmnent :" + String.valueOf(response.body().getQuestionType()));

                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, myFragment).commit();
                        }
                    }
                }

                @Override
                public void onFailure(Call<response> call, Throwable t) {
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void load_question() {
        Call<QuestionDetail> call = api.getSCQ_MCQ_RNKJson(api_key, "Token " + pref.getString("token", null), q_id);

        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<QuestionDetail> call, Response<QuestionDetail> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().getStatus().equals("Sucess")) {

                    //display the question
                    que.setText(response.body().getData().getQuestionText());

                    if ("true".equals(response.body().getData().getQuestionIsMedia().toString())) {

                        MEDIA = "true";
                        //checking and loading for image audio or video
                        if (!"".equals(response.body().getData().getQuestionMedia())) {
                            frameLayout.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.VISIBLE);
                            Glide.with(v).load(response.body().getData().getQuestionMedia()).into(imageView);
                        } else if (!"".equals(response.body().getData().getQuestionVideoMedia())) {
                            frameLayout.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            //Enable Javascript
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                            webView.setWebViewClient(new WebViewClient());      //to load content inside the webview rather then open it in browser
                            webView.loadUrl(response.body().getData().getQuestionVideoMedia());


                        } else if (!"".equals(response.body().getData().getQuestionAudioMedia())) {
                            frameLayout.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            // Enable Javascript
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                            webView.setWebViewClient(new WebViewClient());      //to load content inside the webview rather then open it in browser
                            webView.loadUrl(response.body().getData().getQuestionAudioMedia());
                        }
                    } else {
                        //remove top constraint of liner layout of (3 out of 10) from frame_view bottom to full_que bottom
                        ConstraintLayout constraintLayout = v.findViewById(R.id.constraint_layout);
                        ConstraintSet constraintSet = new ConstraintSet();

                        constraintSet.clone(constraintLayout);

                        constraintSet.connect(R.id.info_text, constraintSet.TOP, que.getId(), constraintSet.BOTTOM, 24);

                        constraintSet.applyTo(constraintLayout);

                    }

                    //inti the data object
                    data = response.body().getData();

                    //view setup complete
                    //now, make array of option received in response to set up recyclervire adapter
                    stringArrayList = response.body().getData().getQuestionOptionsSCQMCQRNK().getOptions();


                    ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
                        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                            Collections.swap(stringArrayList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                            mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                            return true;
                        }

                        @Override
                        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                            // no-op
                        }

                        @Override
                        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                            return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                                    ItemTouchHelper.DOWN | ItemTouchHelper.UP);
                        }
                    });

                    touchHelper.attachToRecyclerView(recyclerView);

                    mAdapter = new RecyclerViewAdapter_RankOrder(stringArrayList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                   /* ItemTouchHelper.Callback callback =
                            new ItemMoveCallback(mAdapter);
                    touchHelper  = new ItemTouchHelper(callback);
                    touchHelper.attachToRecyclerView(recyclerView);*/

                    recyclerView.setAdapter(mAdapter);

                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        //call update token function only when Error is "Invalid token" received form the server
                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_que();
                            } else if (jObjError.getString("detail").equals("AccountDeleted")) {
                                DeleteAccountNotificationErrorDialogFragment deleteAccountNotificationErrorDialogFragment = new DeleteAccountNotificationErrorDialogFragment();
                                deleteAccountNotificationErrorDialogFragment.show(getFragmentManager(), "DeleteNotificationDialogFragment");
                            }
                        }
                        else if (jObjError.has("message")) {
                            Build_alert_dialog(getActivity(), jObjError.getString("message"));
                        }

                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionDetail> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void update_token_submit() {
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
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

                    submit_call();
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

    private void update_token_que() {
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
        /*if (PublicClass.FCM_TOKEN != null) {
            editor.putString("fcm_token", PublicClass.FCM_TOKEN);
            editor.commit();
        }*/

        Call<Login> call = api.getLoginJason(pref.getString("email", null), pref.getString("password", null),
                pref2.getString("fcm_token", null),
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

                    load_question();
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


