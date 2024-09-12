/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController.Axis;
import edu.wpi.first.wpilibj.XboxController.Button;
import frc.robot.commands.ShooterCommands.*;
import frc.robot.Triggers.*;
import frc.robot.Triggers.TriggerPOV.POVDirection;
import frc.robot.commands.AutoMain;
import frc.robot.commands.DriveCommands.DriveTank;
import frc.robot.commands.IntakeCommands.SweepIntake;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import frc.robot.commands.SolenoidSetsAndToggles.CompressIntake;
import frc.robot.commands.SolenoidSetsAndToggles.ReleaseIntake;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  //Limelights and Network Tables
  NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
  UsbCamera camera;

  //Subsystems
  private final AutoAimSubsystem autoAimSubsystem = new AutoAimSubsystem(limelight);
  private final ClimbSubsystem climbSubsystem = new ClimbSubsystem();
  private final ColorWheelSubsystem colorWheelSubsystem = new ColorWheelSubsystem();
  private final DriveSubsystem driveSubsystem = new DriveSubsystem();
  private final IndexSubsystem indexSubsystem = new IndexSubsystem();
  private final IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private final LifterSubsystem lifterSubsystem = new LifterSubsystem();
  private final ShooterSubsystem shooterSubsystem = new ShooterSubsystem(limelight);
  
  // Commands

  // Controllers
  private final XboxController driverController = new XboxController(0);
  private final XboxController operatorController = new XboxController(1);
  
  //Buttons and triggers (mostly Objects created for ALT commands)
  private final TriggerTrigger operatorLeftTrigger = new TriggerTrigger(operatorController, Hand.kLeft, .1);
  private final JoystickButton operatorBButton = new JoystickButton(operatorController, Button.kB.value);
  private final JoystickButton operatorYButton = new JoystickButton(operatorController, Button.kY.value);

  private final TriggerPOV operatorPOVDown = new TriggerPOV(operatorController, POVDirection.kDown);


 
  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   * Basically the robot start up code and the maintain code for the robot
   */
  public RobotContainer() {
    //Update Limelight to the default values and start our lifecam server 
    limelight.getEntry("pipeline").setNumber(0);
    limelight.getEntry("ledMode").setNumber(0);
    camera = CameraServer.getInstance().startAutomaticCapture();

    //Set and Start our default Commans (Only Tank atm)
    driveSubsystem.setDefaultCommand(new DriveTank(
      () -> -driverController.getRawAxis(XboxController.Axis.kLeftY.value),
      () -> -driverController.getRawAxis(XboxController.Axis.kRightY.value),
      driveSubsystem));

    //autoAimSubsystem.setDefaultCommand(new AutoPan(autoAimSubsystem));

    //Make sure our PID targets are set to 0 so we dont get some wierd instant running
    shooterSubsystem.setShooterTarget(0);

    //And then finally wrap up our button binds
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {  


    //DRIVER Binds
    

    //Left Trigger: Sweep The Haters and the Ball Breakers away
    new TriggerTrigger(driverController, Hand.kLeft, .1)
      .whenActive(new SweepIntake(intakeSubsystem), true)
      .whenInactive(intakeSubsystem::stopIntake, intakeSubsystem);


    //Right Trigger: IntakeIn (Just intake)
    new TriggerTrigger(driverController, Hand.kRight, .1)
      .whenActive(new InstantCommand(intakeSubsystem::intakeCells, intakeSubsystem))
      .whenInactive(new InstantCommand(intakeSubsystem::stopIntake, intakeSubsystem));


    //Left Bumper: Release Lifter and Panel
    new JoystickButton(driverController, Button.kBumperLeft.value)
      .whenActive(new ReleaseIntake(lifterSubsystem));


    //Right Bumper: Retract the Lifter and Panel
    new JoystickButton(driverController, Button.kBumperRight.value)
      .whenActive(new CompressIntake(lifterSubsystem));


    //X Button: Toggle the lifter only
    new JoystickButton(driverController, Button.kX.value)
      .whenActive(lifterSubsystem::toggleLifter, lifterSubsystem);

     
    //B Button: Toggle the Panel only
    new JoystickButton(driverController, Button.kB.value)
      .whenActive(lifterSubsystem::togglePanel, lifterSubsystem);


    //OPERATOR BINDS


    //Left Stick X: Manual Pan for target
     new TriggerThumbstickDirectional(operatorController, Axis.kLeftX, false, .15)
       .whenActive(() -> autoAimSubsystem.setPannerOverride(-.2), autoAimSubsystem)
       .whenInactive(autoAimSubsystem::stopPanner, autoAimSubsystem);

    new TriggerThumbstickDirectional(operatorController, Axis.kLeftX, true, .15)
       .whenActive(() -> autoAimSubsystem.setPannerOverride(.2), autoAimSubsystem)
       .whenInactive(autoAimSubsystem::stopPanner, autoAimSubsystem);


    //Right Stick Y: Manual Tilt for target
    new TriggerThumbstickDirectional(operatorController, Axis.kRightY, true, .15)
      .whenActive(() -> autoAimSubsystem.setTiltOverride(-.4), autoAimSubsystem)
      .whenInactive(autoAimSubsystem::stopTilter, autoAimSubsystem);

    new TriggerThumbstickDirectional(operatorController, Axis.kRightY, false, .15)
      .whenActive(() -> autoAimSubsystem.setTiltOverride(.4), autoAimSubsystem)
      .whenInactive(autoAimSubsystem::stopTilter, autoAimSubsystem);


    //Left Trigger: Run index in (for shooting mostly)
    operatorLeftTrigger.whenActive(indexSubsystem::indexIn)
      .whenInactive(indexSubsystem::stopAllIndexMotors);


    //Right Trigger: Set the Target Speed based on the cycleSpeedSelector or TODO if Auto aim active
    new TriggerTrigger(operatorController, Hand.kRight, .1)
      .whenActive(shooterSubsystem::setShooterCycleSpeeds, shooterSubsystem)
      .whenInactive(shooterSubsystem::stopShooter, shooterSubsystem);


    //Left Bumper: Run funnel at a lowerish speed
    new JoystickButton(operatorController, Button.kBumperLeft.value)
      .whenActive((() -> indexSubsystem.setFunnel(ConstantsValues.funnelLowSpeed)), indexSubsystem)
      .whenInactive(indexSubsystem::stopFunnel, indexSubsystem);


    //Right Bumper: Cycle between all target speeds
    new JoystickButton(operatorController, Button.kBumperRight.value)
      .whenActive(shooterSubsystem::cycleSpeeds, shooterSubsystem);


    //A Button: Run the AutoAim Home command to start the automatic aim recenter
    new JoystickButton(operatorController, Button.kA.value)
      .whenActive(new HomeShooter(autoAimSubsystem), false);


    //Y Button: Release climb  (CURRENTLY UNUSED BUT STILL ON THE BOT)
    operatorYButton.whenActive(climbSubsystem::releaseSpring, climbSubsystem);


    //B Button: Release Rope on the climb (CURRENTLY UNUSED BUT STILL ON THE BOT)
    operatorBButton.whenActive(climbSubsystem::releaseRope, climbSubsystem);


    //Start Button: Toggles the Limelight to start running
    new JoystickButton(operatorController, Button.kStart.value)
      .toggleWhenPressed(new AutoPan(autoAimSubsystem).alongWith(new PrintCommand("Starting AutoPan")));


    // new TriggerPOV(operatorController, POVDirection.kLeft)
    //   .whenActive(() -> shooterSubsystem.setShooterTarget(7000), shooterSubsystem)
    //   .whenInactive(new SequentialCommandGroup(
    //     new InstantCommand(() -> shooterSubsystem.setShooterTarget(0), shooterSubsystem),
    //     new InstantCommand(() -> shooterSubsystem.stopShooter(), shooterSubsystem)));

    //     new TriggerPOV(operatorController, POVDirection.kRight)
    //     .whenActive(() -> shooterSubsystem.setShooterTarget(2000), shooterSubsystem)
    //     .whenInactive(new SequentialCommandGroup(
    //       new InstantCommand(() -> shooterSubsystem.setShooterTarget(0), shooterSubsystem),
    //       new InstantCommand(() -> shooterSubsystem.stopShooter(), shooterSubsystem)));

    //CTRL PANEL COMMANDS ARE UNUSED
    //TODO Menu: Tgl Auto Velocity

    //TODO Back: Tgl Auto Aim

    //TODO POV up: Tgl the ctrl panel piston (CURRENTLY UNUSED)

    //TODO POV Left: Run Rotate (Ctrl Panel)

    //TODO POV Right: Run Position (Ctrl Panel)


    //POV Down: Our alt trigger. Dont acutally run a command with this
    //Rather use .and() on a button/trigger with this to activate a command

    //ALT COMMANDS

    //POVDown and Y Button: lock the climb spring
    operatorPOVDown.and(operatorYButton)
      .whenActive(climbSubsystem::lockSpring, climbSubsystem);


    //POVDown and Left Trigger: Reverse the Index to funnel balls out
    operatorPOVDown.and(operatorLeftTrigger)
      .whenActive(indexSubsystem::reverseIndex, indexSubsystem);


    //POVDown and B Button: Resets the rope release to its starting position
    operatorPOVDown.and(operatorBButton)
      .whenActive(climbSubsystem::resetRopeRelease, climbSubsystem);
  }

  public void disableLimelight() {
    autoAimSubsystem.setLimeCameraMode(false);
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return new AutoMain(autoMode.generatorShooter, driveSubsystem, shooterSubsystem, indexSubsystem, intakeSubsystem, lifterSubsystem);
  }
}
