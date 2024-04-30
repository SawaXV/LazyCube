package com.logic;

import java.util.ArrayList;

/**
 * @author Alexandar Bull
 * @description Utility class used as an interface for lists of detection
 * results that can be used with the logic module and android app code
 */
public class PredictionList {
    private ArrayList<Prediction> predictions;

    public PredictionList(ArrayList<Prediction> list) {
        predictions = list;
    }

    public int getLength(){
        return predictions.size();
    }

    /**
     * Get all detection prediction percentage results, as a single list
     * @return List of prediction percentage results, with their indexes relative to
     * the {@link Prediction} object within {@code PredictionList.predictions}
     */
    public ArrayList<Integer> getPredictions() {
        ArrayList<Integer> output = new ArrayList<>();
        for(int i = 0; i < predictions.size(); i++){
            output.add(predictions.get(i).getPredictionPercent());
        }
        return output;
    }

    /**
     * Get all detection class results, as a single list
     * @return List of detection results, with their indexes relative to
     * the {@link Prediction} object within {@code PredictionList.predictions}
     */
    public ArrayList<Integer> getClasses() {
        ArrayList<Integer> output = new ArrayList<>();
        for(int i = 0; i < predictions.size(); i++){
            output.add(predictions.get(i).getClassIndex());
        }
        return output;
    }

    /**
     * Get all detection box dimension results, as a single list
     * @return List of box dimension results, with their indexes relative to
     * the {@link Prediction} object within {@code PredictionList.predictions}
     */
    public ArrayList<int[]> getBoxes() {
        ArrayList<int[]> output = new ArrayList<>();
        for(int i = 0; i < predictions.size(); i++){
            output.add(predictions.get(i).getBox());
        }
        return output;
    }
}
