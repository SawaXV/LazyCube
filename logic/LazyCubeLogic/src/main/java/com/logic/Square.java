package com.logic;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Alexander Bull
 * @description Generates a single square belonging to a face
 */
public class Square implements Serializable
{
    private final HashMap<CubeColour, Integer> confidenceMap = new HashMap<>();

    private CubeColour colourValue = null;

    /**
     * Creates square with confidence values set to 0
     */
    public Square()
    {
        confidenceMap.put(CubeColour.WHITE, 0);
        confidenceMap.put(CubeColour.YELLOW, 0);
        confidenceMap.put(CubeColour.BLUE, 0);
        confidenceMap.put(CubeColour.GREEN, 0);
        confidenceMap.put(CubeColour.RED, 0);
        confidenceMap.put(CubeColour.ORANGE, 0);
        confidenceMap.put(CubeColour.NULL, 0);
        colourValue = CubeColour.NULL;
    }

    /**
     * @return Colour value of square
     */
    public CubeColour getColour()
    {
        return colourValue;
    }

    /**
     * Sets the value shown by the square
     * @param colourValue colour to be set to
     */
    public void setColourValue(CubeColour colourValue) {
        this.colourValue = colourValue;
    }

    /**
     * @return Colour confidence of square
     */
    public int getTimesSeen()
    {
        return confidenceMap.get(colourValue);
    }

    /**
     * Get the second-highest time seen (confidence) colour value, e.g. the
     * colour that has been detected the second most for this square
     * @return The {@link CubeColour} of the second-highest time seen colour
     */
    public CubeColour getSecondTimesSeenColour(){
        int highest = Integer.MIN_VALUE;
        int secondHighest = Integer.MIN_VALUE;
        CubeColour secondHighestColor = null;

        for (CubeColour colour : CubeColour.values()) {
            int frequency = confidenceMap.getOrDefault(colour, 0);
            if (frequency > highest) {
                secondHighest = highest;
                highest = frequency;
                secondHighestColor = colour;
            } else if (frequency > secondHighest && frequency < highest) {
                secondHighest = frequency;
                secondHighestColor = colour;
            }
        }

        return secondHighestColor;
    }

    /**
     * Get the second-highest time seen (confidence) count, e.g. the
     * number of times the second most detected colour for this square has been
     * detected for
     * @return The number of times the second-highest time seen colour has been
     * detected
     */
    public int getSecondTimesSeenCount(){
        int highest = Integer.MIN_VALUE;
        int secondHighest = Integer.MIN_VALUE;

        for (CubeColour colour : CubeColour.values()) {
            int frequency = confidenceMap.getOrDefault(colour, 0);
            if (frequency > highest) {
                secondHighest = highest;
                highest = frequency;
            } else if (frequency > secondHighest && frequency < highest) {
                secondHighest = frequency;
            }
        }

        return secondHighest;
    }

    /**
     * Replaces current value for given colour confidence with new set confidence
     * @param colour - The colour of the square that was detected
     * @param timesSeen - The confidence value of the square that was detected
     */
    public void setColour(CubeColour colour, int timesSeen)
    {
        //The below if statement updates the set ColourValue to make sure that if the new colour is more confident that the saved one it will be replaced
        if (colourValue == CubeColour.NULL || (confidenceMap.get(colourValue) < timesSeen))
        {
            colourValue = colour;
        }
        //the else if checks if the current set colour has had its confidence decreased it checks for if it should be replaced as the saved colour value
        else if (colourValue == colour && timesSeen < confidenceMap.get(colourValue))
        {
            colourCheck();
        }
        confidenceMap.put(colour, timesSeen);
    }

    /**
     * Adds 1 to the value stored for how many times the given colour has been seen in the Square's HashMap
     * @param colour Colour to increment storage value of
     */
    public void incrementColour(CubeColour colour){
        confidenceMap.put(colour, confidenceMap.get(colour) + 1);
        colourCheck();
    }

    /**
     * Loops through all colours and checks if their confidence is higher than the current saved value
     */
    private void colourCheck()
    {
        Arrays.asList(CubeColour.values()).forEach((col) ->
            {
                if (confidenceMap.get(col) > confidenceMap.get(colourValue))
                    colourValue = col;
            });
    }
}
