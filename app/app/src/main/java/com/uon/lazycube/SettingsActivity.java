package com.uon.lazycube;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.uon.lazycubeapp.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

/**
 * Settings activity containing user preferences for language and
 * different options for how the {@link CameraFragment} operates.
 */
public class SettingsActivity extends AppCompatActivity {

    // Top app bar
    MaterialToolbar toolbar;

    /**
     * Called when the activity is starting. Populates the activity
     * with the {@link SettingsFragment}.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        // Populate the activity with the settings fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        // Set toolbar navigation button to go back to previous activity (camera)
        toolbar = findViewById(R.id.topAppBar);
        // Set the toolbar's left back button to close the activity
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Settings fragment holding the app's preference layout
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            // Get the locale dropdown
            DropDownPreference localeDropdown = findPreference("language");
            assert localeDropdown != null;
            // Set locale on change listener when a new language is selected
            localeDropdown.setOnPreferenceChangeListener((preference, newValue) -> {
                // Get the newly selected locale
                Locale locale = new Locale((String) newValue);
                // Update the app's locale
                Locale.setDefault(locale);
                // Update the locale within the app's config, as well
                Resources resources = requireActivity().getResources();
                Configuration config = resources.getConfiguration();
                config.setLocale(locale);
                resources.updateConfiguration(config, resources.getDisplayMetrics());
                return true;
            });
        }
    }
}