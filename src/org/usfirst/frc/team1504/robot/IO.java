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
	 * Winch stuff
	 */
	
	public static double winch_input()
	{
		return Utils.deadzone(Math.abs(_secondary.getRawAxis(Map.WINCH_POWER_AXIS))) * Map.WINCH_DIRECTION;
	}
	
	public static boolean winch_override()
	{
		return _secondary.getRawButtonLatch(Map.WINCH_OVERRIDE_BUTTON);
	}
	
	public static boolean winch_deploy()
	{
		return _secondary.getRawButtonLatch(Map.WINCH_DEPLOY_BUTTON);
	}
	
	/**
	 * Gear stuff
	 */
	
	public static boolean gear_input()
	{
		return _secondary.getRawButtonLatch(Map.GEAR_BUTTON);
	}
	
	/**
	 * Shooter stuff
	 */
	
	public static boolean shooter_input()
	{
		if(_secondary.getRawButton(Map.SHOOTER_FIRE_BUTTON))
			System.out.println("shooter joystick input");
		return _secondary.getRawButton(Map.SHOOTER_FIRE_BUTTON);
	}
	
	public static boolean helicopter_input() //TODO
	{
		if(_secondary.getRawButton(Map.SHOOTER_FIRE_BUTTON))
			System.out.println("shooter joystick input");
		return _secondary.getRawButton(Map.SHOOTER_FIRE_BUTTON);
	}
	
	public static boolean shooter_override()
	{
		return _secondary.getRawButtonLatch(Map.SHOOTER_OVERRIDE_BUTTON);
	}
	
	public static boolean camera_shooter_input()
	{
		return _secondary.getRawButtonLatch(Map.CAMERA_SHOOTER_INPUT_BUTTON);
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
		public static double set_front_side()
	{
		if(_secondary.getRawButtonLatch(Map.FRONT_SIDE_BUTTON))
				return 180.0;
		else 
			return 0.0;	
	}
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}

	public static boolean get_intake_on()
	{
		return _secondary.getRawButton(Map.INTAKE_ON_BUTTON);
	}
	public static boolean get_intake_off()
	{
		return _secondary.getRawButton(Map.INTAKE_OFF_BUTTON);
	}
}
