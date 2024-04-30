package com.logic;

import java.util.*;

/**
 * @author Alexander Bull
 * @editor Sarwar Rashid, Alfie Inman, Alfred Roberts
 * @description Responsible for validating a correct cube coniguration
 */
public class CubeValidator {
    private final Cube cube;
    private final int[] corners = {0, 2, 6, 8, 18, 20, 24, 26};
    private final int[][] otherCorners = {{36, 29}, {9, 42}, {35, 51}, {45, 15}, {44, 11}, {27, 38}, {17, 47}, {53, 33}};

    /**
     * Pass a specified cube to enact as the cube being validated
     * @param cube - Parameter cube to become the class' cube
     */
    public CubeValidator(Cube cube)
    {
        this.cube = cube;
    }

    /**
     * Calls all four validation methods for a cube configuration
     * @return Boolean value from any of the methods, true if correct configuration, false otherwise
     */
    public boolean isCubeValid()
    {
        return centresAreValid() && edgesAreValid() && countsAreValid() && cornersAreValid() && duplicateCorners();
    }

    /**
     * Checks whether the centre of a cube is of a valid colour
     * @return Returns true if valid, false otherwise
     */
    public boolean centresAreValid()
    {
        if(cube.getLongArray().get(4).getColour()!=CubeColour.ORANGE) return false;
        if(cube.getLongArray().get(13).getColour()!=CubeColour.GREEN) return false;
        if(cube.getLongArray().get(22).getColour()!=CubeColour.RED) return false;
        if(cube.getLongArray().get(31).getColour()!=CubeColour.BLUE) return false;
        if(cube.getLongArray().get(40).getColour()!=CubeColour.WHITE) return false;
        return cube.getLongArray().get(49).getColour() == CubeColour.YELLOW;
    }

    /**
     * Contains a valid set of edge pairs
     */
    private static class EdgePair
    {
        public CubeColour colOne;
        public CubeColour colTwo;

        public EdgePair(CubeColour colOne, CubeColour colTwo) {
            this.colOne = colOne;
            this.colTwo = colTwo;
        }

        @Override
        public boolean equals(Object object)
        {
            if (object == this) {
                return true;
            }

            if (!(object instanceof EdgePair)) {
                return false;
            }

            EdgePair instance = (EdgePair) object;
            return instance.colOne == colOne && instance.colTwo == colTwo
                    || instance.colOne == colTwo && instance.colTwo == colOne;
        }
    }

    /**
     * Check if a particular edge pair has valid pairings - e.g. blue cannot go
     * with green
     * @param pair Pair object to check
     * @return True if the edge colours are invalid
     */
    public boolean edgeIsNotValid(EdgePair pair) {
        if (pair.colOne == CubeColour.GREEN && pair.colTwo == CubeColour.BLUE) { return true; }
        if (pair.colOne == CubeColour.WHITE && pair.colTwo == CubeColour.YELLOW) { return true; }
        if (pair.colOne == CubeColour.ORANGE && pair.colTwo == CubeColour.RED) { return true; }
        if (pair.colOne == CubeColour.BLUE && pair.colTwo == CubeColour.GREEN) { return true; }
        if (pair.colOne == CubeColour.YELLOW && pair.colTwo == CubeColour.WHITE) { return true; }
        if (pair.colOne == CubeColour.RED && pair.colTwo == CubeColour.ORANGE) { return true; }
        if (pair.colOne == pair.colTwo) { return true; }
        return false;
    }

