package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Endgame implements Updatable
{
	private static final Endgame instance = new Endgame();
	
	enum ENDGAME_STATE {EXTEND, RETRACT};
	
	private Solenoid[] _solenoids;
	private Solenoid _extender;
	private DriverStation _ds = DriverStation.getInstance();
	private ENDGAME_STATE _state;
	
	protected Endgame()
	{
		_solenoids = new Solenoid[] 
			{
				new Solenoid(Map.ENDGAME_EXTENSION_PORT), 
				new Solenoid(Map.ENDGAME_RETRACTION_PORT)
			};
		_extender = new Solenoid(6);
		_extender.set(false);
		
		Update_Semaphore.getInstance().register(this);
	}
	
	public static Endgame getInstance()
	{
		return instance;
	}
	
	private void set_solenoids(boolean value)
	{
		for(Solenoid s : _solenoids)
			s.set(value);
	}

	@Override
	public void semaphore_update() {
		ENDGAME_STATE state = IO.endgame_state();
		
		// Make sure we can't extend before we're legal to do so
		// TODO: Test this.
		if(state == null || !(_ds.getMatchTime() < 25 || IO.override()))
			return;
		
		_state = state;
		
		new Thread(new Runnable() {
			public void run() {
				if(_state == ENDGAME_STATE.EXTEND)
				{
					_extender.set(true);
					Timer.delay(2);
					
				}
				set_solenoids(_state == ENDGAME_STATE.EXTEND);
			}
		}).start();
	}
}
