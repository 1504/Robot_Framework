package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Elevator.ELEVATOR_MODE;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

public class Hatch implements Updatable
{
	public enum HATCH_STATE {CLOSED, OPEN, HOLDING, DISABLED};
	private HATCH_STATE _state;

    private static final Hatch instance = new Hatch();
    private DriverStation _ds = DriverStation.getInstance();

    private DoubleSolenoid _hatch;
    private DigitalInput _hatch_input;
    private boolean _last_input = false;
	
    public static Hatch getInstance() // sets instance
	{
		return instance;
    }

    private Hatch()
    {
        _hatch = new DoubleSolenoid(Map.GRAB_PISTON_HIGHSIDE_PORT, Map.GRAB_PISTON_LOWSIDE_PORT);
        _hatch_input = new DigitalInput(Map.AUTO_GRABBER_SWITCH);
        _state = HATCH_STATE.DISABLED;
        
        Update_Semaphore.getInstance().register(this);
        System.out.println("Hatch initialized");
    }
    
    public static void initialize() // initialize
    {
        getInstance();
    }
	
	public HATCH_STATE getState()
	{
		return _state;
    }
    
    public boolean getHatchInput()
    {
        return !_hatch_input.get();
    }

    public void set_state(HATCH_STATE state)
    {
        if(Elevator.getInstance().getMode() == ELEVATOR_MODE.HATCH)
            _state = state;
    }

	private void update_state()
	{
        if(_state == HATCH_STATE.DISABLED)
            return;
        
        if(getHatchInput() && _state == HATCH_STATE.OPEN)
            _state = HATCH_STATE.HOLDING;

        if(Elevator.getInstance().getMode() == ELEVATOR_MODE.HATCH && IO.get_grabber() && !_last_input)
        {
            if(_state == HATCH_STATE.CLOSED)
                _state = HATCH_STATE.OPEN;
            else
                _state = HATCH_STATE.CLOSED;
        }
        _last_input = IO.get_grabber();

        if(_state == HATCH_STATE.CLOSED)
            _hatch.set(DoubleSolenoid.Value.kReverse);
        else
            _hatch.set(DoubleSolenoid.Value.kForward);
	}

	private void update_dashboard()
	{
		//uddb
		SmartDashboard.putString("Hatch State", _state.toString());
		SmartDashboard.putBoolean("Hatch Holding", getHatchInput());
		SmartDashboard.putBoolean("Hatch Open", _state == HATCH_STATE.HOLDING || _state == HATCH_STATE.OPEN);
	}

    public void semaphore_update() // updates robot information
	{
		update_dashboard();
		
		if (_ds.isDisabled()) // only runs in teleop
		{
			_state = HATCH_STATE.DISABLED;
			return;
		}

        if(_state == HATCH_STATE.DISABLED)
        {
            if(getHatchInput())
                _state = HATCH_STATE.HOLDING;
            else
                _state = HATCH_STATE.CLOSED;
        }

        if(Elevator.getInstance().getMode() == ELEVATOR_MODE.CARGO)
            _state = HATCH_STATE.CLOSED;

		update_state();
	}
}
