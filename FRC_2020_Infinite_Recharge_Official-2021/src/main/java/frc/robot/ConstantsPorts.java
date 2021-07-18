/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.I2C;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class ConstantsPorts {
    
    //Constants used to determine ports for constructors

    //PORTS
    //Sparks
    public static final int leftDrivePort = 0; 
    public static final int rightDrivePort = 1;
    public static final int indexLeftPort = 2;
    public static final int indexRightPort = 3;
    public static final int intakePort = 4;
    public static final int colorWheelPort = 5;
    public static final int funnelMotorPort = 6;

    //Solenoids 
    public static final int lockPortB = 0;
    public static final int releasePortA = 1;
    public static final int lockPortA = 2;
    public static final int releasePortB = 3;
    public static final int intakeLifterPort = 4;
    public static final int colorWheelPistonPort = 5;
    public static final int panelPort = 6;

    //CAN Device #'s
    public static final int shooterTopMasterID = 2;
    public static final int shooterBottomMasterID = 3;
    public static final int shooterTopSlaveID = 1;
    public static final int shooterBottomSlaveID = 3;
    public static final int panID = 2;
    public static final int tiltID = 0;

    //Servos
    public static final int rightRopeReleasePort = 8;
    public static final int leftRopeReleasePort = 7;

    //Sensors
    public static final I2C.Port colorWheelSensorPort = I2C.Port.kOnboard;

    //Encoders
    public static final int leftDriveEncAPort = 0;
    public static final int leftDriveEncBPort = 1;
    public static final int rightDriveEncAPort = 2;
    public static final int rightDriveEncBPort = 3;
    public static final int tiltCounterPort = 6;
    public static final int panEncAPort = 4;
    public static final int panEncBPort = 5;
    public static final int colorWheelEncAPort = 11;
    public static final int colorWheelEncBPort = 12;

    //Limit Switches
    public static final int shooterTiltLimitSwitch = 7;
    public static final int shooterPanLimitSwitch = 8;
}
