package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.robot.Constants.DrivingConstants;
import frc.robot.lib.Encoders;

/**
 * Driving Subsystem
 *
 * @author Ben Heard and Cole Dewis
 */
public class DrivingSubsystem extends SubsystemBase {
  private VictorSPX m_frontLeftMotor = new VictorSPX(DrivingConstants.kFrontLeftMotorPort);
  private VictorSRX m_rearLeftMotor = new VictorSRX(DrivingConstants.kRearLeftMotorPort);
  private VictorSPX m_frontRightMotor = new VictorSPX(DrivingConstants.kFrontRightMotorPort);
  private VictorSRX m_rearRightMotor = new VictorSRX(DrivingConstants.kRearRightMotorPort);
  private SpeedControllerGroup m_rightMotors = new SpeedControllerGroup(m_rearRightMotor, m_frontRightMotor);
  private SpeedControllerGroup m_leftMotors = new SpeedControllerGroup(m_rearLeftMotor, m_frontLeftMotor);
  private DifferentialDrive m_driveTrain = new DifferentialDrive(m_leftMotors, m_rightMotors);
  private Encoders m_driveEncoders = new Encoders(m_rearLeftMotor, m_rearRightMotor);

  /**
   * Creates a new DrivingSubsystem.
   */
  public DrivingSubsystem() {}

  public drive(double speed, double rotation) {
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
