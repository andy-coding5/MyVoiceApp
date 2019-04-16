package com.rohan.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.Fragments.QuestionsListFragment;
import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.Response.response;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.survey_question_detail_OTT.Data;
import com.rohan.myvoice.pojo.survey_question_detail_OTT.QuestionDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.rohan.myvoice.MainActivity.Build_alert_dialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTTFragment extends Fragment {

    private static String MEDIA = "false";
    View v;
    private String q_id, q_text, response_text = "";
    private ProgressDialog progressDialog;

    private TextView textView, response_text_view;
    private FrameLayout frameLayout;
    private ImageView imageView;
    private WebView webView;

    private ImageButton mic_image;

    private Button submit_button;
    private static int MAX_SIZE = 100;
    private final static int RESULT_SPEECH = 100;
    ApiService api;
    String api_key;
    private SharedPreferences pref, pref2;
    private SharedPreferences.Editor editor;

    private Data data;


    public OTTFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_ott, container, false);


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

        response_text_view = v.findViewById(R.id.response_text);

        mic_image = v.findViewById(R.id.mic_img);
        //option_list_view = v.findViewById(R.id.options_list);


        //intially visibility is gone
        frameLayout.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        submit_button = v.findViewById(R.id.submit_btn);


        Bundle bundle = this.getArguments();
        q_id = bundle.get("q_id").toString();
//        q_text = bundle.get("q_text").toString();

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

        load_question();

        //speech to text mic_button on click listner
        mic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mic_image.setImageResource(R.drawable.micon);
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(i, RESULT_SPEECH);
                    //response.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast t = Toast.makeText(getActivity(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();

                }
                mic_image.setImageResource(R.drawable.mic);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                submit_call();

            }
        });


    }

    private void load_question() {
        Call<QuestionDetail> call = api.getOTTJson(api_key, "Token " + pref.getString("token", null), q_id);

        if (!((Activity) getActivity()).isFinishing()) {
            //show dialog
            progressDialog.show();
        }

        call.enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<QuestionDetail> call, Response<QuestionDetail> response) {
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
                        //remove top constraint of mic view button from frame_view bottom to full_que bottom

                        ConstraintLayout constraintLayout = v.findViewById(R.id.constraint_layout);
                        ConstraintSet constraintSet = new ConstraintSet();

                        constraintSet.clone(constraintLayout);

                        constraintSet.connect(mic_image.getId(), constraintSet.TOP, textView.getId(), constraintSet.BOTTOM, 8);

                        constraintSet.applyTo(constraintLayout);
                    }

                    //inti the data obj
                    data = response.body().getData();

                    MAX_SIZE = Integer.parseInt(response.body().getData().getQuestionOptions().getMAXLength());
                    response_text_view.setMaxLines(MAX_SIZE);
                    //question is now load comopletely and user can now type or speak for enter his response in the edittext of response.

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

    private void submit_call() {
        //submit only if response is not null
        if (!"".equals(response_text_view.getText().toString().trim())) {
            response_text = response_text_view.getText().toString();
            Log.d("ott_response", response_text);

            //submit logic here

            JSONArray ja = new JSONArray();

            JSONObject jo = new JSONObject();

            try {
                jo.put("Key", "OTT");
                jo.put("Value", response_text);
                ja.put(jo);
                Log.v("final_json", ja.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (ja.length() > 0) {

                Call<response> call1 = api.getMCQResponseJson(api_key, "Token " + pref.getString("token", null),
                        data.getAttributeID().toString(), data.getQuestionID().toString(),
                        data.getParentID().toString(), ja,
                        "Android", PublicClass.MainParentID.trim());

                if (!((Activity) getActivity()).isFinishing()) {
                    //show dialog
                    progressDialog.show();
                }

                call1.enqueue(new Callback<response>() {
                    @Override
                    public void onResponse(Call<response> call, Response<response> response) {
                        if (response.isSuccessful() && "Success".equals(response.body().getStatus())) {
                            progressDialog.dismiss();

                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());

                                if (jObjError.has("detail")) {
                                    if (jObjError.getString("detail").equals("Invalid Token")) {
                                        update_token_submit();

                                    }
                                }

                                if (jObjError.has("message")) {
                                    Build_alert_dialog(getActivity(), jObjError.getString("message"));
                                }
                            } catch (Exception e) {

                            }
                            //if IsNext = No
                            if ("No".equals(response.body().getIsNext())) {
                                Log.v("test", "from OTT: response.body().getIsNext()" + response.body().getIsNext());
                                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new QuestionsListFragment()).commit();

                            } else {
                                //if IsNext = Yes
                                //there are children question(s)...we got id and and question type from the response.
                                progressDialog.dismiss();
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

                                Log.v("test", "from OTT: redirect to the new fragmnent :" + String.valueOf(response.body().getQuestionType()));

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

        } else {
            Log.d("ott_response", "empty response");
        }
    }

    private void update_token_submit() {
        ApiService api = RetroClient.getApiService();

        //if fcm token is null then do not write in shared pref!
       /* if (PublicClass.FCM_TOKEN != null) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    response_text = text.get(0);
                    response_text_view.setText(response_text);
                    mic_image.setImageResource(R.drawable.mic);
                }
                break;
            }

        }
    }
}









































