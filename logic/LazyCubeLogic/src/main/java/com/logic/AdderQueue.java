package com.logic;

import static com.logic.PermutationUtil.permute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Alexander Bull
 * @editor Alfred Roberts
 * @description Stores cube faces within an internal buffer, until all 6 faces
 * of a cube are added, where the {@link FaceAdder} will attempt to combine
 * them into a valid cube representation. If a face is added twice, the
 * confidence values of each colour are updated.
 */
public class AdderQueue {
    // Threshold for the number of correct squares to
    // consider a rotation of a face to be the same
    // as a face already in the cube
    private static final int ROTATION_THRESH = 7;
    // Average number of frames a face's colours
    // have to be seen for to try to add the face to the internal cube
    private final int AVERAGE_FRAME_THRESH = 10;
    // the percentage of the most times seen does the second need to be to try swapping it
    private static final double SWAP_THRESH = 0.3;

    // Current threshold for average number of frames needed to add
    // a face to the internal cube
    private int currentFrameThresh = AVERAGE_FRAME_THRESH;

    // Face adder to add faces to the internal cube
    private FaceAdder faceAdder = new FaceAdder();

    // Queue used store newly added faces, who have been
    // scanned < currentFrameThresh number of frames
    private final List<Face> faceQueue = new ArrayList<>();
    // Waiting buffer for "confident" faces to be added to the internal cube,
    // once all 6 faces have been scanned
    private final List<Face> faceBuffer = new ArrayList<>();

    // Buffer to store whether a face has been added or not
    private final HashMap<CubeColour, Boolean> inBuffer ;

    /**
     * Initialises a new AdderQueue
     */
    public AdderQueue() {
        inBuffer = new HashMap<>();
        for (CubeColour colour : CubeColour.values()) {
            inBuffer.put(colour, false);
        }
    }

    /**
     * Get length of face buffer
     * @return Size of face buffer list
     */
    public int getQueueLength() {
        return faceBuffer.size();
    }

    /**
     * Get face buffer that will be added to the internal
     * cube representation with {@code AdderQueue.flushBufferToAdder()}
     * @return List of faces in the buffer
     */
    public List<Face> getFaceQueue() {
        return faceBuffer;
    }

    /**
     * Adds a Face object to the {@code faceQueue}. If the {@code faceQueue} already contains
     * an element with the same center color as the Face object, the Face object at the
     * specified index in the {@code faceQueue} is updated using the
     * {@code AdderQueue.addFaceInstance} method.
     *
     * @param face The Face object to be added to the {@code faceQueue}
     */
    public void addElement(Face face){
        boolean inList = false;
        int index = 0;
        // Check if face is in face queue
        for(Face faceLoop : faceQueue){
            if (face.getCentreColour() == faceLoop.getCentreColour()){
                // this "if statement" could maybe cause issues due to the fact that it
                // checks being based off of the centre colour which if guessed
                // wrong will add values for the whole face colours wrongly
                inList = true;
                break;
            }
            index++;
        }
        // If the face is already in the queue, update face colours
        if (inList) {
            face = fixRotation(face, index);
            addFaceInstance(face, index);
        }
        else {
            faceQueue.add(face);
        }

        // If the face has been detected for num frames > currentFrameThresh,
        // add to the faceBuffer
        Face queueFace = faceQueue.get(index);
        CubeColour queueFaceColour = queueFace.getCentreColour();
        if (queueFace.getMinTimesSeen() > currentFrameThresh &&
                !inBuffer.get(queueFaceColour)) {
            faceBuffer.add(queueFace);
            inBuffer.put(queueFaceColour, true);
        }
    }

    /**
     * Takes a face and the index decided to check against in this object's {@code faceQueue} and
     * will rotate it until it is out of rotations or there are enough squares that match
     * the new face's (based on ROTATIONTHRESH). If this threshold is not reached this function
     * will use the most similar rotation it found
     * @param face - New face that will be rotated
     * @param index - index in {@code faceQueue} of face to check against
     * @return The new correctly rotated face
     */
    private Face fixRotation(Face face, int index){
        int count = 0;
        int countStore = 0;
        int indexStore = 0;
        // outer loop for each rotation
        for(int j = 0; j < 4; j++){
            // inner loop for each square in the rotation
            for (int i = 0; i < 9; i++){
                if(compareSquare(face.getSquare(i), faceQueue.get(index).getSquare(i))){
                    count++;
                }
            }
            // ROTATIONTHRESH is the minimum amount of squares needed to be the
            // same for a face to be identified as in the correct rotation
            if(count >= ROTATION_THRESH){
                countStore = count;
                break;
            } else if (count > countStore) {
                countStore = count;
                indexStore = j;
            }
            face = face.rotate();
        }
        //if the minimum threshold is not met it will rotate back around to the
        // most similar rotation it found
        if(countStore < ROTATION_THRESH){
            for(int i = 0; i < (indexStore + 1); i++){
                face = face.rotate();
            }
        }

        return face;
    }

