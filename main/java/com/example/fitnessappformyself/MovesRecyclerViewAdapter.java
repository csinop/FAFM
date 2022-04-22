package com.example.fitnessappformyself;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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


    //arrays to store move data for each body part
    private final ArrayList<String> chestMovesArray = new ArrayList<>();
    private final ArrayList<String> bicepsMovesArray = new ArrayList<>();
    private final ArrayList<String> tricepsMovesArray = new ArrayList<>();
    private final ArrayList<String> backMovesArray = new ArrayList<>();
    private final ArrayList<String> shouldersMovesArray = new ArrayList<>();
    private final ArrayList<String> legsMovesArray = new ArrayList<>();
    private final ArrayList<String> cardioMovesArray = new ArrayList<>();

    public MovesRecyclerViewAdapter(Context ct, String[] s1, String[] s2, String[] s3, String[] s4, String[] s5, String[] s6, String[] s7, String[] s8){
        context = ct;

        //this array holds the body part names
        dataArray = s1;

        //these arrays hold the move names
        chestArray = s2;
        bicepsArray = s3;
        tricepsArray = s4;
        backArray = s5;
        shouldersArray = s6;
        legsArray = s7;
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
        //initialize the fragment
        initializeFragment(holder, dataArray[position], position);

        //get which body part
        holder.bodyPartButton.setText(dataArray[position]);

        //Toolbar on click listener
        holder.someBodyPartBar.setOnClickListener(v -> onToolBarClicked(holder));

        //Save All button on click listener
        holder.saveAllButton.setOnClickListener(v -> saveWorkoutSet(dataArray[position], returnMoveSetArrayByBodyPartName(dataArray[position])));

        //card button on click listeners
        holder.cancelCardButton.setOnClickListener(v -> onCardCancelButtonClicked(holder));
        holder.addToListButton.setOnClickListener(v -> onCardAddButtonClicked(holder, dataArray[position], currentCheckBox));


    }

    @Override
    public int getItemCount() {
        return dataArray.length;
    }

    /*/INITIALIZE FRAGMENT/*/
    public void initializeFragment(ViewHolder holder, String bodyPart, int position){
        initializeCheckBoxLayout(holder, position);
        onCreateFillMoveLists(bodyPart);
        hideAlreadyAdded(holder, bodyPart);
    }

    public void hideAlreadyAdded(@NonNull ViewHolder holder, String bodyPart){
        for(int i = 0; i < returnMoveSetArrayByBodyPartName(bodyPart).size(); i++){
            //if move is already added, disable that checkbox
            if(!returnMoveSetArrayByBodyPartName(bodyPart).isEmpty()) {
                if (((CheckBox) holder.checkBoxLayout.getChildAt(i)).getText().toString().equals(returnMoveSetArrayByBodyPartName(bodyPart).get(i))) {
                    holder.checkBoxLayout.getChildAt(i).setEnabled(false);
                    ((CheckBox) holder.checkBoxLayout.getChildAt(i)).setChecked(true);
                }
            }
        }
    }

    //SharedPreferences methods start
    public void onCreateFillMoveLists(String bodyPart){
        SharedPreferences workoutPref = context.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        for(int i=0; i<workoutPref.getInt("buttonCount",0); i++){
            if(returnMoveSetArrayByBodyPartName(bodyPart) != null) {
                returnMoveSetArrayByBodyPartName(bodyPart).add(workoutPref.getString("exercise" + i, null));
            }
        }
    }
    //SharedPreferences methods end

    //Save All Button methods start
    public void saveWorkoutSet(String bodyPart, @NonNull ArrayList<String> aList){
        SharedPreferences workoutPref = context.getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor workoutEditor = workoutPref.edit();
        Set<String> holderSet = convertListToSet(aList);
        int i=0;
        for (String s : holderSet) {
            workoutEditor.putString("exercise" + i, s);
            i++;
        }
        workoutEditor.putInt("buttonCount",holderSet.size());
        workoutEditor.commit();

        Toast.makeText(context, "Your changes have been saved.", Toast.LENGTH_SHORT).show();
    }

    public Set<String> convertListToSet(ArrayList<String> aList){
        return  new HashSet<>(aList);
    }
    //Save All Button methods end

    //Card Button methods start

    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardCancelButtonClicked(@NonNull ViewHolder holder){
        switchVisibility(holder.workoutCard);
        enableUnselectedCheckboxes(holder);
        currentCheckBox.setChecked(false);
    }

    //add current move name to its respective move set (ArrayList)
    //save rep and set counts
    //hide CardView
    //enable all check boxes, this step is not necessary as the use should be able to tick only one checkbox at a time
    //un-tick current checkbox
    public void onCardAddButtonClicked(@NonNull ViewHolder holder, String bodyPart, @NonNull CheckBox currentCheckBox){
        returnMoveSetArrayByBodyPartName(bodyPart).add(currentCheckBox.getText().toString());

        //if both fields are NOT empty
        //save set and rep counts
        if(!holder.workoutCardSetInput.getText().toString().isEmpty()){
            if(!holder.workoutCardRepInput.getText().toString().isEmpty()){
                SharedPreferences repAndSetPreference = context.getSharedPreferences(currentCheckBox.getText().toString(), Context.MODE_PRIVATE);
                SharedPreferences.Editor repAndSetEditor = repAndSetPreference.edit();
                try {
                    repAndSetEditor.putInt("set", Integer.parseInt(holder.workoutCardSetInput.getText().toString()));
                    repAndSetEditor.putInt("rep", Integer.parseInt(holder.workoutCardRepInput.getText().toString()));
                    repAndSetEditor.commit();
                    Toast.makeText(context, "Exercise has been added.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    holder.workoutCardSetInput.getText().clear();
                    holder.workoutCardRepInput.getText().clear();
                } catch (NumberFormatException e){
                    Toast.makeText(context, "Set and Rep count must be an integer.", Toast.LENGTH_SHORT).show();

                    //clear the input fields
                    holder.workoutCardSetInput.getText().clear();
                    holder.workoutCardRepInput.getText().clear();

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
    //Card Button methods end

    //CheckBox methods start
    public void initializeCheckBoxLayout(ViewHolder holder, int position){
        //add checkboxes
        String[] tempMoveArray = returnArrayByBodyPartName(dataArray[position]);

        if(tempMoveArray != null){
            for (String s : tempMoveArray) {
                CheckBox newCheckbox = new CheckBox(context);
                newCheckbox.setText(s);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 8, 0, 0);

                //TO DO
                newCheckbox.setLayoutParams(lp);

                newCheckbox.setOnClickListener(v -> onCheckBoxClicked(holder, newCheckbox));

                holder.checkBoxLayout.addView(newCheckbox);
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

    public void enableUnselectedCheckboxes(@NonNull ViewHolder holder){
        for(int i = 0; i < holder.checkBoxLayout.getChildCount(); i++){
            if(!((CheckBox) holder.checkBoxLayout.getChildAt(i)).isChecked()){
                holder.checkBoxLayout.getChildAt(i).setEnabled(true);
            }
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
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView bodyPartButton;
        private final LinearLayout checkBoxLayout;
        private final Toolbar someBodyPartBar;
        private final CardView workoutCard;
        private final Button addToListButton;
        private final Button cancelCardButton;
        private final Button saveAllButton;
        private final EditText workoutCardSetInput;
        private final EditText workoutCardRepInput;

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
        }
    }

    public void switchVisibility(@NonNull View v){
        if(v.getVisibility() == View.INVISIBLE || v.getVisibility() == View.GONE){
            v.setVisibility(View.VISIBLE);
        }else{
            v.setVisibility(View.GONE);
        }
    }
    public ArrayList<String> returnMoveSetArrayByBodyPartName(@NonNull String bodyPartName){
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
