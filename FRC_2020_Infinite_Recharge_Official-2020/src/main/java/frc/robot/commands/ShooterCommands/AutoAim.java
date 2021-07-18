/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.ShooterCommands;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.ConstantsPID;
import frc.robot.ConstantsValues;
import frc.robot.APIs.ProjectileMathAPI;
import frc.robot.subsystems.AutoAimSubsystem;

public class AutoAim extends PIDCommand {

  /*
  * COMPLETE Add in overriding code to stop the shooter so that it doesnt pass a certain angle
  * COMPLETE Add in code to recenter the shooter to 0 when we dont see anything
  * TODO (Maybe) Add in code re-center the shooter using limit switches? ASK MECH LATER
  */

  AutoAimSubsystem autoAimSubsystem;
  PIDController tiltPID;
  ProjectileMathAPI projectileMath;

  //WE BUILD EACH OTHER UP LIKE WE BUILD OUR ROBOT ~ Dorian Head Programmer

  /**
   * Creates a new AutoAim.
   */
  public AutoAim(AutoAimSubsystem m_AutoAimSubsystem) {
    super(
        //Create the controller for the pan PID
        new PIDController(ConstantsPID.kPanP, ConstantsPID.kPanI, ConstantsPID.kPanD),
        //Give it the current value to get error from
        m_AutoAimSubsystem::getPanTarget,
        //Give it the target setpoint
        0,
        //Give it the method that the output will be used on
        output -> m_AutoAimSubsystem.setPanner(output),
        //And add the required subsystem/s
        m_AutoAimSubsystem);

    //Creating the second PID Controller for the tilter
    tiltPID = new PIDController(ConstantsPID.kTiltP, ConstantsPID.kTiltI, ConstantsPID.kTiltD);

    //Create the math API and add the subsytem integration
    projectileMath = new ProjectileMathAPI();
    autoAimSubsystem = m_AutoAimSubsystem;
    addRequirements(m_AutoAimSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //Set both of th tolerances for the PID loops saved in ConstantsValues (probably pretty small)
    tiltPID.setTolerance(ConstantsPID.tiltTolerance);
    getController().setTolerance(ConstantsPID.panTolerance);
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    
    //Detect if the riser is up or down
    if (autoAimSubsystem.riserActive) {
      /*
      * CODE FOR WHEN RISER IS UP
      *
      * Calculate the set angle the tilter should adjust to. This will look confusing, but
      * basically all it's doing is calcualting the the velocity and angle using the limelight
      * distance and the current target height (changes due to lifting pistions)
      */

      //Detect if we are detecting targets
      if (autoAimSubsystem.isTargets()) {
        //Set the setPoint to the calculated Limelight angle
        tiltPID.setSetpoint(
          projectileMath.calculateInitialVelocityAndAngle(
            autoAimSubsystem.getLimelightDis(ConstantsValues.targetHeightLifted, false), 
            ConstantsValues.targetHeightLifted)[1]
          );
 
        //Set the tilter to the calculated PID value using encoder
        autoAimSubsystem.setTilter(tiltPID.calculate(autoAimSubsystem.getTiltAngle()));
      } else {
        //If we dont see targets then set the setPoints to default straight forward
        tiltPID.setSetpoint(ConstantsValues.tiltDefaultPos);
        autoAimSubsystem.setTilter(tiltPID.calculate(autoAimSubsystem.getTiltAngle()));
      }
      
    } else {
      /*
      * CODE FOR WHEN RISER IS DOWN
      *
      * Calculate the set angle the tilter should adjust to. This will look confusing, but
      * basically all it's doing is calcualting the the velocity and angle using the limelight
      * distance and the current target height (changes due to lifting pistions)
      */

     //Detect if we are detecting targets
     if (autoAimSubsystem.isTargets()) {
        //Set the setPoint to the calculated Limelight angle
        tiltPID.setSetpoint(
          projectileMath.calculateInitialVelocityAndAngle(
            autoAimSubsystem.getLimelightDis(ConstantsValues.targetHeightLowered, false), 
            ConstantsValues.targetHeightLowered)[1]
          );

        //Set the tilter to the calculated PID value using encoder
        autoAimSubsystem.setTilter(tiltPID.calculate(autoAimSubsystem.getTiltAngle()));
      } else {
        //If we dont see targets then set the setPoints to default straight forward
        tiltPID.setSetpoint(ConstantsValues.tiltDefaultPos);
        autoAimSubsystem.setTilter(tiltPID.calculate(autoAimSubsystem.getTiltAngle()));
    }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