    /**
     * Compares the colour that represents each inputted square
     * @param square1 - First square to check with
     * @param square2 - Second square to check against
     * @return true or false depending on if the CubeColour is the same
     */
    private boolean compareSquare(Square square1, Square square2){
        return square1.getColour() == square2.getColour();
    }

    /**
     * Updates the Face object at the specified index in the faceList list by iterating
     * through its squares and calling the {@code AdderQueue.addSquareInstance}
     * method for each square.
     *
     * @param face the Face object to be added to the {@code faceQueue}
     * @param index the index of the Face object in the {@code faceQueue} to be updated
     */
    private void addFaceInstance(Face face, int index){
        for(int i = 0; i < 9 ; i++){
            addSquareInstance(face, index, i);
        }
    }


    /**
     * Updates the square at the specified index in the Face object at the specified index
     * in the {@code faceQueue} with the square in the Face object passed to the addElement
     * method. The method calculates the average color confidence of the two squares and
     * sets the color confidence of the updated square accordingly.
     *
     * @param face the Face object passed to the addElement method
     * @param faceIndex the index of the Face object in the faceList list to be updated
     * @param squareIndex the index of the square in the Face object to be updated
     */
    private void addSquareInstance(Face face, int faceIndex, int squareIndex){
        Face storedFace = faceQueue.get(faceIndex);

        Square newSquare = storedFace.getSquare(squareIndex);

        newSquare.incrementColour(face.getSquare(squareIndex).getColour());
        faceQueue.get(faceIndex).setSquare(squareIndex, newSquare);

    }

    /**
     * Once all 6 faces have been scanned and added to {@code faceQueue},
     * adds the faces that the queue is "confident" enough to
     * the internal cube representation
     * @return List of faces that failed to be added. <br> Returns {@code null}
     *         if an unsolvable error (parity issue) is found.
     */
    public List<Face> addCandidateFaces() {
        List<Face> failedFaces = new ArrayList<>();
        // If the adder queue has faces
        if (getQueueLength() == 6) {
            failedFaces = flushBufferToAdder();
        }

        // Try to fix cube parity issues - try different orders
        // of face additions in the hope the parity issue goes away
        // TODO Change temporary parity fix
        if (faceAdder.getCube().isComplete() && faceAdder.getCube().parityCheckFail()) {
            boolean parityFix = fixCubeParity();
            if (!parityFix) {
                return null;
            }
        }
        return failedFaces;
    }

    /**
     * Temporary solution to try to fix cube parity issues by
     * trying different orders of face additions in the hope the
     * parity issue gets fixed
     * @return Boolean whether the parity issue was fixed or not
     */
    private boolean fixCubeParity() {
        int[] perm = {0,1,2,3,4,5};
        List<List<Integer>> perms = permute(perm);
        for (List<Integer> p : perms) {
            System.out.println("Trying face perm: " + p);
            faceAdder = new FaceAdder();
            for (int i = 0; i < faceBuffer.size(); i++) {
                Face face = faceBuffer.get(p.get(i));
                if (!faceAdder.addFace(face)) {
                    System.out.println("FAILED TO ADD " + face.getCentreColour());
//                    if(!tryColourSwaps(faceBuffer.indexOf(face))){
//                        failedFaces.add(face);
//                    }
                }
            }
            if (!faceAdder.getCube().parityCheckFail()) {
                System.out.println("Fixed with perm : " + p);
                return true;
            }
        }
        System.out.println("Failed to create correct config ... must be parity issue...");
        return false;
    }

    /**
     * Flush (but not empty) buffer of faces to the adder to
     * add them to the internal cube in the correct rotation
     * @return List of faces that failed to be added
     */
    private List<Face> flushBufferToAdder() {
        List<Face> failedFaces = new ArrayList<>();
        // Try to add each face to the internal cube
        // if the average "times seen" > 5
        for (Face face : faceBuffer) {
            if (!faceAdder.addFace(face)) {
                System.out.println("FAILED TO ADD " + face.getCentreColour());
                failedFaces.add(face);
            }
        }
        return failedFaces;
    }

