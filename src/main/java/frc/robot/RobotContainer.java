package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj.GenericHID;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.subsystems.DrivingSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.commands.AutomaticIntakeCommand;
import frc.robot.commands.JoystickDriveCommand;
import frc.robot.commands.ShootCommand;

/**
 * This class is where the bulk of the robot should be declared. Very little robot logic
 * should go in this class, rather, the structure of the subsystems and commands and joystick
 * mapping should happen here.
 */
public class RobotContainer {
   private static final class OIConstants {
      public static final GenericHID.Hand YControl = GenericHID.Hand.kLeft;
      public static final GenericHID.Hand XControl = GenericHID.Hand.kRight;
      public static final int kManualShootButton = Button.kY.value;
      public static final int kDistShootButton = Button.kA.value;
      public static final int kVisionShootButton = Button.kB.value;
      public static final int kResetBallsButton = Button.kStart.value;
      public static final int kMagazineReverseButton = Button.kBack.value;
      public static final int kIntakeReverseButton = Button.kX.value;
      public static final int kIntakeButton = Button.kBumperRight.value;
   }

   // The robot's subsystems and commands are defined here
   private final DrivingSubsystem m_driveSystem = new DrivingSubsystem();
   private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
   private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();

   XboxController m_driverController = new XboxController(Ports.kDriverControllerPort);
   XboxController m_gunnerController = new XboxController(Ports.kGunnerControllerPort);

   // TODO: Add missing components - Auto and Vision

   public RobotContainer() {
      // Configure the button bindings
      configureButtonBindings();

      // Configure default commands
      // Set the default drive command to split-stick arcade drive
      m_driveSystem.setDefaultCommand(
         // A split-stick arcade command, with forward/backward controlled by the left
         // hand, and turning controlled by the right.
         new JoystickDriveCommand(
            m_driveSystem,
            () -> m_driverController.getY(OIConstants.YControl),
            () -> m_driverController.getX(OIConstants.XControl)));
   }

   /**
    * Binds commands to joystick buttons
    */
   private void configureButtonBindings() {
      // Manual Shoot
      new JoystickButton(m_gunnerController, OIConstants.kManualShootButton)
         .whileHeld(new SequentialCommandGroup(new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(0.0)),
            new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));
      // Dist Shoot
      new JoystickButton(m_gunnerController, OIConstants.kDistShootButton)
         .whileHeld(new ParallelCommandGroup(
            new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* TODO Distance input */0)),
            new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

      // Vision Shoot
      new JoystickButton(m_gunnerController, OIConstants.kVisionShootButton)
         .whenHeld(new ParallelCommandGroup(
            /* new VisionTargetingCommand(m_visionSubsystem), */
            new InstantCommand(() -> m_shooterSubsystem.setShooterSpeed(/* TODO Distance input */0)),
            new ShootCommand(m_shooterSubsystem, m_intakeSubsystem)));

      // Reset Balls
      new JoystickButton(m_gunnerController, OIConstants.kResetBallsButton)
         .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setBallsStored(0)));

      // Magazine Reverse
      new JoystickButton(m_gunnerController, OIConstants.kMagazineReverseButton)
         .whenPressed(new InstantCommand(() -> m_intakeSubsystem.setMagazineReverse()))
         .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setMagazineOff()));

      // Intake Reverse
      new JoystickButton(m_gunnerController, OIConstants.kIntakeReverseButton)
         .whenPressed(new SequentialCommandGroup(
            new InstantCommand(() -> m_intakeSubsystem.setMagazineReverse()),
            new InstantCommand(() -> m_intakeSubsystem.setIntakeReverse())))
         .whenReleased(new InstantCommand(() -> m_intakeSubsystem.setMagazineOff()));

      // Intake
      new JoystickButton(m_gunnerController, OIConstants.kIntakeButton)
         .toggleWhenActive(new AutomaticIntakeCommand(m_intakeSubsystem));
   }
}
