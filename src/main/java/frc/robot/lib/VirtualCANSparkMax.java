package frc.robot.lib;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import com.revrobotics.*;
import edu.wpi.first.wpilibj.RobotBase;

public class VirtualCANSparkMax {
   private final CANSparkMax physicalMotor;
   private final CANEncoder physicalEncoder;
   private final CANPIDController physicalController;

   private SimDevice simDevice;
   private SimDouble simMotorOutput;
   private SimDouble simMotorVelocity;
   
   private double PID_setPoint;
   private double PID_lastValue;
   private double PID_totalValue;
   private double PID_P;
   private double PID_I;
   private double PID_D;
   private double PID_FF;
   private double PID_minOutput;
   private double PID_maxOutput;
   private double PID_velocity;

   public VirtualCANSparkMax(int port) {
      if (RobotBase.isReal()) {
         physicalMotor = new CANSparkMax(port, CANSparkMaxLowLevel.MotorType.kBrushless);
         physicalEncoder = new CANEncoder(physicalMotor);
         physicalController = physicalMotor.getPIDController();
         simDevice = null;
         simMotorOutput = null;
         simMotorVelocity = null;
      } else {
         simDevice = SimDevice.create("CAN Spark MAX", port);
         if (simDevice != null) {
           simMotorOutput = simDevice.createDouble("Motor Output", true, 0.0);
           simMotorVelocity = simDevice.createDouble("Motor Velocity", true, 0.0);
         }
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
         double speed = Math.min(Math.max(pValue + iValue + dValue + ffValue, PID_minOutput), PID_maxOutput);
         simMotorOutput.set(speed);
         simMotorVelocity.set(PID_velocity);
         PID_velocity *= 0.9; // Very accurate motor simulation
         PID_velocity += speed*1000;
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
      } else {
         PID_minOutput = kMinOutput;
         PID_maxOutput = kMaxOutput;
      }
   }
   public void setReference(double targetSpeed) {
      if (RobotBase.isReal()) {
         physicalController.setReference(targetSpeed, ControlType.kVelocity);
      } else {
         PID_setPoint = targetSpeed;
      }
   }
   public double getVelocity() {
      if (RobotBase.isReal()) {
         return physicalEncoder.getVelocity();
      } else {
         return PID_velocity;
      }
   }
   public double getMotorTemperature() {
      if (RobotBase.isReal()) {
         return physicalMotor.getMotorTemperature();
      } else {
         return 500000; // Oh nO iTs toO Hot
      }
   }
}
