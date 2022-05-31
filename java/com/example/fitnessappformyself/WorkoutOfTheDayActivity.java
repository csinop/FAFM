package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WorkoutOfTheDayActivity extends AppCompatActivity {

    private final ArrayList<String> holdBodyParts = new ArrayList<>();
    private boolean timerRunning;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliSeconds = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_of_the_day);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWorkoutOfTheDayOnCreate();
        fillTitleTextViews();
    }
    //TIMER METHODS START//
    public void countDownButtonOnClick(View v){
        startStop();
    }
    public void resetTimerOnClick(View v){
        resetTimer();
    }
    public void startStop(){
        if(timerRunning)
            stopTimer();
        else
            startTimer();
    }
    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeftInMilliSeconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliSeconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                ((Button) findViewById(R.id.countdownButton)).setText(getResources().getString(R.string.start_timer));
                findViewById(R.id.countdownButton).setVisibility(View.INVISIBLE);
                findViewById(R.id.countdownResetButton).setVisibility(View.VISIBLE);
            }
        }.start();
        ((Button) findViewById(R.id.countdownButton)).setText(getResources().getString(R.string.pause_timer));
        findViewById(R.id.countdownResetButton).setVisibility(View.INVISIBLE);
        timerRunning = true;
    }
    public void stopTimer(){
        countDownTimer.cancel();
        ((Button) findViewById(R.id.countdownButton)).setText(getResources().getString(R.string.start_timer));
        timerRunning = false;
        findViewById(R.id.countdownResetButton).setVisibility(View.VISIBLE);
    }
    public void updateTimer(){
        int minutes = (int) timeLeftInMilliSeconds / 60000;
        int seconds = (int) timeLeftInMilliSeconds % 60000 / 1000;

        String timeLeftText = String.format(Locale.getDefault(), "%02d:%02d",minutes,seconds);

        ((TextView) findViewById(R.id.countdownText)).setText(timeLeftText);
    }
    public void resetTimer(){
        timeLeftInMilliSeconds = 120000;
        findViewById(R.id.countdownResetButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.countdownButton).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.countdownText)).setText(getResources().getString(R.string.countdown_text));
    }
    //TIMER METHODS END//

    @SuppressLint("SetTextI18n")
    public void setMoveNames(String bodyPart){
        SharedPreferences workoutPref = this.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        TableLayout tbl = findViewById(R.id.workoutOfTheDayTableLayout);
        for(int i=0; i<workoutPref.getInt("move_count",0); i++) {
            if(workoutPref.getString("exercise"+i,null) != null) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView tv1 = new TextView(this);
                TextView tv2 = new TextView(this);
                TextView tv3 = new TextView(this);
                tv1.setText(workoutPref.getString("exercise"+i,null));
                tv2.setText("\t\tSets:\t" + workoutPref.getString("set" + i, null));
                tv3.setText("\t\tReps:\t" + workoutPref.getString("rep" + i, null));
                row.addView(tv1);
                row.addView(tv2);
                row.addView(tv3);
                tbl.addView(row,i);
            }
        }
        TableRow tbr = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(bodyPart);
        tv0.setTextSize(24);
        tbr.addView(tv0);
        tbl.addView(tbr,0);
    }
    public void fillTitleTextViews(){
        if(!holdBodyParts.isEmpty()) {
            setMoveNames(holdBodyParts.get(0));
            if (holdBodyParts.size() > 1)
                setMoveNames(holdBodyParts.get(1));
            if (holdBodyParts.size() > 2)
                setMoveNames(holdBodyParts.get(2));
            if (holdBodyParts.size() > 3)
                setMoveNames(holdBodyParts.get(3));
            if (holdBodyParts.size() > 4)
                setMoveNames(holdBodyParts.get(4));
            if (holdBodyParts.size() > 5)
                setMoveNames(holdBodyParts.get(5));
            if (holdBodyParts.size() > 6)
                setMoveNames(holdBodyParts.get(6));
        }
    }
    public String adjustForLanguageDifferences(@NonNull String day){
        switch (day) {
            case "Pazartesi":
                return "Monday";
            case "Salı":
                return "Tuesday";
            case "Çarşamba":
                return "Wednesday";
            case "Perşembe":
                return "Thursday";
            case "Cuma":
                return "Friday";
            case "Cumartesi":
                return "Saturday";
            case "Pazar":
                return "Sunday";
            default:
                return day;
        }
    }
    public void getWorkoutOfTheDayOnCreate(){
        SharedPreferences pref = this.getSharedPreferences(getToday()+"WorkoutPlan", Context.MODE_PRIVATE);
        for(int i=0; i<pref.getInt("move_count",0); i++){
            if(pref.getString("exercise"+i,null) != null){
                holdBodyParts.add(pref.getString("exercise"+i,null));
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    public String getToday(){
        Date now = new Date();
        return (adjustForLanguageDifferences(new SimpleDateFormat("EEEE").format(now)));
    }
}