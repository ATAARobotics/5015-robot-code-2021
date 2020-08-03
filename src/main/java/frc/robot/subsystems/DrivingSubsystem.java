package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Ports;
import frc.robot.lib.FancyLog;

/**
 * Driving Subsystem
 *
 * @author Ben Heard, Cole Dewis and Jacob Guglielmin
 */
public class DrivingSubsystem extends SubsystemBase {
   private final WPI_VictorSPX m_frontLeftMotor = new WPI_VictorSPX(Ports.kFrontLeftMotorPort);
   private final WPI_VictorSPX m_rearLeftMotor = new WPI_VictorSPX(Ports.kRearLeftMotorPort);
   private final WPI_VictorSPX m_frontRightMotor = new WPI_VictorSPX(Ports.kFrontRightMotorPort);
   private final WPI_VictorSPX m_rearRightMotor = new WPI_VictorSPX(Ports.kRearRightMotorPort);
   private final SpeedControllerGroup m_rightMotors = new SpeedControllerGroup(m_rearRightMotor, m_frontRightMotor);
   private final SpeedControllerGroup m_leftMotors = new SpeedControllerGroup(m_rearLeftMotor, m_frontLeftMotor);
   private final DifferentialDrive m_driveTrain = new DifferentialDrive(m_leftMotors, m_rightMotors);

   private static final class EncoderConstants {
      public static final double kPIDProportional = 0.04;
      public static final double kPIDIntegral = 0.00002;
      public static final double kPIDDerivative = 0.0;
      public static final double leftTicksPerInch = 263839.0/174.0;
      public static final double rightTicksPerInch = 300952.0/169.0;
   }

   /**
    * Creates a new DrivingSubsystem.
    */
   public DrivingSubsystem() {
      m_rearLeftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
      m_rearRightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

      m_rearLeftMotor.setSelectedSensorPosition(0);
      m_rearRightMotor.setSelectedSensorPosition(0);

      m_rearLeftMotor.configNominalOutputForward(0);
      m_rearLeftMotor.configNominalOutputReverse(0);
      m_rearLeftMotor.configPeakOutputForward(1);
      m_rearLeftMotor.configPeakOutputReverse(-1);

      m_rearRightMotor.configNominalOutputForward(0);
      m_rearRightMotor.configNominalOutputReverse(0);
      m_rearRightMotor.configPeakOutputForward(1);
      m_rearRightMotor.configPeakOutputReverse(-1);

      m_rearLeftMotor.configAllowableClosedloopError(0, 0);
      m_rearRightMotor.configAllowableClosedloopError(0, 0);

      m_rearLeftMotor.config_kP(0, EncoderConstants.kPIDProportional);
      m_rearLeftMotor.config_kI(0, EncoderConstants.kPIDIntegral);
      m_rearLeftMotor.config_kD(0, EncoderConstants.kPIDDerivative);
      m_rearRightMotor.config_kP(0, EncoderConstants.kPIDProportional);
      m_rearRightMotor.config_kI(0, EncoderConstants.kPIDIntegral);
      m_rearRightMotor.config_kD(0, EncoderConstants.kPIDDerivative);
   }

   public void drive(double speed, double rotation) {
      m_driveTrain.arcadeDrive(speed, rotation);
   }

   public double getLeftEncoderDistance() {
      return (m_rearLeftMotor.getSelectedSensorPosition() * -1) / EncoderConstants.leftTicksPerInch;
   }

   public double getRightEncoderDistance() {
      return m_rearRightMotor.getSelectedSensorPosition() / EncoderConstants.rightTicksPerInch;
   }

   public void resetEncoders() {
      m_rearLeftMotor.setSelectedSensorPosition(0);
      m_rearRightMotor.setSelectedSensorPosition(0);
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "Reset drive encoders, values:");
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "  le: " + m_rearLeftMotor.getClosedLoopError());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "  re: " + m_rearRightMotor.getClosedLoopError());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "  lt: " + m_rearLeftMotor.getClosedLoopTarget());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "  rt: " + m_rearRightMotor.getClosedLoopTarget());
   }
   //Send ticks required to go a specified distance(For use in PIDs)
   //Returns if it completes within 100 ticks
   public boolean encodersPID(double target) {
      m_rearLeftMotor.set(ControlMode.Position, -target*EncoderConstants.leftTicksPerInch);
      m_rearRightMotor.set(ControlMode.Position, target*EncoderConstants.rightTicksPerInch);
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "In PID: " + target);
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "le: " + m_rearLeftMotor.getClosedLoopError());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "re: " + m_rearRightMotor.getClosedLoopError());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "lt: " + m_rearLeftMotor.getClosedLoopTarget());
      FancyLog.Log(FancyLog.LogLevel.LOG, FancyLog.LogSystem.SUBSYSTEM_DRIVE, "rt: " + m_rearRightMotor.getClosedLoopTarget());
      return (m_rearLeftMotor.getClosedLoopError() < EncoderConstants.leftTicksPerInch
            && m_rearRightMotor.getClosedLoopError() < EncoderConstants.rightTicksPerInch
            && m_rearLeftMotor.getClosedLoopError() != 0
            && m_rearRightMotor.getClosedLoopError() != 0);
   }

   public double getDrivetrainTemperature() {
      return (m_rearRightMotor.getTemperature() + m_rearLeftMotor.getTemperature() + m_frontRightMotor.getTemperature() + m_frontLeftMotor.getTemperature()) / 4;
   }
}
