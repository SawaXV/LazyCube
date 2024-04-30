package com.uon.lazycube;

/**
 * Enum representing different scanning stages, used by the {@link Detector}
 * to notify the {@link CameraFragment}
 */
public enum ScanProgress {
    /// Signals the cube has completed scanning
    COMPLETED,
    /// Signals the cube faces have failed to be added correctly
    FAIL,
    /// Signals the cube has continued to fail to add the faces, suggesting
    /// the scan cannot be completed
    COMPLETE_FAIL,
    /// Signals the rotation hint overlay should be shown
    ROTATE_HINT,
    /// Signals the rotation hint overlay should be hidden
    ROTATE_HINT_STOP
}
