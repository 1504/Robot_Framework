package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Lift implements Updatable
{
	
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
			lift_top();
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
	}
	
	private void set_motor() //sets the position of the lift
	{
		if (IO.get_override_lift())
		{
			_motor.set(IO.intake_input());
		}

		if (get_top_lift_sensor) 
		{
			_motor.set(0);
			System.out.println("At top, stopping");
			if (IO.get_lift_up()) 
			{
				_motor.set(0);
			}
			if(IO.get_lift_down() && _pickup.lift_safe()) 
			{
				_motor.set(Map.LIFT_DOWN);
			}
		}
		else 
		{
			System.out.println("Not at top...");
		}
		
		if (get_bottom_lift_sensor) {
			_motor.set(0);
			System.out.println("At bottom, stopping");
			if (IO.get_lift_down()) 
			{
				_motor.set(0);
			}
			if(IO.get_lift_up() && _pickup.lift_safe()) 
			{
				_motor.set(Map.LIFT_UP);
			}
		}
		else 
		{
			System.out.println("Not at bottom...");
		}
	}
	
	
	public static Lift getInstance() //returns instance
	{
		return instance;
	}
		
	public static void initialize() // returns instance
	{
		getInstance();
	}
	
	public static double get_elevator_height() // i don't think this is finished?
	{
		return 0.0; //_blahblahblah.magneticEncoder;
	}
	
	public void plate_angle(double angle) // Sets angle of lift plate
	{
		// return true;
	}
	
	public void semaphore_update() //updates data from robot
	{
		update_mode();
		set_motor();
	}
	
	public void lift_top()//toggle based (no manual input, rises to top)
	{
		if (get_top_lift_sensor) 
		{
			_motor.set(0);
			System.out.println("At top, stopping");
			if (IO.get_lift_up()) 
			{
				_motor.set(0);
			}
			if(IO.get_lift_down() && _pickup.lift_safe()) 
			{
				_motor.set(Map.LIFT_DOWN);
			}
		}
		else 
		{
			System.out.println("Not at top...");
			_motor.set(Map.LIFT_MOTOR_SPEED);
		}
	}
	
	public void lift_middle()//toggle based (no manual input, moves to middle)
	{
		if (get_elevator_height() == Map.LIFT_MAX_HEIGHT / 2)
			{
			_motor.set(0);
			System.out.println("At middle, stopping");
			_motor.set(Map.LIFT_MOTOR_SPEED);
		}
	}
	
	public void lift_bottom()//toggle based (no manual input, falls to bottom)
	{
		if (get_bottom_lift_sensor)
			{
			_motor.set(0);
			System.out.println("At bottom, stopping");
			if (IO.get_lift_down())
			{
				_motor.set(0);
			}
			}
		else
			{
			System.out.println("Not at bottom...");
			_motor.set(Map.LIFT_MOTOR_SPEED);
		}
	}
	
	public boolean pickup_safe() //checks if it is safe to move lift so it won't crash into things
	{
		return (get_elevator_height() < 5);
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
}