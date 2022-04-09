package com.example.fitnessappformyself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class create_profile_next extends AppCompatActivity {

    private Person anotherDummyPerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_create_profile_next);
        getParcelableIntent();
    }

    public void onClickSavePersonInfo(View v){ //send all input to MainActivity (Home page)
        if( !(findViewById(R.id.often1).isEnabled() && findViewById(R.id.often2).isEnabled() && findViewById(R.id.often3).isEnabled() && findViewById(R.id.often4).isEnabled() && findViewById(R.id.often5).isEnabled()) ){
            onClickSendAndLaunch();
        }
        else Toast.makeText(this, "Select one of the above first", Toast.LENGTH_SHORT).show();
    }
    public void onClickDisableRest(View v){
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
    public void getParcelableIntent(){
        Intent i = getIntent();
        anotherDummyPerson = i.getParcelableExtra("profile");
    }
    public void onClickSendAndLaunch(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("profile", anotherDummyPerson);
        SharedPreferences pref = getSharedPreferences("DoesProfileExist",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(pref.getString("exists",null).equals("no"))
            editor.putString("exists","yes");
        else
            Toast.makeText(this, "You shouldn't have gotten here, Bad developer!",Toast.LENGTH_SHORT).show();
        editor.commit();
        startActivity(i);// return to Home screen
        finish();
    }
}