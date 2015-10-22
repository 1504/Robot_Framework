package org.usfirst.frc.team1504.robot;

import edu.wpi.first.wpilibj.Joystick;

public class IO
{
	private static Joystick _drive_forwardright = new Joystick(Map.DRIVE_FORWARDRIGHT_JOYSTICK);
	private static Joystick _drive_rotation = new Joystick(Map.DRIVE_ROTATION_JOYSTICK);
	
	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static double[] mecanum_input() {
		double[] inputs = new double[3];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS), 2) * Math.signum(_drive_forwardright.getRawAxis(Map.JOYSTICK_X_AXIS));// x
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w

		/*for (int i = 0; i < dircns.length - 1; i++) {
			dircns[i] = Utils.deadzone(dircns[i]);
		}*/
		
		return inputs;
	}
	
	public static double front_side() {
		if (_drive_rotation.getRawButton(Map.DRIVE_FRONTSIDE_BACK)) {
			return 180.0;
		} else if (_drive_rotation.getRawButton(Map.DRIVE_FRONTSIDE_RIGHT)) {
			return 270.0;
		} else if (_drive_rotation.getRawButton(Map.DRIVE_FRONTSIDE_FRONT)) {
			return 0.0;
		} else if (_drive_rotation.getRawButton(Map.DRIVE_FRONTSIDE_LEFT)) {
			return 90.0;
		}
		return Double.NaN;
	}
}
