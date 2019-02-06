package com.rohan.myvoice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
import static android.Manifest.permission.RECORD_AUDIO;


public class permission_screen extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private static final int RECORD_PERMISSION_CODE = 122;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_screen);

        //getting coustom layout for toolbar-
        //have to change the menifest file too - change theme of this activity to cutomeTheme
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
    }

    public void allow_record_audio(View view) {
        if (is_recording_allowed()) {
            Toast.makeText(this, "You already have the permission of recording Audio", Toast.LENGTH_SHORT).show();
            return;

        }
        request_recording_permission();
    }

    private boolean is_recording_allowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void request_recording_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        /*if (requestCode == NOTIFICATION_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you see Notofications.", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
        */

        if (requestCode == RECORD_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted. now app can record audio.", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission for recording the audio", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void set_my_pref(View view) {
        String devide_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        startActivity(new Intent(this, Dashboard.class));
    }
}
