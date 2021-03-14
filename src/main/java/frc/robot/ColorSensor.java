package frc.robot;

import java.lang.Math;

import com.revrobotics.ColorSensorV3;
public class ColorSensor {
    private ColorSensorV3 colorSensor = null;

    public ColorSensor(RobotMap robotMap) {
        // Initialize Classes
        //this.colorSensor = robotMap.getColorSensor();
    }

    String findColor() {
        double normRed = colorSensor.getRed();
        double normGreen = colorSensor.getGreen();
        double normBlue = colorSensor.getBlue();

        double max = Math.max(Math.max(normRed, normGreen), normBlue);

        normRed /= max;
        normGreen /= max;
        normBlue /= max;

        // Compute the difference between the actual color and the expected color for each colorwheel color
        double redDifference = Math.abs(normRed-1.0) + Math.abs(normGreen-0.8) + Math.abs(normBlue-0.2);
        double yellowDifference = Math.abs(normGreen-1.0) + Math.abs(normRed-0.5) + Math.abs(normBlue-0.2);
        double greenDifference = Math.abs(normGreen-1.0) + Math.abs(normBlue-0.3) + Math.abs(normRed-0.2);
        double blueDifference = Math.abs(normBlue-1.0) + Math.abs(normGreen-1.0) + Math.abs(normRed-0.2);

        String guess = "Unknown";
        if (redDifference <= greenDifference && redDifference <= blueDifference && redDifference <= yellowDifference) {
            guess = "Red";
        } else if (yellowDifference <= greenDifference && yellowDifference <= blueDifference && yellowDifference <= redDifference) {
            guess = "Yellow";
        } else if (greenDifference <= redDifference && greenDifference <= blueDifference && greenDifference <= yellowDifference) {
            guess = "Green";
        } else if (blueDifference <= greenDifference && blueDifference <= redDifference && blueDifference <= yellowDifference) {
            guess = "Blue";
        }

        return guess;
    }
}
