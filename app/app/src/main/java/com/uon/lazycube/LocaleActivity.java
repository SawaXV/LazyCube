package com.uon.lazycube;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;

/**
 * Sub-class of {@link AppCompatActivity} to define extra behaviour on
 * activity re-enter/restart to update the app's locale. This is
 * used as the base class for all activities within LazyCube.
 */
public class LocaleActivity extends AppCompatActivity {

    // Current locale of activity
    private static Locale currentLocale;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocaleFromPreference();
    }

    /**
     * Updates the activities current locale
     */
    private void setLocaleFromPreference() {
        // Get the current set locale
        currentLocale = getLocale();
        // Update the app's locale via it's config
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(currentLocale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    /**
     * Called after onCreate, used to get locale, in the case
     * its been changed from the settings activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Get the current set locale
        currentLocale = Locale.getDefault();
    }

    /**
     * When the activity is being redrawn. Used to update the locale
     * and recreate the activity if changed.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        // Get the new locale (if there is one)
        Locale locale = getLocale();

        // Recreate (restart) activity if the locale has been updated
        if (!locale.equals(currentLocale)) {
            currentLocale = locale;
            recreate();
        }
    }

    /**
     * Get locale from shared preferences
     * @return Locale set via preferences dropdown
     */
    @NonNull
    private Locale getLocale() {
        // Get the set locale in the shared preferences (set from the
        // settings activity), defaulting as en if no setting is found
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("language", "en");
        return new Locale(lang);
    }
}
