package com.example.fitnessappformyself;

import static android.content.Context.MODE_PRIVATE;

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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    @NonNull
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
            SharedPreferences deletePref = this.requireActivity().getSharedPreferences("Profiles1",Context.MODE_PRIVATE);
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
            SharedPreferences preference = this.requireActivity().getSharedPreferences("Profiles1", MODE_PRIVATE);
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("waist1", ((EditText) requireView().findViewById(R.id.waistInput)).getText().toString());
            editor.putString("neck1", ((EditText) requireView().findViewById(R.id.neckInput)).getText().toString());
            if (preference.getString("gender1", null).equals("female"))
                editor.putString("hip1", ((EditText) requireView().findViewById(R.id.hipInput)).getText().toString());
            editor.apply();

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
            editor.apply();

            ((TextView) requireView().findViewById(R.id.bodyFatDisplay1)).setText(preference.getString("bodyfat1", null));

            setProgressBarForBodyFat();
        } catch (NumberFormatException e){
            Toast.makeText(this.requireActivity(), "Body measures must be integers (waist, neck, hip).s", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickReverseVisibility(View v){
        SharedPreferences preference = this.requireActivity().getSharedPreferences("Profiles1", MODE_PRIVATE);
        final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
        bodyFatLayout.setVisibility(View.GONE);
        //
        if(preference.getString("gender1",null).equals("female"))
            this.requireActivity().findViewById(R.id.hipInputLayout).setVisibility(View.GONE);
        //disable the body fat calculation button
        calculateBodyFat.setVisibility(View.VISIBLE);
        calculateBodyFat.setEnabled(true);
    }

    //on click listener for "calculate body fat" button
    public void setOnClickEnableBodyFatCalcs(View v){
        SharedPreferences preference = this.requireActivity().getSharedPreferences("Profiles1", MODE_PRIVATE);
        final ConstraintLayout bodyFatLayout = this.requireActivity().findViewById(R.id.bodyFatCalcLayout);
        bodyFatLayout.setVisibility(View.VISIBLE);
        //
        if(preference.getString("gender1",null).equals("female"))
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
        if(v.getId() == R.id.save_edit_changes_height){
            SharedPreferences preferences = this.requireActivity().getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(!(hiddenEditHeight.getText().toString().equals(""))){
                profHeight.setText(hiddenEditHeight.getText().toString());
                editor.putString("height1",(hiddenEditHeight.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_weight){
            SharedPreferences preferences = this.requireActivity().getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(!(hiddenEditWeight.getText().toString().equals(""))){
                profWeight.setText(hiddenEditWeight.getText().toString());
                editor.putString("weight1",(hiddenEditWeight.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_age){
            SharedPreferences preferences = this.requireActivity().getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(!(hiddenEditAge.getText().toString().equals(""))){
                profAge.setText(hiddenEditAge.getText().toString());
                editor.putInt("age1",Integer.parseInt(hiddenEditAge.getText().toString()));
            }
            editor.apply();
            switchViewSwitchers(v);
        }else if(v.getId() == R.id.save_edit_changes_name){
            SharedPreferences preferences = this.requireActivity().getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if(!hiddenEditName.getText().toString().equals("")){
                profName.setText(hiddenEditName.getText().toString());
                editor.putString("name1",hiddenEditName.getText().toString());
            }
            editor.apply();
            switchViewSwitchers(v);
        }

    }
    // ON CLICK METHODS END //

    //HELPER METHODS
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

    public String formatDecimalPlaces(double d){
        DecimalFormat df = new DecimalFormat("#.##");//this will be used to round the double value
        df.setRoundingMode(RoundingMode.CEILING);// with more than 2 decimal places to 2 decimal places
        return df.format(d);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setProgressBarForBodyFat(){
        SharedPreferences preference = requireActivity().getSharedPreferences("Profiles1", MODE_PRIVATE);
        NumberFormat nf = NumberFormat.getInstance();
        double bodyFat = 0;
        try {
            bodyFat = Objects.requireNonNull(nf.parse(preference.getString("bodyfat1", null))).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        bodyFatProgressBar.setVisibility(View.VISIBLE);

        if(preference.getString("gender1",null).equals("male")){
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
        else if(preference.getString("gender1",null).equals("female")){
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
    //HELPER METHODS END//

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

            SharedPreferences preferences = context.getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            profName.setText(preferences.getString("name1", null));
            profAge.setText(Integer.toString(preferences.getInt("age1", 0)));
            profWeight.setText(preferences.getString("weight1", null));
            profHeight.setText(preferences.getString("height1", null));
            profBMRLast.setText(preferences.getString("BMR1", null));
            profCaloriesBurnedLast.setText(preferences.getString("calories1", null));
            profActivityLevelLast.setText(preferences.getString("activity1", null));
            bodyFatDisplay1.setText(preferences.getString("bodyfat1", null));
        }

        public void setProfilePicture(@NonNull View rootView){
            ImageView profileAvatar = rootView.findViewById(R.id.profileAvatar);
            SharedPreferences preferences = context.getSharedPreferences("Profiles1",Context.MODE_PRIVATE);
            if(preferences.contains("avatarLink")) {
                switch (preferences.getString("avatarLink", null)) {
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
            ProgressBar activityLevelProgressBar = rootView.findViewById(R.id.activityProgressBarOne);
            SharedPreferences preferences = requireActivity().getSharedPreferences("Profiles1", Context.MODE_PRIVATE);
            if(preferences.getString("activity1",null) != null) {
                if (Double.parseDouble(preferences.getString("activity1", null)) == 1.2) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progres_bar_gradient_20percent));
                    activityLevelProgressBar.setProgress(20);
                } else if (Double.parseDouble(preferences.getString("activity1", null)) == 1.375) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_37percent));
                    activityLevelProgressBar.setProgress(37);
                } else if (Double.parseDouble(preferences.getString("activity1", null)) == 1.55) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_55percent));
                    activityLevelProgressBar.setProgress(55);
                } else if (Double.parseDouble(preferences.getString("activity1", null)) == 1.725) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_72percent));
                    activityLevelProgressBar.setProgress(72);
                } else if (Double.parseDouble(preferences.getString("activity1", null)) == 1.9) {
                    activityLevelProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_gradient_90percent));
                    activityLevelProgressBar.setProgress(90);
                } else
                    activityLevelProgressBar.setProgress(0);
            }
        }
    }
}