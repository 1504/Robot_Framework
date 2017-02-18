package org.usfirst.frc.team1504.robot;

import java.util.Arrays;

import org.usfirst.frc.team1504.robot.Arduino.FRONTSIDE_MODE;
import org.usfirst.frc.team1504.robot.Arduino.GEAR_MODE;
import org.usfirst.frc.team1504.robot.Arduino.INTAKE_LIGHT_MODE;
import org.usfirst.frc.team1504.robot.Arduino.SHOOTER_STATUS;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import edu.wpi.first.wpilibj.DriverStation;
//import java.util.Base64;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.HALUtil;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {

	private Digit_Board _digit_board = Digit_Board.getInstance();
	private DriverStation _ds = DriverStation.getInstance();
	private Drive _drive = Drive.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
//	private Autonomous _autonomous = Autonomous.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	//private Navx _navx = Navx.getInstance();
	private Winch _winch = Winch.getInstance();
//	private CameraInterface ci = CameraInterface.getInstance();
	//private Vision _vision = Vision.getInstance();
	//Pneumatics t3 = Pneumatics.getInstance();
	private Thread _dashboard_task;
	
    /**
     * Create a new Robot
     */
    public Robot() {
    	super();
    	Drive.initialize();
    	DigitBoard.initialize();
    	Digit_Board.initialize();
    	Intake.initialize(); 
//    	Autonomous.initialize();
    	Shooter.initialize();
    	//System.out.println(new String(Base64.getDecoder().decode(Map.TEAM_BANNER)));
    }

    /**
     * Robot-wide initialization code should go here.
     *
     * Users should override this method for default Robot-wide initialization which will
     * be called when the robot is first powered on.
     *
     * Called exactly 1 time when the competition starts.
     */
    protected void robotInit() {
    	_dashboard_task = new Thread(new Runnable() {
			public void run() {
				char edge_track = 0;
				PowerDistributionPanel pdp = new PowerDistributionPanel();
				char[] testImages = new char[20];
				Arrays.fill(testImages, 'a');
				while(true)
				{	
					SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					SmartDashboard.putNumber("Robot Voltage", m_ds.getBatteryVoltage());
					SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					SmartDashboard.putString("Groundtruth raw image", new String(testImages));
					/*
					 * Borrowed from Mike
					 */	
					edge_track = (char)( ( (edge_track << 1) + (HALUtil.getFPGAButton() ? 1 : 0) ) & 3);
					if(edge_track == 1) // Get image from groundtruth sensors, output it to the DS
					{
						SmartDashboard.putString("Groundtruth raw image", new String(_arduino.getSensorImage()));
					}
						Timer.delay(0.5);
				}
			}
		});
    	_dashboard_task.start();
    	
    	//System.out.println(new String(Base64.getDecoder().decode(Map.ROBOT_BANNER)));
        System.out.println("Babbage Initialized ( robotInit() ) @ " + IO.ROBOT_START_TIME);
    }

    /**
     * Disabled should go here.
     * Users should overload this method to run code that should run while the field is
     * disabled.
     *
     * Called once each time the robot enters the disabled state.
     */
    protected void disabled() {
        System.out.println("Robot Disabled");
        if (_ds.getAlliance() == DriverStation.Alliance.Blue)
        	_arduino.setMainLightsColor(0, 0, 255);
        else if (_ds.getAlliance() == DriverStation.Alliance.Red)
        	_arduino.setMainLightsColor(255, 0, 0);
        else
        	_arduino.setMainLightsColor(0, 255, 0);
        _arduino.setPulseSpeed(1);
        _arduino.setGearLights(GEAR_MODE.PULSE);
    }

    /**
     * Autonomous should go here.
     * Users should add autonomous code to this method that should run while the field is
     * in the autonomous period.
     *
     * Called once each time the robot enters the autonomous state.
     */
    public void autonomous() {
    	System.out.println("Autonomous mode");
    }

    /**
     * Operator control (tele-operated) code should go here.
     * Users should add Operator Control code to this method that should run while the field is
     * in the Operator Control (tele-operated) period.
     *
     * Called once each time the robot enters the operator-controlled state.
     */
    public void operatorControl() {
    	System.out.println("Operator Control");
    	_arduino.setGearLights(GEAR_MODE.INDIVIDUAL_INTENSITY, .50, .50);
    }

    /**
     * Test code should go here.
     * Users should add test code to this method that should run while the robot is in test mode.
     */
    
    public void test()
    {
    	System.out.println("Test Mode!");
    	_arduino.setPulseSpeed(10);
    	_arduino.setGearLights(GEAR_MODE.PULSE);
//    	CameraInterface ci = CameraInterface.getInstance();
    	//ci.set_mode(CameraInterface.CAMERA_MODE.MULTI);
    	//ci.set_mode(CameraInterface.CAMERA_MODE.SINGLE);
    	while (isTest() && isEnabled())
    	{
    		// Switch camera views every 5 seconds like a pro
//    		ci.set_active_camera(ci.get_active_camera() == CameraInterface.CAMERAS.GEARSIDE ? CameraInterface.CAMERAS.INTAKESIDE : CameraInterface.CAMERAS.GEARSIDE);
//            System.out.println("Switching active camera to " + ci.get_active_camera().toString());
//            Timer.delay(5);
    	}
    }

    /**
     * Start a competition.
     * This code tracks the order of the field starting to ensure that everything happens
     * in the right order. Repeatedly run the correct method, either Autonomous or OperatorControl
     * when the robot is enabled. After running the correct method, wait for some state to change,
     * either the other mode starts or the robot is disabled. Then go back and wait for the robot
     * to be enabled again.
     */
    public void startCompetition() {
        HAL.report(tResourceType.kResourceType_Framework,tInstances.kFramework_Simple);

        // first and one-time initialization
        LiveWindow.setEnabled(false);
        robotInit();

        HAL.observeUserProgramStarting();
        while (true) {
            if (isDisabled()) {
                m_ds.InDisabled(true);
                disabled();
                while (isDisabled())
                    Timer.delay(0.01);
                m_ds.InDisabled(false);
            } else if (isAutonomous()) {
                m_ds.InAutonomous(true);
                _logger.start("Auto");
                
                autonomous();
                
                while (isAutonomous() && !isDisabled()) {
                	m_ds.waitForData(150);
                	_semaphore.newData();
                }
                
                _logger.stop();
                m_ds.InAutonomous(false);
            
            } else if (isTest()) {
                //LiveWindow.setEnabled(true);
                m_ds.InTest(true);
                
                test();
                
                while (isTest() && isEnabled())
                    Timer.delay(0.01);
                
                m_ds.InTest(false);
                //LiveWindow.setEnabled(false);
            
            } else {
                m_ds.InOperatorControl(true);
                _logger.start("Tele");
                
                operatorControl();
                
                while (isOperatorControl() && !isDisabled()) {
                	m_ds.waitForData(150); // Blocks until we get new datas or 150ms elapse
                	_semaphore.newData();
                    //Timer.delay(0.01);
                }
                
                _logger.stop();
                //_winch.set_deployed(false);
                //Timer.delay(1);
                m_ds.InOperatorControl(false);
            }
        } /* while loop */
    }
}