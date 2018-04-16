package org.usfirst.frc.team1504.robot;
//
import java.util.Arrays;

import org.usfirst.frc.team1504.robot.Arduino.FRONTSIDE_MODE;
import org.usfirst.frc.team1504.robot.Arduino.GEAR_MODE;
import org.usfirst.frc.team1504.robot.Arduino.INTAKE_LIGHT_MODE;
import org.usfirst.frc.team1504.robot.Arduino.PARTY_MODE;
import org.usfirst.frc.team1504.robot.Arduino.SHOOTER_STATUS;
import edu.wpi.first.networktables.*;
import com.analog.adis16448.frc.ADIS16448_IMU;
import java.util.HashMap;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import edu.wpi.first.wpilibj.DriverStation;
//import java.util.Base64;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.hal.HALUtil;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {

	public static double left_x;
	public static double left_y;
	
	public static double right_x;
	public static double right_y;
	
	public static enum v_or_c {VISIONCODE, CONTINGENCY};
	public static v_or_c vorc_state;
	
	private Digit_Board _db = Digit_Board.getInstance();
	private DriverStation _ds = DriverStation.getInstance();
	private Drive _drive = Drive.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
	private Autonomous _autonomous = Autonomous.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	private Pickup _pickup = Pickup.getInstance();
	private Winch _winch = Winch.getInstance();
	
	private HashMap<String, double[][]> map = new HashMap<String, double[][]>();
	private SendableChooser<String> pos = new SendableChooser<String>();
	private SendableChooser<String> autoChooser1 = new SendableChooser<String>();
	//private Lift _lift = Lift.getInstance();
	//private Navx _navx = Navx.getInstance();
