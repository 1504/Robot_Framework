package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Lift implements Updatable
{
	public enum lift_position {TOP, MIDDLE, BOTTOM};
	public static lift_position lift_state = lift_position.BOTTOM;
	
	private WPI_TalonSRX _motor; // declared for future use
	private Pickup _pickup = Pickup.getInstance();// declared for future use 
	
	boolean get_top_lift_sensor; // used as a value to check position of lift
	boolean get_bottom_lift_sensor; // used as a value to check position of lift 
	
	private static final Lift instance = new Lift(); // used later to initialize
	
	private Lift() //assigns motor to lift
	{	
		_motor = new WPI_TalonSRX(Map.LIFT_TALON_PORT);
		Update_Semaphore.getInstance().register(this);
	}
	
	private void update_mode() //checks where the lift is
	{
		if (IO.get_lift_on())
		{
			System.out.println("Lifting things");
			lift_state = lift_position.TOP;
		}
		else if (IO.get_lift_off())
		{
			_motor.set(0);
			System.out.println("Not lifting things");
		}
		if (get_elevator_height() == Map.LIFT_MAX_HEIGHT) 
		{
			get_top_lift_sensor = true;
		}
		else if (get_elevator_height() == Map.LIFT_MIN_HEIGHT) 
		{
			get_bottom_lift_sensor = true;
		}
		else 
		{
			get_top_lift_sensor = false;
			get_bottom_lift_sensor = false;
		}
		if(lift_state == lift_position.TOP) 
		{
			System.out.println("Lifting things en la Estados Unidos");
			if (get_top_lift_sensor) 
			{
				set_lift_speed(0);
				System.out.println("At top, stopping");
				if (IO.get_lift_up()) 
				{
					set_lift_speed(0);
				}
				if(IO.get_lift_down() && _pickup.lift_safe()) 
				{
					set_lift_speed(Map.LIFT_DOWN);
				}
			}
			else 
			{
				System.out.println("Not at top...");
				set_lift_speed(Map.LIFT_MOTOR_SPEED);
			}
		}
		if(lift_state == lift_position.MIDDLE) 
		{
			System.out.println("Take mid!");
			if (get_elevator_height() == Map.LIFT_MAX_HEIGHT / 2)
			{
				set_lift_speed(0);
				System.out.println("At middle, stopping");
				set_lift_speed(Map.LIFT_MOTOR_SPEED);
			}
		}
		if(lift_state == lift_position.BOTTOM) 
		{
			System.out.println("Low low low low low low low");
			if (get_bottom_lift_sensor)
			{
				set_lift_speed(0);
				System.out.println("At bottom, stopping");
				if (IO.get_lift_down())
				{
					set_lift_speed(0);
				}
			}
			else
			{
				System.out.println("Not at bottom...");
				set_lift_speed(Map.LIFT_MOTOR_SPEED);
			}
		}
	}
	
	public void set_state(lift_position state)
	{
		lift_state = state;
	}
	
	private void set_motor() //sets the position of the lift
	{
		if (IO.get_override_lift())
		{
			set_lift_speed(IO.intake_input());
		}
		if (get_top_lift_sensor) 
		{
			set_lift_speed(0);
			System.out.println("At top, stopping");
			if (IO.get_lift_up()) 
			{
				set_lift_speed(0);
			}
			if(IO.get_lift_down() && _pickup.lift_safe()) 
			{
				set_lift_speed(Map.LIFT_DOWN);
			}
		}
		else 
		{
			System.out.println("Not at top...");
		}
		if (get_bottom_lift_sensor) 
		{
			set_lift_speed(0);
			System.out.println("At bottom, stopping");
			if (IO.get_lift_down()) 
			{
				set_lift_speed(0);
			}
			if(IO.get_lift_up() && _pickup.lift_safe()) 
			{
				set_lift_speed(Map.LIFT_UP);
			}
		}
		else 
		{
			System.out.println("Not at bottom...");
		}
	}
	
	public double lift_speed(int speed)//Toggle based (position based, y)
	{
		if(speed == 1)//v = x^2
		{	
			return (((Map.LIFT_MAX_HEIGHT-get_elevator_height())*(Map.LIFT_MAX_HEIGHT-get_elevator_height()))/(Map.LIFT_MAX_HEIGHT*Map.LIFT_MAX_HEIGHT));
		}
		else//v = x
		{
			return ((Map.LIFT_MAX_HEIGHT-get_elevator_height())/Map.LIFT_MAX_HEIGHT);
		}
	}
	
	private void set_lift_speed(double speed) {
		_motor.set(speed);
	}
	
	public static double get_elevator_height() // i don't think this is finished?
	{
		return 0.0; //_blahblahblah.magneticEncoder;
	}
	
	public boolean pickup_safe() //checks if it is safe to move lift so it won't crash into things
	{
		return (get_elevator_height() < 5);
	}
	
	public void plate_angle(double angle) // Sets angle of lift plate
	{
		// return true;
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
		update_mode();
		set_motor();
	}
}