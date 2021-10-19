package com.example.fitnessappformyself;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WorkoutSetupTwo extends AppCompatActivity {

    private final ArrayList<String> mondayWorkoutList = new ArrayList<>();
    private final ArrayList<String> tuesdayWorkoutList = new ArrayList<>();
    private final ArrayList<String> wednesdayWorkoutList = new ArrayList<>();
    private final ArrayList<String> thursdayWorkoutList = new ArrayList<>();
    private final ArrayList<String> fridayWorkoutList = new ArrayList<>();
    private final ArrayList<String> saturdayWorkoutList = new ArrayList<>();
    private final ArrayList<String> sundayWorkoutList = new ArrayList<>();

    private Boolean changeOccurred = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_workout_setup_two);
        fillAllLists();
        showDeleteButton();
        hideDeleteButton();
    }
    ////HELPER METHODS////
    public Set<String> convertListToSet(ArrayList<String> aList){
        return  new HashSet<>(aList);
    }
    ////DELETE METHODS START////
    public void deleteListByTag(@NonNull View v){
        if (v.getTag().toString().contains("monday"))
            mondayWorkoutList.clear();
        else if (v.getTag().toString().contains("tuesday"))
            tuesdayWorkoutList.clear();
        else if (v.getTag().toString().contains("wednesday"))
            wednesdayWorkoutList.clear();
        else if (v.getTag().toString().contains("thursday"))
            thursdayWorkoutList.clear();
        else if (v.getTag().toString().contains("friday"))
            fridayWorkoutList.clear();
        else if (v.getTag().toString().contains("saturday"))
            saturdayWorkoutList.clear();
        else if (v.getTag().toString().contains("sunday"))
            sundayWorkoutList.clear();
        Toast.makeText(this, returnBelongingDay(v, "deleted"), Toast.LENGTH_SHORT).show();
        changeOccurred = true;
        hideButton(v);
    }
    public void showDeleteButton(){
        if(!mondayWorkoutList.isEmpty())
            findViewById(R.id.mondayGarbage).setVisibility(View.VISIBLE);
        if(!tuesdayWorkoutList.isEmpty())
            findViewById(R.id.tuesdayGarbage).setVisibility(View.VISIBLE);
        if(!wednesdayWorkoutList.isEmpty())
            findViewById(R.id.wednesdayGarbage).setVisibility(View.VISIBLE);
        if(!thursdayWorkoutList.isEmpty())
            findViewById(R.id.thursdayGarbage).setVisibility(View.VISIBLE);
        if(!fridayWorkoutList.isEmpty())
            findViewById(R.id.fridayGarbage).setVisibility(View.VISIBLE);
        if(!saturdayWorkoutList.isEmpty())
            findViewById(R.id.saturdayGarbage).setVisibility(View.VISIBLE);
        if(!sundayWorkoutList.isEmpty())
            findViewById(R.id.sundayGarbage).setVisibility(View.VISIBLE);
    }
    public void hideDeleteButton(){
        if(mondayWorkoutList.isEmpty())
            findViewById(R.id.mondayGarbage).setVisibility(View.GONE);
        if(tuesdayWorkoutList.isEmpty())
            findViewById(R.id.tuesdayGarbage).setVisibility(View.GONE);
        if(wednesdayWorkoutList.isEmpty())
            findViewById(R.id.wednesdayGarbage).setVisibility(View.GONE);
        if(thursdayWorkoutList.isEmpty())
            findViewById(R.id.thursdayGarbage).setVisibility(View.GONE);
        if(fridayWorkoutList.isEmpty())
            findViewById(R.id.fridayGarbage).setVisibility(View.GONE);
        if(saturdayWorkoutList.isEmpty())
            findViewById(R.id.saturdayGarbage).setVisibility(View.GONE);
        if(sundayWorkoutList.isEmpty())
            findViewById(R.id.sundayGarbage).setVisibility(View.GONE);
    }
    ////DELETE METHODS END////
    ////RESET METHODS START////
    public void onClickResetSelection(View v){
        resetSelection(v, R.id.mondayReset, R.id.mondayLayout);
        resetSelection(v, R.id.tuesdayReset, R.id.tuesdayLayout);
        resetSelection(v, R.id.wednesdayReset, R.id.wednesdayLayout);
        resetSelection(v, R.id.thursdayReset, R.id.thursdayLayout);
        resetSelection(v, R.id.fridayReset, R.id.fridayLayout);
        resetSelection(v, R.id.saturdayReset, R.id.saturdayLayout);
        resetSelection(v, R.id.sundayReset, R.id.sundayLayout);
        resetListByTag(v);
        onClickHideResetButton();
        changeOccurred = false;
    }
    public void onClickHideResetButton(){
        if(mondayWorkoutList.isEmpty())
            findViewById(R.id.mondayReset).setVisibility(View.GONE);
        if(tuesdayWorkoutList.isEmpty())
            findViewById(R.id.tuesdayReset).setVisibility(View.GONE);
        if(wednesdayWorkoutList.isEmpty())
            findViewById(R.id.wednesdayReset).setVisibility(View.GONE);
        if(thursdayWorkoutList.isEmpty())
            findViewById(R.id.thursdayReset).setVisibility(View.GONE);
        if(fridayWorkoutList.isEmpty())
            findViewById(R.id.fridayReset).setVisibility(View.GONE);
        if(saturdayWorkoutList.isEmpty())
            findViewById(R.id.saturdayReset).setVisibility(View.GONE);
        if(sundayWorkoutList.isEmpty())
            findViewById(R.id.sundayReset).setVisibility(View.GONE);
    }
    public void onClickEnableResetButton(){
        if(!mondayWorkoutList.isEmpty())
            findViewById(R.id.mondayReset).setVisibility(View.VISIBLE);
        if(!tuesdayWorkoutList.isEmpty())
            findViewById(R.id.tuesdayReset).setVisibility(View.VISIBLE);
        if(!wednesdayWorkoutList.isEmpty())
            findViewById(R.id.wednesdayReset).setVisibility(View.VISIBLE);
        if(!thursdayWorkoutList.isEmpty())
            findViewById(R.id.thursdayReset).setVisibility(View.VISIBLE);
        if(!fridayWorkoutList.isEmpty())
            findViewById(R.id.fridayReset).setVisibility(View.VISIBLE);
        if(!saturdayWorkoutList.isEmpty())
            findViewById(R.id.saturdayReset).setVisibility(View.VISIBLE);
        if(!sundayWorkoutList.isEmpty())
            findViewById(R.id.sundayReset).setVisibility(View.VISIBLE);
    }
    public void resetSelection(@NonNull View v, @IdRes int buttonId, @IdRes int layoutId){
        if(v.getId() == buttonId){
            ConstraintLayout layout = findViewById(layoutId);
            for(int i=0; i<layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(true);
            }
        }
    }
    public void resetListByTag(@NonNull View v){
        if(changeOccurred) {
            if (v.getTag().toString().contains("monday"))
                mondayWorkoutList.clear();
            else if (v.getTag().toString().contains("tuesday"))
                tuesdayWorkoutList.clear();
            else if (v.getTag().toString().contains("wednesday"))
                wednesdayWorkoutList.clear();
            else if (v.getTag().toString().contains("thursday"))
                thursdayWorkoutList.clear();
            else if (v.getTag().toString().contains("friday"))
                fridayWorkoutList.clear();
            else if (v.getTag().toString().contains("saturday"))
                saturdayWorkoutList.clear();
            else if (v.getTag().toString().contains("sunday"))
                sundayWorkoutList.clear();
            Toast.makeText(this, returnBelongingDay(v, "reset"), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "You haven't selected anything, there is nothing to reset.", Toast.LENGTH_SHORT).show();
    }
    ////RESET METHODS END////
    ////SAVE METHODS START////
    @SuppressLint("ApplySharedPref")
    public void onClickSave(View v){
        if(changeOccurred) {
            saveDayInfoToCalendar("Monday", mondayWorkoutList);
            saveDayInfoToCalendar("Tuesday", tuesdayWorkoutList);
            saveDayInfoToCalendar("Wednesday", wednesdayWorkoutList);
            saveDayInfoToCalendar("Thursday", thursdayWorkoutList);
            saveDayInfoToCalendar("Friday", fridayWorkoutList);
            saveDayInfoToCalendar("Saturday", saturdayWorkoutList);
            saveDayInfoToCalendar("Sunday", sundayWorkoutList);
            Toast.makeText(this, "Changes has been saved.", Toast.LENGTH_SHORT).show();
            changeOccurred = false;
            showDeleteButton();
        }
        else
            Toast.makeText(this, "You haven't made any changes, there is nothing to save.", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("ApplySharedPref")
    public void saveDayInfoToCalendar(String day, @NonNull ArrayList<String> aList){
        SharedPreferences programPref = this.getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
        SharedPreferences.Editor workoutEditor = programPref.edit();
        Set<String> holderSet = convertListToSet(aList);
        //save selected movements
        int i=0;
        for (String s : holderSet) {
            workoutEditor.putString("exercise" + i, s);
            i++;
        }
        workoutEditor.putInt("buttonCount",holderSet.size());
        workoutEditor.commit();
    }
    ////LIST METHODS START////
    public ArrayList<String> selectListFromDay(@NonNull String day){
        switch (day) {
            case "Monday":
                return mondayWorkoutList;
            case "Tuesday":
                return tuesdayWorkoutList;
            case "Wednesday":
                return wednesdayWorkoutList;
            case "Thursday":
                return thursdayWorkoutList;
            case "Friday":
                return fridayWorkoutList;
            case "Saturday":
                return saturdayWorkoutList;
            case "Sunday":
                return sundayWorkoutList;
        }
        return null;
    }
    public void fillAllLists(){
        onCreateFillDayLists("Monday");
        onCreateFillDayLists("Tuesday");
        onCreateFillDayLists("Wednesday");
        onCreateFillDayLists("Thursday");
        onCreateFillDayLists("Friday");
        onCreateFillDayLists("Saturday");
        onCreateFillDayLists("Sunday");
    }
    public void onCreateFillDayLists(String day){
        SharedPreferences dayPref = this.getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
        for(int i=0; i<dayPref.getInt("buttonCount",0); i++){
            if(selectListFromDay(day) != null)
                selectListFromDay(day).add(dayPref.getString("exercise"+i,null));
        }
    }
    ////LIST METHODS END////
    ////HELPER METHODS START////
    public String returnBelongingDay(@NonNull View v, String resetOrDelete){
        if(v.getTag().toString().contains("monday"))
            return "Monday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("tuesday"))
            return "Tuesday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("wednesday"))
            return "Wednesday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("thursday"))
            return "Thursday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("friday"))
            return "Friday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("saturday"))
            return "Saturday workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("sunday"))
            return "Sunday workout list has been " + resetOrDelete + ".";
        else
            return "";
    }
    public void determineListByTag(@NonNull View v){
        if(v.getTag().toString().contains("monday"))
            mondayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("tuesday"))
            tuesdayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("wednesday"))
            wednesdayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("thursday"))
            thursdayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("friday"))
            fridayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("saturday"))
            saturdayWorkoutList.add(((MaterialButton) v).getText().toString());
        else if(v.getTag().toString().contains("sunday"))
            sundayWorkoutList.add(((MaterialButton) v).getText().toString());
    }
    public void hideButton(@NonNull View v){
        v.setVisibility(View.GONE);
    }
    ////HELPER METHODS END////
    ////ON CLICK METHODS////
    public void onClickSetDisabledAndAddToList(View v){
        determineListByTag(v);
        findViewById(v.getId()).setEnabled(false);
        changeOccurred = true;
        onClickEnableResetButton();
    }
    public void onClickReturnHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}