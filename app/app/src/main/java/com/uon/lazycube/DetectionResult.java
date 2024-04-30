package com.uon.lazycube;

import org.tensorflow.lite.task.vision.detector.Detection;
import java.util.List;

/**
 * Class for holding TFL object detection results
 */
public class DetectionResult {
    // List of detection result objects
    private final List<Detection> results;
    // Height and width in pixels of the image used for detection
    private final int imageWidth;
    private final int imageHeight;

    public List<Detection> getResults() {
        return results;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Constructs a new DetectionResult object to hold detection results
     * @param results TFL detection result objects
     * @param width Width of image used for detection
     * @param height Height of image used for detection
     */
    DetectionResult(List<Detection> results, int width, int height) {
        this.results = results;
        this.imageWidth = width;
        this.imageHeight = height;
    }
}
