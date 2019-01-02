package com.rohan.myvoice;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView t_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting coustom layout for toolbar- but we don't need it here in main(FIRST) activity
        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */

        t_view = (TextView) findViewById(R.id.textView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            t_view.setText(Html.fromHtml(" <pre>\n" +
                    "       Welcome to the <span style=\"color:#004671\">MyVoice App</span>\n" +
                    "            The app that lets you have\n" +
                    "            your voice heard on a wide\n" +
                    "            range of topics.\n" +
                    "   </pre>", Html.FROM_HTML_MODE_COMPACT));
        } else {
            t_view.setText(Html.fromHtml(" <pre>\n" +
                    "       Welcome to the <span style=\"color:#004671\">MyVoice App</span>\n" +
                    "            The app that lets you have\n" +
                    "            your voice heard on a wide\n" +
                    "            range of topics.\n" +
                    "   </pre>"));
        }


    }

    public void sign_in(View view) {
    }
}
