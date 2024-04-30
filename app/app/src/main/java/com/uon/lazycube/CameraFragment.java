package com.uon.lazycube;

import static androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.uon.lazycubeapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.logic.Cube;

import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.uon.lazycubeapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.logic.Cube;
import com.logic.CubeColour;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity for display the camera, and running TFL object detection.
 */
public class CameraFragment extends Fragment {

        // Permission request code for using the camera
        private static final int CAMERA_REQUEST_CODE = 100;
        // Camera preview view, to show the camera view to the user
        PreviewView cameraPreview;
        // Camera options used for tap to focus
        CameraControl cameraControl;
        // TFL detector
        Detector detector;
        // Runnable thread object for running detection separate to UI thread
        Executor detectionThread;
        // Current bitmap from the camera
        Bitmap bitmap = null;
        // Button to go to the cube visualisation activity
        Button cubeActivityBtn;
        // Button to toggle the debug overlay
        FloatingActionButton settingsButton;
        // Button to restart scanning
        FloatingActionButton restartBtn;
        // The internal cube representation to send to the cube activity
        Cube cube;
        // Android vibrator used when the cube is complete
        Vibrator vibrator;
        // Frame layout to store animation that will be moved on tap to focus
        FrameLayout lottieParent;
        // Layout for showing rotation hints
        RelativeLayout rotationLayout;
        // Layout for fading out the camera
        RelativeLayout cameraFadeOverlay;
        // Debug bounding box overlay
        BoundingBoxOverlay debugOverlay;
        // Ar tick overlay
        ArOverlay arOverlay;
        // Animation for tick complete
        LottieAnimationView tickCompleteAnimation;
        // Scan indicator overlay image
        ImageView scanIndicator;

        // Failure snackbar
        Snackbar failSnackbar;
        // Complete fail snackbar
        Snackbar completeFailSnackbar;

        // Future for setting up the camera
        private ListenableFuture<ProcessCameraProvider> cameraFuture;

        // Hashmap of Face colours to corresponding progress bar
        // colours
        private final HashMap<CubeColour, ImageView> progressBoxes = new HashMap<>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.activity_camera, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                super.onViewCreated(view, savedInstanceState);

                cameraPreview = view.findViewById(R.id.camPreview);
                debugOverlay = view.findViewById(R.id.boundingBoxOverlay);
                arOverlay = view.findViewById(R.id.arOverlay);
                cubeActivityBtn = view.findViewById(R.id.solveBtn);
                settingsButton = view.findViewById(R.id.settingsButton);
                vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                lottieParent = view.findViewById(R.id.tapParent);
                rotationLayout = view.findViewById(R.id.cubeRotationHelper);
                cameraFadeOverlay = view.findViewById(R.id.cameraFadeOverlay);
                tickCompleteAnimation = view.findViewById(R.id.complete_tick_animation);
                scanIndicator = view.findViewById(R.id.scan_indicator);
                restartBtn = view.findViewById(R.id.restart_btn);

                failSnackbar = createFailSnackbar();
                completeFailSnackbar = createCompleteFailSnackbar();

                initProgressBoxesHashMap();

