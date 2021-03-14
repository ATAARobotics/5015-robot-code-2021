package frc.robot;

// import libraries
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * A file in charge of managing the drivetrain and all drive functions of the robot
 *
 */
public class SWATDrive {

    //slow variable
    private boolean slow = false;
    //low gear variable
    private boolean lowGear = true;

    private double maxTurnSpeed;
    private double maxStraightSpeed;
    private RobotMap robotMap;

    public SWATDrive(RobotMap robotMap) {
        this.robotMap = robotMap;
        maxStraightSpeed = 1;
        maxTurnSpeed = 0.8;
    }
    public void gearShift() {
        lowGear = !lowGear;
        if(lowGear) {
            robotMap.getGearShift().set(DoubleSolenoid.Value.kReverse);
        } else {
            robotMap.getGearShift().set(DoubleSolenoid.Value.kForward);
        }
    }
    public void gearShiftSafe() {
        robotMap.getGearShift().set(DoubleSolenoid.Value.kReverse);
    }
    public void slow() {
        slow = !slow;
        if(slow) {
            robotMap.getDriveTrain().setMaxOutput(0.7);
            maxTurnSpeed = 0.7;
            maxStraightSpeed = 0.7;
        } else {
            robotMap.getDriveTrain().setMaxOutput(1);
            maxTurnSpeed = 0.8;
            maxStraightSpeed = 1;
        }
    }

    public double getMaxTurnSpeed() {
        return maxTurnSpeed;
    }
    public double getMaxStraightSpeed() {
        return maxStraightSpeed;
    }

    public double getTemperature() {
        return robotMap.getDrivetrainTemperature();
    }

    public void arcadeDrive(double speed, double rotation) {
        robotMap.getDriveTrain().arcadeDrive(-speed, rotation);
    }

    public void tankDrive(double lSpeed, double rSpeed) {
        robotMap.getDriveTrain().tankDrive(lSpeed, rSpeed);
    }
}
