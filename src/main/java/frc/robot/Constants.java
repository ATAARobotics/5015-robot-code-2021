/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants.   This class should not be used for any other purpose.   All constants should be
 * declared globally (i.e. public static).   Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
   public static final class DrivingConstants {
      public static final int kFrontLeftMotorPort = 2;
      public static final int kRearLeftMotorPort = 1;
      public static final int kFrontRightMotorPort = 4;
      public static final int kRearRightMotorPort = 3;
      public static final double kPIDProportional = 0.04;
      public static final double kPIDIntegral = 0.00002;
      public static final double kPIDDerivative = 0.0;
      public static final double leftTicksPerInch = 263839.0/174.0;
      public static final double rightTicksPerInch = 300952.0/169.0;
   }
   /**
    * Constants for the operator interface (OI).
      */
   public static final class OIConstants {
      public static final int kDriverControllerPort = 0;
      public static final int kGunnerControllerPort = 1;
      public static final Hand YControl = Hand.kLeft;
      public static final Hand XControl = Hand.kRight;
   }
}
