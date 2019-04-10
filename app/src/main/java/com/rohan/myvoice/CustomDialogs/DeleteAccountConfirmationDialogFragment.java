package com.rohan.myvoice.CustomDialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.rohan.myvoice.MainActivity;
import com.rohan.myvoice.R;

public class DeleteAccountConfirmationDialogFragment extends DialogFragment {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public DeleteAccountConfirmationDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        View view = inflater.inflate(R.layout.custom_dialog_delete_account_confirmation, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }


        Button dialogButton_OK = (Button) view.findViewById(R.id.ok_btn);


        dialogButton_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                editor.clear();
                editor.commit();
                startActivity(new Intent(getActivity(), MainActivity.class));

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        Window window = getDialog().getWindow();
        window.setLayout(800, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

    }
}
