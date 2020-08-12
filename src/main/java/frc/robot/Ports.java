package frc.robot;

/**
 * This class provides a place to put motor port numbers, so that rewiring the robot
 * is easier in terms of programming. This class should not be used for anything else.
 */
public final class Ports {
   //Driving Ports
   public static final int kFrontLeftMotorPort = 2;
   public static final int kRearLeftMotorPort = 1;
   public static final int kFrontRightMotorPort = 4;
   public static final int kRearRightMotorPort = 3;

   //Shooter Ports
   public static final int kShooterLaserSharkPort = 6;
   public static final int kShooterMotorPort = 7;

   //Intake Ports
   public static final int kMagazineMotorPort = 6;
   public static final int kintakeMotorPort = 5;
   public static final int kIntakeLaserSharkPort = 5;
   public static final int[] kIntakeSolenoidPorts = new int[] { 6, 7 };

   //OI Ports
   public static final int kDriverControllerPort = 0;
   public static final int kGunnerControllerPort = 1;
}
