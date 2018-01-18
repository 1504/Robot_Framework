package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Lift implements Updatable
{
	
	private WPI_TalonSRX _motor;
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
	int on_count = 0;
	int off_count = 0;
	
	private static final Lift instance = new Lift();
	
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
	
	
	private void update_mode()
	{
		if (IO.get_lift_on())
		{
			off_count = 0;
			_mode = state.ON;
			if (on_count == 0)
			{
				System.out.println("Lifting things");
				on_count++;
			}
		}
		else if (IO.get_lift_off())
		{
			on_count = 0;
			_mode = state.OFF;
			if (off_count == 0)
			{
				System.out.println("Not lifting things");
				off_count++;
			}
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			_motor.set(IO.intake_input());
		}
		if (_mode == state.OFF)
		{
			_motor.set(0);
		}
	}
	
	public void semaphore_update()
	{
		update_mode();
		set_motor();
	}
}
