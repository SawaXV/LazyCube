package com.uon.lazycube.about;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.uon.lazycubeapp.BuildConfig;
import com.uon.lazycubeapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AboutFragment extends Fragment {

    // Changelog card used to open changelog dialog
    CardView changelogCard;
    // Version title text
    TextView versionText;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    /**
     * Called to do initial creation of a fragment. Sets version text and
     * changelog callback functionality display the {@link ChangelogDialog} popup.
     * @param savedInstanceState If the fragment is being re-created from a
     *                           previous saved state, this is the state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the version text view to show the current version number
        versionText = view.findViewById(R.id.version_text);
        // Get app version number
        String versionNumber = BuildConfig.VERSION_NAME;
        // Set version text
        String versionString = getString(R.string.version_number, versionNumber);
        versionText.setText(versionString);
        // Get changelog card
        changelogCard = view.findViewById(R.id.changelogCard);
        // Set changelog click to open changelog dialog
        changelogCard.setOnClickListener(v -> {
            ChangelogDialog dialog = new ChangelogDialog(getContext());
            // Set background to transparent so the popup doesn't
            // obscure the fragment below
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }
}