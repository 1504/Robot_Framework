package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Winch implements Updatable
{
	private static Winch _instance = new Winch();
	private Servo _servo1;//, _servo2;
	private boolean _deployed = false;

	private DriverStation _ds = DriverStation.getInstance();
	
	private CANTalon _winch_motor_nancy = new CANTalon(Map.WINCH_TALON_PORT_NANCY);
	private CANTalon _winch_motor_mead = new CANTalon(Map.WINCH_TALON_PORT_MEAD);
	
	//1 servo pwm = 0, 0 deg and 180 = extended
	
	protected Winch()
	{
		_servo1 = new Servo(Map.WINCH_SERVO1);
		//_servo2 = new Servo(Map.WINCH_SERVO2);

		Update_Semaphore.getInstance().register(this);
		System.out.println("Winch initialized");
	}
	
	public static Winch getInstance()
	{
		return _instance;
	}
	public static void initialize()
	{
		getInstance();
	}
	public boolean get_deployed()
	{
		return _deployed;
	}
	
	public void set_deployed(boolean deployed)
	{
		_deployed = deployed;
		set_servo();
	}
	
	private void set_servo()
	{
		if(get_deployed())
		{
			_servo1.set(Map.WINCH_SERVO_DEPLOYED);
			
			//_servo2.set(Map.WINCH_SERVO_DEPLOYED);
		}
		else //if(!get_deployed() || !_ds.isEnabled())
		{
			_servo1.set(Map.WINCH_SERVO_STORED);
			//_servo2.set(Map.WINCH_SERVO_STORED);
			_deployed = false;
		}
	}
	
	public void semaphore_update()
	{
		if(_ds.getMatchTime() > 30.0 && !IO.winch_override())
			return;
		
		// Deploy winch out the side of the robot
		//TODO: What is the mechanism?
		if(IO.winch_deploy())
		{
			if(!get_deployed())
			{
				new Thread(new Runnable() {public void run() 
					{
						Timer.delay(3); 
						set_deployed(false);
					}
				}).start();
			}
			_deployed = true;
		}
		set_servo();
		System.out.println("servo set");
		
		
		
		// Run that thang!
		_winch_motor_nancy.set(IO.winch_input());
		_winch_motor_mead.set(-IO.winch_input());
	}
}