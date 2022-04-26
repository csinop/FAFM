package com.example.fitnessappformyself;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    @NonNull
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //
        TableLayout workoutOfTodayDisplay = view.findViewById(R.id.workoutOfTodayDisplay);
        createCalendarString(getToday(),workoutOfTodayDisplay);

        ////
        TableLayout relativeLayoutM = view.findViewById(R.id.mondayLayout);
        TableLayout relativeLayoutT = view.findViewById(R.id.tuesdayLayout);
        TableLayout relativeLayoutW = view.findViewById(R.id.wednesdayLayout);
        TableLayout relativeLayoutTh = view.findViewById(R.id.thursdayLayout);
        TableLayout relativeLayoutF = view.findViewById(R.id.fridayLayout);
        TableLayout relativeLayoutSt = view.findViewById(R.id.saturdayLayout);
        TableLayout relativeLayoutS = view.findViewById(R.id.sundayLayout);
        //
        createCalendarString("Monday",relativeLayoutM);
        createCalendarString("Tuesday",relativeLayoutT);
        createCalendarString("Wednesday",relativeLayoutW);
        createCalendarString("Thursday",relativeLayoutTh);
        createCalendarString("Friday",relativeLayoutF);
        createCalendarString("Saturday",relativeLayoutSt);
        createCalendarString("Sunday",relativeLayoutS);

        //set onClick listeners
        setCalendarListeners();
    }
    //MY METHODS//
    public String adjustForLanguageDifferences(@NonNull String day){
        switch (day) {
            case "Pazartesi":
                return "Monday";
            case "Salı":
                return "Tuesday";
            case "Çarşamba":
                return "Wednesday";
            case "Perşembe":
                return "Thursday";
            case "Cuma":
                return "Friday";
            case "Cumartesi":
                return "Saturday";
            case "Pazar":
                return "Sunday";
            default:
                return day;
        }
    }
    @SuppressLint("SimpleDateFormat")
    public String getToday(){
        Date now = new Date();
        return (adjustForLanguageDifferences(new SimpleDateFormat("EEEE").format(now)));
    }
    public void createCalendarString(String day, TableLayout tableLayout){
        SharedPreferences pref = this.requireActivity().getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        if(pref.contains("exercise0")) {
            for (int i = 0; i < pref.getInt("move_count", 0); i++) {
                if (pref.getString("exercise" + i, null) != null) {
                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(lp);
                    TextView tv = new TextView(getContext());
                    String tempString = "\n" + pref.getString("exercise" + i, null);
                    tv.setText(tempString);
                    row.addView(tv);
                    tableLayout.addView(row);
                }
            }
        }
        else{
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(lp);
            TextView tv = new TextView(getContext());
            tv.setText(getResources().getString(R.string.off_day));
            row.addView(tv);
            tableLayout.addView(row);
        }
    }

    // on click functionality methods start
    public void setCalendarListeners(){
        setMondayOnClickListener();
        setTuesdayOnClickListener();
        setWednesdayOnClickListener();
        setThursdayOnClickListener();
        setFridayOnClickListener();
        setSaturdayOnClickListener();
        setSundayOnClickListener();
        setWorkoutOfTodayOnClickListener();
    }

    //calendar on click functionality methods
    public void setMondayOnClickListener(){
        requireView().findViewById(R.id.mainMenuMonday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Monday");
            startActivity(intent);
        });
    }
    public void setTuesdayOnClickListener(){
        requireView().findViewById(R.id.mainMenuTuesday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Tuesday");
            startActivity(intent);
        });
    }
    public void setWednesdayOnClickListener(){
        requireView().findViewById(R.id.mainMenuWednesday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Wednesday");
            startActivity(intent);
        });
    }
    public void setThursdayOnClickListener(){
        requireView().findViewById(R.id.mainMenuThursday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Thursday");
            startActivity(intent);
        });
    }
    public void setFridayOnClickListener(){
        requireView().findViewById(R.id.mainMenuFriday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Friday");
            startActivity(intent);
        });
    }
    public void setSaturdayOnClickListener(){
        requireView().findViewById(R.id.mainMenuSaturday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Saturday");
            startActivity(intent);
        });
    }
    public void setSundayOnClickListener(){
        requireView().findViewById(R.id.mainMenuSunday).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday","Sunday");
            startActivity(intent);
        });
    }

    public void setWorkoutOfTodayOnClickListener(){
        requireView().findViewById(R.id.workoutOfTodayTitle).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutOfTheDayActivity.class);
            startActivity(intent);
        });
    }
}