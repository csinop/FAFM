package com.example.fitnessappformyself;


import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

public class ProfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_prof);
        initializeFieldsProfileOne();
        setProgressBarForActivityLevel();
        setOnClickListeners();
    }
    @SuppressLint("SetTextI18n")
    public void initializeFieldsProfileOne(){
        SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
        ((TextView) findViewById(R.id.profName)).setText(preferences.getString("name1",null));
        ((TextView) findViewById(R.id.profAge)).setText(Integer.toString(preferences.getInt("age1",0)));
        ((TextView) findViewById(R.id.profWeight)).setText(preferences.getString("weight1",null));
        ((TextView) findViewById(R.id.profHeight)).setText(preferences.getString("height1",null));
        ((TextView) findViewById(R.id.profBMRLast)).setText(preferences.getString("BMR1",null));
        ((TextView) findViewById(R.id.profCaloriesBurnedLast)).setText(preferences.getString("calories1",null));
        ((TextView) findViewById(R.id.profActivityLevelLast)).setText(preferences.getString("activity1",null));
        setProfilePicture();
        setBodyFat();
    }
    public void setBodyFat(){
        SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
        if(preferences.getString("bodyfat1", null) != null){
            findViewById(R.id.bodyFatTag1).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.bodyFatDisplay1)).setText(preferences.getString("bodyfat1", null));
            findViewById(R.id.bodyFatDisplay1).setVisibility(View.VISIBLE);
            findViewById(R.id.bodyFatProgressBarOne).setVisibility(View.VISIBLE);
            findViewById(R.id.bodyFatUnit).setVisibility(View.VISIBLE);
            setProgressBarForBodyFat();
        }
    }
    public void setProfilePicture(){
        SharedPreferences preferences = this.getSharedPreferences("Avatars",Context.MODE_PRIVATE);
        if(preferences.contains("avatarLink")) {
            switch (preferences.getString("avatarLink", null)) {
                case "avatar_one":
                    findViewById(R.id.profileAvatar).setBackgroundResource(R.drawable.bulldog_96px);
                    break;
                case "avatar_two":
                    findViewById(R.id.profileAvatar).setBackgroundResource(R.drawable.chicken_96px);
                    break;
                case "avatar_three":
                    findViewById(R.id.profileAvatar).setBackgroundResource(R.drawable.lion_96px);
                    break;
                default:
                    Toast.makeText(this, "Bad Developer!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setProgressBarForActivityLevel(){
        ProgressBar progressBar = findViewById(R.id.activityProgressBarOne);
        SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
        if(Double.parseDouble(preferences.getString("activity1",null)) == 1.2) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
            progressBar.setProgress(20);
        }
        else if(Double.parseDouble(preferences.getString("activity1",null)) == 1.375) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
            progressBar.setProgress(37);
        }
        else if(Double.parseDouble(preferences.getString("activity1",null)) == 1.55) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
            progressBar.setProgress(55);
        }
        else if(Double.parseDouble(preferences.getString("activity1",null)) == 1.725) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
            progressBar.setProgress(72);
        }
        else if(Double.parseDouble(preferences.getString("activity1",null)) == 1.9) {
            progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
            progressBar.setProgress(90);
        }
        else
            progressBar.setProgress(0);

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setProgressBarForBodyFat(){
        SharedPreferences preference = this.getSharedPreferences("Profiles1", MODE_PRIVATE);
        NumberFormat nf = NumberFormat.getInstance();
        double bodyFat = 0;
        try {
            bodyFat = Objects.requireNonNull(nf.parse(preference.getString("bodyfat1", null))).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //
        ProgressBar progressBar = findViewById(R.id.bodyFatProgressBarOne);
        findViewById(R.id.bodyFatProgressBarOne).setVisibility(View.VISIBLE);

        if(preference.getString("gender1",null).equals("male")){
            if(bodyFat <= 5.0) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                progressBar.setProgress(10);
            }
            else if(bodyFat <= 14.0 && bodyFat > 5.0) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                progressBar.setProgress(25);
            }
            else if(bodyFat <= 18.0 && bodyFat > 14.0) {//athletes and fitness
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                progressBar.setProgress(40);
            }
            else if(bodyFat <= 25.0 && bodyFat > 18.0) {//average
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                progressBar.setProgress(55);
            }
            else if(bodyFat <= 35.0 && bodyFat > 25.0) {//obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                progressBar.setProgress(70);
            }
            else if(bodyFat <= 40.0 && bodyFat > 35.0) {//morbidly obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                progressBar.setProgress(85);
            }
            else if(bodyFat > 40.0) { //super obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                progressBar.setProgress(99);
            }
            else
                progressBar.setProgress(0);
        }
        else if(preference.getString("gender1",null).equals("female")){
            if(bodyFat <= 10.0) {
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                progressBar.setProgress(10);
            }
            else if(bodyFat <= 15.0 && bodyFat > 10.0) {//essential body fat
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                progressBar.setProgress(25);
            }
            else if(bodyFat <= 25.0 && bodyFat > 15.0) {//athletes and fitness
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                progressBar.setProgress(40);
            }
            else if(bodyFat <= 30.0 && bodyFat > 25.0) {//average
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                progressBar.setProgress(55);
            }
            else if(bodyFat <= 40.0 && bodyFat > 30.0) {//obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                progressBar.setProgress(70);
            }
            else if(bodyFat <= 45.0 && bodyFat > 40.0) {//morbidly obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                progressBar.setProgress(85);
            }
            else if(bodyFat > 45.0) {//super obese
                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                progressBar.setProgress(99);
            }
            else
                progressBar.setProgress(0);
        }
    }

    public String formatDecimalPlaces(double d){
        DecimalFormat df = new DecimalFormat("#.##");//this will be used to round the double value
        df.setRoundingMode(RoundingMode.CEILING);// with more than 2 decimal places to 2 decimal places
        return df.format(d);
    }

    ////ON CLICK METHODS////

    //collective method for all on click listener of this activity
    public void setOnClickListeners(){
        setOnClickDeleteProfile();
        setOnClickSaveAndDisplayBodyfat();
        setOnClickReturnHome();
        setOnTextViewClicked();
        setOnClickListenersForSave();
        setOnClickEnableBodyFatCalcs();
    }

    //on click listener for delete profile button
    @SuppressLint("ApplySharedPref")
    public void setOnClickDeleteProfile(){
        findViewById(R.id.deleteProfile).setOnClickListener(v ->{
            SharedPreferences pref = this.getSharedPreferences("DoesProfileExist",Context.MODE_PRIVATE);
            SharedPreferences.Editor decrementEditor = pref.edit();
            //if at least one profile exists
            if(pref.getString("exists",null).equals("yes")){
                SharedPreferences deletePref = this.getSharedPreferences("Profiles1",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = deletePref.edit();
                editor.clear();
                editor.commit();
                //
                decrementEditor.putString("exists","no");
                decrementEditor.commit();
                //return to home screen
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
            else
                Toast.makeText(this, "You shouldn't have gotten here, Bad developer!",Toast.LENGTH_SHORT).show();
        });
    }

    //on click listener for save body fat button
    @SuppressLint({"SetTextI18n", "ApplySharedPref"})
    public void setOnClickSaveAndDisplayBodyfat(){
        findViewById(R.id.saveBodyFatButton).setOnClickListener(v ->{
            try {
                SharedPreferences preference = this.getSharedPreferences("Profiles1", MODE_PRIVATE);
                SharedPreferences.Editor editor = preference.edit();
                editor.putString("waist1", ((EditText) findViewById(R.id.waistInput)).getText().toString());
                editor.putString("neck1", ((EditText) findViewById(R.id.neckInput)).getText().toString());
                if (preference.getString("gender1", null).equals("female"))
                    editor.putString("hip1", ((EditText) findViewById(R.id.hipInput)).getText().toString());
                editor.commit();

                double bodyFat = 0.0;
                final Person profilePerson = new Person();
                if (preference.getString("gender1", null).equals("male"))
                    bodyFat = profilePerson.measureBodyFatPercentageMale(Double.parseDouble(preference.getString("waist1", null)),
                            Double.parseDouble(preference.getString("neck1", null)), Double.parseDouble(preference.getString("height1", null)));

                else if (preference.getString("gender1", null).equals("female"))
                    bodyFat = profilePerson.measureBodyFatPercentageFemale(Double.parseDouble(preference.getString("waist1", null)),
                            Double.parseDouble(preference.getString("neck1", null)), Double.parseDouble(preference.getString("hip1", null)),
                            Double.parseDouble(preference.getString("height1", null)));

                editor.putString("bodyfat1", formatDecimalPlaces(bodyFat));
                editor.commit();

                findViewById(R.id.bodyFatDisplay1).setVisibility(View.VISIBLE);
                findViewById(R.id.bodyFatTag1).setVisibility(View.VISIBLE);
                findViewById(R.id.bodyFatUnit).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.bodyFatDisplay1)).setText(preference.getString("bodyfat1", null));
                //
                onClickReverseVisibility(v);
                setProgressBarForBodyFat();
                setBodyFat();
            } catch (NumberFormatException e){
                Toast.makeText(this, "Body measures must be integers (waist, neck, hip).s", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClickReverseVisibility(View v){
        SharedPreferences preference = this.getSharedPreferences("Profiles1", MODE_PRIVATE);
        final ConstraintLayout bodyFatLayout = findViewById(R.id.bodyFatCalcLayout);
        bodyFatLayout.setVisibility(View.GONE);
        //
        if(preference.getString("gender1",null).equals("female"))
            findViewById(R.id.hipInputLayout).setVisibility(View.GONE);
        //disable the body fat calculation button
        findViewById(R.id.calculateBodyFat).setVisibility(View.VISIBLE);
        findViewById(R.id.calculateBodyFat).setEnabled(true);
    }

    //on click listener for "calculate body fat" button
    public void setOnClickEnableBodyFatCalcs(){
        findViewById(R.id.calculateBodyFat).setOnClickListener(v -> {
            SharedPreferences preference = this.getSharedPreferences("Profiles1", MODE_PRIVATE);
            final ConstraintLayout bodyFatLayout = findViewById(R.id.bodyFatCalcLayout);
            bodyFatLayout.setVisibility(View.VISIBLE);
            //
            if(preference.getString("gender1",null).equals("female"))
                findViewById(R.id.hipInputLayout).setVisibility(View.VISIBLE);
            //disable the body fat calculation button
            findViewById(R.id.calculateBodyFat).setVisibility(View.INVISIBLE);
            findViewById(R.id.calculateBodyFat).setEnabled(false);
        });
    }

    //on click listener for return to main menu button
    public void setOnClickReturnHome(){
        findViewById(R.id.returnButtonProfToHome).setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    //on click listeners for text fields
    public void setOnTextViewClicked(){
        onTextClicked(R.id.profName);
        onTextClicked(R.id.profAge);
        onTextClicked(R.id.profWeight);
        onTextClicked(R.id.profHeight);
    }
    public void onTextClicked(@IdRes int id){
        findViewById(id).setOnClickListener(v -> {
            if(returnViewSwitcherById(v) != null)
                returnViewSwitcherById(v).showNext();
        });
    }

    //on click listeners for edit text save buttons
    @SuppressLint("ApplySharedPref")
    public void setOnClickListenersForSave(){
        onSaveClicked(R.id.save_edit_changes_height);
        onSaveClicked(R.id.save_edit_changes_weight);
        onSaveClicked(R.id.save_edit_changes_age);
        onSaveClicked(R.id.save_edit_changes_name);
    }
    public void onSaveClicked(@IdRes int id){
        findViewById(id).setOnClickListener(v -> {
            if(v.getId() == R.id.save_edit_changes_height){
                SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if(!((EditText) findViewById(R.id.hiddenEditHeight)).getText().toString().equals("")){
                    ((TextView) findViewById(R.id.profHeight)).setText(((EditText) findViewById(R.id.hiddenEditHeight)).getText().toString());
                    editor.putString("height1",((EditText) findViewById(R.id.hiddenEditHeight)).getText().toString());
                }
                editor.commit();
                switchViewSwitchers(v);
            }else if(v.getId() == R.id.save_edit_changes_weight){
                SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if(!((EditText) findViewById(R.id.hiddenEditWeight)).getText().toString().equals("")){
                    ((TextView) findViewById(R.id.profWeight)).setText(((EditText) findViewById(R.id.hiddenEditWeight)).getText().toString());
                    editor.putString("weight1",((EditText) findViewById(R.id.hiddenEditWeight)).getText().toString());
                }
                editor.commit();
                switchViewSwitchers(v);
            }else if(v.getId() == R.id.save_edit_changes_age){
                SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if(!((EditText) findViewById(R.id.hiddenEditAge)).getText().toString().equals("")){
                    ((TextView) findViewById(R.id.profAge)).setText(((EditText) findViewById(R.id.hiddenEditAge)).getText().toString());
                    editor.putInt("age1",Integer.parseInt(((EditText) findViewById(R.id.hiddenEditAge)).getText().toString()));
                }
                editor.commit();
                switchViewSwitchers(v);
            }else if(v.getId() == R.id.save_edit_changes_name){
                SharedPreferences preferences = this.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if(!((EditText) findViewById(R.id.hiddenEditName)).getText().toString().equals("")){
                    ((TextView) findViewById(R.id.profName)).setText(((EditText) findViewById(R.id.hiddenEditName)).getText().toString());
                    editor.putString("name1",((EditText) findViewById(R.id.hiddenEditName)).getText().toString());
                }
                editor.commit();
                switchViewSwitchers(v);
            }
        });
    }
    // ON CLICK METHODS END //

    //HELPER METHODS
    public ViewSwitcher returnViewSwitcherById(@NonNull View v){
        if(v.getId() == R.id.profName) {
            findViewById(R.id.save_edit_changes_name).setVisibility(View.VISIBLE);
            return findViewById(R.id.nameSwitcher);
        }
        else if(v.getId() == R.id.profAge) {
            findViewById(R.id.save_edit_changes_age).setVisibility(View.VISIBLE);
            return findViewById(R.id.ageSwitcher);
        }
        else if(v.getId() == R.id.profWeight) {
            findViewById(R.id.save_edit_changes_weight).setVisibility(View.VISIBLE);
            return findViewById(R.id.weightSwitcher);
        }
        else if(v.getId() == R.id.profHeight) {
            findViewById(R.id.save_edit_changes_height).setVisibility(View.VISIBLE);
            return findViewById(R.id.heightSwitcher);
        }
        else return null;
    }
    public void switchViewSwitchers(@NonNull View v){
        if(v.getId() == R.id.save_edit_changes_name){
            ((ViewSwitcher) findViewById(R.id.nameSwitcher)).showNext();
            findViewById(R.id.save_edit_changes_name).setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_age){
            ((ViewSwitcher) findViewById(R.id.ageSwitcher)).showNext();
            findViewById(R.id.save_edit_changes_age).setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_weight){
            ((ViewSwitcher) findViewById(R.id.weightSwitcher)).showNext();
            findViewById(R.id.save_edit_changes_weight).setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_height){
            ((ViewSwitcher) findViewById(R.id.heightSwitcher)).showNext();
            findViewById(R.id.save_edit_changes_height).setVisibility(View.GONE);
        }
    }

}