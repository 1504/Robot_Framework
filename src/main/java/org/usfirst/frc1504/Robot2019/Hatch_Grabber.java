package org.usfirst.frc1504.Robot2019;
import org.usfirst.frc1504.Robot2019.Auto_Alignment.alignment_position;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

public class Hatch_Grabber implements Updatable {
	public static DoubleSolenoid _grab_piston; 
	public DoubleSolenoid _grabber;
	private static final Hatch_Grabber instance = new Hatch_Grabber();
	private DriverStation _ds = DriverStation.getInstance();
	public static Hatch_Grabber getInstance() // sets instance
	{
		return instance;
	}
	private Hatch_Grabber() // pickup constructor
	{
		_grab_piston = new DoubleSolenoid(Map.GRAB_PISTON_HIGHSIDE_PORT, Map.GRAB_PISTON_LOWSIDE_PORT); //0 and 1 are the ports, needs to be moved to the map
		_grab_piston.set(DoubleSolenoid.Value.kOff);
		Update_Semaphore.getInstance().register(this);
	}
	public static void initialize() //initialize
	{
		getInstance();
	}
	public static void update_grabber_state() {
		if(_grab_piston.get() == DoubleSolenoid.Value.kOff || _grab_piston.get() == DoubleSolenoid.Value.kReverse) 
		{
			open_grabber();
		}
		if(_grab_piston.get() == DoubleSolenoid.Value.kForward)
		{
			close_grabber();
		}
	}
	public static void open_grabber() 
	{
		_grab_piston.set(DoubleSolenoid.Value.kForward);
	}
	public static void close_grabber()
	{
		_grab_piston.set(DoubleSolenoid.Value.kReverse);
	}
	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{	
			if(IO.get_grabber())
			{
				update_grabber_state();
				Auto_Alignment.alignment_state = alignment_position.PLACEMENT_TRACKING;
			}
		}
	}
}
