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
 * A command that uses the shooter and intake subsystems to automatically
 * shoot balls.
 */
public class ShootCommand extends SequentialCommandGroup {
   private final ShooterSubsystem m_shooterSubsystem;
   private final IntakeSubsystem m_intakeSubsystem;

   public ShootCommand(final ShooterSubsystem shooterSubsystem, final IntakeSubsystem intakeSubsystem) {
      m_shooterSubsystem = shooterSubsystem;
      m_intakeSubsystem = intakeSubsystem;
      addRequirements(shooterSubsystem, intakeSubsystem);
      addCommands(
            new StartEndCommand(
               //Turn on the shooter
               () -> m_shooterSubsystem.setShooter(true),

               //Turn on the magazine
               () -> m_intakeSubsystem.setMagazineOnForShooting(), m_shooterSubsystem, m_intakeSubsystem

            //Cancel this command once the shooter speed is close to the correct speed
            ).withInterrupt(() -> m_shooterSubsystem.nearSetpoint()),
            
            //Turn on the intake
            new InstantCommand(() -> m_intakeSubsystem.setIntakeOn(), m_shooterSubsystem, m_intakeSubsystem),
            
            //Wait until the sensor saw the ball pass by
            new WaitUntilCommand(() -> m_shooterSubsystem.ballDetected()),
            new WaitUntilCommand(() -> !m_shooterSubsystem.ballDetected()),

            //Tells the intake subsystem that a ball has been shot
            new InstantCommand(() -> m_intakeSubsystem.ballShot()));
   }

   @Override
   public void end(boolean interrupted) {
      if (interrupted) {
         //Turn off the shooter
         new InstantCommand(() -> m_shooterSubsystem.setShooter(false));
         //Turn off the magazine
         new InstantCommand(() -> m_intakeSubsystem.setMagazineOff());
      }
   }

}
