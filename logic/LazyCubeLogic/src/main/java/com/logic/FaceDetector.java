package com.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;

/**
 * @author Alfred Roberts, Alexander Bull
 * @description Collection of methods used to detect faces from given
 * {@link DetectionCenter} detection results.
 */
public class FaceDetector {

    // Used for thresholds for online checks, so they're all relative
    // to the average size of the detected cube squares
    private static float avgSquareWidth = 0;

    /**
     * Get the center of rectangle
     * @param top Top left coordinates
     * @param bot Bottom right coordinates
     * @return Center coordinates (x,y)
     */
    public static int[] getCenter(int[] top, int[] bot) {
        int x = (top[0] + bot[0]) / 2;
        int y = (top[1] + bot[1]) / 2;
        return new int[]{x, y};
    }

    /**
     * Checks if a given coordinate is within a rectangular area
     * @param point Coordinate to check
     * @param box List of box coordinates, in the form [top left, bot right]
     * @return True if point is within the rectangular area, false otherwise
     */
    public static boolean pointInBox(int[] point, ArrayList<int[]> box) {
        boolean x0 = point[0] > box.get(0)[0];
        boolean x1 = point[0] < box.get(1)[0];
        boolean y0 = point[1] > box.get(0)[1];
        boolean y1 = point[1] < box.get(1)[1];
        return x0 && x1 && y0 && y1;
    }

    /**
     * Returns groupings of faces, collections of 9 detection objects within
     * a detected face, given a list of detection results
     * @param predictions List of object detection results, as a {@link PredictionList}
     * @return A list of {@link DetectionCenter} lists, holding 9 detected centers
     * believed to be within a face
     */
    public static ArrayList<ArrayList<DetectionCenter>> get_faces(PredictionList predictions){
        ArrayList<ArrayList<DetectionCenter>> faces = new ArrayList<>();

        for(int i = 0 ; i < predictions.getLength(); i++){
            // Ignore predictions if it's NOT a face
            if (predictions.getClasses().get(i) != 6){
                continue;
            }

            // Get face detection bounding box
            int[] face_box = predictions.getBoxes().get(i);

            int[] face_bot = {face_box[2], face_box[3]};
            int[] face_top = {face_box[0], face_box[1]};

            // List for what we're considering to be a face
            ArrayList<DetectionCenter> group = new ArrayList<>();
            int j = 0;
            // Look at all other detection results that are not faces
            for(var box : predictions.getBoxes()){
                // Ignore the same box, and faces
                if (i == j || predictions.getClasses().get(j) == 6){
                    j++;
                    continue;
                }
                // Get detection box dimensions
                int[] bot = {box[2], box[3]};
                int[] top = {box[0], box[1]};

                // Update average detection result size
                avgSquareWidth = (avgSquareWidth + Math.abs(top[0] - bot[0])) / 2;
                avgSquareWidth = (avgSquareWidth + Math.abs(bot[1] - top[1])) / 2;

                // Create detection result center object
                DetectionCenter center = new DetectionCenter(
                        getCenter(top, bot)[0],
                        getCenter(top, bot)[1],
                        CubeColour.values()[predictions.getClasses().get(j)]
                );

                // Check the point is within a face
                int[] centerList = new int[] {center.getX(), center.getY()};
                ArrayList<int[]> box_placeholder = new ArrayList<>();
                box_placeholder.add(face_top);
                box_placeholder.add(face_bot);

                if (pointInBox(centerList, box_placeholder)){
                    group.add(center);
                }
                j++;
            }
            // Only add this face grouping if there is exactly 9 squares
            if (group.size() == 9)
                faces.add(group);
        }

        return faces;
    }

    /**
     * Checks if a point lies on a line, given a straight line equation
     * @param p Center point to check
     * @param m m variable of the y=mx+c equation
     * @param c c variable of the y=mx+c equation
     * @return True if the point lies on the line, given some "leeway",
     * false otherwise
     */
    private static boolean onLine(DetectionCenter p, float m, float c) {
        double y = m * p.getX() + c;
        return isClose(p.getY(), (int)y, (int) avgSquareWidth/2);
    }

    /**
     * Calculate a y=mx+c equation between two points
     * @param point1 First point coordinate
     * @param point2 Second point coordinate
     * @return Line equation variables, of form [m,c]
     */
    private static float[] getLine(DetectionCenter point1, DetectionCenter point2) {
        float m = ((point2.getY() - point1.getY()) / (float) (point2.getX() - point1.getX()));
        float c = point1.getY() - (m * point1.getX());
        return new float[]{m, c};
    }

