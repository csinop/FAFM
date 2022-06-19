package com.example.fitnessappformyself.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessappformyself.StaticStrings.StaticProfilePreferenceStrings;
import com.example.fitnessappformyself.exception_handler.ExceptionHandler;
import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;

public class ProfileActivity extends AppCompatActivity {
    private final PersonHandler personHandler = PersonHandler.getInstance();

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
        findViewById(R.id.next_button).setEnabled(false);

        String profileKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = getSharedPreferences(profileKey, MODE_PRIVATE);

        /* set the avatar string to empty string of the current profile */
        personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "");
    }

    public void onClickSaveAvatarAndDisableOthers(@NonNull View v){
        String profileKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = getSharedPreferences(profileKey, MODE_PRIVATE);

        if(v.getId() == R.id.avatarOne){
            /* set the avatar string to empty string of the current profile */
            personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_one");
            findViewById(R.id.avatarTwo).setEnabled(false);
            findViewById(R.id.avatarThree).setEnabled(false);
        }
        else if(v.getId() == R.id.avatarTwo){
            personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_two");
            findViewById(R.id.avatarThree).setEnabled(false);
            findViewById(R.id.avatarOne).setEnabled(false);
        }
        else if(v.getId() == R.id.avatarThree){
            personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_three");
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
            String profileKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = getSharedPreferences(profileKey, MODE_PRIVATE);

            //set name
            if (!(((TextView) findViewById(R.id.profName)).getText().toString().isEmpty())) {
                String profileName = ((TextView) findViewById(R.id.profName)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_NAME, profileName);
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.invalid_username), Toast.LENGTH_LONG).show();
                return;
            }
            //set age
            if (!(((((TextView) findViewById(R.id.age)).getText().toString())).isEmpty())) {
                String profileAge = ((TextView) findViewById(R.id.age)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AGE, profileAge);
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.invalid_age), Toast.LENGTH_LONG).show();
                return;
            }
            //set weight
            if (!(((((TextView) findViewById(R.id.weight)).getText().toString())).isEmpty())) {
                String profileWeight = ((TextView) findViewById(R.id.weight)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_WEIGHT, profileWeight);
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.invalid_weight), Toast.LENGTH_LONG).show();
                return;
            }
            //set height
            if (!(((((TextView) findViewById(R.id.height)).getText().toString())).isEmpty())) {
                String profileHeight = ((TextView) findViewById(R.id.height)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_HEIGHT, profileHeight);
            }
            else {
                Toast.makeText(this, getResources().getString(R.string.invalid_height), Toast.LENGTH_LONG).show();
                return;
            }
            //set gender
            if (!(((((TextView) findViewById(R.id.gender)).getText().toString())).isEmpty())) {
                String profileGender = ((TextView) findViewById(R.id.gender)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_GENDER, profileGender);
            }
            else
                Toast.makeText(this, getResources().getString(R.string.invalid_gender), Toast.LENGTH_LONG).show();

            launchProfileSetupNextWithIntent();
        } catch (NumberFormatException e){
            Toast.makeText(this, getResources().getString(R.string.invalid_type_string), Toast.LENGTH_LONG).show();
        }
    }
    //On Click Methods End

    public void launchProfileSetupNextWithIntent(){
        Intent i = new Intent(this, create_profile_next.class);
        startActivity(i);
        finish();
    }
}