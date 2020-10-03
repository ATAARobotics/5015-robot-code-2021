package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;
import com.cuforge.libcu.Lasershark;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

/**
 * Ball shooter code
 *
 * @author Alexander Greco
 */

/**
 * The shooter class controls the PID and distance calculations for the shooter.
 */
public class ShooterSubsystem extends SubsystemBase {

   public static final class ShooterConstants {
      // Defaults
      public static final double kDefaultShooterSpeed = 0.82;
      public static final int kDefaultBallsStored = 3;
      // Constants
      public static final double kBallDetectionDistance = 7.0;
      // PID Values
      public static final double kP = 0.007;
      public static final double kI = 0.0000000;
      public static final double kD = 0.0;
      public static final double kIz = 0;
      public static final double kFF = 0.00015;
      public static final double kMaxOutput = 1;
      public static final double kMinOutput = 0;
      public static final double kMaxRPM = 6000;

   }

   private double shooterSpeed = ShooterConstants.kDefaultShooterSpeed;
   private double manualShooterSpeed = ShooterConstants.kDefaultShooterSpeed;
   private WPI_TalonSRX shooterMotorMaster = new WPI_TalonSRX(Ports.kShooterMotorPorts[1]);
   private WPI_TalonSRX shooterMotorFollower = new WPI_TalonSRX(Ports.kShooterMotorPorts[0]);
   private PIDController shooterPID;
   private CANCoder shooterEncoder = new CANCoder(9);
   private Lasershark shootDetector = new Lasershark(Ports.kShooterLaserSharkPort);
   private double processVariable;
   private double setPoint = 0;
   public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;

   public ShooterSubsystem() {
      processVariable = shooterEncoder.getVelocity()/360;
      // set PID coefficients
      kP = ShooterConstants.kP;
      kI = ShooterConstants.kI;
      kD = ShooterConstants.kD;
      kIz = ShooterConstants.kIz;
      kFF = ShooterConstants.kFF;
      //shooterMotorFollower.setInverted(true);
      //shooterMotorFollower.follow(shooterMotorMaster);

      kMaxOutput = ShooterConstants.kMaxOutput;
      kMinOutput = ShooterConstants.kMinOutput;
      maxRPM = ShooterConstants.kMaxRPM;
      shooterPID = new PIDController(kP, kI, kD);
      /*
      shooterController.setIZone(kIz);
      shooterController.setFF(kFF);
      shooterController.setOutputRange(kMinOutput, kMaxOutput);*/

      // display PID coefficients on SmartDashboard
      SmartDashboard.putNumber("Shooting I Gain", kI);
      SmartDashboard.putNumber("Shooting D Gain", kD);
      SmartDashboard.putNumber("Shooting P Gain", kP);
      SmartDashboard.putNumber("Shooting Feed Forward", kFF);
      SmartDashboard.putNumber("Shooting Manual Shooter Speed", manualShooterSpeed);

   }

   @Override
   public void periodic() {
      processVariable = shooterEncoder.getVelocity()/360;
      shooterMotorMaster.set(shooterPID.calculate(processVariable));
      // read PID coefficients from SmartDashboard
      double p = SmartDashboard.getNumber("Shooting P Gain", ShooterConstants.kP);
      double i = SmartDashboard.getNumber("Shooting I Gain", ShooterConstants.kI);
      double d = SmartDashboard.getNumber("Shooting D Gain", ShooterConstants.kD);
      double ff = SmartDashboard.getNumber("Shooting Feed Forward", ShooterConstants.kFF);
      manualShooterSpeed = SmartDashboard.getNumber("Shooting Manual Shooter Speed", ShooterConstants.kDefaultShooterSpeed);
      // if PID coefficients on SmartDashboard have changed, write new values to
      if ((i != kI)) {
         shooterPID.setI(i);
         kI = i;
      }
      if ((d != kD)) {
         shooterPID.setD(d);
         kD = d;
      }
      if ((p != kP)) {
         shooterPID.setP(p);
         kP = p;
      }
      /*if ((ff != kFF)) {
         shooterController.setFF(ff);
         kFF = ff;
      }*/

      SmartDashboard.putNumber("SetPoint", setPoint);
      SmartDashboard.putNumber("ProcessVariable", processVariable);

   }

   /**
    * Turns the shooter on or off
    */
   public void setShooter(boolean running) {
      if (running) {
         setPoint = shooterSpeed * maxRPM;
      } else {
         setPoint = 0 * maxRPM;
      }
      shooterPID.setSetpoint(setPoint);
   }

   /**
    * Sets the shooter speed based on the distance from the target
    */
   public void setShooterSpeed(double distance) {
      double speed = 0.0;
      // If distance is 0.0 (manual entry), sets speed to 0.85
      if (distance != 0.0) {
         distance += 17;
         // Sets speed based on distance from wall
         if (distance < 52) {
            speed = -1.32 + 0.112 * distance + -0.00144 * distance * distance;
         } else {
            speed = 0.658 + -0.00244 * distance + 0.0000161 * distance * distance;
         }
      } else {

         speed = manualShooterSpeed;
      }

      shooterSpeed = speed;
   }

   /**
    * Returns the speed of the shooter motor
    */
   public double getVelocity() {
      return shooterEncoder.getVelocity()/360;
   }

   /**
    * Returns the setpoint for the shooter PID
    */
   public double getSetpoint() {
      return setPoint;
   }

   /**
    * Checks if the shooter motor speed is near the setpoint
    */
   public boolean nearSetpoint() {
      double errorPercent = Math.abs(getVelocity() - setPoint) / setPoint;
      return errorPercent < 0.01;
   }

   /**
    * Checks if a ball is exiting the shooter
    */
   public boolean ballDetected() {
      return shootDetector.getDistanceInches() < ShooterConstants.kBallDetectionDistance;
   }

}
