package com.example.fitnessappformyself.main_menu_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.fitnessappformyself.file_explorer.FileExplorerActivity;
import com.example.fitnessappformyself.R;
import com.example.fitnessappformyself.WeeklyPlanActivity;
import com.example.fitnessappformyself.WorkoutOfTheDayActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarFragment extends Fragment {

    public CalendarFragment() {
        // Required empty public constructor
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

        createCalendarString("Monday",view.findViewById(R.id.mondayLayout));
        createCalendarString("Tuesday",view.findViewById(R.id.tuesdayLayout));
        createCalendarString("Wednesday",view.findViewById(R.id.wednesdayLayout));
        createCalendarString("Thursday",view.findViewById(R.id.thursdayLayout));
        createCalendarString("Friday",view.findViewById(R.id.fridayLayout));
        createCalendarString("Saturday",view.findViewById(R.id.saturdayLayout));
        createCalendarString("Sunday",view.findViewById(R.id.sundayLayout));

        //set export button on click listener
        view.findViewById(R.id.export_button).setOnClickListener(v -> onClickExport());
        view.findViewById(R.id.import_button).setOnClickListener(v -> onClickImport());
        //set onClick listeners
        setCalendarListeners();
    }

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
    @SuppressLint("UseCompatLoadingForDrawables")
    public void createCalendarString(String day, TableLayout tableLayout){
        SharedPreferences pref = this.requireActivity().getSharedPreferences(day+"WorkoutPlan", Context.MODE_PRIVATE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 10);

        /* if there is even a single exercise that day */
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
        setWeekdayOnClickListener(R.id.mainMenuMonday, "Monday");
        setWeekdayOnClickListener(R.id.mainMenuTuesday, "Tuesday");
        setWeekdayOnClickListener(R.id.mainMenuWednesday, "Wednesday");
        setWeekdayOnClickListener(R.id.mainMenuThursday, "Thursday");
        setWeekdayOnClickListener(R.id.mainMenuFriday, "Friday");
        setWeekdayOnClickListener(R.id.mainMenuSaturday, "Saturday");
        setWeekdayOnClickListener(R.id.mainMenuSunday, "Sunday");
        setWorkoutOfTodayOnClickListener();
    }

    //calendar on click functionality methods
    public void setWeekdayOnClickListener(int weekdayButtonId, String weekday){
        requireView().findViewById(weekdayButtonId).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WeeklyPlanActivity.class);
            intent.putExtra("weekday",weekday);
            startActivity(intent);
        });
    }

    public void setWorkoutOfTodayOnClickListener(){
        requireView().findViewById(R.id.workoutOfTodayTitle).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutOfTheDayActivity.class);
            startActivity(intent);
        });
    }

    public void onClickExport(){
        ExportAsync exporter = new ExportAsync(this);
        //passing current context as the parameter here
        exporter.execute();
    }

    public void onClickImport(){
        Intent explorerIntent = new Intent(getActivity(), FileExplorerActivity.class);
        startActivity(explorerIntent);
    }
    // on click functionality methods end




    //this class is a custom format
    public static class FileFormat {
        private final String title;
        private final String spacing;
        private final ArrayList<String> subGroupList;

        //text to be written into the file
        private final String finalString;

        public FileFormat(String title, String spacing, ArrayList<String> subGroupList){
            this.title = title;
            this.spacing = spacing;
            this.subGroupList = subGroupList;

            this.finalString = buildFileStringFromFields();
        }

        public String buildFileStringFromFields(){
            StringBuilder finalString = new StringBuilder(title).append(spacing);

            for(int i = 0; i < subGroupList.size(); i++){
                finalString.append("\n");
                finalString.append(subGroupList.get(i)).append(spacing);
                finalString.append("\n");
            }
            finalString.append(spacing);

            return finalString.toString();
        }

        //getters
        public String getFinalString() {
            return finalString;
        }
    }

    //runnable class for export functionality
    public static class ExportAsync extends AsyncTask<Void, Integer, Boolean> {

        //to prevent memory leak
        private final WeakReference<CalendarFragment> activityReference;


        ExportAsync(CalendarFragment context){ activityReference = new WeakReference<>(context); }

        @Override
        protected Boolean doInBackground(Void... voids) {
            exportWorkout();
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            CalendarFragment calendar = activityReference.get();
            if(calendar == null) return;

            ProgressBar eProgressBar = calendar.requireActivity().findViewById(R.id.export_progress_bar);
            eProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            CalendarFragment calendar = activityReference.get();
            if(calendar == null) return;

            ProgressBar export_progress_bar = calendar.requireActivity().findViewById(R.id.export_progress_bar);
            export_progress_bar.setVisibility(View.INVISIBLE);
        }

        // export functionality methods start
        //body part string builder methods start
        public String buildBodyPartGroupString(){
            CalendarFragment calendar = activityReference.get();
            if(calendar == null) return null;

            String[] weekDays = calendar.requireActivity().getResources().getStringArray(R.array.WeekDays);

            StringBuilder someBuilder = new StringBuilder();

            //For each day of the week
            for (String weekDay : weekDays) {
                SharedPreferences workoutPref = calendar.requireActivity().getSharedPreferences(weekDay+"WorkoutPlan", Context.MODE_PRIVATE);
                someBuilder.append(weekDay).append(";");
                // append what to train that day
                for (int j = 0; j < workoutPref.getInt("move_count", 0); j++) {
                    //if that file exists
                    if (workoutPref.getString("exercise" + j, null) != null) {
                        //append what to train
                        someBuilder.append(workoutPref.getString("exercise" + j, null)).append(",");
                    }
                }
                someBuilder.append("\n");
            }
            return someBuilder.toString();
        }
        //body part string builder methods end

        //moves string builder methods start
        public String buildMovesGroupString(){
            CalendarFragment calendar = activityReference.get();
            if(calendar == null) return null;

            String[] movesString = calendar.requireActivity().getResources().getStringArray(R.array.bodyParts);

            StringBuilder someBuilder = new StringBuilder();
            for (String s : movesString) {
                someBuilder.append(s).append(";");
                SharedPreferences movePref = calendar.requireActivity().getSharedPreferences(s + "WorkoutSet", Context.MODE_PRIVATE);
                for (int j = 0; j < movePref.getInt("move_count", 0); j++) {
                    someBuilder.append(movePref.getString("exercise" + j, null)).append(",");
                    someBuilder.append(movePref.getString("set" + j, null)).append(",");
                    someBuilder.append(movePref.getString("rep" + j, null)).append(",");
                }
                someBuilder.append("\n\n");
            }
            return someBuilder.toString();
        }
        //move string builder methods end

        //this methods combines subGroups
        public ArrayList<String> combineGroups(){
            ArrayList<String> subGroupList = new ArrayList<>();

            subGroupList.add(buildBodyPartGroupString());
            subGroupList.add(buildMovesGroupString());

            return subGroupList;
        }

        public void exportWorkout(){
            FileFormat newFormat = new FileFormat("Workout:", "\n", combineGroups());
            CalendarFragment calendar = activityReference.get();
            if(calendar != null) {
                if (checkStorageState()) {
                    //Create a new file that points to the DOCUMENTS directory
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "workout.txt");
                    FileOutputStream newStream;
                    try {
                        if (file.exists())
                            if (!file.delete())
                                Log.v("information", "Unable to delete file.");
                        if (file.createNewFile()) {
                            newStream = new FileOutputStream(file, false);
                            String stringToWrite = newFormat.getFinalString();
                        /* if this string is empty,
                        then that day is an off day */
                            if (stringToWrite.isEmpty()) {
                                String temp = "Off day.";
                                newStream.write(temp.getBytes());
                            }
                            newStream.write(stringToWrite.getBytes());
                            newStream.flush();
                            newStream.close();
                            //Toast.makeText(calendar.requireActivity(), "File has been saved to your Documents folder", Toast.LENGTH_SHORT).show();
                            Log.v("information", "Export successful");
                        } else {
                            Log.v("information", "Unable to create new file");
                        }
                    } catch (IOException e) {
                        Log.v("information", "Unable to export");
                        e.printStackTrace();
                    }
                }
            }
        }
        // export functionality methods end

        public boolean checkStorageState(){
            //Checking the availability state of the External Storage.
            String state = Environment.getExternalStorageState();
            //If it isn't mounted - we can't write into it.
            return Environment.MEDIA_MOUNTED.equals(state);
        }
    }
}
