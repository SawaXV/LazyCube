package com.logic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Alexander Bull
 * @editor Alfred Roberts
 * @description Generates a single face for a cube
 */
public class Face implements Serializable {
    private final int CENTER_INDEX = 4;
    private Square[] squareList = new Square[9];

    /**
     * Constructs a new face, from a list of squares
     * @param faceList - List of square objects for each square in the face
     */
    public Face(Square[] faceList) {
        squareList = faceList;
    }

    /**
     * Generates a face with a specified middle
     * @param middle - Colour of the middle cube of a face
     */
    public Face(CubeColour middle)
    {
        for(int loop = 0 ; loop < 9 ; loop++)
        {
            squareList[loop] = new Square();
        }
        squareList[CENTER_INDEX].setColour(middle, 1);
    }

    /**
     * Generates a generic face
     */
    public Face()
    {
        for(int loop = 0 ; loop < 9 ; loop++)
        {
            squareList[loop] = new Square();
        }
    }

    public Face(ArrayList<DetectionCenter> points) {
        for(int i = 0; i < 9; i++)
        {
            squareList[i] = new Square();
            squareList[i].setColour(points.get(i).getColour(),0);
        }
    }

    /**
     * Gets the minimum number of frames each square colour has been found in.
     * Used as a metric to check how confidence we are that the face is
     * correctly scanned.
     * @return Minimum frames across all squares
     */
    public int getMinTimesSeen() {
        int minValue = Integer.MAX_VALUE;
        for(int i = 0; i < 9; i++)
        {
            if (squareList[i].getTimesSeen() < minValue) {
                minValue = squareList[i].getTimesSeen();
            }
        }
        return minValue;
    }

    /**
     * @return Centre colour of a face
     */
    public CubeColour getCentreColour()
    {
        return squareList[CENTER_INDEX].getColour();
    }

    /**
     * @param index - Square to be indexed
     * @return Colour of indexed square
     */
    public CubeColour getSquareColour(int index)
    {
        return squareList[index].getColour();
    }

    /**
     * @param index - Square to be indexed
     * @return The indexed square
     */
    public Square getSquare(int index)
    {
        return squareList[index];
    }

    /**
     * Sets the specified square's colour and confidence
     * @param index - Square to be indexed
     * @param colour - Colour to set that square
     * @param timesSeen - Value of times this colour has been "seen"
     */
    public void setSquare(int index, CubeColour colour, int timesSeen)
    {
        squareList[index].setColour(colour, timesSeen);
    }

    /**
     * Adds 1 to the value stored in square at the given index for how many times the given colour has been seen in that Square's HashMap
     * @param index - Square to be indexed
     * @param colour - Colour to have its stored value incremented
     */
    public void addSquareSeen(int index, CubeColour colour){
        squareList[index].incrementColour(colour);
    }

    /**
     * Sets the specified square to another square
     * @param index - Square to be indexed
     * @param square - Square to replace indexed square
     */
    public void setSquare(int index, Square square)
    {
        squareList[index].setColour(square.getColour(), square.getTimesSeen());
    }

    /**
     * Rotates the face 90 degrees clockwise
     * @return Newly rotated face
     */
    public Face rotate() {
        int[] rotationIndex = new int[]{6,3,0,7,4,1,8,5,2};
        Face newFace = new Face();
        for(int i = 0; i < 9; i++){
            int moveIndex = rotationIndex[i];
            newFace.setSquare(i, this.getSquare(moveIndex));
        }
        return newFace;
    }

    /**
     * Checks whether the face has all its squares as null
     * @return <code>true</code> if al the squares in the face are null
     * (excluding the middle colour), <code>false</code> otherwise
     */
    public boolean isEmpty() {
        for (int i = 0; i < 9; i ++) {
            if (i == CENTER_INDEX)
                continue;
            if (squareList[i].getColour() != CubeColour.NULL)
                return false;
        }
        return true;
    }

    /**
     * Converts face into input format of solver (Cubot.io)
     * String of 9 colour characters (represented by first letter of colour)
     * @return Face String
     */
    public String toCubotString()
    {
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<9; i++)
        {
            buffer.append(squareList[i].getColour().toString().charAt(0));
        }

        return buffer.toString();
    }

    /**
     * Converts face into input format of solver (Min2Phase)
     * String of 9 direction characters, given O is Front & W is Up
     * This converts with:
     *   W            U
     * B O G R  ->  L F R B
     *   Y            D
     * @return Face String
     */
    public String toMin2PhaseString()
    {
        String s = toCubotString();

        //System.out.println("Before Regex: " +s);

        s=s.replaceAll("W","U");
        s=s.replaceAll("B","L");
        s=s.replaceAll("R","B");
        s=s.replaceAll("G","R");
        s=s.replaceAll("O","F");
        s=s.replaceAll("Y","D");

        //System.out.println("After regex: " +s);

        return s;
    }


    /**
     * Converts face into input format of display (animcube)
     * One string of 9 digits
     * In order: 0:White,1:Yellow,2:Orange,3:Red,4:Blue,5:Green
     * @return Array of face strings
     */
    public String toDisplayString()
    {
        StringBuffer buffer = new StringBuffer();
        int[] reverseIndexes = {6,7,8,3,4,5,0,1,2};
        for(int i=0; i<9; i++)
        {
            switch(squareList[reverseIndexes[i]].getColour()) {
                case WHITE:
                    buffer.append(0);
                    break;
                case YELLOW:
                    buffer.append(1);
                    break;
                case ORANGE:
                    buffer.append(2);
                    break;
                case RED:
                    buffer.append(3);
                    break;
                case BLUE:
                    buffer.append(4);
                    break;
                case GREEN:
                    buffer.append(5);
                    break;
            }
        }

        return buffer.toString();
    }
}
