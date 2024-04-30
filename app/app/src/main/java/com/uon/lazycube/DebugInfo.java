package com.uon.lazycube;

import java.util.ArrayList;

/**
 * Class for storing debug info of different execution
 * times for stages within the detection pipeline
 */
public class DebugInfo {
    // Time (in ms) for TFL inference
    private long inferenceTime = 0;
    // Time (in ms) for face detection and ordering
    private long faceDetectionTime = 0;
    // Time (in ms) for adding a face to the internal cube rep.
    private long faceAdderTime = 0;
    // Debug log output
    private final ArrayList<String> logOutput = new ArrayList<>();

    // Singleton object
    private static DebugInfo instance;

    /**
     * Get the singleton object for debug info
     * @return Get the singleton instance
     */
    public static DebugInfo getInstance() {
        if (instance == null)
            instance = new DebugInfo();
        return instance;
    }

    public long getInferenceTime() {
        return inferenceTime;
    }

    public long getFaceDetectionTime() {
        return faceDetectionTime;
    }

    public long getFaceAdderTime() {
        return faceAdderTime;
    }

    public ArrayList<String> getLogOutput() {return logOutput;};

    /**
     * Get the fps for scanning
     * @return number of scans performed per second
     */
    public float getFps() {
        float fps = 1 / (((float) this.inferenceTime + this.faceDetectionTime + this.faceAdderTime) / 1000);
        return fps;
    }

    public void setInferenceTime(long inferenceTime) {
        this.inferenceTime = inferenceTime;
    }

    public void setFaceDetectionTime(long faceDetectionTime) {
        this.faceDetectionTime = faceDetectionTime;
    }

    public void setFaceAdderTime(long faceAdderTime) {
        this.faceAdderTime = faceAdderTime;
    }

    /**
     * Add debug log output to display to the screen
     * @param output Text to display on a line in the log output
     */
    public void addLog(String output) {
        logOutput.add(output);
    }

    /**
     * Removes all log data
     */
    public void clearLog() {
        logOutput.clear();
    }
}
