package com.logic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdderTest {

    // USING THE SAME COLOUR DEFINITIONS AS VALIDATOR TESTS

    /*
        TO TEST OUTPUTS:
        for (int i = 0; i < 54; i++) {
            if (i % 9 == 0)
                System.out.println();
           System.out.print(cube.getLongArray().get(i).getColour() + " ");
        }
    */

    public static final int O = 0;
    public static final int G = 1;
    public static final int R = 2;
    public static final int B = 3;
    public static final int W = 4;
    public static final int Y = 5;

    Face initFace(int colours[]) {
        Square squares[] = new Square[9];
        for (int i = 0; i < 9; i++) {
            squares[i] = new Square();
            squares[i].setColour(CubeColour.values()[colours[i]], 1);
        }

        return new Face(squares);
    }

    void testFace(Cube cube, int start, int colours[]) {
        for (int i = 0; i < 9; i++) {
            assertEquals(CubeColour.values()[colours[i]], cube.getLongArray().get(start + i).getColour());
        }
    }

    /*
        TESTING FOR SAME CUBE, ALL ROTATIONS
        Cube 1:
        scrambled used: L' D2 F2 D R D U B' L' R' U' B L' D' U B2 F2 D R F' U' B L' R B2 D2 L R F' D'
        green face 9-17 empty, correct:- Y,B,B,R,G,R,W,O,B

        Cube 2:
        scrambled used: B2 L' U B F' R B2 U' L D2 B' U2 R L F2 B' U R U2 F B R' U D L2
        white face 36-44 empty, correct:- O,R,Y,G,W,B,W,Y,G
    */

    static Cube cube1, cube2;

    @BeforeAll
    static void cubeInit() {
        int[] input1 = {G, Y, G, B, O, G, G, G, R,
                6, 6, 6, 6, 6, 6, 6, 6, 6,
                R, W, W, Y, R, Y, O, W, W,
                G, W, Y, G, B, Y, O, O, W,
                O, R, R, O, W, O, R, R, Y,
                B, G, Y, W, Y, B, O, B, B};
        cube1 = new Cube(input1);

        int[] input2 = {Y, O, R, R, O, Y, W, R, R,
                B, B, O, R, G, O, G, W, B,
                Y, W, R, B, R, W, Y, Y, O,
                G, G, B, G, B, B, B, Y, O,
                6, 6, 6, 6, 6, 6, 6, 6, 6,
                W, O, R, W, Y, G, G, O, W};
        cube2 = new Cube(input2);
    }

    @Test
    @DisplayName("Correct rotation 1")
    void testCorrectRotation1() {
        int colours[] = {Y, B, B, R, G, R, W, O, B};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube1);
        adder.addFace(face);
        cube1 = adder.getCube();

        testFace(cube1, 9, colours);

        assertTrue(new CubeValidator(cube1).isCubeValid());
    }

    @Test
    @DisplayName("Turned 90d clockwise 1")
    void test90dCWTurn1() {
        int colours[] = {W, R, Y, O, G, B, B, R, B};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube1);
        adder.addFace(face);
        cube1 = adder.getCube();

        int testColours[] = {Y, B, B, R, G, R, W, O, B};
        testFace(cube1, 9, testColours);

        assertTrue(new CubeValidator(cube1).isCubeValid());
    }

    @Test
    @DisplayName("Turned 180d 1")
    void test180dTurn1() {
        int colours[] = {B, O, W, R, G, R, B, B, Y};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube1);
        adder.addFace(face);
        cube1 = adder.getCube();

        int testColours[] = {Y, B, B, R, G, R, W, O, B};
        testFace(cube1, 9, testColours);

        assertTrue(new CubeValidator(cube1).isCubeValid());
    }

    @Test
    @DisplayName("Turned 270d clockwise 1")
    void test270dCWTurn1() {
        int colours[] = {B, R, B, B, G, O, Y, R, W};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube1);
        adder.addFace(face);
        cube1 = adder.getCube();

        int testColours[] = {Y, B, B, R, G, R, W, O, B};
        testFace(cube1, 9, testColours);

        assertTrue(new CubeValidator(cube1).isCubeValid());
    }

    @Test
    @DisplayName("Correct rotation 2")
    void testCorrectRotation2() {
        int colours[] = {O, R, Y, G, W, B, W, Y, G};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube2);
        adder.addFace(face);
        cube2 = adder.getCube();

        testFace(cube2, 36, colours);

        assertTrue(new CubeValidator(cube2).isCubeValid());
    }

    @Test
    @DisplayName("Turned 90d clockwise 2")
    void test90dCWTurn2() {
        int colours[] = {W, G, O, Y, W, R, G, B, Y};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube2);
        adder.addFace(face);
        cube2 = adder.getCube();

        int testColours[] = {O, R, Y, G, W, B, W, Y, G};
        testFace(cube2, 36, testColours);

        assertTrue(new CubeValidator(cube2).isCubeValid());
    }

    @Test
    @DisplayName("Turned 180d 2")
    void test180dTurn2() {
        int colours[] = {G, Y, W, B, W, G, Y, R, O};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube2);
        adder.addFace(face);
        cube2 = adder.getCube();

        int testColours[] = {O, R, Y, G, W, B, W, Y, G};
        testFace(cube2, 36, testColours);

        assertTrue(new CubeValidator(cube2).isCubeValid());
    }

    @Test
    @DisplayName("Turned 270d clockwise 2")
    void test270dCWTurn2() {
        int colours[] = {Y, B, G, R, W, Y, O, G, W};
        Face face = initFace(colours);

        FaceAdder adder = new FaceAdder(cube2);
        adder.addFace(face);
        cube2 = adder.getCube();

        int testColours[] = {O, R, Y, G, W, B, W, Y, G};
        testFace(cube2, 36, testColours);

        assertTrue(new CubeValidator(cube2).isCubeValid());
    }

    @Nested
    @DisplayName("Addition to blank tests")
            // no need to rotate
    class AdditionsToBlank {
        @Test
        @DisplayName("Add Orange Face to blank cube")
        void testOrangeFaceToBlank() {
            // scramble used (only 1 face used): U B' L B' D U B' L' U L2 B L2 F R2 B2 D L R' B F L' R D2 L R U2 L' U' F D'
            Cube cube = new Cube();

            int colours[] = {B, W, W, O, O, Y, B, W, Y};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            testFace(cube, 0, colours);

            for (int i = 9; i < 54; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }

        @Test
        @DisplayName("Add Yellow Face to blank cube")
        void testYellowFaceToBlank() {
            // scramble used (only 1 face used): D2 R' U2 F D B F' U F2 R2 B R' U L' D2 B' L' B F2 R F2 U' B F R B D' U B F
            Cube cube = new Cube();
            int colours[] = {O, Y, R, W, Y, R, O, G, G};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            testFace(cube, 45, colours);

            //no other face added
            for (int i = 0; i < 45; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }

        @Test
        @DisplayName("Add Green Face to blank cube")
        void testGreenFaceToBlank() {
            // scramble used (only 1 face used): B D' F2 B' R' D L2 U' B2 L R' B2 U L R D2 R2 L2 F2 U' L2 D2 F D2 L'
            Cube cube = new Cube();
            int colours[] = {B, Y, G, O, G, O, O, R, R};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            testFace(cube, 9, colours);

            //no other face added
            for (int i = 0; i < 9; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }

            //check rest
            for (int i = 18; i < 54; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }

        @Test
        @DisplayName("Add White Face to blank cube")
        void testWhiteFaceToBlank() {
            // scramble used (only 1 face used): D L U L F B R' U' B2 F U' D2 R2 F B' R2 U' R' D2 U R' L2 F' R' F'
            Cube cube = new Cube();
            int colours[] = {Y, G, O, W, W, R, B, O, R};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            testFace(cube, 36, colours);

            //no other face added
            for (int i = 0; i < 36; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }

            //check rest
            for (int i = 45; i < 54; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }

        @Test
        @DisplayName("Add Blue Face to blank cube")
        void testBlueFaceToBlank() {
            // scramble used (only 1 face used): U B2 L2 U B R' L2 D R F R2 L2 D2 L R' F' B U F2 R B' L U2 B L
            Cube cube = new Cube();
            int colours[] = {B, W, G, G, B, R, W, B, G};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            //no other face added
            for (int i = 0; i < 27; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }

            //check rest
            for (int i = 36; i < 54; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }

        @Test
        @DisplayName("Add Red Face to blank cube")
        void testRedFaceToBlank() {
            // scramble used (only 1 face used): L F R' F2 U2 L2 F2 B U' R' U B D' U2 F' R2 D' U R' D2 L2 B2 F2 U2 L
            Cube cube = new Cube();
            int colours[] = {G, R, G, R, R, W, W, G, B};

            Face face = initFace(colours);

            FaceAdder adder = new FaceAdder(cube);
            adder.addFace(face);
            cube = adder.getCube();

            testFace(cube, 18, colours);

            //no other face added
            for (int i = 0; i < 18; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }

            //check rest
            for (int i = 27; i < 54; i++) {
                //if not a centre
                if ((i - 4) % 9 != 0) {
                    assertEquals(CubeColour.NULL, cube.getLongArray().get(i).getColour());
                }
            }
        }
    }

    @Nested
    @DisplayName("Adding an entire cube from scratch")
    class NewCube {
        @Test
        @DisplayName("Adding a new cube 0 from scratch")
        void newCube0() {
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {O, R, W, W, O, Y, G, B, Y};
            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[]{W, Y, G, W, W, G, B, R, Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[]{O, G, B, Y, R, G, R, Y, Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[]{O, G, R, R, G, O, G, B, W};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[]{O, B, G, B, B, R, B, W, R};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[]{R, O, B, W, Y, O, W, O, Y};
            face = initFace(colours);
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[]{O, R, W, W, O, Y, G, B, Y}; // face should be rotated
            testFace(cube, 0, colours);

            colours = new int[]{Y,R,B,G,W,W,G,Y,W}; // face should be rotated
            testFace(cube, 36, colours);

            colours = new int[]{B,G,Y,G,R,Y,O,Y,R};
            testFace(cube, 18, colours);

            colours = new int[] {O,G,R,R,G,O,G,B,W};
            testFace(cube, 9, colours);

            colours = new int[]{O, B, G, B, B, R, B, W, R};
            testFace(cube, 27, colours);

            colours = new int[]{R, O, B, W, Y, O, W, O, Y};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube 0 from scratch, with different faces rotated")
        void newCube0Rotated()
        {
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {O,R,W,W,O,Y,G,B,Y};
            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,G,R,R,G,O,G,B,W};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,G,B,Y,R,G,R,Y,Y};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,B,G,B,B,R,B,W,R};
            face = initFace(colours);
            adder.addFace(face);
            cube = adder.getCube();

            colours = new int[] {W,Y,G,W,W,G,B,R,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {R,O,B,W,Y,O,W,O,Y};
            face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {O,R,W,W,O,Y,G,B,Y};
            testFace(cube, 0, colours);

            colours = new int[] {O,G,R,R,G,O,G,B,W};
            testFace(cube, 9, colours);

            colours = new int[] {B,G,Y,G,R,Y,O,Y,R}; // face should be rotated
            testFace(cube, 18, colours);

            colours = new int[] {O,B,G,B,B,R,B,W,R};
            testFace(cube, 27, colours);

            colours = new int[] {Y,R,B,G,W,W,G,Y,W}; // face should be rotated
            testFace(cube, 36, colours);

            colours = new int[] {R,O,B,W,Y,O,W,O,Y};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube from scratch 1")
        void newCube1()
        {
            // Scramble:
            // D2 U F' D U' B F D B F' D2 F D2 B L2 F2 D2 U B' F' D2 L' D' U' B' R' D2 U2 L2 B2
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {G,R,R,G,O,R,Y,O,W};
            Face face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,Y,W,W,G,Y,O,B,B};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,B,O,O,R,O,Y,G,W};
            face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {Y,B,O,W,B,W,B,R,O};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {W,Y,G,B,W,W,Y,R,R};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {B,O,R,G,Y,Y,B,G,R};
            face = initFace(colours);
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {G,R,R,G,O,R,Y,O,W};
            testFace(cube, 0, colours);

            colours = new int[] {G,Y,W,W,G,Y,O,B,B};
            testFace(cube, 9, colours);

            colours = new int[] {G,B,O,O,R,O,Y,G,W};
            testFace(cube, 18, colours);

            colours = new int[] {Y,B,O,W,B,W,B,R,O};
            testFace(cube, 27, colours);

            colours = new int[] {W,Y,G,B,W,W,Y,R,R};
            testFace(cube, 36, colours);

            colours = new int[] {B,O,R,G,Y,Y,B,G,R};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube 1 from scratch, with different faces rotated")
        void newCube1Rotated()
        {
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {G,R,R,G,O,R,Y,O,W};
            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {G,Y,W,W,G,Y,O,B,B};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,B,O,O,R,O,Y,G,W};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {Y,B,O,W,B,W,B,R,O};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {W,Y,G,B,W,W,Y,R,R};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {B,O,R,G,Y,Y,B,G,R};
            face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {G,R,R,G,O,R,Y,O,W};
            testFace(cube, 0, colours);

            colours = new int[] {G,Y,W,W,G,Y,O,B,B};
            testFace(cube, 9, colours);

            colours = new int[] {G,B,O,O,R,O,Y,G,W};
            testFace(cube, 18, colours);

            colours = new int[] {Y,B,O,W,B,W,B,R,O};
            testFace(cube, 27, colours);

            colours = new int[] {W,Y,G,B,W,W,Y,R,R};
            testFace(cube, 36, colours);

            colours = new int[] {B,O,R,G,Y,Y,B,G,R};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube from scratch 2")
        void newCube2()
        {
            // Scramble:
            // R' U B' U' F2 D2 R F2 R2 B' F2 U2 F' D U2 R F D B2 F D2 R2 U' R' U L' D' U B2 F2
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {O,O,Y,B,W,B,B,G,R};
            Face face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {O,W,W,G,G,B,G,O,W};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {R,G,B,O,Y,Y,Y,W,B};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {W,Y,Y,R,O,R,B,Y,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,W,G,R,R,B,O,G,R};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {O,W,G,O,B,Y,W,R,R};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {W,Y,Y,R,O,R,B,Y,Y};
            testFace(cube, 0, colours);

            colours = new int[] {O,W,W,G,G,B,G,O,W};
            testFace(cube, 9, colours);

            colours = new int[] {G,W,G,R,R,B,O,G,R};
            testFace(cube, 18, colours);

            colours = new int[] {O,W,G,O,B,Y,W,R,R};
            testFace(cube, 27, colours);

            colours = new int[] {O,O,Y,B,W,B,B,G,R};
            testFace(cube, 36, colours);

            colours = new int[] {R,G,B,O,Y,Y,Y,W,B};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube from scratch 2 with different faces rotated")
        void newCube2Rotated()
        {
            // Scramble:
            // R' U B' U' F2 D2 R F2 R2 B' F2 U2 F' D U2 R F D B2 F D2 R2 U' R' U L' D' U B2 F2
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {O,O,Y,B,W,B,B,G,R};
            Face face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,W,W,G,G,B,G,O,W};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {R,G,B,O,Y,Y,Y,W,B};
            face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {W,Y,Y,R,O,R,B,Y,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,W,G,R,R,B,O,G,R};
            face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,W,G,O,B,Y,W,R,R};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {W,Y,Y,R,O,R,B,Y,Y};
            testFace(cube, 0, colours);

            colours = new int[] {O,W,W,G,G,B,G,O,W};
            testFace(cube, 9, colours);

            colours = new int[] {G,W,G,R,R,B,O,G,R};
            testFace(cube, 18, colours);

            colours = new int[] {O,W,G,O,B,Y,W,R,R};
            testFace(cube, 27, colours);

            colours = new int[] {O,O,Y,B,W,B,B,G,R};
            testFace(cube, 36, colours);

            colours = new int[] {R,G,B,O,Y,Y,Y,W,B};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a new cube from scratch 3")
        void newCube3()
        {
            // Scramble:
            // L2 D' B2 F2 D L B D' R D' R B' U R U L R D2 L' R2 U L2 R' U' F2 L2 F D2 L R
            Cube cube = new Cube();
            FaceAdder adder = new FaceAdder(cube);

            int colours[] = {R,O,W,Y,B,B,B,G,B};
            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,O,B,W,R,O,W,R,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,W,G,G,G,B,W,Y,G};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {B,B,R,G,Y,W,O,Y,O};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,G,Y,Y,W,B,Y,O,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {O,R,R,R,O,R,W,W,R};
            face = initFace(colours);
            adder.addFace(face);

            /* test each face individually for validity */
            cube = adder.getCube();
            colours = new int[] {O,R,R,R,O,R,W,W,R};
            testFace(cube, 0, colours);

            colours = new int[] {G,W,G,G,G,B,W,Y,G};
            testFace(cube, 9, colours);

            colours = new int[] {O,O,B,W,R,O,W,R,Y};
            testFace(cube, 18, colours);

            colours = new int[] {R,O,W,Y,B,B,B,G,B};
            testFace(cube, 27, colours);

            colours = new int[] {G,G,Y,Y,W,B,Y,O,Y};
            testFace(cube, 36, colours);

            colours = new int[] {B,B,R,G,Y,W,O,Y,O};
            testFace(cube, 45, colours);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }
    }

    @Test
    @DisplayName("Adding a new cube from scratch 3 with different faces rotated")
    void newCube3Rotated()
    {
        // Scramble:
        // L2 D' B2 F2 D L B D' R D' R B' U R U L R D2 L' R2 U L2 R' U' F2 L2 F D2 L R
        Cube cube = new Cube();
        FaceAdder adder = new FaceAdder(cube);

        int colours[] = {R,O,W,Y,B,B,B,G,B};
        Face face = initFace(colours);
        face = face.rotate();
        adder.addFace(face);

        colours = new int[] {O,O,B,W,R,O,W,R,Y};
        face = initFace(colours);
        face = face.rotate();
        face = face.rotate();
        face = face.rotate();
        adder.addFace(face);

        colours = new int[] {G,W,G,G,G,B,W,Y,G};
        face = initFace(colours);
        face = face.rotate();
        face = face.rotate();
        adder.addFace(face);

        colours = new int[] {B,B,R,G,Y,W,O,Y,O};
        face = initFace(colours);
        adder.addFace(face);

        colours = new int[] {G,G,Y,Y,W,B,Y,O,Y};
        face = initFace(colours);
        face = face.rotate();
        adder.addFace(face);

        colours = new int[] {O,R,R,R,O,R,W,W,R};
        face = initFace(colours);
        face = face.rotate();
        adder.addFace(face);

        /* test each face individually for validity */
        cube = adder.getCube();
        colours = new int[] {O,R,R,R,O,R,W,W,R};
        testFace(cube, 0, colours);

        colours = new int[] {G,W,G,G,G,B,W,Y,G};
        testFace(cube, 9, colours);

        colours = new int[] {O,O,B,W,R,O,W,R,Y};
        testFace(cube, 18, colours);

        colours = new int[] {R,O,W,Y,B,B,B,G,B};
        testFace(cube, 27, colours);

        colours = new int[] {G,G,Y,Y,W,B,Y,O,Y};
        testFace(cube, 36, colours);

        colours = new int[] {B,B,R,G,Y,W,O,Y,O};
        testFace(cube, 45, colours);

        assertTrue(new CubeValidator(cube).isCubeValid());
    }

    @Nested
    @DisplayName("Adding a partial cube from scratch")
    class NewCubeBlank {
        @Test
        @DisplayName("Adding a partial cube from scratch 0")
        void partialCube0()
        {
            // Scramble:
            // L2 D' B2 F2 D L B D' R D' R B' U R U L R D2 L' R2 U L2 R' U' F2 L2 F D2 L R
            Cube cube = new Cube();

            int colours[] = {R,O,W,Y,B,B,B,G,B};

            FaceAdder adder = new FaceAdder(cube);

            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {O,O,B,W,R,O,W,R,Y};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {G,W,G,G,G,B,W,Y,G};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {B,B,R,G,Y,W,O,Y,O};
            face = initFace(colours);
            adder.addFace(face);


            colours = new int[] {O,R,R,R,O,R,W,W,R};
            face = initFace(colours);
            adder.addFace(face);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding a partial cube from scratch 1")
        void partialCube1()
        {
            // Scramble:
            // D U B F U R D' R2 D' U' B' U' L' D' F R U2 F2 L2 D' U2 L D' L2 D L D F2 R' D'
            Cube cube = new Cube();
            int colours[] = {G,Y,B,O,O,Y,Y,R,W};

            FaceAdder adder = new FaceAdder(cube);

            Face face = initFace(colours);
            face = face.rotate();
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {Y,B,Y,O,W,O,R,W,G};
            face = initFace(colours);
            adder.addFace(face);

            colours = new int[] {Y,G,O,W,R,Y,G,R,W};
            face = initFace(colours);
            face = face.rotate();
            adder.addFace(face);

            colours = new int[] {W,B,O,B,G,R,G,O,R};
            face = initFace(colours);
            adder.addFace(face);

            assertTrue(new CubeValidator(cube).isCubeValid());
        }
    }

    @Nested
    @DisplayName("Adding invalid configs causes no cube to be returned")
    class InvalidAdditionsFail
    {
        @Test
        @DisplayName("Adding invalid face to an otherwise valid cube")
        void addInvalidFace6()
        {
            // Scramble:
            // B' F' L2 F' L2 U L' U2 L' R2 B R' U' R B2 L2 U' B F' L' D U' L D U2 F2 L2 U2 B' R2

            int[] validConfig = new int[] {R,O,W,O,O,G,R,B,W,
                    B,Y,Y,R,G,G,R,O,B,
                    G,Y,O,O,R,R,R,W,Y,
                    G,B,Y,W,B,W,B,R,Y,
                    G,Y,W,B,W,R,O,G,O,
                    G,Y,W,W,Y,G,B,B,O};
            Cube cube = new Cube(validConfig);

            assertTrue(new CubeValidator(cube).isCubeValid());

            int[] invalidConfig = new int[] {R,O,W,O,O,G,R,B,W,
                    B,Y,Y,R,G,G,R,O,B,
                    G,Y,O,O,R,R,R,W,Y,
                    G,B,Y,W,B,W,B,R,Y,
                    6,6,6,6,6,6,6,6,6,
                    G,Y,W,W,Y,G,B,B,O};
            cube = new Cube(invalidConfig);

            FaceAdder adder = new FaceAdder(cube);

            Face face = initFace(new int[] {W,W,W,W,W,W,W,W,W});

            adder.addFace(face);

            assertFalse(new CubeValidator(cube).isCubeValid());
        }

        @Test
        @DisplayName("Adding invalid face to a single other face")
        void addInvalidFace2()
        {
            int[] input = new int[] {6,6,6,6,6,6,6,6,6,
                    6,6,6,6,6,6,6,6,6,
                    6,6,6,6,6,6,6,6,6,
                    W,G,R,B,B,G,Y,Y,O,
                    6,6,6,6,6,6,6,6,6,
                    6,6,6,6,6,6,6,6,6};
            Cube cube = new Cube(input);

            FaceAdder adder = new FaceAdder(cube);

            Face face = initFace(new int[] {W,W,W,W,W,W,W,W,W});

            adder.addFace(face);


            for (int i = 0; i < 54; i++) {
                if (i % 9 == 0)
                    System.out.println();
               System.out.print(cube.getLongArray().get(i).getColour() + " ");
            }

            /* should still keep the valid face */
            int colours[] = {W,G,R,B,B,G,Y,Y,O};
            testFace(cube, 27, colours);
        }
    }
}

