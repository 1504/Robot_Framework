package org.usfirst.frc1504.Robot2019;
//
import org.usfirst.frc1504.Robot2019.Arduino.GEAR_MODE;
import org.usfirst.frc1504.Robot2019.Arduino.PARTY_MODE;
import edu.wpi.first.wpilibj.command.Scheduler;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
	
	public static double left_x;
	public static double left_y;
	
	public static double right_x;
	public static double right_y;
	
	public static enum v_or_c {VISIONCODE, CONTINGENCY};
	public static v_or_c vorc_state;
	
	private DriverStation _ds = DriverStation.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	
	private SendableChooser<String> pos = new SendableChooser<String>();
	private SendableChooser<String> autoChooser1 = new SendableChooser<String>();
	
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
    	System.out.println("game specific message:"+_ds.getGameSpecificMessage()); 
    }

    /**
     * Robot-wide initialization code should go here.
     *
     * Users should override this method for default Robot-wide initialization which will
     * be called when the robot is first powered on.
     *
     * Called exactly 1 time when the competition starts.
     */
    public void robotInit() {
    	_dashboard_task = new Thread(new Runnable() {
			public void run() {
				
				_arduino.setPartyMode(PARTY_MODE.ON);
				//PowerDistributionPanel pdp = new PowerDistributionPanel();
				//Compressor c = new Compressor(0);
				SmartDashboard.putNumber("Auton Delay", 0.0);
								
				SmartDashboard.putData("Position Chooser", pos);
				SmartDashboard.putData("Auton Mode Chooser", autoChooser1);
				
				SmartDashboard.putBoolean("Sensor 1", Auto_Alignment.sensor1.get());
				SmartDashboard.putBoolean("Sensor 2", Auto_Alignment.sensor2.get());
				SmartDashboard.putBoolean("Sensor 3", Auto_Alignment.sensor3.get());
				SmartDashboard.putBoolean("Sensor 4", Auto_Alignment.sensor4.get());
				SmartDashboard.putBoolean("Sensor 5", Auto_Alignment.sensor5.get());
				SmartDashboard.putBoolean("Sensor 6", Auto_Alignment.sensor6.get());
				
				SmartDashboard.putBoolean("Good Configuration", Auto_Alignment.check_sensors());
				while(true)
				{	

					//SmartDashboard.putNumber("Robot Voltage", RobotController.getBatteryVoltage());
					//SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					//SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					//SmartDashboard.putNumber("Arm Power", _pickup.getPower());
					//SmartDashboard.putNumber("Pressure High", pressure_1.getAverageVoltage()*50 - 25);
					//SmartDashboard.putNumber("Pressure Low", pressure_2.getAverageVoltage()*50 - 25);
					
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
					
					//SmartDashboard.putBoolean("Pressure", c.getPressureSwitchValue());
					//SmartDashboard.putNumber("Pressure", c.getCompressorCurrent());
					
					//SmartDashbaord.putNumber("", );
					
					
					/*
					 * Borrowed from Mike
					*/	
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
	
    @Override
    public void teleopPeriodic() {
		Scheduler.getInstance().run();
		if (isOperatorControl() && !isDisabled()) {
			m_ds.waitForData(150); // Blocks until we get new data or 150ms elapse
			_semaphore.newData();
			//Timer.delay(0.01);
		}
    }
}