package com.example.fitnessappformyself;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovesRecyclerViewAdapter extends RecyclerView .Adapter<MovesRecyclerViewAdapter.ViewHolder>{
    //input arrays
    //these arrays are used to get information from main activity
    private final String[] dataArray;
    private final String[] chestArray;
    private final String[] bicepsArray;
    private final String[] tricepsArray;
    private final String[] backArray;
    private final String[] shouldersArray;
    private final String[] legsArray;
    private final String[] cardioArray;
    private final Context context;

    private CheckBox currentCheckBox;

    private boolean change_occurred;

    //arrays to store move data for each body part
    private final ArrayList<WorkoutMove> chestMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> bicepsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> tricepsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> backMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> shouldersMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> legsMovesArray = new ArrayList<>();
    private final ArrayList<WorkoutMove> cardioMovesArray = new ArrayList<>();

    public MovesRecyclerViewAdapter(Context ct, String[] s1, String[] s2, String[] s3, String[] s4, String[] s5, String[] s6, String[] s7, String[] s8){
        context = ct;

        //this array holds the body part names
        dataArray = s1;

        //these arrays hold the move names
        chestArray = s2;
        bicepsArray = s3;
        tricepsArray = s4;
        backArray = s5;
        legsArray = s6;
        shouldersArray = s7;
        cardioArray = s8;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.moves_recycle_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentBodyPart = dataArray[position];
        ArrayList<WorkoutMove> currentWorkoutList = returnMoveSetArrayByBodyPartName(currentBodyPart);
        //initialize the fragment
        initializeCheckBoxLayout(holder, position, currentBodyPart, currentWorkoutList);
        //get which body part
        holder.bodyPartButton.setText(currentBodyPart);

        //Toolbar on click listener
        holder.someBodyPartBar.setOnClickListener(v -> onToolBarClicked(holder));

        //Save And Delete button on click listeners
        holder.saveAllButton.setOnClickListener(v -> saveWorkoutSet(currentBodyPart, currentWorkoutList));
        holder.deleteAllButton.setOnClickListener(v -> deleteWorkoutSet(currentWorkoutList, holder));

        //Reset button on click listener
        holder.resetWorkout.setOnClickListener(v -> enableUnSavedCheckBoxes(holder, currentWorkoutList));

        //card button on click listeners
        holder.cancelCardButton.setOnClickListener(v -> onCardCancelButtonClicked(holder));
        holder.addToListButton.setOnClickListener(v -> onCardAddButtonClicked(holder, currentCheckBox, currentWorkoutList));
    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }

    /*/INITIALIZE FRAGMENT/*/

    public void hideAlreadyAdded(@NonNull ViewHolder holder, String bodyPart, ArrayList<WorkoutMove> aList){
        String[] tempMoveArray = returnArrayByBodyPartName(bodyPart);
        if(tempMoveArray != null) {
            for (int i = 0; i < aList.size(); i++) {
                //if move is already added, disable that checkbox
                if(holder.checkBoxLayout.getChildAt(i) != null) {
                    if (((CheckBox) holder.checkBoxLayout.getChildAt(i)).getText().toString().equals(aList.get(i).getWorkout_name())) {
                        holder.checkBoxLayout.getChildAt(i).setEnabled(false);
                        ((CheckBox) holder.checkBoxLayout.getChildAt(i)).setChecked(true);
                    }
                }
            }
        }
    }

    //SharedPreferences methods start
    public boolean onCreateFillMoveList(String bodyPart, ArrayList<WorkoutMove> aList){
        SharedPreferences workoutPref = context.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        int move_count = workoutPref.getInt("move_count",0);

        if(move_count > 0) {
            for (int i = 0; i < move_count; i++) {
                String moveName = workoutPref.getString("exercise" + i, null);
                String setCount = workoutPref.getString("set" + i, null);
                String repCount = workoutPref.getString("rep" + i, null);
                WorkoutMove existingMove = new WorkoutMove(moveName, repCount, setCount);
                aList.add(existingMove);
            }
            //list has been filled successfully
            return true;
        }
        //this body part does not have any saved moves
        return false;
    }
    //SharedPreferences methods end

    //Save And Delete Button methods start
    public void deleteWorkoutSet(@NonNull ArrayList<WorkoutMove> aList, @NonNull ViewHolder holder){
        // if there is a workout set
        if(!aList.isEmpty()) {
            //enable all checkboxes (reset selections)
            resetAllCheckboxes(holder);

            //delete workout set
            aList.clear();

            change_occurred = true;
        }else{
            Toast.makeText(context, "This workout set is already empty.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveWorkoutSet(String bodyPart, @NonNull ArrayList<WorkoutMove> aList){
        // no need to save, if there are no changes
        if(change_occurred){
            SharedPreferences workoutPref = context.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
            SharedPreferences.Editor workoutEditor = workoutPref.edit();

            for(int i = 0; i < aList.size(); i++){
                workoutEditor.putString("exercise" + i, aList.get(i).getWorkout_name());
                workoutEditor.putString("set" + i, aList.get(i).getSet_count());
                workoutEditor.putString("rep" + i, aList.get(i).getRep_count());
            }
            workoutEditor.putInt("move_count",aList.size());
            workoutEditor.apply();

            //changes so far have been saved
            change_occurred = false;

            Toast.makeText(context, "Your changes have been saved.", Toast.LENGTH_SHORT).show();
        }
    }

    //Save And Delete Button methods end

    //Card Button methods start

    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardCancelButtonClicked(@NonNull ViewHolder holder){
        switchVisibility(holder.workoutCard);
        enableUnselectedCheckboxes(holder);
        currentCheckBox.setChecked(false);
        clearSetAndRepFields(holder);
    }

    //add current move name to its respective move set (ArrayList)
    //save rep and set counts
    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardAddButtonClicked(@NonNull ViewHolder holder, @NonNull CheckBox currentCheckBox, ArrayList<WorkoutMove> aList){
        //if both fields are NOT empty
        //save set and rep counts
        if(!holder.workoutCardSetInput.getText().toString().isEmpty()){
            if(!holder.workoutCardRepInput.getText().toString().isEmpty()){
                try {
                    String workout_move_name = currentCheckBox.getText().toString();
                    String rep_count = holder.workoutCardRepInput.getText().toString();
                    String set_count = holder.workoutCardSetInput.getText().toString();
                    WorkoutMove newMove = new WorkoutMove(workout_move_name, rep_count, set_count);

                    aList.add(newMove);

                    Toast.makeText(context, "Exercise has been added.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    clearSetAndRepFields(holder);

                    change_occurred = true;
                } catch (NumberFormatException e){
                    Toast.makeText(context, "Set and Rep count must be an integer.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    clearSetAndRepFields(holder);

                    //disable current checkbox
                    currentCheckBox.setEnabled(false);
                }
            }else{
                Toast.makeText(context, "You must enter a rep count.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "You must enter a set count.", Toast.LENGTH_SHORT).show();
        }
        switchVisibility(holder.workoutCard);
        enableUnselectedCheckboxes(holder);
    }

    public void clearSetAndRepFields(@NonNull ViewHolder holder){
        holder.workoutCardSetInput.getText().clear();
        holder.workoutCardRepInput.getText().clear();
    }
    //Card Button methods end

    //CheckBox methods start
    public void initializeCheckBoxLayout(ViewHolder holder, int position, String bodyPart, ArrayList<WorkoutMove> aList){
        //add checkboxes
        String[] tempMoveArray = returnArrayByBodyPartName(dataArray[position]);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 0);

        //add checkboxes
        if(tempMoveArray != null){
            for (String s : tempMoveArray) {
                CheckBox newCheckbox = new CheckBox(context);

                newCheckbox.setText(s);
                newCheckbox.setLayoutParams(lp);
                newCheckbox.setOnClickListener(v -> onCheckBoxClicked(holder, newCheckbox));

                holder.checkBoxLayout.addView(newCheckbox);
            }

            //read added moves from shared preferences file
            if(onCreateFillMoveList(bodyPart,aList)) {
                hideAlreadyAdded(holder, bodyPart, aList); //hide added moves
            }
        }


    }

    public void onCheckBoxClicked(@NonNull ViewHolder holder, CheckBox newCheckbox){
        switchVisibility(holder.workoutCard);
        currentCheckBox = newCheckbox;
        enableOrDisableCheckBox(currentCheckBox, holder);
    }

    public void enableOrDisableCheckBox(@NonNull CheckBox checkBox, ViewHolder holder){
        if(checkBox.isChecked()){
            disableAllCheckboxes(holder);
        }else{
            enableUnselectedCheckboxes(holder);
        }
    }

    //for reset functionality
    public void enableUnSavedCheckBoxes(@NonNull ViewHolder holder, ArrayList<WorkoutMove> aList){
        if(change_occurred) {
            for (int i = 0; i < holder.checkBoxLayout.getChildCount(); i++) {
                for (int j = 0; j < aList.size(); j++) {
                    //enable the child that is not in the workoutSetList
                    if (!(((CheckBox) holder.checkBoxLayout.getChildAt(i)).getText().equals(aList.get(j).getWorkout_name()))) {
                        holder.checkBoxLayout.getChildAt(i).setEnabled(true);
                        ((CheckBox) holder.checkBoxLayout.getChildAt(i)).setChecked(false);
                    }
                }
            }
            //changes so far should also be reset
            change_occurred = false;
        }else{
            Toast.makeText(context, "There are no changes to reset.", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetAllCheckboxes(@NonNull ViewHolder holder){
        for(int i = 0; i < holder.checkBoxLayout.getChildCount(); i++){
            if(((CheckBox) holder.checkBoxLayout.getChildAt(i)).isChecked()){
                holder.checkBoxLayout.getChildAt(i).setEnabled(true);
                ((CheckBox) holder.checkBoxLayout.getChildAt(i)).setChecked(false);
            }
        }
    }

    public void enableUnselectedCheckboxes(@NonNull ViewHolder holder){
        for(int i = 0; i < holder.checkBoxLayout.getChildCount(); i++){
            if(!((CheckBox) holder.checkBoxLayout.getChildAt(i)).isChecked()){
                holder.checkBoxLayout.getChildAt(i).setEnabled(true);
            }
        }
        //we also need to enable the current check box outside of the loop
        //because when we click cancel currentCheckBox is checked and disabled
        //but in the for loop we only enable check boxes that are not checked but disabled

        //AND we also need to check if no changes have occurred (if the add button was clicked)
        //because than this check box should not be enabled.
        if(!change_occurred) {
            currentCheckBox.setEnabled(true);
        }
    }

    public void disableAllCheckboxes(@NonNull ViewHolder holder){
        for(int i = 0; i < holder.checkBoxLayout.getChildCount(); i++){
            holder.checkBoxLayout.getChildAt(i).setEnabled(false);
        }
    }
    //CheckBox methods end

    //ToolbarOnClickMethod
    public void onToolBarClicked(@NonNull ViewHolder holder){
        switchVisibility(holder.saveAllButton);
        switchVisibility(holder.checkBoxLayout);
        switchVisibility(holder.resetWorkout);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView bodyPartButton;
        private final LinearLayout checkBoxLayout;
        private final Toolbar someBodyPartBar;
        private final CardView workoutCard;
        private final Button addToListButton;
        private final Button cancelCardButton;
        private final Button saveAllButton;
        private final Button deleteAllButton;
        private final EditText workoutCardSetInput;
        private final EditText workoutCardRepInput;
        private final ImageButton resetWorkout;

        public ViewHolder (@NonNull View itemView){
            super(itemView);
            bodyPartButton = itemView.findViewById(R.id.bodyPartButton);
            checkBoxLayout = itemView.findViewById(R.id.checkBoxLayout);
            someBodyPartBar = itemView.findViewById(R.id.someBodyPartBar);
            workoutCard = itemView.findViewById(R.id.workoutCard);
            addToListButton = itemView.findViewById(R.id.workoutCardSave);
            cancelCardButton = itemView.findViewById(R.id.workoutCardCancel);
            workoutCardSetInput = itemView.findViewById(R.id.workoutCardSetInput);
            workoutCardRepInput = itemView.findViewById(R.id.workoutCardRepInput);
            saveAllButton = itemView.findViewById(R.id.save_and_proceed);
            deleteAllButton = itemView.findViewById(R.id.delete_workout);
            resetWorkout = itemView.findViewById(R.id.reset_workout_set);
        }
    }

    public void switchVisibility(@NonNull View v){
        if(v.getVisibility() == View.INVISIBLE || v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }

    //to be removed
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

    public String[] returnArrayByBodyPartName(@NonNull String bodyPartName){
        switch (bodyPartName){
            case "CHEST":
                return chestArray;
            case "BICEPS":
                return bicepsArray;
            case "TRICEPS":
                return tricepsArray;
            case "BACK":
                return backArray;
            case "SHOULDERS":
                return shouldersArray;
            case "LEGS":
                return legsArray;
            case "CARDIO":
                return cardioArray;
            default:
                return null;
        }
    }
}
