package com.rohan.myvoice;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {
    private int[] navIcons;
    private String[] navLabels;
    // another resouces array for active state for the icon
    private int[] navIconsActive;

    private ViewPager viewPager;
    private TabLayout navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

       /* getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.dashboard_custom_action_bar);*/

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        viewPager = findViewById(R.id.pager);


        //navigation = findViewById(R.id.tabs);
       // navigation.setupWithViewPager(viewPager);

       // navIcons = new int[]{R.drawable.icon_home, R.drawable.icon_activity, R.drawable.icon_profile, R.drawable.icon_settings};
       // navLabels = new String[]{"Home", "Activity", "Profile", "Settings"};
        // another resouces array for active state for the icon
       // navIconsActive = new int[]{R.drawable.icon_active_home, R.drawable.icon_active_activity, R.drawable.icon_active_profile, R.drawable.icon_active_settings};

    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();       //to completely close the entire application

        //ViewPager source with Tab activity view; like YouTube Application ..._RV


    }
}
