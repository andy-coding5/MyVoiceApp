package com.rohan.myvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class personal_info_2 extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_2);
        //getting coustom layout for toolbar-
        //have to change the menifest file too - change theme of this activity to cutomeTheme
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dots_custom_action_bar);
        View view = getSupportActionBar().getCustomView();

        ImageView imageView = view.findViewById(R.id.dots_image_1);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.dot_2));
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.action_bar_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        Toast.makeText(this, i.getStringExtra("country_name"), Toast.LENGTH_SHORT).show();

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

    }

    public void income_selection(View view) {

    }

    public void qualification_selection(View view) {
    }

    public void gender_selection(View view) {
    }

    public void dob_selection(View view) {
    }

    public void next_activity(View view) {
    }
}
