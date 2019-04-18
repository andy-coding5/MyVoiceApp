package com.e2excel.myvoice.CustomDialogs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.e2excel.myvoice.MainActivity;
import com.e2excel.myvoice.R;

public class DeleteAccountNotificationErrorDialogFragment extends DialogFragment {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public DeleteAccountNotificationErrorDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        pref = this.getActivity().getSharedPreferences("MYVOICEAPP_PREF", Context.MODE_PRIVATE);
        editor = pref.edit();

        View view = inflater.inflate(R.layout.custom_dialog_delete_account_notification, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
            getDialog().setCancelable(false);
            getDialog().setCanceledOnTouchOutside(false);
        }


        TextView title = view.findViewById(R.id.message_text);
        title.setText("This account has been deleted.");


        Button dialogButton_OK = (Button) view.findViewById(R.id.ok_btn);


        dialogButton_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment f = getFragmentManager().findFragmentByTag("DeleteNotificationDialogFragment");
                if (f != null) {
                    ft.remove(f);
                }
                ft.commit();

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
