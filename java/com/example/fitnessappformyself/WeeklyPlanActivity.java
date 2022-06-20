package com.example.fitnessappformyself;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessappformyself.main_menu_fragments.StaticWorkoutPreferenceStrings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WeeklyPlanActivity extends AppCompatActivity {
    private SharedPreferences dayPref;
    private final ArrayList<Button> currentWorkoutList = new ArrayList<>();
    private String currentDay;
    private Boolean changeOccurred = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_plan_acitivity);

        onStartGetExtras();

        dayPref  = this.getSharedPreferences(currentDay+StaticWorkoutPreferenceStrings.WORKOUT_PLAN, Context.MODE_PRIVATE);

        onCreateInitializeButtonTexts();
        fillWorkoutList();
        showDeleteButton();
        hideDeleteButton();
        disableAlreadySelected(currentWorkoutList);
        setOnClickListeners();
    }

    public void onStartGetExtras(){
        if(getIntent().hasExtra(StaticWorkoutPreferenceStrings.WEEKDAY)){
            currentDay = getIntent().getStringExtra(StaticWorkoutPreferenceStrings.WEEKDAY);
            TextView textView = findViewById(R.id.weekdayTitle);
            textView.setText(currentDay);
        }
    }

    public void onCreateInitializeButtonTexts(){
        ConstraintLayout weeklyPlanLayout = findViewById(R.id.weeklyPlanLayout);
        String[] tempArray = getResources().getStringArray(R.array.bodyParts);
        for(int i = 0; i < weeklyPlanLayout.getChildCount(); i++){
            Button tempButton = (Button) weeklyPlanLayout.getChildAt(i);
            tempButton.setText(tempArray[i]);
        }
    }

    //this methods gets the info from a shared preference file
    public void fillWorkoutList(){
        for(int i=0; i<dayPref.getInt(StaticWorkoutPreferenceStrings.MOVE_COUNT,0); i++){
            if(currentWorkoutList != null){
                //this loop goes through the children of the layout
                ConstraintLayout currentLayout = findViewById(R.id.weeklyPlanLayout);
                for(int j = 0; j < currentLayout.getChildCount(); j++){
                    Button currentChild = (Button) currentLayout.getChildAt(j);
                    if(currentChild.getText().equals(dayPref.getString(StaticWorkoutPreferenceStrings.EXERCISE+i,null))){
                        currentWorkoutList.add(currentChild);
                    }
                }
            }
        }
    }

    //ON CLICK METHODS START
    //helper methods
    public HashSet<Button> convertListToSet(ArrayList<Button> aList){
        return  new HashSet<>(aList);
    }

    //this needs to be called before list.clear()
    //otherwise it won't do anything
    public void resetSelections(){
        for(int i=0; i < currentWorkoutList.size(); i++) {
            currentWorkoutList.get(i).setEnabled(true);
        }
    }
    //collective function
    //so that all onclick methods are under the same methods
    //to prevent polluting the onCreate method
    public void setOnClickListeners(){
        setOnClickListenerForDeleteButton();
        setOnClickListenerForResetButton();
        setOnClickListenerForSaveButton();
        setOnClickListenerForGenericButtons();
        setOnClickListenerForBackButton();
    }
    public void setOnClickListenerForDeleteButton(){
        findViewById(R.id.delete).setOnClickListener(v ->{
            //we need to reset the selection(s)
            //this needs to be called before list.clear()
            //otherwise it won't do anything
            resetSelections();

            //clear the list
            currentWorkoutList.clear();

            findViewById(R.id.reset).setVisibility(View.GONE);
            hideDeleteButton();

            changeOccurred = true;
        });
    }

    public void setOnClickListenerForResetButton(){
        findViewById(R.id.reset).setOnClickListener(v ->{
            findViewById(R.id.reset).setVisibility(View.GONE);
            changeOccurred = false;

            //selection reset occurs here
            resetSelections();
        });
    }

    public void setOnClickListenerForBackButton(){
        findViewById(R.id.goBack).setOnClickListener(v ->{
            Intent intent = new Intent(this, MainActivity.class);
            //save before returning to the previous menu
            if(changeOccurred){
                saveChanges();

                //let the user know
                Toast.makeText(this,getResources().getString(R.string.saved_changes), Toast.LENGTH_SHORT).show();
            }
            //launch previous activity
            startActivity(intent);

            //finish the activity so that
            //if the user presses previous button of their phone
            //they do not return to this activity
            finish();
        });
    }

    //on click listener for the save functionality
    //+
    public void setOnClickListenerForSaveButton(){
        findViewById(R.id.save).setOnClickListener(v -> {
            saveChanges();

            //let the user know
            Toast.makeText(this,getResources().getString(R.string.saved_changes), Toast.LENGTH_SHORT).show();
        });
    }

    public void saveChanges(){
        if(changeOccurred) {
            SharedPreferences.Editor workoutEditor = dayPref.edit();
            Set<Button> holderSet = convertListToSet(currentWorkoutList);

            /* if currentWorkoutList is empty, that means that the user has deleted that program */
            /* then we need to empty the sharedPreferences of that workout plan */
            if(currentWorkoutList.isEmpty()){
                workoutEditor.clear();
            }
            /* otherwise save the workout plan */
            else {
                //save selected movements
                int i = 0;
                for (Button b : holderSet) {
                    workoutEditor.putString(StaticWorkoutPreferenceStrings.EXERCISE + i, b.getText().toString());
                    i++;
                }
                workoutEditor.putInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, holderSet.size());

                //since we have a save now
                //enable the delete button
                showDeleteButton();

                //since the changes so far have been saved
                //no further changes can occur
                //so set changeOccurred to false
            }
            changeOccurred = false;
            workoutEditor.apply();
        }
    }
    //+

    //-
    public void setOnClickListenerForGenericButtons(){
        setOnClickListenerByID(R.id.chest);
        setOnClickListenerByID(R.id.biceps);
        setOnClickListenerByID(R.id.triceps);
        setOnClickListenerByID(R.id.back);
        setOnClickListenerByID(R.id.legs);
        setOnClickListenerByID(R.id.shoulders);
        setOnClickListenerByID(R.id.cardio);
    }
    public void setOnClickListenerByID(@IdRes int id){
        findViewById(id).setOnClickListener(v ->{
            findViewById(id).setEnabled(false);
            currentWorkoutList.add(findViewById(id));
            changeOccurred = true;

            //we need to enable the reset button because we selected something
            findViewById(R.id.reset).setVisibility(View.VISIBLE);
        });
    }
    //-

    //ON CLICK METHODS END

    //this methods show the delete button if there is a saved
    //workout plan
    public void showDeleteButton(){
        if(!currentWorkoutList.isEmpty()) {
            findViewById(R.id.delete).setVisibility(View.VISIBLE);
        }
    }
    //this methods hides the delete button if there is no saved
    //workout plan
    public void hideDeleteButton(){
        if(currentWorkoutList.isEmpty()) {
            findViewById(R.id.delete).setVisibility(View.GONE);
        }
    }

    //previously saved things should appear selected
    public void disableAlreadySelected(@NonNull ArrayList<Button> aList){
        for(int i=0; i<aList.size(); i++) {
            aList.get(i).setEnabled(false);
        }
    }
}