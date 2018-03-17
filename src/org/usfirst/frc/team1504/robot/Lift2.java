package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DigitalInput;
public class Lift2 implements Updatable
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
	
	private static DigitalInput top_lift_switch;
	private static DigitalInput mid_lift_switch;
	private static DigitalInput bottom_lift_switch;
	
	private static boolean top_lock = false;
	private static boolean bottom_lock = false;
	
	private static final Lift2 instance = new Lift2(); // used later to initialize
	
	private Lift2() //assigns motor to lift
	{	
		_motor = new WPI_TalonSRX(Map.LIFT_TALON_PORT);
		Update_Semaphore.getInstance().register(this);
	}
	
	private void update_mode() //checks where the lift is
	{
		checkIfLiftTriggered();
		if(IO.get_crash_detection())
		{
			double[] val = _drive.roborio_crash_bandicoot_check(new double[]{1, 1, 1}, 1001);
			if(val[0] == 0.0) 
			{
				plate_solenoid.set(true);
			}
		}
		if(IO.get_override_lift()){
			if(top_lock && IO.lift_input() > 0)
			{
				set_lift_velocity(0.0);
			}
			else if(bottom_lock && IO.lift_input() < 0)
			{
				set_lift_velocity(0.0);
			}
			else 
			{
				set_lift_velocity(IO.lift_input());
			}
			set_state(lift_position.OFF);
		}
		else if(_pickup.lift_safe()) 
		{
			if(top_lock && lift_state.ordinal() == 2)
			{
				set_lift_velocity(0.0);
			}
			else if(bottom_lock && lift_state.ordinal() == 0)
			{
				set_lift_velocity(0.0);
			}
			else 
			{
				set_lift_velocity((lift_velocity[lift_state.ordinal()])*Map.LIFT_GAIN);
			}
			System.out.println(lifting_messages[lift_state.ordinal()] + "lifting messages");
		}
		else
		{
			//set_lift_velocity((lift_height[1]-get_lift_height())/Map.LIFT_MAX_HEIGHT);
			set_lift_velocity(0);
		}	//makes the lift go to the middle
	}
	
	public void set_state(lift_position state)
	{
		lift_state = state;
	}
	
	private void set_lift_velocity(double speed) {
		_motor.set(speed);
	}
	
	public double get_lift_height() 
	{
		return _motor.getSelectedSensorPosition(0);
	}
	/*
	public void plate_angle(double angle) // Sets angle of lift plate
	{
		if(angle == 0) 
		{
			plate_solenoid.set(false);
		}
		else if (angle > 0)
		{
			plate_solenoid.set(true);
		}
		// return true;
	}
	*/
	public static Lift2 getInstance() //returns instance
	{
		return instance;
	}
		
	public static void initialize() // returns instance
	{
		getInstance();
	}
	public void checkIfLiftTriggered()
	{
		if(bottom_lift_switch.get())
		{
			bottom_lock = true;
		}
		if(mid_lift_switch.get() || (Map.LIFT_MAX_HEIGHT*(1.0 - Map.LIFT_LOCK_RELEASE_RANGE) < get_lift_height() && get_lift_height() < Map.LIFT_MAX_HEIGHT*Map.LIFT_LOCK_RELEASE_RANGE))
		{
			bottom_lock = false;
			top_lock = false;
		}
		if(top_lift_switch.get())
		{
			top_lock = true;
		}
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
		}
		update_mode();
	}
}