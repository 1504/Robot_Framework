package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Servo;

public class Lift implements Updatable
{
	public enum LIFT_STATE {RETRACT, EXTEND, FRONT_UP};
	private LIFT_STATE _state = LIFT_STATE.RETRACT;

	private static final double[] _setpoints = {30.0, 65.0};

    private static final Lift instance = new Lift();
    private DriverStation _ds = DriverStation.getInstance();

   	// We're in the endgame now
	private DoubleSolenoid _lift_front;
	private DoubleSolenoid _lift_back;

	private int _last_input = 0;
	private boolean _lockout;
	

	private BuiltInAccelerometer _accelerometer = new BuiltInAccelerometer(Accelerometer.Range.k8G);
	private Servo _front_servo;
	private Servo _back_servo;
	private AnalogInput _front_analog;
	private AnalogInput _back_analog;
	private AnalogPotentiometer _front_position;
	private AnalogPotentiometer _back_position;
	private double _front_offset, _back_offset;
	
    public static Lift getInstance() // sets instance
	{
		return instance;
    }

    private Lift() // Lift constructor
    {
        _lift_front = new DoubleSolenoid(Map.END_LIFT_FRONT_HIGHSIDE_PORT, Map.END_LIFT_FRONT_LOWSIDE_PORT);
		_lift_back = new DoubleSolenoid(Map.END_LIFT_BACK_HIGHSIDE_PORT, Map.END_LIFT_BACK_LOWSIDE_PORT);
		
		_front_servo = new Servo(Map.LEFT_SERVO_PORT);
		_back_servo = new Servo(Map.RIGHT_SERVO_PORT);

		_front_analog = new AnalogInput(2);
		_back_analog  = new AnalogInput(3);
		_front_position = new AnalogPotentiometer(_front_analog, 100.0);
		_back_position  = new AnalogPotentiometer(_back_analog,  100.0);

		_lockout = false;
        
		Update_Semaphore.getInstance().register(this);
		System.out.println("Lift initialized");
    }
    
    public static void initialize() // initialize
    {
        getInstance();
    }
	
	public LIFT_STATE getState()
	{
		return _state;
	}

	public double getAngle()
	{
		return front_position() - back_position();
	}

	public double front_position()
	{
		return (_front_position.get() - _front_offset);
	}

	public double back_position()
	{
		return (_back_position.get() - _back_offset);
	}

	public boolean get_moving()
	{
		int setpoint = IO.lift_level_3() ? 1 : 0;
		boolean back_moving = back_position() < _setpoints[setpoint];
		switch(_state)
		{
			case EXTEND:
				return (front_position() < _setpoints[setpoint]) && back_moving;
			case FRONT_UP:
				return (front_position() > 1.0) && back_moving;
			default:
				return false;
		}
	}

	private void servo_adjustment()
	{
		switch(_state)
		{
			case EXTEND:
				if(!IO.lift_level_3() && front_position() >= _setpoints[0])
					_front_servo.set(1.0);
				else
					_front_servo.set(getAngle() * Map.SERVO_GAIN_VALUE);
				
				if(!IO.lift_level_3() && back_position() >= _setpoints[0])
					_back_servo.set(1.0);
				else
					_back_servo.set(getAngle() * -Map.SERVO_GAIN_VALUE);

				break;

			case FRONT_UP:
				_front_servo.set(0.0);
				_back_servo.set(1.0);
				break;

			default:
				_front_servo.set(0.0);
				_back_servo.set(0.0);
				_front_offset = _front_servo.get();
				_back_offset = _back_servo.get();
		}
	}

	public boolean get_lifting()
	{
		return _state == LIFT_STATE.EXTEND;
	}

	private void update_state()
	{
		DoubleSolenoid.Value front = DoubleSolenoid.Value.kReverse;
		DoubleSolenoid.Value rear = DoubleSolenoid.Value.kReverse;

		if(Math.abs(getAngle()) > 25.0)
		{
			System.out.println("!!! !!! !!! Oh noes I fell over !!! !!! !!!");
			_state = LIFT_STATE.RETRACT;
		}

		if(_state == LIFT_STATE.RETRACT)
		{
			_front_offset = _front_position.get();
			_back_offset = _back_position.get();
		}

		if(_state == LIFT_STATE.EXTEND)
			front = DoubleSolenoid.Value.kForward;
		
		if(_state == LIFT_STATE.EXTEND || _state == LIFT_STATE.FRONT_UP)
		{
			rear = DoubleSolenoid.Value.kForward;

			Elevator.getInstance().set(Elevator.ELEVATOR_MODE.HATCH, 0, false);
		}
		
		/*if(IO.hid() == 270)
			front = DoubleSolenoid.Value.kReverse;
		else if(IO.hid() == 90)
			rear = DoubleSolenoid.Value.kReverse;*/

		// Tip correction - Failsafe
		/*if(_state == LIFT_STATE.EXTEND && Math.abs(getAngle()) > 10.0) // TIPPING A LOT
		{
			if(Math.signum(getAngle()) < 0)
				front = DoubleSolenoid.Value.kReverse;
			else
				rear = DoubleSolenoid.Value.kReverse;
		}*/
		
		_lift_front.set(front);
		_lift_back.set(rear);
	}

	private void update_dashboard()
	{
		//uddb
		SmartDashboard.putString("Lift State", _state.toString());
		SmartDashboard.putBoolean("Lift Lockout", _lockout);
		SmartDashboard.putNumber("Lift Front Air Restrictor", _front_servo.get());
		SmartDashboard.putNumber("Lift Back Air Restrictor", _back_servo.get());
		SmartDashboard.putNumber("Lift Accelerometer Y", _accelerometer.getY());
		SmartDashboard.putNumber("Lift Accelerometer X", _accelerometer.getX());
		SmartDashboard.putNumber("Lift Accelerometer Abgle", getAngle());
	}

    public void semaphore_update() // updates robot information
	{
		update_dashboard();
		
		if (_ds.isDisabled()) // only runs in teleop
		{
			_state = LIFT_STATE.RETRACT;
			_lockout = false;
			return;
		}

		// Must have pressed one button then both together to make endgame lift fire
		// Must release both buttons to move on to the next position in sequence
		if(!_lockout && IO.get_lift_activation() == 3 && (_last_input == 1 || _last_input == 2))
		{
			boolean reset = _state.ordinal() + 1 >= LIFT_STATE.values().length;
			if(reset)
				Arduino.getInstance().setPartyMode(true);
			_state = (reset) ? LIFT_STATE.RETRACT : LIFT_STATE.values()[_state.ordinal() + 1];
			_lockout = true;
		}
		else if (IO.get_lift_activation() == 0)
		{
			_lockout = false;
		}
		_last_input = IO.get_lift_activation();	

		update_state();
		servo_adjustment();
	}
}
