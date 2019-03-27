package org.usfirst.frc1504.Robot2019;

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

	public static final int OVERRIDE_BUTTON = 5;

	public static final int LIFT_SEQUENCE_BUTTON = 10;
	public static final int LIFT_SEQUENCE_SECOND_BUTTON = 11;

	// Joystick inputs
	// public static final int SPIN_ROTORS_IN = 1; //left joystick axis
	public static final int GRABBER = 6; // RB (used to be 2 = B)
	public static final int CRASH_DETECTION = 7; // left joystick button

	public static final int END_LIFT_FRONT = 9; // (used to be 6 - RB We're in the endgame now
	public static final int END_LIFT_BACK = 5; //LB

	public static final int AUTO_PLACEMENT_BUTTON = 3;
	public static final int TOGGLE_MODE = 4; //Y

	public static final int HATCH_MODE_BUTTON = 4;
	public static final int CARGO_MODE_BUTTON = 2;

	public static final int AUTO_ALIGNMENT_BUTTON = 1;
	public static final int AUTO_GRABBER_SWITCH = 7;
	public static final int AUTO_LIFT_SWITCH = 0;

	//Axes
	public static final int REVERSE_ROTORS = 3;
	public static final int FORWARD_ROTORS = 2;
	

	//1 : A  - Auto_alignment_button
	//2 : B  - Grabber
	//3 : X  - Auto_placement_button
	//4 : Y  - extend/retract arms
	//5 : LB - Endgame Lift Front
	//6 : RB - Endgame Lift Back
	//7 : Back 
	//8 : Start
	//9 : Press left joystick
	//10 : Press right joystick
	
	//0 : X axis-left joystick
	//1 : Y axis-left joystick - spinning rotors
	//2 : LT
	//3 : RT
	//4 : X axis-right joystick
	//5 : Y axis-right joystick

	public static final int ELEVATOR_ACTUATOR_2 = 1;
	public static final int ELEVATOR_ACTUATOR_1 = 5;

	public static final double FIRST_HEIGHT = 60;
	public static final double SECOND_HEIGHT = 180;
	public static final double THIRD_HEIGHT = 300;

	public static final double POTENTIOMETER_LIMIT = 5;
	// Joystick-rotation buttons

	public static final int HOME_HEIGHT_BUTTON = 2;
	public static final int FIRST_HEIGHT_BUTTON = 3; // parallel arms height buttons
	public static final int SECOND_HEIGHT_BUTTON = 4;
	public static final int THIRD_HEIGHT_BUTTON = 5;

/**
 * Pickup stuff
 */
		public static final double ROLLER_SPEED_MULTIPLIER = 0.75;
		
		public static final int INTAKE_POWER_AXIS = 1;
		public static final int LIFT_AXIS = 5;
		
		public static final int ROLLER_TALON_PORT_LEFT = 20;
		public static final int ROLLER_TALON_PORT_RIGHT = 21;
		
		public static final double PICKUP_GAIN = 0.03;
		public static final double ROTATION_SPEED = 1;

		public static final int GRAB_PISTON_HIGHSIDE_PORT = 0;
		public static final int GRAB_PISTON_LOWSIDE_PORT = 1;
		
		public static final int ARM_EXTENSION_HIGHSIDE_PORT = 2;
		public static final int ARM_EXTENSION_LOWSIDE_PORT = 3;

		public static final int END_LIFT_FRONT_HIGHSIDE_PORT = 4;
		public static final int END_LIFT_FRONT_LOWSIDE_PORT = 5;
	
		public static final int END_LIFT_BACK_HIGHSIDE_PORT = 6;
		public static final int END_LIFT_BACK_LOWSIDE_PORT = 7;
