package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Lift implements Updatable
{
	
	private WPI_TalonSRX _motor;
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
	boolean get_top_lift_sensor;
	boolean get_bottom_lift_sensor;
	
	private static final Lift instance = new Lift();
	private Pickup _pickup = Pickup.getInstance();
	
	public static Lift getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	private Lift()
	{	
		_motor = new WPI_TalonSRX(Map.LIFT_TALON_PORT);
		
		Update_Semaphore.getInstance().register(this);
	}
	
	public static double get_elevator_height()
	{
		return 0.0; //_blahblahblah.magneticEncoder;
	}
	
	public boolean pickup_safe() 
	{
		return (get_elevator_height() < 5);
	}
	
	private void update_mode()
	{
		if (IO.get_lift_on())
		{
			_mode = state.ON;
			System.out.println("Lifting things");
			lift_top();
		}
		else if (IO.get_lift_off())
		{
			_mode = state.OFF;
			_motor.set(0);
			System.out.println("Not lifting things");
		}
		
		if (get_elevator_height() == Map.ELEVATOR_MAX_HEIGHT) 
		{
			get_top_lift_sensor = true;
		}
		else if (get_elevator_height() == Map.ELEVATOR_MIN_HEIGHT) 
		{
			get_bottom_lift_sensor = true;
		}
		else 
		{
			get_top_lift_sensor = false;
			get_bottom_lift_sensor = false;
		}
	}
	
	private void set_motor()
	{
		if (IO.get_override_lift())
		{
			_motor.set(IO.intake_input());
		}
		if (_mode == state.OFF)
		{
			_motor.set(0);
		}
		// (IO.get_elevator_height < Map.ELEVATOR_MAX_HEIGHT) || (IO.get_elevator_height > Map.ELEVATOR_MIN_HEIGHT)
		
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
				_motor.set(Map.ELEVATOR_DOWN);
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
				_motor.set(Map.ELEVATOR_UP);
			}
		}
		else 
		{
			System.out.println("Not at bottom...");
		}
	}
	
	public void semaphore_update()
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
				_motor.set(Map.ELEVATOR_DOWN);
			}
		}
		else 
		{
			System.out.println("Not at top...");
			_motor.set(lift_speed(1));
		}
	}
	public double lift_speed(int speed)//Toggle based (position based, y)
	{
		if(speed == 1)//v = x^2
		{	
			return (((Map.ELEVATOR_MAX_HEIGHT-get_elevator_height())*(Map.ELEVATOR_MAX_HEIGHT-get_elevator_height()))/(Map.ELEVATOR_MAX_HEIGHT*Map.ELEVATOR_MAX_HEIGHT));
		}
		else//v = x
		{
			return ((Map.ELEVATOR_MAX_HEIGHT-get_elevator_height())/Map.ELEVATOR_MAX_HEIGHT);
		}
	}
}