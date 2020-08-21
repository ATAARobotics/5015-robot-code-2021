package frc.robot.lib;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.*;
import edu.wpi.first.wpilibj.RobotBase;

public class VirtualCANSparkMax {
   private final CANSparkMax physicalMotor;
   private final CANEncoder physicalEncoder;
   private final CANPIDController physicalController;

   private final WPI_VictorSPX virtualMotor;

   private double PID_setPoint;
   private double PID_lastValue;
   private double PID_totalValue;
   private double PID_P;
   private double PID_I;
   private double PID_D;
   private double PID_FF;
   private double PID_minOutput;
   private double PID_maxOutput;

   public VirtualCANSparkMax(int port) {
      if (RobotBase.isReal()) {
         physicalMotor = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
         physicalEncoder = new CANEncoder(physicalMotor);
         physicalController = physicalMotor.getPIDController();
         virtualMotor = null;
      } else {
         virtualMotor = new WPI_VictorSPX(port);
         physicalMotor = null;
         physicalEncoder = null;
         physicalController = null;
      }
   }

   public void periodic() {
      if (!RobotBase.isReal()) {
         double encoderSpeed = getVelocity();
         double pValue = (PID_setPoint - encoderSpeed) * PID_P;
         PID_totalValue += pValue;
         double iValue = PID_totalValue * PID_I;
         double dValue = (pValue - PID_lastValue) * PID_D;
         double ffValue = PID_setPoint * PID_FF;
         PID_lastValue = pValue;
         virtualMotor.set(pValue + iValue + dValue + ffValue);
      }
   }

   public void setP(double kP) {
      if (RobotBase.isReal()) {
         physicalController.setP(kP);
      } else {
         PID_P = kP;
      }
   }
   public void setI(double kI) {
      if (RobotBase.isReal()) {
         physicalController.setI(kI);
      } else {
         PID_I = kI;
      }
   }
   public void setD(double kD) {
      if (RobotBase.isReal()) {
         physicalController.setD(kD);
      } else {
         PID_D = kD;
      }
   }
   public void setIZone(double kIz) {
      if (RobotBase.isReal()) {
         physicalController.setIZone(kIz);
      }
   }
   public void setFF(double kFF) {
      if (RobotBase.isReal()) {
         physicalController.setFF(kFF);
      } else {
         PID_FF = kFF;
      }
   }
   public void setOutputRange(double kMinOutput, double kMaxOutput) {
      if (RobotBase.isReal()) {
         physicalController.setOutputRange(kMinOutput, kMaxOutput);
      }
   }
   public void setReference(double targetSpeed) {
      if (RobotBase.isReal()) {
         PID_setPoint = targetSpeed;
      } else {
         physicalController.setReference(targetSpeed, ControlType.kVelocity);
      }
   }
   public double getVelocity() {
      return virtualMotor.getSelectedSensorVelocity();
   }
   public double getMotorTemperature() {
      return physicalMotor.getMotorTemperature();
   }
}
