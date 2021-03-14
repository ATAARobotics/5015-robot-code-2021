package frc.robot;

import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.vision.LimeLight;

public class Align {

    private LimeLight limeLight = null;

    // Variables for limelight distance tracking
    private double P = 0.04;
    private double I = 0.03;
    private double D = 0.007;
    private double tolerance = 2.5;

    private PIDController visionAlignPID = new PIDController(P, I, D);

    //Variables to get distance to wall. THESE MUST BE ACCURATE.
    private double targetHeight = 89;
    private double limelightHeight = 41.0;
    private double limelightAngle = 30.0;
    private double distanceToWall = 0;
    private double angleToTarget = 0;

    public Align(RobotMap robotMap){
        this.limeLight = robotMap.limeLight;
        visionAlignPID.setTolerance(tolerance);
    }

    public double visionAlign(){
        return visionAlignPID.calculate(limeLight.getTx(), 0.0);
    }
    public boolean atSetpoint(){
        visionAlignPID.calculate(limeLight.getTx(),0.0);
        return visionAlignPID.atSetpoint();
    }

    public double getDistance(){
        // Calculate distance to wall using limelight.
        angleToTarget = limeLight.getTy();
        distanceToWall = (targetHeight - limelightHeight) / Math.tan(Math.toRadians(limelightAngle + angleToTarget));

        return distanceToWall;
    }
}
