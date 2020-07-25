package frc.robot.commands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.subsystems.VisionSubsystem;

public class VisionAlignCommand extends PIDCommand {
   // Variables for limelight distance tracking
   private static double P = 0.04;
   private static double I = 0.03;
   private static double D = 0.007;
   private double tolerance = 2.5;

   public VisionAlignCommand(DriveSubsystem drive, VisionSubsystem vision) {
      super(
        new PIDController(P, I, D),
        // Close loop on heading
        () -> vision.getTx(),
        // Set reference to target
        0.0,
        // Pipe output to turn robot
        output -> drive.arcadeDrive(0, output),
        // Require the drive
        drive);

        this.getController().setTolerance(tolerance);
   }

   @Override
   public boolean isFinished() {
     // End when the controller is at the reference.
     return getController().atSetpoint();
   }

}
