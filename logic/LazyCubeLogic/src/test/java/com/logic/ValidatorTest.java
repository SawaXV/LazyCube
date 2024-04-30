package com.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alfred Roberts
 * @editor Alexander Bull, Sarwar Rashid, Alfie Inman
 * @description Tests for CubeValidator class
 */
class ValidatorTest {
    /*
    ASSUMING A CUBE REPRESENTATION OF AN ARRAY WITH THE FOLLOWING ARRAY POSITIONS

           36 37 38
           39 40 41
           42 43 44

    0 1 2  9 10 11   18 19 20  27 28 29
    3 4 5  12 13 14  21 22 23  30 31 32
    6 7 8  15 16 17  24 25 26  33 34 35

           45 46 47
           48 49 50
           51 52 53

     WITH THE FOLLOWING COLOURS:

       W
     O G R B
       Y

     USING THESE POSITIONS SINCE WE CAN GENERATE DATA FROM:
     https://www.worldcubeassociation.org/regulations/history/files/scrambles/scramble_cube.htm

     */

    /*
    COLOUR VALUES:
        O = 0
        G = 1
        R = 2
        B = 3
        W = 4
        Y = 5

     HARD CODED HERE, BUT MOVE TO RUBIKS CUBE CLASS WHEN IMPLEMENTED:
     // TODO: should really be "square" objects, but can be converted in the validator
     */
    public static final int O = 0;
    public static final int G = 1;
    public static final int R = 2;
    public static final int B = 3;
    public static final int W = 4;
    public static final int Y = 5;
    public static final int N = 6;

    // Permutation code from: https://java2blog.com/permutations-array-java/
    public List<List<Integer>> permute(int[] arr) {
        List<List<Integer>> list = new ArrayList<>();
        permuteHelper(list, new ArrayList<>(), arr);
        return list;
    }

    private void permuteHelper(List<List<Integer>> list, List<Integer> resultList, int [] arr){

        // Base case
        if(resultList.size() == arr.length){
            list.add(new ArrayList<>(resultList));
        }
        else{
            for (int element : arr) {

                if (resultList.contains(element)) {
                    // If element already exists in the list then skip
                    continue;
                }
                // Choose element
                resultList.add(element);
                // Explore
                permuteHelper(list, resultList, arr);
                // Unchoose element
                resultList.remove(resultList.size() - 1);
            }
        }
    }

    @Nested
    @DisplayName("Valid cube configuration tests")
    class ValidConfigs {
        @Test
        @DisplayName("Validate correct cube configuration 1")
        void testValidCubeConfig1()
        {
            // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
            int[] input = {O, O, B, R, O, Y, B, B, R,
                           R, Y, G, B, G, O, G, R, Y,
                           O, W, Y, Y, R, G, B, W, O,
                           R, G, B, O, B, B, W, W, W,
                           W, Y, G, W, W, B, Y, R, Y,
                           W, G, O, O, Y, R, R, G, G};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }

        @Test
        @DisplayName("Validate correct cube configuration 2")
        void testValidCubeConfig2()
        {
            // scramble used: B L' D2 L R B F D2 L2 U B' R F' R2 B2 F' L2 R' F' U B F D' U' R B2 D2 U2 R2 F2
            int[] input = {B, B, G, R, O, W, B, O, O,
                           Y, G, W, O, G, Y, G, G, B,
                           O, R, W, R, R, Y, O, G, Y,
                           G, Y, R, O, B, W, R, R, O,
                           W, B, R, W, W, G, R, Y, G,
                           Y, O, Y, B, Y, W, W, B, B};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }

        @Test
        @DisplayName("Validate correct cube configuration 3")
        void testValidCubeConfig3()
        {
            // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
            int[] input = {Y, W, W, R, O, Y, W, G, Y,
                           O, O, R, R, G, G, B, B, B,
                           Y, B, W, Y, R, O, R, O, O,
                           G, O, G, G, B, G, G, B, B,
                           R, Y, R, R, W, Y, G, W, B,
                           O, R, W, W, Y, B, O, W, Y};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }
    }

