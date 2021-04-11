package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.vision.CameraMode;
import frc.robot.vision.LimeLight;

public class Teleop {
    // Variables for robot classes
    private SWATDrive driveTrain = null;
    private Encoders encoders = null;
    private OI joysticks = null;
    private LimeLight limeLight = null;
    private Shooter shooter = null;
    private Climber climber = null;
    private Gyro gyro = null;
    private RangeFinder intakeDetector = null;
    private Align alignment = null;

    // Vision Control Variables
    private boolean discoOn = false;
    private int onTargetCounter = 0;

    // Vision PID and PID values
    private boolean visionActive = false;
    private boolean doVision = false;
    private Timer shootTimer = new Timer();

    NetworkTableEntry driveTemp;
    NetworkTableEntry shootTemp;
    private boolean distShootActive = false;

    public Teleop(RobotMap robotMap) {
        // Initialize Classes
        joysticks = new OI();
        this.driveTrain = robotMap.swatDrive;
        this.encoders = robotMap.getDriveEncoders();
        this.limeLight = robotMap.limeLight;
        this.shooter = robotMap.shooter;
        //this.colorSensor = robotMap.colorSensor;
        this.gyro = robotMap.getGyro();
        this.intakeDetector = robotMap.intakeDetector;
        this.climber = robotMap.climber;
        this.alignment = robotMap.align;
    }

    public void teleopInit() {

        // String colorGuess = colorSensor.findColor();
        // SmartDashboard.putString("Color", colorGuess);
        encoders.reset();

        // Disable Vision Processing on Limelight
        limeLight.setCameraMode(CameraMode.Drive);

    }

