package com.rohan.myvoice;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SignUp extends AppCompatActivity {


    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getting coustom layout for toolbar-
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog = new Dialog(this,android.R.style.Theme_NoTitleBar_Fullscreen);

    }

    public void TermsAndConditions(View view) {

        TextView pop_up_text;
        Button agree_btn;

        dialog.setContentView(R.layout.popup_view_layout);

        agree_btn = dialog.findViewById(R.id.agree_button);
        pop_up_text = dialog.findViewById(R.id.pop_up_text);

        pop_up_text.setText(Html.fromHtml("<pre><b>General App Usage</b><br>" +
                "<br>" +
                "Last Revised: May 16, 2018<br>" +
                "<br>" +
                "Welcome to www.lorem-ipsum.info. This site is provided as a service to our visitors and may be used for informational purposes only. Because the Terms and Conditions contain legal obligations, please read them carefully.<br>" +
                "<br>" +
                "<b>1. YOUR AGREEMENT</b><br>"    +
                "<br>" +
                "By using this Site, you agree to be bound by, and to comply with, these Terms and Conditions. If you do not agree to these Terms and Conditions, please do not use this site.<br>" +
                "<br>" +
                "PLEASE NOTE: We reserve the right, at our sole discretion, to change, modify or otherwise alter these Terms and Conditions at any time. Unless otherwise indicated, amendments will become effective immediately. Please review these Terms and Conditions periodically. Your continued use of the Site following the posting of changes and/or modifications will constitute your acceptance of the revised Terms and Conditions and the reasonableness of these standards for notice of changes. For your information, this page was last updated as of the date at the top of these terms and conditions.<br>" +
                "<br>" +
                "<b>2. PRIVACY</b><br>" +
                "<br>" +
                "Please review our Privacy Policy, which also governs your visit to this Site, to understand our practices.<br>" +
                "<br>" +
                "<b>3. LINKED SITES</b><br>" +
                "<br>" +
                "This Site may contain links to other independent third-party Web sites (\"Linked Sites”). These Linked Sites are provided solely as a convenience to our visitors. Such Linked Sites are not under our control, and we are not responsible for and does not endorse the content of such Linked Sites, including any information or materials contained on such Linked Sites. You will need to make your own independent judgment regarding your interaction with these Linked Sites.<br>" +
                "<br>" +
                "<b>4. FORWARD LOOKING STATEMENTS</b><br>" +
                "<br>" +
                "All materials reproduced on this site speak as of the original date of publication or filing. The fact that a document is available on this site does not mean that the information contained in such document has not been modified or superseded by events or by a subsequent document or filing. We have no duty or policy to update any information or statements contained on this site and, therefore, such information or statements should not be relied upon as being current as of the date you access this site.<br></pre>"));
        agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
