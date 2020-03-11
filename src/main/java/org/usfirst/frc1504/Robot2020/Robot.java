package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALUtil;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotBase {

    private Update_Semaphore _semaphore = Update_Semaphore.getInstance();
    //private Logger _logger = Logger.getInstance();
    //private Arduino _arduino = Arduino.getInstance();
    private Thread _dashboard_task;
    private Autonomous _autonomous = Autonomous.getInstance();

    /**
     * Create a new Robot
     */
    public Robot() {
        super();
        Drive.initialize();
        Ion_Cannon.initialize();
        // Optical_Sensor.initialize();
        Tractor_Beam.initialize();
        Tokamak.initialize();
        Pizza.initialize();
        Lightsaber.initialize();
        // DigitBoard.initialize();
        // Digit_Board.initialize();
        // Arduino.initialize();
    }

    /**
     * Robot-wide initialization code should go here.
     *
     * Users should override this method for default Robot-wide initialization which
     * will be called when the robot is first powered on.
     *
     * Called exactly 1 time when the competition starts.
     */
    public void robotInit() 
    {
        CameraServer.getInstance().startAutomaticCapture();
        _dashboard_task = new Thread(new Runnable()
        {
            public void run() 
            {
                //_arduino.setPartyMode(true);
                char edge_track = 0;
                

                while (true)
                {
                    edge_track = (char) (((edge_track << 1) + (HALUtil.getFPGAButton() ? 1 : 0)) & 3);
                    if (edge_track == 1) // Get image from groundtruth sensors, output it to the DS
                    {
                        //_arduino.diagnostic(!_arduino.diagnostic());
                    }

                    if (IO.god())
                        IO.god_state = !IO.god_state;
                    
                    SmartDashboard.putBoolean("God Mode", IO.god_state);
                    
                    Timer.delay(0.02);
                }
            }
        });
        _dashboard_task.start();

        System.out.println("PD-20 Initialized ( robotInit() ) @ " + IO.ROBOT_START_TIME);
    }

    /**
     * Disabled should go here. Users should overload this method to run code that
     * should run while the field is disabled.
     *
     * Called once each time the robot enters the disabled state.
     */
    protected void disabled()
    {
        System.out.println("Robot Disabled");
    }

    public void autonomous() {
        System.out.println("Autonomous mode");
    }

    /**
     * Operator control (tele-operated) code should go here. Users should add
     * Operator Control code to this method that should run while the field is in
     * the Operator Control (tele-operated) period.
     *
     * Called once each time the robot enters the operator-controlled state.
     */
    public void operatorControl()
    {
        System.out.println("Operator Control");
    }

    /**
     * Test code should go here. Users should add test code to this method that
     * should run while the robot is in test mode.
     */
    public void test() 
    {
        System.out.println("Test Mode!");

        /*WPI_TalonSRX _testing1;
        CAN _testing2;

        _testing1 = new WPI_TalonSRX(Map.testing1talon);
        _testing2 = new WPI_TalonSRX(Map.testing2talon);*/

        //while (isTest() && isEnabled()) 
        //    Lightsaber.getInstance().set_lightsaber_raw(IO.snake() * 0.4, (IO.ion_vision() ? IO.serializer() : IO.snake()) * 0.4);
        
        while (m_ds.isEnabled())
        {
            m_ds.waitForData(0.15); // Blocks until we get new data or 150ms elapse
            _semaphore.newData();
        }
        Lightsaber.getInstance().reset_encoders();
    }

    /**
     * Start a competition. This code tracks the order of the field starting to
     * ensure that everything happens in the right order. Repeatedly run the correct
     * method, either Autonomous or OperatorControl when the robot is enabled. After
     * running the correct method, wait for some state to change, either the other
     * mode starts or the robot is disabled. Then go back and wait for the robot to
     * be enabled again.
     */

    public void startCompetition() 
    {
        // HAL.report(tResourceType.kResourceType_Framework,tInstances.kFramework_Simple);
        // first and one-time initialization
        LiveWindow.setEnabled(false);
        robotInit();
        HAL.observeUserProgramStarting();
        while (true) {
            if (isDisabled()) {
                m_ds.InDisabled(true);
                disabled();
                while (isDisabled()) {
                    m_ds.waitForData(0.15); // Blocks until we get new data or 150ms elapse
                    _semaphore.newData();
                }
                // Timer.delay(0.01);
                m_ds.InDisabled(false);

            } else if (isAutonomous()) {
                m_ds.InAutonomous(true);
                autonomous();


                _autonomous.setup_path(Map.SIMPLE_NEW_AUTON);
                _autonomous.start();

                while(isAutonomous() && !isDisabled()) {
                    m_ds.waitForData(150);
                    _semaphore.newData();
                }
                
                m_ds.InAutonomous(false);
            } else if (isTest()) {
                // LiveWindow.setEnabled(true);
                m_ds.InTest(true);

                test();

                while (isTest() && isEnabled())
                    Timer.delay(0.01);

                m_ds.InTest(false);
                // LiveWindow.setEnabled(false);

            } else {
                m_ds.InOperatorControl(true);
                //_logger.start("Tele");

                operatorControl();

                while (!isDisabled()) {
                    m_ds.waitForData(0.15); // Blocks until we get new data or 150ms elapse
                    _semaphore.newData();
                }

                //_logger.stop();
                m_ds.InOperatorControl(false);
            }
        } /* while loop */
    }

    @Override
    public void endCompetition() 
    {
        // TODO Auto-generated method stub
    }
}