    /**
     * Returns list of candidate points that could be the top left corner of a face.
     * Used to help the edge case that the cube is displayed as a "perfect" diamond shape
     * @param a_list List of ordered pointers for finding the top left corner
     * @param key Function used for ordering points for finding the top left corner
     * @return List of candidate points
     */
    private static ArrayList<DetectionCenter> first_occs(ArrayList<DetectionCenter> a_list, Function<DetectionCenter, Integer> key){
        if (a_list.size() == 0){
            return new ArrayList<>();
        }
        int index = 0;
        ArrayList<DetectionCenter> firsts = new ArrayList<>();
        firsts.add(a_list.get(index));

        int last = key.apply(firsts.get(0));
        index++;
        if (index >= a_list.size()){
            return firsts;
        }
        while(isClose(last, key.apply(a_list.get(index)), (int) avgSquareWidth/2)){
            firsts.add(a_list.get(index));
            last = key.apply(a_list.get(index));
            index++;
            if (index >= a_list.size()){
                break;
            }
        }
        return firsts;
    }

    /**
     * Return list of candidate points that could be the top right corner
     * Used to help the edge case that the cube is displayed as a "perfect" diamond shape
     * @param a_list List of ordered pointers for finding the top right corner
     * @param key Function used for ordering points for finding the top right corner
     * @return List of candidate points
     */
    private static ArrayList<DetectionCenter> last_occs(ArrayList<DetectionCenter> a_list, Function<DetectionCenter, Integer> key){
        if (a_list.size() == 0){
            return new ArrayList<>();
        }
        int index = a_list.size() - 1;
        ArrayList<DetectionCenter> lasts = new ArrayList<>();
        lasts.add(a_list.get(index));

        int last = key.apply(lasts.get(0));
        index--;
        if (index < 0){
            return lasts;
        }
        while(isClose(last, key.apply(a_list.get(index)), (int) avgSquareWidth/2)){
            lasts.add(a_list.get(index));
            last = key.apply(a_list.get(index));
            index--;
            if (index < 0){
                break;
            }
        }
        return lasts;
    }

    /**
     * Helper math method to check if two numbers are close, given some tolerance
     * @param a First number
     * @param b Second number
     * @param tolerance Tolerance of what is considered "close"
     * @return True if the two numbers are within {@code tolerance} distance
     * away, false otherwise
     */
    static boolean isClose(int a, int b, int tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * Order a face's points from top left to bottom right
     *<br>
     *<br> Example face order:
     *<br>     0 1 2
     *<br>     3 4 5
     *<br>     6 7 8
     *<br> Where these are indices of points in each face.
     *
     *<br> Works by finding the top left and top right points, and drawing a
     * line between them to find the remaining points in a row. Once found,
     * removes these points from consideration, and repeats until there are no
     * points left.
     *
     *<br> See for further details:
     *     https://stackoverflow.com/questions/29630052/ordering-coordinates-from-top-left-to-bottom-right
     *     https://www.researchgate.net/publication/282446068_Automatic_chessboard_corner_det
     * @param box_centers
     * @return
     */
    public static ArrayList<DetectionCenter> order_face(ArrayList<DetectionCenter> box_centers){
        ArrayList<DetectionCenter> points = new ArrayList<>();

        // Repeat until all points have been considered
        while (box_centers.size() > 0){
            // List to find top left point
            ArrayList<DetectionCenter> a_list = new ArrayList<>(box_centers);
            a_list.sort(Comparator.comparingInt(p-> ((p.getX()) + (p.getY()))));
            // find list of points that could be the upper left point,
            // if multiple, find the first one when sorted smallest (p.getX() + p.getY()) value
            ArrayList<DetectionCenter> a_temp = first_occs(a_list, (p-> (p.getX()) + (p.getY())));
            a_temp.sort(Comparator.comparingInt(DetectionCenter::getX));
            DetectionCenter a = a_temp.get(0);

            // List to find top right point
            ArrayList<DetectionCenter> b_list = new ArrayList<>(box_centers);
            b_list.sort(Comparator.comparingInt(p-> ((p.getX()) - (p.getY()))));
            // find list of points that could be the upper right point,
            // if multiple, find the first one when sorted smallest (p.getX() - p.getY()) value
            ArrayList<DetectionCenter> b_temp = last_occs(b_list, (p-> (p.getX()) - (p.getY())));
            b_temp.sort(Comparator.comparingInt(DetectionCenter::getX));
            DetectionCenter b = b_temp.get(0);

            // If edge case where the top left & right values are the same
            if (a.getX() == b.getX()) {
                points.add(a);
                box_centers.remove(a);
                continue;
            }

            // Find line between top left and right points
            float[] line = getLine(a, b);
            float m = line[0];
            float c = line[1];

            ArrayList<DetectionCenter> row_points = new ArrayList<>();
            ArrayList<DetectionCenter> remaining_points = new ArrayList<>();

            // Find all points that lay on that line
            for (var k : box_centers){
                if (onLine(k, m, c)){
                    row_points.add(k);
                }
                else {
                    remaining_points.add(k);
                }
            }

            // Sort points on row via x-axis
            row_points.sort(Comparator.comparingDouble(DetectionCenter::getX));
            points.addAll(row_points);

            // Remove points that lay on the line from consideration
            box_centers = new ArrayList<>(remaining_points);
        }
        return points;
    }


}
