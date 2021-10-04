package com.example.fitnessappformyself;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkoutOfTheDayActivity extends AppCompatActivity {

    private ArrayList<String> holdBodyParts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_of_the_day);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWorkoutOfTheDayOnCreate();
        fillTextViews();
    }
    public String fillDetailViews(String bodyPart){
        SharedPreferences workoutPref = this.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<workoutPref.getInt("buttonCount",0); i++) {
                if(workoutPref.getString("exercise"+i,null) != null) {
                    builder.append("\n" + workoutPref.getString("exercise"+i, null) + "\n");
                }
        }
        return builder.toString();
    }
    public void fillTextViews(){
        if(!holdBodyParts.isEmpty()) {
            ((TextView) findViewById(R.id.viewWorkoutsTextOne)).setText(holdBodyParts.get(0));
            ((TextView) findViewById(R.id.viewWorkoutsDetailTextOne)).setText(fillDetailViews(holdBodyParts.get(0)));
            if (holdBodyParts.size() > 1) {
                findViewById(R.id.viewWorkoutsLayoutTwo).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextTwo)).setText(holdBodyParts.get(1));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextTwo)).setText(fillDetailViews(holdBodyParts.get(1)));
            }
            if (holdBodyParts.size() > 2) {
                findViewById(R.id.viewWorkoutsLayoutThree).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextThree)).setText(holdBodyParts.get(2));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextThree)).setText(fillDetailViews(holdBodyParts.get(2)));
            }
            if (holdBodyParts.size() > 3) {
                findViewById(R.id.viewWorkoutsLayoutFour).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextFour)).setText(holdBodyParts.get(3));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextFour)).setText(fillDetailViews(holdBodyParts.get(3)));
            }
            if (holdBodyParts.size() > 4) {
                findViewById(R.id.viewWorkoutsLayoutFive).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextFive)).setText(holdBodyParts.get(4));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextFive)).setText(fillDetailViews(holdBodyParts.get(4)));
            }
            if (holdBodyParts.size() > 5) {
                findViewById(R.id.viewWorkoutsLayoutSix).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextSix)).setText(holdBodyParts.get(5));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextSix)).setText(fillDetailViews(holdBodyParts.get(5)));
            }
            if (holdBodyParts.size() > 6) {
                findViewById(R.id.viewWorkoutsLayoutSeven).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.viewWorkoutsTextSeven)).setText(holdBodyParts.get(6));
                ((TextView) findViewById(R.id.viewWorkoutsDetailTextSeven)).setText(fillDetailViews(holdBodyParts.get(6)));
            }
        }
    }
    public void getWorkoutOfTheDayOnCreate(){
        SharedPreferences pref = this.getSharedPreferences(getToday()+"WorkoutPlan", Context.MODE_PRIVATE);
        for(int i=0; i<pref.getInt("buttonCount",0); i++){
            if(pref.getString("exercise"+i,null) != null){
                holdBodyParts.add(pref.getString("exercise"+i,null));
                Log.d("information",pref.getString("exercise"+i,null));//test
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    public String getToday(){
        Date now = new Date();
        return (new SimpleDateFormat("EEEE").format(now));
    }
}