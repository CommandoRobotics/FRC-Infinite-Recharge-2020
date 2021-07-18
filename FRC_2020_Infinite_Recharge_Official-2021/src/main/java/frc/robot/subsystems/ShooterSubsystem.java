/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ConstantsPID;
import frc.robot.ConstantsPorts;
import frc.robot.ConstantsValues;

public class ShooterSubsystem extends SubsystemBase {

  CANSparkMax shooterTopMaster;
  VictorSPX shooterTopSlave;
  CANSparkMax shooterBottomMaster;
  VictorSPX shooterBottomSlave;

  CANEncoder shooterTopEnc;
  CANEncoder shooterBottomEnc;

  NetworkTable limelight;

  CANPIDController topPIDController;
  CANPIDController bottomPIDController;

  double shooterTopMinSpeed;
  double loaderMinSpeed;
  double cycleSpeed;
  public boolean riserActive = false;
  public boolean seeingTarget = false;

  public int INCH_MODE = 0;
  public int METER_MODE = 1;

  public ShooterSubsystem(NetworkTable m_limelight) {
    shooterTopMaster = new CANSparkMax(ConstantsPorts.shooterTopMasterID, MotorType.kBrushed);
    shooterBottomMaster = new CANSparkMax(ConstantsPorts.shooterBottomMasterID, MotorType.kBrushed);
    shooterTopEnc = shooterTopMaster.getEncoder(EncoderType.kQuadrature, ConstantsValues.shooterCPR);
    shooterTopEnc.setInverted(false);
    shooterBottomEnc = shooterBottomMaster.getEncoder(EncoderType.kQuadrature, ConstantsValues.shooterCPR);
    shooterTopMaster.restoreFactoryDefaults();
    shooterBottomMaster.restoreFactoryDefaults();
    shooterTopMaster.setInverted(true);
    shooterTopSlave = new VictorSPX(ConstantsPorts.shooterTopSlaveID);
    shooterBottomSlave = new VictorSPX(ConstantsPorts.shooterBottomSlaveID);
    shooterBottomSlave.setInverted(true);

    topPIDController = shooterTopMaster.getPIDController();
    bottomPIDController = shooterBottomMaster.getPIDController();
    topPIDController.setFeedbackDevice(shooterTopEnc);
    bottomPIDController.setFeedbackDevice(shooterBottomEnc);

    topPIDController.setP(ConstantsPID.kP);
    topPIDController.setI(ConstantsPID.kI);
    topPIDController.setD(ConstantsPID.kD);
    topPIDController.setIZone(ConstantsPID.kIZone);
    topPIDController.setFF(ConstantsPID.kFF);
    topPIDController.setOutputRange(ConstantsPID.kMin, ConstantsPID.kMax);

    bottomPIDController.setP(ConstantsPID.kP);
    bottomPIDController.setI(ConstantsPID.kI);
    bottomPIDController.setD(ConstantsPID.kD);
    bottomPIDController.setIZone(ConstantsPID.kIZone);
    bottomPIDController.setFF(ConstantsPID.kFF);
    bottomPIDController.setOutputRange(ConstantsPID.kMin, ConstantsPID.kMax);

    loaderMinSpeed = ConstantsValues.loaderMinSpeed;
    shooterTopMinSpeed = ConstantsValues.shooterTMinSpeed;

    limelight = m_limelight;
  }

  /**Set the shooterTop to a certain inputted speed
   * given that the input speed is greater than the minSpeed
   */
  public void setShooter(double speed) {
    if (speed > shooterTopMinSpeed) {
      shooterTopMaster.set(speed);
      shooterBottomMaster.set(speed * .9);
    } else {
      shooterTopMaster.stopMotor();
      shooterBottomMaster.stopMotor();
    }
  }

  public void setShooterCycleSpeeds() {
    shooterTopMaster.set(cycleSpeed);
    shooterBottomMaster.set(cycleSpeed * .9);
  }

  /**Stops the shooter motors */
  public void stopShooter() {
    shooterTopMaster.stopMotor();
    shooterBottomMaster.stopMotor();
  }

