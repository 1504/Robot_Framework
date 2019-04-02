package org.usfirst.frc1504.Robot2019;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;

public class Elevator implements Updatable {
	// Elevator
	public enum ELEVATOR_MODE {HATCH, CARGO, INIT};
	private ELEVATOR_MODE _mode = ELEVATOR_MODE.INIT;

	private String[] _setpoint_names = {"Home 1", "Home 2", "Low", "Mid", "High"};
	//private double[][] _bottom_setpoints = {{18.4, 10.4}, {18.4, 16.1}, {10.6, 12.5}, {10.6, 30.9}, {80.0, 74.3}};
	//private double[][] _top_setpoints = {{12.0, 13.8}, {12.0, 58.8}, {24.5, 40.3}, {72.4, 63.9}, {71.0, 72.5}};
	private double[][] _bottom_setpoints = {{18.4, 10.4}, {18.4, 16.1}, {10.6, 12.5}, {10.6, 30.9}, {80.0, 74.3}};
	private double[][] _top_setpoints = {{13.0, 13.8}, {13.0, 58.8}, {23.8, 41.3}, {72.4, 64.9}, {71.0, 73.5}};
	private boolean _elevator_enable = false;
	private int _setpoint = 0;

	private int _last_input = -1;
	private boolean _last_mode_input = false;
	private int _override_setpoint_count = 0;
	private boolean _moving;

	//private WPI_TalonSRX _top_actuator;
	//private WPI_TalonSRX _bottom_actuator;
	private CANSparkMax _top_actuator;
	private CANSparkMax _bottom_actuator;

	public boolean lastElevatorButtonState = false;

	private static final Elevator instance = new Elevator();
	private DriverStation _ds = DriverStation.getInstance();

	private Potentiometer _bottom_potentiometer;
	private Potentiometer _top_potentiometer;
	private CANEncoder _bottom_encoder;
	private CANEncoder _top_encoder;
	private Glide _bottom_glide;
	private Glide _top_glide;


	public static Elevator getInstance() // sets instance
	{
		return instance;
	}

	private Elevator() // Elevator constructor
	{
		AnalogInput a = new AnalogInput(Map.BOTTOM_POTENTIOMETER_PORT);
		_bottom_potentiometer = new AnalogPotentiometer(a, 100, 0);

		AnalogInput b = new AnalogInput(Map.TOP_POTENTIOMETER_PORT);
		_top_potentiometer = new AnalogPotentiometer(b, 100, 0);

		//_top_actuator = new WPI_TalonSRX(Map.TOP_ACTUATOR_PORT);
		//_bottom_actuator = new WPI_TalonSRX(Map.BOTTOM_ACTUATOR_PORT);
		//_top_actuator.setNeutralMode(NeutralMode.Brake);
		//_bottom_actuator.setNeutralMode(NeutralMode.Brake);

		_top_actuator = new CANSparkMax(Map.TOP_ACTUATOR_PORT, com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless);
		_bottom_actuator = new CANSparkMax(Map.BOTTOM_ACTUATOR_PORT, com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless);
		_top_actuator.setIdleMode(CANSparkMax.IdleMode.kCoast);
		_bottom_actuator.setIdleMode(CANSparkMax.IdleMode.kCoast);
		_top_encoder = _top_actuator.getEncoder();
		_bottom_encoder = _bottom_actuator.getEncoder();
		_top_encoder.setPositionConversionFactor((10.0 * 14.0)/(2.5 * 96.0));    // 2.5:1 linear actuator, 14:96 gear ratio, 10 turn potentiometer
		_bottom_encoder.setPositionConversionFactor((10.0 * 14.0)/(2.5 * 96.0)); // 2.5:1 linear actuator
		_top_encoder.setPosition(_top_potentiometer.get());
		_bottom_encoder.setPosition(_bottom_potentiometer.get());

		_top_glide = new Glide(.007, .025);
		_bottom_glide = new Glide(.007, .025);

		Preferences p = Preferences.getInstance();
		int i, j;
		for(i = 0; i < _top_setpoints.length; i++)
		{
			for(j = 0; j < _top_setpoints[i].length; j++)
			{
				if(!p.containsKey("Elevator_top_" + i + "_" + j))
					p.putDouble("Elevator_top_" + i + "_" + j, _top_setpoints[i][j]);
				if(!p.containsKey("Elevator_bottom_" + i + "_" + j))
					p.putDouble("Elevator_bottom_" + i + "_" + j, _bottom_setpoints[i][j]);
				
				_top_setpoints[i][j] = p.getDouble("Elevator_top_" + i + "_" + j, _top_setpoints[i][j]);
				_bottom_setpoints[i][j] = p.getDouble("Elevator_bottom_" + i + "_" + j, _bottom_setpoints[i][j]);
			}
		}

		Update_Semaphore.getInstance().register(this);
		System.out.println("Elevator initialized");
	}

	public static void initialize() // initialize
	{
		getInstance();
	}

