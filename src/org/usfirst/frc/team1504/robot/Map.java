package org.usfirst.frc.team1504.robot;

public class Map {
	//methods
	
	public static double[][] get_side_delivery_sequence(double DIRECTIONAL_MULTIPLIER) {
		double[][] SIDE_DELIVERY_SEQUENCES = {{0.0, 0.0, (DIRECTIONAL_MULTIPLIER*(0.4)), 0, 1000}, {AUTON_DEFAULT_SPEED, 0.0, 0.0, 0, 1000}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 100}};
		return SIDE_DELIVERY_SEQUENCES;
	}

	public static double[][] get_return_to_spot_sequence(double DIRECTIONAL_MULTIPLIER) {
		double angle = MID_ANGLE;
		if(DIRECTIONAL_MULTIPLIER == -1.0)
			angle = MID_ANGLE + 4;
		double[][] RETURN_TO_SPOT = {{((-angle)*(-1*DIRECTIONAL_MULTIPLIER)), (-1*AUTON_FAST_SPEED), 0.0, 11, 1500}, {0.0, 0.0, (DIRECTIONAL_MULTIPLIER*(0.4)), 0, 2000}, {0.0, 0.0, 0.0, 0.0, 100}};
		return RETURN_TO_SPOT;
	}
	
	public static double[][] get_spot_to_switch_sequence(double DIRECTIONAL_MULTIPLIER) { //WARNING: HAS NOT BEEN TESTED
		double[][] SPOT_TO_SWITCH = {{(90-MID_ANGLE*(-1*DIRECTIONAL_MULTIPLIER)), AUTON_DEFAULT_SPEED, 0.0, 13, 2000}};
		return SPOT_TO_SWITCH;
	}
	
	public static double[][] get_alternate_switch_sequence(double DIRECTIONAL_MULTIPLIER) {
		double[][] ALTERNATE_SWITCH_SEQUENCES = {{(45*(DIRECTIONAL_MULTIPLIER)), 0.0, 0.0, 11, 1000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 13, 3000}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 100}, {0.0, 0.0, 0.0, 5, 100}};
		return ALTERNATE_SWITCH_SEQUENCES;
	}
	
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
	//public static final int PICKUP_ON = 8; 
	//public static final int PICKUP_OFF = 9;
	public static final int PICKUP_UP = 3; //X
	public static final int PICKUP_DOWN = 6; //A
	//public static final int LIFT_ON = 6;
	//public static final int LIFT_OFF = 7;
	public static final int LIFT_UP = 9; //B
	public static final int LIFT_DOWN = 4; //A
	public static final int SPIN_ROTORS_IN = 10; //left trigger axis
	public static final int SPIN_ROTORS_OUT = 5; //LT1 -- LB
	//public static final int OPEN_FLIPPERS = 3; //right trigger axis
	public static final int GRABBER = 2; //left joystick button
	public static final int CRASH_DETECTION = 7; //left joystick button
	// WE DON'T HAVE A BUTTON FOR DROPPING THE CUBE FROM THE ELEVATOR

	
/**
 * Pickup stuff
 */
	
		public static final int ARM_TALON_PORT = 20;
		
		public static final int ROLLER_TALON_PORT_LEFT = 21;
		public static final int ROLLER_TALON_PORT_RIGHT = 22;
		
		public static int ARM_UP_ANGLE = 1000; //not final because they vary from bots
		public static int ARM_DOWN_ANGLE = 1000;
		public static int ARM_MID_ANGLE = 1000;
		
		public static final double FLIPPER_MAGIC = 1.0;
		public static final double PICKUP_GAIN = 0.03;
		public static final double ROTATION_SPEED = 1;
		/*
		public static final int ENCODER_PORT_1 = 1;
		public static final int ENCODER_PORT_2 = 2;
		*/
/**
 * Elevator / Lift Stuff
 */
		
		public static final int LIFT_TALON_PORT = 30;
		
		public static final double LIFT_MOTOR_SPEED = 0.5;
		
		public static final double LIFT_MAX_HEIGHT = 10; 
		public static final double LIFT_MIN_HEIGHT = 0; 
		
		public static final double LIFT_GAIN = 0.3;
		public static final double LIFT_SAFETY_THRESHOLD = 5;
		public static final double LIFT_LOCK_RELEASE_RANGE = 0.7;
		public static final boolean LIMIT_SWITCH_EXISTS = false; 
