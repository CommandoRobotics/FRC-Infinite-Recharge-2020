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
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LifterSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import GeneratorShooterAuto;
import OpposingTrenchAuto;
import PushTowardWallAuto;

enum autoMode {
  pushTowardWall,
  generatorShooter,
  opposingTrench
}

public void AutoMain(
  autoMode mode,
  DriveSubsystem driveSubsystem, 
  ShooterSubsystem shooterSubsystem,
  IndexSubsystem indexSubsystem,
  IntakeSubsystem intakeSubsystem,
  LifterSubsystem lifterSubsystem
) {
    switch (mode) {
      case pushTowardWall:
        return PushTowardWallAuto(driveSubsystem);
        break;
      case generatorShooter:
        return GeneratorShooterAuto(driveSubsystem, shooterSubsystem, indexSubsystem, intakeSubsystem, lifterSubsystem);
        break;
      case OpposingTrenchAuto(driveSubsystem, shooterSubsystem, indexSubsystem, intakeSubsystem, lifterSubsystem);
  }
}
