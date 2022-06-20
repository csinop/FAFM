package com.example.fitnessappformyself.main_menu_fragments.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.fitnessappformyself.MainActivity;
import com.example.fitnessappformyself.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    private final PersonHandler personHandler = PersonHandler.getInstance();
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
        View link = inflater.inflate(R.layout.fragment_profile, container, false);
        /* start background thread
         to do the heavy work */
        ProfileAsync profileAsync = new ProfileAsync(this);
        //profileAsync.execute();

        return link;
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
        view.findViewById(R.id.bodyFatCalcCancel).setOnClickListener(this::onClickReverseVisibility);
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
                SharedPreferences.Editor editor = preference.edit();
                editor.clear();
                editor.apply();
                //
                editor1.putString("exists", "no");
                editor1.apply();
                //return to home screen
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

                setProgressBarForBodyFat(view);
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

                EditText hiddenEditHeight = v.findViewById(R.id.hiddenEditHeight);
                TextView profHeight = v.findViewById(R.id.profHeight);

                if (!(hiddenEditHeight.getText().toString().equals(""))) {
                    profHeight.setText(hiddenEditHeight.getText().toString());
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, (hiddenEditHeight.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_weight) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditWeight = v.findViewById(R.id.hiddenEditWeight);
                TextView profWeight = v.findViewById(R.id.profWeight);

                if (!(hiddenEditWeight.getText().toString().equals(""))) {
                    profWeight.setText(hiddenEditWeight.getText().toString());
                    editor.putString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, (hiddenEditWeight.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_age) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditAge = v.findViewById(R.id.hiddenEditAge);
                TextView profAge = v.findViewById(R.id.profAge);

                if (!(hiddenEditAge.getText().toString().equals(""))) {
                    profAge.setText(hiddenEditAge.getText().toString());
                    editor.putInt(StaticProfilePreferenceStrings.PROFILE_AGE, Integer.parseInt(hiddenEditAge.getText().toString()));
                }
                editor.apply();
                switchViewSwitchers(v);
            } else if (v.getId() == R.id.save_edit_changes_name) {
                SharedPreferences.Editor editor = preference.edit();

                EditText hiddenEditName = v.findViewById(R.id.hiddenEditName);
                TextView profName = v.findViewById(R.id.profName);

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

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setProgressBarForBodyFat(View view){
        if(preference != null) {
            NumberFormat nf = NumberFormat.getInstance();
            double bodyFat = 0;
            try {
                bodyFat = Objects.requireNonNull(nf.parse(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null))).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ProgressBar bodyFatProgressBar = view.findViewById(R.id.bodyFatProgressBarOne);
            bodyFatProgressBar.setVisibility(View.VISIBLE);

            if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("male")) {
                if (bodyFat <= 5.0) {
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progres_bar_gradient_20percent), null));
                    bodyFatProgressBar.setProgress(10);
                } else if (bodyFat <= 14.0 && bodyFat > 5.0) {
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progres_bar_gradient_20percent), null));
                    bodyFatProgressBar.setProgress(25);
                } else if (bodyFat <= 18.0 && bodyFat > 14.0) {//athletes and fitness
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_37percent), null));
                    bodyFatProgressBar.setProgress(40);
                } else if (bodyFat <= 25.0 && bodyFat > 18.0) {//average
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_55percent), null));
                    bodyFatProgressBar.setProgress(55);
                } else if (bodyFat <= 35.0 && bodyFat > 25.0) {//obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_72percent), null));
                    bodyFatProgressBar.setProgress(70);
                } else if (bodyFat <= 40.0 && bodyFat > 35.0) {//morbidly obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_72percent), null));
                    bodyFatProgressBar.setProgress(85);
                } else if (bodyFat > 40.0) { //super obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_90percent), null));
                    bodyFatProgressBar.setProgress(99);
                } else
                    bodyFatProgressBar.setProgress(0);
            } else if (preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null).equals("female")) {
                if (bodyFat <= 10.0) {
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progres_bar_gradient_20percent), null));
                    bodyFatProgressBar.setProgress(10);
                } else if (bodyFat <= 15.0 && bodyFat > 10.0) {//essential body fat
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progres_bar_gradient_20percent), null));
                    bodyFatProgressBar.setProgress(25);
                } else if (bodyFat <= 25.0 && bodyFat > 15.0) {//athletes and fitness
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_37percent), null));
                    bodyFatProgressBar.setProgress(40);
                } else if (bodyFat <= 30.0 && bodyFat > 25.0) {//average
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_55percent), null));
                    bodyFatProgressBar.setProgress(55);
                } else if (bodyFat <= 40.0 && bodyFat > 30.0) {//obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_72percent), null));
                    bodyFatProgressBar.setProgress(70);
                } else if (bodyFat <= 45.0 && bodyFat > 40.0) {//morbidly obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_72percent), null));
                    bodyFatProgressBar.setProgress(85);
                } else if (bodyFat > 45.0) {//super obese
                    bodyFatProgressBar.setProgressDrawable(ResourcesCompat.getDrawable(getResources(), (R.drawable.progress_bar_gradient_90percent), null));
                    bodyFatProgressBar.setProgress(99);
                } else
                    bodyFatProgressBar.setProgress(0);
            }
        }
    }
    /* HELPER METHODS END */

    //runnable class
    public static class ProfileAsync extends AsyncTask<Void, Integer, Boolean> {

        private final WeakReference<ProfileFragment> activityReference;
        private final PersonHandler personHandler = PersonHandler.getInstance();

        ProfileAsync(ProfileFragment context){
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            fillPersonalInfo(activityReference.get());
            setProgressBarForActivityLevel(activityReference.get());
            setProfilePicture(activityReference.get());
        }

        //MY METHODS
        public void calculateBMR(ProfileFragment link){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            ProfileFragment calendar = activityReference.get();

            MasterKey masterKey = null;
            try {
                masterKey = new MasterKey.Builder(link.requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            SharedPreferences preference = null;
            if (masterKey != null) {
                try {
                    preference = EncryptedSharedPreferences.create(
                            calendar.requireActivity(),
                            preferenceKey,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            if(preference != null) {
                SharedPreferences.Editor editor = preference.edit();
                double weight = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, null));
                double height = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null));
                int age = Integer.parseInt(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE, null));
                String gender = preference.getString(StaticProfilePreferenceStrings.PROFILE_GENDER, null);

                String result = personHandler.calculateBMR_Approximate(weight, height, age, gender); /* round up to 2 decimal places */

                editor.putString(StaticProfilePreferenceStrings.PROFILE_BMR, result);
                editor.apply();
            }
        }

        public void calculateCaloriesBurned(ProfileFragment link){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;
            ProfileFragment calendar = activityReference.get();

            MasterKey masterKey = null;
            try {
                masterKey = new MasterKey.Builder(link.requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            SharedPreferences preference = null;
            if (masterKey != null) {
                try {
                    preference = EncryptedSharedPreferences.create(
                            calendar.requireActivity(),
                            preferenceKey,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            if(preference != null) {
                SharedPreferences.Editor editor = preference.edit();

                double activityLevel = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null));
                double BMR = Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR, null).replace(",", "."));

                String result = personHandler.calculateCaloriesBurned_Approximate(BMR, activityLevel); /* round up to 2 decimal places */

                editor.putString(StaticProfilePreferenceStrings.PROFILE_CALORIES, result);
                editor.apply();
            }
        }

        @SuppressLint("SetTextI18n")
        public void fillPersonalInfo(ProfileFragment link){
            TextView profName = link.requireActivity().findViewById(R.id.profName);
            TextView profAge = link.requireActivity().findViewById(R.id.profAge);
            TextView profWeight = link.requireActivity().findViewById(R.id.profWeight);
            TextView profHeight = link.requireActivity().findViewById(R.id.profHeight);
            TextView profBMRLast = link.requireActivity().findViewById(R.id.profBMRLast);
            TextView profCaloriesBurnedLast = link.requireActivity().findViewById(R.id.profCaloriesBurnedLast);
            TextView profActivityLevelLast = link.requireActivity().findViewById(R.id.profActivityLevelLast);
            TextView bodyFatDisplay1 = link.requireActivity().findViewById(R.id.bodyFatDisplay1);


            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

            MasterKey masterKey = null;
            try {
                masterKey = new MasterKey.Builder(link.requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            SharedPreferences preference = null;
            if (masterKey != null) {
                try {
                    preference = EncryptedSharedPreferences.create(
                            link.requireActivity(),
                            preferenceKey,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            if(preference != null) {
                profName.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_NAME, null));
                profAge.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_AGE, null));
                profWeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_WEIGHT, null));
                profHeight.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_HEIGHT, null));
                calculateBMR(link);
                profBMRLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BMR, null));
                calculateCaloriesBurned(link);
                profCaloriesBurnedLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_CALORIES, null));
                profActivityLevelLast.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null));
                bodyFatDisplay1.setText(preference.getString(StaticProfilePreferenceStrings.PROFILE_BODYFAT, null));
            }
        }

        public void setProfilePicture(@NonNull ProfileFragment link){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

            MasterKey masterKey = null;
            try {
                masterKey = new MasterKey.Builder(link.requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            SharedPreferences preference = null;
            if (masterKey != null) {
                try {
                    preference = EncryptedSharedPreferences.create(
                            link.requireActivity(),
                            preferenceKey,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            if(preference != null) {
                ImageView profileAvatar = link.requireActivity().findViewById(R.id.profileAvatar);

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
                            Toast.makeText(link.requireActivity(), "Bad Developer!", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setProgressBarForActivityLevel(@NonNull ProfileFragment link){
            String preferenceKey = StaticProfilePreferenceStrings.SHARED_PREFERENCE_PROFILE_KEY;

            MasterKey masterKey = null;
            try {
                masterKey = new MasterKey.Builder(link.requireActivity(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
            SharedPreferences preference = null;
            if (masterKey != null) {
                try {
                    preference = EncryptedSharedPreferences.create(
                            link.requireActivity(),
                            preferenceKey,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }

            if(preference != null) {
                ProgressBar activityLevelProgressBar = link.requireActivity().findViewById(R.id.activityProgressBarOne);
                if (preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null) != null) {
                    if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.2) {
                        ((ProgressBar) link.requireActivity().findViewById(R.id.bodyFatProgressBarOne)).setProgressDrawable(ResourcesCompat.getDrawable(link.requireActivity().getResources(), (R.drawable.progres_bar_gradient_20percent), null));
                        activityLevelProgressBar.setProgress(20);
                    } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.375) {
                        ((ProgressBar) link.requireActivity().findViewById(R.id.bodyFatProgressBarOne)).setProgressDrawable(ResourcesCompat.getDrawable(link.requireActivity().getResources(), (R.drawable.progress_bar_gradient_37percent), null));
                        activityLevelProgressBar.setProgress(37);
                    } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.55) {
                        ((ProgressBar) link.requireActivity().findViewById(R.id.bodyFatProgressBarOne)).setProgressDrawable(ResourcesCompat.getDrawable(link.requireActivity().getResources(), (R.drawable.progress_bar_gradient_55percent), null));
                        activityLevelProgressBar.setProgress(55);
                    } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.725) {
                        ((ProgressBar) link.requireActivity().findViewById(R.id.bodyFatProgressBarOne)).setProgressDrawable(ResourcesCompat.getDrawable(link.requireActivity().getResources(), (R.drawable.progress_bar_gradient_72percent), null));
                        activityLevelProgressBar.setProgress(72);
                    } else if (Double.parseDouble(preference.getString(StaticProfilePreferenceStrings.PROFILE_ACTIVITY, null)) == 1.9) {
                        ((ProgressBar) link.requireActivity().findViewById(R.id.bodyFatProgressBarOne)).setProgressDrawable(ResourcesCompat.getDrawable(link.requireActivity().getResources(), (R.drawable.progress_bar_gradient_90percent), null));
                        activityLevelProgressBar.setProgress(90);
                    } else
                        activityLevelProgressBar.setProgress(0);
                }
            }
        }
    }
}