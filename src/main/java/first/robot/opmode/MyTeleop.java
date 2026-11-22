/*
 * Copyright 2026 FRCSoftware
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package first.robot.opmode;

import first.robot.Robot;
import org.wpilib.driverstation.XboxController;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

@Teleop
public class MyTeleop extends PeriodicOpMode {
  private final Robot robot;

  private final XboxController controller = new XboxController(0);

  /** The Robot instance is passed into the opmode via the constructor. */
  public MyTeleop(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void periodic() {
    /* Called periodically (set time interval) while the robot is enabled. */
    robot.drivetrain.arcadeDrive(controller.getLeftY(), controller.getRightX());

    if (controller.getAButton()) {
      // intake
      robot.intakeLauncher.setThrottle(0.8);
      robot.feeder.setThrottle(-1.);
    } else if (controller.getBButton()) {
      // shoot
      robot.intakeLauncher.setThrottle(0.9);
      robot.feeder.setThrottle(0.75);
    } else if (controller.getYButton()) {
      // outtake
      robot.intakeLauncher.setThrottle(-0.8);
      robot.feeder.setThrottle(1.);
    } else {
      // stop
      robot.intakeLauncher.setThrottle(0);
      robot.feeder.setThrottle(0);
    }
  }
}
