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

// Steals the nearest two power cells from the opposing alliance's trench.
// CAUTION: Avoid running this if an opposing team's short robot lines up for their trench.
// Our robot might not be faster than theirs. If we are not faster, we will incur a penalty.
// Requriements: Robot's front bumper fully on the initialization line (parrallel with it),
//   intake pointed toward trench, and robot centered on the two balls in the trench.

public class OpposingTrenchAuto extends SequentialCommandGroup {
  public AutoMain(DriveSubsystem driveSubsystem, ShooterSubsystem shooterSubsystem, 
                  IndexSubsystem indexSubsystem, IntakeSubsystem intakeSubsystem,
                  LifterSubsystem lifterSubsystem) {
    addCommands(

      // Drop/run Intake
      // Drive toward the balls in the trench.
      // Immediately drive foward (out of the trench) when we think we've driven far enough to the balls.
      // Stop when we think we are out of the trench.
      // Turn toward the Outer Port (optionally drive closer to the port)
      // Fire!
    );
  }
}
