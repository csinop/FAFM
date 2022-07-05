package com.example.fitnessappformyself.main_menu_fragments.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fitnessappformyself.exception_handler.ExceptionHandler;
import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class create_profile_next extends AppCompatActivity {
    private final PersonHandler personHandler = new PersonHandler();
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_create_profile_next);
        setOnClickListeners();

        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

        /* initialize shared preferences */
        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (masterKey != null) {
            try {
                preference = EncryptedSharedPreferences.create(
                        this,
                        preferenceKey,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    //On Click Methods Start
    public void setOnClickListeners(){
        findViewById(R.id.saveProfileButton).setOnClickListener(v -> onClickSavePersonInfo());
        findViewById(R.id.often1).setOnClickListener(this::onClickDisableAllAndSaveActivityLevel);
        findViewById(R.id.often2).setOnClickListener(this::onClickDisableAllAndSaveActivityLevel);
        findViewById(R.id.often3).setOnClickListener(this::onClickDisableAllAndSaveActivityLevel);
        findViewById(R.id.often4).setOnClickListener(this::onClickDisableAllAndSaveActivityLevel);
        findViewById(R.id.often5).setOnClickListener(this::onClickDisableAllAndSaveActivityLevel);
        findViewById(R.id.resetActivitySelectionButton).setOnClickListener(v -> onClickResetSelection());
    }

    public void onClickSavePersonInfo(){ //send all input to MainActivity (Home page)
        if( !(findViewById(R.id.often1).isEnabled() && findViewById(R.id.often2).isEnabled() && findViewById(R.id.often3).isEnabled() && findViewById(R.id.often4).isEnabled() && findViewById(R.id.often5).isEnabled()) ){
            onClickSaveAndLaunch();
        }
        else Toast.makeText(this, getResources().getString(R.string.none_selected), Toast.LENGTH_SHORT).show();
    }

    public void onClickResetSelection(){
        findViewById(R.id.often1).setEnabled(true);
        findViewById(R.id.often2).setEnabled(true);
        findViewById(R.id.often3).setEnabled(true);
        findViewById(R.id.often4).setEnabled(true);
        findViewById(R.id.often5).setEnabled(true);
    }

    public void onClickDisableAllAndSaveActivityLevel(@NonNull View v){
        String profileKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        SharedPreferences preference = null;
        if (masterKey != null) {
            try {
                preference = EncryptedSharedPreferences.create(
                        this,
                        profileKey,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }

        findViewById(R.id.often1).setEnabled(false);
        findViewById(R.id.often2).setEnabled(false);
        findViewById(R.id.often3).setEnabled(false);
        findViewById(R.id.often4).setEnabled(false);
        findViewById(R.id.often5).setEnabled(false);

        if(preference != null) {
            if (v.getId() == R.id.often1)
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_ACTIVITY, "1.2");
            else if (v.getId() == R.id.often2)
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_ACTIVITY, "1.375");
            else if (v.getId() == R.id.often3)
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_ACTIVITY, "1.55");
            else if (v.getId() == R.id.often4)
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_ACTIVITY, "1.725");
            else if (v.getId() == R.id.often5)
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_ACTIVITY, "1.9");
            else return;
        }

        SharedPreferences pref = getSharedPreferences("DoesProfileExist", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pref.edit();

        editor2.putString("exists","yes");
        editor2.apply();
    }

    public void calculateBMR(){
        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (masterKey != null) {
            try {
                preference = EncryptedSharedPreferences.create(
                        this,
                        preferenceKey,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        if(preference != null) {
            SharedPreferences.Editor editor = preference.edit();
            double weight = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, null));
            double height = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null));
            int age = Integer.parseInt(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE, null));
            String gender = preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null);

            String result = personHandler.calculateBMR_Approximate(weight, height, age, gender); /* round up to 2 decimal places */

            editor.putString(StaticProfilePreferenceStrings.PROFILE_BMR, result);
            editor.apply();
        }
    }

    public void calculateCaloriesBurned(){
        if(preference != null) {
            SharedPreferences.Editor editor = preference.edit();

            double activityLevel = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null));
            double BMR = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR, null).replace(",", "."));

            String result = personHandler.calculateCaloriesBurned_Approximate(BMR, activityLevel); /* round up to 2 decimal places */

            editor.putString(StaticProfilePreferenceStrings.PROFILE_CALORIES, result);
            editor.apply();
        }
    }

    //save all personal information into SharedPreferences
    public void onClickSaveAndLaunch(){
        calculateBMR();
        calculateCaloriesBurned();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);// return to Home screen
        finish();
    }
}