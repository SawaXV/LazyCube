package com.uon.lazycube;

import static com.logic.FaceDetector.get_faces;
import static com.logic.FaceDetector.order_face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import com.logic.AdderQueue;
import com.logic.Cube;
import com.logic.CubeColour;
import com.logic.CubeValidator;
import com.logic.DetectionCenter;
import com.logic.Face;
import com.logic.Prediction;
import com.logic.PredictionList;
import com.logic.Square;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Detector object used to run TFL object detection on a given image.
 * Currently using the "lazycubedet0-s3v2.tflite" detection model.
 * <br>
 * Treated as the subject within the observer pattern used to notify
 * detection results.
 */
public class Detector implements IFaceScanned{

    // Prediction probability threshold
    private final double THRESHOLD = 0.75;
    // Number of threads used for detection
    private final int NUM_THREADS = 2;
    // Number of max results to detect
    private final int NUM_RESULTS = 30;
    // Time (in ms) until a complete fail message appears
    private final long COMPLETE_FAIL_LENGTH = 15000;
    // Time (in ms) until cube rotation hint is shown
    private final long ROTATE_HINT_LENGTH = 5000;

    // TFL object detection object
    private ObjectDetector objectDetector;
    // Android app context
    private final Context context;
    // Observers to notify of detection changes
    private final ArrayList<DetectionObserver> observers = new ArrayList<>();

    // Cube completed callback to invoke when correct internal cube config
    // has been found
    private DetectionProgress detectionProgressCallback;

    // Adder queue to submit scanned faces to the face adder
    private final AdderQueue adderQueue = new AdderQueue();
    // Whether the correct internal cube config has been found
    private boolean completed = false;
    // Time when first fail message was sent
    private long failTime = 0;
    // Time since last successful face scan
    private long lastFaceScanTime = 0;
    // Last length of the scanned face list
    private int lastFaceListLength = -1;
    // Whether a complete fail has been made
    private boolean failed = false;
    // Used to count how many times a face is failed to be added
    private final HashMap<CubeColour, Integer> failCount = new HashMap<>();

    /**
     * Constructs a new Detector object for TFL object detection using the
     * "lazycubedet0-batch200.tflite" detection model.
     * @param context Context for current application
     */
    public Detector(Context context) {
        this.context = context;
        // Initialise TFL detector object
        initDetector();
    }

    /**
     * Registers a new detection observer to be notified when
     * new detection results have been made
     * @param observer DetectionObserver to late notify
     */
    public void registerObserver(DetectionObserver observer) {
        observers.add(observer);
    }


    public void setTurbo(boolean state) {
        adderQueue.setTurbo(state);
    }


    /**
     * Set callback to call when the face has been completed scanned
     * correctly.
     * @param callback
     */
    public void addCompleteCallback(DetectionProgress callback) {
        detectionProgressCallback = callback;
    }

    /**
     * Initialises the TFL object detection object used for inference
     */
    private void initDetector() {

        // Configure Java Task threads
        BaseOptions baseOptions = BaseOptions.builder()
                .setNumThreads(NUM_THREADS)
                .build();

        // Configure TFL object detector
        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setScoreThreshold((float) THRESHOLD)
                        .setMaxResults(NUM_RESULTS)
                        .setBaseOptions(baseOptions)
                        .build();

        try {
            // Load model
            objectDetector = ObjectDetector.createFromFileAndOptions(context,
                    "lazycubedet0.tflite", options);
        } catch (IOException e) {
            System.out.println("Unable to init object detector");
            e.printStackTrace();
        }

    }

    /**
     * Converts TFL detection results to a PredictionList
     * class compatible with LazyCubeLogic code.
     * <br> Resizes bounding boxes to the correct size for
     * the phone's screen, given a scaling factor
     *
     * @param results TFL detection result object
     * @param factor Factor for rescaling bounding boxes by
     * @return PredictionList object to use for internal cube logic
     */
    public static PredictionList detectionToPredictionList(DetectionResult results, float factor) {
        final int[] detectionClassIndexes = {3, 1, 0, 2, 4, 5, 6};

        ArrayList<Prediction> predictions = new ArrayList<>();
        // For every TFL result, add to the prediction list
        for (Detection result : results.getResults()) {
            // Get bounding box and resize to be the correct size for the phone
            RectF boundingBox = result.getBoundingBox();
            int[] box = {(int) (boundingBox.left * factor),
                    (int) (boundingBox.top * factor),
                    (int) (boundingBox.right * factor),
                    (int) (boundingBox.bottom * factor)};

            int detectionIndex = result.getCategories().get(0).getIndex();
            int classIndex = detectionClassIndexes[detectionIndex];
            // TODO scores/prediction percentages not used
            Prediction prediction = new Prediction(
                    (int) (result.getCategories().get(0).getScore() * 100),
                    box,
                    classIndex
            );
            predictions.add(prediction);
        }
        return new PredictionList(predictions);
    }

