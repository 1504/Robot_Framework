package org.usfirst.frc.team1504.robot;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import edu.wpi.first.wpilibj.DriverStation;
//import java.util.Base64;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {
	
	private DigitBoard _db = DigitBoard.getInstance();
	private Digit_Board _digit_board = Digit_Board.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
	private Autonomous _autonomous = Autonomous.getInstance();
	
	Pneumatics t3 = Pneumatics.getInstance();
	Drive t5 = Drive.getInstance();
	
	private Thread _dashboard_task;
	
    /**
     * Create a new Robot
     */
    public Robot() {
    	super();
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
				PowerDistributionPanel pdp = new PowerDistributionPanel();
				while(true)
				{	
					SmartDashboard.putNumber("Robot Current", pdp.getTotalCurrent());
					SmartDashboard.putNumber("Robot Voltage", m_ds.getBatteryVoltage());
					SmartDashboard.putNumber("Robot Time", m_ds.getMatchTime());
					Timer.delay(.05);
				}
			}
		});
    	_dashboard_task.start();
    	
    	//System.out.println(new String(Base64.getDecoder().decode(Map.ROBOT_BANNER)));
        System.out.println("Quixote Initialized ( robotInit() ) @ " + IO.ROBOT_START_TIME);
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
    }

    /**
     * Test code should go here.
     * Users should add test code to this method that should run while the robot is in test mode.
     */
    public void test() {}

    /**
     * Start a competition.
     * This code tracks the order of the field starting to ensure that everything happens
     * in the right order. Repeatedly run the correct method, either Autonomous or OperatorControl
     * when the robot is enabled. After running the correct method, wait for some state to change,
     * either the other mode starts or the robot is disabled. Then go back and wait for the robot
     * to be enabled again.
     */
    public void startCompetition() {
        UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Sample);
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();

        // first and one-time initialization
        LiveWindow.setEnabled(false);
        robotInit();

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
                m_ds.InOperatorControl(false);
            }
        } /* while loop */
    }
}
