package frc.robot;

import edu.wpi.first.wpilibj.Ultrasonic;

public class RangeFinder {

    private Ultrasonic ultrasonic = null;

    public RangeFinder(Ultrasonic ultrasonic) {
        this.ultrasonic = ultrasonic;

        Ultrasonic.setAutomaticMode(true);
    }
    
    public double getDistance() {
        return ultrasonic.getRangeInches();
    }
}