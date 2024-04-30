package com.uon.lazycube;

import com.logic.Cube;
import com.logic.CubeColour;

/**
 * Callback interface, to implemented the observer design pattern, to define
 * different behaviours during the scanning process
 */
public interface DetectionProgress {
    /**
     * Notify a new {@link ScanProgress} signal
     * is made
     * @param cube Current internal cube representation at the
     *             time of notification
     * @param progress Signal for new progress
     */
    void notifyProgress(Cube cube, ScanProgress progress);

    /**
     * Notify a new face that has been scanned
     * @param faceColour Center face colour scanned
     */
    void notifyFaceScan(CubeColour faceColour);

    /**
     * Notify a face that must be rescanned (due to scanning errors)
     * @param faceColour Center face colour to rescan
     */
    void notifyFaceRescan(CubeColour faceColour);

    /**
     * Notify a scan failure that cannot be fixed
     */
    void notifyFail();
}
