package com.example.fitnessappformyself;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //MY FIELDS//
    private boolean changeOccurred = false;

    private CheckBox currentCheckBox = null;

    private final ArrayList<String> chestMoves = new ArrayList<>();
    private final ArrayList<String> bicepsMoves = new ArrayList<>();
    private final ArrayList<String> tricepsMoves = new ArrayList<>();
    private final ArrayList<String> backMoves = new ArrayList<>();
    private final ArrayList<String> legMoves = new ArrayList<>();
    private final ArrayList<String> shouldersMoves = new ArrayList<>();
    private final ArrayList<String> cardioMoves = new ArrayList<>();
    //MY VIEWS//

    //edit texts//
    EditText workoutCardSetInput;
    EditText workoutCardRepInput;
    //layouts//
    RelativeLayout chest_layout;
    RelativeLayout biceps_layout;
    RelativeLayout triceps_layout;
    RelativeLayout back_layout;
    RelativeLayout legs_layout;
    RelativeLayout shoulders_layout;
    RelativeLayout cardio_layout;


    //buttons//
    AppCompatButton chestGarbage;
    AppCompatButton bicepsGarbage;
    AppCompatButton tricepsGarbage;
    AppCompatButton backGarbage;
    AppCompatButton legsGarbage;
    AppCompatButton shouldersGarbage;
    AppCompatButton cardioGarbage;

    AppCompatButton chestReset;
    AppCompatButton bicepsReset;
    AppCompatButton tricepsReset;
    AppCompatButton backReset;
    AppCompatButton legsReset;
    AppCompatButton shouldersReset;
    AppCompatButton cardioReset;
    //checkboxes//
    CheckBox chestCheckBox;
    CheckBox bicepsCheckBox;
    CheckBox tricepsCheckBox;
    CheckBox backCheckBox;
    CheckBox legsCheckBox;
    CheckBox shouldersCheckBox;
    CheckBox cardioCheckBox;
    //card views//
    CardView workoutCard;
    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fillAllLists();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);

        //on click listeners for delete buttons//
        AppCompatButton chestDelete = rootView.findViewById(R.id.chestGarbage);
        chestDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton bicepsDelete = rootView.findViewById(R.id.bicepsGarbage);
        bicepsDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton tricepsDelete = rootView.findViewById(R.id.tricepsGarbage);
        tricepsDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton backDelete = rootView.findViewById(R.id.backGarbage);
        backDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton legsDelete = rootView.findViewById(R.id.legsGarbage);
        legsDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton shouldersDelete = rootView.findViewById(R.id.shouldersGarbage);
        shouldersDelete.setOnClickListener(deleteButtonOnClickListener);

        AppCompatButton cardioDelete = rootView.findViewById(R.id.cardioGarbage);
        cardioDelete.setOnClickListener(deleteButtonOnClickListener);

        //on click listeners for reset buttons//
        AppCompatButton chestReset = rootView.findViewById(R.id.chest_selection_reset);
        chestReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton bicepsReset = rootView.findViewById(R.id.biceps_selection_reset);
        bicepsReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton tricepsReset = rootView.findViewById(R.id.triceps_selection_reset);
        tricepsReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton backReset = rootView.findViewById(R.id.back_selection_reset);
        backReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton legsReset = rootView.findViewById(R.id.legs_selection_reset);
        legsReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton shouldersReset = rootView.findViewById(R.id.shoulders_selection_reset);
        shouldersReset.setOnClickListener(resetButtonOnClickListener);

        AppCompatButton cardioReset = rootView.findViewById(R.id.cardio_selection_reset);
        cardioReset.setOnClickListener(resetButtonOnClickListener);

        //on click listeners for checkboxes buttons//
        CheckBox chestCheckBox = rootView.findViewById(R.id.chestCheckBox);
        chestCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox bicepsCheckBox = rootView.findViewById(R.id.bicepsCheckBox);
        bicepsCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox tricepsCheckBox = rootView.findViewById(R.id.tricepsCheckBox);
        tricepsCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox backCheckBox = rootView.findViewById(R.id.backCheckBox);
        backCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox legsCheckBox = rootView.findViewById(R.id.legsCheckBox);
        legsCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox shouldersCheckBox = rootView.findViewById(R.id.shouldersCheckBox);
        shouldersCheckBox.setOnClickListener(checkBoxOnClickListener);

        CheckBox cardioCheckBox = rootView.findViewById(R.id.cardioCheckBox);
        cardioCheckBox.setOnClickListener(checkBoxOnClickListener);

        //on click listener for save button//
        AppCompatButton saveInfoButton = rootView.findViewById(R.id.save_and_proceed);
        saveInfoButton.setOnClickListener(saveButtonOnClickListener);

        //on click listener for card view save button//
        Button cardViewSaveButton = rootView.findViewById(R.id.workoutCardSave);
        cardViewSaveButton.setOnClickListener(saveRepAndSetListener);

        Button cardViewCancelButton = rootView.findViewById(R.id.workoutCardCancel);
        cardViewCancelButton.setOnClickListener(cancelRepAndSetSetupListener);

        //on click listeners for every other fucking button//
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.chest_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.biceps_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.triceps_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.back_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.legs_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.shoulders_layout);
        setOnClickListenerForEveryOtherFuckingButton(rootView,R.id.cardio_layout);
        return rootView;
    }
    public void setOnClickListenerForEveryOtherFuckingButton(@NonNull View rootView, @IdRes int layoutId){
        RelativeLayout chestLayout = rootView.findViewById(layoutId);
        for(int i=0; i<chestLayout.getChildCount(); i++){
            CheckBox checkBox = (CheckBox) chestLayout.getChildAt(i);
            checkBox.setOnClickListener(cardViewOnClickListener);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        // initialize my views so I can use them ffs.
        workoutCardSetInput = requireView().findViewById(R.id.workoutCardSetInput);
        workoutCardRepInput = requireView().findViewById(R.id.workoutCardRepInput);
        //layouts//
        chest_layout = requireView().findViewById(R.id.chest_layout);
        biceps_layout = requireView().findViewById(R.id.biceps_layout);
        triceps_layout = requireView().findViewById(R.id.triceps_layout);
        back_layout = requireView().findViewById(R.id.back_layout);
        legs_layout = requireView().findViewById(R.id.legs_layout);
        shoulders_layout = requireView().findViewById(R.id.shoulders_layout);
        cardio_layout = requireView().findViewById(R.id.cardio_layout);
        //buttons//
        chestGarbage = requireView().findViewById(R.id.chestGarbage);
        chestReset = requireView().findViewById(R.id.chest_selection_reset);

        bicepsGarbage = requireView().findViewById(R.id.bicepsGarbage);
        bicepsReset = requireView().findViewById(R.id.biceps_selection_reset);

        tricepsGarbage = requireView().findViewById(R.id.tricepsGarbage);
        tricepsReset = requireView().findViewById(R.id.triceps_selection_reset);

        backGarbage = requireView().findViewById(R.id.backGarbage);
        backReset = requireView().findViewById(R.id.back_selection_reset);

        legsGarbage = requireView().findViewById(R.id.legsGarbage);
        legsReset = requireView().findViewById(R.id.legs_selection_reset);

        shouldersGarbage = requireView().findViewById(R.id.shouldersGarbage);
        shouldersReset = requireView().findViewById(R.id.shoulders_selection_reset);

        cardioGarbage = requireView().findViewById(R.id.cardioGarbage);
        cardioReset = requireView().findViewById(R.id.cardio_selection_reset);
        //checkboxes//
        chestCheckBox = requireView().findViewById(R.id.chestCheckBox);
        bicepsCheckBox = requireView().findViewById(R.id.bicepsCheckBox);
        tricepsCheckBox = requireView().findViewById(R.id.tricepsCheckBox);
        backCheckBox = requireView().findViewById(R.id.backCheckBox);
        legsCheckBox = requireView().findViewById(R.id.legsCheckBox);
        shouldersCheckBox = requireView().findViewById(R.id.shouldersCheckBox);
        cardioCheckBox = requireView().findViewById(R.id.cardioCheckBox);
        //card views//
        workoutCard = requireView().findViewById(R.id.workoutCard);
    }
    //
    /////WORKOUT FRAGMENT/////
    public void onCheckBoxClicked(@NonNull View v){
        if(v.getId() == R.id.chestCheckBox){
            if(chest_layout.getVisibility() == View.GONE) {
                chest_layout.setVisibility(View.VISIBLE);
                if(!chestMoves.isEmpty()) {
                    chestGarbage.setVisibility(View.VISIBLE);
                    chestGarbage.setEnabled(true);
                }
                chestReset.setVisibility(View.VISIBLE);
            }
            else{
                chest_layout.setVisibility(View.GONE);
                chestGarbage.setVisibility(View.INVISIBLE);
                chestGarbage.setEnabled(false);
                chestReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.bicepsCheckBox){
            if(biceps_layout.getVisibility() == View.GONE) {
                biceps_layout.setVisibility(View.VISIBLE);
                if(!bicepsMoves.isEmpty()) {
                    bicepsGarbage.setVisibility(View.VISIBLE);
                    bicepsGarbage.setEnabled(true);
                }
                bicepsReset.setVisibility(View.VISIBLE);
            }
            else{
                biceps_layout.setVisibility(View.GONE);
                bicepsGarbage.setVisibility(View.INVISIBLE);
                bicepsGarbage.setEnabled(false);
                bicepsReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.tricepsCheckBox){
            if(triceps_layout.getVisibility() == View.GONE) {
                triceps_layout.setVisibility(View.VISIBLE);
                if(!tricepsMoves.isEmpty()) {
                    tricepsGarbage.setVisibility(View.VISIBLE);
                    tricepsGarbage.setEnabled(true);
                }
                tricepsReset.setVisibility(View.VISIBLE);
            }
            else{
                triceps_layout.setVisibility(View.GONE);
                tricepsGarbage.setVisibility(View.INVISIBLE);
                tricepsGarbage.setEnabled(false);
                tricepsReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.backCheckBox){
            if(back_layout.getVisibility() == View.GONE) {
                back_layout.setVisibility(View.VISIBLE);
                if(!backMoves.isEmpty()) {
                    backGarbage.setVisibility(View.VISIBLE);
                    backGarbage.setEnabled(true);
                }
                backReset.setVisibility(View.VISIBLE);
            }
            else{
                back_layout.setVisibility(View.GONE);
                backGarbage.setVisibility(View.INVISIBLE);
                backGarbage.setEnabled(false);
                backReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.legsCheckBox){
            if(legs_layout.getVisibility() == View.GONE) {
                legs_layout.setVisibility(View.VISIBLE);
                if(!legMoves.isEmpty()) {
                    legsGarbage.setVisibility(View.VISIBLE);
                    legsGarbage.setEnabled(true);
                }
                legsReset.setVisibility(View.VISIBLE);
            }
            else{
                legs_layout.setVisibility(View.GONE);
                legsGarbage.setVisibility(View.INVISIBLE);
                legsGarbage.setEnabled(false);
                legsReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.shouldersCheckBox){
            if(shoulders_layout.getVisibility() == View.GONE) {
                shoulders_layout.setVisibility(View.VISIBLE);
                if(!shouldersMoves.isEmpty()) {
                    shouldersGarbage.setVisibility(View.VISIBLE);
                    shouldersGarbage.setEnabled(true);
                }
                shouldersReset.setVisibility(View.VISIBLE);
            }
            else{
                shouldersGarbage.setVisibility(View.GONE);
                shouldersGarbage.setVisibility(View.INVISIBLE);
                shouldersGarbage.setEnabled(false);
                shouldersReset.setVisibility(View.GONE);
            }
        }
        else if(v.getId() == R.id.cardioCheckBox){
            if(cardio_layout.getVisibility() == View.GONE) {
                cardio_layout.setVisibility(View.VISIBLE);
                if(!cardioMoves.isEmpty()) {
                    cardioGarbage.setVisibility(View.VISIBLE);
                    cardioGarbage.setEnabled(true);
                }
                cardioReset.setVisibility(View.VISIBLE);
            }
            else{
                cardio_layout.setVisibility(View.GONE);
                cardioGarbage.setVisibility(View.INVISIBLE);
                cardioGarbage.setEnabled(false);
                cardioReset.setVisibility(View.GONE);
            }
        }
    }
    ////METHODS FOR CARD VIEW START////
    @SuppressLint("ApplySharedPref")
    public void saveRepsAndSets(View v){
        SharedPreferences repAndSetPreference = this.requireActivity().getSharedPreferences(currentCheckBox.getText().toString(), Context.MODE_PRIVATE);
        if(!(workoutCardSetInput.getText().toString().isEmpty()) && !(workoutCardRepInput.getText().toString().isEmpty())){
            SharedPreferences.Editor repAndSetEditor = repAndSetPreference.edit();
            try {
                repAndSetEditor.putInt("set", Integer.parseInt(workoutCardSetInput.getText().toString()));
                repAndSetEditor.putInt("rep", Integer.parseInt(workoutCardRepInput.getText().toString()));
                repAndSetEditor.commit();
                Toast.makeText(this.getActivity(), "Exercise has been saved.", Toast.LENGTH_SHORT).show();
                workoutCardSetInput.getText().clear();
                workoutCardRepInput.getText().clear();
                saveCurrentMoveAndHideWorkoutCard(v);
            } catch (NumberFormatException e){
                Toast.makeText(this.getActivity(), "Set and Rep count should be an integer.", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this.getActivity(), "You have to enter set and rep count to save the exercise.", Toast.LENGTH_SHORT).show();
    }
    public void onClickLaunchCardView(@NonNull View v){
        workoutCard.setVisibility(View.VISIBLE);
        currentCheckBox = (CheckBox) v;
        v.setEnabled(false);
    }
    public void saveCurrentMoveAndHideWorkoutCard(@NonNull View v){
        saveExerciseByView(v);
        cancelWorkoutCard();
        changeOccurred = true;
        currentCheckBox.setChecked(false);
        currentCheckBox.setEnabled(true);
    }
    public void saveExerciseByView(@NonNull View v){
        if(currentCheckBox.getTag().toString().contains("chest")) {
            chestMoves.add(currentCheckBox.getText().toString());
            chestGarbage.setVisibility(View.VISIBLE);
            chestGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("bicep")) {
            bicepsMoves.add(currentCheckBox.getText().toString());
            bicepsGarbage.setVisibility(View.VISIBLE);
            bicepsGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("tricep")) {
            tricepsMoves.add(currentCheckBox.getText().toString());
            tricepsGarbage.setVisibility(View.VISIBLE);
            tricepsGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("leg")) {
            legMoves.add(currentCheckBox.getText().toString());
            legsGarbage.setVisibility(View.VISIBLE);
            legsGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("back")) {
            backMoves.add(currentCheckBox.getText().toString());
            backGarbage.setVisibility(View.VISIBLE);
            backGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("shoulders")) {
            shouldersMoves.add(currentCheckBox.getText().toString());
            shouldersGarbage.setVisibility(View.VISIBLE);
            shouldersGarbage.setEnabled(true);
        }
        else if(currentCheckBox.getTag().toString().contains("cardio")) {
            cardioMoves.add(currentCheckBox.getText().toString());
            cardioGarbage.setVisibility(View.VISIBLE);
            cardioGarbage.setEnabled(true);
        }
    }
    public void onClickCancelRepSetSetup(@NonNull View v){
        if(v.getId() == R.id.workoutCardCancel)
            cancelWorkoutCard();
    }
    public void cancelWorkoutCard(){ workoutCard.setVisibility(View.GONE); }
    ////METHODS FOR CARD VIEW END////
    @SuppressLint("ApplySharedPref")
    public void saveWorkoutSet(String bodyPart, @NonNull List<String> aList){
        SharedPreferences workoutPref = this.requireActivity().getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        SharedPreferences.Editor workoutEditor = workoutPref.edit();
        for (int i = 0; i < aList.size(); i++)
            workoutEditor.putString("exercise" + i, aList.get(i));

        workoutEditor.putInt("buttonCount",aList.size());
        workoutEditor.commit();
    }
    public void onClickSaveAndProceed(View v){
        if(changeOccurred) {
            saveWorkoutSet("CHEST", chestMoves);
            saveWorkoutSet("LEGS", legMoves);
            saveWorkoutSet("SHOULDERS", shouldersMoves);
            saveWorkoutSet("BACK", backMoves);
            saveWorkoutSet("BICEPS", bicepsMoves);
            saveWorkoutSet("TRICEPS", tricepsMoves);
            saveWorkoutSet("CARDIO", cardioMoves);
            Toast.makeText(this.getActivity(), "Changes has been saved.", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this.getActivity(), "You haven't made any changes, there is nothing to save.", Toast.LENGTH_SHORT).show();
        onSaveClickUnSelectIfSelected();
    }
    public void onSaveClickUnSelectIfSelected(){
        if(chestCheckBox.isChecked())
            chestCheckBox.setChecked(false);
        else if(bicepsCheckBox.isChecked())
            bicepsCheckBox.setChecked(false);
        else if(tricepsCheckBox.isChecked())
            tricepsCheckBox.setChecked(false);
        else if(backCheckBox.isChecked())
            backCheckBox.setChecked(false);
        else if(legsCheckBox.isChecked())
            legsCheckBox.setChecked(false);
        else if(shouldersCheckBox.isChecked())
            shouldersCheckBox.setChecked(false);
        else if(cardioCheckBox.isChecked())
            cardioCheckBox.setChecked(false);
    }
    ////DELETE METHODS START////
    public void onClickDeleteWorkoutSet(View v){
        clearListByView(v);
        hideGarbageOnIdMatch(v);
        changeOccurred = true;
        Toast.makeText(this.getActivity(), returnBelongingWorkout(v, "deleted"), Toast.LENGTH_SHORT).show();
    }
    ////DELETE METHODS END////
    ////RESET METHODS START////
    public void onClickResetSelection(View v) {
        resetSelection(chest_layout);
        resetSelection(biceps_layout);
        resetSelection(triceps_layout);
        resetSelection(back_layout);
        resetSelection(legs_layout);
        resetSelection(shoulders_layout);
        resetSelection(cardio_layout);
        Toast.makeText(this.getActivity(), returnBelongingWorkout(v, "reset"), Toast.LENGTH_SHORT).show();
    }
    public void resetSelection(@NonNull RelativeLayout layoutAsParameter){
        for(int i=0; i<layoutAsParameter.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) layoutAsParameter.getChildAt(i);
            if(checkBox.isChecked()) {
                checkBox.setChecked(false);
                checkBox.setEnabled(true);
            }
        }
    }
    ////RESET METHODS END////
    ////HELPER METHODS START////
    public String returnBelongingWorkout(@NonNull View v, String resetOrDelete){
        if(v.getTag().toString().contains("chest"))
            return "Chest workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("bicep"))
            return "Biceps workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("tricep"))
            return "Triceps workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("back"))
            return "Back workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("leg"))
            return "Legs workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("shoulder"))
            return "Shoulders workout list has been " + resetOrDelete + ".";
        else if(v.getTag().toString().contains("cardio"))
            return "Cardio workout list has been " + resetOrDelete + ".";
        else
            return "";
    }
    public void fillAllLists(){
        onCreateFillMoveLists("CHEST");
        onCreateFillMoveLists("BICEPS");
        onCreateFillMoveLists("TRICEPS");
        onCreateFillMoveLists("BACK");
        onCreateFillMoveLists("LEGS");
        onCreateFillMoveLists("SHOULDERS");
        onCreateFillMoveLists("CARDIO");
    }
    public ArrayList<String> selectListFromBodyPart(@NonNull String bodyPart){
        switch (bodyPart) {
            case "CHEST":
                return chestMoves;
            case "BICEPS":
                return bicepsMoves;
            case "TRICEPS":
                return tricepsMoves;
            case "BACK":
                return backMoves;
            case "LEGS":
                return legMoves;
            case "SHOULDERS":
                return shouldersMoves;
            case "CARDIO":
                return cardioMoves;
        }
        return null;
    }
    public void onCreateFillMoveLists(String bodyPart){
        SharedPreferences workoutPref = this.requireActivity().getSharedPreferences(bodyPart+"WorkoutSet", Context.MODE_PRIVATE);
        for(int i=0; i<workoutPref.getInt("buttonCount",0); i++){
            if(selectListFromBodyPart(bodyPart) != null)
                selectListFromBodyPart(bodyPart).add(workoutPref.getString("exercise"+i,null));
        }
    }
    public void hideGarbageOnIdMatch(@NonNull View v){
        v.setVisibility(View.INVISIBLE);
        v.setEnabled(false);
    }
    public void clearListByView(@NonNull View v){
        if(v.getTag().toString().contains("chest"))
            chestMoves.clear();
        else if(v.getTag().toString().contains("biceps"))
            bicepsMoves.clear();
        else if(v.getTag().toString().contains("triceps"))
            tricepsMoves.clear();
        else if(v.getTag().toString().contains("legs"))
            legMoves.clear();
        else if(v.getTag().toString().contains("back"))
            backMoves.clear();
        else if(v.getTag().toString().contains("shoulders"))
            shouldersMoves.clear();
        else if(v.getTag().toString().contains("cardio"))
            cardioMoves.clear();
    }
    ////ON CLICK LISTENERS////
    private final View.OnClickListener saveRepAndSetListener = this::saveRepsAndSets;
    private final View.OnClickListener cancelRepAndSetSetupListener = this::onClickCancelRepSetSetup;
    private final View.OnClickListener cardViewOnClickListener = this::onClickLaunchCardView;
    private final View.OnClickListener saveButtonOnClickListener = this::onClickSaveAndProceed;
    private final View.OnClickListener deleteButtonOnClickListener = this::onClickDeleteWorkoutSet;
    private final View.OnClickListener resetButtonOnClickListener = this::onClickResetSelection;
    private final View.OnClickListener checkBoxOnClickListener = this::onCheckBoxClicked;
    ////HELPER METHODS END////
}