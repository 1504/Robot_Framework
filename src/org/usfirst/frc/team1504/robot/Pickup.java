package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _motorL;
	private WPI_TalonSRX _motorR;
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
	int on_count = 0;
	int off_count = 0;
	
	private static final Pickup instance = new Pickup();
	
	public static Pickup getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	private Pickup()
	{	
		_motorL = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_LEFT);
		_motorR = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_RIGHT);
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Grape Crush Initialized.");
		System.out.println("Grape Crush Disabled");
	}
	
	
	private void update_mode()
	{
		if (IO.get_pickup_on())
		{
			off_count = 0;
			_mode = state.ON;
			if (on_count == 0)
			{
				System.out.println("Pickup is intaking some cubes.");
				on_count++;
			}
		}
		else if (IO.get_pickup_off())
		{
			on_count = 0;
			_mode = state.OFF;
			if (off_count == 0)
			{
				System.out.println("Pickup stopped intaking.");
				off_count++;
			}
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			_motorL.set(IO.winch_input());
			_motorR.set(IO.winch_input());
		}
		if (_mode == state.OFF)
		{
			_motorL.set(0);
			_motorR.set(0);
		}
	}

	public void semaphore_update()
	{
		update_mode();
		set_motor();
	}
}
