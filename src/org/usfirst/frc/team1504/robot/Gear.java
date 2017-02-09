package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import org.usfirst.frc.team1504.utils.Average;

import edu.wpi.first.wpilibj.AnalogInput;
public class Gear implements Updatable{
	
	private static final Gear _instance = new Gear();
	AnalogInput _port = new AnalogInput(0);
	AnalogInput _star = new AnalogInput(1);
	Arduino.GEAR_MODE _mode;
	double _portDist = 0;
	double _starDist = 0;
	/*double [] _starAverage = new double[4];
	double _starSum = 0;
	int _starIndex = 0;
	double [] _portAverage = new double[4];
	double _portSum = 0;
	int _portIndex = 0;*/
	Average _portAvg;
	Average _starAvg;


	public static Gear getInstance() {
		
		return Gear._instance;

	}
	public static void initialize()
	{
		getInstance();
	}
	public Gear()
	{
		Update_Semaphore.getInstance().register(this);
		//drive methods in gear class, call from drive
	}
	
	public double [] setDriveInput()
	{
		double [] output = new double[3];
		
		output[0] = Map.GEAR_GAIN * (Math.max(_starDist, _portDist) - Map.GEAR_DISTANCE);
		output[1] = 0;
		output[2] = Map.GEAR_GAIN * (_portDist - _starDist);
		
		output[0] = Math.max(Math.min(output[0], Map.GEAR_MAX_OUTPUT_POWER), -Map.GEAR_MAX_OUTPUT_POWER);
		output[2] = Math.max(Math.min(output[2], Map.GEAR_MAX_OUTPUT_POWER), -Map.GEAR_MAX_OUTPUT_POWER);
		
		//_average[0] = 
				
		//System.out.println("y input " + output[0]);
		//System.out.println("x input " + output[2]);
		
		return output;
	}
	
	public void semaphore_update()
	{
		getInput();
	}
	
	public double [] getInput()
	{	
		double portDist = _port.getValue()/4096.0;
		double starDist = _star.getValue()/4096.0;
		
		//System.out.println("distance from port side " + _portDist); //_port.getAverageValue());
		//System.out.println("distance from star side " + _starDist);//_star.getAverageValue());
		
		if(_portDist > Map.GEAR_DISTANCE || _starDist > Map.GEAR_DISTANCE)
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
		
		_portDist = _portAvg.findAverage(portDist);
		_starDist = _starAvg.findAverage(starDist);

		
		/*_starIndex = ++_starIndex % _starAverage.length;
		_starSum += -_starAverage[_starIndex] + starDist;
		_starAverage[_starIndex] = starDist;
		_starDist  = _starSum/_starAverage.length;
		
		_portIndex = ++_portIndex % _portAverage.length;
		_portSum += -_portAverage[_portIndex] + portDist;
		_portAverage[_portIndex] = portDist;
		_portDist = _portSum/_portAverage.length;*/
		
		//System.out.println("average port distance " + _portDist);
		//System.out.println("average star distance " + _starDist);

		
		double [] arr = {_portDist, _starDist};
		return arr;

	}
}