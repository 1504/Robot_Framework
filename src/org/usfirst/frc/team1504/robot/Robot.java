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
    	map.put("LeftSwitchR", Map.RIGHT_SWITCH_FROM_LEFT_SEQUENCES);
    	map.put("MidSwitchL", Map.LEFT_SWITCH_FROM_MID_SEQUENCES);
    	map.put("MidSwitchR", Map.RIGHT_SWITCH_FROM_MID_SEQUENCES);
    	map.put("RightSwitchL", Map.LEFT_SWITCH_FROM_RIGHT_SEQUENCES);
    	map.put("RightSwitchR", Map.FORWARD_SHOOT_SEQUENCES);
    	
    	/*
    	map.put("LeftSwitchBlockL", _autonomous.build_auton(Map.FORWARD_SHOOT_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	map.put("LeftSwitchBlockR", _autonomous.build_auton(Map.RIGHT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	map.put("MidSwitchBlockL", _autonomous.build_auton(Map.LEFT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	map.put("MidSwitchBlockR", _autonomous.build_auton(Map.RIGHT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	map.put("RightSwitchBlockL", _autonomous.build_auton(Map.LEFT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	map.put("RightSwitchBlockR", _autonomous.build_auton(Map.FORWARD_SHOOT_SEQUENCES, Map.PICKUP_FROM_SPOT));
    	*/
    	
    	/*map.put("LeftSwitchScaleLL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("LeftSwitchScaleLR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	map.put("LeftSwitchScaleRL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("LeftSwitchScaleRR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	map.put("MidSwitchScaleLL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("MidSwitchScaleLR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	map.put("MidSwitchScaleRL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("MidSwitchScaleRR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	map.put("RightSwitchScaleLL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("RightSwitchScaleLR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	map.put("RightSwitchScaleRL", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.LEFT_SCALE_FROM_SPOT));
    	map.put("RightSwitchScaleRR", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.RIGHT_SCALE_FROM_SPOT));
    	
    	map.put("LeftSwitchExchangeLE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	map.put("LeftSwitchExchangeRE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_LEFT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	map.put("MidSwitchExchangeLE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	map.put("MidSwitchExchangeRE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_MID_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	map.put("RightSwitchExchangeLE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_LEFT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	map.put("RightSwitchExchangeRE", _autonomous.build_auton(_autonomous.build_auton(Map.CONTINGENCY_RIGHT_SWITCH_FROM_RIGHT_SEQUENCES, Map.PICKUP_FROM_SPOT), Map.AUTON_EXCHANGE_FROM_SPOT));
    	
    	map.put("LeftExchangeEE", Map.AUTON_EXCHANGE_FROM_LEFT_SEQUENCES);
    	map.put("MidExchangeEE", Map.AUTON_EXCHANGE_FROM_MID_SEQUENCES);
    	map.put("RightExchangeEE", Map.AUTON_EXCHANGE_FROM_RIGHT_SEQUENCES);
    	System.out.println(map.get("Contingencies"));
    	*/
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
				autoChooser1.addObject("Switch & Block", new String("SwitchBlock"));
				//autoChooser1.addObject("Switch & Scale", new String("SwitchScale"));
				//autoChooser1.addObject("Switch & Exchange", new String("SwitchExchange"));
				
				
				SmartDashboard.putData("Position Chooser", pos);
				SmartDashboard.putData("Auton Mode Chooser", autoChooser1);
				
				AnalogInput pressure_1 = new AnalogInput(0);
				AnalogInput pressure_2 = new AnalogInput(1);
				Preferences prefs = Preferences.getInstance();
				Map.ARM_UP_ANGLE = prefs.getInt("Arm Up Angle", 0);
				Map.ARM_DOWN_ANGLE = prefs.getInt("Arm Down Angle", 0);
				Map.ARM_MID_ANGLE = prefs.getInt("Arm Mid Angle", 0);
				while(true)
				{	
					SmartDashboard.putNumber("Robot Voltage", RobotController.getBatteryVoltage());
					SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					SmartDashboard.putNumber("Arm Power", _pickup.getPower());
					SmartDashboard.putNumber("Distance (ft)", _drive.sanic.getAverageValue());
					SmartDashboard.putNumber("Pressure 1", pressure_1.getValue());
					SmartDashboard.putNumber("Pressure 2", pressure_2.getValue());
					
					
					
					/*
					SmartDashboard.putNumber("PDP Current: Channel 0", pdp.getCurrent(0));
					SmartDashboard.putNumber("PDP Current: Channel 1", pdp.getCurrent(1));
					SmartDashboard.putNumber("PDP Current: Channel 2", pdp.getCurrent(2));
					SmartDashboard.putNumber("PDP Current: Channel 3", pdp.getCurrent(3));
					
					SmartDashboard.putNumber("PDP Current: Channel 10", pdp.getCurrent(10));
					SmartDashboard.putNumber("PDP Current: Channel 11", pdp.getCurrent(11));
					SmartDashboard.putNumber("PDP Current: Channel 12", pdp.getCurrent(12));
					SmartDashboard.putNumber("PDP Current: Channel 13", pdp.getCurrent(13));
					SmartDashboard.putNumber("PDP Current: Channel 14", pdp.getCurrent(14));
					SmartDashboard.putNumber("PDP Current: Channel 15", pdp.getCurrent(15));
					*/
					
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
    	//ADIS16448_IMU imu = new ADIS16448_IMU();
    	/*DoubleSolenoid _piston1 = new DoubleSolenoid(0, 1);
    	WPI_TalonSRX _motor = new WPI_TalonSRX(Map.ARM_TALON_PORT);
		Latch_Joystick control = new Latch_Joystick(0);
		double magic = 1.0;*/
//    	CameraInterface ci = CameraInterface.getInstance();
    	//ci.set_mode(CameraInterface.CAMERA_MODE.MULTI);
    	//ci.set_mode(CameraInterface.CAMERA_MODE.SINGLE); 4 or 5
    	//BuiltInAccelerometer accel = new BuiltInAccelerometer();
    	WPI_TalonSRX _grab_left;
		WPI_TalonSRX _grab_right;
		_grab_left = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
		_grab_right = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
		Latch_Joystick _secondary = new Latch_Joystick(Map.DRIVE_SECONDARY_JOYSTICK);
    	while (isTest() && isEnabled())
    	{
    		double speed = _secondary.getRawAxis(Map.INTAKE_POWER_AXIS);
    		_grab_left.set(speed);
    		_grab_right.set(-speed);
    		
    		//System.out.println("Gyro-X"+accel.getX());
    		/*System.out.println("Gyro-X"+imu.getAngleX());
    	    System.out.println("Gyro-Y"+imu.getAngleY());
    	    System.out.println("Gyro-Z"+imu.getAngleZ());
    	    
    	    System.out.println("Accel-X"+imu.getAccelX());
    	    System.out.println("Accel-Y"+imu.getAccelY());
    	    System.out.println("Accel-Z"+imu.getAccelZ());
    	    
    	    System.out.println("Pitch"+imu.getPitch());
    	    System.out.println("Roll"+imu.getRoll());
    	    System.out.println("Yaw"+imu.getYaw());
    	    
    	    System.out.println("Pressure: "+imu.getBarometricPressure());
    	    System.out.println("Temperature: "+imu.getTemperature()); */
    	    //Timer.delay(1000);
    		//System.out.println("Test Mode!");
    		/*if(control.getRawButton(1)) {
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
    		}*/
    		
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
        //NetworkTable table;
        //table = NetworkTableInstance.getDefault().getTable("GRIP/switch");
        //double[] xRects = table.getEntry("centerX").getDoubleArray(new double[] {2, 3});
        //double[] yRects = table.getEntry("centerY").getDoubleArray(new double[] {2, 3});
        //if(xRects.length == 1)
        //	xRects = new double[] {2, 3};
        //if(yRects.length == 1)
        //	yRects = new double[] {2, 3};
        //double[] angles = _autonomous.switch_angles(xRects[0], xRects[1], 
        //											yRects[0], yRects[1]);
        //double[] arr = _drive.follow_angle(angles[0], .2);
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
                
                char[] new_message = message.toCharArray();
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
                
                
                String seq = pos.getSelected() +  autoChooser1.getSelected() + new_message[0];
                if (autoChooser1.getSelected() == "SwitchScale")
                {
                	seq = seq + new_message[1];
                }
                

               // double [][] path = _autonomous.build_auton(new double[][][]{map.get(seq)});
                //System.out.println(seq);
                
                double[][] path;
                System.out.println(pos.getSelected());
                System.out.println(new_message[0]);
                System.out.println(pos.getSelected() == "Left");
                System.out.println(new_message[0] == 'L');
                
                path = map.get(seq);
                
                if(new_message[0] == 'L')
                	Map.RETURN_TO_CENTER_DIRECTION = -1.0;
                else
                	Map.RETURN_TO_CENTER_DIRECTION = 1.0;
                	
                if(pos.getSelected().toCharArray()[0] == new_message[0])
                	path = Map.FORWARD_SHOOT_SEQUENCES;
                else
                	path = Map.FORWARD_SEQUENCE;
                
                if (autoChooser1.getSelected() == "Spot")
                {
                	path = _autonomous.build_auton(map.get(seq), Map.RETURN_TO_CENTER);
                }
                
                //double [][] path = map.get(seq);
                //double [][] auton_delay = new double[][] {{0.0, 0.0, 0.0, 0, SmartDashboard.getNumber("Auton Delay", 0.0)}};
                // double [][] path = {{0, (Map.AUTON_DEFAULT_SPEED), 0.0, 11, Map.AUTON_RUNTIME}};
                //_autonomous.setup_path(_autonomous.build_auton(new double[][][]{auton_delay, path}));
                //_autonomous.setup_path(_autonomous.build_auton(auton_delay, path);
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