                // Hide the cube activity button
                cubeActivityBtn.setVisibility(View.GONE);
                // Set button on click to launch cube visualisation
                cubeActivityBtn.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), CubeActivity.class);
                        intent.putExtra("cube", cube);
                        requireActivity().startActivity(intent);
                });

                settingsButton.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), SettingsActivity.class);
                        requireActivity().startActivity(intent);
                });


                // On click listener to reset detection
                restartBtn.setOnClickListener(v -> resetDetection());


                initDetector();

                // Init single thread for running image analysis on
                detectionThread = Executors.newSingleThreadExecutor();

                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // If not, request them
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                }

                // Get a camera provider
                cameraFuture = ProcessCameraProvider.getInstance(requireContext());

                cameraFuture.addListener(() -> {
                        try {
                                ProcessCameraProvider cameraProvider = cameraFuture.get();
                                // Initialise and bind the camera
                                bindCamera(cameraProvider);
                        } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                        }
                }, ContextCompat.getMainExecutor(requireContext()));

                // Set up tap to focus on the camera
                setUpCameraFocus();
        }

        @Override
        public void onResume() {
                super.onResume();
                setPreferences();
        }

        /**
         * Sets various object properties based on preferences set in
         * the settings menu.
         */
        @SuppressLint("ResourceType")
        private void setPreferences() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                // Update debug mode
                boolean debugMode = prefs.getBoolean("dev_mode", false);
                debugOverlay.setVisible(debugMode);
                // Set scanning turbo mode
                boolean turboMode = prefs.getBoolean("turbo_mode", false);
                detector.setTurbo(turboMode);
        }

        /**
         * Gets all progress bar squares and store within hashmap
         * against their respective CubeColour colours
         */
        private void initProgressBoxesHashMap() {
                ImageView square;
                square = getView().findViewById(R.id.yellowSquare);
                progressBoxes.put(CubeColour.YELLOW, square);
                square = getView().findViewById(R.id.whiteSquare);
                progressBoxes.put(CubeColour.WHITE, square);
                square = getView().findViewById(R.id.redSquare);
                progressBoxes.put(CubeColour.RED, square);
                square = getView().findViewById(R.id.orangeSquare);
                progressBoxes.put(CubeColour.ORANGE, square);
                square = getView().findViewById(R.id.greenSquare);
                progressBoxes.put(CubeColour.GREEN, square);
                square = getView().findViewById(R.id.blueSquare);
                progressBoxes.put(CubeColour.BLUE, square);
        }


        /**
         * Resets cube detection progress + displays
         * a reset message to the user
         */
        private void resetDetection() {
                // Reset detector
                initDetector();
                vibrateMotor(20);
                // Notify user with a toast
                Toast toast = Toast.makeText(requireActivity(), "Scanning Reset", Toast.LENGTH_SHORT);
                toast.show();
                // Reset progress bar images
                resetProgressBar();
                // Hide all fail snackbars
                hideAllSnackbars();
                // Show overlays that could have been hidden by
                // cube completion
                scanIndicator.setVisibility(View.VISIBLE);
                arOverlay.setVisibility(View.VISIBLE);
                debugOverlay.setVisibility(View.VISIBLE);
        }


        /**
         * Initialises the scanning detection object ready for
         * scanning
         */
        private void initDetector() {
                // Init detector
                detector = new Detector(requireContext());
                detector.registerObserver(debugOverlay);
                detector.registerObserver(arOverlay);

                // Set detection completion callback to enable button
                // to move to cube visualisation activity
                detector.addCompleteCallback(new DetectionProgress() {
                        @Override
                        public void notifyProgress(Cube cube, ScanProgress progress) {
                                if (!isAdded()) return;
                                onProgressUpdate(cube, progress);
                        }

                        @Override
                        public void notifyFaceScan(CubeColour faceColour) {
                                if (!isAdded()) return;
                                onFaceScanned(faceColour);
                        }

                        @Override
                        public void notifyFaceRescan(CubeColour faceColour) {
                                if (!isAdded()) return;
                                onFaceFail(faceColour);
                        }

                        @Override
                        public void notifyFail() {
                                if (!isAdded()) return;
                                System.out.println("CANNOT FIX!!! :((");
                                // TODO go to fail activity/fragment
                        }
                });
                arOverlay.setFaceScannedCallBack(detector);
        }


        /**
         * Called when a new face is scanned. Hides the respective
         * colour in the progress bar
         * @param faceColour New face colour scanned
         */
        private void onFaceScanned(CubeColour faceColour) {
                ImageView square = progressBoxes.get(faceColour);
                requireActivity().runOnUiThread(() -> {
                        assert square != null;
                        square.setVisibility(ImageView.GONE);
                });
        }

        /**
         * Called when a face needs to be rescanned. Shows the
         * respective colour back on the progress bar
         * @param faceColour Face to show on progress bar again
         */
        private void onFaceFail(CubeColour faceColour) {
                ImageView square = progressBoxes.get(faceColour);
                requireActivity().runOnUiThread(() -> {
                        assert square != null;
                        square.setVisibility(ImageView.VISIBLE);
                });
        }


        /**
         * Resets progress bar images to full
         */
        private void resetProgressBar() {
                for (CubeColour colour : CubeColour.values()) {
                        if (colour == CubeColour.NULL) continue;
                        Objects.requireNonNull(progressBoxes.get(colour))
                                .setVisibility(ImageView.VISIBLE);
                }
                cubeActivityBtn.setVisibility(View.GONE);
                tickCompleteAnimation.setVisibility(View.INVISIBLE);
        }

        /**
         * Helper function to set touch listener to focus the
         * camera on screen tap
         */
        private void setUpCameraFocus() {
                cameraPreview.setOnTouchListener((v, event) -> {
                        if (event.getAction() != MotionEvent.ACTION_UP) return true;
                        // Get metering
                        MeteringPointFactory factory = cameraPreview.getMeteringPointFactory();
                        MeteringPoint point = factory.createPoint(event.getX(), event.getY());
                        // Set focus metering action
                        FocusMeteringAction action = new FocusMeteringAction.Builder(point).build();
                        // Start focusing at given point
                        cameraControl.startFocusAndMetering(action);
                        v.performClick();

                        // Play animation at touch position
                        playFocusAnimation((int) event.getX(), (int) event.getY());

                        return true;
                });
        }

        /**
         * Plays the tap to focus animation at a given position
         * @param x Absolute x position of animation
         * @param y Absolute y position of animation
         */
        private void playFocusAnimation(int x, int y) {
                // Get lottie animation view
                LottieAnimationView tapAnimation = requireView().findViewById(R.id.tap_focus_anim);

                // Set position of animation to tap position
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tapAnimation.getLayoutParams();
                // Must subtract half the dimensions to center on tap position
                params.setMargins(x - tapAnimation.getWidth() / 2,
                        y - tapAnimation.getHeight() / 2, 0, 0);

                // Remove view to be able to re-add it
                lottieParent.removeView(tapAnimation);

                tapAnimation.setLayoutParams(params);
                tapAnimation.playAnimation();

                lottieParent.addView(tapAnimation);
        }


        /**
         * Play big tick animation to indicate the scanning
         * is complete
         */
        private void playTickAnimation() {
                // Hide the scan indicator overlay, ar tick and debug overlays
                // to avoid things clashing with the animation
                scanIndicator.setVisibility(View.INVISIBLE);
                arOverlay.setVisibility(View.INVISIBLE);
                debugOverlay.setVisibility(View.INVISIBLE);
                cameraFadeOverlay.setVisibility(View.VISIBLE);
                tickCompleteAnimation.setVisibility(View.VISIBLE);
                tickCompleteAnimation.playAnimation();
        }

        /**
         * Invoked when the detector has progress updates, either on
         * cube scan success, failure to add all faces, or a "total" fail
         * where the user is advised to rescan in better lighting
         * @param cube Complete cube config from the detector
         * @param progress Detection progress indicator
         */
        private void onProgressUpdate(Cube cube, ScanProgress progress) {
                switch (progress) {
                        case COMPLETED: // On cube scan completion
                                cubeComplete(cube);
                                break;
                        case FAIL: // On >= 1 failed faces additions
                                showFailSnackbar();
                                break;
                        case COMPLETE_FAIL:
                                // On "complete fail", where we cannot see if
                                // viable to continue with the scan
                                showCompleteFailSnackbar();
                                break;
                        case ROTATE_HINT: // When to show rotation hint
                                showRotationHint(true);
                                break;
                        case ROTATE_HINT_STOP:
                                showRotationHint(false);
                                break;
                }

        }

        /**
         * Sets the visibility of the rotation hint to help the
         * user to move to the next face to continue scanning
         * @param show Whether to show or hide the hint
         */
        private void showRotationHint(boolean show) {
                requireActivity().runOnUiThread(() -> {
                        int visibility = View.VISIBLE;
                        if (!show) {
                                visibility = View.INVISIBLE;
                        }
                        rotationLayout.setVisibility(visibility);
                        cameraFadeOverlay.setVisibility(visibility);
                });
        }

        /**
         * Shows fail snackbar popup to advice the user to continue
         * scanning in better lighting conditions
         */
        private void showFailSnackbar() {
                requireActivity().runOnUiThread(() -> failSnackbar.show());
        }

        @NonNull
        private Snackbar createFailSnackbar() {
                // Get parent view to add snackbar to
                CoordinatorLayout parent = requireView().findViewById(R.id.snackbarParent);
                // Create snackbar with fail text
                Snackbar failSnackbar = Snackbar.make(parent, R.string.scan_fail_snackbar, 10000);
                failSnackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
                View snackbarView = failSnackbar.getView();
                // Get text, to set height of snackbar
                TextView snackTextView = (TextView) snackbarView.findViewById(
                        com.google.android.material.R.id.snackbar_text);
                snackTextView.setMaxLines(3);
                return failSnackbar;
        }

        /**
         * Shows "complete fail" snackbar popup, where we cannot seem to scan
         * the cube successfully.
         * Advises the user to "rescan" (reset detection logic), and move to
         * a much better environment
         */
        private void showCompleteFailSnackbar() {
                requireActivity().runOnUiThread(() -> completeFailSnackbar.show());
        }

        @NonNull
        private Snackbar createCompleteFailSnackbar() {
                // Get parent view to add snackbar to
                CoordinatorLayout parent = requireView().findViewById(R.id.snackbarParent);
                // Create snackbar with complete fail text
                Snackbar failSnackbar = Snackbar
                        .make(parent, R.string.scan_complete_fail_snackbar, BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction(R.string.restart_scan, v -> resetDetection());
                failSnackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE);
                View snackbarView = failSnackbar.getView();
                // Get text to set height of snackbar
                TextView snackTextView = (TextView) snackbarView.findViewById(
                        com.google.android.material.R.id.snackbar_text);
                snackTextView.setMaxLines(5);
                return failSnackbar;
        }

        /**
         * On cube completion, to enable the finished button to move towards
         * the cube solution screen
         * @param cube Scanned cube config to pass to solver
         */
        private void cubeComplete(Cube cube) {
                this.cube = cube;
                requireActivity().runOnUiThread(() -> {
                        // Hide all remaining progress cubes
                        hideAllProgressColours();
                        // Show solve btn
                        cubeActivityBtn.setVisibility(View.VISIBLE);
                        // Enable button to solving screen
                        cubeActivityBtn.setEnabled(true);
                        // Hide any snackbars
                        hideAllSnackbars();
                        // Play completed tick animation
                        playTickAnimation();
                });
                //Create SFX
                MediaPlayer mp = MediaPlayer.create(requireContext(),R.raw.scan_complete);
                //Set up SFX to be freed after playing
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                                mp.release();
                        }
                });
                //Play SFX
                try {
                        //mp.prepare();
                        mp.start();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                // Vibrate the motor to notify the user
                vibrateMotor(200);
        }

        /**
         * Hides all progress bar colours
         */
        private void hideAllProgressColours() {
                for (CubeColour colour : CubeColour.values()) {
                        if (colour == CubeColour.NULL) continue;
                        progressBoxes.get(colour).setVisibility(View.GONE);
                }
        }

        /**
         * Hides any visible snackbars
         */
        private void hideAllSnackbars() {
                completeFailSnackbar.dismiss();
                failSnackbar.dismiss();
        }

        /**
         * Helper method for vibrating the phone's motor
         * @param duration Duration in ms to vibrate for
         */
        private void vibrateMotor(int duration) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                        vibrator.vibrate(duration);
                }
        }

        /**
         * Callback for the result from requesting permissions.
         * Used to request for camera permissions
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                // If the camera permission is requested
                if (requestCode == CAMERA_REQUEST_CODE) {
                        // Display a toast for success or failure
                        if (grantResults.length > 0 &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(requireContext(), "Camera Permission Given :)",
                                        Toast.LENGTH_SHORT).show();
                        }
                        else{
                                // TODO create activity to explain to the user the app is usable without these permissions
                                Toast.makeText(requireContext(), "Camera Permission Denied :(",
                                        Toast.LENGTH_SHORT).show();
                        }
                }
        }


        /**
         * Initialises the camera with correct parameters for TFL detection
         * @param cameraProvider Camera provider used to bind the phones camera
         *                       lifecycle.
         */
        private void bindCamera(ProcessCameraProvider cameraProvider) {
                // Build a new preview, setting the aspect ratio and rotation
                Preview preview = new Preview.Builder()
                        // Training is mostly done with 4/3 ratio, so use that
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

                // Get back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Set image analyser to use a RGB image format
                ImageAnalysis imageAnalyzer =
                        new ImageAnalysis.Builder()
                                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                                .setTargetRotation(Surface.ROTATION_0)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .build();

                // Set callback to run detection when an image is received
                imageAnalyzer.setAnalyzer(detectionThread, image -> {
                        // Only initialise the image bitmap once
                        if (bitmap == null) {
                                bitmap = Bitmap.createBitmap(
                                        image.getWidth(),
                                        image.getHeight(),
                                        Bitmap.Config.ARGB_8888
                                );
                        }
                        // Run detection
                        runDetection(image);
                        // Close image (since this is blocking), so we can get another one
                        image.close();
                });

                // Unbind camera lifecycle
                cameraProvider.unbindAll();

                // Bind camera
                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, imageAnalyzer, preview
                );

                // Get camera control for tap to focus
                cameraControl = camera.getCameraControl();

                // Add preview
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());
        }

        /**
         * Perform any final cleanup before an activity is destroyed
         */
        @Override
        public void onDestroy() {
                super.onDestroy();
                // Required method by View
                // TODO shutdown thread?
        }

        /**
         * Run TFL detection on an image from the Android ImageAnalysis
         * @param image Image received from the camera
         */
        synchronized private void runDetection(ImageProxy image) {
                // Copy the image into the bitmap
                bitmap.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());
                // TODO add rotation info from camera?
                // Run detection with bitmap
                detector.detect(bitmap);
        }

}