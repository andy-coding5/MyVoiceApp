package com.rohan.myvoice;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

      getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboard_custom_action_bar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();       //to completely close the entire application

        //ViewPager source with Tab activity view; like YouTube Application ..._RV


    }
}
