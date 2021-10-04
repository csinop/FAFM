package com.example.fitnessappformyself;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.mainViewPager);

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);

        CalendarFragment calendarFragment = new CalendarFragment();
        WorkoutFragment workoutFragment = new WorkoutFragment();
        SuggestionsFragment suggestionsFragment = new SuggestionsFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(calendarFragment, "Calendar");
        viewPagerAdapter.addFragment(suggestionsFragment, "Suggestions");
        viewPagerAdapter.addFragment(workoutFragment, "Workout Set");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_outline_calendar_today_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_outline_message_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_outline_sports_handball_24);
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
        profilePerson = i.getParcelableExtra(StaticStrings.PROFILE);
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
    public void saveProfilePreferences(Person person, String str){
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
    public void onClickLaunchCalendarSetupAndResetCalendar(View v){
        Intent i = new Intent(this, WorkoutSetupTwo.class);
        startActivity(i);
    }
    public void onClickViewInDetail(View v){
        Intent i = new Intent(this, WorkoutOfTheDayActivity.class);
        startActivity(i);
    }
    ////SUGGESTION FRAGMENT////
    public void onClickPrepareForSuggestion(View v){
        ConstraintLayout suggestionLayout = (ConstraintLayout) findViewById(R.id.suggestionSelectionLayout);
        suggestionLayout.setVisibility(View.GONE);
        onClickSuggest(v);
    }
    public void onClickSuggest(View v){
        SharedPreferences pref = this.getSharedPreferences("Profiles1",MODE_PRIVATE);

        final ConstraintLayout suggestionLayout = (ConstraintLayout) findViewById(R.id.suggestionLayout);
        suggestionLayout.setVisibility(View.VISIBLE);

        if(v.getId() == R.id.goalOptionOne){
            String toLoseWeightOne = "Based on the amount of calories you burn in a day, which is: " + pref.getString("calories1",null) + " calories, one way to lose weight is" +
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
            ((TextView) findViewById(R.id.suggestionThree)).setVisibility(View.GONE);
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
            ((TextView) findViewById(R.id.suggestionThree)).setVisibility(View.GONE);
            //
            ((TextView) findViewById(R.id.suggestionOne)).setText(toGainWeightOne);
            ((TextView) findViewById(R.id.suggestionTwo)).setText(toGainWeightTwo);
            //
            findViewById(R.id.arrowDownTwo).setVisibility(View.GONE);
        }
    }
    public void onClickResetSuggestion(View v){
        ConstraintLayout suggestionLayout = (ConstraintLayout) findViewById(R.id.suggestionLayout);
        suggestionLayout.setVisibility(View.GONE);

        suggestionLayout = (ConstraintLayout) findViewById(R.id.suggestionSelectionLayout);
        suggestionLayout.setVisibility(View.VISIBLE);
    }
    /////WORKOUT FRAGMENT/////
    public void onCheckBoxClicked(View v){
        if(v.getId() == R.id.chestCheckBox){
            if(findViewById(R.id.chest_layout).getVisibility() == View.GONE) {
                findViewById(R.id.chest_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.chestGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.chestGarbage).setEnabled(true);
                findViewById(R.id.chest_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.chest_layout).setVisibility(View.GONE);
                findViewById(R.id.chestGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.chestGarbage).setEnabled(false);
                findViewById(R.id.chest_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.bicepsCheckBox){
            if(findViewById(R.id.biceps_layout).getVisibility() == View.GONE) {
                findViewById(R.id.biceps_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.bicepsGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.bicepsGarbage).setEnabled(true);
                findViewById(R.id.biceps_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.biceps_layout).setVisibility(View.GONE);
                findViewById(R.id.bicepsGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.bicepsGarbage).setEnabled(false);
                findViewById(R.id.biceps_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.tricepsCheckBox){
            if(findViewById(R.id.triceps_layout).getVisibility() == View.GONE) {
                findViewById(R.id.triceps_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.tricepsGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.tricepsGarbage).setEnabled(true);
                findViewById(R.id.triceps_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.triceps_layout).setVisibility(View.GONE);
                findViewById(R.id.tricepsGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.tricepsGarbage).setEnabled(false);
                findViewById(R.id.triceps_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.backCheckBox){
            if(findViewById(R.id.back_layout).getVisibility() == View.GONE) {
                findViewById(R.id.back_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.backGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.backGarbage).setEnabled(true);
                findViewById(R.id.back_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.back_layout).setVisibility(View.GONE);
                findViewById(R.id.backGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.backGarbage).setEnabled(false);
                findViewById(R.id.back_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.legsCheckBox){
            if(findViewById(R.id.legs_layout).getVisibility() == View.GONE) {
                findViewById(R.id.legs_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.legsGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.legsGarbage).setEnabled(true);
                findViewById(R.id.legs_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.legs_layout).setVisibility(View.GONE);
                findViewById(R.id.legsGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.legsGarbage).setEnabled(false);
                findViewById(R.id.legs_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.shouldersCheckBox){
            if(findViewById(R.id.shoulders_layout).getVisibility() == View.GONE) {
                findViewById(R.id.shoulders_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.shouldersGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.shouldersGarbage).setEnabled(true);
                findViewById(R.id.shoulders_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.shoulders_layout).setVisibility(View.GONE);
                findViewById(R.id.shouldersGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.shouldersGarbage).setEnabled(false);
                findViewById(R.id.shoulders_selection_reset).setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.cardioCheckBox){
            if(findViewById(R.id.cardio_layout).getVisibility() == View.GONE) {
                findViewById(R.id.cardio_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.cardioGarbage).setVisibility(View.VISIBLE);
                findViewById(R.id.cardioGarbage).setEnabled(true);
                findViewById(R.id.cardio_selection_reset).setVisibility(View.VISIBLE);
            }
            else{
                findViewById(R.id.cardio_layout).setVisibility(View.GONE);
                findViewById(R.id.cardioGarbage).setVisibility(View.INVISIBLE);
                findViewById(R.id.cardioGarbage).setEnabled(false);
                findViewById(R.id.cardio_selection_reset).setVisibility(View.GONE);
            }
        }
    }

    public void resetSelection(View v, @IdRes int buttonId, @IdRes int layoutId){
        if(v.getId() == buttonId){
            RelativeLayout layout = findViewById(layoutId);
            for(int i=0; i<layout.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) layout.getChildAt(i);
                if(checkBox.isChecked())
                    checkBox.setChecked(false);
            }
        }
    }
    public void saveWorkoutSet(String bodyPart, @IdRes int layoutId){
        SharedPreferences workoutPref = this.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        String workoutString = "exercise";
        RelativeLayout layout = findViewById(layoutId);
        SharedPreferences.Editor workoutEditor = workoutPref.edit();
        for(int i=0; i<layout.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) layout.getChildAt(i);
            if(checkBox.isChecked())
                workoutEditor.putString(workoutString + i, checkBox.getText().toString());
        }
        workoutEditor.putInt("buttonCount",layout.getChildCount());
        workoutEditor.commit();
    }
    public void deleteWorkoutOnIdMatch(String bodyPart, @IdRes int resourceId, View v){
        SharedPreferences deletePref;
        SharedPreferences.Editor editor;
        if(v.getId() == resourceId) {
            deletePref = this.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
            editor = deletePref.edit();
            editor.clear();
            editor.commit();
        }
    }
    ////ON CLICK METHODS////
    public void onClickSaveAndProceed(View v){
        saveWorkoutSet("CHEST", R.id.chest_layout);
        saveWorkoutSet("LEGS", R.id.legs_layout);
        saveWorkoutSet("SHOULDERS", R.id.shoulders_layout);
        saveWorkoutSet("BACK", R.id.back_layout);
        saveWorkoutSet("BICEPS", R.id.biceps_layout);
        saveWorkoutSet("TRICEPS", R.id.triceps_layout);
        saveWorkoutSet("CARDIO", R.id.cardio_layout);
        onClickSaveAndReturnHome(v);
        finish();//end the activity
    }
    public void onClickSaveAndReturnHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void onClickSetDisabled(View v){ findViewById(v.getId()).setEnabled(false); }
    public void onClickResetSelection(View v) {
        resetSelection(v, R.id.chest_selection_reset, R.id.chest_layout);
        resetSelection(v, R.id.biceps_selection_reset, R.id.biceps_layout);
        resetSelection(v, R.id.legs_selection_reset, R.id.legs_layout);
        resetSelection(v, R.id.shoulders_selection_reset, R.id.shoulders_layout);
        resetSelection(v, R.id.back_selection_reset, R.id.back_layout);
        resetSelection(v, R.id.triceps_selection_reset, R.id.triceps_layout);
        resetSelection(v, R.id.cardio_selection_reset, R.id.cardio_layout);
    }
    public void onClickDeleteWorkoutSet(View v){
        deleteWorkoutOnIdMatch("CHEST",R.id.chestGarbage,v);
        deleteWorkoutOnIdMatch("BICEPS",R.id.bicepsGarbage,v);
        deleteWorkoutOnIdMatch("TRICEPS",R.id.tricepsGarbage,v);
        deleteWorkoutOnIdMatch("BACK",R.id.backGarbage,v);
        deleteWorkoutOnIdMatch("LEGS",R.id.legsGarbage,v);
        deleteWorkoutOnIdMatch("SHOULDERS",R.id.shouldersGarbage,v);
        deleteWorkoutOnIdMatch("CARDIO", R.id.cardioGarbage, v);
    }
    ////UNUSED////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}