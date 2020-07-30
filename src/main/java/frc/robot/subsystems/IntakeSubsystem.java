/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.cuforge.libcu.Lasershark;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

/**
 * Ball intake code
 *
 * @author Alexander Greco
 */

/**
 * The intake class controls the ball intake and storage. It will automatically
 * intake, but not too many balls.
 */
public class IntakeSubsystem extends SubsystemBase {
   public static final class IntakeConstants {
      // Defaults
      public static final int kDefaultBallsStored = 3;
      // Constants
      public static final double kBallDetectionDistance = 5.0;
      private final static double kMagazineIntakeSpeed = -0.70;
      private final static double kMagazineShootSpeed = -1.00;
      private final static double kMagazineReverseSpeed = 0.70;

      private final static double kIntakeForwardSpeed = 1.0;
      private final static double kIntakeReverseSpeed = -1.0;
      private final static double kMaxBallsStored = 5.0;
      private final static Value kIntakeUp = Value.kReverse;
      private final static Value kIntakeDown = Value.kForward;
   }

   private VictorSPX magazineMotor = new VictorSPX(Ports.kMagazineMotorPort);
   private VictorSPX intakeMotor = new VictorSPX(Ports.kintakeMotorPort);
   private Lasershark intakeDetector = new Lasershark(Ports.kIntakeLaserSharkPort);
   private DoubleSolenoid intakeControl = new DoubleSolenoid(Ports.kIntakeSolenoidPorts[0],
         Ports.kIntakeSolenoidPorts[1]);

   private double ballsStored = IntakeConstants.kDefaultBallsStored;

   public IntakeSubsystem() {
      SmartDashboard.putNumber("Balls Stored", ballsStored);
   }

   @Override
   public void periodic() {
      SmartDashboard.putNumber("Balls Stored", ballsStored);
   }

   public void setMagazineOff() {
      magazineMotor.set(ControlMode.PercentOutput, 0.00);
   }

   public void setMagazineOnForIntake() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineIntakeSpeed);
   }

   public void setMagazineOnForShooting() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineShootSpeed);
   }

   public void setMagazineReverse() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineReverseSpeed);
   }

   /**
    * Sets the amount of balls stored for a user-override.
    */
   public void setBallsStored(int ballsStored) {
      this.ballsStored = ballsStored;
   }

   public double getBallsStored() {
      return ballsStored;
   }

   public void ballShot() {
      ballsStored--;
      if (ballsStored < 0) {
         ballsStored = 0;
      }
   }

   /**
    * Main update loop for intaking balls automatically.
    */
   public boolean getIntakeDectector() {

      if (intakeDetector.getDistanceInches() < IntakeConstants.kBallDetectionDistance && intakeDetector.getDistanceInches() != 0.0) {

         return true;

      } else if (intakeDetector.getDistanceInches() == 0.0) {

         DriverStation.reportError("Lasershark Disconnected", false);
         return false;

      } else {
         return false;
      }
   }

   public void setIntakeOff() {
      intakeMotor.set(ControlMode.PercentOutput, 0.0);
      intakeControl.set(IntakeConstants.kIntakeUp);
   }
   public void setIntakeOn() {
      intakeMotor.set(ControlMode.PercentOutput, IntakeConstants.kIntakeForwardSpeed);
      intakeControl.set(IntakeConstants.kIntakeDown);
   }

   public void setIntakeReverse() {
      intakeMotor.set(ControlMode.PercentOutput, IntakeConstants.kIntakeReverseSpeed);
      intakeControl.set(Value.kForward);
   }

   public boolean getMagazineFree() {
      return (getBallsStored() < IntakeConstants.kMaxBallsStored);
   }

   public boolean ballDetected() {
      return (getIntakeDectector() && getBallsStored() != IntakeConstants.kMaxBallsStored);
   }

   public boolean getLastBall() {
      return !(getBallsStored() < IntakeConstants.kMaxBallsStored-1);
   }

   public void addBall() {
      ballsStored++;
      if (ballsStored > IntakeConstants.kMaxBallsStored) {
         ballsStored = IntakeConstants.kMaxBallsStored;

      }
   }

}
