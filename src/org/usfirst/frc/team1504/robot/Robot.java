package org.usfirst.frc.team1504.robot;

import java.util.Arrays;

import org.usfirst.frc.team1504.robot.Arduino.FRONTSIDE_MODE;
import org.usfirst.frc.team1504.robot.Arduino.GEAR_MODE;
import org.usfirst.frc.team1504.robot.Arduino.INTAKE_LIGHT_MODE;
import org.usfirst.frc.team1504.robot.Arduino.PARTY_MODE;
import org.usfirst.frc.team1504.robot.Arduino.SHOOTER_STATUS;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import edu.wpi.first.wpilibj.DriverStation;
//import java.util.Base64;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
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

	private Digit_Board _db = Digit_Board.getInstance();
	private DriverStation _ds = DriverStation.getInstance();
	private Drive _drive = Drive.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
	private Autonomous _autonomous = Autonomous.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	//private Pickup _pickup = Pickup.getInstance();
	//private Lift _lift = Lift.getInstance();
	//private Navx _navx = Navx.getInstance();
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
    	//Pickup.initialize();
    	//Lift.initialize();
    	//CameraServer.getInstance().startAutomaticCapture();
    	System.out.println(_ds.getGameSpecificMessage()); 
    	//RRL - Right side switch (closer), Right side scale, Left side switch (farther)
    	//System.out.println(new String(Base64.getDecoder().decode(Map.TEAM_BANNER)));
    	String message = _ds.getGameSpecificMessage();
    	/*String[] game_message;
    	game_message[0] = message.substring(0, 1);
    	game_message[1] = message.substring(1, 2);
    	game_message[2] = message.substring(2, 3);*/
    	
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
				_arduino.setPartyMode(PARTY_MODE.ON);
				char edge_track = 0;
				PowerDistributionPanel pdp = new PowerDistributionPanel();
				while(true)
				{	
					SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					SmartDashboard.putNumber("Robot Voltage", m_ds.getBatteryVoltage());
					SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
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
        _arduino.setPartyMode(PARTY_MODE.ON);
        _arduino.setGearLights(GEAR_MODE.PULSE);
        _arduino.setPulseSpeed(1);
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
    	_arduino.setPulseSpeed(4);
        _arduino.setPartyMode(PARTY_MODE.OFF);
        if (_ds.getAlliance() == DriverStation.Alliance.Blue)
        	_arduino.setMainLightsColor(0, 255, 0);
        else if (_ds.getAlliance() == DriverStation.Alliance.Red)
        	_arduino.setMainLightsColor(0, 0, 255);
        else
        	_arduino.setMainLightsColor(255, 0, 0);
    	_arduino.setGearLights(GEAR_MODE.INDIVIDUAL_INTENSITY, 0.5, 0.5);
    }

    /**
     * Test code should go here.
     * Users should add test code to this method that should run while the robot is in test mode.
     */
    
    
    public void test() {
    	System.out.println("Test Mode!");
    	DoubleSolenoid _piston1 = new DoubleSolenoid(0, 1);
    	WPI_TalonSRX _motor = new WPI_TalonSRX(Map.ARM_TALON_PORT);
		Latch_Joystick control = new Latch_Joystick(0);
		double magic = 1.0;
//    	CameraInterface ci = CameraInterface.getInstance();
    	//ci.set_mode(CameraInterface.CAMERA_MODE.MULTI);
    	//ci.set_mode(CameraInterface.CAMERA_MODE.SINGLE); 4 or 5
    	while (isTest() && isEnabled())
    	{
    		
    		if(control.getRawButton(1)) {
    			_piston1.set(DoubleSolenoid.Value.kForward);
    		} else if (control.getRawButton(2)) {
    			_piston1.set(DoubleSolenoid.Value.kReverse);
    		}
    		
    		if(control.getRawButton(1)){
    			magic = 1.0;
    		} else{
    			magic = 2.0;
    		}
    		if (control.getRawButton(4)){
    			_motor.set(control.getRawAxis(1)/magic*-1.0);
    		}
    		else if (control.getRawButton(5)){
    			_motor.set(control.getRawAxis(1)/magic);
    		}
    		else{
        		_motor.set(control.getRawAxis(1)/magic);
    		}
    		
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
                
                String message = _ds.getGameSpecificMessage();
                char left = 'L';
                char right = 'R';
                char starting_position = 'E';
                
                char[] new_message = message.toCharArray();
                
                if (new_message[1] == left && starting_position == left)
                { 		
                	_autonomous.setup_path(Map.AUTON_LEFT_SCALE_SEQUENCES);
                }
                else if (new_message[1] == right && starting_position == right)
                {
                	_autonomous.setup_path(Map.AUTON_RIGHT_SCALE_SEQUENCES);
                }
                else if (new_message[0] == 0)
                {
                	
                }
                else
                {
                	// We must be in the middle so don't do anything unless we are net setting what to run in auton somewhere else
                }
                
                if(_db.pos%3 == 0)
                {	
                	//Move forward, turn left,
                	_autonomous.setup_path(new double[][] {{0.25, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.25, 0, 2000}, /* Method implementation goes here */{0, 0.0, -0.25, 0, 2000}, {0.25, 0.0, 0, 0, 2000}, /* Method implementation goes here */{0.25, 0.0, 0, 0, 2000}});
                	starting_position = 'L';
                }
                else if (_db.pos%3 == 1)
                {
                	//Move forward, turn left, move forward
	                _autonomous.setup_path(new double[][] {{0.25, 0.0, 0.0, 0, 3000}, {0.0, 0.0, 0.25, 0, 4200}, { 0.25, 0.0, 0.0, 0, 6200}});
	                starting_position = 'M';
                }
                else if (_db.pos%3 == 2)
                {
                	//Move forward
	                _autonomous.setup_path(new double[][] {{0.25, 0.0, 0.0, 0, 4000}});
                	starting_position = 'R';
                }
	            
                _autonomous.start();
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
                	m_ds.waitForData(150); // Blocks until we get new data or 150ms elapse
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