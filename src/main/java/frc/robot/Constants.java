/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static final class ShooterConstants {
        // Ports
        public static final int kLaserSharkPort = 6;
        public static final int kShooterMotorPort = 7;
        // Defaults
        public static final double kDefaultShooterSpeed = 0.82;
        public static final int kDefaultBallsStored = 3;
        // Constants
        public static final double kBallDetectionDistance = 7.0;
        // PID Values
        public static final double kP = 0.0007;
        public static final double kI = 0.0000002;
        public static final double kD = 0.1;
        public static final double kIz = 0;
        public static final double kFF = 0.00015;
        public static final double kMaxOutput = 1;
        public static final double kMinOutput = 0;
        public static final double kMaxRPM = 5600;

    }

    public static final class IntakeConstants {
        // Ports
        public static final int kMagazineMotorPort = 6;
        public static final int kintakeMotorPort = 5;
        public static final int kLasersharkPort = 5;
        public static final int[] kSolenoidPorts = new int[]{6, 7};
        // Defaults
        public static final double kDefaultMagazineSpeed = -0.7;
        public static final double kDefaultIntakeSpeed = 1.0;
        public static final int kDefaultBallsStored = 3;
        // Constants
        public static final double kBallDetectionDistance = 7.0;
    }

    public static final class OIConstants {
        public static final int kDriverControllerPort = 0;
        public static final int kGunnerControllerPort = 1;
    }
}