    /**
     * Add detection results into the internal cube via the adder queue,
     * if the specific conditions are met
     * @param detectionResult Detection results to add
     */
    private void addResults(DetectionResult detectionResult) {
        long start = System.currentTimeMillis();
        // Get list of predictions for internal logic to use
        PredictionList predictions = detectionToPredictionList(detectionResult, 1.0f);
        // Get list of faces, an ordered list of points from top left
        // to bottom right
        ArrayList<ArrayList<DetectionCenter>> faces = get_faces(predictions);
        // Set face detection time
        DebugInfo info = DebugInfo.getInstance();
        info.setFaceDetectionTime(System.currentTimeMillis() - start);

        // For every face, display indexes of each square, from 0 to 8
        // (top left to bottom right)
        for (ArrayList<DetectionCenter> face : faces) {
            ArrayList<DetectionCenter> points = order_face(face);
            // Create logic face from ordered points
            Face logicFace = new Face(points);
            // Add to adder queue
            adderQueue.addElement(logicFace);
        }

        start = System.currentTimeMillis();
        // Add faces to the cube that the queue is "confident" enough to be correct
        List<Face> failedFaces = adderQueue.addCandidateFaces();
        // Set face adder time metric to debug info
        info.setFaceAdderTime(System.currentTimeMillis() - start);

        // If there is an unfixable parity issue
        if (failedFaces == null) {
            detectionProgressCallback.notifyFail();
            return;
        }

        // Notify scanning fragment of faces failed to be added to
        // internal cube representation, needing to be rescanned
        for (Face face : failedFaces) {
            detectionProgressCallback.notifyFaceRescan(face.getCentreColour());
        }

        // Get current working internal cube
        Cube internalCube = adderQueue.getCube();
        // Update debug info with current scanned faces, failed faces and the cube
        debugShowFaces("Scanned: ", adderQueue.getFaceQueue(), info);
        debugShowFaces("FAILED: ", failedFaces, info);
        debugShowCube(internalCube, info);


        // If new face added to adder queue
        if (lastFaceListLength != adderQueue.getQueueLength()) {
            lastFaceListLength = adderQueue.getQueueLength();
            lastFaceScanTime = System.currentTimeMillis();

            // Update that a new face has been scanned
            if (adderQueue.getQueueLength() != 0) {
                CubeColour lastScanned = getLastScannedFace();
                detectionProgressCallback.notifyFaceScan(lastScanned);
            }
        }

        long scanTimeDelta = System.currentTimeMillis() - lastFaceScanTime;
        long failTimeDelta = System.currentTimeMillis() - failTime;

        // If the time since last new scan is long enough
        if (scanTimeDelta > ROTATE_HINT_LENGTH && failTime == 0) {
            // Notify to display rotation hint
            detectionProgressCallback.notifyProgress(null, ScanProgress.ROTATE_HINT);
        } else {
            // If new face scanned, notify to hide rotation hint
            detectionProgressCallback.notifyProgress(null, ScanProgress.ROTATE_HINT_STOP);
        }

        // If faces failed to be added to the cube representation (e.g. some
        // scanning error)
        if (failedFaces.size() > 0 && failTime == 0) {
            // Notify of "soft" scan error
            detectionProgressCallback.notifyProgress(null, ScanProgress.FAIL);

            // Resets the times seen on faces that failed to enter >= FAIL_THRESH times
            resetFaces(failedFaces);

            failTime = System.currentTimeMillis();
        }
        // If faces failed to be added, but wasn't fixed for x seconds (e.g. 20s)
        else if (failTimeDelta > COMPLETE_FAIL_LENGTH && !failed && failTime != 0) {
            // Notify suggestion of complete fail
            detectionProgressCallback.notifyProgress(null, ScanProgress.COMPLETE_FAIL);
            //resetFaces(CubeColour.values());
            failed = true;
        }

        // If the face adder cube is value
        // If the cube is complete (no nulls) and the cube is value
        if (internalCube.isComplete()
                && new CubeValidator(internalCube).isCubeValid()) {
            // Set detection as completed
            completed = true;
            // Invoke call back to camera activity
            detectionProgressCallback.notifyProgress(internalCube, ScanProgress.COMPLETED);
        }
    }

