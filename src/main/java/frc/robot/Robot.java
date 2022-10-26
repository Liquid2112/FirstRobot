// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private static final String kDefaultAuto = "Default";
    private static final String kCustomAuto = "My Auto";
    private String m_autoSelected;
    private final SendableChooser<String> m_chooser = new SendableChooser<>();

    public final WPI_TalonFX leftLeader = new WPI_TalonFX(2);
    private final WPI_TalonFX left2 = new WPI_TalonFX(1);
    private final WPI_TalonFX left3 = new WPI_TalonFX(3);

    public final WPI_TalonFX rightLeader = new WPI_TalonFX(5);
    private final WPI_TalonFX right3 = new WPI_TalonFX(4);
    private final WPI_TalonFX right2 = new WPI_TalonFX(6);

    public final DifferentialDrive robotDrive = new DifferentialDrive(leftLeader, rightLeader);

    public final XboxController controller = new XboxController(0);

    private Timer timer = new Timer();
    public double motorSpeed = 0;
    public boolean ifCoast = false;
    public boolean donut = false;

    public final String IN_COAST = "inCoast";

    public final String MOTOR_SPEED_SD = "Leftmotor";

    public final String DONUT = "Donut";

    /**
     * This function is run when the robot is first started up and should be used
     * for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
        m_chooser.addOption("My Auto", kCustomAuto);
        SmartDashboard.putData("Auto choices", m_chooser);
        leftLeader.setNeutralMode(NeutralMode.Brake);
        left2.setNeutralMode(NeutralMode.Brake);
        left3.setNeutralMode(NeutralMode.Brake);
        rightLeader.setNeutralMode(NeutralMode.Brake);
        right2.setNeutralMode(NeutralMode.Brake);
        right3.setNeutralMode(NeutralMode.Brake);

        rightLeader.setInverted(true);
        right2.setInverted(true);
        right3.setInverted(true);
        left2.follow(leftLeader);
        left3.follow(leftLeader);
        right2.follow(rightLeader);
        right3.follow(rightLeader);

        SmartDashboard.putNumber(MOTOR_SPEED_SD, 0.1);
        SmartDashboard.putBoolean(IN_COAST, false);
        SmartDashboard.putBoolean(DONUT, false);
    }

    /**
     * This function is called every 20 ms, no matter the mode. Use this for items
     * like diagnostics
     * that you want ran during disabled, autonomous, teleoperated and test.
     *
     * <p>
     * This runs after the mode specific periodic functions, but before LiveWindow
     * and
     * SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        NeutralMode brakeMode = NeutralMode.Brake;

        motorSpeed = SmartDashboard.getNumber(MOTOR_SPEED_SD, 0);
        ifCoast = SmartDashboard.getBoolean(IN_COAST, false);
        donut = SmartDashboard.getBoolean(DONUT, false);

        if (ifCoast) {
            brakeMode = NeutralMode.Coast;
        }

        leftLeader.setNeutralMode(brakeMode);
        left2.setNeutralMode(brakeMode);
        left3.setNeutralMode(brakeMode);
        rightLeader.setNeutralMode(brakeMode);
        right2.setNeutralMode(brakeMode);
        right3.setNeutralMode(brakeMode);
    }

    /**
     * This autonomous (along with the chooser code above) shows how to select
     * between different
     * autonomous modes using the dashboard. The sendable chooser code works with
     * the Java
     * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
     * chooser code and
     * uncomment the getString line to get the auto name from the text box below the
     * Gyro
     *
     * <p>
     * You can add additional auto modes by adding additional comparisons to the
     * switch structure
     * below with additional strings. If using the SendableChooser make sure to add
     * them to the
     * chooser code above as well.
     */
    @Override
    public void autonomousInit() {
        m_autoSelected = m_chooser.getSelected();
        m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
        System.out.println("Auto selected: " + m_autoSelected);

        timer.start();
        timer.reset();
        System.out.println("autoinit");

    }

    enum AutoStates {
        FORWARD, OFF, TURN_90_LEFT
    };

    AutoStates state = AutoStates.FORWARD;

    /** This function is called periodically during autonomous. */
    @Override
    public void autonomousPeriodic() {
        if (state == AutoStates.FORWARD) {
            robotDrive.arcadeDrive(motorSpeed, 0);

            if (timer.get() > 0.5) {
                robotDrive.arcadeDrive(0, 0);
                timer.reset();
                state = AutoStates.OFF;
            }
        } else if (state == AutoStates.OFF) {
            // Do nothing

            if (timer.get() > 0.5) {
                timer.reset();
                state = AutoStates.TURN_90_LEFT;
            }
        } else if (state == AutoStates.TURN_90_LEFT) {
            robotDrive.arcadeDrive(0, motorSpeed);

            if (timer.get() > 0.5) {
                timer.reset();
                state = AutoStates.FORWARD;
            }
        }
    }
    /*
     * if (state == AutoStates.ONHALF) {
     * robotDrive.arcadeDrive(motorSpeed * .5, 0);
     * 
     * if (timer.get() > 2) {
     * timer.reset();
     * state = AutoStates.ON;
     * }
     * }
     * 
     * if (state == AutoStates.ON) {
     * 
     * robotDrive.arcadeDrive(motorSpeed, 0);
     * 
     * 
     * if (timer.get() > 3) {
     * timer.reset();
     * state = AutoStates.OFF;
     * }
     */

    /*
     * for(int i = 0; i <= 4; i++) {
     * if(!(timer.hasElapsed(5))) {
     * 
     * System.out.println("start motor");
     * 
     * robotDrive.arcadeDrive(0.5, 0);
     * }
     * else{
     * System.out.println("Stop Motors");
     * 
     * robotDrive.arcadeDrive(0, 0);
     * System.out.println("AutoPeriod");
     * 
     * }
     * timer.reset();
     * 
     * if(!(timer.hasElapsed(2))) {
     * robotDrive.tankDrive(0.3, -1 * 0.3);
     * 
     * } else {
     * robotDrive.tankDrive(0, 0);
     * }
     * }
     */

    /*
     * 
     * 
     * double roundedTimer = Math.floor(timer.get());
     * 
     * boolean isEven = roundedTimer % 2 == 0;
     * System.out.println(leftLeader.get());
     * System.out.println(rightLeader.get());
     * 
     * if(isEven) {
     * 
     * //System.out.println("start motor");
     * 
     * robotDrive.arcadeDrive(motorSpeed, 0);
     * 
     * } else {
     * 
     * //System.out.println("Stop Motors");
     * 
     * robotDrive.arcadeDrive(0, 0);
     * //System.out.println("AutoPeriod");
     * 
     * 
     * if(donut) {
     * 
     * robotDrive.tankDrive(motorSpeed, -1 * motorSpeed);
     * 
     * } else {
     * robotDrive.tankDrive(motorSpeed, motorSpeed);
     * }
     */

    /** This function is called once when teleop is enabled. */
    @Override
    public void teleopInit() {
    }

    /** This function is called periodically during operator control. */
    @Override
    public void teleopPeriodic() {
        robotDrive.arcadeDrive(controller.getLeftY(), controller.getRightX());
    }

    /** This function is called once when the robot is disabled. */
    @Override
    public void disabledInit() {
    }

    /** This function is called periodically when disabled. */
    @Override
    public void disabledPeriodic() {
    }

    /** This function is called once when test mode is enabled. */
    @Override
    public void testInit() {
    }

    /** This function is called periodically during test mode. */
    @Override
    public void testPeriodic() {
    }

    /** This function is called once when the robot is first started up. */
    @Override
    public void simulationInit() {
    }

    /** This function is called periodically whilst in simulation. */
    @Override
    public void simulationPeriodic() {
    }

}
