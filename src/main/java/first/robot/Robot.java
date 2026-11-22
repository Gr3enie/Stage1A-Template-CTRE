/*
 * Copyright 2026 FRCSoftware
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package first.robot;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import first.robot.simulation.DrivetrainSim;
import first.robot.simulation.FuelSim;
import first.robot.simulation.SingleFlywheelSim;

import org.wpilib.drive.DifferentialDrive;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

/**
 * The methods in this class are called automatically as described in the OpModeRobot documentation.
 * OpMode classes anywhere in the package (or sub-packages) where this class is located are
 * automatically registered to display in the Driver Station. If you change the name of this class
 * or the package after creating this project, you must also update the Main.java file in the
 * project.
 */
public class Robot extends OpModeRobot {

  private final CANBus mainCAN = new CANBus(CANPort.CAN_S0);

  private final TalonFX m_fl = new TalonFX(0, mainCAN);
  private final TalonFX m_bl = new TalonFX(1, mainCAN);
  private final TalonFX m_fr = new TalonFX(2, mainCAN);
  private final TalonFX m_br = new TalonFX(3, mainCAN);

  public final TalonFX intakeLauncher = new TalonFX(4, mainCAN);
  public final TalonFX feeder = new TalonFX(5, mainCAN);

  public final DifferentialDrive drivetrain =
      new DifferentialDrive(m_fl::setThrottle, m_fr::setThrottle);

  private final DrivetrainSim drivetrainSim = new DrivetrainSim(m_fl, m_fr);
  private final SingleFlywheelSim intakeLauncherSim = SingleFlywheelSim.forIntakeLauncher(intakeLauncher);
  private final SingleFlywheelSim feederSim = SingleFlywheelSim.forFeeder(feeder);

  private final OnboardIMU imu = new OnboardIMU(MountOrientation.FLAT);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    TalonFXConfiguration lConf = new TalonFXConfiguration();
    TalonFXConfiguration rConf = new TalonFXConfiguration();

    lConf.MotorOutput.withInverted(InvertedValue.CounterClockwise_Positive);
    rConf.MotorOutput.withInverted(InvertedValue.Clockwise_Positive);

    m_fl.getConfigurator().apply(lConf);
    m_bl.getConfigurator().apply(lConf);
    m_fr.getConfigurator().apply(rConf);
    m_br.getConfigurator().apply(rConf);

    m_bl.setControl(new Follower(m_fl.getDeviceID(), MotorAlignmentValue.Aligned));
    m_br.setControl(new Follower(m_fr.getDeviceID(), MotorAlignmentValue.Aligned));
  }

  @Override
  public void simulationPeriodic() {
    drivetrainSim.periodic();
    intakeLauncherSim.periodic();
    feederSim.periodic();
    FuelSim.periodic();
  }
}