	public ELEVATOR_MODE getMode()
	{
		return _mode;
	}

	public boolean getMoving()
	{
		return _moving;
	}

	public int getSetpoint()
	{
		return _setpoint;
	}

	private void compute_nearest_setpoint()
	{
		int i;
		for(i = 0; i < _top_setpoints.length; i++)
		{
			if(Math.abs(_top_setpoints[i][_mode.ordinal()] - _top_potentiometer.get()) < 3.0 &&
				Math.abs(_bottom_setpoints[i][_mode.ordinal()] - _bottom_potentiometer.get()) < 3.0)
			{
				_setpoint = i;
				_elevator_enable = true;
				return;
			}
		}
		_setpoint = 0;
		_elevator_enable = false;
	}

	private void compute_mode()
	{
		/*if(IO.toggle_elevator_mode() && !_last_mode_input)
		{
			if(_mode == ELEVATOR_MODE.CARGO)
				_mode = ELEVATOR_MODE.HATCH;
			else if(_mode == ELEVATOR_MODE.HATCH && Hatch.getInstance().getState() != Hatch.HATCH_STATE.HOLDING)
				_mode = ELEVATOR_MODE.CARGO;
		}
		_last_mode_input = IO.toggle_elevator_mode();*/
		if(IO.elevator_mode() == ELEVATOR_MODE.HATCH)
			_mode = ELEVATOR_MODE.HATCH;
		else if(IO.elevator_mode() == ELEVATOR_MODE.CARGO && (Hatch.getInstance().getState() != Hatch.HATCH_STATE.HOLDING || IO.override()))
			_mode = ELEVATOR_MODE.CARGO;
	}

	private void compute_setpoint()
	{
		if(IO.override())
			return;
		
		int input = IO.hid();
		
		if(input != -1)
			_elevator_enable = true;
		if(Lift.getInstance().get_lifting())
			_elevator_enable = false;

		switch(input)
		{
			case -1: // No input
				break;
			case 270: // HOME 1
				_setpoint = 0;
				break;
			case 90: // HOME 2
				_setpoint = 1;
				break;
			default:
				if(_last_input != -1)
					break;
				if(input == 0) // up
				{
					if(_setpoint < 2)
					{
						_setpoint = 2;
						break;
					}
					if(_setpoint < _top_setpoints.length - 1)
						_setpoint++;
				}
				else if(input == 180)
				{
					if(_setpoint > 0)
						_setpoint--;
				}
		}
		_last_input = input;
	}

	private void update()
	{
		// Disable elevator if encoder and potentiometer drift too far apart
		if(	Math.abs(_top_encoder.getPosition() - _top_potentiometer.get()) > 3.0 ||
			Math.abs(_bottom_encoder.getPosition() - _bottom_potentiometer.get()) > 3.0)
			_elevator_enable = false;
		
		if(!_elevator_enable || _mode == ELEVATOR_MODE.INIT)
		{
			if(!IO.override())
			{
				_top_actuator.set(0.0);
				_bottom_actuator.set(0.0);
			}
			_moving = false;
			return;
		}
		
		//double top_error = (_top_setpoints[_setpoint][_mode.ordinal()] - _top_potentiometer.get());
		//double bottom_error = (_bottom_setpoints[_setpoint][_mode.ordinal()] - _bottom_potentiometer.get());
		double top_error = (_top_setpoints[_setpoint][_mode.ordinal()] - _top_encoder.getPosition());
		double bottom_error = (_bottom_setpoints[_setpoint][_mode.ordinal()] - _bottom_encoder.getPosition());
		
		top_error = Math.pow(top_error / 1.4, 2.0) * Math.signum(top_error);
		bottom_error = Math.pow(bottom_error  / 1.4, 2.0) * Math.signum(bottom_error);

		if(_mode == ELEVATOR_MODE.HATCH)
			top_error += /*Math.abs*/(IO.get_intake_speed()) * 2.5;//2.5;
		
		if(Math.abs(top_error) < 1.0)
			top_error = 0.0;
		if(Math.abs(bottom_error) < 1.0)
			bottom_error = 0.0;

		if(top_error == 0 && bottom_error == 0)
			_moving = false;
		else
			_moving = true;
		
		/*if(top_error < 0.0 && _bottom_potentiometer.get() < Map.SWING_BOTTOM_SAFEZONE && Math.abs(bottom_error) > Map.SWING_SAFEZONE_TOLERANCE)
			_top_actuator.set(0.0);
		else*/
			_top_actuator.set(_top_glide.gain_adjust(top_error * Map.ELEVATOR_GAIN * (Math.signum(top_error) < 0.0 ? 0.3 : 0.8)));

		// Don't run bottom actuator up unless the top arm won't intersect the post
		/*if(bottom_error > 0.0 && _top_potentiometer.get() < Map.SWING_TOP_SAFEZONE && _top_actuator.get() != 0.0)
			_bottom_actuator.set(0.0);
		else*/
			_bottom_actuator.set(_bottom_glide.gain_adjust(bottom_error * Map.ELEVATOR_GAIN * (Math.signum(bottom_error) < 0.0 ? 0.3 : 0.8)));
	}

