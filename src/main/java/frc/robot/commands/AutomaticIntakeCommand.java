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
 * A command that uses the intake subsystem to automatically intake and index
 * balls.
 */
public class AutomaticIntakeCommand extends SequentialCommandGroup {
   private final IntakeSubsystem m_intakeSubsystem;

   public AutomaticIntakeCommand(IntakeSubsystem intakeSubsystem) {
      m_intakeSubsystem = intakeSubsystem;
      addRequirements(intakeSubsystem);
      addCommands(
         new ConditionalCommand(
            //If magazine has empty space:
            new ParallelCommandGroup(
               //Turn on magazine
               new InstantCommand(() -> m_intakeSubsystem.setMagazineOnForIntake()),
               //Turn on intake
               new InstantCommand(() -> m_intakeSubsystem.setIntakeOn())),

            //If the magazine is full:
            new SequentialCommandGroup(
               new ParallelCommandGroup(
                  //Turn off magazine
                  new InstantCommand(() -> m_intakeSubsystem.setMagazineOff()),
                  //Turn off intake
                  new InstantCommand(() -> m_intakeSubsystem.setIntakeOff())),
                  new InstantCommand(() -> this.cancel())),

         //Checks for the empty space in the magazine required to run the above code
         () -> m_intakeSubsystem.getMagazineFree()),
            
         //Wait until a ball has been detected entering the magazine
         new WaitUntilCommand(() -> m_intakeSubsystem.ballDetected()),
         new ConditionalCommand(
            //If this is the last ball:
            new StartEndCommand(() -> m_intakeSubsystem.setMagazineOnForIntake(),
               //Turn off the magazine after the specified time in seconds
               () -> m_intakeSubsystem.setMagazineOff()).withTimeout(0.1),

               //If this is not the last ball:
               new StartEndCommand(() -> m_intakeSubsystem.setMagazineOnForIntake(),
                  //Turn off the magazine after the specified time in seconds
                  () -> m_intakeSubsystem.setMagazineOff()).withTimeout(0.2),

         //Checks if the ball entering the robot is the last one to run the above code
         () -> m_intakeSubsystem.getLastBall()),

         //Tells the intake subsystem that a ball has entered the magazine
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
      m_intakeSubsystem.setIntakeOff();
      m_intakeSubsystem.setMagazineOff();
   }

   // Returns true when the command should end.
   @Override
   public boolean isFinished() {
      return false;
   }
}
