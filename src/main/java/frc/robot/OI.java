package frc.robot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
class OI {

    //Driver Variables
    private XboxController driveStick = new XboxController(0);
    private String driverScheme = "Default";
    private double XSpeed;
    private double ZRotation;
    private boolean gearShift;
    private boolean slow;
    private boolean visionShoot;
    private boolean climbToggle;
    private boolean manualClimb;
    private boolean climbRelease;
    private boolean intakeToggle;
    private boolean intakeReverse;
    private boolean hoodToggle;
    private boolean magazineReverse;
    private boolean resetBalls;

    //Gunner variables
    private XboxController gunnerStick = new XboxController(1);
    private String gunnerScheme = "Default";
    private boolean manualShoot;
    private boolean overrideSafeties = false;
    private boolean discoToggle;
    private boolean distShoot;

    public OI() {

    }
    //periodic function to update controller input
    public void checkInputs() {

        gearShift = driveStick.getXButtonReleased();
        slow = driveStick.getAButtonReleased();

        manualClimb = driveStick.getBumper(Hand.kLeft) && driveStick.getBumper(Hand.kRight);

        //climbToggle = driveStick.getBumperReleased(Hand.kLeft);
        climbToggle = false;
        climbRelease = driveStick.getStartButton();

        resetBalls = gunnerStick.getStartButtonReleased();
        visionShoot = gunnerStick.getBButtonReleased();
        manualShoot = gunnerStick.getYButton();
        distShoot = gunnerStick.getAButton();
        intakeToggle = gunnerStick.getBumperReleased(Hand.kRight);
        hoodToggle = gunnerStick.getBumperReleased(Hand.kLeft);
        intakeReverse = gunnerStick.getXButtonReleased();
        magazineReverse = gunnerStick.getBackButton();


        //Switch statement to determine controls for the driver
        switch (driverScheme) {
            case "Reverse Turning":
                XSpeed = -driveStick.getY(Hand.kLeft);
                ZRotation = driveStick.getX(Hand.kRight);
                break;
            default:
                XSpeed = driveStick.getY(Hand.kLeft);
                ZRotation = -driveStick.getX(Hand.kRight);
                break;
        }

        //Switch statement to determine controls for the gunner
        switch (gunnerScheme) {
            case "Fun Mode":

                discoToggle = gunnerStick.getBackButtonReleased();
                break;

            default:

                // if ((gunnerStick.getTriggerAxis(Hand.kRight) >= 0.75) && (gunnerStick.getTriggerAxis(Hand.kLeft) >= 0.75) && !overriding) {
                //     overrideSafeties = !overrideSafeties;
                //     overriding = true;
                // } else if ((gunnerStick.getTriggerAxis(Hand.kRight) <= 0.75) && (gunnerStick.getTriggerAxis(Hand.kLeft) <= 0.75)) {
                //     overriding = false;
                // }

                break;
        }
    }

    //Getter functions for controls
    public double getXSpeed() {
        return XSpeed;
    }
    public double getZRotation() {
        return ZRotation;
    }

    public boolean getGearShift() {
        return gearShift;
    }

    public boolean getSlow() {
        return slow;
    }

    public boolean getVisionShoot() {
        return visionShoot;
    }
    public boolean getDiscoButton(){
        return discoToggle;
    }
    public void setDriveScheme(String driveScheme){
        driverScheme = driveScheme;
    }
    public void setGunnerScheme(String gunnerScheme){
        this.gunnerScheme = gunnerScheme;
    }
	public boolean getManualShoot() {
		return manualShoot;
    }

    public boolean getOverride() {
        return overrideSafeties;
    }

    public boolean getClimbButton() {
        return climbToggle;
    }

    public boolean getManualClimb() {
        return manualClimb;
    }

    public boolean getDistShoot() {
        return distShoot;
    }

    public boolean getClimbRelease() {
        return climbRelease;
    }
    public boolean getIntakeToggle(){
        return intakeToggle;
    }
    public boolean getIntakeReverse(){
        return intakeReverse;
    }
    public boolean getHoodToggle(){
        return hoodToggle;
    }
    public boolean getMagazineReverse(){
        return magazineReverse;
    }
    public boolean getBallReset(){
        return resetBalls;
    }
}
