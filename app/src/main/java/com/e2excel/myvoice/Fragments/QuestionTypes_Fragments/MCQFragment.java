package com.e2excel.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.e2excel.myvoice.CustomDialogs.DeleteAccountNotificationErrorDialogFragment;
import com.e2excel.myvoice.Fragments.QuestionsListFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.MainActivity;
import com.e2excel.myvoice.R;
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
public class MCQFragment extends Fragment {

    private static String MEDIA = "false";
    View v;
    private String q_id, q_text;
    private ProgressDialog progressDialog;

    private TextView textView;
    private FrameLayout frameLayout;
    private ImageView imageView;
    private WebView webView;

    private Button submit_button;

    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;
    CheckBox[] cb;

    private ArrayList<String> selected_options, selected_options_keys;

    private Data data;


    public MCQFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_mcq, container, false);


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

        textView = v.findViewById(R.id.full_que);
        frameLayout = v.findViewById(R.id.frame_view);
        imageView = v.findViewById(R.id.image_view);
        webView = v.findViewById(R.id.web_view);
        submit_button = v.findViewById(R.id.submit_btn);
        //option_list_view = v.findViewById(R.id.options_list);


        //intially visibility is gone
        frameLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);

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

        //textView.setText(q_text);       //q_text
        selected_options = new ArrayList<>();
        selected_options_keys = new ArrayList<>();

        load_question();

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                submit_call();


            }
        });
    }

    private void submit_call() {
        Log.d("check_box", selected_options.toString());

        //first make a map of key value pair
                /*Map<String, String> final_map = new HashMap<>();
                for (int i = 0; i < selected_options.size(); i++) {
                    final_map.put(selected_options_keys.get(i), selected_options.get(i));
                }*/
        JSONArray ja = new JSONArray();      //main parent


        for (int i = 0; i < selected_options.size(); i++) {
            JSONObject jo = new JSONObject();

            try {
                jo.put("Key", selected_options_keys.get(i));
                jo.put("Value", selected_options.get(i));
                ja.put(jo);
                Log.v("final_json", ja.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        //if IsNext = No
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
                            else if (jObjError.has("message")) {
                                Build_alert_dialog(getActivity(), jObjError.getString("message"));
                            }
                        } catch (Exception e) {

                        }

                        if ("No".equals(response.body().getIsNext())) {
                            progressDialog.dismiss();
                            Log.v("test", "from MCQ: response.body().getIsNext()" + response.body().getIsNext());
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

                            Log.v("test", "from MCQ: redirect to the new fragmnent :" + String.valueOf(response.body().getQuestionType()));

                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, myFragment).commit();
                            //replace = remove and then add
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

    private void load_question() {
        Call<QuestionDetail> call = api.getSCQ_MCQ_RNKJson(api_key, "Token " + pref.getString("token", null), q_id);

        if(!((Activity) getActivity()).isFinishing())
        {
            //show dialog
            progressDialog.show();
        }
        call.enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail> call, Response<QuestionDetail> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().getStatus().equals("Sucess")) {
                    //display the question
                    textView.setText(response.body().getData().getQuestionText());

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
                            // Enable Javascript
                            WebSettings webSettings = webView.getSettings();
                            webView.setWebViewClient(new WebViewClient());      //to load content inside the webview rather then open it in browser
                            webSettings.setJavaScriptEnabled(true);
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
                    }

                    //inti the data obj

                    data = response.body().getData();

                    //loading the options

                    //design dynamic buttons and add them into the constrain view resides inside the scroll view
                    final List<Option> op = response.body().getData().getQuestionOptionsSCQMCQRNK().getOptions();

                    LinearLayout ll = v.findViewById(R.id.inside_ll);
                    if (MEDIA.equals("false")) {
                        ll.removeAllViews();
                    }

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
                    //implementation of check box here
                    cb = new CheckBox[op.size()];

                    for (int i = 0; i < cb.length; i++) {

                        cb[i] = new CheckBox(getActivity());
                        cb[i].setTag(op.get(i).getKey());
                        cb[i].setText(op.get(i).getValue());
                        cb[i].setTextColor(getActivity().getResources().getColor(R.color.grey));
                        cb[i].setTextSize(15);
                        cb[i].setBackground(getActivity().getDrawable(R.drawable.option_items_scq_mcq));

                        Typeface fonts = Typeface.createFromAsset(getActivity().getAssets(), "quicksand_regular.ttf");
                        cb[i].setTypeface(fonts);
                        //checkBox.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
                        params.setMargins(2, 10, 2, 10);
                        cb[i].setOnClickListener(getOnClickDoSomething(cb[i]));

                        cb[i].setButtonTintList(
                                ContextCompat.getColorStateList(getActivity(),
                                        R.color.grey));
                        ll.addView(cb[i], params);
                    }
                    //final List<Option> op2 = response.body().getData().getQuestionOptionsSCQMCQRNK().getOptions();


                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        /* String status = jObjError.getString("detail");
                         */
                        //call update token function only when Error is "Invalid token" received form the server
                        if (jObjError.has("detail")) {
                            if (jObjError.getString("detail").equals("Invalid Token")) {
                                update_token_que();

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
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.e2excel.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private View.OnClickListener getOnClickDoSomething(final CheckBox checkBox) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("checkbox", checkBox.getText() + " and id is: " + checkBox.getId());

                Typeface fonts_1 = Typeface.createFromAsset(getActivity().getAssets(), "quicksand_medium.ttf");
                checkBox.setTypeface(fonts_1);
                checkBox.getId();

                //first clear all the options
                selected_options.clear();
                selected_options_keys.clear();

                for (int i = 0; i < cb.length; i++) {

                    if (cb[i].isChecked()) {
                        selected_options_keys.add(cb[i].getTag().toString().trim());        //getTag -> keys
                        selected_options.add(cb[i].getText().toString().trim());           //getText -> value
                    } else {
                        //add the selected options here
                        Typeface fonts = Typeface.createFromAsset(getActivity().getAssets(), "quicksand_regular.ttf");
                        cb[i].setTypeface(fonts);

                    }
                }
            }
        };
    }

}
