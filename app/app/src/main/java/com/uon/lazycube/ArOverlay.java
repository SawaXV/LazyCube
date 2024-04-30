package com.uon.lazycube;

import static com.logic.FaceDetector.get_faces;
import static com.logic.FaceDetector.order_face;
import static com.uon.lazycube.Detector.detectionToPredictionList;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.uon.lazycubeapp.R;
import com.logic.CubeColour;
import com.logic.DetectionCenter;
import com.logic.Face;
import com.logic.PredictionList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Overlay view, primarily drawing to its canvas, to display an emulated
 * AR tick over currently scanned faces.
 * Implements {@link DetectionObserver} to receive detection results from
 * it's corresponding subject (observer design pattern).
 */
public class ArOverlay extends View implements DetectionObserver {

    // The current detection result from the model
    private DetectionResult currentResult;

    // The number of frames for a given face until a tick is shown
    private final int AVERAGE_FRAME_COUNT = 1;
    // The fade speed when showing a tick
    private final int FADE_VELOCITY = 110;

    // Tick bitmap to render
    private Bitmap tick;
    // Paint to draw bitmap colour
    private Paint paint;

    // Interface for retrieving scanned faces
    private IFaceScanned faceScanned;

    // List of "current faces" detected by the camera this frame,
    // each stored as an average position
    private ArrayList<DetectionCenter> currentFaces = new ArrayList<>();
    // Hashmap for the number of frames each face has been seen for
    private HashMap<CubeColour, Integer> faceSeenCount = new HashMap<>();
    // Hashmap for the opacity of a face
    private HashMap<CubeColour, Integer> faceOpacities = new HashMap<>();
    // Hashmap for average width/height dimensions of a face, stored as
    // x/y points
    private HashMap<CubeColour, Point> faceDimensions = new HashMap<>();


    /**
     * Constructs a new AR overlay.
     * @param context Context for current application
     * @param attrs A collection of attributes, as found associated with a tag
     *              in an XML document.
     */
    public ArOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Get the tick asset for the tick
        tick = getBitmapFromVector(R.drawable.face_complete);

