package com.rohan.myvoice.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohan.myvoice.R;
import com.rohan.myvoice.Retrofit.ApiService;
import com.rohan.myvoice.Retrofit.RetroClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {
    View v;
    ApiService api;
    String api_key;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Set up progress before call
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeToRefresh);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.dark_blue);

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        api = RetroClient.getApiService();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("Notifications");

        ImageView back = toolbar.findViewById(R.id.back_image);
        back.setVisibility(View.INVISIBLE);

        api_key = getResources().getString(R.string.APIKEY);

    }
}
