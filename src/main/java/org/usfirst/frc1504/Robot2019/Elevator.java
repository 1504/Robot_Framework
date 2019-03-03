package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogInput;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator implements Updatable {
	// Elevator
	private WPI_TalonSRX _first_actuator;
	private WPI_TalonSRX _second_actuator;

	public boolean lastElevatorButtonState = false;

	private static final Elevator instance = new Elevator();
	private DriverStation _ds = DriverStation.getInstance();

	static Potentiometer firstPotentiometer;
	static Potentiometer secondPotentiometer;

	public static Elevator getInstance() // sets instance
	{
		return instance;
	}

	private Elevator() // Elevator constructor
	{
		AnalogInput a = new AnalogInput(Map.FIRST_POTENTIOMETER_PORT);
		firstPotentiometer = new AnalogPotentiometer(a, 100, 0);

		AnalogInput b = new AnalogInput(Map.SECOND_POTENTIOMETER_PORT);
		secondPotentiometer = new AnalogPotentiometer(b, 100, 0);

		_first_actuator = new WPI_TalonSRX(Map.FIRST_ACTUATOR_PORT);
		_second_actuator = new WPI_TalonSRX(Map.SECOND_ACTUATOR_PORT);

		Update_Semaphore.getInstance().register(this);
	}

	public static void initialize() // initialize
	{
		getInstance();
	}

	public void set_lift_speed(double speed) // sets both the front and back actuators
	{
		_first_actuator.set(speed);
		_second_actuator.set(speed);
	}

	public void auto_hatch_elevator_levels() {

		int current_level = 0;
		if (IO.hid_home()) {
			current_level = 0;
		} else if (IO.hid_up() && IO.hid_up() != lastElevatorButtonState) {
			if (current_level < 2) {
				current_level += 1;
			}
		} else if (IO.hid_down() && IO.hid_down() != lastElevatorButtonState) {
			if (current_level > 0) {
				current_level += 1;
			}
		}

		try {
			_first_actuator.set(Map.BOTTOM_PM_BALL_LEVELS[current_level] - firstPotentiometer.get());
			_second_actuator.set(Map.TOP_PM_BALL_LEVELS[current_level] - secondPotentiometer.get());
		} catch (Exception e) {
			System.out.println("EXCEPTION: Potentiometer array out of bounds");
		}

		lastElevatorButtonState = IO.hid_up() || IO.hid_down() || IO.hid_home();
	}

	public void update_actuator_speed() {
		if (potentiometer_check()) {
			if (IO.get_first_height_button()) {
				_first_actuator.set(Map.FIRST_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.FIRST_HEIGHT - secondPotentiometer.get());
			} else if (IO.get_second_height_button()) {
				_first_actuator.set(Map.SECOND_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.SECOND_HEIGHT - secondPotentiometer.get());
			} else if (IO.get_third_height_button()) {
				_first_actuator.set(Map.THIRD_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.THIRD_HEIGHT - secondPotentiometer.get());
			}
		}
	}

	public boolean potentiometer_check() {
		if (Math.abs(firstPotentiometer.get() - secondPotentiometer.get()) < Map.POTENTIOMETER_LIMIT) {
			return true;
		} else {
			return false;
		}
	}

	public void semaphore_update() // updates robot information
	{
		if (_ds.isOperatorControl() && !_ds.isDisabled()) // only runs in teleop
		{
			/*
			 * if(IO.extend_arm() && !Auto_Alignment.get_grabber_trigger() &&
			 * IO.extend_arm() != lastArmButtonState) { update_arm_state(); } else if
			 * (Auto_Alignment.get_grabber_trigger()) {
			 * _arm_extension.set(DoubleSolenoid.Value.kReverse); }
			 * if(Auto_Alignment.get_lift_trigger()) { update_end_lift_state(); }
			 * lastArmButtonState = IO.get_grabber();
			 */
			// set_lift_speed(IO.get_lift_speed());
			_first_actuator.set(IO.get_actuator_2_speed() * -1 * Map.ACTUATOR_MULTIPLIER);
			_second_actuator.set(IO.get_actuator_1_speed() * -1 * Map.ACTUATOR_MULTIPLIER);

		}
	}
}
