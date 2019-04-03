package com.rohan.myvoice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.rohan.myvoice.Fragments.ActivityFragment;
import com.rohan.myvoice.Fragments.HomeFragment;
import com.rohan.myvoice.Fragments.NotificationFragment;
import com.rohan.myvoice.Fragments.QuestionsListFragment;
import com.rohan.myvoice.Fragments.SettingsFragment;
import com.rohan.myvoice.GlobalValues.PublicClass;

public class Dashboard extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

    /*    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);*/

        //  viewPager = findViewById(R.id.pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        frameLayout = findViewById(R.id.framelayout_container);

       /* viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HomeFragment());
        viewPagerAdapter.addFragments(new ActivityFragment());
        viewPagerAdapter.addFragments(new NotificationFragment());
        viewPagerAdapter.addFragments(new SettingsFragment());

        viewPager.setAdapter(viewPagerAdapter);*/


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.home:
                                selectedFragment = new HomeFragment();
                                break;
                            case R.id.activity:
                                selectedFragment = new ActivityFragment();
                                break;
                            case R.id.notifications:
                                selectedFragment = new NotificationFragment();
                                break;
                            case R.id.settings:
                                selectedFragment = new SettingsFragment();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout_container, selectedFragment);
                        transaction.commit();
                        return true;

                    }
                });

        //bottomNavigationView.setItemIconTintList(null);
        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout_container, new HomeFragment());
        transaction.commit();


        // viewPager.setOnTouchListener(new View.OnTouchListener() {
       /* @Override
        public boolean onTouch (View v, MotionEvent event){
            return true;
        }*/

    }

    @Override
    public void onBackPressed() {


        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (PublicClass.CURRENT_FRAG == 0) {
            super.onBackPressed();
            finishAffinity();       //to completely close the entire application

        } else {
            // it means that either we have pressed the back button from Questionlist or any of the question fragment
            if (PublicClass.CURRENT_FRAG == 1) {      //we have pressed back button from questionlist
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, new HomeFragment()).commit();

            } else {
                //we have presses back button from any of the question fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_container, new QuestionsListFragment()).commit();
            }
        }

    }
}