	private void update_dashboard()
	{
		//uddb
		SmartDashboard.putString("Elevator Mode", _mode.toString());
		SmartDashboard.putBoolean("Elevator Enabled", _elevator_enable);
		SmartDashboard.putNumber("Elevator Setpoint", _setpoint);
		SmartDashboard.putString("Elevator Setpoint Name", _setpoint_names[_setpoint]);
		SmartDashboard.putNumber("Elevator Top Actuator Position", _top_potentiometer.get());
		SmartDashboard.putNumber("Elevator Bottom Actuator Position", _bottom_potentiometer.get());
		SmartDashboard.putNumber("Elevator Top Encoder Position", _top_encoder.getPosition());
		SmartDashboard.putNumber("Elevator Bottom Encoder Position", _bottom_encoder.getPosition());
		SmartDashboard.putNumber("Elevator Top Actuator Current", _top_actuator.getOutputCurrent());
		SmartDashboard.putNumber("Elevator Bottom Actuator Current", _bottom_actuator.getOutputCurrent());
		SmartDashboard.putNumber("Elevator Top Actuator Speed", _top_actuator.getEncoder().getVelocity());
		SmartDashboard.putNumber("Elevator Bottom Actuator Speed", _bottom_actuator.getEncoder().getVelocity());
		
		int mode = (_mode.ordinal() < _top_setpoints[0].length) ? _mode.ordinal() : 0;

		SmartDashboard.putNumber("Elevator Top Actuator Commanded Position", _top_setpoints[_setpoint][mode]);
		SmartDashboard.putNumber("Elevator Bottom Actuator Commanded Position", _bottom_setpoints[_setpoint][mode]);
		SmartDashboard.putNumber("Elevator Top Actuator Position Error", (_top_setpoints[_setpoint][mode] - _top_potentiometer.get()));
		SmartDashboard.putNumber("Elevator Bottom Actuator Position Error", (_bottom_setpoints[_setpoint][mode] - _bottom_potentiometer.get()));
	}

	public void set(ELEVATOR_MODE mode, int position, boolean enable_override)
	{
		_mode = mode;
		_setpoint = position;

		if(!_elevator_enable && enable_override)
			_elevator_enable = true;
	}

	public void semaphore_update() // updates robot information
	{
		update_dashboard();  

		if (_ds.isDisabled()) // only runs in teleop
		{
			_mode = ELEVATOR_MODE.INIT;
			_elevator_enable = false;
			return;
		}

		if(_mode == ELEVATOR_MODE.INIT)
		{
			if(Hatch.getInstance().getState() == Hatch.HATCH_STATE.DISABLED)
				return;
			//_mode = Hatch.getInstance().getState() == Hatch.HATCH_STATE.CLOSED ? ELEVATOR_MODE.CARGO : ELEVATOR_MODE.HATCH;
			_mode = ELEVATOR_MODE.HATCH;
			compute_nearest_setpoint();
			if(_mode == ELEVATOR_MODE.HATCH)
			{
//				Auto_Alignment.alignment_state = Auto_Alignment.alignment_position.PLACEMENT_TRACKING;
			}
		}
		
		if(IO.override())
		{
			_elevator_enable = false;
			_bottom_actuator.set(IO.get_bottom_actuator_speed() * .7);
			_top_actuator.set(IO.get_top_actuator_speed() * .7);

			_top_encoder.setPosition(_top_potentiometer.get());
			_bottom_encoder.setPosition(_bottom_potentiometer.get());

			if(_mode != ELEVATOR_MODE.INIT)
			{
				if(IO.hid() != -1)
					_override_setpoint_count++;
				else
					_override_setpoint_count = 0;
				
				if(_override_setpoint_count == 40) // > 800 milliseconds
				{
					_top_setpoints[_setpoint][_mode.ordinal()] = _top_potentiometer.get();
					_bottom_setpoints[_setpoint][_mode.ordinal()] = _bottom_potentiometer.get();
					Preferences.getInstance().putDouble("Elevator_top_" + _setpoint + "_" + _mode.ordinal(), _top_setpoints[_setpoint][_mode.ordinal()]);
					Preferences.getInstance().putDouble("Elevator_bottom_" + _setpoint + "_" + _mode.ordinal(), _bottom_setpoints[_setpoint][_mode.ordinal()]);
					
					System.out.println("Elevator setpoint for " + _mode.toString() + " - " + _setpoint_names[_setpoint] + " (" + _setpoint + ") updated to (" + _top_setpoints[_setpoint][_mode.ordinal()] + ", " + _bottom_setpoints[_setpoint][_mode.ordinal()] + ")");
				}
			}
		}

		compute_mode();
		compute_setpoint();
		update();
	}
}
