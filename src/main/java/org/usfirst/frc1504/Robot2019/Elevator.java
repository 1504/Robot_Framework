package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogInput;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Elevator implements Updatable {
	// Elevator
	private WPI_TalonSRX _bottom_actuator;
	private WPI_TalonSRX _top_actuator;

	public boolean lastElevatorButtonState = false;
	public boolean elevator_mode_state = false;
	public boolean elevator_mode = true;

	private static final Elevator instance = new Elevator();
	private DriverStation _ds = DriverStation.getInstance();

	static Potentiometer bottomPotentiometer;
	static Potentiometer topPotentiometer;

	public static Elevator getInstance() // sets instance
	{
		return instance;
	}

	private Elevator() // Elevator constructor
	{
		AnalogInput a = new AnalogInput(Map.BOTTOM_POTENTIOMETER_PORT);
		bottomPotentiometer = new AnalogPotentiometer(a, 100, 0);

		AnalogInput b = new AnalogInput(Map.TOP_POTENTIOMETER_PORT);
		topPotentiometer = new AnalogPotentiometer(b, 100, 0);

		_bottom_actuator = new WPI_TalonSRX(Map.BOTTOM_ACTUATOR_PORT);
		_top_actuator = new WPI_TalonSRX(Map.TOP_ACTUATOR_PORT);

		Update_Semaphore.getInstance().register(this);
	}

	public static void initialize() // initialize
	{
		getInstance();
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
			if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
				_bottom_actuator.set(Map.BOTTOM_PM_HATCH_LEVELS[current_level] - bottomPotentiometer.get());
			}
			_top_actuator.set(Map.TOP_PM_HATCH_LEVELS[current_level] - topPotentiometer.get());
		} catch (Exception e) {
			System.out.println("EXCEPTION: Potentiometer array out of bounds");
		}

		lastElevatorButtonState = IO.hid_up() || IO.hid_down() || IO.hid_home();
	
	}

	public void auto_ball_elevator_levels() {

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
			if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
				_bottom_actuator.set(Map.BOTTOM_PM_HATCH_LEVELS[current_level] - bottomPotentiometer.get());
			}
			_top_actuator.set(Map.TOP_PM_BALL_LEVELS[current_level] - topPotentiometer.get());
		} catch (Exception e) {
			System.out.println("EXCEPTION: Potentiometer array out of bounds");
		}

		lastElevatorButtonState = IO.hid_up() || IO.hid_down() || IO.hid_home();
	}

	public void update_actuator_speed() {
		if (potentiometer_check()) {
			if (IO.get_first_height_button()) {
				if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
					_bottom_actuator.set(Map.FIRST_HEIGHT - bottomPotentiometer.get());
				}
				_top_actuator.set((Map.FIRST_HEIGHT - topPotentiometer.get()) * Map.ELEVATOR_SPEED_MAGIC_NUMBER);
			} else if (IO.get_second_height_button()) {
				if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
					_bottom_actuator.set(Map.SECOND_HEIGHT - bottomPotentiometer.get());
				}
				_top_actuator.set((Map.SECOND_HEIGHT - topPotentiometer.get()) * Map.ELEVATOR_SPEED_MAGIC_NUMBER);
			} else if (IO.get_third_height_button()) {
				if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
					_bottom_actuator.set(Map.THIRD_HEIGHT - bottomPotentiometer.get());
				}
				_top_actuator.set((Map.THIRD_HEIGHT - topPotentiometer.get()) * Map.ELEVATOR_SPEED_MAGIC_NUMBER);
			}
		}
	}

	public boolean potentiometer_check() {
		if (Math.abs(bottomPotentiometer.get() - topPotentiometer.get()) < Map.POTENTIOMETER_LIMIT) {
			return true;
		} else {
			return false;
		}
	}

	public void change_elevator_state()
	{
		if(IO.get_elevator_mode() && IO.get_elevator_mode() != elevator_mode_state)
		{
			elevator_mode = !elevator_mode;
		}

		if(elevator_mode)
			auto_hatch_elevator_levels();
		else
			auto_ball_elevator_levels();
			
		elevator_mode_state = IO.get_elevator_mode();
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
			_top_actuator.set(IO.get_actuator_1_speed() * -1 * Map.ACTUATOR_MULTIPLIER);
			if(!(bottomPotentiometer.get() > Map.SWING_BOTTOM_ACTUATOR_LIMIT - Map.SWING_TOLERANCE && topPotentiometer.get() < Map.SWING_TOP_ACTUATOR_LIMIT)){
				_bottom_actuator.set(IO.get_actuator_2_speed() * -1 * Map.ACTUATOR_MULTIPLIER);
			}
			change_elevator_state();			

		}
	}
}
