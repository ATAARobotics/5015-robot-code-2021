package frc.robot.lib;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
 * Encoder code
 *
 * @author Jacob Guglielmin
 */

public class Encoders {
   private static final class EncoderConstants {
      public static final double kPIDProportional = 0.04;
      public static final double kPIDIntegral = 0.00002;
      public static final double kPIDDerivative = 0.0;
      public static final double leftTicksPerInch = 263839.0/174.0;
      public static final double rightTicksPerInch = 300952.0/169.0;
   }

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
      leftMotor.config_kP(0, EncoderConstants.kPIDProportional);
      leftMotor.config_kI(0, EncoderConstants.kPIDIntegral);
      leftMotor.config_kD(0, EncoderConstants.kPIDDerivative);

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
      rightMotor.config_kP(0, EncoderConstants.kPIDProportional);
      rightMotor.config_kI(0, EncoderConstants.kPIDIntegral);
      rightMotor.config_kD(0, EncoderConstants.kPIDDerivative);

   }
   public double getLeftDistance() {
      return (leftMotor.getSelectedSensorPosition() * -1) / EncoderConstants.leftTicksPerInch;
   }

   public double getRightDistance() {
      return rightMotor.getSelectedSensorPosition() / EncoderConstants.rightTicksPerInch;
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
      leftMotor.set(ControlMode.Position, -target*EncoderConstants.leftTicksPerInch);
      rightMotor.set(ControlMode.Position, target*EncoderConstants.rightTicksPerInch);
      System.out.println("In PID: " + target);
      System.out.println("le: " + leftMotor.getClosedLoopError());
      System.out.println("re: " + rightMotor.getClosedLoopError());
      System.out.println("lt: " + leftMotor.getClosedLoopTarget());
      System.out.println("rt: " + rightMotor.getClosedLoopTarget());
      return (leftMotor.getClosedLoopError() < EncoderConstants.leftTicksPerInch
            && rightMotor.getClosedLoopError() < EncoderConstants.rightTicksPerInch
            && leftMotor.getClosedLoopError() != 0
            && rightMotor.getClosedLoopError() != 0);
   }
}
