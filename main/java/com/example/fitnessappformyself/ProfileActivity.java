package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity {

    private final Person dummyPerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_profile);
        setOnClickListeners();
    }

    //On Click Methods Start
    public void setOnClickListeners(){
        findViewById(R.id.next_button).setOnClickListener(v -> onNextButtonClicked());
        findViewById(R.id.back_button).setOnClickListener(v -> onGoBackButtonClicked());
        findViewById(R.id.avatarOne).setOnClickListener(this::onClickSaveAvatarAndDisableOthers);
        findViewById(R.id.avatarTwo).setOnClickListener(this::onClickSaveAvatarAndDisableOthers);
        findViewById(R.id.avatarThree).setOnClickListener(this::onClickSaveAvatarAndDisableOthers);
        findViewById(R.id.avatarSelectionReset).setOnClickListener(v -> onClickResetAvatarSelection());
    }

    public void onClickResetAvatarSelection(){
        findViewById(R.id.avatarOne).setEnabled(true);
        findViewById(R.id.avatarTwo).setEnabled(true);
        findViewById(R.id.avatarThree).setEnabled(true);

        //disable next button
        //as the avatar selection is now reset
        findViewById(R.id.next_button).setEnabled(false);

        //set avatar field to empty
        dummyPerson.setAvatar("");
    }

    public void onClickSaveAvatarAndDisableOthers(@NonNull View v){
        if(v.getId() == R.id.avatarOne){
            dummyPerson.setAvatar("avatar_one");
            findViewById(R.id.avatarTwo).setEnabled(false);
            findViewById(R.id.avatarThree).setEnabled(false);
        }
        else if(v.getId() == R.id.avatarTwo){
            dummyPerson.setAvatar("avatar_two");
            findViewById(R.id.avatarThree).setEnabled(false);
            findViewById(R.id.avatarOne).setEnabled(false);
        }
        else if(v.getId() == R.id.avatarThree){
            dummyPerson.setAvatar("avatar_three");
            findViewById(R.id.avatarTwo).setEnabled(false);
            findViewById(R.id.avatarOne).setEnabled(false);
        }
        findViewById(R.id.next_button).setEnabled(true);
    }

    public void onGoBackButtonClicked(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
    public void onNextButtonClicked(){
        try{
            //set name
            if (!(((TextView) findViewById(R.id.profName)).getText().toString().isEmpty()))
                dummyPerson.setName(((TextView) findViewById(R.id.profName)).getText().toString());
            else {
                Toast.makeText(this, "Name field cannot be empty and cannot contain special characters", Toast.LENGTH_LONG).show();
                return;
            }
            //set age
            if (!(((((TextView) findViewById(R.id.age)).getText().toString())).isEmpty()))
                dummyPerson.setAge(Integer.parseInt(((TextView) findViewById(R.id.age)).getText().toString()));
            else {
                Toast.makeText(this, "Age field cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set weight
            if (!(((((TextView) findViewById(R.id.weight)).getText().toString())).isEmpty()))
                dummyPerson.setWeight(Integer.parseInt(((TextView) findViewById(R.id.weight)).getText().toString()));
            else {
                Toast.makeText(this, "Weight field cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set height
            if (!(((((TextView) findViewById(R.id.height)).getText().toString())).isEmpty()))
                dummyPerson.setHeight(Integer.parseInt(((TextView) findViewById(R.id.height)).getText().toString()));
            else {
                Toast.makeText(this, "Height field cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set gender
            if (!(((((TextView) findViewById(R.id.gender)).getText().toString())).isEmpty()))
                dummyPerson.setGender(((TextView) findViewById(R.id.gender)).getText().toString());
            else
                Toast.makeText(this, "Gender field cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
            calculateBMR();

            saveProfilePreferences(dummyPerson, "1");
        } catch (NumberFormatException e){
            Toast.makeText(this, "Age, height and weight cannot contain letters.", Toast.LENGTH_LONG).show();
        }
    }
    //On Click Methods End

    public void saveProfilePreferences(@NonNull Person person, String str){
        String tempString = "Profiles" + str;
        SharedPreferences preference = getSharedPreferences(tempString, MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString("name"+str,person.getName());
        editor.putInt("age"+str,person.getAge());
        editor.putString("weight"+str,Double.toString(person.getWeight()));
        editor.putString("height"+str,Double.toString(person.getHeight()));
        editor.putString("gender"+str,person.getGender());
        editor.putString("BMR"+str,formatDecimalPlaces(person.getBMR()));
        editor.putString("avatarLink", person.getAvatar());
        editor.apply();
    }
    public String formatDecimalPlaces(double d){
        DecimalFormat df = new DecimalFormat("#.##");//this will be used to round the double value
        df.setRoundingMode(RoundingMode.CEILING);// with more than 2 decimal places to 2 decimal places
        return df.format(d);
    }
    public void calculateBMR(){
        try {
            dummyPerson.calculateBMR(Integer.parseInt(((TextView) findViewById(R.id.weight)).getText().toString()),
                    Integer.parseInt(((TextView) findViewById(R.id.height)).getText().toString()),
                    Integer.parseInt(((TextView) findViewById(R.id.age)).getText().toString()),
                    ((TextView) findViewById(R.id.gender)).getText().toString()
            );
            launchProfileSetupNextWithIntent(dummyPerson);
        } catch (NumberFormatException e){
            Toast.makeText(this, "Height and age cannot contain letters.", Toast.LENGTH_LONG).show();
        }
    }
    public void launchProfileSetupNextWithIntent(Person person){
        Intent i = new Intent(this, create_profile_next.class);
        i.putExtra("profile", person);
        startActivity(i);
        finish();
    }
}