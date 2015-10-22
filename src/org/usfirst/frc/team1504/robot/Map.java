package org.usfirst.frc.team1504.robot;

public class Map {
/**
 * Drive class things
 */
	
	// Joystick inputs
	public static final int DRIVE_FORWARDRIGHT_JOYSTICK = 0;
	public static final int DRIVE_ROTATION_JOYSTICK = 1;
	
	// Drive Motor enumeration
	public enum DRIVE_MOTOR { FRONT_LEFT, BACK_LEFT, BACK_RIGHT, FRONT_RIGHT }
	
	// Drive Motor ports
	public static final int FRONT_LEFT_TALON_PORT = 10;
	public static final int BACK_LEFT_TALON_PORT = 11;
	public static final int BACK_RIGHT_TALON_PORT = 12;
	public static final int FRONT_RIGHT_TALON_PORT = 13;
	public static final int[] DRIVE_MOTOR_PORTS = {
			FRONT_LEFT_TALON_PORT,
			BACK_LEFT_TALON_PORT,
			BACK_RIGHT_TALON_PORT,
			FRONT_RIGHT_TALON_PORT
	};
	
	// Drive Input magic numbers
	public static final double[] DRIVE_INPUT_MAGIC_NUMBERS = { 1.0, -1.0, 0.7 };
	
	// Drive Front Side changing
	public static final int DRIVE_FRONTSIDE_FRONT = 3;
	public static final int DRIVE_FRONTSIDE_BACK = 2;
	public static final int DRIVE_FRONTSIDE_RIGHT = 5;
	public static final int DRIVE_FRONTSIDE_LEFT = 4;
	
	// Glide gain
	public static final double[][] DRIVE_GAIN = {{0.0015, 0.0025, 0.003}, {0.008, 0.008, 0.008}};
	
	// Drive Output magic numbers - for getting everything spinning the correct direction
	public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS = { -1.0, -1.0, 1.0, 1.0 };	
	
/**
 * IO stuff
 */
	
	// Joystick raw axes
	public static final int JOYSTICK_Y_AXIS = 1;
	public static final int JOYSTICK_X_AXIS = 0;

}