    /**
     * Check if an edge pair is in the correct order given a colour
     * e.g. A white corner piece should not follow green and orange but orange and green
     * @param colour Main colour to be checked against
     * @param pair Pair object to check
     * @return True if the edge order is invalid
     */
    public boolean edgeOrderNotValid(CubeColour colour, EdgePair pair) {
        if(colour == CubeColour.ORANGE){
            if(pair.colOne == CubeColour.GREEN && pair.colTwo == CubeColour.YELLOW) { return true; }
            if(pair.colOne == CubeColour.WHITE && pair.colTwo == CubeColour.GREEN) { return true; }
            if(pair.colOne == CubeColour.BLUE && pair.colTwo == CubeColour.WHITE) { return true; }
            if(pair.colOne == CubeColour.YELLOW && pair.colTwo == CubeColour.BLUE) { return true; }
        }
        if(colour == CubeColour.GREEN){
            if(pair.colOne == CubeColour.RED && pair.colTwo == CubeColour.YELLOW) { return true; }
            if(pair.colOne == CubeColour.WHITE && pair.colTwo == CubeColour.RED) { return true; }
            if(pair.colOne == CubeColour.ORANGE && pair.colTwo == CubeColour.WHITE) { return true; }
            if(pair.colOne == CubeColour.YELLOW && pair.colTwo == CubeColour.ORANGE) { return true; }
        }
        if(colour == CubeColour.RED){
            if(pair.colOne == CubeColour.GREEN && pair.colTwo == CubeColour.WHITE) { return true; }
            if(pair.colOne == CubeColour.YELLOW && pair.colTwo == CubeColour.GREEN) { return true; }
            if(pair.colOne == CubeColour.BLUE && pair.colTwo == CubeColour.YELLOW) { return true; }
            if(pair.colOne == CubeColour.WHITE && pair.colTwo == CubeColour.BLUE) { return true; }
        }
        if(colour == CubeColour.BLUE){
            if(pair.colOne == CubeColour.ORANGE && pair.colTwo == CubeColour.YELLOW) { return true; }
            if(pair.colOne == CubeColour.WHITE && pair.colTwo == CubeColour.ORANGE) { return true; }
            if(pair.colOne == CubeColour.RED && pair.colTwo == CubeColour.WHITE) { return true; }
            if(pair.colOne == CubeColour.YELLOW && pair.colTwo == CubeColour.RED) { return true; }
        }
        if(colour == CubeColour.WHITE){
            if(pair.colOne == CubeColour.GREEN && pair.colTwo == CubeColour.ORANGE) { return true; }
            if(pair.colOne == CubeColour.RED && pair.colTwo == CubeColour.GREEN) { return true; }
            if(pair.colOne == CubeColour.BLUE && pair.colTwo == CubeColour.RED) { return true; }
            if(pair.colOne == CubeColour.ORANGE && pair.colTwo == CubeColour.BLUE) { return true; }
        }
        if(colour == CubeColour.YELLOW){
            if(pair.colOne == CubeColour.GREEN && pair.colTwo == CubeColour.RED) { return true; }
            if(pair.colOne == CubeColour.ORANGE && pair.colTwo == CubeColour.GREEN) { return true; }
            if(pair.colOne == CubeColour.BLUE && pair.colTwo == CubeColour.ORANGE) { return true; }
            if(pair.colOne == CubeColour.RED && pair.colTwo == CubeColour.BLUE) { return true; }
        }
        return false;
    }

    /**
     * Checks whether the edges of a cube is valid
     * @param faces - A given face of the cube
     * @return True if valid edges, false otherwise
     */
    public boolean edgesValid(int[] faces) {
        List<EdgePair> pairs = new ArrayList<>();
        EdgePair loopPair;
        boolean valid = true;

        int[][] locations = {{1, 39}, {3, 32}, {7, 48}, {5, 12}, {19, 41}, {21, 14}, {23, 30}, {25, 50}, {37, 28}, {34, 52}, {46, 16}, {10, 43}};

        //this will loop through all the pairs of location in the array "locations"
        for(int i = 0; i < locations.length; i ++)
        {
            int edgeIndex0 = locations[i][0];
            int edgeIndex1 = locations[i][1];
            // Get which face the edge is on
            int edgeFace0 = edgeIndex0 / 9;
            int edgeFace1 = edgeIndex1 / 9;
            // Skip pairs in faces that aren't specified in the faces parameter
            // TODO optimise this... only ever one edge between faces
            if (Arrays.stream(faces).noneMatch(value -> value == edgeFace0)
                    || Arrays.stream(faces).noneMatch(value -> value == edgeFace1))
                continue;
            if (cube.getLongArray().get(edgeIndex0).getColour() == CubeColour.NULL
                    || cube.getLongArray().get(edgeIndex1).getColour() == CubeColour.NULL)
                continue;
            loopPair = new EdgePair(cube.getLongArray().get(edgeIndex0).getColour(), cube.getLongArray().get(edgeIndex1).getColour());
            if (pairs.contains(loopPair))
            {
                valid = false;
            }
            pairs.add(loopPair);
        }
        for(EdgePair pair : pairs)
        {
            if (edgeIsNotValid(pair))
                valid = false;
        }
        return valid;
    }

    /**
     * Checks for valid edge configurations, taking into account all edges
     * @return True if correct configuration, false otherwise
     */
    public boolean edgesAreValid() {
        return edgesValid(new int[]{0,1,2,3,4,5});
    }

