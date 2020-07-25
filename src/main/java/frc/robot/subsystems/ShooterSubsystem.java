package frc.robot.subsystems;

import com.cuforge.libcu.Lasershark;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

/**
 * Ball shooter code
 *
 * @author Alexander Greco
 */

/**
 * The shooter class controls the ball intake, storage, and shooter. It will
 * automatically intake, but not too many balls.
 */
public class ShooterSubsystem extends SubsystemBase {

   private double shooterSpeed = ShooterConstants.kDefaultShooterSpeed;
   private double manualShooterSpeed = ShooterConstants.kDefaultShooterSpeed;
   private CANSparkMax shooterMotor = new CANSparkMax(ShooterConstants.kShooterMotorPort, MotorType.kBrushless);
   private CANEncoder shooterEncoder = new CANEncoder(shooterMotor);
   private CANPIDController shooterController = shooterMotor.getPIDController();
   private Lasershark shootDetector = new Lasershark(ShooterConstants.kLaserSharkPort);

   private double setPoint = 0;
   public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;

   public ShooterSubsystem() {

      // set PID coefficients
      kP = ShooterConstants.kP;
      kI = ShooterConstants.kI;
      kD = ShooterConstants.kD;
      kIz = ShooterConstants.kIz;

      // Max rpm
      kFF = ShooterConstants.kFF;

      kMaxOutput = ShooterConstants.kMaxOutput;
      kMinOutput = ShooterConstants.kMinOutput;
      maxRPM = ShooterConstants.kMaxRPM;
      shooterController.setP(kP);
      shooterController.setI(kI);
      shooterController.setD(kD);
      shooterController.setIZone(kIz);
      shooterController.setFF(kFF);
      shooterController.setOutputRange(kMinOutput, kMaxOutput);

      // display PID coefficients on SmartDashboard
      SmartDashboard.putNumber("Shooting I Gain", kI);
      SmartDashboard.putNumber("Shooting D Gain", kD);
      SmartDashboard.putNumber("Shooting P Gain", kP);
      SmartDashboard.putNumber("Shooting Feed Forward", kFF);
      SmartDashboard.putNumber("Shooting Manual Shooter Speed", manualShooterSpeed);

   }

   @Override
   public void periodic() {

      // read PID coefficients from SmartDashboard
      double p = SmartDashboard.getNumber("Shooting P Gain", 0);
      double i = SmartDashboard.getNumber("Shooting I Gain", 0);
      double d = SmartDashboard.getNumber("Shooting D Gain", 0);
      double ff = SmartDashboard.getNumber("Shooting Feed Forward", 0);
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
      if ((ff != kFF)) {
         shooterController.setFF(ff);
         kFF = ff;
      }

      SmartDashboard.putNumber("SetPoint", setPoint);
      SmartDashboard.putNumber("ProcessVariable", shooterEncoder.getVelocity());
   }

   public void setShooter(boolean running) {
      if (running) {
         shooterController.setOutputRange(kMinOutput, kMaxOutput);
         setPoint = shooterSpeed * maxRPM;
      } else {
         setPoint = 0 * maxRPM;
         shooterController.setOutputRange(0, 0);
      }
      shooterController.setReference(setPoint, ControlType.kVelocity);
   }

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

   public double getTemperature() {
      return shooterMotor.getMotorTemperature();
   }

   public double getVelocity() {
      return shooterEncoder.getVelocity();
   }

   public double getSetpoint() {
      return setPoint;
   }

   public boolean atSetpoint() {
      return getVelocity() >= getSetpoint();
   }

   public boolean ballDetected() {
      return shootDetector.getDistanceInches() < ShooterConstants.kBallDetectionDistance;
   }

}