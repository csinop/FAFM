package com.example.fitnessappformyself.main_menu_fragments.profile;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/* singleton access */
public class PersonHandler {
    private static volatile PersonHandler instance;

    private PersonHandler(){ }

    public static PersonHandler getInstance(){
        if(instance == null){
            instance = new PersonHandler();
        }
        return instance;
    }

    /* methods */
    public String calculateBMR_Approximate(double weight, double height, int age, @NonNull String gender){ //this calculates an approximate
        //first we need to convert metric values to imperial values
        weight = (weight*2.2046); //kg -> lbs
        height = (height*0.39370); //cm -> inches

        //Mifflin-St Jeor Equation
        if(gender.equals("female")) {
            return formatDecimalPlaces((10.0 * weight) + (6.25 * height) - (5.0 * age) - 161); //BMR for females
        }
        else if(gender.equals("male")) {
            return formatDecimalPlaces((10.0 * weight) + (6.25 * height) - (5.0 * age) + 5); //BMR for males
        }else{
            return "-0.1";
        }
    }

    /* for males */
    public String calculateBodyFatPercentage_Approximate(Double waist, Double neck, Double height){
        //uses U.S Navy Method for measuring body fat
        //uses metric units
        //for males
        //result is an estimate, don't take it at face value
        return formatDecimalPlaces(( 495.0 / (1.0324 - ( 0.19077*Math.log10( waist - neck )) + (0.15456 * Math.log10( height )))) - 450);
    }

    /* for females */
    public String calculateBodyFatPercentage_Approximate(Double waist, Double neck, Double hip, Double height){
        //uses U.S Navy Method for measuring body fat
        //uses metric units
        //for females
        //result is an estimate, don't take it at face value
        return formatDecimalPlaces(( 495.0 / ( 1.29579 - (0.35004*Math.log10( waist + hip - neck )) + (0.22100 * Math.log10( height )))) - 450);
    }

    public String calculateCaloriesBurned_Approximate(double BMR, double activityLevel){
        return formatDecimalPlaces(BMR*activityLevel);
    }

    public String formatDecimalPlaces(double d){
        DecimalFormat df = new DecimalFormat("#.##");//this will be used to round the double value
        df.setRoundingMode(RoundingMode.CEILING);// with more than 2 decimal places to 2 decimal places
        return df.format(d);
    }

    public void saveStringToSharedPreferences(SharedPreferences whereToSave, String sharedPreferenceKey, String whatToSave){
        SharedPreferences.Editor editor = whereToSave.edit();
        editor.putString(sharedPreferenceKey, whatToSave);
        editor.apply();
    }
}
