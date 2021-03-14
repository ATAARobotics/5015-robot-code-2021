package frc.robot.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Class to communicate with the limelight via Network Tables.
 *
 * */

public class LimeLight {

    private NetworkTable table;
    NetworkTableEntry tv;
    NetworkTableEntry tx;
    NetworkTableEntry ty;
    NetworkTableEntry ta;
    NetworkTableEntry tc;
    NetworkTableEntry ledMode;
    NetworkTableEntry camMode;

    public LimeLight(){
        table = NetworkTableInstance.getDefault().getTable("limelight-swatbot");
        tv = table.getEntry("tv");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta = table.getEntry("ta");
        tc = table.getEntry("tc");
        ledMode =  table.getEntry("ledMode");
        camMode = table.getEntry("camMode");

    }

    /**
     * Returns a number to represent whether the limelight has detected any valid targets.
     * @return Returns 0 if no targets are found or 1 if targets are found.
     */
    public double getTv(){
        return tv.getDouble(2);
    }

    /**
     * Returns the horizontal offset from the crosshair to the target
     * @return Target Horizontal Offset from Crosshair. (Value between -29.8 and 29.8)
     */
    public double getTx(){
        return tx.getDouble(0);
    }

    /**
     * Returns the vertical offset from crosshair to target
     * @return Target Vertical Offset from Crosshair. (Value between -24.85 and 24.85)
     */
    public double getTy(){
        return ty.getDouble(0);
    }

    /**
     * Returns percentage representing how much of the image the target(s) cover
     * @return Target Area. (0% of the image to 100% of the image)
     */
    public double getTa(){
        return ta.getDouble(0);
    }

    /**
     * Returns rgb values at the target's crosshair
     * @return Red, Green and Blue values at crosshair location
     */
    public double getTc(){
        return tc.getDouble(0);
    }

    /**
     * Set the limelight LED to follow LED mode set in current pipeline
     */
    public void ledDefault(){
        ledMode.setDouble(0);
    }

    /**
     * Force the limelight LED to on
     */
    public void ledOn(){
        ledMode.setDouble(3);
    }

    /**
     * Force the limelight LED to off
     */
    public void ledOff(){
        ledMode.setDouble(1);
    }

    /**
     * Force the limelight LED to Blink
     */
    public void ledStrobe(){
        ledMode.setDouble(2);
    }

    /**
     * Switch LimeLight between different modes. Takes CameraMode as a parameter to set camera mode.
     * @param mode The CameraMode for the mode to set the camera to.
     * @see CameraMode
     * */
    public void setCameraMode(CameraMode mode) {
        switch (mode) {
            case Drive:
                camMode.setDouble(1);
                ledMode.setDouble(1);
                break;
            case Vision:
                camMode.setDouble(0);
                ledMode.setDouble(3);
                break;
            case Disco:
                camMode.setDouble(1);
                ledMode.setDouble(2);
                break;
        }
    }

    public String getCamMode() {
        switch ((int)camMode.getDouble(-1)) {
            case 0:

                return "Vision";
                
            case 1:

                return "Driving";
        
            default:

                return "Error";
        }
    }

}