/**
 * Elevator / Lift Stuff
 */
		public static final double ACTUATOR_MULTIPLIER = 0.75;

		public static final int BOTTOM_ACTUATOR_PORT = 30; //actuator ports
		public static final int TOP_ACTUATOR_PORT = 31; 	

		public static final int BOTTOM_POTENTIOMETER_PORT = 0;
		public static final int TOP_POTENTIOMETER_PORT = 1;
		
		public static final double LIFT_MOTOR_SPEED = 0.5;
		
		public static final double LIFT_MAX_HEIGHT = 10; 
		public static final double LIFT_MIN_HEIGHT = 0; 
		
		public static final double LIFT_GAIN = 0.3;
		public static final double LIFT_SAFETY_THRESHOLD = 5;
		public static final double LIFT_LOCK_RELEASE_RANGE = 0.7;
		public static final boolean LIMIT_SWITCH_EXISTS = false; 

		public static final double SWING_BOTTOM_SAFEZONE = 18.4;
		public static final double SWING_TOP_SAFEZONE = 17.0;
		public static final double SWING_SAFEZONE_TOLERANCE = 0.7;

		public static final int LEFT_SERVO_PORT = 0;
		public static final int RIGHT_SERVO_PORT = 1;

		public static final double OPEN_SERVO = 0.0;
		public static final double SEMI_OPEN_SERVO = 45.0;
		public static final double CLOSED_SERVO = 180.0;

		public static final double SERVO_GAIN_VALUE = 1.0/10.0;

		/**
		 * Potentiometer Values
		 * 
		 * These are the hard coded values returned from the potentiometers 
		 * for the positions we set the linear actuators to when delivering balls and hatch panels.
		 * First/Front/Lower Linear Actuator
		 * Second/Back/Higher Linear Actuator
		 */

		/**
		 * Elevator Delivery Settings for Hatch Panels
		 * 
		 * Linear Actuator = {Home, Low, Medium, High}
		 */
		public static final double[] BOTTOM_PM_HATCH_LEVELS = {18.4, 10.6, 10.6, 80.0};
		public static final double[] TOP_PM_HATCH_LEVELS = {12.0, 24.5, 72.4, 71.0};

		/**
		 * Elevator Delivery Settings for Balls
		 * 
		 * Linear Actuator = {Home, Low, Medium, High, Human Pickup}
		 */
		public static final double[] BOTTOM_PM_BALL_LEVELS = {10.4, 12.5, 30.9, 74.3, 16.1};
		public static final double[] TOP_PM_BALL_LEVELS = {13.8, 40.3, 63.9, 72.5, 58.8};

		// Elevator 
		public static final double ELEVATOR_GAIN = 0.2;


		
