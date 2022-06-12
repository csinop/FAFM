package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileExplorerActivity extends AppCompatActivity {

    //permissions
    private static final int REQUEST_PERMISSIONS = 1242;
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSIONS_COUNT = 2;

    //arrays
    private boolean[] selected;
    private File[] files;

    //file paths and files
    private String rootPath;
    private String currentPath;
    private List<String> filesList;
    private String copyPath;

    //files
    private File dir;

    //Views
    private TextAdapter textAdapterOne;
    private TextView pathOutput;
    private ListView listView;

    //flags
    private boolean isManagerInitialized; //false by default
    private boolean isLongClick; //false by default
    private boolean isCopied;

    //indices
    private int selectedFileIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_one);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS,REQUEST_PERMISSIONS);
            return;
        }

        if(!isManagerInitialized) {
            initializePaths();
            initializeExplorer(files);
            setGoBackListener();
            setRefreshListener();
            setCheckBoxOnClickListener();
            setRenameButtonOnClickListener();
            setCopyButtonOnClickListener();
            setPasteButtonOnClickListener();
        }else {
            findViewById(R.id.refresh).callOnClick();
        }
    }

    // permission methods
    private boolean arePermissionsDenied(){
        //if the device is running on Marshmallow or higher
        //check for permissions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int p = 0;
            while(p < PERMISSIONS_COUNT){
                //if the permission is not granted, return true
                if(checkSelfPermission(PERMISSIONS[p]) == PackageManager.PERMISSION_DENIED){
                    return true;
                }
                p++;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults){

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        //check if the result is the one we requested
        //and we are not receiving empty results
        if(requestCode == REQUEST_PERMISSIONS && grantResults.length > 0){
            //clear app data when permissions are denied
            //to tell the user they can only use the app if they
            //grant the permissions
            if(arePermissionsDenied()){
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }else{
                onResume();
            }
        }
    }

    // permission methods end

    // onClickListenerMethods
    public void setPasteButtonOnClickListener(){
        findViewById(R.id.paste).setOnClickListener(view -> {
            String destinationPath = currentPath + copyPath.substring(copyPath.lastIndexOf('/'));
            copy(new File(copyPath), new File(destinationPath));

            isCopied = false;
            hideBottomBar();

            updateSelection();
            findViewById(R.id.refresh).callOnClick();
        });
    }

    public void setCopyButtonOnClickListener(){
        findViewById(R.id.copy).setOnClickListener(view ->{
            copyPath = files[selectedFileIndex].getAbsolutePath();
            isCopied = true;
            updateSelection();
            hideBottomBar();
        });
    }

    public void setRenameButtonOnClickListener(){
        findViewById(R.id.rename).setOnClickListener(view -> renameProcess());
    }

    public void setCheckBoxOnClickListener(){
        final Button checkButton = findViewById(R.id.select);
        checkButton.setOnClickListener(view-> {
            String selectedFilePath = files[selectedFileIndex].getAbsolutePath();

            ImportAsync importer = new ImportAsync(this, selectedFilePath);
            importer.execute();

            findViewById(R.id.refresh).callOnClick();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });
    }

    public void setOnLongClickListItem(){
        selected = new boolean[files.length];
        listView.setOnItemLongClickListener((adapterView, view, position, l) -> {
            isLongClick = true;
            selected[position] = !selected[position];
            textAdapterOne.setSelected(selected);
            int selection_count = 0;
            for(boolean selection : selected){
                if(selection)
                    selection_count++;
            }
            if(selection_count > 0){
                if(selection_count == 1){
                    selectedFileIndex = position;
                    showBottomBar();
                }else{
                    hideBottomBar();
                }
                findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.bottomBar).setVisibility(View.GONE);
            }
            new Handler().postDelayed(() -> isLongClick = false,1000);
            return false;
        });
    }

    public void setOnClickListItem(){
        listView.setOnItemClickListener((adapterView, view,  position, l) -> new Handler().postDelayed(() -> {
            if(!isLongClick){
                if(position > files.length)
                    return;
                if(files[position].isDirectory()){
                    currentPath = files[position].getAbsolutePath();
                    dir = new File(currentPath);
                    pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/') + 1));
                    findViewById(R.id.refresh).callOnClick();
                }
            }
        },50));
    }

    public void setGoBackListener(){
        findViewById(R.id.goBackButton).setOnClickListener(view -> {
            if(currentPath.equals(rootPath))
                return;
            currentPath = currentPath.substring(0,currentPath.lastIndexOf('/'));
            dir = new File(currentPath);
            pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/') + 1));

            findViewById(R.id.refresh).callOnClick();
        });
    }

    public void setRefreshListener(){
        findViewById(R.id.refresh).setOnClickListener(view -> {
            files = dir.listFiles();
            if(files == null)
                return;
            filesList.clear();
            for (File file : files)
                    filesList.add(file.getAbsolutePath());
            textAdapterOne.setDataList(filesList);

            hideBottomBar();
            updateSelection();
        });
    }
    // onClickListenerMethods end

    // helper functions //
    public void initializeExplorer(@NonNull File[] files){
        listView = findViewById(R.id.mainListView);
        textAdapterOne = new TextAdapter();
        listView.setAdapter(textAdapterOne);

        filesList = new ArrayList<>();
        for (File file : files) {
            filesList.add(file.getAbsolutePath());
        }
        textAdapterOne.setDataList(filesList);

        setOnClickListItem();
        setOnLongClickListItem();

        isManagerInitialized = true;
    }

    public void initializePaths(){
        rootPath = Environment.getExternalStorageDirectory().getPath();// "/sdcard/Download";//String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
        currentPath = rootPath;
        dir = new File(rootPath);
        files = dir.listFiles();
        pathOutput = findViewById(R.id.pathOutput);
        pathOutput.setText(rootPath.substring(rootPath.lastIndexOf('/')+1));
    }

    public void renameProcess(){
        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(FileExplorerActivity.this);
        renameDialog.setTitle(getResources().getString(R.string.rename_dialog));
        final EditText input = new EditText(FileExplorerActivity.this);
        String renamePath = files[selectedFileIndex].getAbsolutePath();

        input.setText(renamePath.substring(renamePath.lastIndexOf('/') + 1));
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        renameDialog.setView(input);
        renameDialog.setPositiveButton(getResources().getString(R.string.confirm),
                (dialogInterface, i) -> {
                    String s = new File(renamePath).getParent() + "/" + input.getText();
                    File newFile = new File(s);
                    boolean changeSuccessful = new File(renamePath).renameTo(newFile);
                    if(!changeSuccessful) {
                        Toast.makeText(this, "Failed to change file name.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "File name has been changed.", Toast.LENGTH_SHORT).show();
                    }
                    findViewById(R.id.refresh).callOnClick();
                });
        renameDialog.setNegativeButton(getResources().getString(R.string.cancel),
                (dialogInterface, i) -> findViewById(R.id.refresh).callOnClick());


        renameDialog.show();
    }

    private void copy(File source, File destination){
        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while((length = in.read(buffer)) > 0){
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void hideBottomBar(){
        findViewById(R.id.bottomBar).setVisibility(View.GONE);
        findViewById(R.id.rename).setVisibility(View.GONE);
        findViewById(R.id.select).setVisibility(View.GONE);
        findViewById(R.id.copy).setVisibility(View.GONE);
        findViewById(R.id.paste).setVisibility(View.GONE);

        if(isCopied) {
            findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
            findViewById(R.id.paste).setVisibility(View.VISIBLE);
        }
    }

    public void showBottomBar(){
        findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
        findViewById(R.id.rename).setVisibility(View.VISIBLE);
        findViewById(R.id.select).setVisibility(View.VISIBLE);
        findViewById(R.id.copy).setVisibility(View.VISIBLE);
        findViewById(R.id.paste).setVisibility(View.VISIBLE);

        if(!isCopied) {
            findViewById(R.id.paste).setVisibility(View.GONE);
        }
    }

    public void updateSelection(){
        selected = new boolean[files.length];
        textAdapterOne.setSelected(selected);
    }
    // helper functions end

    /* importer async class */
    public static class ImportAsync extends AsyncTask<Void, Integer, Boolean> {
        //to prevent memory leak
        private final WeakReference<FileExplorerActivity> activityReference;
        private final String filePath;

        ImportAsync(FileExplorerActivity context, String filePath){
            activityReference = new WeakReference<>(context);
            this.filePath = filePath;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            importWorkout();
            return null;
        }

        public void importWorkout(){
            readFromFile();
        }

        /* first word in the file must be Workout:,
        this to ensure that the users picks the right file */
        /* After that, first word in each line is a keyword
        that specifies where the following words belong */
        public void readFromFile(){
            FileExplorerActivity explorer = activityReference.get();
            File file = new File(filePath);
            if(file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String currentLine;
                    boolean readFirstLine = false;
                    while ((currentLine = br.readLine()) != null) {
                        /* check if the first line in the file is "Workout:" */
                        /* if so the user has picked the right file */
                        if (!readFirstLine) {
                            if (!currentLine.equals("Workout:")) {
                                Log.v("information", "File format is incorrect");
                                /* print error message here */
                                break;
                            } else {
                                Log.v("information", "File format is correct");
                                readFirstLine = true;
                            }
                        }else{
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
                                SharedPreferences dayPref = explorer.getSharedPreferences(keyword + "WorkoutPlan", Context.MODE_PRIVATE);
                                SharedPreferences.Editor workoutEditor = dayPref.edit();

                                String[] contentSplit = split[1].split(",");

                                for (int i = 0; i < contentSplit.length; i++) {
                                    workoutEditor.putString("exercise" + i, contentSplit[i]);
                                }
                                workoutEditor.putInt("move_count", contentSplit.length);
                                workoutEditor.apply();
                                Log.v("information", "Found body part");
                            }

                            keyword = split[0];
                            Log.v("information", split[0]);
                            /* check for moves here */
                            if (!keyword.equals("") && split.length > 1) {
                                SharedPreferences workoutPref = explorer.getSharedPreferences(keyword + "WorkoutSet", Context.MODE_PRIVATE);
                                SharedPreferences.Editor workoutEditor = workoutPref.edit();

                                String[] contentSplit = split[1].split(",");
                                
                                /* the length of the contentSplit must be at least 3,
                                otherwise something is wrong with the file */
                                if(contentSplit.length >= 3) {
                                    for (int i = 0; i < contentSplit.length; i += 3) {
                                        workoutEditor.putString("exercise" + i, contentSplit[i]);
                                        workoutEditor.putString("set" + i, contentSplit[i + 1]);
                                        workoutEditor.putString("rep" + i, contentSplit[i + 2]);
                                    }
                                }

                                workoutEditor.putInt("move_count", contentSplit.length / 3);
                                workoutEditor.apply();
                                Log.v("information", "Found move");
                            }
                        }
                        Log.v("information", "Import successful");
                    }

                } catch (IOException e) {
                    Log.v("information", "Unable to read the file");
                    e.printStackTrace();
                }
            }else{
                Log.v("information", "File does not exist");
            }
        }
    }
}
