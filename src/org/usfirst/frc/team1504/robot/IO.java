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
	
	public static double intake_input()
	{
		return Utils.deadzone(Math.abs(_secondary.getRawAxis(Map.INTAKE_POWER_AXIS)));
	}
	public static boolean get_override_pickup()
	{
		return _secondary.getRawButton(Map.MASTER_OVERRIDE) && _secondary.getRawButton(Map.PICKUP_DOWN);
	}
	public static boolean get_override_lift()
	{
		return _secondary.getRawButton(Map.MASTER_OVERRIDE) && _secondary.getRawButton(Map.LIFT_DOWN);
	}
	public static boolean spin_rotors_in()
	{
		return _secondary.getRawButton(Map.SPIN_ROTORS_IN);
	}
	public static boolean spin_rotors_out()
	{
		return _secondary.getRawButton(Map.SPIN_ROTORS_OUT);
	}
	public static boolean open_flippers()
	{
		return _secondary.getRawButton(Map.OPEN_FLIPPERS);
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
	
	public static boolean get_pickup_on()
	{
		return _secondary.getRawButton(Map.PICKUP_ON);
	}
	public static boolean get_pickup_off()
	{
		return _secondary.getRawButton(Map.PICKUP_OFF);
	}
	public static boolean get_pickup_up()
	{
		return _secondary.getRawButton(Map.PICKUP_UP);
	}
	public static boolean get_pickup_down()
	{
		if( _secondary.getRawButton(Map.MASTER_OVERRIDE)) {
			return false;
		} else {
			return _secondary.getRawButton(Map.PICKUP_DOWN);
		}
	}
	public static boolean get_lift_on()
	{
		return _secondary.getRawButton(Map.LIFT_ON);
	}
	public static boolean get_lift_off()
	{
		return _secondary.getRawButton(Map.LIFT_OFF);
	}
	public static boolean get_lift_up()
	{
		return _secondary.getRawButton(Map.LIFT_UP);
	}
	public static boolean get_lift_down()
	{
		return _secondary.getRawButton(Map.LIFT_DOWN);
	}

}
