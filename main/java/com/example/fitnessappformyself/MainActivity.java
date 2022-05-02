package com.example.fitnessappformyself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //permissions
    private static final int REQUEST_PERMISSIONS = 1242;
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSIONS_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        setFragments();

            //to be changed
        //direct the user to profile creation activity
        if(!profileExists()){
            launchCreateProfile();

            //finish the activity so that
            //if the user presses previous button of their phone
            //they should not bypass create profile activity
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
    }

    //permission methods start
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

    //permission methods end
    @SuppressLint("ApplySharedPref")
    public boolean profileExists(){
        SharedPreferences pref = getSharedPreferences("DoesProfileExist", Context.MODE_PRIVATE);
        if (pref.getString("exists", null) != null) {
            return pref.getString("exists", null).equals("yes");
        }
        return false;
    }

    public void launchCreateProfile(){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

    public void setFragments(){
        ViewPager viewPager = findViewById(R.id.mainViewPager);

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);

        CalendarFragment calendarFragment = new CalendarFragment();
        WorkoutFragment workoutFragment = new WorkoutFragment();
        ReadMeFragment readMeFragment = new ReadMeFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(calendarFragment, "CALENDAR");
        viewPagerAdapter.addFragment(workoutFragment, "MOVES");
        viewPagerAdapter.addFragment(profileFragment, "PROFILE");
        viewPagerAdapter.addFragment(readMeFragment, "READ ME");
        viewPager.setAdapter(viewPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_baseline_calendar_today_24);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_baseline_sports_handball_24);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_baseline_profile_24);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_baseline_info_24);
    }

    //on click functionality methods end
    ////UNUSED////
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {}
}