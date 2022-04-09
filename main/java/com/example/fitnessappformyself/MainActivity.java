package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
        disableCreateProfileButtonActivateProfilesButton();
    }
    public void setFragments(){
        ViewPager viewPager = findViewById(R.id.mainViewPager);

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);

        CalendarFragment calendarFragment = new CalendarFragment();
        WorkoutFragment workoutFragment = new WorkoutFragment();
        SuggestionsFragment suggestionsFragment = new SuggestionsFragment();
        ReadMeFragment readMeFragment = new ReadMeFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(calendarFragment, "CALENDAR");
        viewPagerAdapter.addFragment(suggestionsFragment, "SUGGESTIONS");
        viewPagerAdapter.addFragment(workoutFragment, "MOVES");
        viewPagerAdapter.addFragment(readMeFragment, "READ ME");
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_calendar_today_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_message_24);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_sports_handball_24);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_baseline_info_24);
    }
    public void disableCreateProfileButtonActivateProfilesButton(){
        SharedPreferences pref = this.getSharedPreferences("DoesProfileExist", Context.MODE_PRIVATE);
        if(pref.getString("exists",null).equals("yes")) {
            FloatingActionButton floatButton = findViewById(R.id.createProfile);
            floatButton.setImageResource(R.drawable.ic_baseline_profile_24);
        }
    }
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
    public void getParcelableIntent(){
        Intent i = getIntent();
        profilePerson = i.getParcelableExtra("profile");
    }
    public void saveProfiles(){
        //if there is a new profile, save it in an available profile slot
        if(profilePerson != null) {
            SharedPreferences prefOne = getSharedPreferences("Profiles1", MODE_PRIVATE);
            //if profile1 is empty, save current profile to profile1
            if (prefOne.getString("name1", null) == null)
                saveProfilePreferences(profilePerson, "1");
        }
    }
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
    //ON CLICK METHODS
    public void onClickLaunchProfileSetupOrProfiles(View v){
        Intent i = new Intent(this, ProfileActivity.class);
        Intent j = new Intent(this, ProfActivity.class);
        //create profiles activity
        SharedPreferences preference = getSharedPreferences("DoesProfileExist", MODE_PRIVATE);
        if(!preference.getString("exists",null).equals("yes"))
            startActivity(i);
        else
            startActivity(j);
    }
    ////CALENDAR FRAGMENT////
    //collective methods for on click listeners

    public void onClickViewInDetail(View v){
        Intent i = new Intent(this, WorkoutOfTheDayActivity.class);
        startActivity(i);
    }
    //on click functionality methods end
    ////SUGGESTION FRAGMENT////
    public void onClickPrepareForSuggestion(View v){
        ConstraintLayout suggestionLayout = findViewById(R.id.suggestionSelectionLayout);
        suggestionLayout.setVisibility(View.GONE);
        onClickSuggest(v);
    }
    public void onClickSuggest(@NonNull View v){
        SharedPreferences pref = this.getSharedPreferences("Profiles1",MODE_PRIVATE);

        final ConstraintLayout suggestionLayout = findViewById(R.id.suggestionLayout);
        suggestionLayout.setVisibility(View.VISIBLE);

        if(v.getId() == R.id.goalOptionOne){
            String toLoseWeightOne = "Based on your daily calorie consumption, which is: " + pref.getString("calories1",null) + " calories, one way to lose weight is" +
                    " increasing your activity level you can do this by going to the gym more often, being more active throughout the day or you can increase the intensity of your cardio.";
            String toLoseWeightTwo = "Another way to lose weight would be to eat less than the amount of calories you burn in a day, which is: " + pref.getString("calories1",null);
            ((TextView) findViewById(R.id.suggestionOne)).setText(toLoseWeightOne);
            ((TextView) findViewById(R.id.suggestionTwo)).setText(toLoseWeightTwo);
            ((TextView) findViewById(R.id.suggestionThree)).setText(R.string.toLoseWeightThree);
        }
        else if(v.getId() == R.id.goalOptionTwo){
            String toMaintainWeightOne = "Based on the amount of calories you burn in a day, which is: " + pref.getString("calories1",null) + " calories, to maintain your body weight"
                    + " and your muscle mass you should consume the same amount of calories you burn in a day.";
            String toMaintainWeightTwo = "If you want to eat more in a day or want to eat calorie dense foods (snacks), you need to be more active (burn more calories)." +
                    " You can achieve this by going to the gym more often, increasing the intensity of your cardio or by being more active throughout the day";
            findViewById(R.id.suggestionThree).setVisibility(View.GONE);
            //
            ((TextView) findViewById(R.id.suggestionOne)).setText(toMaintainWeightOne);
            ((TextView) findViewById(R.id.suggestionTwo)).setText(toMaintainWeightTwo);
            //
            findViewById(R.id.arrowDownTwo).setVisibility(View.GONE);
        }
        else if(v.getId() == R.id.goalOptionThree){
            String toGainWeightOne = "Based on the amount of calories you burn in a day, which is: " + pref.getString("calories1",null) + " calories, to gain weight you need to" +
                    " consume more calories than you are burning or you can be less active but doing so will have a negative impact on your overall health, thus this is not advised.";
            String toGainWeightTwo = "You need to be careful not to eat too much because to gain weight you only need to be on a slight surplus. Meaning that if you are burning " +
                    pref.getString("calories1",null) + ", you need to eat " + pref.getString("calories1",null) + " +1 calories to gain weight. Of course consuming " +
                    "just +1 calorie will result in a too long of a weight gain process, your surplus should be higher than that. But be careful to not over do it you do not want to" +
                    " become obese or overweight.";
            findViewById(R.id.suggestionThree).setVisibility(View.GONE);
            //
            ((TextView) findViewById(R.id.suggestionOne)).setText(toGainWeightOne);
            ((TextView) findViewById(R.id.suggestionTwo)).setText(toGainWeightTwo);
            //
            findViewById(R.id.arrowDownTwo).setVisibility(View.GONE);
        }
    }
    public void onClickResetSuggestion(View v){
        ConstraintLayout suggestionLayout = findViewById(R.id.suggestionLayout);
        suggestionLayout.setVisibility(View.GONE);

        suggestionLayout = findViewById(R.id.suggestionSelectionLayout);
        suggestionLayout.setVisibility(View.VISIBLE);
    }
    ////UNUSED////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}