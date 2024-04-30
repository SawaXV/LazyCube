package com.uon.lazycube.about;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uon.lazycubeapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A {@link Dialog} subclass to display the current changelog markdown
 * within a dialog box popup.
 */
public class ChangelogDialog extends Dialog implements View.OnClickListener {

    // Text view for changelog
    TextView changelogTextBody;
    // Done button
    Button doneBtn;

    /**
     * Creates a new changelog dialog window
     * @param context Current context for the application
     */
    public ChangelogDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * Called to do initial creation of a fragment. Populates the changelog
     * text from assets, and initialises the done btn callback.
     * @param savedInstanceState If the fragment is being re-created from a
     *                           previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changelog_dialog);
        // Get the changelog text view
        changelogTextBody = findViewById(R.id.changelogBody);
        // Get done btn
        doneBtn = findViewById(R.id.doneBtn);
        // Set bottom done button to close the dialog
        doneBtn.setOnClickListener(v -> dismiss());
        // Set changelog text
        readChangelog();
    }

    /**
     * Reads changelog file in assets to set text of the dialog
     * changelog textview to it
     */
    private void readChangelog() {
        BufferedReader file = null;
        String changelogText = "";
        try {
            // Read the changelog asset file
            file = new BufferedReader(new InputStreamReader(
                    getContext()
                            .getAssets()
                            .open("CHANGELOG.md"), StandardCharsets.UTF_8)
            );
            String line;
            // Read each line
            while ((line = file.readLine()) != null) {
                // Add a newline after each line since it's not included in
                // the read buffer
                changelogText += line + "\n";
            }
        } catch (IOException e) {
            System.out.println("Unable to read file");
        } finally {
            // Make sure to close the file, even after a caught exception
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    System.out.println("Unable to close file");
                }
            }
        }
        // Sets the text for the changelog
        changelogTextBody.setText(changelogText);
    }

    // Template required functions
    @Override
    public void onClick(View v) { }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}