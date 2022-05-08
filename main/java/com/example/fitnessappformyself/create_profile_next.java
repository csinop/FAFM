package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class create_profile_next extends AppCompatActivity {

    private Person anotherDummyPerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_create_profile_next);
        getParcelableIntent();
        setOnClickListeners();
    }

    //On Click Methods Start
    public void setOnClickListeners(){
        findViewById(R.id.saveProfileButton).setOnClickListener(v -> onClickSavePersonInfo());
        findViewById(R.id.often1).setOnClickListener(this::onClickDisableAll);
        findViewById(R.id.often2).setOnClickListener(this::onClickDisableAll);
        findViewById(R.id.often3).setOnClickListener(this::onClickDisableAll);
        findViewById(R.id.often4).setOnClickListener(this::onClickDisableAll);
        findViewById(R.id.often5).setOnClickListener(this::onClickDisableAll);
        findViewById(R.id.resetActivitySelectionButton).setOnClickListener(v -> onClickResetSelection());
    }

    public void onClickSavePersonInfo(){ //send all input to MainActivity (Home page)
        if( !(findViewById(R.id.often1).isEnabled() && findViewById(R.id.often2).isEnabled() && findViewById(R.id.often3).isEnabled() && findViewById(R.id.often4).isEnabled() && findViewById(R.id.often5).isEnabled()) ){
            onClickSaveAndLaunch();
        }
        else Toast.makeText(this, "Select one of the above first", Toast.LENGTH_SHORT).show();
    }

    public void onClickResetSelection(){
        findViewById(R.id.often1).setEnabled(true);
        findViewById(R.id.often2).setEnabled(true);
        findViewById(R.id.often3).setEnabled(true);
        findViewById(R.id.often4).setEnabled(true);
        findViewById(R.id.often5).setEnabled(true);
    }

    public void onClickDisableAll(@NonNull View v){
        findViewById(R.id.often1).setEnabled(false);
        findViewById(R.id.often2).setEnabled(false);
        findViewById(R.id.often3).setEnabled(false);
        findViewById(R.id.often4).setEnabled(false);
        findViewById(R.id.often5).setEnabled(false);

        if(v.getId() == R.id.often1) anotherDummyPerson.setActivityLevel(1.2);
        else if(v.getId() == R.id.often2) anotherDummyPerson.setActivityLevel(1.375);
        else if(v.getId() == R.id.often3) anotherDummyPerson.setActivityLevel(1.55);
        else if(v.getId() == R.id.often4) anotherDummyPerson.setActivityLevel(1.725);
        else if(v.getId() == R.id.often5) anotherDummyPerson.setActivityLevel(1.9);
        else return;

        if((anotherDummyPerson.getActivityLevel() != 0) && (anotherDummyPerson.getBMR() != 0)) {
            anotherDummyPerson.setCaloriesBurned(anotherDummyPerson.getActivityLevel() * anotherDummyPerson.getBMR());
        }
    }

    //save all personal information into SharedPreferences
    public void onClickSaveAndLaunch(){
        Intent i = new Intent(this, MainActivity.class);

        SharedPreferences preference = getSharedPreferences("Profiles1", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preference.edit();

        editor1.putString("calories1",Double.toString(anotherDummyPerson.getCaloriesBurned()));
        editor1.putString("activity1",Double.toString(anotherDummyPerson.getActivityLevel()));

        SharedPreferences pref = getSharedPreferences("DoesProfileExist",MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref.edit();

        editor2.putString("exists","yes");

        editor2.apply();
        editor1.apply();
        startActivity(i);// return to Home screen
        finish();
    }
    //On Click Methods End



    //get Person object from the previous activity
    public void getParcelableIntent(){
        Intent i = getIntent();
        anotherDummyPerson = i.getParcelableExtra("profile");
    }
}