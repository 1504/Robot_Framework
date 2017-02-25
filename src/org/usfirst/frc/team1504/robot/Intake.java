package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.*;

public class Intake implements Updatable
{
	
	private CANTalon _motor;
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
	int on_count = 0;
	int off_count = 0;
	
	private static final Intake instance = new Intake();
	
	public static Intake getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	private Intake()
	{	
		_motor = new CANTalon(Map.INTAKE_TALON_PORT);
		
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Grape Crush Initialized.");
		System.out.println("Grape Crush Disabled");
	}
	
	
	private void update_mode()
	{
		if (IO.get_intake_on())
		{
			off_count = 0;
			_mode = state.ON;
			if (on_count == 0)
			{
				System.out.println("Grape Crush is crushing some grapes.");
				on_count++;
			}
		}
		else if (IO.get_intake_off())
		{
			on_count = 0;
			_mode = state.OFF;
			if (off_count == 0)
			{
				System.out.println("Grape Crush stopped crushing.");
				off_count++;
			}
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			_motor.set(-1*IO.winch_input());
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
