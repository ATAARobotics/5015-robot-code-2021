//TODO: Fix this code

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.CANCoder;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Ball shooter code
 *
 * @author Jacob Guglielmin, Ben Heard, Alexander Greco
 */

enum IntakeCase {
    WAITING, STARTING, RUNNING, REVERSE, MAGREVERSE, ALLREVERSE, OFF
}

enum ShootCase {
    INITIAL, WARMUP, RUNNING, BALL_SHOOTING
}

/**
 * The shooter class controls the ball intake, storage, and shooter. It will
 * automatically intake, but not too many balls.
 */
public class Shooter {

    // private final double beltCircumference = 0.0 * Math.PI;
    // private final double magazineTicksPerBall = 0.0 / beltCircumference * 7.5;

    private final double magazineSpeed = -0.70;
    private double intakeSpeed = 1.0;
    private double shooterSpeed = 0.7;
    private double manualShooterSpeed = 0.7;
    private boolean shooterActive = false;
    private WPI_TalonSRX shooterMotorMaster = null;
    private WPI_TalonSRX shooterMotorFollower = null;
    private SpeedControllerGroup shooterMotors = null;
    private CANCoder shooterEncoder = null;
    private PIDController shooterController = null;
    private WPI_VictorSPX magazineMotor = null;
    private WPI_VictorSPX intakeMotor = null;
    private RangeFinder intakeDetector = null;
    private RangeFinder shootDetector = null;
    private DoubleSolenoid intakeControl = null;
    private DoubleSolenoid hoodControl = null;

    private Timer magazineTimer = new Timer();

    private boolean hoodForward = false;
    private double ballsStored = 3;
    private IntakeCase intakeCase = IntakeCase.WAITING;
    private ShootCase shootCase = ShootCase.INITIAL;
    private double setPoint = 0;
    private double rpm = 0;
    public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;

    private boolean safetyOverride = false;
    private double minOutput = kMinOutput;
    private double maxOutput = kMaxOutput;

    /**
     * Constructs a shooter object with the motors for the shooter and the
     * intake/conveyor, as well as the lasershark for the intake.
     *
     * @param shooterMotor  The motor that shoots balls
     * @param magazineMotor The magazine motor
     * @param intakeMotor   The intake motor
     */
    public Shooter(WPI_TalonSRX shooterMotorMaster, WPI_TalonSRX shooterMotorFollower, SpeedControllerGroup shooterMotor, WPI_VictorSPX magazineMotor, WPI_VictorSPX intakeMotor,
            DoubleSolenoid intakeControl, DoubleSolenoid hoodControl, CANCoder shooterEncoder,
            RangeFinder intakeDetector, RangeFinder shootDetector) {

        this.shooterMotors = shooterMotor;
        this.magazineMotor = magazineMotor;
        this.intakeMotor = intakeMotor;
        this.intakeDetector = intakeDetector;
        this.shootDetector = shootDetector;
        this.intakeControl = intakeControl;
        this.hoodControl = hoodControl;
        this.shooterEncoder = shooterEncoder;
        shooterController = new PIDController(kP, kI, kD);
        this.shooterMotorMaster = shooterMotorMaster;
        this.shooterMotorFollower = shooterMotorMaster;
    }

    /**
     * Sets the intake on or off, uses intakeSpeed for the power if on.
     * @param running Whether the intake wheels should be spinning
     */

     ////START: PID
    public void ShooterInit() {
        // set PID coefficients
        kP = 0.002;
        kI = 0.0000008;
        kD = 0.0;
        kIz = 0;

        //Max rpm
        kFF = 0.00015;

        kMaxOutput = 1;
        kMinOutput = 0;
        maxRPM = 10000;
        shooterController.setP(kP);
        shooterController.setI(kI);
        shooterController.setD(kD);
        /*shooterController.setIZone(kIz);
        shooterController.setFF(kFF);
        shooterController.setOutputRange(kMinOutput, kMaxOutput);*/

        shooterMotorMaster.enableCurrentLimit(true);
        //Reset all settings - Optional
        shooterMotorMaster.configFactoryDefault();

        //Current Settings - Optional: Are persistent and can be done from Tuner
        shooterMotorMaster.configContinuousCurrentLimit(35);
        shooterMotorMaster.configPeakCurrentDuration(0);
        shooterMotorMaster.configPeakCurrentLimit(35);

        //Reset all settings - Optional
        shooterMotorFollower.configFactoryDefault();

        //Current Settings - Optional: Are persistent and can be done from Tuner
        shooterMotorFollower.configContinuousCurrentLimit(35);
        shooterMotorFollower.configPeakCurrentDuration(0);
        shooterMotorFollower.configPeakCurrentLimit(35);

        //enable limit - Mandatory: must be done from API during RobotInit
        shooterMotorMaster.enableCurrentLimit(true);
        shooterMotorFollower.enableCurrentLimit(true);

        // display PID coefficients on SmartDashboard
        SmartDashboard.putNumber("Shooting I Gain", kI);
        SmartDashboard.putNumber("Shooting D Gain", kD);
        SmartDashboard.putNumber("Shooting P Gain", kP);
        SmartDashboard.putNumber("Shooting Feed Forward", kFF);
        SmartDashboard.putNumber("Shooting Manual Shooter Speed", manualShooterSpeed);
    }

