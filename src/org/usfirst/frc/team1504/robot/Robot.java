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
	
	Digit_Board _digit_board = Digit_Board.getInstance();
	private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
	private Logger _logger = Logger.getInstance();
	private Autonomous _autonomous = Autonomous.getInstance();
	
	Wheel_Shooter t2 = Wheel_Shooter.getInstance();
	Pneumatics t3 = Pneumatics.getInstance();
	Endgame test = Endgame.getInstance();
	Vision_Interface t4 = Vision_Interface.getInstance();
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
					/*try
					{
						Runtime r = Runtime.getRuntime();
						Process p = r.exec("ps -e | wc -l");
						p.waitFor();
						BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = b.readLine();
						b.close();
						
						SmartDashboard.putString("Robot Thread Count", line);
					}
					catch (IOException e) { e.printStackTrace(); }
					catch (InterruptedException e) { e.printStackTrace(); }*/
					
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
    public void test() {
        System.out.println("test()");
        
        System.out.println("Testing latching joystick");
        Latch_Joystick testlatch = new Latch_Joystick(0);
        CANTalon A, B, C;
        A = new CANTalon(30);
        B = new CANTalon(31);
        C = new CANTalon(20);
        
        C.reverseOutput(true);
        Glide gain = new Glide(0.001, .01);
        
        double setpoint = 0;
        while (isTest() && isEnabled())
        {
        	/*testlatch.semaphore_update();
        	System.out.println("Latch: " + testlatch.getRawButtonLatch(1) + "    Rising: " + testlatch.getRawButtonOnRisingEdge(1) + "    Button: " + testlatch.getRawButton(1));
            Timer.delay(1.0);*/
        	/*while(setpoint < 1.0)
        	{
        		setpoint += .1;
        		A.set(-1.0 * setpoint);
            	B.set(1.0 * setpoint);
        		Timer.delay(0.2);
        	}*/
        	//A.set(-1.0);
        	//B.set(1.0);
        	
        	setpoint = gain.gain_adjust(1.0);
        	A.set(-1.0 * setpoint);
        	B.set(1.0 * setpoint);
        	//C.set(-1.0);
        	Timer.delay(0.05);
        	if(testlatch.getRawButton(1))
        		C.set(-1.0);
        	else
        		C.set(0.0);
        	//System.out.println(setpoint);
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
                
                // forward, ccw, type, time
               /* double turn_power = 0.2 - 0.1 * (double)_digit_board.getPosition();
                if(_digit_board.getPosition() == 5)
                	_autonomous.setup_path(new double[][] {{0,0,2,14000}, {0,0,0, 15000}});
                else if(_digit_board.getDefense() == "LowB" && _digit_board.getPosition() == 0)
                	_autonomous.setup_path(new double[][] {{0.5,0,0,2200}, {0, .2, 0, 2600}, {0.0,0,0,3000}, {0,0,1, 15000}});
                else if(_digit_board.getDefense() == "LowB" && _digit_board.getPosition() == 4)
                	_autonomous.setup_path(new double[][] {{0.5,0,0,2200}, {0, .2, 0, 2600}, {0.0,0,0,3000}, {0,0,1, 11000}, {0,0,2, 15000}});
                else if(_digit_board.getDefense() == "LowB" && _digit_board.getPosition() != 0)
                    _autonomous.setup_path(new double[][] {{0.5,0,0,2200}, {0.0,0,0,2600}, {-.5,0,0, 4800}, {.5,0,0, 4900}, {0,0,0, 14000}});
                else if(_digit_board.getDefense() == "Ruff")
                	_autonomous.setup_path(new double[][] {{0.5,0,0,2700}, {0, turn_power, 0, 2900}, {0.0,0,0,3000}, {0,0,1, 15000}});
                else if(_digit_board.getDefense() == "Moat" || _digit_board.getDefense() == "Rock")
                	_autonomous.setup_path(new double[][] {{-0.85,0,0,3500}, {0.0,turn_power,0,4000}, {0.0,0.0,0,4100}, {0,0,1, 15000}});
                else if( _digit_board.getDefense() == "Ramp")
                	_autonomous.setup_path(new double[][] {{0.55,0,0,4500}, {0.0,turn_power,0,4900}, {0.0,0.0,0,5000}, {0,0,1, 15000}});
                else
                	_autonomous.setup_path(new double[][] {{0,0,0,1000}});*/
                
                //_autonomous.setup_path(new double[][] {{0.5,0,0,2200}, {0.0,0,0,2600}, {-.5,0,0, 4800}, {.5,0,0, 9000}, {0,0,1, 14000}});
                
                //_autonomous.start();
                while (isAutonomous() && !isDisabled()) {
                	m_ds.waitForData(150);
                	_semaphore.newData();
                    //Timer.delay(0.01);
                }
                //_autonomous.stop();
                
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