/*
 * Winch Stuff
 */
		
		public static final int INTAKE_POWER_AXIS = 1;
		public static final int LIFT_AXIS = 5;
		public static final int LEFT_TALON_PORT = 40;
		public static final int RIGHT_TALON_PORT = 41;
		
		public static final int WINCH_CURRENT_LIMIT = 70;
		
		public static final int WINCH_POWER_AXIS = 1;
		
		public static final double WINCH_DIRECTION = -1.0;
		public static final double WINCH_SERVO_DEPLOYED = 180.0;
		public static final double WINCH_SERVO_STORED = 0.0;
		public static final double WINCH_BRAKE_TIMEOUT = 15.0;
		
/**
 * Drive class things
 */
	
	// Drive angle stuff
		public static final double DRIVE_ANGLE = 60.0; // We need to get this value from IO and vision code
		public static final double DRIVE_SPEED = 1.0; // This can be changed but it is 1 so we ram into things;
		
		
		
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
	public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS = { -1.0, -1.0, 1.0, 1.0 };
	
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
	public static final int MASTER_OVERRIDE = 6; //RT1 -- RB
	
	
/**
 * Camera
 */
	public static final long CAMERA_X = 180;
	public static final long CAMERA_Y = 160;
	
	
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
	public static final int LIFT_DROP_BUTTON = 10;
	
/**
 * Vision Interface stuff
 */
	public static final int sensor1 = 0;
	public static final int sensor2 = 1;
	public static final int sensor3 = 2;
	public static final int sensor4 = 3;
	public static final int sensor5 = 4;
	public static final int sensor6 = 5;
	
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

	public static final double CRASH_DETECTION_PORT = 3;
	public static final double GET_AVERAGE_TIME_DELAY = 500;
/**
 * Pneumatics stuff
 */
	public static final int GRAB_PISTON_HIGHSIDE_PORT = 0;
	public static final int GRAB_PISTON_LOWSIDE_PORT = 1;
	
	public static final int LIFT_PLATE_SOLENOID_PORT = 2; 
	
/**
 * Auton stuff	
 */
	public static final int AUTO_ALIGNMENT_BUTTON = 1;
	
	//norm/starting conditions arm down, lift down, facing alliance station
	public static final double HORIZONTAL_MULTIPLIER = 1.4;
	public static final double AUTON_DEFAULT_SPEED = -0.5;
	public static final double AUTON_FAST_SPEED = -0.8;
	public static final double CRASH_DETECTION_THRESHOLD_MULTIPLIER = 1.1;
	public static final int CRASH_DETECTION_DISTANCE_THRESHOLD = 700;
	public static final int CRASH_DETECTION_MODE = 0;
	public static final double DETECTION_DELAY = 1000;
	public static final double FIRE_TIME = 1500;
	public static final double ARM_OPENING_TIME = 100;
	public static final double AUTON_RUNTIME = 4000;
	public static final double AUTON_FAST_RUNTIME = 4000;
	public static final double AUTON_RETURN_MULTIPLIER = 0.75;
	public static final double SIDE_TO_MID_RUNTIME = 1500;
	public static final double MID_TO_CUBE_RUNTIME = 1500;
	public static double DIRECTIONAL_MULTIPLIER = 1.0; //Technically the multiplier for returning to spot
	public static double MID_ANGLE = 33.0; //Angles for moving from mid to switch
	public static double EDGE_ANGLE = 55.0; //Angles for moving from edge (far left or right starting positions) to switch
	
	public static final double[] PUSH_FORWARD = {0, -.35, 0.0, 11, 750};
	public static final double[][] TEST_SEQUENCES = {{0.0, (AUTON_FAST_SPEED), 0.0, 13, 1000}};
	public static final double[][] FORWARD_SHOOT_SEQUENCES = {{0.0, (AUTON_FAST_SPEED), 0.0, 13, 2800}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}};
	public static final double[][] FORWARD_SHOOT_GRAB_CUBE_SEQUENCES = {{0, (AUTON_FAST_SPEED), 0.0, 13, AUTON_FAST_RUNTIME}, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}, {0.0, -1.0*AUTON_FAST_SPEED, 0.0, 11, AUTON_FAST_RUNTIME*AUTON_RETURN_MULTIPLIER}, {90.0*DIRECTIONAL_MULTIPLIER, AUTON_DEFAULT_SPEED, 0.0, 11, SIDE_TO_MID_RUNTIME}, {0.0, 0.0, 0.5, 11, 1000}, {0.0, 0.0, 0.0, 1, 50}, {0.0, 0.0, 0.0, 6, 500}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 11, MID_TO_CUBE_RUNTIME}, {0.0, 0.0, 0.0, 5, 1000}, {0.0, 0.0, 0.0, 9, 50}, {90.0*DIRECTIONAL_MULTIPLIER, -1.0*AUTON_DEFAULT_SPEED, 0.0, 11, SIDE_TO_MID_RUNTIME}};//, {-32.735, AUTON_FAST_SPEED, 0.0, 11, AUTON_FAST_RUNTIME*AUTON_RETURN_MULTIPLIER}}; //Move straight to switch and eject cube
	public static final double[][] LEFT_SWITCH_FROM_MID_SEQUENCES = {{(-1*(MID_ANGLE + 4.0)), AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}}; //Move to switch at an angle and eject cube
	public static final double[][] RIGHT_SWITCH_FROM_MID_SEQUENCES = {{MID_ANGLE, AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}}; //Move to switch at an angle and eject cube
	
	public static final double[][] FORWARD_SEQUENCE = {{0, (AUTON_DEFAULT_SPEED), 0.0, 11, 2800}};

	public static final double[][] PICKUP_FROM_SPOT = {{0.0, 0.0, 0.5, 0, 1000}, {(-1*AUTON_DEFAULT_SPEED), 0.0, 0.0, 13, 2000}, {0.0, 0.0, 0.0, 2, 50}, {0.0, 0.0, 0.0, 6, 50}, {(-1*AUTON_DEFAULT_SPEED), 0.0, 0.0, 13, 1000}, {0.0, 0.0, 0.0, 5, 50}, {0.0, 0.0, 0.0, 9, 50}, {AUTON_DEFAULT_SPEED, 0.0, 0.0, 11, 2000}}; //Pickup a cube, return to common starting spot, and then move cube onto lift
	public static final double[][] RIGHT_SCALE_FROM_SPOT = {{90.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 13, 3000}, {0.0, 0.0, 0.0, 7, 500}, {0.0, 0.0, 0.0, 14, 100}};
	public static final double[][] LEFT_SCALE_FROM_SPOT = {{-90.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 13, 3000}, {0.0, 0.0, 0.0, 7, 500}, {0.0, 0.0, 0.0, 14, 100}};
	public static final double[][] AUTON_EXCHANGE_FROM_SPOT = {{32.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}};
	//{angle,strength/speed,turning,mode,time}
	
	
	//{forward, right, counterclock}
