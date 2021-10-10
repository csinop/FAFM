package com.example.fitnessappformyself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.InputMismatchException;

public class ProfileActivity extends AppCompatActivity {

    private Person dummyPerson = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_profile);
    }
    public void saveGenericInfo(View v){
        try{
            //set name
            if (!(((TextView) findViewById(R.id.name)).getText().toString().isEmpty()))
                dummyPerson.setName(((TextView) findViewById(R.id.name)).getText().toString());
            else {
                Toast.makeText(this, "Name cannot be empty and cannot contain special characters", Toast.LENGTH_LONG).show();
                return;
            }
            //set age
            if (!(((((TextView) findViewById(R.id.age)).getText().toString())).isEmpty()))
                dummyPerson.setAge(Integer.parseInt(((TextView) findViewById(R.id.age)).getText().toString()));
            else {
                Toast.makeText(this, "Age cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set weight
            if (!(((((TextView) findViewById(R.id.weight)).getText().toString())).isEmpty()))
                dummyPerson.setWeight(Integer.parseInt(((TextView) findViewById(R.id.weight)).getText().toString()));
            else {
                Toast.makeText(this, "Weight cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set height
            if (!(((((TextView) findViewById(R.id.height)).getText().toString())).isEmpty()))
                dummyPerson.setHeight(Integer.parseInt(((TextView) findViewById(R.id.height)).getText().toString()));
            else {
                Toast.makeText(this, "Height cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
                return;
            }
            //set gender
            if (!(((((TextView) findViewById(R.id.gender)).getText().toString())).isEmpty()))
                dummyPerson.setGender(((TextView) findViewById(R.id.gender)).getText().toString());
            else
                Toast.makeText(this, "Gender cannot be empty, 0 or contain letters", Toast.LENGTH_LONG).show();
            calculateBMR();
        } catch (NumberFormatException e){
            Toast.makeText(this, "Age, height and weight cannot contain letters.", Toast.LENGTH_LONG).show();
        }
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
    public void onClickSaveAvatar(View v){
        SharedPreferences avatarPreference = this.getSharedPreferences("Avatars", Context.MODE_PRIVATE);
        SharedPreferences.Editor avatarEditor = avatarPreference.edit();
        if(v.getId() == R.id.avatarOne){
            avatarEditor.putString("avatarLink","avatar_one");
            avatarEditor.commit();
        }
        else if(v.getId() == R.id.avatarTwo){
            avatarEditor.putString("avatarLink","avatar_two");
            avatarEditor.commit();
        }
        else if(v.getId() == R.id.avatarThree){
            avatarEditor.putString("avatarLink","avatar_three");
            avatarEditor.commit();
        }
        else
            Toast.makeText(this, "Bad Developer!", Toast.LENGTH_LONG).show();
        findViewById(R.id.next_button).setEnabled(true);
    }
    public void launchProfileSetupNextWithIntent(Person person){
        Intent i = new Intent(this, create_profile_next.class);
        i.putExtra(StaticStrings.PROFILE, person);
        startActivity(i);
        finish();
    }
}