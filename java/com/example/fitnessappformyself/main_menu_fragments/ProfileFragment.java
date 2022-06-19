package com.example.fitnessappformyself.main_menu_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;
import com.example.fitnessappformyself.StaticStrings.StaticProfilePreferenceStrings;
import com.example.fitnessappformyself.profile.PersonHandler;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private final PersonHandler personHandler = PersonHandler.getInstance();

    //VIEWS//
    //TextViews
    private TextView profName;
    private TextView profAge;
    private TextView profWeight;
    private TextView profHeight;

    //ViewSwitchers
    private ViewSwitcher nameSwitcher;
    private ViewSwitcher ageSwitcher;
    private ViewSwitcher weightSwitcher;
    private ViewSwitcher heightSwitcher;

    //ImageButton
    private ImageButton save_edit_changes_name;
    private ImageButton save_edit_changes_age;
    private ImageButton save_edit_changes_weight;
    private ImageButton save_edit_changes_height;

    //EditTexts
    private EditText hiddenEditName;
    private EditText hiddenEditAge;
    private EditText hiddenEditWeight;
    private EditText hiddenEditHeight;

    //Buttons
    private Button calculateBodyFat;

    //On Click Listeners
    private final View.OnClickListener deleteButtonListener = this::setOnClickDeleteProfile;
    private final View.OnClickListener saveBodyFatListener = this::setOnClickSaveAndDisplayBodyfat;
    private final View.OnClickListener textListener = this::onTextClicked;
    private final View.OnClickListener cancelCalcsListener = this::onClickReverseVisibility;
    private final View.OnClickListener enableCalcsListener = this::setOnClickEnableBodyFatCalcs;
    private final View.OnClickListener saveButtonListeners = this::onSaveClicked;

    //ProgressBars
    private ProgressBar bodyFatProgressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        //FloatingActionButtonListeners
        rootView.findViewById(R.id.deleteProfile).setOnClickListener(deleteButtonListener);

        //ButtonListeners
        rootView.findViewById(R.id.calculateBodyFat).setOnClickListener(enableCalcsListener);
        rootView.findViewById(R.id.bodyFatCalcCancel).setOnClickListener(cancelCalcsListener);
        rootView.findViewById(R.id.saveBodyFatButton).setOnClickListener(saveBodyFatListener);

        //ImageButtonListeners
        rootView.findViewById(R.id.save_edit_changes_name).setOnClickListener(saveButtonListeners);
        rootView.findViewById(R.id.save_edit_changes_age).setOnClickListener(saveButtonListeners);
        rootView.findViewById(R.id.save_edit_changes_weight).setOnClickListener(saveButtonListeners);
        rootView.findViewById(R.id.save_edit_changes_height).setOnClickListener(saveButtonListeners);

        //TextListeners
        rootView.findViewById(R.id.profName).setOnClickListener(textListener);
        rootView.findViewById(R.id.profAge).setOnClickListener(textListener);
        rootView.findViewById(R.id.profWeight).setOnClickListener(textListener);
        rootView.findViewById(R.id.profHeight).setOnClickListener(textListener);

        /* start background thread
         to do the heavy work */
        ProfileRunnable newRunnable = new ProfileRunnable(rootView, this.requireActivity());
        new Thread(newRunnable).start();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        //initialize TextViews
        profName = requireView().findViewById(R.id.profName);
        profAge = requireView().findViewById(R.id.profAge);
        profWeight = requireView().findViewById(R.id.profWeight);
        profHeight = requireView().findViewById(R.id.profHeight);

        //initialize ViewSwitchers
        nameSwitcher = requireView().findViewById(R.id.nameSwitcher);
        ageSwitcher = requireView().findViewById(R.id.ageSwitcher);
        weightSwitcher = requireView().findViewById(R.id.weightSwitcher);
        heightSwitcher = requireView().findViewById(R.id.heightSwitcher);

        //initialize ImageButtons
        save_edit_changes_name = requireView().findViewById(R.id.save_edit_changes_name);
        save_edit_changes_age = requireView().findViewById(R.id.save_edit_changes_age);
        save_edit_changes_weight = requireView().findViewById(R.id.save_edit_changes_weight);
        save_edit_changes_height = requireView().findViewById(R.id.save_edit_changes_height);

        //initialize EditTexts
        hiddenEditName = requireView().findViewById(R.id.hiddenEditName);
        hiddenEditAge = requireView().findViewById(R.id.hiddenEditAge);
        hiddenEditWeight = requireView().findViewById(R.id.hiddenEditWeight);
        hiddenEditHeight = requireView().findViewById(R.id.hiddenEditHeight);

        //initialize Buttons
        calculateBodyFat = requireView().findViewById(R.id.calculateBodyFat);

        //initialize ProgressBars
        bodyFatProgressBar = requireView().findViewById(R.id.bodyFatProgressBarOne);
    }

    ////ON CLICK METHODS////

    //on click listener for delete profile button
    @SuppressLint("ApplySharedPref")
    public void setOnClickDeleteProfile(View v){
        SharedPreferences pref = this.requireActivity().getSharedPreferences("DoesProfileExist",Context.MODE_PRIVATE);
        SharedPreferences.Editor decrementEditor = pref.edit();
        //if at least one profile exists
        if(pref.getString("exists",null).equals("yes")){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences deletePref = this.requireActivity().getSharedPreferences(preferenceKey+1,Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = deletePref.edit();
            editor.clear();
            editor.apply();
            //
            decrementEditor.putString("exists","no");
            decrementEditor.apply();
            //return to home screen
            Intent i = new Intent(this.requireActivity(), MainActivity.class);
            startActivity(i);
        }
        else
            Toast.makeText(this.requireActivity(), "You shouldn't have gotten here, Bad developer!",Toast.LENGTH_SHORT).show();
    }

    //on click listener for save body fat button
    @SuppressLint({"SetTextI18n", "ApplySharedPref"})
    public void setOnClickSaveAndDisplayBodyfat(View v){
        try {
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = this.requireActivity().getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preference.edit();
            editor.putString(StaticProfilePreferenceStrings.PROFILE_WAIST, ((EditText) requireView().findViewById(R.id.waistInput)).getText().toString());
            editor.putString(StaticProfilePreferenceStrings.PROFILE_NECK, ((EditText) requireView().findViewById(R.id.neckInput)).getText().toString());
            if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("female"))
                editor.putString(StaticProfilePreferenceStrings.PROFILE_HIP, ((EditText) requireView().findViewById(R.id.hipInput)).getText().toString());
            editor.apply();

            String result;

            if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("male")){
                result = personHandler.calculateBodyFatPercentage_Approximate(Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WAIST, null)),
                        Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_NECK, null)), Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null)));
            }else{
                result = personHandler.calculateBodyFatPercentage_Approximate(Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WAIST, null)),
                        Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_NECK, null)), Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HIP, null)),
                        Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null)));
            }

            editor.putString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, result);
            editor.apply();

            ((TextView) requireView().findViewById(R.id.bodyFatDisplay1)).setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null));

            setProgressBarForBodyFat();
        } catch (NumberFormatException e){
            Toast.makeText(this.requireActivity(), getResources().getString(R.string.body_fat_measurements), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickReverseVisibility(View v){
        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = this.requireActivity().getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

        final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
        bodyFatLayout.setVisibility(View.GONE);
        //
        if(preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER,null).equals("female"))
            this.requireActivity().findViewById(R.id.hipInputLayout).setVisibility(View.GONE);
        //disable the body fat calculation button
        calculateBodyFat.setVisibility(View.VISIBLE);
        calculateBodyFat.setEnabled(true);
    }

    //on click listener for "calculate body fat" button
    public void setOnClickEnableBodyFatCalcs(View v){
        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = this.requireActivity().getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

        final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
        bodyFatLayout.setVisibility(View.VISIBLE);
        //
        if(preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER,null).equals("female"))
            this.requireActivity().findViewById(R.id.hipInputLayout).setVisibility(View.VISIBLE);

        //disable the body fat calculation button
        calculateBodyFat.setVisibility(View.INVISIBLE);
        calculateBodyFat.setEnabled(false);
    }

    //on click listeners for text fields

    public void onTextClicked(View v){
        if(returnViewSwitcherById(v) != null)
            returnViewSwitcherById(v).showNext();
    }

    //on click method for edit text save buttons
    @SuppressLint("ApplySharedPref")
    public void onSaveClicked(@NonNull View v){
        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = this.requireActivity().getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

        if(v.getId() == R.id.save_edit_changes_height){
            SharedPreferences.Editor editor = preference.edit();
            if(!(hiddenEditHeight.getText().toString().equals(""))){
                profHeight.setText(hiddenEditHeight.getText().toString());
                editor.putString(StaticProfilePreferenceStrings.PROFILE_HEIGHT,(hiddenEditHeight.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_weight){
            SharedPreferences.Editor editor = preference.edit();
            if(!(hiddenEditWeight.getText().toString().equals(""))){
                profWeight.setText(hiddenEditWeight.getText().toString());
                editor.putString(StaticProfilePreferenceStrings.PROFILE_WEIGHT,(hiddenEditWeight.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_age){
            SharedPreferences.Editor editor = preference.edit();
            if(!(hiddenEditAge.getText().toString().equals(""))){
                profAge.setText(hiddenEditAge.getText().toString());
                editor.putInt(StaticProfilePreferenceStrings.PROFILE_AGE,Integer.parseInt(hiddenEditAge.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_name){
            SharedPreferences.Editor editor = preference.edit();
            if(!hiddenEditName.getText().toString().equals("")){
                profName.setText(hiddenEditName.getText().toString());
                editor.putString(StaticProfilePreferenceStrings.PROFILE_NAME,hiddenEditName.getText().toString());
            }
            editor.apply();
            switchViewSwitchers(v);
        }

    }
    // ON CLICK METHODS END //

    /* HELPER METHODS START */
    public ViewSwitcher returnViewSwitcherById(@NonNull View v){
        if(v.getId() == R.id.profName) {
            save_edit_changes_name.setVisibility(View.VISIBLE);
            return nameSwitcher;
        }
        else if(v.getId() == R.id.profAge) {
            save_edit_changes_age.setVisibility(View.VISIBLE);
            return ageSwitcher;
        }
        else if(v.getId() == R.id.profWeight) {
            save_edit_changes_weight.setVisibility(View.VISIBLE);
            return weightSwitcher;
        }
        else if(v.getId() == R.id.profHeight) {
            save_edit_changes_height.setVisibility(View.VISIBLE);
            return heightSwitcher;
        }
        else return null;
    }
    public void switchViewSwitchers(@NonNull View v){
        if(v.getId() == R.id.save_edit_changes_name){
            nameSwitcher.showNext();
            save_edit_changes_name.setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_age){
            ageSwitcher.showNext();
            save_edit_changes_age.setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_weight){
            weightSwitcher.showNext();
            save_edit_changes_weight.setVisibility(View.GONE);
        }
        if(v.getId() == R.id.save_edit_changes_height){
            heightSwitcher.showNext();
            save_edit_changes_height.setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setProgressBarForBodyFat(){
        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
        SharedPreferences preference = this.requireActivity().getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

        NumberFormat nf = NumberFormat.getInstance();
        double bodyFat = 0;
        try {
            bodyFat = Objects.requireNonNull(nf.parse(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null))).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bodyFatProgressBar.setVisibility(View.VISIBLE);

        if(preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER,null).equals("male")){
            if(bodyFat <= 5.0) {
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                bodyFatProgressBar.setProgress(10);
            }
            else if(bodyFat <= 14.0 && bodyFat > 5.0) {
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                bodyFatProgressBar.setProgress(25);
            }
            else if(bodyFat <= 18.0 && bodyFat > 14.0) {//athletes and fitness
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                bodyFatProgressBar.setProgress(40);
            }
            else if(bodyFat <= 25.0 && bodyFat > 18.0) {//average
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                bodyFatProgressBar.setProgress(55);
            }
            else if(bodyFat <= 35.0 && bodyFat > 25.0) {//obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                bodyFatProgressBar.setProgress(70);
            }
            else if(bodyFat <= 40.0 && bodyFat > 35.0) {//morbidly obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                bodyFatProgressBar.setProgress(85);
            }
            else if(bodyFat > 40.0) { //super obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                bodyFatProgressBar.setProgress(99);
            }
            else
                bodyFatProgressBar.setProgress(0);
        }
        else if(preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER,null).equals("female")){
            if(bodyFat <= 10.0) {
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                bodyFatProgressBar.setProgress(10);
            }
            else if(bodyFat <= 15.0 && bodyFat > 10.0) {//essential body fat
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                bodyFatProgressBar.setProgress(25);
            }
            else if(bodyFat <= 25.0 && bodyFat > 15.0) {//athletes and fitness
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                bodyFatProgressBar.setProgress(40);
            }
            else if(bodyFat <= 30.0 && bodyFat > 25.0) {//average
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                bodyFatProgressBar.setProgress(55);
            }
            else if(bodyFat <= 40.0 && bodyFat > 30.0) {//obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                bodyFatProgressBar.setProgress(70);
            }
            else if(bodyFat <= 45.0 && bodyFat > 40.0) {//morbidly obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                bodyFatProgressBar.setProgress(85);
            }
            else if(bodyFat > 45.0) {//super obese
                bodyFatProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                bodyFatProgressBar.setProgress(99);
            }
            else
                bodyFatProgressBar.setProgress(0);
        }
    }
    /* HELPER METHODS END */

    //runnable class
    public class ProfileRunnable implements Runnable {

        private final View rootView;
        private final Context context;

        public ProfileRunnable(View rootView, Context context){
            this.rootView = rootView;
            this.context = context;
        }

        //Do the heavy work on the background
        @Override
        public void run() {
            if(profileExists())
                initializeProfileOne(rootView);
        }

        //MY METHODS
        public void calculateBMR(){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preference.edit();
            double weight = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT,null));
            double height = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT,null));
            int age = Integer.parseInt(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE,null));
            String gender = preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER,null);

            String result = personHandler.calculateBMR_Approximate(weight, height, age, gender); /* round up to 2 decimal places */

            editor.putString(StaticProfilePreferenceStrings.PROFILE_BMR,result);
            editor.apply();
        }

        public void calculateCaloriesBurned(){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preference.edit();

            double activityLevel = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY,null));
            double BMR = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR,null));

            String result = personHandler.calculateCaloriesBurned_Approximate(BMR, activityLevel); /* round up to 2 decimal places */

            editor.putString(StaticProfilePreferenceStrings.PROFILE_CALORIES, result);
            editor.apply();
        }

        //launch create profile menu if there is no profile
        @SuppressLint("ApplySharedPref")
        public boolean profileExists(){
            SharedPreferences pref = context.getSharedPreferences("DoesProfileExist", Context.MODE_PRIVATE);
            if (pref.getString("exists", null) != null) {
                return pref.getString("exists", null).equals("yes");
            }
            return false;
        }

        @SuppressLint("SetTextI18n")
        public void initializeProfileOne(View rootView){
            fillPersonalInfo(rootView);
            setProgressBarForActivityLevel(rootView);
            setProfilePicture(rootView);
        }
        @SuppressLint("SetTextI18n")
        public void fillPersonalInfo(@NonNull View rootView){
            TextView profName = rootView.findViewById(R.id.profName);
            TextView profAge = rootView.findViewById(R.id.profAge);
            TextView profWeight = rootView.findViewById(R.id.profWeight);
            TextView profHeight = rootView.findViewById(R.id.profHeight);
            TextView profBMRLast = rootView.findViewById(R.id.profBMRLast);
            TextView profCaloriesBurnedLast = rootView.findViewById(R.id.profCaloriesBurnedLast);
            TextView profActivityLevelLast = rootView.findViewById(R.id.profActivityLevelLast);
            TextView bodyFatDisplay1 = rootView.findViewById(R.id.bodyFatDisplay1);


            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            profName.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_NAME, null));
            profAge.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE, null));
            profWeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, null));
            profHeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null));
            calculateBMR();
            profBMRLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR, null));
            calculateCaloriesBurned();
            profCaloriesBurnedLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_CALORIES, null));
            profActivityLevelLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null));
            bodyFatDisplay1.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null));
        }

        public void setProfilePicture(@NonNull View rootView){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            ImageView profileAvatar = rootView.findViewById(R.id.profileAvatar);

            if(preference.contains(StaticProfilePreferenceStrings.PROFILE_AVATAR)) {
                switch (preference.getString(StaticProfilePreferenceStrings.PROFILE_AVATAR, null)) {
                    case "avatar_one":
                        profileAvatar.setBackgroundResource(R.drawable.bulldog_96px);
                        break;
                    case "avatar_two":
                        profileAvatar.setBackgroundResource(R.drawable.chicken_96px);
                        break;
                    case "avatar_three":
                        profileAvatar.setBackgroundResource(R.drawable.lion_96px);
                        break;
                    default:
                        Toast.makeText(context, "Bad Developer!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
        @SuppressLint("UseCompatLoadingForDrawables")
        public void setProgressBarForActivityLevel(@NonNull View rootView){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            SharedPreferences preference = context.getSharedPreferences(preferenceKey,Context.MODE_PRIVATE);

            ProgressBar activityLevelProgressBar = rootView.findViewById(R.id.activityProgressBarOne);
            if(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY,null) != null) {
                if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.2) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                    activityLevelProgressBar.setProgress(20);
                } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.375) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                    activityLevelProgressBar.setProgress(37);
                } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.55) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                    activityLevelProgressBar.setProgress(55);
                } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.725) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                    activityLevelProgressBar.setProgress(72);
                } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.9) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                    activityLevelProgressBar.setProgress(90);
                } else
                    activityLevelProgressBar.setProgress(0);
            }
        }
    }
}