/**
 * Drive class things
 */
	
	// Drive Motor enumeration
	public static enum DRIVE_MOTOR {
		FRONT_LEFT, BACK_LEFT, BACK_RIGHT, FRONT_RIGHT
	}

	// Drive Motor ports
	public static final int FRONT_LEFT_TALON_PORT = 10;
	public static final int BACK_LEFT_TALON_PORT = 11;
	public static final int BACK_RIGHT_TALON_PORT = 12;
	public static final int FRONT_RIGHT_TALON_PORT = 13;
	public static final int[] DRIVE_MOTOR_PORTS = { FRONT_LEFT_TALON_PORT, BACK_LEFT_TALON_PORT, BACK_RIGHT_TALON_PORT,
			FRONT_RIGHT_TALON_PORT };

	public static final int END_LIFT_WHEELS_PORT = 40;

	// Drive disable orbit point buttons

	public static final int DRIVE_LEFT_BOTTOM = 7;
	public static final int DRIVE_LEFT_TOP = 6;
	public static final int DRIVE_RIGHT_BOTTOM = 10;
	public static final int DRIVE_RIGHT_TOP = 11;

	public static final int[] DRIVE_OP_BUTTONS = { DRIVE_LEFT_BOTTOM, DRIVE_LEFT_TOP, DRIVE_RIGHT_BOTTOM,
			DRIVE_RIGHT_TOP };

	// Drive Input magic numbers
	public static final double[] DRIVE_INPUT_MAGIC_NUMBERS = { -1.0, 1.0, -0.6 };
	public static final double DRIVE_INPUT_TURN_FACTOR = 0.2;

	public static final double DRIVE_INPUT_VISION_SPEED = 0.75;

	// Drive Front Side changing
	public static final int DRIVE_FRONTSIDE_FRONT = 1;
	public static final int DRIVE_FRONTSIDE_BACK = 1;

	// Glide gain
	public static final double[][] DRIVE_GLIDE_GAIN = { { 0.0015, 0.003 }, { 0.008, 0.008 } };

	// Drive Output magic numbers - for getting everything spinning the correct
	// direction
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
	public static final int MASTER_OVERRIDE = 6; // RT1 -- RB

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
	public static final double GEAR_DISTANCE = .06;
	public static final double GEAR_GAIN = .75;
	public static final double GEAR_MAX_OUTPUT_POWER = .25;

	/**
	 * Ground truth sensor
	 */
	public static final byte GROUNDTRUTH_QUALITY_MINIMUM = 40;
	public static final double GROUNDTRUTH_DISTANCE_PER_COUNT = 1.0;
	public static final double GROUNDTRUTH_TURN_CIRCUMFERENCE = 3.1416 * 1.25;
	public static final int GROUNDTRUTH_SPEED_AVERAGING_SAMPLES = 4;

	// Maximum (empirically determined) speed the robot can go in its three
	// directions.
	public static final double[] GROUNDTRUTH_MAX_SPEEDS = { 12.0, 5.0, 7.0 };

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
	public static final int[] ALIGNMENT_SENSOR_MAP = {0,1,2,3,4,5};
	public static final int sensor1 = 0;
	public static final int sensor2 = 1;
	public static final int sensor3 = 2;
	public static final int sensor4 = 3;
	public static final int sensor5 = 4;
	public static final int sensor6 = 5;

	// for jevois ball detection
	public static final double xcenter = 999; // shows how far away the ball is from center in (x)
	public static final double ycenter = 998; // shows how far away the ball is from center in (x)

	public static final int VISION_INTERFACE_PORT1 = 0;
	public static final int VISION_INTERFACE_PORT2 = 1;
	public static final double VISION_INTERFACE_VIDEO_WIDTH = 600; // 800; //480; // FOV UPDATE
	public static final double VISION_INTERFACE_VIDEO_FOV = 50;// 68;
	public static double VISION_INTERFACE_AIM_OFFSET = -7.5; // 0.85; // FOV UPDATE
	public static final double VISION_INTERFACE_TURN_GAIN = 0.02;// 0.1;
	public static final double VISION_INTERFACE_AIM_DEADZONE = 0.75; // 1; //0.075;
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

	public static final int LIFT_PLATE_SOLENOID_PORT = 2;

	// norm/starting conditions arm down, lift down, facing alliance station
	public static final double CRASH_DETECTION_THRESHOLD_MULTIPLIER = 1.1;
	public static final int CRASH_DETECTION_DISTANCE_THRESHOLD = 700;
	public static final int CRASH_DETECTION_MODE = 0;
	public static final double DETECTION_DELAY = 1000;

	// {angle,strength/speed,turning,mode,time}
	// {forward, right, counterclock}
	/**
	 * Logger stuff
	 */
	public static enum LOGGED_CLASSES {
		SEMAPHORE, DRIVE, GROUNDTRUTH, PNEUMATICS, SHOOTER
	}

	public static final String TEAM_BANNER = "ICAgICAgICAgICBfX18gICAgICAgICAgICAgIF9fICBfXw0KICAgICAgICAgICAgfCBfIF8gIF8gICAgL3wgfF8gIC8gIFwgfF9ffA0KICAgICAgICAgICAgfCgtKF98fHx8ICAgIHwgX18pIFxfXy8gICAgfA0KDQogICAgICAgICAgICAgICAgICAgICAgICAgXy4NCiAgICAgICAgICAgICAgICAgICAgICAgLicgb28NCiAgICAgICAgICAgICAgICAgICAgICAgfCAgICA+DQogICAgICAgICAgICAgICAgICAgICAgLyAvIDogYC4NCiAgICAgICAgICAgICAgICAgICAgIHxfLyAvICAgfA0KICAgICAgICAgICAgICAgICAgICAgICB8LyAgd3cNCl9fXyAgICAgICAgX18gICAgICAgICAgICAgICAgICAgICAgX18NCiB8IHxfICBfICB8ICBcIF8gXyBfICBfIF8gXyB8XyBfICB8X18pXyBfICBfICAgIC4gXyAgXw0KIHwgfCApKC0gIHxfXy8oLV8pfF8pKC18IChffHxfKC0gIHwgICgtfCApKF8pfF98fHwgKV8pDQogICAgICAgICAgICAgICAgICB8ICAgICAgICAgICAgICAgICAgICAgICBfLw==";
	public static final String ROBOT_BANNER = "ICAgICAvXCAgICAgL1wgICAgICAgICAgICAgICoNCiAgICAnLiBcICAgLyAsJyAgICAgICAgICAgICAgXCAgIH4NCiAgICAgIGAuXC0vLCcgICAgICAgICAgICAgXywsIFwge10NCiAgICAgICAoIFggICApICAgICAgICAgICAiLT1cO19cICUNCiAgICAgICwnLyBcYC5cICAgICAgICAgICAgXyBcXDsoIyklDQogICAgLicgLyAgIFwgYCwgICAgICAgICAgIF9cfCBcXyUlDQogICAgIFwvLS0tLS1cLycgICAgICAgICAgIFwgIFwvXCAgXA0KX19fX19fIHxfSF9fX3xfX19fX19fX19fX19fX18gKCApfn5+X19fXw0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfCBcDQogICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8gIC8=";
}
