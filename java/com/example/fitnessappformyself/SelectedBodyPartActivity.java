package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessappformyself.exception_handler.ExceptionHandler;

import java.util.ArrayList;

public class SelectedBodyPartActivity extends AppCompatActivity {

    //arrays to store move data for each body part
    private final ArrayList<WorkoutMove> chestMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> bicepsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> tricepsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> backMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> shouldersMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> legsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> cardioMovesArray = new ArrayList<>();

    private boolean change_occurred = false;

    private CheckBox currentCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_body_part_layout);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        initializeActivity();
    }

    public void initializeActivity() {
        String currentBodyPart = getBodyPartFromIntent();
        ((TextView) findViewById(R.id.selectedBodyPartText)).setText(currentBodyPart);

        ArrayList<String> moveList = getMoveListFromIntent();
        ArrayList<WorkoutMove> addedMoveList = returnMoveSetArrayByBodyPartName(currentBodyPart);
        LinearLayout checkBoxLayout = findViewById(R.id.checkBoxLayout);

        initializeCheckBoxLayout(moveList, currentBodyPart, addedMoveList, checkBoxLayout);

        //set on click listeners
        setOnClickListeners(currentBodyPart, addedMoveList, checkBoxLayout);
    }

    public void setOnClickListeners(String bodyPart, ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout){
        findViewById(R.id.save_and_proceed).setOnClickListener(v -> saveWorkoutSet(bodyPart, addedMoveList));
        findViewById(R.id.delete_workout).setOnClickListener(v -> deleteWorkoutSet(addedMoveList, checkBoxLayout));
        findViewById(R.id.reset_workout_set).setOnClickListener(v -> enableUnSavedCheckBoxes(checkBoxLayout, addedMoveList));
        findViewById(R.id.workoutCardCancel).setOnClickListener(v -> onCardCancelButtonClicked(checkBoxLayout));
        findViewById(R.id.workoutCardAdd).setOnClickListener(v -> onCardAddButtonClicked(currentCheckBox, addedMoveList, checkBoxLayout));
    }

    //CheckBox methods start
    public void initializeCheckBoxLayout(ArrayList<String> moveList, String bodyPart, ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
        //add checkboxes

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 0);

        //add checkboxes
        if (moveList != null) {
            for (String s : moveList) {
                CheckBox newCheckbox = new CheckBox(this);

                newCheckbox.setText(s);
                newCheckbox.setLayoutParams(lp);
                newCheckbox.setOnClickListener(v -> onCheckBoxClicked(checkBoxLayout, newCheckbox));

                checkBoxLayout.addView(newCheckbox);
            }

            //read added moves from shared preferences file
            if (onCreateFillMoveList(bodyPart, addedMoveList)) {
                hideAlreadyAdded(moveList, addedMoveList, checkBoxLayout); //hide added moves
            }
        }
    }

    //receive current body part string
    public String getBodyPartFromIntent() {
        Intent i = getIntent();
        return i.getStringExtra("bodyPart");
    }
    //receive current move list
    public ArrayList<String> getMoveListFromIntent(){
        Intent i = getIntent();
        return i.getStringArrayListExtra("moveList");
    }

    public void hideAlreadyAdded(ArrayList<String> moveList, ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
        if (moveList != null) {
            for (int i = 0; i < addedMoveList.size(); i++) {
                //if move is already added, disable that checkbox
                for(int j = 0; j < checkBoxLayout.getChildCount(); j++) {
                    if (checkBoxLayout.getChildAt(j) != null) {
                        if (((CheckBox) checkBoxLayout.getChildAt(j)).getText().toString().equals(addedMoveList.get(i).getWorkout_name())) {
                            checkBoxLayout.getChildAt(j).setEnabled(false);
                            ((CheckBox) checkBoxLayout.getChildAt(j)).setChecked(true);
                        }
                    }
                }
            }
        }
    }

    public void onCheckBoxClicked(LinearLayout checkBoxLayout, CheckBox newCheckbox) {
        switchVisibility(findViewById(R.id.workoutCard));
        currentCheckBox = newCheckbox;
        enableOrDisableCheckBox(currentCheckBox, checkBoxLayout);
    }

    public void enableOrDisableCheckBox(@NonNull CheckBox checkBox, LinearLayout checkBoxLayout) {
        if (checkBox.isChecked()) {
            disableAllCheckboxes(checkBoxLayout);
        } else {
            enableUnselectedCheckboxes(checkBoxLayout);
        }
    }

    //for reset functionality
    public void enableUnSavedCheckBoxes(LinearLayout checkBoxLayout, ArrayList<WorkoutMove> addedMoveList) {
        if (change_occurred) {
            for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
                for (int j = 0; j < addedMoveList.size(); j++) {
                    //enable the child that is not in the workoutSetList
                    if (!(((CheckBox) checkBoxLayout.getChildAt(i)).getText().equals(addedMoveList.get(j).getWorkout_name()))) {
                        checkBoxLayout.getChildAt(i).setEnabled(true);
                        ((CheckBox) checkBoxLayout.getChildAt(i)).setChecked(false);
                    }
                }
            }
            //changes so far should also be reset
            change_occurred = false;
        } else {
            Toast.makeText(this, "There are no changes to reset.", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetAllCheckboxes(@NonNull LinearLayout checkBoxLayout) {
        for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
            if (((CheckBox) checkBoxLayout.getChildAt(i)).isChecked()) {
                checkBoxLayout.getChildAt(i).setEnabled(true);
                ((CheckBox) checkBoxLayout.getChildAt(i)).setChecked(false);
            }
        }
    }

    public void enableUnselectedCheckboxes(@NonNull LinearLayout checkBoxLayout) {
        for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
            if (!((CheckBox) checkBoxLayout.getChildAt(i)).isChecked()) {
                checkBoxLayout.getChildAt(i).setEnabled(true);
            }
        }
        //we also need to enable the current check box outside of the loop
        //because when we click cancel currentCheckBox is checked and disabled
        //but in the for loop we only enable check boxes that are not checked but disabled

        //AND we also need to check if no changes have occurred (if the add button was clicked)
        //because than this check box should not be enabled.
        if (!change_occurred) {
            currentCheckBox.setEnabled(true);
        }
    }

    public void disableAllCheckboxes(@NonNull LinearLayout checkBoxLayout) {
        for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
            checkBoxLayout.getChildAt(i).setEnabled(false);
        }
    }
    //CheckBox methods end

    //SharedPreferences methods start
    public boolean onCreateFillMoveList(String bodyPart, ArrayList<WorkoutMove> addedMoveList) {
        SharedPreferences workoutPref = getSharedPreferences(bodyPart + "WorkoutSet", Context.MODE_PRIVATE);
        int move_count = workoutPref.getInt("move_count", 0);

        if (move_count > 0) {
            for (int i = 0; i < move_count; i++) {
                String moveName = workoutPref.getString("exercise" + i, null);
                String setCount = workoutPref.getString("set" + i, null);
                String repCount = workoutPref.getString("rep" + i, null);
                WorkoutMove existingMove = new WorkoutMove(moveName, repCount, setCount);
                addedMoveList.add(existingMove);
            }
            //list has been filled successfully
            return true;
        }
        //this body part does not have any saved moves
        return false;
    }
    //SharedPreferences methods end

    //Save And Delete Button methods start
    public void deleteWorkoutSet(@NonNull ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
        // if there is a workout set
        if (!addedMoveList.isEmpty()) {
            //enable all checkboxes (reset selections)
            resetAllCheckboxes(checkBoxLayout);

            //delete workout set
            addedMoveList.clear();

            change_occurred = true;
        } else {
            Toast.makeText(this, "This workout set is already empty.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveWorkoutSet(String bodyPart, @NonNull ArrayList<WorkoutMove> addedMoveList) {
        // no need to save, if there are no changes
        if (change_occurred) {
            SharedPreferences workoutPref = getSharedPreferences(bodyPart + "WorkoutSet", Context.MODE_PRIVATE);
            SharedPreferences.Editor workoutEditor = workoutPref.edit();

            for (int i = 0; i < addedMoveList.size(); i++) {
                workoutEditor.putString("exercise" + i, addedMoveList.get(i).getWorkout_name());
                workoutEditor.putString("set" + i, addedMoveList.get(i).getSet_count());
                workoutEditor.putString("rep" + i, addedMoveList.get(i).getRep_count());
            }
            workoutEditor.putInt("move_count", addedMoveList.size());
            workoutEditor.apply();

            //changes so far have been saved
            change_occurred = false;

            Toast.makeText(this, "Your changes have been saved.", Toast.LENGTH_SHORT).show();
        }
    }

    //Save And Delete Button methods end

    //Card Button methods start

    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardCancelButtonClicked(LinearLayout checkBoxLayout) {
        switchVisibility(findViewById(R.id.workoutCard));
        enableUnselectedCheckboxes(checkBoxLayout);
        currentCheckBox.setChecked(false);
        clearSetAndRepFields();
    }

    //add current move name to its respective move set (ArrayList)
    //save rep and set counts
    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardAddButtonClicked(@NonNull CheckBox currentCheckBox, ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
        //if both fields are NOT empty
        //save set and rep counts
        EditText repInput = findViewById(R.id.workoutCardRepInput);
        EditText setInput = findViewById(R.id.workoutCardSetInput);

        if (!setInput.getText().toString().isEmpty()) {
            if (!repInput.toString().isEmpty()) {
                try {
                    String workout_move_name = currentCheckBox.getText().toString();
                    String rep_count = repInput.getText().toString();
                    String set_count = setInput.getText().toString();
                    WorkoutMove newMove = new WorkoutMove(workout_move_name, rep_count, set_count);

                    addedMoveList.add(newMove);

                    Toast.makeText(this, "Exercise has been added.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    clearSetAndRepFields();

                    change_occurred = true;
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Set and Rep count must be an integer.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    clearSetAndRepFields();

                    //disable current checkbox
                    currentCheckBox.setEnabled(false);
                }
            } else {
                Toast.makeText(this, "You must enter a rep count.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You must enter a set count.", Toast.LENGTH_SHORT).show();
        }
        switchVisibility(findViewById(R.id.workoutCard));
        enableUnselectedCheckboxes(checkBoxLayout);
    }

    public void switchVisibility(@NonNull View v){
        if(v.getVisibility() == View.INVISIBLE || v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }

    public void clearSetAndRepFields() {
        ((EditText) findViewById(R.id.workoutCardSetInput)).getText().clear();
        ((EditText) findViewById(R.id.workoutCardRepInput)).getText().clear();
    }
    //Card Button methods end

    public ArrayList<WorkoutMove> returnMoveSetArrayByBodyPartName(@NonNull String bodyPartName){
        switch (bodyPartName){
            case "CHEST":
                return chestMovesArray;
            case "BICEPS":
                return bicepsMovesArray;
            case "TRICEPS":
                return tricepsMovesArray;
            case "BACK":
                return backMovesArray;
            case "SHOULDERS":
                return shouldersMovesArray;
            case "LEGS":
                return legsMovesArray;
            case "CARDIO":
                return cardioMovesArray;
            default:
                return null;
        }
    }
}