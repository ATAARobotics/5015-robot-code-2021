package frc.robot.lib;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.robot.Constants.DrivingConstants;

/**
 * Encoder code
 *
 * @author Jacob Guglielmin
 */

public class Encoders {

   // Creates left and right encoder objects
   private VictorSPX leftMotor;
   private VictorSPX rightMotor;

   public Encoders(VictorSPX leftMotor, VictorSPX rightMotor) {
      this.leftMotor = leftMotor;
      this.rightMotor = rightMotor;

      leftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
      rightMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

      this.leftMotor.setSelectedSensorPosition(0);
      this.rightMotor.setSelectedSensorPosition(0);

      leftMotor.configNominalOutputForward(0);
      leftMotor.configNominalOutputReverse(0);
      leftMotor.configPeakOutputForward(1);
      leftMotor.configPeakOutputReverse(-1);

      /**
       * Config the allowable closed-loop error, Closed-Loop output will be
       * neutral within this range. See Table in Section 17.2.1 for native
       * units per rotation.
       */
      leftMotor.configAllowableClosedloopError(0, 0);

      /* Config Position Closed Loop gains in slot0, tsypically kF stays zero. */
      leftMotor.config_kP(0, DrivingConstants.kPIDProportional);
      leftMotor.config_kI(0, DrivingConstants.kPIDIntegral);
      leftMotor.config_kD(0, DrivingConstants.kPIDDerivative);

                  /* Config the peak and nominal outputs, 12V means full */
      rightMotor.configNominalOutputForward(0);
      rightMotor.configNominalOutputReverse(0);
      rightMotor.configPeakOutputForward(1);
      rightMotor.configPeakOutputReverse(-1);

      /**
       * Config the allowable closed-loop error, Closed-Loop output will be
       * neutral within this range. See Table in Section 17.2.1 for native
       * units per rotation.
       */
      rightMotor.configAllowableClosedloopError(0, 0);

      /* Config Position Closed Loop gains in slot0, tsypically kF stays zero. */
      rightMotor.config_kP(0, DrivingConstants.kPIDProportional);
      rightMotor.config_kI(0, DrivingConstants.kPIDIntegral);
      rightMotor.config_kD(0, DrivingConstants.kPIDDerivative);

   }
   public double getLeftDistance() {
      return (leftMotor.getSelectedSensorPosition() * -1) / DrivingConstants.leftTicksPerInch;
   }

   public double getRightDistance() {
      return rightMotor.getSelectedSensorPosition() / DrivingConstants.rightTicksPerInch;
   }

   public void reset() {
      leftMotor.setSelectedSensorPosition(0);
      rightMotor.setSelectedSensorPosition(0);
      System.out.println("Did reset");
      System.out.println("le: " + leftMotor.getClosedLoopError());
      System.out.println("re: " + rightMotor.getClosedLoopError());
      System.out.println("lt: " + leftMotor.getClosedLoopTarget());
      System.out.println("rt: " + rightMotor.getClosedLoopTarget());
   }

   //Send ticks required to go a specified distance(For use in PIDs)
   //Returns if it completes within 100 ticks
   public boolean PID(double target) {
      leftMotor.set(ControlMode.Position, -target*DrivingConstants.leftTicksPerInch);
      rightMotor.set(ControlMode.Position, target*DrivingConstants.rightTicksPerInch);
      System.out.println("In PID: " + target);
      System.out.println("le: " + leftMotor.getClosedLoopError());
      System.out.println("re: " + rightMotor.getClosedLoopError());
      System.out.println("lt: " + leftMotor.getClosedLoopTarget());
      System.out.println("rt: " + rightMotor.getClosedLoopTarget());
      return (leftMotor.getClosedLoopError() < DrivingConstants.leftTicksPerInch
            && rightMotor.getClosedLoopError() < DrivingConstants.rightTicksPerInch
            && leftMotor.getClosedLoopError() != 0
            && rightMotor.getClosedLoopError() != 0);
   }
}
