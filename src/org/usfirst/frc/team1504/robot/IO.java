package org.usfirst.frc.team1504.robot;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.SECONDARY_JOYSTICK);
	private static Latch_Joystick _tertiary_shooter = new Latch_Joystick(Map.TERTIARY_JOYSTICK);
	private static Latch_Joystick _tertiary_aim = new Latch_Joystick(Map.TERTIARY_JOYSTICK);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	/**
	 * Override button
	 */
	public static boolean override()
	{
		return _secondary.getRawButton(Map.UTIL_OVERRIDE_BUTTON) ||
				_tertiary_shooter.getRawButton(Map.TERTIARY_FIRE_BUTTON);
	}
	
	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static double[] drive_input() {
		double[] inputs = new double[2];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		
		return inputs;
	}
	
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}
	
	/**
	 * Vision Interface stuff
	 */
	public static boolean vision_target_override()
	{
		return _secondary.getRawButton(Map.VISION_INTERFACE_OVERRIDE_BUTTON) ||
				_tertiary_aim.getRawButton(Map.TERTIARY_WIN_BUTTON) ||
				_tertiary_aim.getRawButton(Map.TERTIARY_AIM_BUTTON);
	}
	
	public static boolean vision_target_override_rising()
	{
		return _secondary.getRawButtonOnRisingEdge(Map.VISION_INTERFACE_OVERRIDE_BUTTON) ||
				_tertiary_aim.getRawButtonOnRisingEdge(Map.TERTIARY_WIN_BUTTON) ||
				_tertiary_aim.getRawButtonOnRisingEdge(Map.TERTIARY_AIM_BUTTON);
	}
}