  /**Returns the current set speed of the shooter motors
   * 
   * @return set speed as double from -1 to 1
  */
  public double getShooterSetSpeed() {
    return shooterTopMaster.get();
  }

  /**Overrides the minimum speed the shooterTop will run */
  public void setMinShooterSpeed(double speed) {
    shooterTopMinSpeed = speed;
  }
  
  /**Overrides the min speed the loader will run */
  public void setMinLoaderSpeed(double speed) {
    loaderMinSpeed = speed;
  }

  public void setShooterTarget(double targetRPM) {
    topPIDController.setReference(targetRPM, ControlType.kVelocity);
    bottomPIDController.setReference(targetRPM *.9, ControlType.kVelocity);
    SmartDashboard.putNumber("TARGET RPM", targetRPM);
    SmartDashboard.putNumber("TARGET OUTPUT", shooterTopMaster.getClosedLoopRampRate());
  }

  //LIMELIGHT THINGS

  public double getLimelightXOffset() {
    return limelight.getEntry("tx").getDouble(0);
  }

  public double getLimelightYOffset() {
    return limelight.getEntry("ty").getDouble(0);
  }

  /**
   * Gets the distance directly to the target straight from the limlight using
   * right triangles.
   * @param targetHeight The height of the target realtive to the current shooter
   *                     height. This input affects the unit of measurement outputted
   * @param isLifted     Whether or not the the limelight is angled low or high  (this changes 
   *                     the constant angle)
   * @return The distance directly to the target in the same unit of measurement as the
   *         targetHeight
   */
  public double getLimelightDis(double targetHeight, boolean isAngledUp) {
    if (isAngledUp) {
      return targetHeight/Math.sin(ConstantsValues.limlightAngleHigh + limelight.getEntry("ty").getDouble(0));
    } else {
      return targetHeight/Math.sin(ConstantsValues.limlightAngleLow + limelight.getEntry("ty").getDouble(0));
    }
  }

  /**
   * Returns the number   
   * @return
   */
  public boolean isTargets() { 
    seeingTarget = (limelight.getEntry("tv").getDouble(0) == 1) ? true : false;
    return seeingTarget;
  }

  public void setLights(boolean lightsOn) {
    if (lightsOn) {
      limelight.getEntry("pipeline").setNumber(1);
    } else {
      limelight.getEntry("pipeline").setNumber(0);
    }
  }

  public void toggleLimelightLights() {
    if (limelight.getEntry("pipeline").getDouble(0) == 1) {
      limelight.getEntry("pipeline").setNumber(0);
    } else if (limelight.getEntry("pipeline").getDouble(0) == 0) {
      limelight.getEntry("pipeline").setNumber(1);
    }
  }

  public void cycleSpeeds() {
    if (cycleSpeed == 0) {
      cycleSpeed = .1;
    } else if (cycleSpeed == .1) {
      cycleSpeed = .25;
    } else if (cycleSpeed == .25){
      cycleSpeed = .35;
    } else if (cycleSpeed == .35) {
      cycleSpeed = .40;
    } else if (cycleSpeed == .40) {
      cycleSpeed = .5;
    } else if (cycleSpeed == .5) {
      cycleSpeed = .75;
    } else if (cycleSpeed == .75) {
      cycleSpeed = 1;
    } else if (cycleSpeed == 1) {
      cycleSpeed = 0;
    }

  }

  

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    shooterBottomSlave.set(ControlMode.PercentOutput,
                           shooterBottomMaster.get());
    shooterTopSlave.set(ControlMode.PercentOutput, 
                        shooterTopMaster.get());
    SmartDashboard.putNumber("shooterRPM Top", shooterBottomEnc.getVelocity());
    SmartDashboard.putNumber("shooterRPM Bottom", shooterBottomEnc.getVelocity());
    SmartDashboard.putNumber("Current SPEED", cycleSpeed);
    SmartDashboard.putNumber("Limelight LED mode", limelight.getEntry("pipeline").getDouble(3));
  }
}
