package com.example.fitnessappformyself;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        setFragments();

            //to be changed
        //direct the user to profile creation activity
        if(!profileExists()){
            launchCreateProfile();
        }
    }

    @SuppressLint("ApplySharedPref")
    public boolean profileExists(){
        SharedPreferences pref = getSharedPreferences("DoesProfileExist", Context.MODE_PRIVATE);
        if (pref.getString("exists", null) != null) {
            return pref.getString("exists", null).equals("yes");
        }
        return false;
    }

    public void launchCreateProfile(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void setFragments(){
        ViewPager viewPager = findViewById(R.id.mainViewPager);

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);

        CalendarFragment calendarFragment = new CalendarFragment();
        WorkoutFragment workoutFragment = new WorkoutFragment();
        ReadMeFragment readMeFragment = new ReadMeFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(calendarFragment, "CALENDAR");
        viewPagerAdapter.addFragment(workoutFragment, "MOVES");
        viewPagerAdapter.addFragment(profileFragment, "PROFILE");
        viewPagerAdapter.addFragment(readMeFragment, "READ ME");
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_calendar_today_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_sports_handball_24);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_profile_24);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_baseline_info_24);
    }

    //on click functionality methods end
    ////UNUSED////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}