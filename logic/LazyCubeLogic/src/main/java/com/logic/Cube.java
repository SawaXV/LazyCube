package com.logic;

import static cs.min2phase.Tools.verify;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Alfred Roberts
 * @editor Alexander Bull
 * @description Generates a Rubik's cube with either a default preset or with specified faces
 */
public class Cube implements Serializable {
    private Face[] faceList = new Face[6];
    private final ArrayList<Square> longArray = new ArrayList<>();

    /**
     * Creates a cube with specified faces
     * @param faceList - A list of cube faces
     */
    public Cube(Face[] faceList) {
        this.faceList = faceList;

        setLongArray();
    }

    /**
     * Creates a cube with an array of integers
     * @param inputArray - Array of integers
     */
    public Cube(int[] inputArray) {
        faceList = new Face[6];

        for(int i = 0; i<6;i++)
        {
            faceList[i]=new Face();
            for(int j = 0; j<9; j++)
            {
                faceList[i].setSquare(j, CubeColour.values()[inputArray[9*i+j]],0);
            }
        }

        setLongArray();
    }

    /**
     * Creates a cube from another. Used for copying one
     * cube object to another
     * @param cube Cube object to copy
     */
    public Cube(Cube cube) {
        faceList = new Face[6];

        for(int i = 0; i<6;i++)
        {
            faceList[i]=new Face();
            for(int j = 0; j<9; j++)
            {
                faceList[i].setSquare(j, cube.getFace(i).getSquare(j).getColour(),
                        cube.getFace(i).getSquare(j).getTimesSeen());
            }
        }
    }

    /**
     * Updates long array representation of the cube
     */
    private void setLongArray()
    {
        longArray.clear();
        for (Face face: faceList)
        {
            for(int loop = 0 ; loop <9 ; loop++)
            {
                longArray.add(face.getSquare(loop));
            }
        }
    }

    /**
     * Get the long array of a cube to refer to its squares
     * @return Cube long array
     */
    public ArrayList<Square> getLongArray()
    {
        return longArray;
    }
    
    /**
     * Gets the face from an index value, 0 for orange, 1 for green...
     * @param index - Index of the face to get
     * @return Face object 
     */
    public Face getFace(int index) {
        return faceList[index];
    }

    /**
     * Sets a face at the specified index.
     * @param index - Index of the face to set
     * @param face - New face to set to
     */
    public void setFace(int index, Face face) {
        faceList[index] = new Face();
        for(int i = 0; i < 9; i ++) {
            faceList[index].setSquare(i, face.getSquare(i));
        }
        setLongArray();
    }

    /**
     * Gets the neighboring faces of a given face within the cube
     * @param index - Index of the face to find neighbors of
     * @return An array of indexes of the 4 neighboring faces
     */
    public int[] getFaceNeighbors(int index) {
        if (index >= 4) {
            return new int[]{0,1,2,3};
        }
        int offset = 1 - (index % 2);
        return new int[]{offset, 2 + offset ,4,5};
    }


    /**
     * Setup cube by applying all faces with their default colours
     */
    public Cube()
    {
        faceList = new Face[6];
        faceList[0] = new Face(CubeColour.ORANGE);
        faceList[1] = new Face(CubeColour.GREEN);
        faceList[2] = new Face(CubeColour.RED);
        faceList[3] = new Face(CubeColour.BLUE);
        faceList[4] = new Face(CubeColour.WHITE);
        faceList[5] = new Face(CubeColour.YELLOW);

        setLongArray();
    }

    /**
     * Converts cube into input format of solver (Cubot.io)
     * 6 face strings of 9 colour characters (represented by first letter of colour)
     * In order: Red, Green, Orange, Blue, White, Yellow
     * @return Array of face strings
     */
    public String[] toCubotStringArray()
    {
        String result[] = new String[6];

        result[0]=faceList[2].toCubotString();
        result[1]=faceList[1].rotate().rotate().toCubotString();
        result[2]=faceList[0].rotate().rotate().toCubotString();
        result[3]=faceList[3].rotate().rotate().toCubotString();
        result[4]=faceList[4].rotate().rotate().toCubotString();
        result[5]=faceList[5].rotate().rotate().toCubotString();

        return result;
    }

    /**
     * Converts cube into input format of solver (Min2Phase)
     * Single string of 54 direction characters
     * Assumes O is Front & W is Up
     * This converts with:
     *   W            U
     * B O G R  ->  L F R B
     *   Y            D
     * In order URFDLB
     * @return Direction Strings
     */
    public String toMin2PhaseString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(faceList[4].rotate().rotate().rotate().toMin2PhaseString());
        buffer.append(faceList[1].toMin2PhaseString());
        buffer.append(faceList[0].toMin2PhaseString());
        buffer.append(faceList[5].rotate().toMin2PhaseString());
        buffer.append(faceList[3].toMin2PhaseString());
        buffer.append(faceList[2].toMin2PhaseString());

        return buffer.toString();
    }


    /**
     * Converts cube into input format of display (animcube)
     * One string of 54 digits, 6 faces of 9 squares (not seperated)
     * In order: 0:White (U),1:Yellow (D),2:Orange (F),3:Red (B),4:Blue (L),5:Green (R)
     * @return Display String
     */
    public String toDisplayString()
    {
        StringBuffer buffer = new StringBuffer();

//        Original
//        buffer.append(faceList[5].rotate().rotate().toDisplayString());
//        buffer.append(faceList[4].rotate().toDisplayString());
//        buffer.append(faceList[1].rotate().toDisplayString());
//        buffer.append(faceList[3].rotate().toDisplayString());
//        buffer.append(faceList[2].toDisplayString());
//        buffer.append(faceList[0].rotate().toDisplayString());

        //Reworked so min2phase & animcube line up
        buffer.append(faceList[4].rotate().rotate().rotate().toDisplayString());
        buffer.append(faceList[5].toDisplayString());
        buffer.append(faceList[0].rotate().rotate().rotate().toDisplayString());
        buffer.append(faceList[2].rotate().rotate().rotate().toDisplayString());
        buffer.append(faceList[3].rotate().rotate().toDisplayString());
        buffer.append(faceList[1].rotate().rotate().rotate().toDisplayString());


        return buffer.toString();
    }

    /**
     * Helper method to print the cube as 6 rows of 9 colours
     */
    public void show() {
        for (int i = 0; i < 54; i++) {
            if (i % 9 == 0)
                System.out.println();
            System.out.print(getLongArray().get(i).getColour() + " ");
        }
        System.out.println(" ");
    }

    /**
     * Checks if the cube is complete. That is, the cube has no null values
     * within its representation. <b>This does not mean it is a valid cube</b>,
     * only that all the square values have been updated.
     * @return Whether all cube values have been updated or not.
     */
    public boolean isComplete() {
        int nullCount = 0;
        for (Face face: faceList)
        {
            for(int i = 0 ; i <9 ; i++)
            {
                if (face.getSquare(i).getColour() == CubeColour.NULL)
                    nullCount ++;
            }
        }
        return nullCount == 0;
    }

    /**
     * Check if a parity fail has been generated through the min2phase solver
     * library
     * @return Whether the cube configuration contains a parity error or not
     */
    public boolean parityCheckFail() {
        String min2phaseStr = toMin2PhaseString();
        int error = verify(min2phaseStr);
        return error != 0;
    }
}
