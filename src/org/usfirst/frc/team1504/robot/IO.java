package org.usfirst.frc.team1504.robot;

public class IO
{
	private static Latch_Joystick _drive_forwardright = new Latch_Joystick(Map.DRIVE_FORWARDRIGHT_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_ROTATION_JOYSTICK);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static double[] mecanum_input() {
		double[] inputs = new double[3];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS));// x
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		
		//inputs[0] = _drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS);
		//inputs[1] = _drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS);
		//inputs[2] = _drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS);
		
		return inputs;
	}
	
	public static double front_side() {
		if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_BACK)) {
			return 180.0;
		} else if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_RIGHT)) {
			return 270.0;
		} else if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_FRONT)) {
			return 0.0;
		} else if (_drive_rotation.getRawButtonLatch(Map.DRIVE_FRONTSIDE_LEFT)) {
			return 90.0;
		}
		return Double.NaN;
	}
}