    @Nested
    @DisplayName("Cube configuration with empty null values tests")
    class NullConfigs {
        @Test
        @DisplayName("Validate cube configuration with nulls 1")
        void testCubeConfigNull1()
        {
            // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
            // change input[0,1,2] to null
            int[] input = {N, N, N, R, O, Y, B, B, R,
                           R, Y, G, B, G, O, G, R, Y,
                           O, W, Y, Y, R, G, B, W, O,
                           R, G, B, O, B, B, W, W, W,
                           W, Y, G, W, W, B, Y, R, Y,
                           W, G, O, O, Y, R, R, G, G};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }

        @Test
        @DisplayName("Validate cube configuration with nulls 2")
        void testCubeConfigNull2()
        {
            // scramble used: B L' D2 L R B F D2 L2 U B' R F' R2 B2 F' L2 R' F' U B F D' U' R B2 D2 U2 R2 F2
            // change input[9,18] to null
            int[] input = {B, B, G, R, O, W, B, O, O,
                           N, G, W, O, G, Y, G, G, B,
                           N, R, W, R, R, Y, O, G, Y,
                           G, Y, R, O, B, W, R, R, O,
                           W, B, R, W, W, G, R, Y, G,
                           Y, O, Y, B, Y, W, W, B, B};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }

        @Test
        @DisplayName("Validate cube configuration with nulls 3")
        void testCubeConfigNull3()
        {
            // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
            // change input[29,42,42] to null
            // change input[0..8] to null
            int[] input = {N, N, N, N, O, N, N, N, N,
                           O, O, R, R, G, G, B, B, B,
                           Y, B, W, Y, R, O, R, O, O,
                           G, O, N, G, B, G, G, B, B,
                           R, Y, R, R, W, Y, N, N, B,
                           O, R, W, W, Y, B, O, W, Y};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertTrue(validator.isCubeValid());
        }
    }

    @Nested
    @DisplayName("Duplicate square tests")
    class Duplicates {
        @Test
        @DisplayName("Validate duplicate edge colours 1")
        void testDuplicateEdge1() {
            // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
            // replaced input[14] from O to B
            // Two blue yellow edge pieces
            int[] input = {O, O, B, R, O, Y, B, B, R,
                    R, Y, G, B, G, B, G, R, Y,
                    O, W, Y, Y, R, G, B, W, O,
                    R, G, B, O, B, B, W, W, W,
                    W, Y, G, W, W, B, Y, R, Y,
                    W, G, O, O, Y, R, R, G, G};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.edgesAreValid());
        }