    /**
     * This method tries to swap squares in a face based on the number of times they have been seen.
     * If a square's second colour has been seen more times than the SWAP_THRESH times the highest colour's seen count,
     * the colour is then swapped and then checked to be added. If the add is successful the function finishes as it found
     * a correct face variation
     * @param faceIndex The index of the face in the faceList to be considered for swaps.
     * @return true if a swap was successful, false otherwise.
     */
    private boolean tryColourSwaps(int faceIndex){
        Face face = faceBuffer.get(faceIndex);
        List<Integer> squaresToSwap = new ArrayList<>();

        // Find all squares that can be swapped
        for(int i = 0; i < 9 ; i++){
            Square square = face.getSquare(i);
            int secondCount = square.getSecondTimesSeenCount();

            if(secondCount > SWAP_THRESH * square.getTimesSeen()){
                squaresToSwap.add(i);
            }
        }
        if(squaresToSwap.size() > 0 && squaresToSwap.size() < 4){
            // Check every permutation of the squares that can be swapped
            List<List<Integer>> permutations = getSubsets(squaresToSwap);
            for (List<Integer> permutation : permutations) {
                // Make a copy of the face to modify
                Face modifiedFace = face;

                // Swap the squares according to the current permutation
                for (int i = 0; i < permutation.size(); i++) {
                    Square square = modifiedFace.getSquare(permutation.get(i));
                    CubeColour secondColour = square.getSecondTimesSeenColour();
                    swapSquare(faceIndex, permutation.get(i), secondColour);
                }

                // Attempt to add the modified face to the faceAdder
                if (faceAdder.addFace(modifiedFace)) {
                    System.out.println("SUCCESSFUL ON SWAP TO " + permutation);
                    for(int i = 0 ; i < permutation.size() ; i++){
                        Square squareChange = modifiedFace.getSquare(permutation.get(i));
                        squareChange.setColour(squareChange.getColour(), Integer.MAX_VALUE);
                        modifiedFace.setSquare(permutation.get(i), squareChange);
                    }
                    faceBuffer.set(faceIndex, modifiedFace);
                    return true;
                } else {
                    System.out.println("FAILED ON SWAP TO " + permutation);
                }
            }
        }


        return false;
    }

    private List<List<Integer>> getSubsets(List<Integer> inputList) {
        List<List<Integer>> subsets = new ArrayList<>();
        int n = inputList.size();

        // Generate all possible binary numbers with n digits
        for (int i = 0; i < (1 << n); i++) {
            List<Integer> subset = new ArrayList<>();

            // Check each bit in the binary number
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    // If the bit is set, add the corresponding element to the subset
                    subset.add(inputList.get(j));
                }
            }

            if(subset.size() != 0){
                subsets.add(subset);
            }
        }

        return subsets;
    }


    private void swapSquare(int faceIndex, int squareIndex, CubeColour colour){
        Face face = faceBuffer.get(faceIndex);
        Square square = face.getSquare(squareIndex);
        square.setColourValue(colour);
        face.setSquare(squareIndex, square);
    }

    /**
     * Gets the current working internal cube representation
     * @return Internal cube representation
     */
    public Cube getCube() {
        return faceAdder.getCube();
    }

    public void resetFace(CubeColour faceColour){
        // -1 represents if the the face is even inside of faceBuffer and catches when this is not true
        int index = -1;
        for(int i = 0; i < faceBuffer.size() ; i++){
            if (faceBuffer.get(i).getCentreColour() == faceColour){
                index = i;
            }
        }
        if(index != -1){
            faceBuffer.remove(index);
            inBuffer.replace(faceColour, false);
        }
    }

    /**
     * Sets turbo mode, reducing the average frame threshold for adding
     * faces to the internal cube representation to just 1 frame.
     * @param turboEnabled Whether to reduce the threshold or not
     */
    public void setTurbo(boolean turboEnabled) {
        if (turboEnabled) {
            // Set average threshold needed for detecting a face
            // to just one frame
            currentFrameThresh = 1;
        } else {
            currentFrameThresh = AVERAGE_FRAME_THRESH;
        }
    }
}