    public void TeleopPeriodic() {
        climber.moveClimber();
        SmartDashboard.putNumber("Distance To Wall", alignment.getDistance());
        SmartDashboard.putNumber("Angle To Target", limeLight.getTy());
        SmartDashboard.putString("Limelight Mode", limeLight.getCamMode());
        SmartDashboard.putNumber("Gyro Value", gyro.getAngle());

        joysticks.checkInputs();

        if (!climber.getClimbing()) {

            shooter.shooterPeriodic();

            // When vision button is pressed, toggle vision and CameraMode
            if (joysticks.getVisionShoot()) {
                visionActive = !visionActive;
                if (visionActive) {
                    onTargetCounter = 0;
                    limeLight.setCameraMode(CameraMode.Vision);
                } else {
                    limeLight.setCameraMode(CameraMode.Drive);
                }
            }
            else if(!visionActive){
                distShootActive = joysticks.getDistShoot();
                if (distShootActive) {
                    limeLight.setCameraMode(CameraMode.Vision);
                } else {
                    limeLight.setCameraMode(CameraMode.Drive);
                }
            }


            SmartDashboard.putNumber("EncoderLeft", encoders.getLeft());
            SmartDashboard.putNumber("EncoderRight", encoders.getRight());
            SmartDashboard.putNumber("EncoderLeftDistance", encoders.getLeftDistance());
            SmartDashboard.putNumber("EncoderRightDistance", encoders.getRightDistance());

            // Vision Alignment
            if(visionActive) {
                //shooter.shoot(false);
                //shooter.setShooterSpeed(alignment.getDistance());
                // Disable Vision if Aligned
                    if(alignment.atSetpoint()){
                        DriverStation.reportWarning("On target", false);
                        onTargetCounter++;
                        // Once has been on target for 10 counts: Disable PID, Reset Camera Settings
                        if (onTargetCounter > 10) {
                            //Pass target distance to shooter
                            /* if (shooter.getBallsStored() > 0) {
                                shooter.shoot(true);
                            } else {
                                visionActive = false;
                                limeLight.setCameraMode(CameraMode.Drive);
                            } */

                        }
                    } else {
                        DriverStation.reportWarning("Not on target", false);
                        //Rotate using values from the limelight
                        driveTrain.arcadeDrive(0.0, alignment.visionAlign());

                    }
            // If Vision is disabled normal driving and control operations. (AKA Mainly not vision code)
            }else{
                shooter.intake();

                //Set shoot speed based on button
                boolean toShoot = false;
                
                if (joysticks.getManualShoot(0)){
                    shootTimer.reset();
                    onTargetCounter = 0;
                    shooter.setShooterSpeed(0);
                    toShoot = true;
                } else if (joysticks.getManualShoot(1)){
                    shootTimer.reset();
                    onTargetCounter = 0;
                    shooter.setShooterSpeed(1);
                    toShoot = true;
                } else if (joysticks.getManualShoot(2)){
                    shootTimer.reset();
                    onTargetCounter = 0;
                    shooter.setShooterSpeed(2);
                    toShoot = true;
                } else if (joysticks.getManualShoot(3)){
                    shootTimer.reset();
                    onTargetCounter = 0;
                    shooter.setShooterSpeed(3);
                    toShoot = true;
                }
                if(toShoot) {
                    doVision = true;
                }
                if(doVision) {
                    limeLight.setCameraMode(CameraMode.Vision);
                    if(alignment.atSetpoint()){
                        DriverStation.reportWarning("On target", false);
                        onTargetCounter++;
                        // Once has been on target for 10 counts: Disable PID, Reset Camera Settings
                        if (onTargetCounter > 10) {
                            //Pass target distance to shooter
                            limeLight.setCameraMode(CameraMode.Drive);
                            shootTimer.start();
                            System.out.println(shootTimer.get());
                            if(shootTimer.get() < 3.0) {
                                shooter.shoot(true);
                            }else{
                                shootTimer.stop();
                                shooter.shoot(false);
                                doVision = false;
                            }
                            
                        }
                    } else {
                        DriverStation.reportWarning("Not on target", false);
                        //Rotate using values from the limelight
                        driveTrain.arcadeDrive(0.0, alignment.visionAlign());
                    }
                }
                //shooter.shoot(toShoot);

                //This is where the robot is driven (disabled during vision)
                if(!toShoot && !doVision) {
				    driveTrain.arcadeDrive(joysticks.getXSpeed(), joysticks.getZRotation());
                }
                if(joysticks.getDiscoButton()){
                    discoOn = !discoOn;
                    if(discoOn){
                        limeLight.setCameraMode(CameraMode.Disco);
                    }else{
                        limeLight.setCameraMode(CameraMode.Drive);
                    }
                }
                if(joysticks.getIntakeToggle()){
                    shooter.toggleIntake();
                }
                if(joysticks.getIntakeReverse()){
                    shooter.reverseIntake();
                }
                if(joysticks.getHoodToggle()){
                    shooter.toggleHood();
                }
                shooter.reverseMagazine(joysticks.getMagazineReverse());
                if(joysticks.getGearShift()) {
                    driveTrain.gearShift();
                }
                /* if(joysticks.getBallReset()){
                    shooter.setBallsStored(0);
                } */

                if (joysticks.getSlow()) {
                    driveTrain.slow();
                }
            }
        } else {
            shooter.setIntakeSpeed(0);
        }

        SmartDashboard.putNumber("Lasershark Intake Distance", intakeDetector.getDistance());

        climber.manualClimb(joysticks.getManualClimb());

        if (joysticks.getClimbButton()) {
            climber.toggleClimb();
        }
    }

	public void drive(double speedA, double speedB, boolean arcade) {

        if(arcade) {
            driveTrain.arcadeDrive(speedA, speedB);
        }
        else {
            driveTrain.tankDrive(speedA, speedB);
        }
	}

	public void TestPeriodic() {
        joysticks.checkInputs();

        climber.release(joysticks.getClimbRelease());
    }
    public void setDriveScheme(String driveScheme){
        joysticks.setDriveScheme(driveScheme);
    }
    public void setGunnerScheme(String gunnerScheme){
        joysticks.setGunnerScheme(gunnerScheme);
    }

}
