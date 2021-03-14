package frc.robot;

import com.cuforge.libcu.Lasershark;

public class RangeFinder {

    private Lasershark lasershark = null;

    public RangeFinder(Lasershark lasershark) {
        this.lasershark = lasershark;
    }
    
    public double getDistance() {
        return lasershark.getDistanceInches();
    }
}