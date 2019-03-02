package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Lift implements Updatable
{
    private static final Lift instance = new Lift();
    private DriverStation _ds = DriverStation.getInstance();

   	// We're in the endgame now
	public static DoubleSolenoid _end_lift_front;
	public static DoubleSolenoid _end_lift_back;

    public boolean lastEndLiftFrontState = false;
	public boolean lastEndLiftBackState = false;
    public static int lastEndLiftClimbState = 0;
    
    public static Lift getInstance() // sets instance
	{
		return instance;
    }

    private Lift() // Lift constructor
    {
        _end_lift_front = new DoubleSolenoid(Map.END_LIFT_FRONT_HIGHSIDE_PORT, Map.END_LIFT_FRONT_LOWSIDE_PORT);
		_end_lift_front.set(DoubleSolenoid.Value.kOff);

		_end_lift_back = new DoubleSolenoid(Map.END_LIFT_BACK_HIGHSIDE_PORT, Map.END_LIFT_BACK_LOWSIDE_PORT);
        _end_lift_back.set(DoubleSolenoid.Value.kOff);
        
        Update_Semaphore.getInstance().register(this);
    }
    
    public static void initialize() // initialize
    {
        getInstance();
    }

    public static void switch_end_lift_front_state() {
		if (_end_lift_front.get() == DoubleSolenoid.Value.kOff
				|| _end_lift_front.get() == DoubleSolenoid.Value.kReverse) {
			_end_lift_front.set(DoubleSolenoid.Value.kForward);
		} else {
			_end_lift_front.set(DoubleSolenoid.Value.kReverse);
		}
    }
    
    public static void switch_end_lift_back_state() {
		if (_end_lift_back.get() == DoubleSolenoid.Value.kOff
				|| _end_lift_back.get() == DoubleSolenoid.Value.kReverse) {
			_end_lift_back.set(DoubleSolenoid.Value.kForward);
		} else {
			_end_lift_back.set(DoubleSolenoid.Value.kReverse);
		}
	}
	
	public void update_climb_state_sequence()
	{
		if(IO.get_lift_activation())
		{

			if(lastEndLiftClimbState == 0) {
				switch_end_lift_front_state();
				switch_end_lift_back_state();
				lastEndLiftClimbState = 1;
			} else if((lastEndLiftClimbState == 1)) {
				switch_end_lift_front_state();
				lastEndLiftClimbState = 2;
			} else if(lastEndLiftClimbState == 2) {
				switch_end_lift_back_state();
				lastEndLiftClimbState = 0;
			} else {
				lastEndLiftClimbState = 0;
			}
		}
	}
    
    public void semaphore_update() // updates robot information
	{
		if (_ds.isOperatorControl() && !_ds.isDisabled()) // only runs in teleop
		{
			if (IO.get_endlift_front() && IO.get_endlift_front() != lastEndLiftFrontState) {
				switch_end_lift_front_state();
			}
			lastEndLiftFrontState = IO.get_endlift_front();

			if (IO.get_endlift_back() && IO.get_endlift_back() != lastEndLiftBackState) {
				switch_end_lift_back_state();
			}
            lastEndLiftBackState = IO.get_endlift_back();
        }		
	}
}
