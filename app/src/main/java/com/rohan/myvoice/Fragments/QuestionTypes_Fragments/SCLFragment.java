package com.rohan.myvoice.Fragments.QuestionTypes_Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rohan.myvoice.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SCLFragment extends Fragment {


    public SCLFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scale, container, false);
    }

}
