package org.usfirst.frc1504.Robot2019;

import edu.wpi.first.networktables.*;
import java.util.HashMap;
import edu.wpi.first.wpilibj.command.Scheduler;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALUtil;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import edu.wpi.first.wpilibj.DriverStation;
//import java.util.Base64;

//-import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
//-import edu.wpi.first.wpilibj.hal.HAL;
//-import edu.wpi.first.wpilibj.hal.HALUtil;
//-import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances;
//-import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {
	
	public static double left_x;
	public static double left_y;
	
	public static double right_x;
	public static double right_y;
	
	private Digit_Board _db = Digit_Board.getInstance();
	private DriverStation _ds = DriverStation.getInstance();
	private Drive _drive = Drive.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
	private Arduino _arduino = Arduino.getInstance();
	private Elevator _elevator = Elevator.getInstance();
	
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
    	Drive.initialize();
    	DigitBoard.initialize();
    	Digit_Board.initialize();
    	Elevator.initialize();
		Lift.initialize();
		Hatch.initialize();
		Cargo.initialize();
		Arduino.initialize();
		 //   	//CameraServer.getInstance().startAutomaticCapture();
    	//System.out.println("Game specific message: "+_ds.getGameSpecificMessage()); 
    	//RRL - Right side switch (closer), Right side scale, Left side switch (farther)
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
    public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture();
    	_dashboard_task = new Thread(new Runnable() {
			public void run() {
				_arduino.setPartyMode(true);
				char edge_track = 0;
				PowerDistributionPanel pdp = new PowerDistributionPanel();

				AnalogInput pressure_1 = new AnalogInput(4);
				AnalogInput pressure_2 = new AnalogInput(5);
				//Compressor c = new Compressor(0);
				
				/*SmartDashboard.putNumber("Auton Delay", 0.0);
				
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
				SmartDashboard.putData("Auton Mode Chooser", autoChooser1);*/
				
				
				
				
				while(true)
				{	
					//System.out.println("firstPotentiometer: " + Elevator.firstPotentiometer.get());
					//System.out.println("secondPotentiometer: " + Elevator.secondPotentiometer.get());
					//System.out.println("Distance From Ball: " + Auto_Alignment.ai.getValue());

					//SmartDashboard.putNumber("Robot Voltage", RobotController.getBatteryVoltage());
					//SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					//SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					//SmartDashboard.putNumber("Arm Power", _pickup.getPower());
					SmartDashboard.putNumber("Pressure High", pressure_1.getAverageVoltage()*50 - 25);
					SmartDashboard.putNumber("Pressure Low", pressure_2.getAverageVoltage()*50 - 25);
					
					/*SmartDashboard.putBoolean("Alignment Good Configuration", Auto_Alignment.check_sensors());
					SmartDashboard.putBoolean("Alignment Sensor 1", !Auto_Alignment.sensor1.get());
					SmartDashboard.putBoolean("Alignment Sensor 2", !Auto_Alignment.sensor2.get());
					SmartDashboard.putBoolean("Alignment Sensor 3", !Auto_Alignment.sensor3.get());
					SmartDashboard.putBoolean("Alignment Sensor 4", !Auto_Alignment.sensor4.get());
					SmartDashboard.putBoolean("Alignment Sensor 5", !Auto_Alignment.sensor5.get());
					SmartDashboard.putBoolean("Alignment Sensor 6", !Auto_Alignment.sensor6.get());*/
					//SmartDashboard.putBoolean("Hatch Indicator", Arms.grabstate);

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
										
					
					/*
					 * Borrowed from Mike
					 */	
					edge_track = (char)( ( (edge_track << 1) + (HALUtil.getFPGAButton() ? 1 : 0) ) & 3);
					if(edge_track == 1) // Get image from groundtruth sensors, output it to the DS
					{
						//SmartDashboard.putString("Groundtruth raw image", new String(_arduino.getSensorImage()));
						
						_arduino.diagnostic(!_arduino.diagnostic());
					}
					
					Timer.delay(0.02);
				}
			}
		});
    	_dashboard_task.start();
    	
    	//System.out.println(new String(Base64.getDecoder().decode(Map.ROBOT_BANNER)));
        System.out.println("Pathfinder Initialized ( robotInit() ) @ " + IO.ROBOT_START_TIME);
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
        _arduino.setPartyMode(true);
		_arduino.setPulseSpeed(1);
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
		_arduino.diagnostic(false);
		_arduino.update(true);
    	_arduino.setPulseSpeed(20);
        _arduino.setPartyMode(false);
    }

    /**
     * Test code should go here.
     * Users should add test code to this method that should run while the robot is in test mode.
     */
    public void test() {
		System.out.println("Test Mode!");

    	while (isTest() && isEnabled())
    	{
			Elevator.getInstance().set(Elevator.ELEVATOR_MODE.HATCH, 0, true);
			m_ds.waitForData(.150); // Blocks until we get new data or 150ms elapse
            _semaphore.newData();
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
        //HAL.report(tResourceType.kResourceType_Framework,tInstances.kFramework_Simple);
        // first and one-time initialization
        LiveWindow.setEnabled(false);
        robotInit();
        HAL.observeUserProgramStarting();
        while (true) {
            if (isDisabled()) {
                m_ds.InDisabled(true);
                disabled();
				while (isDisabled())
				{
					m_ds.waitForData(0.15); // Blocks until we get new data or 150ms elapse
                	_semaphore.newData();
				}
                    //Timer.delay(0.01);
                m_ds.InDisabled(false);
            
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
                
                while (!isDisabled()) {
                	m_ds.waitForData(0.15); // Blocks until we get new data or 150ms elapse
                	_semaphore.newData();
                    //Timer.delay(0.01);
                }
                
                _logger.stop();
                //Timer.delay(1);
                m_ds.InOperatorControl(false);
            }
        } /* while loop */
    }
}