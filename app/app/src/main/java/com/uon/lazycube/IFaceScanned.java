package com.uon.lazycube;

import com.logic.Face;

import java.util.List;

/**
 * Interface used as a callback to retrieve scanned
 * faces the detector has already scanned
 */
public interface IFaceScanned {
     /**
      * Getter for retrieving a list of already scanned faces
      * @return List of scanned faces
      */
     List<Face> getScannedFaces();
}
