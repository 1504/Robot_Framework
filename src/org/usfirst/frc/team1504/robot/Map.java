package org.usfirst.frc.team1504.robot;

public class Map {
/**
 * Utilities
 */
	public static final double UTIL_JOYSTICK_DEADZONE = 0.05;
	
	
/**
 * Inputs
 */
	public static final int DRIVE_CARTESIAN_JOYSTICK = 0;
	public static final int DRIVE_POLAR_JOYSTICK = 1;
	public static final int DRIVE_SECONDARY_JOYSTICK = 2;
	
	// Joystick inputs


	
/**
 * Pickup stuff
 */
	
		public static final int ARM_TALON_PORT = 38;
		
		public static final int ROLLER_TALON_PORT_LEFT = 40;
		public static final int ROLLER_TALON_PORT_RIGHT = 41;
		
		public static final double ROLLER_SPEED = 0.7;
		
		public static final int ARM_UP_ANGLE = 1000;
		public static final int ARM_DOWN_ANGLE = 1000;
		public static final int ARM_MID_ANGLE = 1000;
		
		public static final double ARM_SPEED = 0.3;
		
		public static final double FLIPPER_MAGIC = 1.0;
		
		
/**
 * Elevator / Lift Stuff
 */
		
		public static final int LIFT_TALON_PORT = 42;
		
		public static final int LIFT_UP = 1;
		public static final int LIFT_DOWN = -1;
		
		public static final double LIFT_MOTOR_SPEED = 0.5;
		
		public static final double LIFT_MAX_HEIGHT = 10; 
		public static final double LIFT_MIN_HEIGHT = 0; 
		
/*
 * Winch Stuff
 */
		
		public static final int INTAKE_POWER_AXIS = 1;
		
/**
 * Drive class things
 */
	
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
	
	// Drive disable orbit point buttons
	
	public static final int DRIVE_LEFT_BOTTOM = 7;
	public static final int DRIVE_LEFT_TOP = 6;
	public static final int DRIVE_RIGHT_BOTTOM = 10;
	public static final int DRIVE_RIGHT_TOP = 11;
	
	public static final int[]DRIVE_OP_BUTTONS = {
			DRIVE_LEFT_BOTTOM, 
			DRIVE_LEFT_TOP, 
			DRIVE_RIGHT_BOTTOM, 
			DRIVE_RIGHT_TOP};

	
	// Drive Input magic numbers
	public static final double[] DRIVE_INPUT_MAGIC_NUMBERS = { -1.0, 1.0, -0.6 };
	public static final double DRIVE_INPUT_TURN_FACTOR = 0.2;
	
	public static final double DRIVE_INPUT_VISION_SPEED = 0.75;
	
	// Drive Front Side changing
	public static final int DRIVE_FRONTSIDE_FRONT = 1;
	public static final int DRIVE_FRONTSIDE_BACK = 1;
	
	// Glide gain
	public static final double[][] DRIVE_GLIDE_GAIN = {{0.0015, 0.003}, {0.008, 0.008}};
	
	// Drive Output magic numbers - for getting everything spinning the correct direction
	public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS = { 1.0, 1.0, -1.0, -1.0 };
	
