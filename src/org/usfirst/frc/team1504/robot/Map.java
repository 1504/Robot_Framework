package org.usfirst.frc.team1504.robot;

public class Map {
/**
 * Utilities
 */
	public static final double UTIL_JOYSTICK_DEADZONE = 0.09;
	
/**
 * Drive class things
 */
	
	// Joystick inputs
	public static final int DRIVE_FORWARDRIGHT_JOYSTICK = 0;
	public static final int DRIVE_ROTATION_JOYSTICK = 1;
	public static final int SECONDARY_JOYSTICK = 2;
	
	// Drive Motor enumeration
	public static enum DRIVE_MOTOR { FRONT_LEFT, BACK_LEFT, BACK_RIGHT, FRONT_RIGHT }
	
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
	public static final double[] DRIVE_INPUT_MAGIC_NUMBERS = { -1.0, 0.7 };
	public static final double DRIVE_INPUT_TURN_FACTOR = 0.2;
	public static final int DRIVE_INPUT_TURN_FACTOR_OVERRIDE_BUTTON = 1;
	
	// Drive Front Side changing
	public static final int DRIVE_FRONTSIDE_FRONT = 3;
	public static final int DRIVE_FRONTSIDE_BACK = 2;
	
	// Glide gain
	public static final double[][] DRIVE_GLIDE_GAIN = {{0.0015, 0.003}, {0.008, 0.008}};
	
	// Drive Output magic numbers - for getting everything spinning the correct direction
	public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS = { -1.0, -1.0, 1.0, 1.0 };	
	
	
/**
 * Ground truth sensor
 */
	public static final byte GROUNDTRUTH_QUALITY_MINIMUM = 40;
	public static final double GROUNDTRUTH_DISTANCE_PER_COUNT = 1.0;
	public static final double GROUNDTRUTH_TURN_CIRCUMFERENCE = 3.1416 * 1.25;
	public static final int GROUNDTRUTH_SPEED_AVERAGING_SAMPLES = 4;
	
	// Maximum (empirically determined) speed the robot can go in its three directions. 
	public static final double[] GROUNDTRUTH_MAX_SPEEDS = {12.0, 5.0, 7.0};
	
	
/**
 * IO stuff
 */
	
	// Joystick raw axes
	public static final int JOYSTICK_Y_AXIS = 1;
	public static final int JOYSTICK_X_AXIS = 0;
	
/**
 * Lifter stuff
 */
	public static final int LIFTER_RETRACTION_PORT = 1;
	public static final int LIFTER_EXTENSION_LIFT_PORT = 1;
	
	public static final int LIFTER_UP_BUTTON = 2;
	public static final int LIFTER_DOWN_BUTTON = 3;
	public static final int LIFTER_TOGGLE_BUTTON = 4;
	public static final int LIFTER_OVERRIDE_BUTTON = 5;
	
	public static enum LIFTER_STATE {UP, DOWN, TOGGLE}
	
/**
 * Wheel shooter stuff
 */
	public static final int WHEEL_SHOOTER_INTAKE_MOTOR = 20;
	public static final int WHEEL_SHOOTER_LEFT_SHOOTER_MOTOR = 21;
	public static final int WHEEL_SHOOTER_RIGHT_SHOOTER_MOTOR = 22;
	
	public static final int WHEEL_SHOOTER_INTAKE_ON_BUTTON = 1;
	public static final int WHEEL_SHOOTER_INTAKE_OFF_BUTTON = 1;
	public static final int WHEEL_SHOOTER_INTAKE_REVERSE_BUTTON = 1;
	public static final int WHEEL_SHOOTER_SPINUP_BUTTON = 1;
	public static final int WHEEL_SHOOTER_FIRE_BUTTON = 1;
	
	public static final double WHEEL_SHOOTER_INTAKE_SPEED = 0.75;
	public static final double WHEEL_SHOOTER_TARGET_SPEED = 0.75;
	public static final double WHEEL_SHOOTER_GAIN = 0.01;
	public static final double WHEEL_SHOOTER_SPEED_GOOD_DEADBAND = 1.0;
	
/**
 * Logger stuff
 */
	public static enum LOGGED_CLASSES { SEMAPHORE, DRIVE, GROUNDTRUTH }

	
public static String ROBOT_BANNER = "ICAgICAvXCAgICAgL1wgICAgICAgICAgICAgICoNCiAgICAnLiBcICAgLyAsJyAgICAgICAgICAgICAgXCAgIH4NCiAgICAgIGAuXC0vLCcgICAgICAgICAgICAgXywsIFwge10NCiAgICAgICAoIFggICApICAgICAgICAgICAiLT1cO19cICUNCiAgICAgICwnLyBcYC5cICAgICAgICAgICAgXyBcXDsoIyklDQogICAgLicgLyAgIFwgYCwgICAgICAgICAgIF9cfCBcXyUlDQogICAgIFwvLS0tLS1cLycgICAgICAgICAgIFwgIFwvXCAgXA0KX19fX19fIHxfSF9fX3xfX19fX19fX19fX19fX18gKCApfn5+X19fXw0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfCBcDQogICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8gIC8=";
}
