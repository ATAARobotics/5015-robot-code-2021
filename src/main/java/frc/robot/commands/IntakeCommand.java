/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.subsystems.IntakeSubsystem;

/**
 * An example command that uses an example subsystem.
 */
public class IntakeCommand extends SequentialCommandGroup {
    @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
    private final IntakeSubsystem m_intakeSubsystem;

    /**
     * Creates a new ExampleCommand.
     *
     * @param subsystem The subsystem used by this command.
     */
    public IntakeCommand(IntakeSubsystem intakeSubsystem) {
        m_intakeSubsystem = intakeSubsystem;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(intakeSubsystem);
        addCommands(
            new ConditionalCommand(
                new ParallelCommandGroup(
                    new InstantCommand( () -> m_intakeSubsystem.setMagazine(false)),
                    new InstantCommand( () -> m_intakeSubsystem.setIntake(true))
                ),
                new SequentialCommandGroup(
                    new ParallelCommandGroup(
                        new InstantCommand( () -> m_intakeSubsystem.setMagazine(false)),
                        new InstantCommand( () -> m_intakeSubsystem.setIntake(false))
                    ),
                    new InstantCommand( () -> this.cancel())
                ),
                () -> m_intakeSubsystem.getMagazineFree()
            ).beforeStarting(() -> m_intakeSubsystem.setIntakeSpeed(1.0)),
            new WaitUntilCommand(() -> m_intakeSubsystem.ballDetected()),
            new ConditionalCommand(
                new StartEndCommand(() -> m_intakeSubsystem.setMagazine(true),
                                    () -> m_intakeSubsystem.setMagazine(false)).withTimeout(0.1),
                
                new StartEndCommand(() -> m_intakeSubsystem.setMagazine(true),
                                    () -> m_intakeSubsystem.setMagazine(false)).withTimeout(0.2),
                
                () -> m_intakeSubsystem.getLastBall()
            ),
            new InstantCommand(() -> m_intakeSubsystem.addBall())

        );
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        m_intakeSubsystem.setIntake(false);
        m_intakeSubsystem.setMagazine(false);
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}