	public static final int DRIVE_MAX_UNLOGGED_LOOPS = 15;
	
/**
 * Robot config stuff
 */	
	public static final double ROBOT_WARNING_TIME_LONG = 20.0;
	public static final double ROBOT_WARNING_TIME_SHORT = 10.0;

/**
 * Buttons
 */
	public static final int VISION_INTERFACE_CAMERA_PORT_BUTTON = 5;
	public static final int PICKUP_OVERRIDE = 6;
	public static final int LIFT_OVERRIDE = 7;
	public static final int MASTER_OVERRIDE = 8;
	
/**
 * Arduino addresses
 */
	public static final byte ARDUINO_ADDRESS = 64;
	public static final byte GROUNDTRUTH_ADDRESS = 01;
	public static final byte MAIN_LIGHTS_ADDRESS = 02;
	public static final byte FRONTSIDE_LIGHTS_ADDRESS = 03;
	public static final byte GEAR_LIGHTS_ADDRESS = 04;
	public static final byte SHOOTER_LIGHTS_ADDRESS = 05;
	public static final byte INTAKE_LIGHTS_ADDRESS = 06;
	public static final byte PARTY_MODE_ADDRESS = 07;
	public static final byte PULSE_SPEED_ADDRESS = 11;
		
/**
 * Gear stuff
 */
	public static final double GEAR_DISTANCE = .096;
	public static final double GEAR_GAIN = .75;
	public static final double GEAR_MAX_OUTPUT_POWER = .25;
	
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
 * Vision Interface stuff
 */
	public static final int VISION_INTERFACE_PORT1 = 0;
	public static final int VISION_INTERFACE_PORT2 = 1;
	public static final double VISION_INTERFACE_VIDEO_WIDTH = 600; //800; //480; // FOV UPDATE
	public static final double VISION_INTERFACE_VIDEO_FOV = 50;//68;
	public static double VISION_INTERFACE_AIM_OFFSET = -7.5; // 0.85; // FOV UPDATE 
	public static final double VISION_INTERFACE_TURN_GAIN = 0.02;//0.1;
	public static final double VISION_INTERFACE_AIM_DEADZONE = 0.75; //1; //0.075;
	public static final double VISION_INTERFACE_TURN_MAX_OUTPUT = 0.15;
	public static final int VISION_INTERFACE_IMAGE_CAPTURE_SETTLE_TIMEOUT = 1750;

	
	public static final double VISION_HUE1 = 42.0;
	public static final double VISION_HUE2 = 146.0;
	public static final double VISION_SAT1 = 119.0;
	public static final double VISION_SAT2 = 246.0;
	public static final double VISION_VAL1 = 243.0755395683453;
	public static final double VISION_VAL2 = 255.0;

	
	
/**
 * Pneumatics stuff
 */
	public static final int PNEUMATICS_HIGHSIDE_PORT = 0;
	public static final int PNEUMATICS_LOWSIDE_PORT = 1;
	
/**
 * Auton stuff	
 */
	public static final double[][][] AUTON_RIGHT_DROP_SEQUENCES = {{{0.5, 0.0, 0.0, 0, 3000}},{{0.25, 0.0, 0.0, 0, 1000},{0.0, 0.0, 0.25, 0, 1000},{0.25, 0.0, 0.0, 0, 3000},{0.0, 25.0, 0.0, 0, 1000},{0.5, 0.0, 0.0, 0, 2000}, {-0.25, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.0, 0, 1000}}};
	 /*If at right side and right end is the goal(ram)
	  *If at right side and left end is goal (forward, turn left, forward, turn right, ram)
     */
	public static final double[][][] AUTON_LEFT_DROP_SEQUENCES = {{{0.5, 0.0, 0.0, 0, 3000}},{{0.25, 0.0, 0.0, 0, 1500}, {0.0, 0.25, 0.0, 0, 1000}, {0.25, 0.0, 0.0, 0, 3500}, {0.0, 0.0, 0.25, 0, 1000}, {0.5, 0.0, 0.0, 0.0, 3000}}};
    /*If at left side and left side is the goal (ram)
     *If at left side and right end is the goal (forward, right, forward, left, ram)
     */
    public static final double[][][] AUTON_MID_DROP_SEQUENCES = {{{0.25, 0.0, 0.0, 0, 1000}, {0.0, 0.25, 0.0, 0, 1000}, {0.25, 0.0, 0.0, 0, 1750}, {0.0, 0.0, 0.25, 0, 1000}, {0.5, 0.0, 0.0, 0, 2500}}, {{0.25, 0.0, 0.0, 0, 1000},{0.0, 0.25, 0.0, 0, 1000},{0.25, 0.0, 0.0, 0, 2000},{0.0, 0.0, 25.0, 0, 1000},{0.5, 0.0, 0.0, 0, 2000}}};
    /*If at middle and left end is the goal (ram, forward)
	* If at middle and right end is goal (ram, forward, right, forward, left, ram)
	*/
	public static final double[][][] AUTON_RIGHT_SCALE_SEQUENCES = {{{}}};
	public static final double[][][] AUTON_LEFT_SCALE_SEQUENCES = {{{}}};
	public static final double[][][] AUTON_MID_SCALE_SEQUENCES = {{{}}};
	
