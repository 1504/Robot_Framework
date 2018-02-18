package org.usfirst.frc.team1504.robot;

import java.util.TimerTask;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

public class Winch implements Updatable
{
	private static Winch _instance = new Winch();

	private boolean _deployed = true;
	private boolean _override = false;
	
	private DriverStation _ds = DriverStation.getInstance();
	
	private WPI_TalonSRX _nancy;
	private WPI_TalonSRX _mead;
	private Thread _winch;
	
	protected Winch()
	{
		_nancy = new WPI_TalonSRX(Map.RIGHT_TALON_PORT);
		_nancy.enableCurrentLimit(true);
		_nancy.configContinuousCurrentLimit(Map.WINCH_CURRENT_LIMIT, 20); //20 is timeout in ms
		
		_mead = new WPI_TalonSRX(Map.LEFT_TALON_PORT);
		_mead.enableCurrentLimit(true);
		_mead.configContinuousCurrentLimit(Map.WINCH_CURRENT_LIMIT, 20); //20 is timeout in ms
		
		new Thread( new Runnable() {
			public void run() {
				double timeout = Map.WINCH_BRAKE_TIMEOUT;

				while(true)
				{
					if(_ds.isEnabled())
					{
						_mead.setNeutralMode(NeutralMode.Brake);
						_nancy.setNeutralMode(NeutralMode.Brake);
					}
					
					else if(!_ds.isEnabled())
					{
//						System.out.println("Winch brakes OFF in "+ timeout +" seconds.");
						new Thread( new Runnable()
						{
							public void run() 
							{
								Timer.delay(timeout);
								if(_ds.isEnabled())
									return;
								_mead.setNeutralMode(NeutralMode.Coast);
								_nancy.setNeutralMode(NeutralMode.Coast);
//								System.out.println("Winch brakes OFF");
							}
						}).start();
					}
					Timer.delay(.2);
				}
			}
		}).start();
		
		_mead.setNeutralMode(NeutralMode.Brake);
		_nancy.setNeutralMode(NeutralMode.Brake);
		
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
				_deployed = false;
				
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
			_nancy.enableCurrentLimit(!_override);
			_mead.enableCurrentLimit(!_override);
		}
		_override = override;
	}
	
	public void semaphore_update()
	{
		//set_current_limit(IO.winch_override());
		//uncomment this once we have inputs figured out
		/*if(_ds.getMatchTime() > 30.0 && !IO.winch_override())
			return;*/
		// Run that thang!
		_nancy.set(IO.winch_input());
		_mead.set(-IO.winch_input());
	}
}