package com.rohan.myvoice.Fragments.QuestionTypes_Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;
import com.rohan.myvoice.pojo.survey_question_detail_SCL.QuestionDetail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SCLFragment extends Fragment {
    private static String MEDIA = "false";
    View v;
    private String q_id, q_text, response_text = "";
    private ProgressDialog progressDialog;

    private TextView que, current, total;
    private FrameLayout frameLayout;
    private ImageView imageView;
    private WebView webView;
    private LinearLayout linearLayout;


    private AppCompatSeekBar seekbar;

    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private double min_double, max_double, step_double;
    private int min_int, max_int, step_int;
    private Number min_num, max_num, step_num;
    private int step_count = 1;

    private Button submit_button;

    Boolean DOUBLE_VALUE = false;
    Boolean NEGATIVE_VALUE = false;

    public SCLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_scale, container, false);


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

        que = v.findViewById(R.id.full_que);
        frameLayout = v.findViewById(R.id.frame_view);
        imageView = v.findViewById(R.id.image_view);
        webView = v.findViewById(R.id.web_view);
        submit_button = v.findViewById(R.id.submit_btn);
        linearLayout = v.findViewById(R.id.linear_layout);
        seekbar = v.findViewById(R.id.appCompatSeekBar);

        current = v.findViewById(R.id.current_num);
        total = v.findViewById(R.id.total_num);

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

        super.onActivityCreated(savedInstanceState);
        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api_key = getResources().getString(R.string.APIKEY);

        api = RetroClient.getApiService();

        que.setText(q_text);          //q_text

        Call<QuestionDetail> call = api.getSCLJson(api_key, "Token " + pref.getString("token", null), q_id);

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

                        constraintSet.connect(linearLayout.getId(), constraintSet.TOP, que.getId(), constraintSet.BOTTOM, 64);

                        constraintSet.applyTo(constraintLayout);

                    }
                    current.setText(response.body().getData().getQuestionOptions().getMin());
                    total.setText(response.body().getData().getQuestionOptions().getMax());

                    //if max, min and step value we receive from response contain .(i.e its double type of value) OR contain -(i.e negative value then I use logic of DOUBLE_VALUE true)
                    if (response.body().getData().getQuestionOptions().getMax().contains(".") ||
                            response.body().getData().getQuestionOptions().getMin().contains(".") ||
                            response.body().getData().getQuestionOptions().getStep().contains(".")) {
                        DOUBLE_VALUE = true;
                    }

                    if (response.body().getData().getQuestionOptions().getMax().contains("-") ||
                            response.body().getData().getQuestionOptions().getMin().contains("-") ||
                            response.body().getData().getQuestionOptions().getStep().contains("-")) {
                        NEGATIVE_VALUE = true;
                    }
                    //seekbar.incrementProgressBy();


                    //if min value is int and all the values are not float (i.e all are int) then...
                    if (!response.body().getData().getQuestionOptions().getMax().contains(".") &&
                            !response.body().getData().getQuestionOptions().getMin().contains(".") &&
                            !response.body().getData().getQuestionOptions().getStep().contains(".") &&
                            "0".equals(response.body().getData().getQuestionOptions().getMin())) {
                        DOUBLE_VALUE = false;
                        NEGATIVE_VALUE = false;

                    }

                    //if DOUBLE
                    if (DOUBLE_VALUE) {

                        max_double = Double.valueOf(response.body().getData().getQuestionOptions().getMax());
                        min_double = Double.valueOf(response.body().getData().getQuestionOptions().getMin());
                        step_double = Double.valueOf(response.body().getData().getQuestionOptions().getStep());

                        double temp = min_double;
                        String temp_string;
                        while (temp <= max_double) {
                            step_count++;
                            temp = temp + step_double;
                            temp_string = String.format("%.2f", temp);
                            temp = Float.valueOf(temp_string);
                        }
                        step_count -= 2;      //because we have started from 0
                        Log.v("seekbar", "step: " + String.valueOf(step_count));
                        seekbar.setMax(step_count);

                    } else {
                        //it's int
                        max_int = Integer.valueOf(response.body().getData().getQuestionOptions().getMax());
                        min_int = Integer.valueOf(response.body().getData().getQuestionOptions().getMin());
                        step_int = Integer.valueOf(response.body().getData().getQuestionOptions().getStep());
                        seekbar.setMax(max_int);
                        seekbar.incrementProgressBy(step_int);
                        seekbar.setProgress(min_int);
                    }
                }
            }

            @Override
            public void onFailure(Call<QuestionDetail> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (DOUBLE_VALUE) {
                    double current_progress = (progress * step_double) + min_double;

                    String current_string = String.format("%.2f", current_progress);

                    Log.v("seekbar", "current (in double) " + current_string);

                    current.setText(current_string);
                } else {
                    //int logic
                    current.setText(String.valueOf(progress));
                    Log.v("seekbar", "current (in int) " + current.getText());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