    public double PIDPeriodic() {
        // read PID coefficients from SmartDashboard
        double p = SmartDashboard.getNumber("Shooting P Gain", 0);
        double i = SmartDashboard.getNumber("Shooting I Gain", 0);
        double d = SmartDashboard.getNumber("Shooting D Gain", 0);
        //double ff = SmartDashboard.getNumber("Shooting Feed Forward", 0);
        manualShooterSpeed = SmartDashboard.getNumber("Shooting Manual Shooter Speed", 0.85);
        // if PID coefficients on SmartDashboard have changed, write new values to
        // controller
        if ((i != kI)) {
            shooterController.setI(i);
            kI = i;
        }
        if ((d != kD)) {
            shooterController.setD(d);
            kD = d;
        }
        if ((p != kP)) {
            shooterController.setP(p);
            kP = p;
        }
        /*if ((ff != kFF)) {
            shooterController.setFF(ff);
            kFF = ff;
        }*/
        rpm = -(shooterEncoder.getVelocity())/360;
        SmartDashboard.putNumber("SetPoint", setPoint);
        SmartDashboard.putNumber("ProcessVariable", rpm);
        double speed = shooterController.calculate(rpm, setPoint);
        SmartDashboard.putNumber("Shooter Speed", speed);
        if(speed > kMaxOutput) {
            speed = maxOutput;
        }

        else if(speed < kMinOutput) {
            speed = minOutput;
        }

        return speed;

    }
    ////END: PID

    private void setMagazine(boolean running) {
        setMagazine(running, magazineSpeed);
    }

