package com.uon.lazycube;

import static com.logic.FaceDetector.get_faces;
import static com.logic.FaceDetector.order_face;
import static com.uon.lazycube.Detector.detectionToPredictionList;
import static java.lang.Math.max;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.logic.AdderQueue;
import com.logic.Cube;
import com.logic.CubeColour;
import com.logic.CubeValidator;
import com.logic.DetectionCenter;
import com.logic.Face;
import com.logic.FaceAdder;
import com.logic.Prediction;
import com.logic.PredictionList;
import com.logic.Square;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class for displaying bounding boxes for detected features from TFL. Bounding
 * boxes are displayed on a View canvas.<br>
 * Implements {@link DetectionObserver} to receive detection results from
 * it's corresponding subject (observer design pattern).
 */
public class BoundingBoxOverlay extends View implements DetectionObserver {

    // The current detection result from the model
    private DetectionResult currentResult;
    // Hashmap of colours, to display a color for each of the model's classes
    private final HashMap<String, Integer> boxColours;
    // Debug info class
    DebugInfo info = DebugInfo.getInstance();
    // Whether the bounding box debug info is visible or ont
    private boolean visible = false;

    /**
     * Constructs a new overlay. Initialises the boxColours hashmap with
     * box colours
     * @param context Context for current application
     * @param attrs A collection of attributes, as found associated with a tag
     *              in an XML document.
     */
    public BoundingBoxOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Init colour hashmap
        boxColours = new HashMap<>();
        boxColours.put("Green", Color.rgb(0,255,0));
        boxColours.put("Red", Color.rgb(255,0,0));
        boxColours.put("White", Color.rgb(255,255,255));
        boxColours.put("Blue", Color.rgb(0,0,255));
        boxColours.put("Orange", Color.rgb(255,125,0));
        boxColours.put("Yellow", Color.rgb(255,255,0));
        boxColours.put("Face", Color.rgb(0,0,0));

    }

    /**
     * Draw a collection of lines to a given canvas, displaying each
     * string on a new line (increasing in the y dir)
     * @param canvas Canvas to draw to
     * @param lines Array of strings to draw
     * @param x Start draw x position
     * @param y Start draw y position
     * @param paint Paint to draw text with
     */
    private void drawMultiLineString(Canvas canvas, ArrayList<String> lines,
                                     int x, int y, Paint paint) {
        for (String line : lines) {
            // Draw the text
            canvas.drawText(line, x, y, paint);
            // Increase y value by the size of the text
            y += paint.getTextSize();
        }
    }

    /**
     * Manually render this view (and all of its children) to the given Canvas.
     * Used to render bounding boxes at their correct positions relative
     * to the camera preview.
     * @param canvas The View's canvas to draw to
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        // Don't draw anything if no results have been received yet
        if (currentResult == null || !visible) {
            info.clearLog();
            // Invalidate canvas, so it can be redrawn again
            this.invalidate();
            return;
        }

        // Get scale factor between scanned image size and the canvas size
        // to scale bounding box sizes, so they appear the correct size.
        float widthRatio = getWidth()/ (float) this.currentResult.getImageWidth();
        float heightRatio = getHeight() / (float) this.currentResult.getImageHeight();
        float scaleFactor = max(widthRatio, heightRatio);

        // Time face detection
        // Get list of predictions for internal logic to use
        PredictionList predictions = detectionToPredictionList(currentResult, scaleFactor);
        // Get list of faces, an ordered list of points from top left
        // to bottom right
        ArrayList<ArrayList<DetectionCenter>> faces = get_faces(predictions);

        // For each object detection result
        for (Detection result : this.currentResult.getResults()) {
            RectF boundingBox = result.getBoundingBox();
            // Rescale bounding box
            RectF drawingBox = new RectF(
                    boundingBox.left * scaleFactor,
                    boundingBox.top * scaleFactor,
                    boundingBox.right * scaleFactor,
                    boundingBox.bottom * scaleFactor
            );
            // Get detection class (e.g. White, Green, Blue, ...)
            String detectionClass = result.getCategories().get(0).getLabel();
            // Create a paint corresponding to the class name
            Paint paint = new Paint();
            paint.setColor(boxColours.get(detectionClass));
            paint.setStrokeWidth(8);
            paint.setStyle(Paint.Style.STROKE);
            // Draw bounding box
            canvas.drawRect(drawingBox, paint);
        }

        // Create a paint for drawing the square ordering text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(64);

        // For every face, display indexes of each square, from 0 to 8
        // (top left to bottom right)
        for (ArrayList<DetectionCenter> face : faces) {
            ArrayList<DetectionCenter> points = order_face(face);
            for (int i = 0; i < points.size(); i++) {
                canvas.drawText(String.valueOf(i), points.get(i).getX(), points.get(i).getY(), paint);
            }
        }

        // Create a paint for displaying debug info
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(48);


        // Display some debug info
        canvas.drawText("Inference time: " + info.getInferenceTime() + " ms", 75, 300, paint);
        canvas.drawText("Face Detection time: " + info.getFaceDetectionTime() + " ms", 75, 350, paint);
        canvas.drawText("Face Adder time: " + info.getFaceAdderTime() + " ms", 75, 400, paint);
        canvas.drawText("FPS: " + info.getFps() + " ms", 75, 450, paint);

        // Get a list of debug info to draw to the canvas
        ArrayList<String> log = new ArrayList<>(info.getLogOutput());
        drawMultiLineString(canvas, log, 75, 500, paint);
        // Clear the log data, so next frame can populate with new data
        info.clearLog();
    }

    /**
     * Notify observer of new detection results. Used to redraw bounding boxes.
     * @param result New detection results scanned
     */
    @Override
    public void notifyResults(DetectionResult result) {
        this.currentResult = result;
        // Invalidate the view, so it is redrawn with new detection results
        this.invalidate();
    }

    /**
     * Sets whether the bounding box is shown or not
     * @param visible Boolean to show the overlay
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
