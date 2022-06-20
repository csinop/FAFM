package com.example.fitnessappformyself.main_menu_fragments.profile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessappformyself.exception_handler.ExceptionHandler;
import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
        findViewById(R.id.avatarOne).setOnClickListener(v3 -> {
            try {
                onClickSaveAvatarAndDisableOthers(v3);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.avatarTwo).setOnClickListener(v2 -> {
            try {
                onClickSaveAvatarAndDisableOthers(v2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.avatarThree).setOnClickListener(v1 -> {
            try {
                onClickSaveAvatarAndDisableOthers(v1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.avatarSelectionReset).setOnClickListener(v -> onClickResetAvatarSelection());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClickResetAvatarSelection(){
        findViewById(R.id.avatarOne).setEnabled(true);
        findViewById(R.id.avatarTwo).setEnabled(true);
        findViewById(R.id.avatarThree).setEnabled(true);

        //disable next button
        findViewById(R.id.next_button).setEnabled(false);

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

        /* set the avatar string to empty string of the current profile */
        if (preference != null) {
            personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "");
        }
    }

    public void onClickSaveAvatarAndDisableOthers(@NonNull View v) throws IOException {
        String profileKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException e) {
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
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        if (preference != null) {
            if (v.getId() == R.id.avatarOne) {
                /* set the avatar string to empty string of the current profile */
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_one");
                findViewById(R.id.avatarTwo).setEnabled(false);
                findViewById(R.id.avatarThree).setEnabled(false);
            } else if (v.getId() == R.id.avatarTwo) {
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_two");
                findViewById(R.id.avatarThree).setEnabled(false);
                findViewById(R.id.avatarOne).setEnabled(false);
            } else if (v.getId() == R.id.avatarThree) {
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AVATAR, "avatar_three");
                findViewById(R.id.avatarTwo).setEnabled(false);
                findViewById(R.id.avatarOne).setEnabled(false);
            }
            findViewById(R.id.next_button).setEnabled(true);
        }
    }

    public void onGoBackButtonClicked(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onNextButtonClicked(){
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

        if(preference != null) {
            //set name
            if (!(((TextView) findViewById(R.id.profName)).getText().toString().isEmpty())) {
                String profileName = ((TextView) findViewById(R.id.profName)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_NAME, profileName);
            } else {
                Toast.makeText(this, getResources().getString(R.string.invalid_username), Toast.LENGTH_LONG).show();
                return;
            }
            //set age
            if (!(((((TextView) findViewById(R.id.age)).getText().toString())).isEmpty())) {
                String profileAge = ((TextView) findViewById(R.id.age)).getText().toString();
                try {
                    Integer.parseInt(profileAge);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getResources().getString(R.string.invalid_age), Toast.LENGTH_LONG).show();
                    return;
                }
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_AGE, profileAge);
            } else {
                Toast.makeText(this, getResources().getString(R.string.empty_age), Toast.LENGTH_LONG).show();
                return;
            }
            //set weight
            if (!(((((TextView) findViewById(R.id.weight)).getText().toString())).isEmpty())) {
                String profileWeight = ((TextView) findViewById(R.id.weight)).getText().toString();
                try {
                    Double.parseDouble(profileWeight);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getResources().getString(R.string.invalid_weight), Toast.LENGTH_LONG).show();
                    return;
                }
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_WEIGHT, profileWeight);
            } else {
                Toast.makeText(this, getResources().getString(R.string.empty_weight), Toast.LENGTH_LONG).show();
                return;
            }
            //set height
            if (!(((((TextView) findViewById(R.id.height)).getText().toString())).isEmpty())) {
                String profileHeight = ((TextView) findViewById(R.id.height)).getText().toString();
                try {
                    Double.parseDouble(profileHeight);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getResources().getString(R.string.invalid_height), Toast.LENGTH_LONG).show();
                    return;
                }
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_HEIGHT, profileHeight);
            } else {
                Toast.makeText(this, getResources().getString(R.string.empty_height), Toast.LENGTH_LONG).show();
                return;
            }
            //set gender
            if (!(((((TextView) findViewById(R.id.gender)).getText().toString())).isEmpty())) {
                String profileGender = ((TextView) findViewById(R.id.gender)).getText().toString();
                personHandler.saveStringToSharedPreferences(preference, StaticProfilePreferenceStrings.PROFILE_GENDER, profileGender);
            } else
                Toast.makeText(this, getResources().getString(R.string.invalid_gender), Toast.LENGTH_LONG).show();

            launchProfileSetupNextWithIntent();
        }
    }
    //On Click Methods End

    public void launchProfileSetupNextWithIntent(){
        Intent i = new Intent(this, create_profile_next.class);
        startActivity(i);
        finish();
    }
}