package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.AnalogInput;
public class Gear implements Updatable{
	
	private static final Gear _instance = new Gear();
	AnalogInput _port;
	AnalogInput _star;
	Arduino.GEAR_MODE _mode;
	double _portDist;
	double _starDist;

	public static Gear getInstance() {
		
		return Gear._instance;

	}
	
	public Gear()
	{
		Update_Semaphore.getInstance().register(this);
		//drive methods in gear class, call from drive
	}
	
	public double [] setDriveInput()
	{
		double [] input = getInput();
		double [] output = new double[2];
		
		output[0] = Map.GEAR_GAIN * (Math.max(_starDist, _portDist) - Map.GEAR_DISTANCE);
		output[1] = 0;
		output[2] = Map.GEAR_GAIN * (_portDist - _starDist);
		
		return output;
	}
	
	public void semaphore_update()
	{
		getInput();
	}
	
	public double [] getInput()
	{	
		_portDist = _port.getAverageValue()/1024;
		_starDist = _star.getAverageValue()/1024;
		
		System.out.println("distance from port side " + _portDist);
		System.out.println("distance from starboard side " + _starDist);
		
		if(_portDist > Map.GEAR_DISTANCE)
		{
			_mode = Arduino.GEAR_MODE.INDIVIDUAL_INTENSITY;
		}
		
		else if(_starDist > Map.GEAR_DISTANCE)
		{
			_mode = Arduino.GEAR_MODE.INDIVIDUAL_INTENSITY;
		}
		
		else if(_portDist > Map.GEAR_DISTANCE && _starDist > Map.GEAR_DISTANCE)
		{
			_mode = Arduino.GEAR_MODE.OFF;
		}
		
		else if(_portDist <= Map.GEAR_DISTANCE && _starDist <= Map.GEAR_DISTANCE)
		{
			_mode = Arduino.GEAR_MODE.PULSE;
		}
		
		double [] arr = {_portDist, _starDist};
		return arr;

	}
}