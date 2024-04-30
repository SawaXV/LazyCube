package com.uon.lazycube;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.uon.lazycubeapp.R;
import com.uon.lazycubeapp.databinding.ActivityMainBinding;
import com.uon.lazycube.about.AboutFragment;

public class MainActivity extends LocaleActivity {

    ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Set default fragment on creation to the scanning camera
        // fragment
        replaceFragment(new CameraFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.scan_nav_bar);

        // Set bottom navbar onclick callbacks to change the displayed
        // fragment
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.scan_nav_bar:
                    replaceFragment(new CameraFragment());
                    break;
                case R.id.tutorial_nav_bar:
                    replaceFragment(new TutorialFragment());
                    break;
                case R.id.about_nav_bar:
                    replaceFragment(new AboutFragment());
                    break;
            }
            return true;
        });
    }

    /**
     * On resume to the activity (e.g. when returning, and first creation),
     * update any object states from settings parameters.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Reset bottom navbar selection to the scanning view
        binding.bottomNavigationView.setSelectedItemId(R.id.scan_nav_bar);
    }

    /**
     * Sets back button press to not do anything. Avoids any strange activity
     * behaviour from happening.
     */
    @Override
    public void onBackPressed() {}

    /**
     * Replaces current fragment displayed with a given fragment
     * @param fragment Fragment to display
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
