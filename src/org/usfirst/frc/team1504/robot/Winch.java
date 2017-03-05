package org.usfirst.frc.team1504.robot;

import java.util.TimerTask;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Winch implements Updatable
{
	private static Winch _instance = new Winch();

	private boolean _deployed = false;
	private boolean _override = false;
	
	private DriverStation _ds = DriverStation.getInstance();
	
	private CANTalon _nancy;
	private CANTalon _mead;
	private Thread _winch;
	
	protected Winch()
	{
		_nancy = new CANTalon(Map.NANCY_TALON_PORT);
		_nancy.EnableCurrentLimit(true);
		_nancy.setCurrentLimit(Map.WINCH_CURRENT_LIMIT);
		
		_mead = new CANTalon(Map.MEAD_TALON_PORT);
		_mead.EnableCurrentLimit(true);
		_mead.setCurrentLimit(Map.WINCH_CURRENT_LIMIT);
		
		new Thread( new Runnable() {
			public void run() {
				double timeout = Map.WINCH_BRAKE_TIMEOUT;

				while(true)
				{
					if(_ds.isEnabled())
					{
						_mead.enableBrakeMode(true);
						_nancy.enableBrakeMode(true);
					}
					
					else if(!_ds.isEnabled())
					{
						System.out.println("Winch brakes OFF in "+ timeout +" seconds.");
						new Thread( new Runnable()
						{
							public void run() 
							{
								Timer.delay(timeout);
								if(_ds.isEnabled())
									return;
								_nancy.enableBrakeMode(false); //only on disable
								_mead.enableBrakeMode(false);
								System.out.println("Winch brakes OFF");
							}
						}).start();
					}
					Timer.delay(.2);
				}
			}
		}).start();
		
		_mead.enableBrakeMode(true);
		_nancy.enableBrakeMode(true);
		
		_winch = new Thread(new Runnable() {
			public void run()
			{
				while(!_deployed) //while winch not deployed, periodically backdrive winch to keep tension
				{
					_nancy.set(-.25);
					_mead.set(.25);
					Timer.delay(.01);
					_nancy.set(0.0);
					_mead.set(0.0);
					
					try {
						Thread.sleep(500); //ms
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//_deployed = false;
				
				_nancy.set(1.0); //deploy winch
				_mead.set(-1.0);
				Timer.delay(2); 
				_nancy.set(0.0);
				_mead.set(0.0);
				
				_deployed = true;
				
			}
		}); 
		_winch.start();

		Update_Semaphore.getInstance().register(this);
		System.out.println("Winch is ready to end the game. And end your life.");
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
	}

	private void set_current_limit(boolean override)
	{
		if (_override != override)
		{
			_nancy.EnableCurrentLimit(!_override);
			_mead.EnableCurrentLimit(!_override);
		}
		_override = override;
	}
	
	public void semaphore_update()
	{
		set_current_limit(IO.winch_override());
		
		if(_ds.getMatchTime() > 30.0 && !IO.winch_override())
			return;
		
		// Deploy winch
		if(IO.winch_deploy())
		{
			set_deployed(true);
		}
		
		// Run that thang!
		_nancy.set(IO.winch_input());
		_mead.set(-IO.winch_input());
	}
}