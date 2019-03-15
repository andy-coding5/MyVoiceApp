package com.rohan.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
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
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.survey_question_detail_OTT.QuestionDetail;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


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

        Call<QuestionDetail> call = api.getOTTJson(api_key, "Token " + pref.getString("token", null), q_id);

        progressDialog.show();

        call.enqueue(new Callback<QuestionDetail>() {
            @Override
            public void onResponse(Call<QuestionDetail> call, Response<QuestionDetail> response) {
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
                    MAX_SIZE = Integer.parseInt(response.body().getData().getQuestionOptions().getMAXLength());
                    response_text_view.setMaxLines(MAX_SIZE);
                    //question is now load comopletely and user can now type or speak for enter his response in the edittext of response.
                }
            }

            @Override
            public void onFailure(Call<QuestionDetail> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

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
            public void onClick(View v) {

                //submit only if response is not null
                if (!"".equals(response_text)) {
                    response_text = response_text_view.getText().toString();
                    Log.d("ott_response", response_text);
                    //submit logic here

                } else {
                    Log.d("ott_response", "empty response");
                }

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









































