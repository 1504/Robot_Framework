package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;

public class Lifter implements Updatable
{
	private static final Lifter instance = new Lifter();
	
	private static DriverStation _ds = DriverStation.getInstance();
	
	private static boolean _state = false;
	
	private static Solenoid _retraction;
	private static Solenoid _extension_lift;
	
    /**
     * Gets an instance of the Lifter
     *
     * @return The Lifter.
     */
    public static Lifter getInstance()
    {
        return Lifter.instance;
    }
    
	protected Lifter()
	{
		_retraction = new Solenoid(Map.LIFTER_RETRACTION_PORT);
		_extension_lift = new Solenoid(Map.LIFTER_EXTENSION_LIFT_PORT);
		
		set_solenoid();
		
		Update_Semaphore.getInstance().register(this);
		System.out.println("Drive Initialized");
	}
	
	public void toggle()
	{
		toggle(false);
	}
	
	public void toggle(boolean override)
	{
		set(!_state, override);
	}
	
	public void set(boolean state, boolean override)
	{
		if(state == false || _ds.getMatchTime() > 20.0 || override)
		{
			_state = state;
			set_solenoid();
		}
	}
	
	private void set_solenoid()
	{
		_retraction.set(_state);
		_extension_lift.set(_state);
	}

	public void semaphore_update()
	{
		Map.LIFTER_STATE_SET state = IO.lift_state();
		boolean override = IO.lift_override();
		if(state != null)
		{
			if(state == Map.LIFTER_STATE_SET.DOWN)
				set(false, override);
			if(state == Map.LIFTER_STATE_SET.UP)
				set(true, override);
			if(state == Map.LIFTER_STATE_SET.TOGGLE)
				toggle(override);
		}
	}
}
