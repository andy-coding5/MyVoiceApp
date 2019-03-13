package com.rohan.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.GlobalValues.PublicClass;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.Option;
import com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail;

import org.json.JSONObject;

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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    CheckBox[] cb;

    private ArrayList<String> selected_options;


    public MCQFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                if (getFragmentManager().getBackStackEntryCount() != 0) {
                    getFragmentManager().popBackStack();
                }
            }
        });

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
        q_text = bundle.get("q_text").toString();

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);

        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api_key = getResources().getString(R.string.APIKEY);

        api = RetroClient.getApiService();

        textView.setText(q_text);       //q_text
        selected_options = new ArrayList<>();

        Call<QuestionDetail> call = api.getSCQ_MCQ_RNKJson(api_key, "Token " + pref.getString("token", null), q_id);

        progressDialog.show();

        call.enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail> call, Response<QuestionDetail> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body().getStatus().equals("Sucess")) {
                    if (response.body().getData().getQuestionIsMedia()) {

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
                            webView.loadUrl(response.body().getData().getQuestionVideoMedia());
                        } else if (!"".equals(response.body().getData().getQuestionAudioMedia())) {
                            frameLayout.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.VISIBLE);
                            // Enable Javascript
                            WebSettings webSettings = webView.getSettings();
                            webSettings.setJavaScriptEnabled(true);
                            webView.loadUrl(response.body().getData().getQuestionAudioMedia());
                        }
                    }

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
                    CheckBox checkBox;


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
                        if (jObjError.getString("detail").equals("Invalid Token")) {
                            update_token();
                        }

                    } catch (Exception e) {
                        //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("check_box", selected_options.toString());
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

                for (int i = 0; i < cb.length; i++) {

                    if (cb[i].isChecked()) {
                        selected_options.add(cb[i].getTag().toString());
                    } else {
                        //add the selected options here
                        Typeface fonts = Typeface.createFromAsset(getActivity().getAssets(), "quicksand_regular.ttf");
                        cb[i].setTypeface(fonts);

                    }
                }

            }
        };

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
                    //but but i can access the error body here.
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
