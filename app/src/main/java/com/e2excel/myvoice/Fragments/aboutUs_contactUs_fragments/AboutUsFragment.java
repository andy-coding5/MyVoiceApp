package com.e2excel.myvoice.Fragments.aboutUs_contactUs_fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.e2excel.myvoice.Fragments.SettingsFragment;
import com.e2excel.myvoice.GlobalValues.PublicClass;
import com.e2excel.myvoice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    View v;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_about_us, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PublicClass.CURRENT_FRAG = 221;     //we set it 221 as profiles fragment also has 221 bcs from both when we press back, we have to ove on settings fragment


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tt = toolbar.findViewById(R.id.title_text);
        tt.setText("About Us");

        ImageView back = toolbar.findViewById(R.id.back_image);

        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.framelayout_container, new SettingsFragment()).commit();
            }
        });

        TextView logout_btn = toolbar.findViewById(R.id.logout_textview);
        logout_btn.setVisibility(View.INVISIBLE);

        TextView t = v.findViewById(R.id.info);
        t.setText("For many years, E2Excel has been focused on bringing clients the \"right\" combination of traditional, online, digital, mobile and social marketing to help their businesses excel and meet the needs of their specific markets. We took our experience and expertise in research and analysis to develop cutting edge technology which pushes the boundaries of what is possible in marketing intelligence. Our mission is to listen to our clients, clearly understanding their goals and objectives while delivering a first-to-market solution that produces real-time insights for charting strategies — keeping them two steps ahead of their competition at all times.");

    }
}
