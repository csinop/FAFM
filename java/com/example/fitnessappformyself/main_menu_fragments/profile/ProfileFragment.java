package com.example.fitnessappformyself.main_menu_fragments.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ProfileFragment extends Fragment {
    private final PersonHandler personHandler = new PersonHandler();
    //Encrypted SharedPreference reference
    private SharedPreferences preference = null;

    /* Image Buttons */
    ImageButton save_edit_changes_name;
    ImageButton save_edit_changes_age;
    ImageButton save_edit_changes_weight;
    ImageButton save_edit_changes_height;

    /* View Switchers */
    ViewSwitcher nameSwitcher;
    ViewSwitcher ageSwitcher;
    ViewSwitcher weightSwitcher;
    ViewSwitcher heightSwitcher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

        /* initialize shared preferences */
        MasterKey masterKey = null;
        try {
            masterKey = new MasterKey.Builder(requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        if (masterKey != null) {
            try {
                preference = EncryptedSharedPreferences.create(
                        requireActivity(),
                        preferenceKey,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        //initialize Views
        save_edit_changes_name = view.findViewById(R.id.save_edit_changes_name);
        save_edit_changes_age = view.findViewById(R.id.save_edit_changes_age);
        save_edit_changes_weight = view.findViewById(R.id.save_edit_changes_weight);
        save_edit_changes_height = view.findViewById(R.id.save_edit_changes_height);

        nameSwitcher = view.findViewById(R.id.nameSwitcher);
        ageSwitcher = view.findViewById(R.id.ageSwitcher);
        weightSwitcher = view.findViewById(R.id.weightSwitcher);
        heightSwitcher = view.findViewById(R.id.heightSwitcher);

        //FloatingActionButtonListeners
        view.findViewById(R.id.deleteProfile).setOnClickListener(v -> setOnClickDeleteProfile());

        //ButtonListeners
        view.findViewById(R.id.calculateBodyFat).setOnClickListener(this::setOnClickEnableBodyFatCalcs);
        view.findViewById(R.id.bodyFatCalcCancel).setOnClickListener(v -> onClickReverseVisibility(view));
        view.findViewById(R.id.saveBodyFatButton).setOnClickListener(this::setOnClickSaveAndDisplayBodyfat);

        //ImageButtonListeners
        view.findViewById(R.id.save_edit_changes_name).setOnClickListener(this::onSaveClicked);
        view.findViewById(R.id.save_edit_changes_age).setOnClickListener(this::onSaveClicked);
        view.findViewById(R.id.save_edit_changes_weight).setOnClickListener(this::onSaveClicked);
        view.findViewById(R.id.save_edit_changes_height).setOnClickListener(this::onSaveClicked);

        //TextListeners
        view.findViewById(R.id.profName).setOnClickListener(this::onTextClicked);
        view.findViewById(R.id.profAge).setOnClickListener(this::onTextClicked);
        view.findViewById(R.id.profWeight).setOnClickListener(this::onTextClicked);
        view.findViewById(R.id.profHeight).setOnClickListener(this::onTextClicked);

        fillPersonalInfo();
        setProfilePicture();
    }

    @SuppressLint("SetTextI18n")
    public void fillPersonalInfo(){
        TextView profName = requireActivity().findViewById(R.id.profName);
        TextView profAge = requireActivity().findViewById(R.id.profAge);
        TextView profWeight = requireActivity().findViewById(R.id.profWeight);
        TextView profHeight = requireActivity().findViewById(R.id.profHeight);
        TextView profBMRLast = requireActivity().findViewById(R.id.profBMRLast);
        TextView profCaloriesBurnedLast = requireActivity().findViewById(R.id.profCaloriesBurnedLast);
        TextView profActivityLevelLast = requireActivity().findViewById(R.id.profActivityLevelLast);
        TextView bodyFatDisplay1 = requireActivity().findViewById(R.id.bodyFatDisplay1);

        if(preference != null) {
            profName.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_NAME, null));
            profAge.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE, null));
            profWeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, null));
            profHeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null));
            profBMRLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR, null));
            profCaloriesBurnedLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_CALORIES, null));
            profActivityLevelLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null));
            bodyFatDisplay1.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null));
        }
    }

    public void setProfilePicture(){
        if(preference != null) {
            ImageView profileAvatar = requireActivity().findViewById(R.id.profileAvatar);

            if (preference.contains(StaticProfilePreferenceStrings.PROFILE_AVATAR)) {
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
                        Toast.makeText(requireActivity(), "Bad Developer!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    }

    ////ON CLICK METHODS////

    //on click listener for delete profile button
    @SuppressLint("ApplySharedPref")
    public void setOnClickDeleteProfile(){
        SharedPreferences pref = this.requireActivity().getSharedPreferences("DoesProfileExist",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref.edit();
        //if at least one profile exists
        if(pref.getString("exists",null).equals("yes")){
            if(preference != null) {
                //preference.edit().clear().apply();

                editor1.putString("exists", "no").apply();

                //return to main activity
                Intent i = new Intent(this.requireActivity(), MainActivity.class);
                startActivity(i);
            }
        }
        else
            Toast.makeText(this.requireActivity(), "You shouldn't have gotten here, Bad developer!",Toast.LENGTH_SHORT).show();
    }

    //on click listener for save body fat button
    @SuppressLint({"SetTextI18n", "ApplySharedPref"})
    public void setOnClickSaveAndDisplayBodyfat(View view){
        try {
            SharedPreferences.Editor editor;
            if (preference != null) {
                editor = preference.edit();

                editor.putString(StaticProfilePreferenceStrings.PROFILE_WAIST, ((EditText) requireView().findViewById(R.id.waistInput)).getText().toString());
                editor.putString(StaticProfilePreferenceStrings.PROFILE_NECK, ((EditText) requireView().findViewById(R.id.neckInput)).getText().toString());
                if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("female"))
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_HIP, ((EditText) requireView().findViewById(R.id.hipInput)).getText().toString());
                editor.apply();

                String result;

                if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("male")) {
                    result = personHandler.calculateBodyFatPercentage_Approximate(Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WAIST, null)),
                            Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_NECK, null)), Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null)));
                } else {
                    result = personHandler.calculateBodyFatPercentage_Approximate(Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WAIST, null)),
                            Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_NECK, null)), Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HIP, null)),
                            Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null)));
                }

                editor.putString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, result);
                editor.apply();

                ((TextView) requireView().findViewById(R.id.bodyFatDisplay1)).setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null));
            }
        } catch (NumberFormatException e){
            Toast.makeText(this.requireActivity(), getResources().getString(R.string.body_fat_measurements), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickReverseVisibility(View v){
        if(preference != null) {
            final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
            bodyFatLayout.setVisibility(View.GONE);

            if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("female"))
                this.requireActivity().findViewById(R.id.hipInputLayout).setVisibility(View.GONE);
            //disable the body fat calculation button
            v.findViewById(R.id.calculateBodyFat).setVisibility(View.VISIBLE);
            v.findViewById(R.id.calculateBodyFat).setEnabled(true);
        }
    }

    //on click listener for "calculate body fat" button
    public void setOnClickEnableBodyFatCalcs(View v){
        if(preference != null) {
            final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
            bodyFatLayout.setVisibility(View.VISIBLE);

            if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("female"))
                this.requireActivity().findViewById(R.id.hipInputLayout).setVisibility(View.VISIBLE);

            //disable the body fat calculation button
            v.findViewById(R.id.calculateBodyFat).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.calculateBodyFat).setEnabled(false);
        }
    }

    //on click method for edit text save buttons
    @SuppressLint("ApplySharedPref")
    public void onSaveClicked(@NonNull View v){
        if(preference != null) {
            if (v.getId() == R.id.save_edit_changes_height) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditHeight = requireActivity().findViewById(R.id.hiddenEditHeight);
                TextView profHeight = requireActivity().findViewById(R.id.profHeight);

                if (!(hiddenEditHeight.getText().toString().equals(""))) {
                    profHeight.setText(hiddenEditHeight.getText().toString());
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, (hiddenEditHeight.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_weight) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditWeight = requireActivity().findViewById(R.id.hiddenEditWeight);
                TextView profWeight = requireActivity().findViewById(R.id.profWeight);

                if (!(hiddenEditWeight.getText().toString().equals(""))) {
                    profWeight.setText(hiddenEditWeight.getText().toString());
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, (hiddenEditWeight.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_age) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditAge = requireActivity().findViewById(R.id.hiddenEditAge);
                TextView profAge = requireActivity().findViewById(R.id.profAge);

                if (!(hiddenEditAge.getText().toString().equals(""))) {
                    profAge.setText(hiddenEditAge.getText().toString());
                    editor.putInt(StaticProfilePreferenceStrings.PROFILE_AGE, Integer.parseInt(hiddenEditAge.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_name) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditName = requireActivity().findViewById(R.id.hiddenEditName);
                TextView profName = requireActivity().findViewById(R.id.profName);

                if (!hiddenEditName.getText().toString().equals("")) {
                    profName.setText(hiddenEditName.getText().toString());
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_NAME, hiddenEditName.getText().toString());
                }
                editor.apply();
                switchViewSwitchers(v);
            }
        }
    }

    //on click listener for text fields
    public void onTextClicked(View v){
        ViewSwitcher tempSwitcher = returnViewSwitcherById(v);
        if(tempSwitcher != null)
            tempSwitcher.showNext();
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
    /* HELPER METHODS END */
}