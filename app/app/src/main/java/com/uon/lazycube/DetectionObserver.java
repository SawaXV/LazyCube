package com.uon.lazycube;

/**
 * Interface to implement the observer design pattern to notify observers
 * of new detection results that have been scanned from the TFL model
 */
public interface DetectionObserver {
    /**
     * Notify observer of new detection results
     * @param result New detection results scanned
     */
    void notifyResults(DetectionResult result);
}
