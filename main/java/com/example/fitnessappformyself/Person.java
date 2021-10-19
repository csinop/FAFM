package com.example.fitnessappformyself;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class Person implements Parcelable {
    private String Name;
    private int age;
    private double weight;
    private double height;
    private String gender;
    private double BMR;
    private double activityLevel;
    private double caloriesBurned;
    private ImageView avatar;
    private double bodyFat;
    // empty constructor
    public Person(){ }

    protected Person(Parcel in){
        Name = in.readString();
        age = in.readInt();
        weight = in.readDouble();
        height = in.readDouble();
        gender = in.readString();
        BMR = in.readDouble();
        activityLevel = in.readDouble();
        caloriesBurned = in.readDouble();
        bodyFat = in.readDouble();
    }
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeInt(age);
        parcel.writeDouble(weight);
        parcel.writeDouble(height);
        parcel.writeString(gender);
        parcel.writeDouble(BMR);
        parcel.writeDouble(activityLevel);
        parcel.writeDouble(caloriesBurned);
        parcel.writeDouble(bodyFat);
    }
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>(){
        public Person createFromParcel(Parcel in){
            return new Person(in);
        }
        public Person[] newArray(int size){
            return new Person[size];
        }
    };
    //
    public String displayPersonInfo(){
        String holder = "Name: " + getName() + ", Age " + getAge() + ", Height: " + getHeight() + ", Weight: " + getWeight()
                + ", Gender: " + getGender() + ", Calories Burned: " + getCaloriesBurned() + ", Body Fat: " +getBodyFat();
        return holder;
    }
    public void calculateBMR(double weight, double height, int age, String gender){ //this calculates an approximate
        //first we need to convert metric values to imperial values, because 'Murica
        weight = (weight*2.2046); //kg -> lbs
        height = (height*0.39370); //cm -> inches

        //Mifflin-St Jeor Equation
        if(gender.equals("female"))
            setBMR( (10.0 * weight) + (6.25 * height) - (5.0 * age) - 161 ); //BMR for females
        else if(gender.equals("male"))
            setBMR( (10.0 * weight) + (6.25 * height) - (5.0 * age) + 5 ); //BMR for males
    }
    public double measureBodyFatPercentageMale(Double waist, Double neck, Double height){
        //uses U.S Navy Method for measuring body fat
        //uses metric units
        //for males
        //result is an estimate, don't take it at face value
        return ( 495.0 / (1.0324 - ( 0.19077*Math.log10( waist - neck )) + (0.15456 * Math.log10( height )))) - 450;
    }
    public double measureBodyFatPercentageFemale(Double waist, Double neck, Double hip, Double height){
        //uses U.S Navy Method for measuring body fat
        //uses metric units
        //for females
        //result is an estimate, don't take it at face value
        return ( 495.0 / ( 1.29579 - (0.35004*Math.log10( waist + hip - neck )) + (0.22100 * Math.log10( height )))) - 450;
    }
    //getters and setters
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) { this.height = height; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public double getBMR() { return BMR; }

    public void setBMR(double BMR) { this.BMR = BMR; }

    public double getActivityLevel() { return activityLevel; }

    public void setActivityLevel(double activityLevel) { this.activityLevel = activityLevel; }

    public double getCaloriesBurned() { return caloriesBurned; }

    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public ImageView getAvatar() { return avatar; }

    public void setAvatar(ImageView avatar) { this.avatar = avatar; }

    public double getBodyFat() { return bodyFat; }

    public void setBodyFat(double bodyFat) { this.bodyFat = bodyFat; }
}