        @Test
        @DisplayName("Validate duplicate edge colours 2")
        void testDuplicateEdge2() {
            // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
            // replaced input[43] from W to G
            // Two green orange edge pieces
            int[] input = {Y, W, W, R, O, Y, W, G, Y,
                    O, O, R, R, G, G, B, B, B,
                    Y, B, W, Y, R, O, R, O, O,
                    G, O, G, G, B, G, G, B, B,
                    R, Y, R, R, W, Y, G, G, B,
                    O, R, W, W, Y, B, O, W, Y};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.edgesAreValid());
        }

        @Test
        @DisplayName("Validate duplicate edge colours 1")
        void testDuplicateCorner1() {
            // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
            // replaced input[47] from W to Y
            // Two Blue-red-yellow corner pieces
            int[] input = {Y, W, W, R, O, Y, W, G, Y,
                    O, O, R, R, G, G, B, B, B,
                    Y, B, W, Y, R, O, R, O, O,
                    G, O, G, G, B, G, G, B, B,
                    R, Y, R, R, W, Y, G, W, B,
                    O, R, Y, W, Y, B, O, W, Y};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.duplicateCorners());
        }

        @Test
        @DisplayName("Validate duplicate edge colours 2")
        void testDuplicateCorner2() {
            // scramble used: B L' D2 L R B F D2 L2 U B' R F' R2 B2 F' L2 R' F' U B F D' U' R B2 D2 U2 R2 F2
            // replaced input[20] from W to Y
            // replaced input[27] from G to B
            // two Red-blue-yellow corners
            int[] input = {B, B, G, R, O, W, B, O, O,
                    Y, G, W, O, G, Y, G, G, B,
                    O, R, Y, R, R, Y, O, G, Y,
                    B, Y, R, O, B, W, R, R, O,
                    W, B, R, W, W, G, R, Y, G,
                    Y, O, Y, B, Y, W, W, B, B};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.duplicateCorners());
        }

        @Test
        @DisplayName("Validate duplicate center colours 1")
        void testDuplicateCenter1() {
            // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
            // replaced input[49] from Y to W
            // two white centers
            int[] input = {O, O, B, R, O, Y, B, B, R,
                    R, Y, G, B, G, O, G, R, Y,
                    O, W, Y, Y, R, G, B, W, O,
                    R, G, B, O, B, B, W, W, W,
                    W, Y, G, W, W, B, Y, R, Y,
                    W, G, O, O, W, R, R, G, G};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.centresAreValid());
        }

        @Test
        @DisplayName("Validate duplicate center colours 2")
        void testDuplicateCenter2() {
            // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
            // replaced input[22] from G to B
            // two blue centers
            int[] input = {O, O, B, R, O, Y, B, B, R,
                    R, Y, G, B, G, O, G, R, Y,
                    O, W, Y, Y, B, G, B, W, O,
                    R, G, B, O, B, B, W, W, W,
                    W, Y, G, W, W, B, Y, R, Y,
                    W, G, O, O, Y, R, R, G, G};
            CubeValidator validator = new CubeValidator(new Cube(input));
            assertFalse(validator.centresAreValid());
        }
        @Test
        @DisplayName("Validate corner with 3 of the same colour, for all colours")
        void testDuplicateCornerAllColours() {
            // scramble used:  F D2 L2 D U2 L2 R D2 U2 R' B L2 R' D2 U' R' D L2 F2 D U L B2 L2 U R F2 L U' L2

            final int[] corners = {0, 2, 6, 8, 9, 11, 15, 17, 18, 20, 24, 26,
                    27, 29, 33, 35, 36, 38, 42, 44, 45, 47, 51, 53};
            final int[] colours = {0,1,2,3,4,5};

            for (int colour : colours) {
                int[] input = {W, R, W, B, O, W, Y, G, R,
                        R, G, Y, B, G, G, G, W, W,
                        R, W, B, W, R, B, G, R, Y,
                        R, Y, O, O, B, Y, B, O, G,
                        B, G, Y, B, W, R, B, R, G,
                        W, O, O, O, Y, Y, O, Y, O};
                // Set every corner to a single colour
                for (int cornerIndex : corners) {
                    input[cornerIndex] = colour;
                }
                Cube cube = new Cube(input);
                cube.show();
                CubeValidator validator = new CubeValidator(cube);
                assertFalse(validator.isCubeValid());
            }
        }

        @Test
        @DisplayName("Validate edge with 2 of the same colour, for all colours")
        void testDuplicateEdgeAllColours() {
            // scramble used:  F D2 L2 D U2 L2 R D2 U2 R' B L2 R' D2 U' R' D L2 F2 D U L B2 L2 U R F2 L U' L2

            final int[] edges = {1, 3, 5, 7, 10, 12, 14, 16, 19, 21, 23, 25,
                    28, 30, 32, 34, 37, 39, 41, 43, 46, 48, 50, 52};
            final int[] colours = {0,1,2,3,4,5};

            for (int colour : colours) {
                int[] input = {W, R, W, B, O, W, Y, G, R,
                        R, G, Y, B, G, G, G, W, W,
                        R, W, B, W, R, B, G, R, Y,
                        R, Y, O, O, B, Y, B, O, G,
                        B, G, Y, B, W, R, B, R, G,
                        W, O, O, O, Y, Y, O, Y, O};
                // Set every edge to a single colour
                for (int edgeIndex : edges) {
                    input[edgeIndex] = colour;
                }
                Cube cube = new Cube(input);
                cube.show();
                CubeValidator validator = new CubeValidator(cube);
                assertFalse(validator.edgesAreValid());
            }
        }
        @Test
        @DisplayName("Validate all colours the same, for all colours")
        void testAllSameColours() {
            final int[] colours = {0,1,2,3,4,5};

            for (int colour : colours) {
                int[] input = new int[54];
                // Set every square to a single colour
                Arrays.fill(input, colour);
                Cube cube = new Cube(input);
                cube.show();
                CubeValidator validator = new CubeValidator(cube);
                assertFalse(validator.edgesAreValid());
            }
        }
    }
        @Nested
        @DisplayName("Invalid colour combination tests")
        class InvalidColours {
            @Test
            @DisplayName("Validate incorrect edge colours 1")
            void testInvalidEdge1() {
                // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
                // replaced input[12] from B to W
                // invalid yellow white edge
                int[] input = {O, O, B, R, O, Y, B, B, R,
                        R, Y, G, W, G, O, G, R, Y,
                        O, W, Y, Y, R, G, B, W, O,
                        R, G, B, O, B, B, W, W, W,
                        W, Y, G, W, W, B, Y, R, Y,
                        W, G, O, O, Y, R, R, G, G};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.edgesAreValid());
            }

            @Test
            @DisplayName("Validate incorrect edge colours 2")
            void testInvalidEdge2() {
                // scramble used: B L' D2 L R B F D2 L2 U B' R F' R2 B2 F' L2 R' F' U B F D' U' R B2 D2 U2 R2 F2
                // replaced input[46] from O to B
                // invalid blue greem edge
                int[] input = {B, B, G, R, O, W, B, O, O,
                        Y, G, W, O, G, Y, G, G, B,
                        O, R, W, R, R, Y, O, G, Y,
                        G, Y, R, O, B, W, R, R, O,
                        W, B, R, W, W, G, R, Y, G,
                        Y, B, Y, B, Y, W, W, B, B};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.edgesAreValid());
            }


            @Test
            @DisplayName("Validate incorrect corner colours 1")
            void testInvalidCorner1() {
                // scramble used: B L' D2 L R B F D2 L2 U B' R F' R2 B2 F' L2 R' F' U B F D' U' R B2 D2 U2 R2 F2
                // replaced input[15] from G to R
                // invalid orange red yellow corner
                int[] input = {B, B, G, R, O, W, B, O, O,
                        Y, G, W, O, G, Y, R, G, B,
                        O, R, W, R, R, Y, O, G, Y,
                        G, Y, R, O, B, W, R, R, O,
                        W, B, R, W, W, G, R, Y, G,
                        Y, O, Y, B, Y, W, W, B, B};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.cornersAreValid());
            }

            @Test
            @DisplayName("Validate incorrect corner colours 2")
            void testInvalidCorner2() {
                // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
                // replaced input[38] from R to Y
                // invalid yellow-white-green corner
                int[] input = {Y, W, W, R, O, Y, W, G, Y,
                        O, O, R, R, G, G, B, B, B,
                        Y, B, W, Y, R, O, R, O, O,
                        G, O, G, G, B, G, G, B, B,
                        R, Y, Y, R, W, Y, G, W, B,
                        O, R, W, W, Y, B, O, W, Y};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.cornersAreValid());
            }


            @Test
            @DisplayName("Validate incorrect center colours 1")
            void testInvalidCenter1() {
                // TODO this test might be reduntant since we should always put the centers in the correct positions
                // scramble used: L B' F' D2 U2 B2 U2 B F' D L2 R' F2 U2 R2 D' L R' B2 D' U B2 D' B' F R' U R' U B
                // switched red and blue centers
                int[] input = {Y, W, W, R, O, Y, W, G, Y,
                        O, O, R, R, G, G, B, B, B,
                        Y, B, W, Y, B, O, R, O, O,
                        G, O, G, G, R, G, G, B, B,
                        R, Y, R, R, W, Y, G, W, B,
                        O, R, W, W, Y, B, O, W, Y};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.centresAreValid());
            }

            @Test
            @DisplayName("Validate incorrect center colours 2")
            void testInvalidCenter2() {
                // scramble used: B L' U B' D2 U2 L2 F R B2 L' F' U B' F' D2 U2 R' B' D B D2 B2 F D2 U F' R' D' R2
                // switched orange and green centers
                int[] input = {O, O, B, R, G, Y, B, B, R,
                        R, Y, G, B, O, O, G, R, Y,
                        O, W, Y, Y, R, G, B, W, O,
                        R, G, B, O, B, B, W, W, W,
                        W, Y, G, W, W, B, Y, R, Y,
                        W, G, O, O, Y, R, R, G, G};
                CubeValidator validator = new CubeValidator(new Cube(input));
                assertFalse(validator.centresAreValid());
            }

            @Test
            @DisplayName("Checks invalid corner colour combinations for every corner")
            void testInvalidCornerCombinations() {
                // scramble used:    F2 D2 B' D2 L' D2 B D R2 F2 U' L' R D' L B D' F2 R' D B' D2 L R2 D U L2 D L R2

                final int[] corners = {0, 2, 6, 8, 18, 20, 24, 26};
                final int[][] otherCorners = {{29, 36}, {9, 42}, {51, 35}, {15, 45}, {11, 44}, {27, 38}, {17, 47}, {33, 53}};

                final int[][] invalidColourPairs = {{0,2}, {1,3}, {4,5}};

                // Set each corner (in every permutation) to have two invalid colours in them
                for (int[] pair : invalidColourPairs) {
                    for (int i = 0; i < corners.length; i++) {
                            int[] corner = {corners[i], otherCorners[i][0],
                                    otherCorners[i][1]};
                            for (List<Integer> colourPerm : permute(pair)) {
                                for (List<Integer> cornerPerm : permute(corner)) {
                                    int[] input = {Y, B, R, B, O, O, O, R, Y,
                                            W, W, Y, G, G, Y, R, W, B,
                                            G, R, O, O, R, R, R, B, G,
                                            W, B, B, Y, B, O, R, G, G,
                                            O, R, B, W, W, W, G, O, O,
                                            B, G, W, G, Y, Y, W, Y, Y};
                                    System.out.println(cornerPerm.toString());
                                    System.out.println(colourPerm.toString());
                                    input[cornerPerm.get(0)] = colourPerm.get(0);
                                    input[cornerPerm.get(1)] = colourPerm.get(1);
                                    Cube cube = new Cube(input);
                                    cube.show();
                                    CubeValidator validator = new CubeValidator(cube);
                                    assertFalse(validator.isCubeValid());
                                }
                            }

                    }
                }
            }

            @Test
            @DisplayName("Checks invalid edge colour combinations for every edge")
            void testInvalidEdgeCombinations() {
                // scramble used:    F2 D2 B' D2 L' D2 B D R2 F2 U' L' R D' L B D' F2 R' D B' D2 L R2 D U L2 D L R2

                int[][] edges = {{1, 39}, {3, 32}, {7, 48}, {5, 12}, {19, 41}, {21, 14}, {23, 30}, {25, 50}, {37, 28}, {34, 52}, {46, 16}, {10, 43}};

                final int[][] invalidColourPairs = {{0,2}, {1,3}, {4,5}};

                // Set each edge (in every permutation) to have two invalid colours in them
                for (int[] pair : invalidColourPairs) {
                    for (int[] edge : edges) {
                        for (List<Integer> colourPerm : permute(pair)) {
                            for (List<Integer> edgePerm : permute(edge)) {
                                int[] input = {Y, B, R, B, O, O, O, R, Y,
                                            W, W, Y, G, G, Y, R, W, B,
                                            G, R, O, O, R, R, R, B, G,
                                            W, B, B, Y, B, O, R, G, G,
                                            O, R, B, W, W, W, G, O, O,
                                            B, G, W, G, Y, Y, W, Y, Y};
                                System.out.println(edgePerm.toString());
                                System.out.println(colourPerm.toString());
                                input[edgePerm.get(0)] = colourPerm.get(0);
                                input[edgePerm.get(1)] = colourPerm.get(1);
                                Cube cube = new Cube(input);
                                cube.show();
                                CubeValidator validator = new CubeValidator(cube);
                                assertFalse(validator.isCubeValid());
                            }
                        }
                    }
                }
            }

        }
    }