    /**
     * Notifies a "face rescan" for each face failed to be added
     * to the internal cube that have failed to be specified number
     * of times
     * @param failedFaces Failed faces added to the internal cube
     */
    private void resetFaces(List<Face> failedFaces){
        for (Face face : failedFaces){
            CubeColour colour = face.getCentreColour();
            // Update failed count for the face
            if(failCount.containsKey(colour)){
                failCount.replace(colour, failCount.get(colour) + 1);
            }
            else {
                failCount.put(colour, 1);
            }
            // If already failed before, notify to rescan the face again
            if(failCount.get(colour) >= 1){
                System.out.println("RESET FACE: " + colour);
                detectionProgressCallback.notifyFaceRescan(colour);
                adderQueue.resetFace(colour);
                failCount.replace(colour, 0);
            }
        }
    }
    private void resetFaces(CubeColour[] colours){
        for (CubeColour colour : colours){
            if(failCount.containsKey(colour)){
                failCount.replace(colour, failCount.get(colour) + 1);
            }
            else {
                failCount.put(colour, 1);
            }
            if(failCount.get(colour) >= 1){
                System.out.println("RESET FACE: " + colour);
                detectionProgressCallback.notifyFaceRescan(colour);
                adderQueue.resetFace(colour);
                failCount.replace(colour, 0);
            }
        }
    }


    /**
     * Gets the last face scanned colour put in the adder buffer
     * @return Center colour of last face scanned
     */
    private CubeColour getLastScannedFace() {
        Face lastFaceScanned = adderQueue.getFaceQueue().get(adderQueue.getQueueLength() - 1);
        CubeColour lastScanned = lastFaceScanned.getCentreColour();
        return lastScanned;
    }

    /**
     * Updates debug log with a formatted message about information
     * of a specific face. E.g. "Scanned: Blue"
     * @param msg Prefix message before face colour
     * @param faceList List of faces to add to debug log
     * @param info DebugInfo object instance
     */
    private void debugShowFaces(String msg, List<Face> faceList, DebugInfo info) {
        for (Face face : faceList) {
            info.addLog(msg + face.getCentreColour());
        }
    }

    /**
     * Updates debug log formatted representation of the cube, as
     * single chars for each colour.
     * @param cube Cube representation to display
     * @param info DebugInfo object instance
     */
    private void debugShowCube(Cube cube, DebugInfo info) {
        String line = "";
        int index = 0;
        // For each colour in 54 length
        for (Square square : cube.getLongArray()) {
            // Get first character in each colour
            line += square.getColour().toString().charAt(0) + " ";
            index++;
            // Add new line every 9 colours (a face)
            if (index % 9 == 0) {
                info.addLog(line);
                line = "";
            }
        }
    }

    /**
     * Runs TFL object detection inference on the given bitmap image
     * @param bitmap Image to run inference on
     */
    public void detect(Bitmap bitmap) {
        // Stop detection when the config has been found
        if (completed)
            return;
        long start = System.currentTimeMillis();
        // Rotate image for processing
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new Rot90Op(-1))
                .build();
        // Add image processing
        TensorImage processedImage = imageProcessor.process(
                TensorImage.fromBitmap(bitmap)
        );
        // Run inference
        List<Detection> results = objectDetector.detect(processedImage);
        DetectionResult detectionResult = new DetectionResult(results, processedImage.getWidth(),
                processedImage.getHeight());
        // Notify observers of new results
        for (DetectionObserver observer : observers) {
            observer.notifyResults(detectionResult);
        }
        // Set inference time debug info
        long inferTime = System.currentTimeMillis() - start;
        DebugInfo info = DebugInfo.getInstance();
        info.setInferenceTime(inferTime);

        // Add potential face results to the internal cube
        addResults(detectionResult);
    }

    @Override
    public List<Face> getScannedFaces() {
        return adderQueue.getFaceQueue();
    }
}
