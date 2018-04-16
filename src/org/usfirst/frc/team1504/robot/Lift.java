package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
public class Lift implements Updatable
{
	public static final int TALON_PORT = 30;
	public static final int PLATE_SOLENOID_PORT = 2; 
	
	public enum position {BOTTOM, MIDDLE, TOP, OFF};
	public static position state = position.OFF;
	private Drive _drive = Drive.getInstance();
	private WPI_TalonSRX _motor; // declared for future use
	
	public Solenoid plate_solenoid = new Solenoid(PLATE_SOLENOID_PORT);
	boolean get_top_sensor; // used as a value to check position of lift
	boolean get_bottom_sensor; // used as a value to check position of lift 
	private DriverStation _ds = DriverStation.getInstance();
	private double speed = 0.0;
	private boolean button_mode = false;
	private static DigitalInput top_switch = new DigitalInput(0); //needs to be initialized
	private static DigitalInput bottom_switch = new DigitalInput(1);
	
	private static final Lift instance = new Lift(); // used later to initialize
	
	private Lift() //assigns motor to lift
	{	
		_motor = new WPI_TalonSRX(TALON_PORT);
		Update_Semaphore.getInstance().register(this);
	}
	
	private void update_mode() //checks where the lift is
	{
		if(IO.crash_detection())
		{
			double[] val = _drive.roborio_crash_bandicoot_check(new double[]{1, 1, 1}, 200, Map.CRASH_DETECTION_MODE);
			if(val[0] == 0.0) 
			{
				plate_solenoid.set(true);
			}
		}
		_motor.set(speed);
		if(button_mode && (!bottom_switch.get() && speed > 0) || (!top_switch.get() && speed < 0)){
			set_velocity(0.0);
			button_mode = false;
		}
	}
	
	public void set_state(position state_)
	{
		state = state_;
	}
	
	public boolean set_velocity(double speed_) {
		if(!top_switch.get() && speed < 0)
		{
			set_velocity(0);
			return false;
		} else if(!bottom_switch.get() && speed > 0)
		{
			set_velocity(0);
			return false;
		}
		speed = speed_;
		return true;
	}
	
	public static Lift getInstance() //returns instance
	{
		return instance;
	}
		
	public static void initialize() // returns instance
	{
		getInstance();
	}
	
	public void semaphore_update() //updates data from robot
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{
			if(IO.lift_drop()) 
			{
				plate_solenoid.set(true);
				System.out.println(IO.lift_drop());
			} 
			else
			{
				plate_solenoid.set(false);
			}
			if(IO.override_lift())
			{
				speed = IO.lift_input();
			}
			else
			{
				if(IO.lift_up())
				{
					set_velocity(-1.0);
					button_mode = true;
				} else if(IO.lift_down())
				{
					set_velocity(1.0);
					button_mode = true;
				} else if(!button_mode || Math.abs(IO.lift_input()) > 0.1)
				{
					set_velocity(IO.lift_input());
					button_mode = false;
				}
			}
		}
		SmartDashboard.putNumber("Lift Current", _motor.getOutputCurrent());
		//System.out.println(IO.get_override_lift());
		update_mode();
	}

}