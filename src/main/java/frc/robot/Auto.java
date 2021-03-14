package frc.robot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import frc.robot.vision.CameraMode;
import frc.robot.vision.LimeLight;
import edu.wpi.first.wpilibj.Timer;

//import frc.robot.pathweaver.PathFinder;

/**
 * A file dedicated to all auto related code
 *
 * @author Alexander Greco and Jacob Guglielmin
 */
public class Auto {

    String autoSelected = null;

    // TODO: Tune PID
    // Turn values
    double Tp = 0.04;
    double Ti = 0.03;
    double Td = 0.007;

    double turn = 0;
    PIDController turnPID;

    boolean driveIsEnabled;
    boolean turnIsEnabled;

    // Adjusts motor speeds so that they match
    // private final double LEFT_SPEED_CONSTANT = -0.851;
    // private final double RIGHT_SPEED_CONSTANT = -1;

    Encoders encoders = null;
    SWATDrive swatDrive = null;
    Gyro gyro = null;
    List<String> autoCommands;
    List<List<String>> splitAutoCommands = new ArrayList<List<String>>();
    final String rev = "5015-2020-rev1";
    String fileName = "/home/lvuser/autos/swatbots.auto";
    Timer timer = new Timer();
    Path path = Paths.get(fileName);

    private int commandNumber = 0;
    private boolean nextCommand = false;
    private int nextCounter = 0;
    private int correctCounter = 0;

    private Shooter shooter;
    private LimeLight limeLight;

    private Align alignment;

    private int onTargetCounter = 0;

    private List<String> autoCommandList;

    public Auto(RobotMap robotMap) {
        this.gyro = robotMap.getGyro();
        this.encoders = robotMap.getDriveEncoders();
        this.swatDrive = robotMap.swatDrive;
        this.shooter = robotMap.shooter;
        this.alignment = robotMap.align;
        this.limeLight = robotMap.limeLight;
    }

    /**
     * Function that contains tasks designed to be ran at initalization
     */
    public void AutoInit() {
        encoders.reset();
        gyro.reset();
        limeLight.setCameraMode(CameraMode.Vision);
        // Speed PID
        /* Config the peak and nominal outputs, 12V means full */

        // Turn PID
        turnPID = new PIDController(Tp, Ti, Td);

        turnPID.setTolerance(2.0);
        turnPID.setIntegratorRange(-1.0, 1.0);
        turnPID.setSetpoint(0.0);
        //Remove rev#
        autoCommands.remove(0);
        //auto start
        int start = 1;
        int end = autoCommands.size();
        int i=0;
        //Get the index of the line which defines the selected auto and get the first command based on its location
        outer: for (;i<autoCommands.size();i++) {
            String command = autoCommands.get(i);
            if(command.contains(autoSelected)) {
               start=i+1;
               break outer;
            }
        }
        i++;
        //Get the index of the line which defines the next auto  and get the last command based on its location
        outer: for (;i<autoCommands.size();i++) {
            String command = autoCommands.get(i);
            if(command.contains(":")) {
               end=i;
               break outer;
            }
        }
        //Split commands into command type and value ex.["m","10"]
        autoCommandList = autoCommands.subList(start, end);
        for (String command : autoCommandList) {
            List<String> item = Arrays.asList(command.split(" "));
            splitAutoCommands.add(item);
        }
    }

    /**
     * Periodic function that contains tasks that are designed to be ran
     * periodically.
     */
    public void AutoPeriodic() {
        System.out.println(autoSelected);
        shooter.shooterPeriodic();
        if (nextCounter < 10) {
            nextCounter++;
        }
        if (commandNumber > splitAutoCommands.size() - 1) {
            return;
        }
        List<String> command = splitAutoCommands.get(commandNumber);
        String commandType = command.get(0);
        double commandValue = Double.parseDouble(command.get(1));
        System.out.println(commandType);
        //Move with encoder value PID
        if(commandType.equals("m")) {
            commandValue = commandValue*12;
            System.out.println(commandValue);
            if (encoders.PID(commandValue)) {
                correctCounter++;
            } else {
                correctCounter = 0;
            }
            if (correctCounter > 10) {
                nextCommand = true;
            }
        }
        //Rotate with gyro value PID
        else if(commandType.equals("r")) {
            //TODO: PID for rotation
            turnPID.calculate(gyro.getAngle(), commandValue);
            nextCommand = turnPID.atSetpoint();
        }
        //Shoot until empty
        else if(commandType.equals("s")) {
            if(alignment.atSetpoint()){
                DriverStation.reportWarning("On target with " + shooter.getBallsStored(), false);
                onTargetCounter++;
                // Once has been on target for 10 counts: Disable PID, Reset Camera Settings
                if (onTargetCounter > 10) {
                    //Pass target distance to shooter
                    if(shooter.getBallsStored() != 0) {
                        shooter.setShooterSpeed(alignment.getDistance());
                        shooter.shoot(true);
                    }
                    else {
                        shooter.shoot(false);
                        nextCommand = true;
                    }
                }
            } else {
                DriverStation.reportWarning("Not on target", false);
                //Rotate using values from the limelight
               swatDrive.arcadeDrive(0.0, alignment.visionAlign());
            }
        }
        else if(commandType.equals("f")) {
            if(Timer.getMatchTime()>5) {
                shooter.setShooterSpeed(alignment.getDistance()+2);
                shooter.shoot(true);
            }
            else {
                shooter.shoot(false);
                nextCommand = true;
            }
        }
        //increment commandNumber after a completed command and run resets
        if(nextCommand) {
            shooter.shoot(false);
            onTargetCounter = 0;
            System.out.println("New Command");
            commandNumber++;
            encoders.reset();
            encoders.PID(0);
            nextCommand = false;
            nextCounter = 0;
            correctCounter = 0;
        }
    }

    /**
     * Function that contains tasks designed to be ran when the robot is disabled.
     */
    public void AutoDisabled() {

    }

    //Get auto selected from dashboard
    public void setAutoMode(String autoMode) {
        if(autoSelected != autoMode) {
            autoSelected = autoMode;
            int start = 1;
            int end = autoCommands.size();
            int i=0;
            outer: for (;i<autoCommands.size();i++) {
                String command = autoCommands.get(i);
                if(command.contains(autoSelected)) {
                   start=i+1;
                   break outer;
                }
            }
            i++;
            //Get the index of the line which defines the next auto  and get the last command based on its location
            outer: for (;i<autoCommands.size();i++) {
                String command = autoCommands.get(i);
                if(command.contains(":")) {
                   end=i;
                   break outer;
                }
            }
            //Split commands into command type and value ex.["m","10"]
            autoCommandList = autoCommands.subList(start, end);
            for (String command : autoCommandList) {
                List<String> item = Arrays.asList(command.split(" "));
                splitAutoCommands.add(item);
            }
        }
    }

    //Get commands from auto file
	public void setAutoCommands(List<String> autoCommands) {
        this.autoCommands = autoCommands;
        System.out.println(autoCommands.size());
	}
}
