package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.Constants.DrivingConstants;
import frc.robot.lib.Encoders;

/**
 * Driving Subsystem
 *
 * @author Ben Heard and Cole Dewis
 */
public class DrivingSubsystem extends SubsystemBase {
  private final WPI_VictorSPX m_frontLeftMotor = new WPI_VictorSPX(DrivingConstants.kFrontLeftMotorPort);
  private final WPI_VictorSPX m_rearLeftMotor = new WPI_VictorSPX(DrivingConstants.kRearLeftMotorPort);
  private final WPI_VictorSPX m_frontRightMotor = new WPI_VictorSPX(DrivingConstants.kFrontRightMotorPort);
  private final WPI_VictorSPX m_rearRightMotor = new WPI_VictorSPX(DrivingConstants.kRearRightMotorPort);
  private final SpeedControllerGroup m_rightMotors = new SpeedControllerGroup(m_rearRightMotor, m_frontRightMotor);
  private final SpeedControllerGroup m_leftMotors = new SpeedControllerGroup(m_rearLeftMotor, m_frontLeftMotor);
  private final DifferentialDrive m_driveTrain = new DifferentialDrive(m_leftMotors, m_rightMotors);
  private final Encoders m_driveEncoders = new Encoders(m_rearLeftMotor, m_rearRightMotor);

  /**
   * Creates a new DrivingSubsystem.
   */
  public DrivingSubsystem() {}

  public void drive(double speed, double rotation) {
    m_driveTrain.arcadeDrive(speed, rotation);
  }

  public double getLeftEncoderDistance() {
    return m_driveEncoders.getLeftDistance();
  }

  public double getRightEncoderDistance() {
    return m_driveEncoders.getRightDistance();
  }

  public void resetEncoders() {
    m_driveEncoders.reset();
  }

  public boolean encodersPID(double target) {
    return m_driveEncoders.PID(target);
  }

  public double getDrivetrainTemperature() {
    return (m_rearRightMotor.getTemperature() + m_rearLeftMotor.getTemperature() + m_frontRightMotor.getTemperature() + m_frontLeftMotor.getTemperature()) / 4;
  }
}
