package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Person profilePerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        setFragments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getParcelableIntent();
        saveProfiles();
        checkIfProfileExists();
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

    //to be removed
    @SuppressLint("ApplySharedPref")
    public void checkIfProfileExists(){
        SharedPreferences preference = getSharedPreferences("DoesProfileExist", MODE_PRIVATE);
        SharedPreferences pref = getSharedPreferences("Profiles1", MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        if(pref.contains("name1"))
            editor.putString("exists", "yes");
        else
            editor.putString("exists", "no");
        editor.commit();
    }

    //to be removed
    public void getParcelableIntent(){
        Intent i = getIntent();
        profilePerson = i.getParcelableExtra("profile");
    }

    //to be removed
    public void saveProfiles(){
        //if there is a new profile, save it in an available profile slot
        if(profilePerson != null) {
            SharedPreferences prefOne = getSharedPreferences("Profiles1", MODE_PRIVATE);
            //if profile1 is empty, save current profile to profile1
            if (prefOne.getString("name1", null) == null)
                saveProfilePreferences(profilePerson, "1");
        }
    }

    //to be removed
    @SuppressLint("ApplySharedPref")
    public void saveProfilePreferences(@NonNull Person person, String str){
        String tempString = "Profiles" + str;
        Log.d("information",tempString);//test
        SharedPreferences preference = getSharedPreferences(tempString, MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("name"+str,person.getName());
        editor.putInt("age"+str,person.getAge());
        editor.putString("weight"+str,Double.toString(person.getWeight()));
        editor.putString("height"+str,Double.toString(person.getHeight()));
        editor.putString("gender"+str,person.getGender());
        editor.putString("BMR"+str,formatDecimalPlaces(person.getBMR()));
        editor.putString("activity"+str,Double.toString(person.getActivityLevel()));
        editor.putString("calories"+str,formatDecimalPlaces(person.getCaloriesBurned()));
        editor.commit();
    }

    public String formatDecimalPlaces(double d){
        DecimalFormat df = new DecimalFormat("#.##");//this will be used to round the double value
        df.setRoundingMode(RoundingMode.CEILING);// with more than 2 decimal places to 2 decimal places
        return df.format(d);
    }

    //on click functionality methods end
    ////UNUSED////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}