	public static final double[][] AUTON_PORTAL_FROM_MID_SEQUENCES = {{0.0, 0.25, 0.0, 0, 1000}, {0.1, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 25.0, 0, 1000}, {0.25, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.0, 1, 1000}, {0.0, 0.0, 0.0, 3, 1000}, {0.0, 0.0, 0.0, 9, 1000}};
	public static final double[][] AUTON_PICKUP_FROM_MID_SEQUENCES =  {{0.1, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.0, 1, 500}, {0.0, 0.0, 0.0, 2, 500}, {0.0, 0.0, 0.0, 6, 3000}, {0.1, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.0, 0, 1500}, {0.0, 0.0, 0.0, 5, 500}};
	
	/*Going to the exchange from mid
	 *Picking up a cube from mid
	 * */
/**
 * Logger stuff
 */
	public static enum LOGGED_CLASSES { SEMAPHORE, DRIVE, GROUNDTRUTH, PNEUMATICS, SHOOTER }
	
	
public static final String TEAM_BANNER = "ICAgICAgICAgICBfX18gICAgICAgICAgICAgIF9fICBfXw0KICAgICAgICAgICAgfCBfIF8gIF8gICAgL3wgfF8gIC8gIFwgfF9ffA0KICAgICAgICAgICAgfCgtKF98fHx8ICAgIHwgX18pIFxfXy8gICAgfA0KDQogICAgICAgICAgICAgICAgICAgICAgICAgXy4NCiAgICAgICAgICAgICAgICAgICAgICAgLicgb28NCiAgICAgICAgICAgICAgICAgICAgICAgfCAgICA+DQogICAgICAgICAgICAgICAgICAgICAgLyAvIDogYC4NCiAgICAgICAgICAgICAgICAgICAgIHxfLyAvICAgfA0KICAgICAgICAgICAgICAgICAgICAgICB8LyAgd3cNCl9fXyAgICAgICAgX18gICAgICAgICAgICAgICAgICAgICAgX18NCiB8IHxfICBfICB8ICBcIF8gXyBfICBfIF8gXyB8XyBfICB8X18pXyBfICBfICAgIC4gXyAgXw0KIHwgfCApKC0gIHxfXy8oLV8pfF8pKC18IChffHxfKC0gIHwgICgtfCApKF8pfF98fHwgKV8pDQogICAgICAgICAgICAgICAgICB8ICAgICAgICAgICAgICAgICAgICAgICBfLw==";
public static final String ROBOT_BANNER = "ICAgICAvXCAgICAgL1wgICAgICAgICAgICAgICoNCiAgICAnLiBcICAgLyAsJyAgICAgICAgICAgICAgXCAgIH4NCiAgICAgIGAuXC0vLCcgICAgICAgICAgICAgXywsIFwge10NCiAgICAgICAoIFggICApICAgICAgICAgICAiLT1cO19cICUNCiAgICAgICwnLyBcYC5cICAgICAgICAgICAgXyBcXDsoIyklDQogICAgLicgLyAgIFwgYCwgICAgICAgICAgIF9cfCBcXyUlDQogICAgIFwvLS0tLS1cLycgICAgICAgICAgIFwgIFwvXCAgXA0KX19fX19fIHxfSF9fX3xfX19fX19fX19fX19fX18gKCApfn5+X19fXw0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfCBcDQogICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8gIC8=";
}