/**
 * Logger stuff
 */
	public static enum LOGGED_CLASSES { SEMAPHORE, DRIVE, GROUNDTRUTH, PNEUMATICS, SHOOTER }
	
	
public static final String TEAM_BANNER = "ICAgICAgICAgICBfX18gICAgICAgICAgICAgIF9fICBfXw0KICAgICAgICAgICAgfCBfIF8gIF8gICAgL3wgfF8gIC8gIFwgfF9ffA0KICAgICAgICAgICAgfCgtKF98fHx8ICAgIHwgX18pIFxfXy8gICAgfA0KDQogICAgICAgICAgICAgICAgICAgICAgICAgXy4NCiAgICAgICAgICAgICAgICAgICAgICAgLicgb28NCiAgICAgICAgICAgICAgICAgICAgICAgfCAgICA+DQogICAgICAgICAgICAgICAgICAgICAgLyAvIDogYC4NCiAgICAgICAgICAgICAgICAgICAgIHxfLyAvICAgfA0KICAgICAgICAgICAgICAgICAgICAgICB8LyAgd3cNCl9fXyAgICAgICAgX18gICAgICAgICAgICAgICAgICAgICAgX18NCiB8IHxfICBfICB8ICBcIF8gXyBfICBfIF8gXyB8XyBfICB8X18pXyBfICBfICAgIC4gXyAgXw0KIHwgfCApKC0gIHxfXy8oLV8pfF8pKC18IChffHxfKC0gIHwgICgtfCApKF8pfF98fHwgKV8pDQogICAgICAgICAgICAgICAgICB8ICAgICAgICAgICAgICAgICAgICAgICBfLw==";
public static final String ROBOT_BANNER = "ICAgICAvXCAgICAgL1wgICAgICAgICAgICAgICoNCiAgICAnLiBcICAgLyAsJyAgICAgICAgICAgICAgXCAgIH4NCiAgICAgIGAuXC0vLCcgICAgICAgICAgICAgXywsIFwge10NCiAgICAgICAoIFggICApICAgICAgICAgICAiLT1cO19cICUNCiAgICAgICwnLyBcYC5cICAgICAgICAgICAgXyBcXDsoIyklDQogICAgLicgLyAgIFwgYCwgICAgICAgICAgIF9cfCBcXyUlDQogICAgIFwvLS0tLS1cLycgICAgICAgICAgIFwgIFwvXCAgXA0KX19fX19fIHxfSF9fX3xfX19fX19fX19fX19fX18gKCApfn5+X19fXw0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfCBcDQogICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8gIC8=";
}
