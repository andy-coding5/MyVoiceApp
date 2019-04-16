package com.e2excel.myvoice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView t_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("MYVOICEAPP_PREF", MODE_PRIVATE);
        SharedPreferences pref2 = getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE);
        ;


        // startActivity(new Intent(this, preference.class));
        //If user is already logged in then send him to getstarted activity and user has filled
        //all the detils already then from getStarted,send him to the main Dashboard activity
        //Boolean temp = pref.getBoolean("isUserLoggedIn", false);

       /* Map<String, ?> allEntries = MyFirebaseMessagingService.preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }*/

        String IsComplete = pref.getString("IsComplete", "false");
        String IsVerified = pref.getString("isVerified", "false");

        if (pref.getBoolean("isUserLoggedIn", false)) {

            if (IsComplete.equals("false")) {
                startActivity(new Intent(this, GetStarted.class));      //for filling the details

            } else {
                if (IsVerified.equals("true")) {
                    startActivity(new Intent(this, Dashboard.class));       //redirecting to main dashboard
                } else {
                    startActivity(new Intent(this, Verification.class));       //redirecting to Verification
                }

            }
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

        String t = t_view.getText().toString();

        ClickableSpan c = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.dark_blue));
                ds.setUnderlineText(false);
            }
        };

        SpannableString s = new SpannableString(t);
        s.setSpan(c, 15, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        t_view.setText(s);


        Log.v("fcm_token", "Fcm_token: in main activity " + pref2.getString("fcm_token", "null or empty"));
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


    //define alert box , message is in argument of calling function ... code reusability -- by RV

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

    //without the title
    public static void Build_alert_dialog(final Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}



































