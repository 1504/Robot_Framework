package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.Solenoid;

public class Endgame implements Updatable
{
	private static final Endgame instance = new Endgame();
	
	enum ENDGAME_STATE {EXTEND, RETRACT};
	
	Solenoid _extension;
	Solenoid _retraction;
	
	protected Endgame()
	{
		_extension = new Solenoid(0);
		_retraction = new Solenoid(1);
		
		Update_Semaphore.getInstance().register(this);
	}
	
	public static Endgame getInstance()
	{
		return instance;
	}

	@Override
	public void semaphore_update() {
		ENDGAME_STATE state = IO.endgame_state();
		if(state == null)
			return;
		
		if(state == ENDGAME_STATE.EXTEND)
		{
			_extension.set(true);
			_retraction.set(true);
		}
		else
		{
			_extension.set(false);
			_retraction.set(false);
		}
	}
}
