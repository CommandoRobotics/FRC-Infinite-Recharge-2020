/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.ConstantsPorts;
import frc.robot.ConstantsValues;

public class AutoAimSubsystem extends SubsystemBase {
  Spark tilt;
  Spark pan;

  Encoder tiltEnc;
  Encoder panEnc;

  double panMinSpeed = ConstantsValues.panMinSpeed;
  double tiltMinSpeed = ConstantsValues.tiltMinSpeed;

  NetworkTable limelight;

  Servo limelightServo;

  public boolean riserActive = false;

  public AutoAimSubsystem(NetworkTable m_limelight) {
    tilt = new Spark(ConstantsPorts.tiltPort);
    pan = new Spark(ConstantsPorts.panPort);

    tiltEnc = new Encoder(ConstantsPorts.tiltEncAPort, ConstantsPorts.tiltEncBPort);
    tiltEnc.setDistancePerPulse(ConstantsValues.tiltDisPerPulse);
    panEnc = new Encoder(ConstantsPorts.panEncAPort, ConstantsPorts.panEncBPort);
    panEnc.setDistancePerPulse(ConstantsValues.panDisPerPulse);

    limelight = m_limelight;
    limelightServo = new Servo(ConstantsPorts.limelightServoPort);
  }

  /**Sets the tilter to a given speed */
  public void setTilter(double speed) {
    if (speed >= tiltMinSpeed) {
      tilt.setSpeed(speed);
    } else {
      tilt.stopMotor();
    }
  }

  /**Sets the Panner to a given speed */
  public void setPanner(double speed) {
    if (speed >= panMinSpeed) {
      pan.setSpeed(speed);
    } else {
      pan.stopMotor();
    }
  }

  /**Stops the Tilter motor */
  public void stopTilter() {
    tilt.stopMotor();
  }

  /**Stops the Panner motor */
  public void stopPanner() {
    pan.stopMotor();
  }

  /**Returns the current set speed of the tilt motor 
   * 
   * @return set speed as double from -1 to 1
  */
  public double getTilterSetSpeed() {
    return tilt.get();
  }

  /**Returns the current set speed of the pan motor 
   * 
   * @return set speed as double from -1 to 1
  */
  public double getPannerSetSpeed() {
    return pan.get();
  }

  /**Returns the raw counts recieved from the tilt encoder 
   * 
   * @return Current raw output from the tilt encoder since the last reset
  */
  public double getTilterEncRaw() {
    return tiltEnc.getRaw();
  }

  /**Returns the tilt encoder's distance in inches */
  public double getTilterDistance() {
    return tiltEnc.getDistance();
  }

  /**
   * This takes the current raw enc value for the tilt encoder and gets the 
   * angle by finding the proportion of rotations and then converting to degrees
   * 
   * @return The angle at which the motor has rotated from start or reset
   */
  public double getTiltAngle() {
    return (tiltEnc.getRaw()/ConstantsValues.tiltEncPPR) * 360;
  }

  /**Resets the Tilter encoder back to zero */
  public void resetTilterEnc() {
    tiltEnc.reset();
  }

  /**Returns the raw counts recieved from the pan encoder 
   * 
   * @return Current raw output from the pan encoder since the last reset
  */
  public double getPannerEncRaw() {
    return panEnc.getRaw();
  }

  /**Returns the pan encoder's distance in inches */
  public double getPannerDistance() {
    return panEnc.getDistance();
  }

  /**
   * This takes the current raw enc value for the pan encoder and gets the 
   * angle by finding the proportion of rotations and then converting to degrees
   * 
   * @return The angle at which the motor has rotated from the last start or reset
   */
  public double getPanAngle() {
    return (panEnc.getRaw()/ConstantsValues.panEncPPR) * 360;
  }

  /**Resets the Panner encoder back to zero */
  public void resetPannerEnc() {
    panEnc.reset();
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
   * right triangles
   * @param targetHeight The height of the target realtive to the current shooter
   *                     height. This input affects the unit of measurement outputted
   * @return The distance directly to the target in the same unit of measurement as the
   *         targetHeight
   */
  public double getLimelightDis(double targetHeight) {
    return targetHeight/Math.sin(ConstantsValues.limlightAngleLow + limelight.getEntry("ty").getDouble(0));
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
