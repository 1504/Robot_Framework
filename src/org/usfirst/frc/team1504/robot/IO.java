package org.usfirst.frc.team1504.robot;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.DRIVE_SECONDARY_JOYSTICK);

	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
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
	public static double winch_input()
	{
		if(IO.get_override_winch())
			return Utils.deadzone(Math.abs(_secondary.getRawAxis(Map.INTAKE_POWER_AXIS)));
		return 0;
	}
	public static boolean get_override_winch()
	{
		return _secondary.getRawButton(Map.WINCH_BUTTON);
	}
	
	public static boolean get_override_pickup()
	{
		return _secondary.getRawButton(Map.MASTER_OVERRIDE) && _secondary.getRawButton(Map.PICKUP_DOWN);
	}
	public static boolean get_override_lift()
	{
		return _secondary.getRawButton(Map.MASTER_OVERRIDE) && _secondary.getRawButton(Map.LIFT_DOWN);
	}
	public static boolean get_crash_detection()
	{
		return _secondary.getRawButton(Map.CRASH_DETECTION);
	}
	public static double get_intake_speed()
	{
		if ((_secondary.getRawButton(Map.SPIN_ROTORS_OUT) && ((_secondary.getRawAxis(Map.SPIN_ROTORS_IN)) > 0)))
		{
			double new_speed = 1.0 - _secondary.getRawAxis(Map.SPIN_ROTORS_IN);
			return -new_speed;
		}
		else if (_secondary.getRawButton(Map.SPIN_ROTORS_OUT))
		{
			return -1.0;
		}
		else
		{
			return _secondary.getRawAxis(Map.SPIN_ROTORS_IN);
		}
	}
	
	public static int open_flippers()
	{
		if(_secondary.getRawButton(Map.CRASH_DETECTION)) return 0;
		return (int) (Math.abs(_secondary.getRawAxis(Map.OPEN_FLIPPERS)-1)); //some math
	}
	public static boolean get_arm_up()
	{
		return _secondary.getRawButton(Map.PICKUP_UP);
	}
	public static boolean get_arm_down()
	{
		return ! _secondary.getRawButton(Map.MASTER_OVERRIDE) && _secondary.getRawButton(Map.PICKUP_DOWN);
	}
	public static boolean get_lift_drop()
	{
		return _secondary.getRawButton(Map.LIFT_DROP_BUTTON);
	}
	public static double lift_input()
	{
		return Utils.deadzone(_secondary.getRawAxis(Map.LIFT_AXIS));
	}
	public static boolean get_lift_up()
	{
		return _secondary.getRawButton(Map.LIFT_UP);
	}
	public static boolean get_lift_down()
	{
		return _secondary.getRawButton(Map.LIFT_DOWN);
	}
	public static double get_secondary_pov()
	{
		return _secondary.getPOV();
	}
	
	/**
	 * Gear stuff
	 */
	
	
	public static double shooter_turn_input()
	{
		return Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(Utils.deadzone(_secondary.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_secondary.getRawAxis(Map.JOYSTICK_X_AXIS));
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
	//	public static double set_front_side()
	//{
		//if(_secondary.getRawButtonLatch(Map.FRONT_SIDE_BUTTON))
			//	return 180.0;
		//else 
		//	return 0.0;	
	//}
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}
	public static boolean reset_front_side()
	{
		return (_drive_forward.getRawButton(Map.DRIVE_FRONTSIDE_FRONT));
	}
	public static boolean front_side_reverse()
	{
		return (_drive_rotation.getRawButton(Map.DRIVE_FRONTSIDE_BACK));
	}
	public static boolean get_drive_op_toggle()
	{
		return (_drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[3]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[3]));
	}

}
