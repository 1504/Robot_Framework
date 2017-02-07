package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;

public class Winch implements Updatable
{
	private static Winch _instance = new Winch();
	private Servo _servo1, _servo2;
	private boolean _deployed = false;

	private DriverStation _driver_station = DriverStation.getInstance();
	
	private CANTalon _winch_motor_nancy = new CANTalon(Map.WINCH_TALON_PORT_NANCY);
	private CANTalon _winch_motor_mead = new CANTalon(Map.WINCH_TALON_PORT_MEAD);
	
	protected Winch()
	{
		//_servo1 = new Servo(Map.WINCH_SERVO1);
		//_servo2 = new Servo(Map.WINCH_SERVO2);

		Update_Semaphore.getInstance().register(this);
		System.out.println("Winch initialized");
	}
	
	public static Winch getInstance()
	{
		return _instance;
	}
	
	public boolean get_deployed()
	{
		return _deployed;
	}
	
	public void semaphore_update()
	{
		if(_driver_station.getMatchTime() > 30.0 && !IO.operator_override())
			return;
		
		// Deploy winch out the side of the robot
		//TODO: What is the mechanism?
		if(IO.winch_deploy())
		{
			_deployed = true;
		}
		
		if(get_deployed())
		{
			//_servo1.set(Map.WINCH_SERVO_DEPLOYED);
			//_servo2.set(Map.WINCH_SERVO_DEPLOYED);
		}
		else
		{
			//_servo1.set(Map.WINCH_SERVO_STORED);
			//_servo2.set(Map.WINCH_SERVO_STORED);
			//_deployed = false;
		}
		
		// Run that thang!
		_winch_motor_nancy.set(IO.winch_input());
		_winch_motor_mead.set(-IO.winch_input());
	}
}