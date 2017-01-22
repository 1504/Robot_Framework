package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.AnalogInput;
public class Gear implements Updatable{
	
	private static final Gear _instance = new Gear();
	AnalogInput _port;
	AnalogInput _star;
	Arduino.GEAR_MODE _mode;

	public static Gear getInstance() {
		
		return Gear._instance;

	}
	
	private Gear()
	{
		//drive methods in gear class, call from drive
		//put it on semaphore.
	}
	
	public double getTurn()
	{
		return 1;
	}
	
	public double getForward()
	{
		return 1;
	}
	
	public void semaphore_update()
	{
		
	}
	
	public double getInput()
	{	
		double portDist = _port.getAverageValue()/1024;
		double starDist = _star.getAverageValue()/1024;
		
		System.out.println("analog port is " + portDist);
		System.out.println("analog starboard is " + starDist);
		
		if(portDist > Map.GEAR_DISTANCE)
		{
			_mode = Arduino.GEAR_MODE.INDIVIDUAL_INTENSITY;
		}
		
		return 1;

	}
}