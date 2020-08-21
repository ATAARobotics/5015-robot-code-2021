package frc.robot.lib;

import edu.wpi.first.wpilibj.DriverStation;

public class FancyLog {
   public static enum LogLevel {
      ERROR, WARNING, LOG,
   }
   public static enum LogSystem {
      SUBSYSTEM_DRIVE, SUBSYSTEM_SHOOT, SUBSYSTEM_INTAKE, SUBSYSTEM_CLIMB, SUBSYSTEM_VISION,
      COMMAND_OI_DRIVE, COMMAND_AUTO_DRIVE,
   }

   public static final LogSystem enabledLogs[] = {
      LogSystem.SUBSYSTEM_DRIVE, LogSystem.COMMAND_OI_DRIVE, LogSystem.SUBSYSTEM_SHOOT,
   };

   public static void Log(LogLevel level, LogSystem where, String message) {
      boolean enabled = false;
      for (LogSystem enabledLog : FancyLog.enabledLogs) {
         if (enabledLog == where) {
            enabled = true;
         }
      }
      if (enabled) {
         switch (level) {
            case ERROR:
               DriverStation.reportError(where.name() + " (e): " + message, true);
               break;
            case WARNING:
               DriverStation.reportWarning(where.name() + " (w): " + message, true);
               break;
            case LOG:
               System.out.println(where.name() + " (l): " + message);
               break;
         }
      }
   }
}
