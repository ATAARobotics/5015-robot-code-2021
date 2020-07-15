/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.ShootCommand;

import frc.robot.Constants.OIConstants;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
	// The robot's subsystems and commands are defined here...
	private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
	private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();

	private final AutoCommand m_autoCommand = new AutoCommand(m_driveSubsystem, m_visionSubsystem, m_shooterSubsystem, m_intakeSubsystem);
	
	XboxController m_driverController = new XboxController(OIConstants.kDriverControllerPort);
	XboxController m_gunnerController = new XboxController(OIConstants.kGunnerControllerPort);

	/**
	 * The container for the robot. Contains subsystems, OI devices, and commands.
	 */
  //TODO: Add missing components such as Auto, Vision and Drive

	public RobotContainer() {
		// Configure the button bindings
		configureButtonBindings();
	}

	/**
	 * Use this method to define your button->command mappings. Buttons can be
	 * created by instantiating a {@link GenericHID} or one of its subclasses
	 * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
	 * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
	 */
	private void configureButtonBindings() {
		// Manual Shoot
		new JoystickButton(m_gunnerController, Button.kY.value)
				.whileHeld(new SequentialCommandGroup(new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(0.0)),
						new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));
		// Dist Shoot
		new JoystickButton(m_gunnerController, Button.kA.value).whileHeld(new ParallelCommandGroup(
				new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* distanceInput */)),
				new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

		// Vision Shoot
		new JoystickButton(m_gunnerController, Button.kB.value)
				.whenHeld(new ParallelCommandGroup(new VisionTargetingCommand(m_visionSubsystem),
						new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* distanceInput */)),
						new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

		// Reset Balls
		new JoystickButton(m_gunnerController, Button.kStart.value)
				.whenReleased(new InstantCommand(() -> m_intakeSubsystem.setBallsStored(0)));

		// Magazine Reverse
		new JoystickButton(m_gunnerController, Button.kBack.value)
				.whenPressed(
						new SequentialCommandGroup(new InstantCommand(() -> m_intakeSubsystem.reverseMagazine(true)),
								new InstantCommand(() -> m_intakeSubsystem.setMagazine(true))))
				.whenReleased(
						new SequentialCommandGroup(new InstantCommand(() -> m_intakeSubsystem.reverseMagazine(false)),
								new InstantCommand(() -> m_intakeSubsystem.setMagazine(false))));

		// Intake Reverse
		new JoystickButton(m_gunnerController, Button.kX.value)
				.whenPressed(
						new SequentialCommandGroup(new InstantCommand(() -> m_intakeSubsystem.reverseMagazine(true)),
								new InstantCommand(() -> m_intakeSubsystem.setMagazine(true))))
				.whenReleased(
						new SequentialCommandGroup(new InstantCommand(() -> m_intakeSubsystem.reverseMagazine(false)),
								new InstantCommand(() -> m_intakeSubsystem.setMagazine(false))));

		// Intake
		new JoystickButton(m_gunnerController, Button.kA.value).toggleWhenActive(new IntakeCommand(m_intakeSubsystem));
	}

	/**
	 * Use this to pass the autonomous command to the main {@link Robot} class.
	 *
	 * @return the command to run in autonomous
	 */
	public Command getAutonomousCommand() { // An ExampleCommand will run in autonomous
		return m_autoCommand;
	}

}