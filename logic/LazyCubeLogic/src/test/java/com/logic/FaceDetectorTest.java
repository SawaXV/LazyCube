package com.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FaceDetectorTest {

    @Nested
    @DisplayName("Valid detection tests")
    class Valid {
        @Test
        @DisplayName("Is point in box?")
        void testPointInBox() {
            int[] point = {200, 200};
            ArrayList<int[]> box = new ArrayList<>();
            box.add(new int[]{100, 100});
            box.add(new int[]{500, 500});

            assertTrue(FaceDetector.pointInBox(point, box));
        }

        @Test
        @DisplayName("Get ordered square positions of face from input (valid test)")
        void testGetFaces1() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{120, 158, 180, 216}, 1));
            predictions.add(new Prediction(99, new int[]{123, 231, 182, 290}, 5));
            predictions.add(new Prediction(99, new int[]{122, 298, 183, 355}, 1));
            predictions.add(new Prediction(99, new int[]{191, 157, 248, 214}, 1));
            predictions.add(new Prediction(99, new int[]{194, 226, 250, 285}, 3));
            predictions.add(new Prediction(99, new int[]{193, 298, 247, 353}, 5));
            predictions.add(new Prediction(99, new int[]{54, 160, 253, 352}, 6));
            predictions.add(new Prediction(99, new int[]{53, 232, 112, 293}, 1));
            predictions.add(new Prediction(99, new int[]{53, 302, 110, 362}, 3));
            predictions.add(new Prediction(99, new int[]{50, 162, 109, 219}, 5));


            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> output = FaceDetector.get_faces(predictionList);


            ArrayList<Object> answers = new ArrayList<>(){};
            answers.add(CubeColour.GREEN);
            answers.add(150);
            answers.add(187);
            answers.add(CubeColour.YELLOW);
            answers.add(152);
            answers.add(260);
            answers.add(CubeColour.GREEN);
            answers.add(152);
            answers.add(326);
            answers.add(CubeColour.GREEN);
            answers.add(219);
            answers.add(185);
            answers.add(CubeColour.BLUE);
            answers.add(222);
            answers.add(255);
            answers.add(CubeColour.YELLOW);
            answers.add(220);
            answers.add(325);
            answers.add(CubeColour.GREEN);
            answers.add(82);
            answers.add(262);
            answers.add(CubeColour.BLUE);
            answers.add(81);
            answers.add(332);
            answers.add(CubeColour.YELLOW);
            answers.add(79);
            answers.add(190);

            for(int i = 0 ; i < 27 ;i++){
                assertEquals(output.get(0).get(i/3).getColour(), answers.get(i));
                assertEquals(output.get(0).get(i/3).getX(), answers.get(i + 1));
                assertEquals(output.get(0).get(i/3).getY(), answers.get(i + 2));
                i += 2;
            }

        }

        @Test
        @DisplayName("Get ordered square positions of face from input (valid test)")
        void testGetFaces2() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> output = FaceDetector.get_faces(predictionList);

            ArrayList<Object> answers = new ArrayList<>(){};
            answers.add(CubeColour.BLUE);
            answers.add(173);
            answers.add(248);
            answers.add(CubeColour.BLUE);
            answers.add(248);
            answers.add(244);
            answers.add(CubeColour.ORANGE);
            answers.add(89);
            answers.add(179);
            answers.add(CubeColour.ORANGE);
            answers.add(175);
            answers.add(327);
            answers.add(CubeColour.WHITE);
            answers.add(241);
            answers.add(170);
            answers.add(CubeColour.RED);
            answers.add(93);
            answers.add(253);
            answers.add(CubeColour.BLUE);
            answers.add(252);
            answers.add(318);
            answers.add(CubeColour.YELLOW);
            answers.add(96);
            answers.add(331);
            answers.add(CubeColour.ORANGE);
            answers.add(166);
            answers.add(177);

            for(int i = 0 ; i < 27 ;i++){
                assertEquals(output.get(0).get(i/3).getColour(), answers.get(i));
                assertEquals(output.get(0).get(i/3).getX(), answers.get(i + 1));
                assertEquals(output.get(0).get(i/3).getY(), answers.get(i + 2));
                i += 2;
            }

        }

        @Test
        @DisplayName("Orders Faces (valid test)")
        void testOrderFace() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> input = FaceDetector.get_faces(predictionList);

            ArrayList<DetectionCenter> output = FaceDetector.order_face(input.get(0));

            ArrayList<DetectionCenter> answer = new ArrayList<>();
            answer.add(new DetectionCenter(89,179, CubeColour.ORANGE));
            answer.add(new DetectionCenter(166,177, CubeColour.ORANGE));
            answer.add(new DetectionCenter(241,170, CubeColour.WHITE));
            answer.add(new DetectionCenter(93,253, CubeColour.RED));
            answer.add(new DetectionCenter(173,248, CubeColour.BLUE));
            answer.add(new DetectionCenter(248,244, CubeColour.BLUE));
            answer.add(new DetectionCenter(96,331, CubeColour.YELLOW));
            answer.add(new DetectionCenter(175,327, CubeColour.ORANGE));
            answer.add(new DetectionCenter(252,318, CubeColour.BLUE));


            for(int i = 0; i < 9; i++){
                assertEquals(output.get(i).getColour(), answer.get(i).getColour());
                assertEquals(output.get(i).getX(), answer.get(i).getX());
                assertEquals(output.get(i).getY(), answer.get(i).getY());
            }
        }

    }

    @Nested
    @DisplayName("Negative detection tests")
    class Negative {

        @Test
        @DisplayName("Is point in box? (negative test - x coord)")
        void testPointInBox1() {
            int[] point = {2000, 200};
            ArrayList<int[]> box = new ArrayList<>();
            box.add(new int[]{100, 100});
            box.add(new int[]{500, 500});

            assertFalse(FaceDetector.pointInBox(point, box));
        }

        @Test
        @DisplayName("Is point in box? (negative test - y coord)")
        void testPointInBox2() {
            int[] point = {200, 2000};
            ArrayList<int[]> box = new ArrayList<>();
            box.add(new int[]{100, 100});
            box.add(new int[]{500, 500});

            assertFalse(FaceDetector.pointInBox(point, box));
        }

        @Test
        @DisplayName("Is point in box? (negative test - both coords)")
        void testPointInBox3() {
            int[] point = {2000, 2000};
            ArrayList<int[]> box = new ArrayList<>();
            box.add(new int[]{100, 100});
            box.add(new int[]{500, 500});

            assertFalse(FaceDetector.pointInBox(point, box));
        }

        @Test
        @DisplayName("Get Squares (negative test - NULL colour values)")
        void testGetFaces1() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> output = FaceDetector.get_faces(predictionList);

            ArrayList<Object> answers = new ArrayList<>(){};
            answers.add(CubeColour.NULL);
            answers.add(173);
            answers.add(248);
            answers.add(CubeColour.NULL);
            answers.add(248);
            answers.add(244);
            answers.add(CubeColour.NULL);
            answers.add(89);
            answers.add(179);
            answers.add(CubeColour.NULL);
            answers.add(175);
            answers.add(327);
            answers.add(CubeColour.NULL);
            answers.add(241);
            answers.add(170);
            answers.add(CubeColour.NULL);
            answers.add(93);
            answers.add(253);
            answers.add(CubeColour.NULL);
            answers.add(252);
            answers.add(318);
            answers.add(CubeColour.NULL);
            answers.add(96);
            answers.add(331);
            answers.add(CubeColour.NULL);
            answers.add(166);
            answers.add(177);

            for(int i = 0 ; i < 27 ;i++){
                assertNotEquals(output.get(0).get(i/3).getColour(), answers.get(i));
                assertEquals(output.get(0).get(i/3).getX(), answers.get(i + 1));
                assertEquals(output.get(0).get(i/3).getY(), answers.get(i + 2));
                i += 2;
            }

        }

        @Test
        @DisplayName("Get Squares (negative test - NULL X Values)")
        void testGetFaces2() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> output = FaceDetector.get_faces(predictionList);

            ArrayList<Object> answers = new ArrayList<>(){};
            answers.add(CubeColour.BLUE);
            answers.add(0);
            answers.add(248);
            answers.add(CubeColour.BLUE);
            answers.add(0);
            answers.add(244);
            answers.add(CubeColour.ORANGE);
            answers.add(0);
            answers.add(179);
            answers.add(CubeColour.ORANGE);
            answers.add(0);
            answers.add(327);
            answers.add(CubeColour.WHITE);
            answers.add(0);
            answers.add(170);
            answers.add(CubeColour.RED);
            answers.add(0);
            answers.add(253);
            answers.add(CubeColour.BLUE);
            answers.add(0);
            answers.add(318);
            answers.add(CubeColour.YELLOW);
            answers.add(0);
            answers.add(331);
            answers.add(CubeColour.ORANGE);
            answers.add(0);
            answers.add(177);

            for(int i = 0 ; i < 27 ;i++){
                assertEquals(output.get(0).get(i/3).getColour(), answers.get(i));
                assertNotEquals(output.get(0).get(i/3).getX(), answers.get(i + 1));
                assertEquals(output.get(0).get(i/3).getY(), answers.get(i + 2));
                i += 2;
            }

        }

        @Test
        @DisplayName("Get Squares (negative test - NULL Y Values)")
        void testGetFaces3() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> output = FaceDetector.get_faces(predictionList);

            ArrayList<Object> answers = new ArrayList<>(){};
            answers.add(CubeColour.BLUE);
            answers.add(173);
            answers.add(0);
            answers.add(CubeColour.BLUE);
            answers.add(248);
            answers.add(0);
            answers.add(CubeColour.ORANGE);
            answers.add(89);
            answers.add(0);
            answers.add(CubeColour.ORANGE);
            answers.add(175);
            answers.add(0);
            answers.add(CubeColour.WHITE);
            answers.add(241);
            answers.add(0);
            answers.add(CubeColour.RED);
            answers.add(93);
            answers.add(0);
            answers.add(CubeColour.BLUE);
            answers.add(252);
            answers.add(0);
            answers.add(CubeColour.YELLOW);
            answers.add(96);
            answers.add(0);
            answers.add(CubeColour.ORANGE);
            answers.add(166);
            answers.add(0);

            for(int i = 0 ; i < 27 ;i++){
                assertEquals(output.get(0).get(i/3).getColour(), answers.get(i));
                assertEquals(output.get(0).get(i/3).getX(), answers.get(i + 1));
                assertNotEquals(output.get(0).get(i/3).getY(), answers.get(i + 2));
                i += 2;
            }

        }

        @Test
        @DisplayName("Orders Faces (negative test - NULL colour values)")
        void testOrderFace1() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> input = FaceDetector.get_faces(predictionList);

            ArrayList<DetectionCenter> output = FaceDetector.order_face(input.get(0));

            ArrayList<DetectionCenter> answer = new ArrayList<>();
            answer.add(new DetectionCenter(89,179, CubeColour.NULL));
            answer.add(new DetectionCenter(166,177, CubeColour.NULL));
            answer.add(new DetectionCenter(241,170, CubeColour.NULL));
            answer.add(new DetectionCenter(93,253, CubeColour.NULL));
            answer.add(new DetectionCenter(173,248, CubeColour.NULL));
            answer.add(new DetectionCenter(248,244, CubeColour.NULL));
            answer.add(new DetectionCenter(96,331, CubeColour.NULL));
            answer.add(new DetectionCenter(175,327, CubeColour.NULL));
            answer.add(new DetectionCenter(252,318, CubeColour.NULL));


            for(int i = 0; i < 9; i++){
                assertNotEquals(output.get(i).getColour(), answer.get(i).getColour());
                assertEquals(output.get(i).getX(), answer.get(i).getX());
                assertEquals(output.get(i).getY(), answer.get(i).getY());
            }
        }

        @Test
        @DisplayName("Orders Faces (negative test - NULL X values)")
        void testOrderFace2() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> input = FaceDetector.get_faces(predictionList);

            ArrayList<DetectionCenter> output = FaceDetector.order_face(input.get(0));

            ArrayList<DetectionCenter> answer = new ArrayList<>();
            answer.add(new DetectionCenter(0,179, CubeColour.ORANGE));
            answer.add(new DetectionCenter(0,177, CubeColour.ORANGE));
            answer.add(new DetectionCenter(0,170, CubeColour.WHITE));
            answer.add(new DetectionCenter(0,253, CubeColour.RED));
            answer.add(new DetectionCenter(0,248, CubeColour.BLUE));
            answer.add(new DetectionCenter(0,244, CubeColour.BLUE));
            answer.add(new DetectionCenter(0,331, CubeColour.YELLOW));
            answer.add(new DetectionCenter(0,327, CubeColour.ORANGE));
            answer.add(new DetectionCenter(0,318, CubeColour.BLUE));


            for(int i = 0; i < 9; i++){
                assertEquals(output.get(i).getColour(), answer.get(i).getColour());
                assertNotEquals(output.get(i).getX(), answer.get(i).getX());
                assertEquals(output.get(i).getY(), answer.get(i).getY());
            }
        }

        @Test
        @DisplayName("Orders Faces (negative test - NULL Y values)")
        void testOrderFace3() {
            ArrayList<Prediction> predictions = new ArrayList<>();

            predictions.add(new Prediction(99, new int[]{140, 216, 207, 281}, 3));
            predictions.add(new Prediction(99, new int[]{217, 212, 280, 276}, 3));
            predictions.add(new Prediction(99, new int[]{57, 149, 121, 209}, 0));
            predictions.add(new Prediction(99, new int[]{142, 294, 209, 360}, 0));
            predictions.add(new Prediction(99, new int[]{210, 142, 273, 199}, 4));
            predictions.add(new Prediction(99, new int[]{59, 219, 128, 287}, 2));
            predictions.add(new Prediction(99, new int[]{53, 140, 281, 364}, 6));
            predictions.add(new Prediction(99, new int[]{220, 286, 284, 350}, 3));
            predictions.add(new Prediction(99, new int[]{62, 299, 130, 364}, 5));
            predictions.add(new Prediction(99, new int[]{131, 147, 201, 207}, 0));

            PredictionList predictionList = new PredictionList(predictions);

            ArrayList<ArrayList<DetectionCenter>> input = FaceDetector.get_faces(predictionList);

            ArrayList<DetectionCenter> output = FaceDetector.order_face(input.get(0));

            ArrayList<DetectionCenter> answer = new ArrayList<>();
            answer.add(new DetectionCenter(89,0, CubeColour.ORANGE));
            answer.add(new DetectionCenter(166,0, CubeColour.ORANGE));
            answer.add(new DetectionCenter(241,0, CubeColour.WHITE));
            answer.add(new DetectionCenter(93,0, CubeColour.RED));
            answer.add(new DetectionCenter(173,0, CubeColour.BLUE));
            answer.add(new DetectionCenter(248,0, CubeColour.BLUE));
            answer.add(new DetectionCenter(96,0, CubeColour.YELLOW));
            answer.add(new DetectionCenter(175,0, CubeColour.ORANGE));
            answer.add(new DetectionCenter(252,0, CubeColour.BLUE));


            for(int i = 0; i < 9; i++){
                assertEquals(output.get(i).getColour(), answer.get(i).getColour());
                assertEquals(output.get(i).getX(), answer.get(i).getX());
                assertNotEquals(output.get(i).getY(), answer.get(i).getY());
            }
        }

    }


}
