package com.uon.lazycube;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.catalinjurjiu.animcubeandroid.AnimCube;
import cs.min2phase.Search;
import com.uon.lazycubeapp.R;
import com.logic.Cube;
import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Activity for displaying a 3D cube to display the solution to
 * the user.
 */
public class CubeActivity extends LocaleActivity {

    // Button for stepping the cube forward
    Button moveBtn;
    // Button for stepping the cube backwards
    Button backBtn;
    // Button for going back to the scanner
    Button rescanBtn;
    // Button for autoplay features
    Button playBtn;
    // Bar for changing autoplay speed
    SeekBar playSpeedBar;
    // Text displaying current move
    TextView moveText;
    // The 3d cube to animate
    AnimCube cube;
    // Completion Animation
    LottieAnimationView animationView;

    // Moves making up the solution
    String[] solutionMoves;

    // Step the solver is currently showing
    int currentStep=0;
    // Whether the autoplayer is currently on
    boolean isPlaying = false;
    // The speeds that the play can go
    static double speeds[] = {3.0,2.0,1.5,1.0,0.75};

    //Timer used for autoplay
    Timer timer;

    /**
     * Called when the activity is starting.
     * Used to set button on click events for moving the 3d cube.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most recently supplied
     *                           in onSaveInstanceState.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cube);
        // Get buttons & text references
        moveBtn = findViewById(R.id.move_btn);
        backBtn = findViewById(R.id.back_btn);
        rescanBtn = findViewById(R.id.rescan_btn);
        playBtn = findViewById(R.id.play_btn);
        moveText = findViewById(R.id.move_text);
        playSpeedBar = findViewById(R.id.playSpeedBar);

        // Initialise Timer
        timer=new Timer();

        // Get AnimCube to animate
        cube = findViewById(R.id.animCube);
        //Get AnimationView
        animationView = findViewById(R.id.complete_animation);
        // Get the completed scanned cube
        Cube logicCube = (Cube) getIntent().getSerializableExtra("cube");

        // Get the cube solver and get solution sequence

        //Using Cubots
        //Cubot3 cubot = new Cubot3(logicCube.toCubotStringArray());
        //System.out.println(cubot);
        //String solution = cubot.solve();

        //Using Min2Phase
        String cubeString = logicCube.toMin2PhaseString();
        String solution = new Search().solution(cubeString, 21, 100000000, 0, 0);

        //Handle 180 degree turns
        //solution = extrapolateSolution(solution);

        // Set the state for the cube
        cube.setCubeModel(logicCube.toDisplayString());
        // Set the moves for the solution
        cube.setMoveSequence(solution);

        //Stores moves for display
        solution = solution.trim().replaceAll("\\s{2,}", " ");
        solutionMoves=solution.split(" ");
        System.out.println("\n\nSOLUTION: " + solution );

        // Set the move button to animate the next move
        moveBtn.setOnClickListener(v -> nextStep());
        // Set the back button to animate reverse of last move
        backBtn.setOnClickListener(v -> backStep());

        //Initialise text & buttons
        backBtn.setEnabled(false);
        moveText.bringToFront();
        setText();

        // Set rescan onclick callback to close to activity (back to scanning view)
        rescanBtn.setOnClickListener(v -> finish());
        // Set play onclick callback to toggle auto-play
        playBtn.setOnClickListener(v -> playToggle());

        // Set the play button icon to "play"
        runOnUiThread(()->playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,R.drawable.play_arrow));

        // Set on slider change listener
        playSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Update auto-play speed on slider change
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeSpeed(playSpeedBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        });
    }

    /**
     * Sets back button press to not do anything. Therefore, user
     * must use the rescan button
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Convert solution 180 degree turns to 2 90 degree turns
     * @param s Solution string
     * @return Converted solution string
     */
    static String extrapolateSolution(String s)
    {
        String r = s.replaceAll("R2","R R");

        r=r.replaceAll("L2", "L L");
        r=r.replaceAll("U2", "U U");
        r=r.replaceAll("D2", "D D");
        r=r.replaceAll("F2", "F F");
        r=r.replaceAll("B2", "B B");

        return r;
    }

    /**
     * Invoked on pressing next. Moves cube forwards, updates move text
     * and move buttons (if at end)
     */
    void nextStep()
    {
        if(currentStep++<solutionMoves.length)
        {
            // If at the end of solution - now solved
            if (currentStep == solutionMoves.length) {
                // Disable move button, so we can't move beyond end of solution
                moveBtn.setEnabled(false);
                animationView.playAnimation();

                if (playSpeedBar.getProgress() > 0) {
                    playToggle();
                }

                // Create confetti SFX
                MediaPlayer mp = MediaPlayer.create(this,R.raw.fanfare);
                // Set up SFX to be freed after playing
                mp.setOnCompletionListener(MediaPlayer::release);
                // Play SFX
                try {
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            backBtn.setEnabled(true);

            setText();

            cube.animateMove();
        }
    }

    /**
     * On pressing back; moves cube back, updates move text
     * and move buttons
     */
    void backStep()
    {
        // Disable back btn if first move
        if(--currentStep==0) backBtn.setEnabled(false);
        moveBtn.setEnabled(true);

        setText();

        cube.animateMoveReversed();
    }

    /**
     * Updates move text to match the current move in the solution
     */
    void setText()
    {
        // Create new move text, e.g. "Move 5/20"
        StringBuffer newText = new StringBuffer(
                getResources().getString(R.string.updated_move) +
                        " " + currentStep + "/" + solutionMoves.length
        );

        // If not the first step, show the previous move, e.g. "Move 5/20 - F"
        if(currentStep!=0)
            newText.append(" - " + solutionMoves[currentStep-1]);

        // Update text on the UI thread
        runOnUiThread(()->moveText.setText(newText.toString()));
    }

    /**
     * Toggle auto-play animation
     */
    void playToggle()
    {
        // Must end playing and cancel timer if the solving is complete
        if(currentStep==solutionMoves.length) {
            isPlaying = false;
            timer.cancel();
            runOnUiThread(()->
                    playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,0,0,R.drawable.play_arrow
                    ));
            return;
        };

        isPlaying = !isPlaying;
        int d;

        // If toggled to start playing
        if(isPlaying) {
            // Update auto-play icon to pause
            d = R.drawable.pause;
            // Update speed (if changed while paused)
            changeSpeed(playSpeedBar.getProgress());
        } else { // If toggled to pause
            // Update auto-play icon to play
            d = R.drawable.play_arrow;
            // Stop timer (now paused)
            timer.cancel();
        }

        // Update auto-play icon on the UI thread
        runOnUiThread(()->playBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,d));
    }

    /**
     * Change speed of auto-play animation intervals
     * @param speedIndex Index of valid speed values
     */
    void changeSpeed(int speedIndex)
    {
        // Reset animation interval timer
        timer.cancel();
        timer=new Timer();

        // Update auto-play with new time interval
        if(isPlaying) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> nextStep());
                }
            };
            timer.schedule(task,1000, (int)(speeds[speedIndex]*1000.0));
        }
    }

}
