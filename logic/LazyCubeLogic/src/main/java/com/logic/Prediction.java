package com.logic;

/**
 * @author Alexander Bull
 * @description Utility class used as an interface for detection results that can
 * be used with the logic module and android app code
 */
public class Prediction {

    // Percent confidence for the given detection result
    private int predictionPercent;
    // List of box coordinates for the object detection results
    private int[] box;
    // Class index of the prediction result
    private int classIndex;
    // Notation for the classes value is as follows:
    // 0 =
    // 1 =
    // 2 =
    // 3 =
    // 4 =
    // 5 =
    // 6 = face

    public Prediction(int predictionPercent, int[] box, int classIndex) {
        this.predictionPercent = predictionPercent;
        this.box = box;
        this.classIndex = classIndex;
    }

    public int getPredictionPercent() {
        return predictionPercent;
    }

    public int[] getBox() {
        return box;
    }

    public int getClassIndex() {
        return classIndex;
    }

}
