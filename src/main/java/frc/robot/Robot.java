/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.vision.CameraMode;
import frc.robot.vision.LimeLight;
import edu.wpi.first.wpilibj.RobotController;

public class Robot extends TimedRobot {
    // Create objects to run auto and teleop code
    public Teleop teleop = null;
    Auto auto = null;
    Encoders encoders = null;
    ColorSensor colorSensor = null;
    LimeLight limeLight = null;
    RobotMap robotMap = null;
    Shooter shooter = null;
    // Climber climber = null;
    SWATDrive driveTrain = null;
    Gyro gyro = null;
    List<String> autoCommands;

    // Add variables for the auto selector
    final String rev = "5015-2020-rev1";
    String fileName = "/home/lvuser/autos/swatbots.auto";
    Path path = Paths.get(fileName);
    private String autoSelected;
    private final SendableChooser<String> autoPicker = new SendableChooser<>();

    private final String defaultDriverScheme = "Default";
    private final String reverseTurning = "Reverse Turning";
    private String driveSchemeSelected;
    private final SendableChooser<String> driveSchemePicker = new SendableChooser<>();

    private final String defaultGunnerScheme = "Default";
    private final String funMode = "Fun Mode";
    private String gunnerSchemeSelected;
    private final SendableChooser<String> gunnerSchemePicker = new SendableChooser<>();
    private NetworkTableEntry driveTemp;
    private NetworkTableEntry shooterTemp;
    private NetworkTableEntry batteryVolt;

    public Robot() {
        robotMap = new RobotMap();
        teleop = new Teleop(robotMap);
        driveTrain = robotMap.swatDrive;
        shooter = robotMap.shooter;
        auto = new Auto(robotMap);
    }

    @Override
    public void robotInit() {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("Min Value", 0);
        properties.put("Max Value", 70);
        properties.put("Threshold", 50);
        properties.put("Angle Range", 180);
        properties.put("Color", "green");
        properties.put("Threshold Color", "red");

        Map<String, Object> propertiesBattery = new HashMap<String, Object>();
        propertiesBattery.put("Min Value", 0);
        propertiesBattery.put("Max Value", 100);
        propertiesBattery.put("Threshold", 10);
        propertiesBattery.put("Angle Range", 180);
        propertiesBattery.put("Color", "red");
        propertiesBattery.put("Threshold Color", "green");

        driveTemp = Shuffleboard.getTab("Dashboard Refresh")
        .add("Drive Train Temperature", driveTrain.getTemperature())
        .withWidget("Temperature Gauge") // specify the widget here
        .withProperties(properties)
        .getEntry();

        shooterTemp = Shuffleboard.getTab("Dashboard Refresh")
            .add("Shooter Temperature", shooter.getTemperature())
            .withWidget("Temperature Gauge") // specify the widget here
            .withProperties(properties)
            .getEntry();

        double volt = (RobotController.getBatteryVoltage() - 11) / 2;
        batteryVolt = Shuffleboard.getTab("Dashboard Refresh")
            .add("Battery Gauge", volt)
            .withWidget("Temperature Gauge") // specify the widget here
            .withProperties(propertiesBattery)
            .getEntry();

        try {
            autoCommands = Files.readAllLines(path, StandardCharsets.UTF_8);
            if(!autoCommands.get(0).equals(rev)) {
                DriverStation.reportError("Error: Auto File revision did not match. \nExpected: " + rev + ", Actual: " + autoCommands.get(0), true);
            }
            else {
                autoCommands = Files.readAllLines(path, StandardCharsets.UTF_8);
                auto.setAutoCommands(autoCommands);
                for (String command : autoCommands) {
                    if(command.endsWith(":") && command.length() > 1) {
                        String commandString = command.substring(0, command.length()-1);
                        autoPicker.addOption(commandString, commandString);
                        System.out.println(commandString);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        autoPicker.setDefaultOption("Default Auto", "Default");
        SmartDashboard.putData("Auto Selector", autoPicker);

        driveSchemePicker.setDefaultOption("Default", defaultDriverScheme);
        driveSchemePicker.addOption("Reverse Turning", reverseTurning);
        SmartDashboard.putData("Drive Scheme choices", driveSchemePicker);

        gunnerSchemePicker.setDefaultOption("Default", defaultGunnerScheme);
        gunnerSchemePicker.addOption("Fun Mode", funMode);
        SmartDashboard.putData("Gunner Scheme choices", gunnerSchemePicker);

        SmartDashboard.putNumber("Balls Stored", 3.0);

        robotMap.shooter.ShooterInit();

        teleop.teleopInit();
        robotMap.limeLight.setCameraMode(CameraMode.Drive);
    }

    /**
    * This function is called every robot packet, no matter the mode. Use
    * this for items like diagnostics that you want ran during disabled,
    * autonomous, teleoperated and test.
    *
    * <p>This runs after the mode specific periodic functions, but before
    * LiveWindow and SmartDashboard integrated updating.
    *
    */
    @Override
    public void robotPeriodic() {
        driveTemp.setDouble(driveTrain.getTemperature());
        shooterTemp.setDouble(shooter.getTemperature());
        double volt = Math.floor(((RobotController.getBatteryVoltage() - 11.75) / 2) * 100);
        if (volt < 0) {
            volt = 0;
        } else if (volt > 100) {
            volt = 100;
        }
        batteryVolt.setDouble(volt);
    }

    @Override
    public void disabledInit() {
        super.disabledInit();
    }

    @Override
    public void disabledPeriodic() {
        robotMap.shooter.setBallsStored((int)SmartDashboard.getNumber("Balls Stored", 3));
    }

    @Override
    public void autonomousInit() {
        autoSelected = autoPicker.getSelected();
        auto.setAutoMode(autoSelected);
        System.out.println(autoSelected);
        auto.AutoInit();
    }

    /**
    * This function is called periodically during autonomous.
    */
    @Override
    public void autonomousPeriodic() {
        auto.AutoPeriodic();
    }

    /**
    * This function is called periodically during operator control.
    */
    @Override
    public void teleopInit() {
        auto.AutoDisabled();

        driveSchemeSelected = driveSchemePicker.getSelected();
        teleop.setDriveScheme(driveSchemeSelected);

        gunnerSchemeSelected = gunnerSchemePicker.getSelected();
        teleop.setGunnerScheme(gunnerSchemeSelected);
        teleop.teleopInit();
    }
    @Override
    public void teleopPeriodic() {
        teleop.TeleopPeriodic();
    }

    public void testPeriodic() {
        teleop.TestPeriodic();
    }
}
