package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DigitalInput;
public class Lift implements Updatable
{
	public enum lift_position {BOTTOM, MIDDLE, TOP, OFF};
	private double[] lift_velocity = {-1.0, 0, 1.0, 0};
	private String[] lifting_messages = {"lift is going to bottom","lift is going to mid","lift is going to top", "lift is off"};
	public static lift_position lift_state = lift_position.OFF;
	private Drive _drive = Drive.getInstance();
	private WPI_TalonSRX _motor; // declared for future use
	private Pickup _pickup = Pickup.getInstance();// declared for future use 
	
	public Solenoid plate_solenoid = new Solenoid(Map.LIFT_PLATE_SOLENOID_PORT);
	boolean get_top_lift_sensor; // used as a value to check position of lift
	boolean get_bottom_lift_sensor; // used as a value to check position of lift 
	private DriverStation _ds = DriverStation.getInstance();
	private double lift_speed = 0.0;
	private boolean button_mode = false;
	private static DigitalInput top_lift_switch = new DigitalInput(0); //needs to be initialized
	private static DigitalInput bottom_lift_switch = new DigitalInput(1);
	
	private static final Lift instance = new Lift(); // used later to initialize
	
	private Lift() //assigns motor to lift
	{	
		_motor = new WPI_TalonSRX(Map.LIFT_TALON_PORT);
		Update_Semaphore.getInstance().register(this);
	}
	
	private void update_mode() //checks where the lift is
	{
		if(IO.get_crash_detection())
		{
			double[] val = _drive.roborio_crash_bandicoot_check(new double[]{1, 1, 1}, 200, Map.CRASH_DETECTION_MODE);
			if(val[0] == 0.0) 
			{
				plate_solenoid.set(true);
			}
		}
		_motor.set(lift_speed);
	}
	
	public void set_state(lift_position state)
	{
		lift_state = state;
	}
	
	public boolean set_lift_velocity(double speed) {
		if(Map.LIMIT_SWITCH_EXISTS && top_lift_switch.get() && speed > 0)
		{
			set_lift_velocity(0);
			return false;
		} else if(Map.LIMIT_SWITCH_EXISTS && bottom_lift_switch.get() && speed < 0)
		{
			set_lift_velocity(0);
			return false;
		}
		lift_speed = speed;
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
			if(IO.get_lift_drop()) 
			{
				plate_solenoid.set(true);
				System.out.println(IO.get_lift_drop());
			} 
			else
			{
				plate_solenoid.set(false);
			}
			if(IO.get_lift_up())
			{
				set_lift_velocity(-1.0);
				button_mode = true;
			} else if(IO.get_lift_down())
			{
				set_lift_velocity(1.0);
				button_mode = true;
			} else if(!button_mode || Math.abs(IO.lift_input()) > 0.1)
			{
				set_lift_velocity(IO.lift_input());
				button_mode = false;
			}
		}
		update_mode();
	}

}