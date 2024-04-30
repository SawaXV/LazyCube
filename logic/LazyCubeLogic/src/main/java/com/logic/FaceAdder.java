package com.logic;
import java.util.*;

/**
 * @author Alfred Roberts
 * @editor Sarwar Rashid, Alexander Bull
 * @description Add face to a cube configuration and validate that face against other faces within a cube
 */
public class FaceAdder {
    private Cube cube;

    /**
     * Creates a new FaceAdder instance, using a blank cube to
     * start from
     */
    public FaceAdder(){
        this.cube = new Cube();
    }

    /**
     * Creates a new FaceAdder instance, with the given cube as the input
     * to add a new faces to
     * @param cube
     * Cube configuration to add faces to
     */
    public FaceAdder(Cube cube){
        this.cube = cube;
    }

    /**
     * Finds non empty faces in cube that are not added to tmp cube
     * @param cube
     * Cube with all faces already added (but not valid)
     * @param tmpCube
     * Current working cube config from csp problem
     * @return
     * Index of a face within tmpCube that isn't added yet, or -1 if there
     * aren't any face left
     */
    private int findUnassignedVariable(Cube cube, Cube tmpCube) {
        for (int i = 0; i < 6; i ++) {
            Face face = cube.getFace(i);
            Face tmpFace = tmpCube.getFace(i);
            if (!face.isEmpty() && tmpFace.isEmpty())
                return i;
        }
        return -1;
    }

    /**
     * Finds whether all the faces from cube have been added to tmpCube
     * @param cube
     * Cube with all faces already added (but not valid)
     * @param tmpCube
     * Current working cube config from csp problem
     * @return
     * True, if all faces have been assigned, false otherwise.
     */
    private boolean assigned(Cube cube, Cube tmpCube) {
        boolean allAssigned = true;
        ArrayList<Integer> assignedFaces = new ArrayList<>();
        for (int i = 0; i < 6; i ++) {
            Face face = cube.getFace(i);
            if (!face.isEmpty())
                assignedFaces.add(i);
        }
        for (int i = 0; i < 6; i ++) {
            Face face = tmpCube.getFace(i);
            if (face.isEmpty() && assignedFaces.contains(i)) {
                allAssigned = false;
            }
        }
        return allAssigned;
    }

    /**
     * Recursive constraint specification problem (CSP).
     * Creates a new cube config, and starts finding valid faces to
     * add, recursively backtracking if a specific rotation doesn't work
     * @param cube
     * Cube object with all the current faces within the cube config
     * @param tmpCube
     * Current working cube config, to add new face rotations to
     * @return
     * The new cube config, with new valid rotations
     */
    public Cube recursiveCSPBacktrack(Cube cube, Cube tmpCube) {
        // https://web.stanford.edu/class/cs227/Lectures/lec14.pdf
        CubeValidator cubeValidator = new CubeValidator(tmpCube);
        // If csp problem is complete
        if (cubeValidator.isCubeValid() && assigned(cube, tmpCube)) {
            return tmpCube;
        }
        // Find an "unassigned" face
        int var = findUnassignedVariable(cube, tmpCube);
        if (var == -1)
            return null;
        Face face = cube.getFace(var);
        // Get the face's neighbors
        int[] neighbors = tmpCube.getFaceNeighbors(var);
        // For all values in the variable's domain (rotations)
        for (int value = 0; value < 5; value ++) {
            // Add current rotation to tmpCube
            addFaceToConfig(tmpCube, face);
            face = face.rotate(); // Add and rotate for later
            boolean pass = true;
            // Check if all the neighbors have valid config with the current
            // variable
            for (int neighbor : neighbors) {
                int[] facePair = new int[]{var, neighbor};
                if (!cubeValidator.edgesValid(facePair) || !cubeValidator.invalidCorners(facePair))
                    pass = false;
            }

            if (pass) { // local constraint?
                // Add face to assignment
                Cube result = recursiveCSPBacktrack(cube, tmpCube);
                if (result != null) {
                    return result;
                }
            }
            // Remove face from cube
            tmpCube.setFace(var, new Face(face.getCentreColour()));
        }
        return null; // Fail
    }

    /**
     * Adds a new face to the cube config. Returns a new config with the
     * added face
     * @param face
     * Cube face to add
     * @return
     * New cube config. Returns <code>null</code> if the addition failed
     */
    public boolean addFace(Face face){
        // Copy current working cube
        Cube candidateCube = new Cube(cube);
        CubeValidator cubeValidator = new CubeValidator(candidateCube);
        // First, try to add the face without any other rotations from other
        // faces...
        addFaceToConfig(candidateCube, face);

        if (!cubeValidator.countsAreValid())
            return false;

        for (int i = 0; i < 5; i ++) {
            if (cubeValidator.isCubeValid()) {
                //candidateCube.show();
                cube = candidateCube;
                return true;
            }
            face = face.rotate();
            addFaceToConfig(candidateCube, face);
        }
        // If fails, we must do recursive csp backtracking
        Cube newCube = recursiveCSPBacktrack(candidateCube, new Cube());
        if (newCube != null) {
            cube = newCube;
            return true;
        }
        return false;
    }

    /**
     * Adds a face to a cube config, via the center colour
     * @param cube
     * Cube config to add the face to
     * @param face
     * Face object to add
     */
    public void addFaceToConfig(Cube cube, Face face){
        if(face.getCentreColour() == CubeColour.ORANGE){
            cube.setFace(0, face);
        }
        if(face.getCentreColour() == CubeColour.GREEN){
            cube.setFace(1, face);
        }
        if(face.getCentreColour() == CubeColour.RED){
            cube.setFace(2, face);
        }
        if(face.getCentreColour() == CubeColour.BLUE){
            cube.setFace(3, face);
        }
        if(face.getCentreColour() == CubeColour.WHITE){
            cube.setFace(4, face);
        }
        if(face.getCentreColour() == CubeColour.YELLOW){
            cube.setFace(5, face);
        }
    }

    public Cube getCube() {
        return cube;
    }
}