        // Init each hashmap values to 0s
        for (CubeColour colour : CubeColour.values()) {
            faceSeenCount.put(colour, 0);
            faceOpacities.put(colour, 0);
            faceDimensions.put(colour, new Point(0,0));
        }
    }

    /**
     * Sets the faceScanned object, used to get the current scanned faces
     * from the AdderQueue
     * @param faceScanned FaceScanned object to retrieved scanned faces
     *                    from (e.g. AdderQueue)
     */
    public void setFaceScannedCallBack(IFaceScanned faceScanned) {
        this.faceScanned = faceScanned;
    }

    /**
     * Gets a bitmap from a drawable vector asset. Used to render a tick
     * to the canvas
     * @param drawableId Android studio drawable asset id
     * @return Bitmap of vector asset
     */
    private Bitmap getBitmapFromVector(int drawableId) {
        // Get the vector asset drawable
        Drawable drawable = ContextCompat.getDrawable(this.getContext(), drawableId);
        assert drawable != null;
        // Create an empty bitmap, the same size as the asset
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        // Draw the vector asset to the bitmap via a canvas
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Manually render this view (and all of its children) to the given Canvas.
     * Used to render ticks on scanned faces
     * @param canvas The View's canvas to draw to
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        // Don't draw anything if no results have been received yet
        if (currentResult == null) {
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

        // Get the faces already scanned from adder queue
        List<Face> scannedFaces = faceScanned.getScannedFaces();
        // Diff list to show what faces are different from last frame
        List<DetectionCenter> diffFaces = new ArrayList<>(currentFaces);

        // Loop through all detected faces
        for (ArrayList<DetectionCenter> faceList : faces) {
            // Order the points within the given face
            ArrayList<DetectionCenter> points = order_face(faceList);
            // Create logic face from ordered points
            Face logicFace = new Face(points);

            // Get bounding box from ordered points
            RectF drawingBox = new RectF(
                    points.get(0).getX(),
                    points.get(2).getY(),
                    points.get(8).getX(),
                    points.get(6).getY()
            );

            // Update average width/height of face
            int width = faceDimensions.get(logicFace.getCentreColour()).x;
            int height = faceDimensions.get(logicFace.getCentreColour()).y;

            int avgWidth = (int) ((width + drawingBox.width()) / 2);
            int avgHeight = (int) ((height + drawingBox.height()) / 2);
            faceDimensions.put(logicFace.getCentreColour(), new Point(avgWidth, avgHeight));


            // If the newly scanned face is in the current faces (list of faces that were
            // previously detected last frame)
            boolean found = false;
            for (DetectionCenter faceCenter : currentFaces) {
                // Get the number of frames the face has been seen
                int timesSeen = faceSeenCount.get(faceCenter.getColour());
                if (faceCenter.getColour() == logicFace.getCentreColour()) {
                    // If face is in list
                    // Update face center value, averaging from last values
                    faceCenter.setX((int) ((faceCenter.getX() + drawingBox.centerX()) / 2));
                    faceCenter.setY((int) ((faceCenter.getY() + drawingBox.centerY()) / 2));
                    // Remove from diff list, to say the face hasn't been removed from view
                    diffFaces.remove(faceCenter);
                    // Update number of frames seen (cap the avg frame count)
                    if (timesSeen < AVERAGE_FRAME_COUNT) {
                        faceSeenCount.put(faceCenter.getColour(), timesSeen + 1);
                    }
                    found = true;
                }
            }
            // Add the face if not found in the last frame's list
            if (!found) {
                currentFaces.add(new DetectionCenter((int) drawingBox.centerX(), (int) drawingBox.centerY(), logicFace.getCentreColour()));
            }

        }


        for (DetectionCenter faceCenter : currentFaces) {
            int timesSeen = faceSeenCount.get(faceCenter.getColour());
            // Check faces in frame have been scanned and in the adder queue list
            // of faces
            boolean isScanned = false;
            for(Face face : scannedFaces){
                if (face.getCentreColour() == faceCenter.getColour()){
                    // this if could maybe cause issues due to the fact that it checks being based
                    // off of the centre colour which if guessed wrong will add values for the whole
                    // face colours wrongly
                    isScanned = true;
                    break;
                }
            }
            // Get the opacity of the face
            int opacity = faceOpacities.get(faceCenter.getColour());
            // Get average width/height
            int width = faceDimensions.get(faceCenter.getColour()).x;
            int height = faceDimensions.get(faceCenter.getColour()).y;
            // Get ratio of width / height
            float ratio = min(width,height)/(float)max(width,height);

            // Calculate bounding box for displaying the tick
            RectF drawingBox = new RectF(
                    faceCenter.getX() - width / 2,
                    faceCenter.getY() - height / 2,
                    faceCenter.getX() + width / 2,
                    faceCenter.getY() + height / 2
            );

            // Increase opacity (fade in) if the face is being seen, and has been scanned
            if (timesSeen > 0 && isScanned) {
                faceOpacities.put(faceCenter.getColour(), min(opacity + FADE_VELOCITY, 255));
            } else {
                faceOpacities.put(faceCenter.getColour(), max(opacity - FADE_VELOCITY, 0));
            }

            // Set opacity of tick, multiply the opacity by width/height ratio
            // to render thin ticks at a lighter opacity
            int white = Color.argb((int) (opacity * min(1,ratio)), 255, 255, 255);

            drawTick(canvas, drawingBox, white);
        }

        // For all frames that are different from last frame (e.g. the ones removes from
        // view
        for (DetectionCenter faceCenter : diffFaces) {
            int timesSeen = faceSeenCount.get(faceCenter.getColour());
            // Decrease number of frames seen
            if (timesSeen > 0) {
                faceSeenCount.put(faceCenter.getColour(), timesSeen - 1);
            }
            // Remove if opacity of the tick is 0
            if (faceOpacities.get(faceCenter.getColour()) == 0) {
                faceDimensions.put(faceCenter.getColour(), new Point(0,0));
            }
        }

    }

    /**
     * Draws the tick image using a given drawing bounding box
     * @param canvas Canvas to draw to
     * @param drawingBox Drawing bounding box to constrain the tick image to
     * @param colour Colour to display the tick
     */
    private void drawTick(@NonNull Canvas canvas, RectF drawingBox, int colour) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColorFilter(new PorterDuffColorFilter(colour, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(tick, null, drawingBox, paint);
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
}
