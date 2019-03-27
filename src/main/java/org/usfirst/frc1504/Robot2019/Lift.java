package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Servo;

public class Lift implements Updatable
{
	public enum LIFT_STATE {RETRACT, EXTEND, FRONT_UP};
	private LIFT_STATE _state = LIFT_STATE.RETRACT;

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
		return Math.atan2(-_accelerometer.getX(), _accelerometer.getY()) * 180.0 / Math.PI;
	}

	private void servo_adjustment()
	{
		if(_state == LIFT_STATE.EXTEND)
		{
			_front_servo.set(getAngle() * Map.SERVO_GAIN_VALUE);
			_back_servo.set(getAngle() * -Map.SERVO_GAIN_VALUE);
		}
		else
		{
			_front_servo.set(0.0);
			_back_servo.set(0.0);
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

		/*if(Math.abs(getAngle()) > 35.0)
		{
			System.out.println("Oh noes I fell over");
			_state = LIFT_STATE.RETRACT;
		}*/

		if(_state == LIFT_STATE.EXTEND)
			front = DoubleSolenoid.Value.kForward;
		
		if(_state == LIFT_STATE.EXTEND || _state == LIFT_STATE.FRONT_UP)
		{
			rear = DoubleSolenoid.Value.kForward;

			Elevator.getInstance().set(Elevator.ELEVATOR_MODE.HATCH, 0, false);
		}
		
		if(IO.hid() == 270)
			front = DoubleSolenoid.Value.kReverse;
		else if(IO.hid() == 90)
			rear = DoubleSolenoid.Value.kReverse;

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
			_state = (_state.ordinal() + 1 >= LIFT_STATE.values().length) ? 
				LIFT_STATE.RETRACT :
				LIFT_STATE.values()[_state.ordinal() + 1];
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
