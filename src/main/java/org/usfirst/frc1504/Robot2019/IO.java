package org.usfirst.frc1504.Robot2019;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.DRIVE_SECONDARY_JOYSTICK);

	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	public static boolean override()
	{
		return _secondary.getRawButton(Map.OVERRIDE_BUTTON);
	}

	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static boolean camera_port()
	{
		return _secondary.getRawButton(Map.VISION_INTERFACE_CAMERA_PORT_BUTTON);
	}
	
	/**
	 * Pickup stuff
	 */
	
	public static double override_input()
	{
		return Utils.deadzone(_secondary.getRawAxis(Map.INTAKE_POWER_AXIS));
	}
	public static boolean get_crash_detection()
	{
		return _secondary.getRawButton(Map.CRASH_DETECTION);
	}
	public static double get_top_actuator_speed()
	{
		return Math.pow(-_secondary.getRawAxis(Map.ELEVATOR_ACTUATOR_2), 3.0);
	}
	public static boolean toggle_elevator_mode()
	{
		return _secondary.getRawButton(Map.TOGGLE_MODE);
	}
	public static Elevator.ELEVATOR_MODE elevator_mode()
	{
		if(_secondary.getRawButton(Map.HATCH_MODE_BUTTON))
			return Elevator.ELEVATOR_MODE.HATCH;
		if(_secondary.getRawButton(Map.CARGO_MODE_BUTTON))
			return Elevator.ELEVATOR_MODE.CARGO;
		return null;
	}
	public static double get_bottom_actuator_speed()
	{		
		return Math.pow(-_secondary.getRawAxis(Map.ELEVATOR_ACTUATOR_1), 3.0);
	}
	public static boolean get_grabber()
	{
		return _secondary.getRawButton(Map.GRABBER);
	}
	public static int get_lift_activation()
	{
		return (_drive_rotation.getRawButton(Map.LIFT_SEQUENCE_BUTTON) ? 1 : 0) +
		 (_drive_rotation.getRawButton(Map.LIFT_SEQUENCE_SECOND_BUTTON) ? 2 : 0);
	}

	public static boolean lift_level_3()
	{
		return _drive_rotation.getThrottle() > 0.75;
	}

	public static double get_intake_speed()
	{
		return _secondary.getRawAxis(Map.FORWARD_ROTORS) - _secondary.getRawAxis(Map.REVERSE_ROTORS);
	}
	/** Hid Stuff
	 * 
	 */
	public static int hid()
	{
		return _secondary.getPOV();
	}
	public static boolean hid_up()
	{
		if(hid() == 0)
		{
			return true;
		}
		else 
			return false;
	}
	public static boolean hid_home()
	{
		if(hid() == 90 || hid() == 270)
		{
			return true;
		}
		else 
			return false;
	}
	public static boolean hid_down()
	{
		if(hid() == 180)
		{
			return true;
		}
		else 
			return false;
	}





	/**
	 * Drive stuff
	 */
	
	public static double[] drive_input() {
		double[] inputs = new double[3];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS));//x
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));//w
		return inputs;
	}
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}
	public static boolean reset_front_side()
	{
		return (_drive_forward.getRawButton(Map.DRIVE_FRONTSIDE_FRONT));
	}
	public static boolean get_drive_op_toggle()
	{
		return (_drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[3]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[3]));
	}	
	public static boolean get_auto_alignment()
	{
		return _secondary.getRawButton(Map.AUTO_ALIGNMENT_BUTTON);
	}
	public static boolean get_auto_placement() 
	{
		return _secondary.getRawButton(Map.AUTO_PLACEMENT_BUTTON);
	}
	
	public static boolean get_home_height_button()
	{
		return _drive_rotation.getRawButton(Map.HOME_HEIGHT_BUTTON);
	}
	public static boolean get_first_height_button()
	{
		return _drive_rotation.getRawButton(Map.FIRST_HEIGHT_BUTTON);
	}
	public static boolean get_second_height_button()
	{
		return _drive_rotation.getRawButton(Map.SECOND_HEIGHT_BUTTON);
	}
	public static boolean get_third_height_button()
	{
		return _drive_rotation.getRawButton(Map.THIRD_HEIGHT_BUTTON);
	}
}
