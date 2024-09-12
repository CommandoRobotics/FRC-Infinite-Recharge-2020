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

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class AutoMain extends SequentialCommandGroup {
  /**
   * Creates a new AutoMain.
   */
  public AutoMain(DriveSubsystem driveSubsystem, ShooterSubsystem shooterSubsystem, 
                  IndexSubsystem indexSubsystem, IntakeSubsystem intakeSubsystem,
                  LifterSubsystem lifterSubsystem) {
    addCommands(

      //Set the shooter to .45 power
      new InstantCommand(() -> shooterSubsystem.setShooter(.47), shooterSubsystem),

      //Wait one seconds to charge up
      new WaitCommand(1),

      //Activate index motors to shoot
      new InstantCommand(indexSubsystem::indexIn, indexSubsystem),

      //Wait for a set time to let all balls out of the index
      new WaitCommand(5),

      //And Stop all motors
      new SequentialCommandGroup(
        new InstantCommand(indexSubsystem::stopAllIndexMotors, indexSubsystem),
        new InstantCommand(shooterSubsystem::stopShooter, indexSubsystem)),

      //Drive Striaght for two seconds
       new DriveStraightTime(-.75, -.75, 1.75, driveSubsystem),

       new ToggleIntake(lifterSubsystem),

       new InstantCommand(intakeSubsystem::intakeCells, intakeSubsystem),

       new WaitCommand(2),

       new InstantCommand(intakeSubsystem::stopIntake, intakeSubsystem)
    );
  }
}
