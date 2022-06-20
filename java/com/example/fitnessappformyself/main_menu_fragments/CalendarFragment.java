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
import android.widget.Toast;

import com.example.fitnessappformyself.file_explorer.FileExplorerActivity;
import com.example.fitnessappformyself.R;
import com.example.fitnessappformyself.WeeklyPlanActivity;
import com.example.fitnessappformyself.WorkoutOfTheDayActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalendarFragment extends Fragment {
    private String resultOfImport;
    private String resultOfExport;

    public void setResultOfImport(String resultOfImport) { this.resultOfImport = resultOfImport; }
    public void setResultOfExport(String resultOfExport) { this.resultOfExport = resultOfExport; }

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
        initializeCalendarFragment(view);

        //set export button on click listener
        view.findViewById(R.id.export_button).setOnClickListener(v -> onClickExport());
        view.findViewById(R.id.import_button).setOnClickListener(v -> onClickImport());
        view.findViewById(R.id.pick_file_button).setOnClickListener(v -> onClickPickFile());
        //set onClick listeners
        setCalendarListeners();
    }

    public void initializeCalendarFragment(View view){
        TableLayout workoutOfTodayDisplay = view.findViewById(R.id.workoutOfTodayDisplay);
        createCalendarString(getToday(),workoutOfTodayDisplay);

        createCalendarString("Monday",view.findViewById(R.id.mondayLayout));
        createCalendarString("Tuesday",view.findViewById(R.id.tuesdayLayout));
        createCalendarString("Wednesday",view.findViewById(R.id.wednesdayLayout));
        createCalendarString("Thursday",view.findViewById(R.id.thursdayLayout));
        createCalendarString("Friday",view.findViewById(R.id.fridayLayout));
        createCalendarString("Saturday",view.findViewById(R.id.saturdayLayout));
        createCalendarString("Sunday",view.findViewById(R.id.sundayLayout));
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
        SharedPreferences pref = this.requireActivity().getSharedPreferences(day + StaticWorkoutPreferenceStrings.WORKOUT_PLAN, Context.MODE_PRIVATE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 10);

        /* if there is even a single exercise that day */
        if(pref.contains("exercise0")) {
            for (int i = 0; i < pref.getInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, 0); i++) {
                if (pref.getString(StaticWorkoutPreferenceStrings.EXERCISE + i, null) != null) {
                    TableRow row = new TableRow(getContext());
                    row.setLayoutParams(lp);
                    TextView tv = new TextView(getContext());

                    String tempString = "\n" + pref.getString(StaticWorkoutPreferenceStrings.EXERCISE + i, null);
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
            intent.putExtra(StaticWorkoutPreferenceStrings.WEEKDAY,weekday);
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
        if(exporter.getStatus() != AsyncTask.Status.RUNNING) {
            exporter.execute();
            Toast.makeText(requireActivity(), resultOfExport,Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(requireActivity(), getResources().getString(R.string.export_already_in_progress), Toast.LENGTH_SHORT).show();
    }

    public void onClickPickFile(){
        Intent explorerIntent = new Intent(getActivity(), FileExplorerActivity.class);
        startActivity(explorerIntent);
    }

    public String checkIntentFromFileExplorer(){
        Intent intent = requireActivity().getIntent();
        intent.getStringExtra("filePath");

        Bundle extras = intent.getExtras();

        if(extras != null){
            if(extras.containsKey("filePath")){
                return extras.getString("filePath", null);
            }
        }
        return "";
    }

    public void onClickImport(){
        String filePath = checkIntentFromFileExplorer();
        if(!filePath.equals("")) {
            ImportAsync importer = new ImportAsync(this, filePath);
            importer.execute();

            Toast.makeText(requireActivity(), resultOfImport, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(requireActivity(), R.string.pick_a_file_first, Toast.LENGTH_SHORT).show();
        }
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
            StringBuilder finalString = new StringBuilder();

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

        public String getTitle() {
            return title;
        }
    }


    public static class ImportAsync extends AsyncTask<Void, Integer, Boolean> {
        //to prevent memory leak
        private final WeakReference<CalendarFragment> fragmentReference;
        private final String filePath;
        private String resultOfImport;

        ImportAsync(CalendarFragment context, String filePath){
            fragmentReference = new WeakReference<>(context);
            this.filePath = filePath;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            this.resultOfImport = readFromFile();
            Log.d("import","importing");
            Log.d("import",filePath);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            fragmentReference.get().setResultOfImport(this.resultOfImport);

            /* refresh the activity, so the user can see the result of the import */
            fragmentReference.get().requireFragmentManager().beginTransaction().detach(fragmentReference.get()).attach(fragmentReference.get()).commit();
        }

        /* first word in the file must be Workout:,
        this to ensure that the users picks the right file */
        /* After that, first word in each line is a keyword
        that specifies where the following words belong */
        public String readFromFile(){
            CalendarFragment calendar = fragmentReference.get();
            File file = new File(filePath);
            if(file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String currentLine;
                    if (!file.getName().equals(StaticWorkoutPreferenceStrings.FILENAME)) {
                        Log.v("information", "wrong file");
                        return fragmentReference.get().getResources().getString(R.string.wrong_file);
                    }else{
                        while ((currentLine = br.readLine()) != null) {
                            /* since the order of week days is always the same,
                             we can just expect that the file will contain them */
                            /* we also need to split the strings by "," first and then by ";" second
                            we do this because that is how I designed the file format */
                            String[] split = currentLine.split(";");
                            /* now this array always contains two elements and the first one is always
                            the keyword that the content of the second string, which will be split again,
                            will be written to. Basically split[0] is the SharedPrefs key */

                            String keyword = split[0];
                            /* check for body parts here */
                            if (!keyword.equals("") && split.length > 1) {
                                SharedPreferences dayPref = calendar.requireActivity().getSharedPreferences(keyword + StaticWorkoutPreferenceStrings.WORKOUT_PLAN, Context.MODE_PRIVATE);
                                SharedPreferences.Editor workoutEditor = dayPref.edit();

                                String[] contentSplit = split[1].split(",");

                                for (int i = 0; i < contentSplit.length; i++) {
                                    workoutEditor.putString(StaticWorkoutPreferenceStrings.EXERCISE + i, contentSplit[i]);
                                }
                                workoutEditor.putInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, contentSplit.length);
                                workoutEditor.apply();
                            }

                            keyword = split[0];
                            Log.v("information", split[0]);
                            /* check for moves here */
                            if (!keyword.equals("") && split.length > 1) {
                                SharedPreferences workoutPref = calendar.requireActivity().getSharedPreferences(keyword + StaticWorkoutPreferenceStrings.WORKOUT_SET, Context.MODE_PRIVATE);
                                SharedPreferences.Editor workoutEditor = workoutPref.edit();

                                String[] contentSplit = split[1].split(",");

                                /* the length of the contentSplit must be at least 3,
                                otherwise something is wrong with the file */
                                if(contentSplit.length >= 3) {
                                    for (int i = 0; i < contentSplit.length; i += 3) {
                                        workoutEditor.putString(StaticWorkoutPreferenceStrings.EXERCISE + i, contentSplit[i]);
                                        workoutEditor.putString(StaticWorkoutPreferenceStrings.SET + i, contentSplit[i + 1]);
                                        workoutEditor.putString(StaticWorkoutPreferenceStrings.REP + i, contentSplit[i + 2]);
                                    }
                                }

                                workoutEditor.putInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, contentSplit.length / 3);
                                workoutEditor.apply();
                            }
                        }
                        Log.v("information", "Import successful");
                        return fragmentReference.get().getResources().getString(R.string.import_successful);
                    }
                } catch (IOException e) {
                    Log.v("information", "Unable to read the file");
                    e.printStackTrace();
                    return fragmentReference.get().getResources().getString(R.string.unable_to_read_the_file);
                }
            }else{
                return fragmentReference.get().getResources().getString(R.string.file_does_not_exist);
            }
        }
    }

    public static class ExportAsync extends AsyncTask<Void, Integer, Boolean> {

        //to prevent memory leak
        private final WeakReference<CalendarFragment> fragmentReference;
        private String resultOfExport;

        ExportAsync(CalendarFragment context){ fragmentReference = new WeakReference<>(context); }

        @Override
        protected Boolean doInBackground(Void... voids) {
            this.resultOfExport = exportWorkout();
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            CalendarFragment calendar = fragmentReference.get();
            if(calendar == null) return;

            ProgressBar eProgressBar = calendar.requireActivity().findViewById(R.id.export_progress_bar);
            eProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            CalendarFragment calendar = fragmentReference.get();
            if(calendar == null) return;

            ProgressBar export_progress_bar = calendar.requireActivity().findViewById(R.id.export_progress_bar);
            export_progress_bar.setVisibility(View.INVISIBLE);

            calendar.setResultOfExport(this.resultOfExport);
        }

        // export functionality methods start
        //body part string builder methods start
        public String buildBodyPartGroupString(){
            CalendarFragment calendar = fragmentReference.get();
            if(calendar == null) return null;

            String[] weekDays = calendar.requireActivity().getResources().getStringArray(R.array.WeekDays);

            StringBuilder someBuilder = new StringBuilder();

            //For each day of the week
            for (String weekDay : weekDays) {
                SharedPreferences workoutPref = calendar.requireActivity().getSharedPreferences(weekDay+StaticWorkoutPreferenceStrings.WORKOUT_PLAN, Context.MODE_PRIVATE);
                someBuilder.append(weekDay).append(";");
                // append what to train that day
                for (int j = 0; j < workoutPref.getInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, 0); j++) {
                    //if that file exists
                    String exercise = workoutPref.getString(StaticWorkoutPreferenceStrings.EXERCISE + j, null);
                    if (exercise != null) {
                        //append what to train
                        someBuilder.append(exercise).append(",");
                    }
                }
                someBuilder.append("\n");
            }
            return someBuilder.toString();
        }
        //body part string builder methods end

        //moves string builder methods start
        public String buildMovesGroupString(){
            CalendarFragment calendar = fragmentReference.get();
            if(calendar == null) return null;

            String[] movesString = calendar.requireActivity().getResources().getStringArray(R.array.bodyParts);

            StringBuilder someBuilder = new StringBuilder();
            for (String s : movesString) {
                someBuilder.append(s).append(";");
                SharedPreferences movePref = calendar.requireActivity().getSharedPreferences(s + StaticWorkoutPreferenceStrings.WORKOUT_SET, Context.MODE_PRIVATE);
                for (int j = 0; j < movePref.getInt(StaticWorkoutPreferenceStrings.MOVE_COUNT, 0); j++) {
                    someBuilder.append(movePref.getString(StaticWorkoutPreferenceStrings.EXERCISE + j, null)).append(",");
                    someBuilder.append(movePref.getString(StaticWorkoutPreferenceStrings.SET + j, null)).append(",");
                    someBuilder.append(movePref.getString(StaticWorkoutPreferenceStrings.REP + j, null)).append(",");
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

        public String exportWorkout(){
            FileFormat newFormat = new FileFormat("FAFMWorkout", "\n", combineGroups());
            CalendarFragment calendar = fragmentReference.get();
            if(calendar != null) {
                if (checkStorageState()) {
                    //Create a new file that points to the DOCUMENTS directory
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), newFormat.getTitle()+".txt");
                    FileOutputStream newStream;
                    try {
                        if (file.exists())
                            if (!file.delete()) {
                                Log.v("information", "Unable to delete file.");
                                return calendar.getResources().getString(R.string.unable_to_delete_file);
                            }
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
                            return calendar.getResources().getString(R.string.export_successful);
                        } else {
                            Log.v("information", "Unable to create new file");
                            return calendar.getResources().getString(R.string.unable_to_create_file);
                        }
                    } catch (IOException e) {
                        Log.v("information", "Unable to export");
                        e.printStackTrace();
                        return calendar.getResources().getString(R.string.export_failed);
                    }
                }
            }
            return "calendar is null, this is a bug.";
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