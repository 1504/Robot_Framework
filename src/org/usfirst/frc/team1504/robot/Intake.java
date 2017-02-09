package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.*;

public class Intake implements Updatable
{
	
	private CANTalon _motor;
	
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
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
		
		System.out.println("Intake Initialized.");
	}
	
	
	private void update_mode()
	{
		if (IO.get_intake_on())
		{
			_mode = state.ON;
		}
		else if (IO.get_intake_off())
		{
			_mode = state.OFF;
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			_motor.set(1);
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
