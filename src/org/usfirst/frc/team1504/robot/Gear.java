package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Arduino.GEAR_MODE;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;

public class Gear implements Updatable{
	
	private static final Gear _instance = new Gear();
	private CANTalon _gear = new CANTalon(Map.GEAR_TALON_PORT);

	
	protected Gear()
	{
		_gear.enableLimitSwitch(true, true);
		Update_Semaphore.getInstance().register(this);
		System.out.println("Gear Initialized");
	}
	
	public static Gear getInstance()
	{
		return _instance;
	}
	
	public void semaphore_update()
	{
		if(IO.gear_input())
		{
			_gear.set(Map.GEAR_MAGIC_NUMBER);
		}
		else
			_gear.set(-Map.GEAR_MAGIC_NUMBER);
	}
}