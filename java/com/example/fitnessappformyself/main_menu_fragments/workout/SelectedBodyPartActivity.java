package com.example.fitnessappformyself.main_menu_fragments.workout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitnessappformyself.main_menu_fragments.AsyncResult;
import com.example.fitnessappformyself.R;
import com.example.fitnessappformyself.exception_handler.ExceptionHandler;
import com.example.fitnessappformyself.main_menu_fragments.SharedPreferencesHandler;
import com.example.fitnessappformyself.main_menu_fragments.StaticWorkoutPreferenceStrings;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SelectedBodyPartActivity extends AppCompatActivity implements AsyncResult<String> {
    private SharedPreferences workoutPref;

    //arrays to store move data for each body part
    private final ArrayList<WorkoutMove> movesArray = new ArrayList<>();
    private final ArrayList<CheckBox> selectedCheckboxes = new ArrayList<>();

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
        String bodyPart = getBodyPartFromIntent();
        workoutPref = getSharedPreferences(bodyPart + StaticWorkoutPreferenceStrings.WORKOUT_SET, Context.MODE_PRIVATE);

        ((TextView) findViewById(R.id.selectedBodyPartText)).setText(bodyPart);

        ArrayList<String> moveList = getMoveListFromIntent();
        ArrayList<WorkoutMove> addedMoveList = movesArray;
        LinearLayout checkBoxLayout = findViewById(R.id.checkBoxLayout);

        initializeCheckBoxLayout(moveList, checkBoxLayout);

        //set on click listeners
        setOnClickListeners(addedMoveList, checkBoxLayout);

        //read added moves from shared preferences file
        if(onCreateFillMoveList(addedMoveList)) {
            InitializerAsync newInitializer = new InitializerAsync(moveList, addedMoveList, this);
            newInitializer.delegate = this;
            newInitializer.execute();
        }
    }

    public void setOnClickListeners(ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout){
        findViewById(R.id.save_and_proceed).setOnClickListener(v -> saveWorkoutSet(addedMoveList));
        findViewById(R.id.delete_workout).setOnClickListener(v -> deleteWorkoutSet(addedMoveList, checkBoxLayout));
        findViewById(R.id.reset_workout_set).setOnClickListener(v -> resetCheckBoxes());
        findViewById(R.id.workoutCardCancel).setOnClickListener(v -> onCardCancelButtonClicked(checkBoxLayout));
        findViewById(R.id.workoutCardAdd).setOnClickListener(v -> onCardAddButtonClicked(currentCheckBox, addedMoveList, checkBoxLayout));
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


    //CheckBox methods start
    public void initializeCheckBoxLayout(ArrayList<String> moveList, LinearLayout checkBoxLayout) {
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
    public void resetCheckBoxes() {
        if (change_occurred) {
            for (int i = 0; i < selectedCheckboxes.size(); i++) {
                selectedCheckboxes.get(i).setEnabled(true);
                selectedCheckboxes.get(i).setChecked(false);
            }
            //changes so far should also be reset
            change_occurred = false;
        } else {
            Toast.makeText(this, "There are no changes to reset.", Toast.LENGTH_SHORT).show();
        }
    }

    public void uncheckAllCheckBoxes(@NonNull LinearLayout checkBoxLayout) {
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

    public boolean onCreateFillMoveList(ArrayList<WorkoutMove> addedMoveList) {
        int move_count = workoutPref.getInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, 0);

        if (move_count > 0) {
            for (int i = 0; i < move_count; i++) {
                String moveName = workoutPref.getString(StaticWorkoutPreferenceStrings.EXERCISE + i, null);
                String setCount = workoutPref.getString(StaticWorkoutPreferenceStrings.SET + i, null);
                String repCount = workoutPref.getString(StaticWorkoutPreferenceStrings.REP + i, null);
                WorkoutMove existingMove = new WorkoutMove(moveName, repCount, setCount);
                addedMoveList.add(existingMove);
            }
            //list has been filled successfully
            return true;
        }
        //this body part does not have any saved moves
        return false;
    }

    //Save And Delete Button methods start
    public void deleteWorkoutSet(@NonNull ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
        // if there is a workout set
        if (!addedMoveList.isEmpty()) {
            //enable all checkboxes (reset selections)
            uncheckAllCheckBoxes(checkBoxLayout);

            //delete workout set
            addedMoveList.clear();
            selectedCheckboxes.clear();

            change_occurred = true;
        } else {
            Toast.makeText(this, "This workout set is already empty.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveWorkoutSet(@NonNull ArrayList<WorkoutMove> addedMoveList) {
        // no need to save, if there are no changes
        if (change_occurred) {
            SharedPreferencesHandler<String> stringPreferenceHandler = new SharedPreferencesHandler<>(workoutPref);

            for (int i = 0; i < addedMoveList.size(); i++) {
                stringPreferenceHandler.saveToSharedPreferences(StaticWorkoutPreferenceStrings.EXERCISE + i, addedMoveList.get(i).getWorkout_name());
                stringPreferenceHandler.saveToSharedPreferences(StaticWorkoutPreferenceStrings.SET + i, addedMoveList.get(i).getSet_count());
                stringPreferenceHandler.saveToSharedPreferences(StaticWorkoutPreferenceStrings.REP + i, addedMoveList.get(i).getRep_count());
            }

            SharedPreferencesHandler<Integer> integerPreferenceHandler = new SharedPreferencesHandler<>(workoutPref);
            integerPreferenceHandler.saveToSharedPreferences(StaticWorkoutPreferenceStrings.MOVE_COUNT, addedMoveList.size());

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

                    switchVisibility(findViewById(R.id.workoutCard));
                    enableUnselectedCheckboxes(checkBoxLayout);

                    change_occurred = true;

                    /* holding the checkboxes in a separate list saves us from a nested for loop */
                    selectedCheckboxes.add(currentCheckBox);
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

    @Override
    public void onTaskFinish(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
    //Card Button methods end

    /* to handle an O(n^2) task in the background */
    public static class InitializerAsync extends AsyncTask<Void, Void, String>{
        // to get the result ot this task
        public AsyncResult<String> delegate;

        private final ArrayList<String> moveList;
        private final ArrayList<WorkoutMove> addedMoveList;
        private final WeakReference<SelectedBodyPartActivity> weakReference;

        InitializerAsync(ArrayList<String> moveList, ArrayList<WorkoutMove> addedMoveList, SelectedBodyPartActivity context){
            this.moveList = moveList;
            this.addedMoveList = addedMoveList;
            this.weakReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... voids) {
            LinearLayout checkBoxLayout = weakReference.get().findViewById(R.id.checkBoxLayout);
            hideAlreadyAdded(moveList, addedMoveList, checkBoxLayout);
            return "";
        }

        @Override
        protected void onPostExecute(String str){
            super.onPostExecute(str);
            delegate.onTaskFinish(str);
        }

        public String hideAlreadyAdded(ArrayList<String> moveList, ArrayList<WorkoutMove> addedMoveList, LinearLayout checkBoxLayout) {
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
                return weakReference.get().getString(R.string.hide_successful);
            }
            return weakReference.get().getString(R.string.hide_failed);
        }
    }
}