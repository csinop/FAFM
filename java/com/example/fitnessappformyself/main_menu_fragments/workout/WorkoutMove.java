package com.example.fitnessappformyself.main_menu_fragments.workout;

public class WorkoutMove {
    private String workout_name;
    private String rep_count;
    private String set_count;

    public WorkoutMove(String workout_name, String rep_count, String set_count){
        this.workout_name = workout_name;
        this.rep_count = rep_count;
        this.set_count = set_count;
    }

    //getters and setters
    public String getWorkout_name() {
        return workout_name;
    }

    public void setWorkout_name(String workout_name) {
        this.workout_name = workout_name;
    }

    public String getRep_count() {
        return rep_count;
    }

    public void setRep_count(String rep_count) {
        this.rep_count = rep_count;
    }

    public String getSet_count() {
        return set_count;
    }

    public void setSet_count(String set_count) {
        this.set_count = set_count;
    }
}
