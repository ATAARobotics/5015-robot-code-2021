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

   /**
    * Turn off the magazine
    */
   public void setMagazineOff() {
      magazineMotor.set(ControlMode.PercentOutput, 0.00);
   }

   /**
    * Turn on the magazine at the speed that the intake requires
    */
   public void setMagazineOnForIntake() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineIntakeSpeed);
   }

   /**
    * Turn on the magazine at the speed that the shooter requires
    */
   public void setMagazineOnForShooting() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineShootSpeed);
   }

   /**
    * Turn on the magazine in reverse
    */
   public void setMagazineReverse() {
      magazineMotor.set(ControlMode.PercentOutput, IntakeConstants.kMagazineReverseSpeed);
   }

   /**
    * Sets the amount of balls stored for a user-override.
    */
   public void setBallsStored(int ballsStored) {
      this.ballsStored = ballsStored;
   }

   /**
    * Returns the current amount of balls in the magazine
    */
   public double getBallsStored() {
      return ballsStored;
   }

   /**
    * Removes a ball from ballsStored
    */
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

   /**
    * Turns off the intake
    */
   public void setIntakeOff() {
      intakeMotor.set(ControlMode.PercentOutput, 0.0);
      intakeControl.set(IntakeConstants.kIntakeUp);
   }

   /**
    * Turns on the intake
    */
   public void setIntakeOn() {
      intakeMotor.set(ControlMode.PercentOutput, IntakeConstants.kIntakeForwardSpeed);
      intakeControl.set(IntakeConstants.kIntakeDown);
   }

   /**
    * Turns on the intake in reverse
    */
   public void setIntakeReverse() {
      intakeMotor.set(ControlMode.PercentOutput, IntakeConstants.kIntakeReverseSpeed);
      intakeControl.set(Value.kForward);
   }

   /**
    * Checks if there is space in the magazine
    */
   public boolean getMagazineFree() {
      return (getBallsStored() < IntakeConstants.kMaxBallsStored);
   }

   /**
    * Checks if a ball has been detected entering the magazine
    */
   public boolean ballDetected() {
      return (getIntakeDectector() && getBallsStored() != IntakeConstants.kMaxBallsStored);
   }

   /**
    * Checks if the next ball that enters the magazine is the last one that there is space for
    */
   public boolean getLastBall() {
      return !(getBallsStored() < IntakeConstants.kMaxBallsStored-1);
   }

   /**
    * Adds a ball to ballsStored
    */
   public void addBall() {
      ballsStored++;
      if (ballsStored > IntakeConstants.kMaxBallsStored) {
         ballsStored = IntakeConstants.kMaxBallsStored;

      }
   }

}
