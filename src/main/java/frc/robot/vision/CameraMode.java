package frc.robot.vision;

/**
 * LimeLight camera modes
 * */
public enum CameraMode {
    /**
     * Raises Camera Exposure, Disables Vision Processing, and Turns off Vision LED
     */
    Drive,

    /**
     * Lowers Camera Exposure, Enabled Vision Processing, and Turns on Vision LED
     */
    Vision,
    /**
     * Drive Mode but with strobe function on LED
     */
    Disco
}
