/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.commands.AutomaticIntakeCommand;
import frc.robot.commands.ShootCommand;

/**
 * This class is where the bulk of the robot should be declared. Very little robot logic
 * should go in this class, rather, the structure of the subsystems and commands and joystick
 * mapping should happen here.
 */
public class RobotContainer {
   // The robot's subsystems and commands are defined here
   private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
   private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();

   XboxController m_driverController = new XboxController(Ports.kDriverControllerPort);
   XboxController m_gunnerController = new XboxController(Ports.kGunnerControllerPort);

   // TODO: Add missing components such as Auto, Vision and Drive

   public RobotContainer() {
      // Configure the button bindings
      configureButtonBindings();
   }

   /**
    * Binds commands to joystick buttons
    */
   private void configureButtonBindings() {
      // Manual Shoot
      new JoystickButton(m_gunnerController, Button.kY.value)
            .whileHeld(new SequentialCommandGroup(new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(0.0)),
                  new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));
      // Dist Shoot
      new JoystickButton(m_gunnerController, Button.kA.value).whileHeld(new ParallelCommandGroup(
            new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* TODO Distance input */0)),
            new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

      // Vision Shoot
      new JoystickButton(m_gunnerController, Button.kB.value).whenHeld(new ParallelCommandGroup(
            /* new VisionTargetingCommand(m_visionSubsystem), */
            new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* S */0)),
            new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

      // Reset Balls
      new JoystickButton(m_gunnerController, Button.kStart.value)
            .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setBallsStored(0)));

      // Magazine Reverse
      new JoystickButton(m_gunnerController, Button.kBack.value)
            .whenPressed(new InstantCommand(() -> m_intakeSubsystem.setMagazineReverse()))
            .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setMagazineOff()));

      // Intake Reverse
      new JoystickButton(m_gunnerController, Button.kX.value)
            .whenPressed(new SequentialCommandGroup(new InstantCommand(() -> m_intakeSubsystem.setMagazineReverse()),
                  new InstantCommand(() -> m_intakeSubsystem.setIntakeReverse())))
            .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setMagazineOff()));

      // Intake
      new JoystickButton(m_gunnerController, Button.kA.value)
            .toggleWhenActive(new AutomaticIntakeCommand(m_intakeSubsystem));
   }
}
