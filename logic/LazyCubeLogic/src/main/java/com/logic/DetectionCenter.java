package com.logic;

/**
 * @author Alfred Roberts
 * @description Utility class used between the logic and android app modules to store
 * x,y 2d coordinates for detection results, along with their respective detected
 * colour
 */
public class DetectionCenter {
    private int x;
    private int y;
    private CubeColour colour;

    /**
     * Creates a new detection center object to store x,y coordinates
     * @param x Center X coordinate of detection object
     * @param y Center Y coordinate of detection object
     * @param colour Colour of detected object
     */
    public DetectionCenter(int x, int y, CubeColour colour) {
        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public CubeColour getColour() {
        return colour;
    }
}
