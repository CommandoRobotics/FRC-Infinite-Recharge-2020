/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.DriveCommands.DriveStraightTime;
import frc.robot.commands.SolenoidSetsAndToggles.ToggleIntake;
import frc.robot.subsystems.DriveSubsystem;

// Moves off the initialization line toward the Oppsing Alliance's driver station.
// May or may not push another robot (that presumably does not have auto) in the process.
public class PushTowardWallAuto extends SequentialCommandGroup {
  double motorSpeed = 0.75;
  double driveTime = 3.0;
  public AutoPushTowardWall(DriveSubsystem driveSubsystem) {
    addCommands(
       new DriveStraightTime(driveSpeed, driveSpeed, driveTime, driveSubsystem)
    );
  }
}
