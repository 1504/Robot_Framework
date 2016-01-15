package org.usfirst.frc.team1504.robot;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_FORWARDRIGHT_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_ROTATION_JOYSTICK);
	
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.SECONDARY_JOYSTICK);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
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
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		
		return inputs;
	}
	
	public static double front_side() {
		if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK)) {
			return 180.0;
		} else if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_FRONT)) {
			return 0.0;
		}
		return Double.NaN;
	}
	
	/**
	 * Lifter stuff
	 */
	
	public static Map.LIFTER_STATE_SET lift_state()
	{
		if(_secondary.getRawButtonLatch(Map.LIFTER_DOWN_BUTTON))
			return Map.LIFTER_STATE_SET.DOWN;
		if(_secondary.getRawButtonLatch(Map.LIFTER_UP_BUTTON))
			return Map.LIFTER_STATE_SET.UP;
		if(_secondary.getRawButtonOnRisingEdge(Map.LIFTER_TOGGLE_BUTTON))
			return Map.LIFTER_STATE_SET.TOGGLE;
		return null;
	}
	public static boolean lift_override()
	{
		return _secondary.getRawButtonLatch(Map.LIFTER_OVERRIDE_BUTTON);
	}
}