    /**
     * Checks for a valid corner configuration
     * @param faces - A given set of faces of the cube to check
     * @return True if correct configuration, false otherwise
     */
    public boolean invalidCorners(int[] faces) {
        ArrayList<Square> cubeArr = cube.getLongArray();

        /* for each corner, check its respective corners for invalid colour */
        for (int i = 0; i < 8; i++) {
            // Get which face the edge is on
            int cornerFace0 = corners[i] / 9;
            int cornerFace1 = otherCorners[i][0] / 9;
            int cornerFace2 = otherCorners[i][1] / 9;
            // TODO optimise this...
            // Skip if none of the specified faces are within the corner
            if ((Arrays.stream(faces).noneMatch(value -> value == cornerFace0)
                    && Arrays.stream(faces).noneMatch(value -> value == cornerFace1)
                    && Arrays.stream(faces).noneMatch(value -> value == cornerFace2)))
                continue;
            CubeColour[] corner = new CubeColour[]{cubeArr.get(corners[i]).getColour(),
                    cubeArr.get(otherCorners[i][0]).getColour(),
                    cubeArr.get(otherCorners[i][1]).getColour()};
            // Skip only if 3 nulls
            if (Arrays.stream(corner).filter(colour -> colour == CubeColour.NULL).count() <= 1) {
                if (corner.length != new HashSet(List.of(corner)).size()) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.ORANGE) { /* ORANGE */
                if (cubeArr.get(otherCorners[i][0]).getColour() == CubeColour.RED || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.RED) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.GREEN) { /* GREEN */
                if (cubeArr.get(otherCorners[i][0]).getColour() == CubeColour.BLUE || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.BLUE) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.RED) { /* RED */
                if (cubeArr.get(otherCorners[i][0]).getColour() == CubeColour.ORANGE || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.ORANGE) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.BLUE) { /* BLUE */
                if (cubeArr.get(otherCorners[i][0]).getColour() == CubeColour.GREEN || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.GREEN) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.WHITE) { /* WHITE */
                if (cube.getLongArray().get(otherCorners[i][0]).getColour() == CubeColour.YELLOW || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.YELLOW) {
                    return false;
                }
            }
            if (cubeArr.get(corners[i]).getColour() == CubeColour.YELLOW) { /* YELLOW */
                if (cubeArr.get(otherCorners[i][0]).getColour() == CubeColour.WHITE || cubeArr.get(otherCorners[i][1]).getColour() == CubeColour.WHITE) {
                    return false;
                }
            }
            // Only check other two corner colours against eachother if they are both
            // not null
            if (cubeArr.get(otherCorners[i][0]).getColour() != CubeColour.NULL
                    && cubeArr.get(otherCorners[i][1]).getColour() != CubeColour.NULL) {
                // Also check the other corner colours are valid with eachother
                EdgePair pair = new EdgePair(cubeArr.get(otherCorners[i][0]).getColour(),
                        cubeArr.get(otherCorners[i][1]).getColour());
                if (edgeIsNotValid(pair))
                    return false;
                // Check the other two corner colours' order
                if(edgeOrderNotValid(cubeArr.get(corners[i]).getColour(), pair))
                    return false;
            }
        }
        return true;
    }

    /**
     * Applies invalidCorners to a list of corners
     * @return True if valid, false otherwise
     */
    public boolean cornersAreValid() {
        return invalidCorners(new int[]{0,1,2,3,4,5});
    }

    /**
     * Checks whether the corner of a cube is a duplicate
     * @return True if no duplicates, false otherwise
     */
    public boolean duplicateCorners() {
        ArrayList<Square> cubeArr = cube.getLongArray();

        /* for each valid corner and its colours, check if they are duplicated*/
        for (int i = 0; i < 7; i++) {
            for (int j = i + 1; j < 8; j++) {
                if (cube.getLongArray().get(corners[i]).getColour() == CubeColour.NULL
                        || cube.getLongArray().get(otherCorners[i][0]).getColour() == CubeColour.NULL
                        || cube.getLongArray().get(otherCorners[i][1]).getColour() == CubeColour.NULL)
                    continue;
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(corners[j]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()) {
                    return false;
                }
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(corners[j]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()) {
                    return false;
                }
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(corners[j]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()) {
                    return false;
                }
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(corners[j]).getColour()) {
                    return false;
                }
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(corners[j]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()) {
                    return false;
                }
                if (cubeArr.get(corners[i]).getColour() == cubeArr.get(otherCorners[j][1]).getColour()
                        && cubeArr.get(otherCorners[i][0]).getColour() == cubeArr.get(otherCorners[j][0]).getColour()
                        && cubeArr.get(otherCorners[i][1]).getColour() == cubeArr.get(corners[j]).getColour()) {
                    return false;
                }
            }
        }
        return true;

    }

    /**
     * Checks whether the number of each colour is correct (at most 9 for each)
     * @return True if counts are correct (all <= 9), False if any colour has
     *         > 9 occurrences
     */
    public boolean countsAreValid() {
        int[] counts = new int[7];
        int colour;

        for (int i = 0; i < 54; i++)
        {
            colour = cube.getLongArray().get(i).getColour().ordinal();
            counts[colour]++;
            if(colour<6 && counts[colour]>9)
                return false;
        }

        return true;
    }
}
