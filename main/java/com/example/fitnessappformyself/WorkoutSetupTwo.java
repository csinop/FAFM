package com.example.fitnessappformyself;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class WorkoutSetupTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_workout_setup_two);
    }
    public void resetSelection(View v, @IdRes int buttonId, @IdRes int layoutId){
        if(v.getId() == buttonId){
            ConstraintLayout layout = findViewById(layoutId);
            for(int i=0; i<layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                child.setEnabled(true);
            }
        }
    }
    public void deleteDayOnIdMatch(String day, @IdRes int resourceId, View v){
        SharedPreferences deletePref;
        SharedPreferences.Editor editor;
        if(v.getId() == resourceId) {
            deletePref = this.getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
            editor = deletePref.edit();
            editor.clear();
            editor.commit();
        }
    }
    public void saveDayInfoToCalendar(String day, @IdRes int resourceId, View v){
        SharedPreferences programPref = this.getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
        ConstraintLayout layout = findViewById(resourceId);
        SharedPreferences.Editor workoutEditor = programPref.edit();
        //save selected chest movements
        for(int i=0; i<layout.getChildCount(); i++) {
            MaterialButton child = (MaterialButton) layout.getChildAt(i);
            //Log.d("information","b");
            if(!child.isEnabled())
                workoutEditor.putString("exercise"+i, child.getText().toString());
        }
        workoutEditor.putInt("buttonCount",layout.getChildCount());
        workoutEditor.commit();
    }
    ////ON CLICK METHODS////
    @SuppressLint("ApplySharedPref")
    public void onClickSaveAndProceed(View v){
        saveDayInfoToCalendar("Monday",R.id.mondayLayout,v);
        saveDayInfoToCalendar("Tuesday",R.id.tuesdayLayout,v);
        saveDayInfoToCalendar("Wednesday",R.id.wednesdayLayout,v);
        saveDayInfoToCalendar("Thursday",R.id.thursdayLayout,v);
        saveDayInfoToCalendar("Friday",R.id.fridayLayout,v);
        saveDayInfoToCalendar("Saturday",R.id.saturdayLayout,v);
        saveDayInfoToCalendar("Sunday",R.id.sundayLayout,v);
        onClickLaunchReturnHome(v);
    }
    public void onClickResetSelection(View v){
        resetSelection(v, R.id.mondayReset, R.id.mondayLayout);
        resetSelection(v, R.id.tuesdayReset, R.id.tuesdayLayout);
        resetSelection(v, R.id.wednesdayReset, R.id.wednesdayLayout);
        resetSelection(v, R.id.thursdayReset, R.id.thursdayLayout);
        resetSelection(v, R.id.fridayReset, R.id.fridayLayout);
        resetSelection(v, R.id.saturdayReset, R.id.saturdayLayout);
        resetSelection(v, R.id.sundayReset, R.id.sundayLayout);
    }
    public void onClickDeleteDayAndResetSelection(View v){
        deleteDayOnIdMatch("Monday",R.id.mondayGarbage,v);
        deleteDayOnIdMatch("Tuesday",R.id.tuesdayGarbage,v);
        deleteDayOnIdMatch("Wednesday",R.id.wednesdayGarbage,v);
        deleteDayOnIdMatch("Thursday",R.id.thursdayGarbage,v);
        deleteDayOnIdMatch("Friday",R.id.fridayGarbage,v);
        deleteDayOnIdMatch("Saturday",R.id.saturdayGarbage,v);
        deleteDayOnIdMatch("Sunday",R.id.sundayGarbage,v);
    }
    public void onClickSetDisabled(View v){ findViewById(v.getId()).setEnabled(false); }
    public void onClickLaunchReturnHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}