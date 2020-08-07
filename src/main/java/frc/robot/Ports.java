/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * This class provides a place to put motor port numbers, so that rewiring the robot
 * is easier in terms of programming. This class should not be used for anything else.
 */
public final class Ports {
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
