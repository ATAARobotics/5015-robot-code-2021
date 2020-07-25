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

/**
 * Ball intake code
 *
 * @author Alexander Greco
 */


/**
 * The intake class controls the ball intake and storage.
 * It will automatically intake, but not too many balls.
 */
public class IntakeSubsystem extends SubsystemBase {

    private double magazineSpeed = -0.70;
    private double intakeSpeed = 1.0;
    private VictorSPX magazineMotor = new VictorSPX(6);
    private VictorSPX intakeMotor = new VictorSPX(5);
    private Lasershark intakeDetector = new Lasershark(5);
    private DoubleSolenoid intakeControl = new DoubleSolenoid(6, 7);

    private double ballsStored = 3;


    public IntakeSubsystem() {
        SmartDashboard.putNumber("Balls Stored", ballsStored);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Balls Stored", ballsStored);
    }


    public void setMagazine(boolean running) {
        setMagazine(running, magazineSpeed);
    }

    public void setMagazine(boolean running, double speed) {
        if (running) {
            magazineMotor.set(ControlMode.PercentOutput, speed);
        } else {
            magazineMotor.set(ControlMode.PercentOutput, 0.0);
        }
    }

    public void reverseMagazine(boolean reverse){
        if(reverse) {
            magazineSpeed = Math.abs(magazineSpeed);
        }
        else {
            magazineSpeed = -Math.abs(magazineSpeed);
        }
    }

    public void reverseMagazine(){
        magazineSpeed = -magazineSpeed;
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

        if (intakeDetector.getDistanceInches() < 5.0 && intakeDetector.getDistanceInches() != 0.0) {

            return true;

        } else if (intakeDetector.getDistanceInches() == 0.0) {

            DriverStation.reportError("Lasershark Disconnected", false);
            return false;

        } else {
            return false;
        }
    }
   
    //Allow code to control intake motor and solenoid
    public void setIntake(boolean running) {
        if(running) {
            intakeMotor.set(ControlMode.PercentOutput, intakeSpeed);
            intakeControl.set(Value.kForward);
        } else {
            intakeMotor.set(ControlMode.PercentOutput, 0.0);
            intakeControl.set(Value.kReverse);
        }
    }

    public void setIntakeSpeed(double speed){
        intakeSpeed = speed;
    }
    
    public void reverseIntake(){
        setIntakeSpeed(-intakeSpeed);
    }


	public boolean getMagazineFree() {
		return (getBallsStored() < 5);
	}


	public boolean ballDetected() {
		return (getIntakeDectector() && getBallsStored() != 5);
	}


	public boolean getLastBall() {
		return(getBallsStored() < 4);
	}


	public void addBall() {
        ballsStored++;
        if (ballsStored > 5) {
            ballsStored = 5;

        }
	}
    
}