    private void setMagazine(boolean running, double speed) {
        if (running) {
            magazineMotor.set(ControlMode.PercentOutput, speed);
        } else {
            magazineMotor.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void reverseMagazine(boolean reverse){
        if(intakeCase == IntakeCase.WAITING && reverse){
            intakeCase = IntakeCase.MAGREVERSE;
        } else if (intakeCase == IntakeCase.REVERSE && reverse) {
            intakeCase = IntakeCase.ALLREVERSE;
        } else if ((intakeCase == IntakeCase.MAGREVERSE || intakeCase == IntakeCase.ALLREVERSE) && !reverse) {
            intakeCase = IntakeCase.WAITING;
        }
        //System.out.println("INTAKE CASE: " + intakeCase);
    }

    /**
     * Sets the shooter on or off, uses shootSpeed for the power if on.
     *
     * @param running Whether the magazine should be moving
     */

    ////START: Shooter
    public void shooterPeriodic() {
        SmartDashboard.putNumber("Balls Stored", ballsStored);
        SmartDashboard.putBoolean("Override", safetyOverride);
        setShooter(shooterActive);
        shooterMotors.set(PIDPeriodic());

    }

    private void setShooter(boolean running) {
        if (running) {
            minOutput = kMinOutput;
            maxOutput = kMaxOutput;
            setPoint = shooterSpeed * maxRPM;
        } else {
            setPoint = 0 * maxRPM;
            minOutput = 0;
            maxOutput = 0;
        }
    }

    public void setShooterSpeed(double distance) {
        double speed = 0.0;
        //If distance is 0.0 (manual entry), sets speed to 0.85
        if(distance != 0.0){
            distance += 17;
            //Sets speed based on distance from wall
            if(distance < 52) {
                speed = -1.32 + 0.112*distance + -0.00144*distance*distance;
            } else {
                speed = 0.658 + -0.00244*distance + 0.0000161*distance*distance;
            }
        }else{

            speed = manualShooterSpeed;
        }

            shooterSpeed = speed;
    }

    public void shoot(boolean active) {
        if (active) {
            DriverStation.reportWarning(String.format("Shoot Case: %s", shootCase.toString()), false);
            //DriverStation.reportWarning("Shooter LZRSHRK Distance: " + shootDetector.getDistance(), false);
            switch (shootCase) {
                case INITIAL: // Shooter was not active last tick
                    shooterActive = true;
                    shootCase = ShootCase.WARMUP;
                    //DriverStation.reportWarning("Switching to cooldown", false);

                    break;
                case WARMUP: // Shooter speeding up
                    if (rpm >= (setPoint * 0.95)) {
                        shootCase = ShootCase.RUNNING;
                    }
                    break;

                case RUNNING: // Shooter running
                    setMagazine(true, -1.0);
                    setIntake(true);
                    if (shootDetector.getDistance() < 7.0) {
                        shootCase = ShootCase.BALL_SHOOTING;
                    }

                    break;
                case BALL_SHOOTING:
                    if(shootDetector.getDistance() > 7.0){
                        shootCase = ShootCase.WARMUP;
                        if (ballsStored >= 1) {
                            ballsStored--;
                        }
                        if (ballsStored < 0) {
                            ballsStored = 0;
                        }

                    }
                    break;
                default:
                    DriverStation.reportError(String.format("Invalid Shoot Case: %s", shootCase.toString()), false);
            }
        } else if (shootCase == ShootCase.WARMUP ||
            shootCase == ShootCase.RUNNING) { // User released the button
            setMagazine(false);
            shooterActive = false;

            shootCase = ShootCase.INITIAL;
        }
    }
    ////END: Shooter

    /**
     * Sets the amount of balls stored for a user-override.
     */
    public void setBallsStored(int ballsStored) {
        this.ballsStored = ballsStored;
    }

    public double getBallsStored() {
		return ballsStored;
    }

    public void setOverride(boolean newOverride) {
        safetyOverride = newOverride;
        if (intakeCase == IntakeCase.OFF && safetyOverride == false) {
            intakeCase = IntakeCase.WAITING;
        }
    }

    /**
     * Main update loop for intaking balls automatically.
     */
    private boolean getIntakeDectector() {

        if (intakeDetector.getDistance() < 5.0 && intakeDetector.getDistance() != 0.0) {

            return true;

        } else if(intakeDetector.getDistance() == 0.0) {

            DriverStation.reportError("Lasershark Disconnected", false);
            return false;

        } else {
            return false;
        }
    }

    //Allow code to control intake motor and solenoid
    private void setIntake(boolean running) {
        if(running) {
            intakeMotor.set(ControlMode.PercentOutput, intakeSpeed);
            intakeControl.set(Value.kForward);
        } else {
            intakeMotor.set(ControlMode.PercentOutput, 0.0);
            intakeControl.set(Value.kReverse);
        }
    }

    public void intake() {
        switch (intakeCase) {
            case WAITING:
                setIntakeSpeed(1.0);
                if (ballsStored < 5 || safetyOverride) {
                    setMagazine(false);
                    setIntake(true);
                } else {
                    setMagazine(false);
                    setIntake(false);
                    //disableIntake();
                }

                if (getIntakeDectector() && ballsStored != 5) {
                    intakeCase = IntakeCase.STARTING;
                }

                break;

            case STARTING:
                setIntakeSpeed(1.0);
                if (ballsStored < 4) {
                    if (getIntakeDectector()) {
                        setMagazine(true);
                    } else {
                        magazineTimer.reset();
                        magazineTimer.start();
                        intakeCase = IntakeCase.RUNNING;
                    }
                } else {
                    magazineTimer.reset();
                    magazineTimer.start();
                    intakeCase = IntakeCase.RUNNING;
                }

                break;

            case RUNNING:
                setIntakeSpeed(1.0);
                if(ballsStored < 4) {
                    if(magazineTimer.get() < 0.2) {
                        setMagazine(true);
                    } else {
                        ballsStored++;
                        intakeCase = IntakeCase.WAITING;
                    }
                } else {
                    if(magazineTimer.get() < 0.1) {
                        setMagazine(true);
                    } else {
                        ballsStored++;
                        intakeCase = IntakeCase.WAITING;
                    }
                }
                break;
            case REVERSE:
                setIntakeSpeed(-1.0);
                setIntake(true);
                setMagazine(false);
                break;
            case MAGREVERSE:
                setMagazine(true, 0.3);
                break;
            case ALLREVERSE:
                setIntakeSpeed(-1.0);
                setIntake(true);
                setMagazine(true, 0.3);
            case OFF:
                setIntake(false);
                break;
            default:
                DriverStation.reportError(String.format("Invalid Intake Case: %d", intakeCase), false);
        }
    }

    /**
     * Main update loop for the shooter, when not active, just shuts off the shooter.
     * @param active Whether the shooter should be shooting.
     */
    public void toggleIntake(){
        if(intakeCase != IntakeCase.OFF){
            intakeCase = IntakeCase.OFF;
        }else{
            intakeCase = IntakeCase.WAITING;
        }

    }

    //Shoot at different speeds based on distance from wall

    public void setIntakeSpeed(double speed){
        intakeSpeed = speed;
    }

    public void reverseIntake(){
        if(intakeCase != IntakeCase.OFF){
            if(intakeCase != IntakeCase.REVERSE && intakeCase != IntakeCase.ALLREVERSE){
                intakeCase = IntakeCase.REVERSE;
            }else{
                intakeCase = IntakeCase.WAITING;
            }
        }
        System.out.println("INTAKE CASE: " + intakeCase);
    }

    public void setHood(boolean forward){
        if(forward){
            hoodControl.set(Value.kForward);
            hoodForward = true;
        }else{
            hoodControl.set(Value.kReverse);
            hoodForward = false;
        }
    }
    public void toggleHood(){
        if(hoodForward){
            setHood(false);
        }else{
            setHood(true);
        }
    }

    public double getTemperature() {
        return (shooterMotorMaster.getTemperature() + shooterMotorFollower.getTemperature())/2;
    }

}
