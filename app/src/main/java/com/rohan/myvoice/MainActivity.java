package com.rohan.myvoice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        //If user is already logged in then send him to getstarted activity and user has filled
        //all the detils already then from getStarted,send him to the main Dashboard activity
        Boolean temp = pref.getBoolean("isUserLoggedIn", false);
        if (pref.getBoolean("isUserLoggedIn", false)) {
            startActivity(new Intent(this, GetStarted.class));
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //getting coustom layout for toolbar- but we don't need it here in main(FIRST) activity
        /*getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_layout);
        View view = getSupportActionBar().getCustomView();

        ImageButton imageButton= (ImageButton)view.findViewById(R.id.action_bar_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Overridef
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


    //this is SIGN UP button on click method
    public void sign_up(View view) {

        Intent i = new Intent(getApplicationContext(), SignUp.class);
        startActivity(i);


    }

    //this is SIGN IN button on click method
    public void sign_in(View view) {
        Intent i = new Intent(getApplicationContext(), SignIn.class);
        startActivity(i);
    }


    //define alert box , message is in argument of calling func tion ... code reusability -- by RV

    public static void Build_alert_dialog(final Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }


}



