//	//private CameraInterface ci = CameraInterface.getInstance();
	//private Vision _vision = Vision.getInstance();
	//Pneumatics t3 = Pneumatics.getInstance();
	private Thread _dashboard_task;
	
    /**
     * Create a new Robot
     */
    public Robot() {
    	super();
    	CameraServer.getInstance().startAutomaticCapture();
		CameraServer.getInstance();
    	Drive.initialize();
    	DigitBoard.initialize();
    	Digit_Board.initialize();
    	//Pickup.initialize();
    	//Lift.initialize();
 //   	//CameraServer.getInstance().startAutomaticCapture();
    	System.out.println("game specific message:"+_ds.getGameSpecificMessage()); 
    	//RRL - Right side switch (closer), Right side scale, Left side switch (farther)
    	//System.out.println(new String(Base64.getDecoder().decode(Map.TEAM_BANNER)));
    	
    	
    	// "TYPE-OF-AUTON_START_END"   	 	
    	map.put("LeftSwitchL", Map.FORWARD_SHOOT_SEQUENCES);
    	map.put("LeftSwitchR", Map.FORWARD_SEQUENCE);
    	map.put("MidSwitchL", Map.LEFT_SWITCH_FROM_MID_SEQUENCES);
    	map.put("MidSwitchR", Map.RIGHT_SWITCH_FROM_MID_SEQUENCES);
    	map.put("RightSwitchL", Map.FORWARD_SEQUENCE);
    	map.put("RightSwitchR", Map.FORWARD_SHOOT_SEQUENCES);
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
				Compressor c = new Compressor(0);
				SmartDashboard.putNumber("Auton Delay", 0.0);
				
				pos.addDefault("Left", new String("Left"));
				pos.addObject("Mid", new String("Mid"));
				pos.addObject("Right", new String("Right"));
				
				
				autoChooser1.addDefault("Switch", new String("Switch"));
				autoChooser1.addObject("+ Spot", new String("Spot"));
				autoChooser1.addObject("Switch & Block", new String("Block"));
				autoChooser1.addObject("Go Forward", new String("GoForward"));
				autoChooser1.addObject("Side Delivery", new String("SideDelivery"));
				//autoChooser1.addObject("Switch & Scale", new String("SwitchScale"));
				//autoChooser1.addObject("Switch & Exchange", new String("SwitchExchange"));
				
				
				SmartDashboard.putData("Position Chooser", pos);
				SmartDashboard.putData("Auton Mode Chooser", autoChooser1);
				
				AnalogInput pressure_1 = new AnalogInput(0);
				AnalogInput pressure_2 = new AnalogInput(1);
				while(true)
				{	
					SmartDashboard.putNumber("Robot Voltage", RobotController.getBatteryVoltage());
					SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					SmartDashboard.putNumber("Arm Power", _pickup.getPower());
					SmartDashboard.putNumber("Pressure High", pressure_1.getAverageVoltage()*50 - 25);
					SmartDashboard.putNumber("Pressure Low", pressure_2.getAverageVoltage()*50 - 25);					
					SmartDashboard.putBoolean("Pressure", c.getPressureSwitchValue());
					SmartDashboard.putNumber("Pressure", c.getCompressorCurrent());
					
					//SmartDashbaord.putNumber("", );
					
					
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
    	System.out.println("hey now you're ");
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
    	while (isTest() && isEnabled())
    	{
    		
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
                System.out.println("hey now");
                
                String message = _ds.getGameSpecificMessage();
                
                char[] game_message = message.toCharArray();
                /*
                if(new_message.length == 3)
                {
                if (new_message[0] == 'L')
                {
                	if(vorc_state == v_or_c.VISIONCODE)
                	{
                	//left_x = xRects[0];
                	//left_y = yRects[0];
                	}
                	if(vorc_state == v_or_c.CONTINGENCY)
                	{
                		//
                	}
                }
                else if (new_message[0] == 'R')
                {
                	if(vorc_state == v_or_c.VISIONCODE)
                	{
                	//right_x = xRects[1];
                	//right_y = yRects[1];
                	}
                	if(vorc_state == v_or_c.CONTINGENCY)
                	{
                		//
                	}
                }
                else
                {
                	// We must be in the middle so don't do anything unless we are net setting what to run in auton somewhere else
                }
            }
                */
                
                
                String seq = pos.getSelected() +  "Switch" + game_message[0];
                if (autoChooser1.getSelected().equals("SwitchScale"))
                	seq += game_message[1];
                
                double[][] path;
                System.out.println(pos.getSelected());
                System.out.println(game_message[0]);
                System.out.println(pos.getSelected() == "Left");
                System.out.println(game_message[0] == 'L');
                path = map.get(pos.getSelected() + "Switch" + game_message[0]);
                
                path = map.get(seq);
                
                if(game_message[0] == 'L')
                	Map.DIRECTIONAL_MULTIPLIER = -1.0;
                else
                	Map.DIRECTIONAL_MULTIPLIER = 1.0;
          
                double [][] auton_delay = new double[][] {{0.0, 0.0, 0.0, 0, SmartDashboard.getNumber("Auton Delay", 0.0) * 1000}};
                path = _autonomous.build_auton(auton_delay, path);
               
                System.out.println(autoChooser1.getSelected());
                if (autoChooser1.getSelected().equals("Spot") || autoChooser1.getSelected().equals("Block"))
                	path = _autonomous.build_auton(path, Map.get_return_to_spot_sequence(Map.DIRECTIONAL_MULTIPLIER));
                if (autoChooser1.getSelected().equals("Block"))
                	path = _autonomous.build_auton(path, Map.PICKUP_FROM_SPOT);
                if (autoChooser1.getSelected().equals("GoForward"))
                	path = Map.FORWARD_SEQUENCE;
                
                if ((autoChooser1.getSelected().equals("SideDelivery") && (game_message[0] == 'R') && pos.getSelected().equals("Right")) || ((autoChooser1.getSelected().equals("SideDelivery") && (game_message[0] == 'L') && pos.getSelected().equals("Left"))))
                	path = _autonomous.build_auton(Map.FORWARD_SEQUENCE, Map.get_side_delivery_sequence(Map.DIRECTIONAL_MULTIPLIER));
                else if ((autoChooser1.getSelected().equals("SideDelivery") && (pos.getSelected().equals("Right"))) || (autoChooser1.getSelected().equals("SideDelivery")) && (pos.getSelected().equals("Left")))
                	path = Map.FORWARD_SEQUENCE;
                
                _autonomous.setup_path(path);
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