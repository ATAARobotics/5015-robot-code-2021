/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

/**
 * An example command that uses an example subsystem.
 */
public class ShootCommand extends SequentialCommandGroup {
  @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
  private final ShooterSubsystem m_shooterSubsystem;
  private final IntakeSubsystem m_intakeSubsystem;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public ShootCommand(final ShooterSubsystem shooterSubsystem, final IntakeSubsystem intakeSubsystem) {
        m_shooterSubsystem = shooterSubsystem;
        m_intakeSubsystem = intakeSubsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(shooterSubsystem, intakeSubsystem);
        addCommands(
            new StartEndCommand(
                () -> m_shooterSubsystem.setShooter(true), 
                () -> m_intakeSubsystem.setMagazine(true, -1.0), 
                m_shooterSubsystem, m_intakeSubsystem)
                .withInterrupt(() -> m_shooterSubsystem.getVelocitySetpoint()),
            new InstantCommand(() -> m_intakeSubsystem.setIntake(true), m_shooterSubsystem, m_intakeSubsystem),
            new WaitUntilCommand(() -> m_shooterSubsystem.ballDetected()),
            new WaitUntilCommand(() -> !m_shooterSubsystem.ballDetected()),
            new InstantCommand(() -> m_intakeSubsystem.ballShot())
        );           
    }

    @Override
    public void end(boolean interrupted) {
        if(interrupted) {
            new InstantCommand(() -> m_shooterSubsystem.setShooter(false));
            new InstantCommand(() -> m_intakeSubsystem.setMagazine(false));

        }
    }

}