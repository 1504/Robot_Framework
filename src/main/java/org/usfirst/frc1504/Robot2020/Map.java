package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

public class Map {

    /** Utilities */
    public static final double UTIL_JOYSTICK_DEADZONE = 0.05;

    /** Camera */
	public static final long CAMERA_X = 180;
    public static final long CAMERA_Y = 160;
    public static final int VISION_INTERFACE_CAMERA_PORT_BUTTON = 5;

    /** Arduino Addresses */
    public static final byte ARDUINO_ADDRESS = 64;
	public static final byte GROUNDTRUTH_ADDRESS = 1;
	public static final byte ARM_LIGHTS_ADDRESS = 2;
	public static final byte ARM_MODE_ADDRESS = 3;
	public static final byte POST_LIGHTS_ADDRESS = 4;
	public static final byte POST_MODE_ADDRESS = 5;
	public static final byte PARTY_MODE_ADDRESS = 8;
    public static final byte PULSE_SPEED_ADDRESS = 11;
    
    /** Ground Truth Sensor */
    public static final byte GROUNDTRUTH_QUALITY_MINIMUM = 40;
	public static final double GROUNDTRUTH_DISTANCE_PER_COUNT = 1.0;
	public static final double GROUNDTRUTH_TURN_CIRCUMFERENCE = 3.1416 * 1.25;
    public static final int GROUNDTRUTH_SPEED_AVERAGING_SAMPLES = 4;
    // Maximum (empirically determined) speed the robot can go in its three directions.
	public static final double[] GROUNDTRUTH_MAX_SPEEDS = { 12.0, 5.0, 7.0 };

    /** IO Stuff */
    // Joystick raw axes
	public static final int JOYSTICK_Y_AXIS = 1;
	public static final int JOYSTICK_X_AXIS = 0;
    public static final int LIFT_DROP_BUTTON = 10;
    
    /** Robot Config Stuff */
	public static final double ROBOT_WARNING_TIME_LONG = 20.0;
	public static final double ROBOT_WARNING_TIME_SHORT = 10.0;
    
    /** Crash Detection Stuff */
	public static final double CRASH_DETECTION_PORT = 3;
	public static final double GET_AVERAGE_TIME_DELAY = 500;
	public static final double CRASH_DETECTION_THRESHOLD_MULTIPLIER = 1.1;
	public static final int CRASH_DETECTION_DISTANCE_THRESHOLD = 700;
	public static final int CRASH_DETECTION_MODE = 0;
	public static final double DETECTION_DELAY = 1000;

    /**
     * Drive Class Things
     */

    // Drive Motor enumeration
	public static enum DRIVE_MOTOR { FRONT_LEFT, BACK_LEFT, BACK_RIGHT, FRONT_RIGHT }

    // Drive Motor ports
	public static final int FRONT_LEFT_TALON_PORT = 11;
	public static final int BACK_LEFT_TALON_PORT = 12;
	public static final int BACK_RIGHT_TALON_PORT = 13;
	public static final int FRONT_RIGHT_TALON_PORT = 10;
	//public static final int[] DRIVE_MOTOR_PORTS = { FRONT_LEFT_TALON_PORT, BACK_LEFT_TALON_PORT, BACK_RIGHT_TALON_PORT, FRONT_RIGHT_TALON_PORT };
	public static final int[] DRIVE_MOTOR_PORTS = { 10, 11, 12, 13 };

    // Drive disable orbit point buttons
	public static final int DRIVE_LEFT_BOTTOM = 7;
	public static final int DRIVE_LEFT_TOP = 6;
	public static final int DRIVE_RIGHT_BOTTOM = 10;
	public static final int DRIVE_RIGHT_TOP = 11;

	public static final int[] DRIVE_OP_BUTTONS = { DRIVE_LEFT_BOTTOM, DRIVE_LEFT_TOP, DRIVE_RIGHT_BOTTOM,
			DRIVE_RIGHT_TOP };

    // Drive Input magic numbers
	public static final double[] DRIVE_INPUT_MAGIC_NUMBERS = { -1.0, -1.0, -0.6 };
	public static final double DRIVE_INPUT_TURN_FACTOR = 0.2;
	public static final double DRIVE_INPUT_VISmanual_ion_speed = 0.75;
	// Drive Front Side changing
	public static final int DRIVE_FRONTSIDE_FRONT = 1;
	public static final int DRIVE_FRONTSIDE_BACK = 1;
	// Glide gain
	public static final double[][] DRIVE_GLIDE_GAIN = { { 0.0015, 0.003 }, { 0.008, 0.008 } };
	// Drive Output magic numbers - for getting everything spinning the correct direction
	public static final double[] DRIVE_OUTPUT_MAGIC_NUMBERS = { 1.0, 1.0, -1.0, -1.0 };
	public static final int DRIVE_MAX_UNLOGGED_LOOPS = 15;

    
    /**
     * Inputs & Controls
     */

    public static final int DRIVE_CARTESIAN_JOYSTICK = 0;
	public static final int DRIVE_POLAR_JOYSTICK = 1;
	public static final int DRIVE_SECONDARY_JOYSTICK = 2;

    /** Controler Mappings */
    // 1  : A                      - Lightsaber extend
	// 2  : B                      - Lightsaber toggle retract
	// 3  : X                      - Ion cannon shoot
	// 4  : Y                      - Tractor Beam toggle
	// 5  : Left Shoulder Button   - Manual
	// 6  : Right Shoulder Button  - Rotation Control
	// 7  : Back                   - Ion Cannon Setpoint
	// 8  : Start                  - God Mode
	// 9  : Press left joystick    - 
	// 10 : Press right joystick   - 
	
	// 0  : X axis-left joystick   - Tokamak Override
	// 1  : Y axis-left joystick   - 
	// 2  : LT                     - Tractor Beam
	// 3  : RT                     - Ion Cannon
	// 4  : X axis-right joystick  - Lightsaber Manual Control
	// 5  : Y axis-right joystick  - 

    /** Controler Map Assignment */

        // Ion Cannon
        public static final int MANUAL_ION_SPEED = 3;
        public static final int ION_SETPOINT_BUTTON = 7;

        // Lightsaber
        public static final int LIGHTSABER_BUTTON = 1;
        public static final int LIGHTSABER_INVERTER = 2;
        public static final int LS_SPEED_CTRL_JOYSTICK = 4;

        // Pizza
        public static final int PIZZA_SLICER_BUTTON = 5;
        public static final int ROTATION_CONTROL_BUTTON = 6;

        // Tokamak
        public static final int TOKAMAK_OVERRIDE = 2; // trigger

        // Tractor Beam
        public static final int ACTIVATE_TRACTOR_BEAM_BUTTON = 4;


        /** Overrides */
        public static final int GOD_MODE = 8;

    /**
     * Unique Game Values Go Here
     */

        /** 
         * Ports: Talons, Sparks and Pistons
         */

            /** Ion Cannon */
            public static final int ION_CANNON_TOP = 20;          // Talon
            public static final int ION_CANNON_BOTTOM = 21;       // Talon
            /** Lightsaber */
            public static final int LIGHTSABER_TOP = 27;          // Spark
            public static final int LIGHTSABER_BOTTOM = 28;       // Spark
            /** Pizza */
            public static final int PIZZA_SLICER = 29;            // UNKNOWN
            public static final int SCOMP_LINK_HIGHSIDE_PORT = 4; // Piston
            public static final int SCOMP_LINK_LOWSIDE_PORT = 5;  // ''
            /** Tokamak */
            public static final int TOKAMAK_TOP = 25;             // Talon
            public static final int TOKAMAK_BOTTOM = 26;          // Talon
            /** Tractor Beam */
            public static final int TRACTOR_BEAM = 22;            // Talon
            public static final int EF_ENGAGER_HIGHSIDE_PORT = 2; // Piston
            public static final int EF_ENGAGER_LOWSIDE_PORT = 3;  // ''


        /** Ion Cannon */
        public static final double ION_CORRECTIONAL_GAIN = (1/5676);

        /** Lightsaber */
        public static final double LS_CORRECTIONAL_GAIN = (1/40);
        public static final double LS_TARGET_SPEED = 0.33;

        /** Pizza */

        /** Tokamak */
        public static final double TOKAMAK_SPEED = 1;

        /** Tractor Beam */
        public static final double TRACTOR_BEAM_SPEED = 1;



    /**
     * Auton Stuff
     */

        /** Important Numbers */

        
	
        //norm/starting conditions arm down, lift down, facing alliance station
        public static final double HORIZONTAL_MULTIPLIER = 1.4;
        public static final double AUTON_DEFAULT_SPEED = -0.5;
        public static final double AUTON_FAST_SPEED = -0.8;
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

        /** Auton Sequences */

        /*
        public static final double[][] CONTINGENCY_RIGHT_SWITCH_FROM_LEFT_SEQUENCES = {{36.87, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}};
        public static final double[][] CONTINGENCY_LEFT_SWITCH_FROM_RIGHT_SEQUENCES = {{-36.87, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}};
        public static final double[][] CONTINGENCY_LEFT_SWITCH_FROM_MID_SEQUENCES = {{-14.04, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}};
        public static final double[][] CONTINGENCY_RIGHT_SWITCH_FROM_MID_SEQUENCES = {{14.04, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}};
        */
        /*
        public static final double[][] LEFT_SWITCH_FROM_LEFT_AND_RETURN_SEQUENCES = {{0, (AUTON_DEFAULT_SPEED), 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {-32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move straight to switch and eject cube
        public static final double[][] LEFT_SWITCH_FROM_MID_AND_RETURN_SEQUENCES = {{-14.04, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {-32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move to switch at an angle and eject cube
        public static final double[][] RIGHT_SWITCH_FROM_MID_AND_SEQUENCES = {{14.04, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move to switch at an angle and eject cube
        public static final double[][] RIGHT_SWITCH_FROM_LEFT_AND_SEQUENCES = {{36.87, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move to switch at an angle and eject cube
        public static final double[][] LEFT_SWITCH_FROM_RIGHT_AND_SEQUENCES = {{-36.87, AUTON_DEFAULT_SPEED, 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {-32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move to switch at an angle and eject cube
        public static final double[][] RIGHT_SWITCH_FROM_RIGHT_AND_SEQUENCES = {{0, (AUTON_DEFAULT_SPEED), 0.0, 11, AUTON_RUNTIME}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 1000}, {32.735, AUTON_DEFAULT_SPEED, 0.0, 11, 2000}}; //Move straight to switch and eject cube
        */
        public static final double[] PUSH_FORWARD = {0, -.35, 0.0, 11, 750};
        public static final double[][] TEST_SEQUENCES = {{0.0, (AUTON_FAST_SPEED), 0.0, 13, 1000}};
        public static final double[][] FORWARD_SHOOT_SEQUENCES = {{0.0, (AUTON_FAST_SPEED), 0.0, 13, 2800}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}};
        public static final double[][] FORWARD_SHOOT_GRAB_CUBE_SEQUENCES = {{0, (AUTON_FAST_SPEED), 0.0, 13, AUTON_FAST_RUNTIME}, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}, {0.0, -1.0*AUTON_FAST_SPEED, 0.0, 11, AUTON_FAST_RUNTIME*AUTON_RETURN_MULTIPLIER}, {90.0*DIRECTIONAL_MULTIPLIER, AUTON_DEFAULT_SPEED, 0.0, 11, SIDE_TO_MID_RUNTIME}, {0.0, 0.0, 0.5, 11, 1000}, {0.0, 0.0, 0.0, 1, 50}, {0.0, 0.0, 0.0, 6, 500}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 11, MID_TO_CUBE_RUNTIME}, {0.0, 0.0, 0.0, 5, 1000}, {0.0, 0.0, 0.0, 9, 50}, {90.0*DIRECTIONAL_MULTIPLIER, -1.0*AUTON_DEFAULT_SPEED, 0.0, 11, SIDE_TO_MID_RUNTIME}};//, {-32.735, AUTON_FAST_SPEED, 0.0, 11, AUTON_FAST_RUNTIME*AUTON_RETURN_MULTIPLIER}}; //Move straight to switch and eject cube
        public static final double[][] LEFT_SWITCH_FROM_MID_SEQUENCES = {{(-1*(MID_ANGLE + 4.0)), AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}}; //Move to switch at an angle and eject cube
        public static final double[][] RIGHT_SWITCH_FROM_MID_SEQUENCES = {{MID_ANGLE, AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, PUSH_FORWARD, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}}; //Move to switch at an angle and eject cube
        //public static final double[][] RIGHT_SWITCH_FROM_LEFT_SEQUENCES = {{EDGE_ANGLE, AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}}; //Move to switch at an angle and eject cube
        //public static final double[][] LEFT_SWITCH_FROM_RIGHT_SEQUENCES = {{(-1*(EDGE_ANGLE)), AUTON_FAST_SPEED, 0.0, 13, AUTON_FAST_RUNTIME}, {0.0, 0.0, 0.0, 2, ARM_OPENING_TIME}, {0.0, 0.0, 0.0, 14, FIRE_TIME}};//Move to switch at an angle and eject cube

        //public static final double[][] LEFT_SIDE_DELIVERY_SEQUENCES = {{0.0, 0.0, ((-0.4)), 0, 1000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 11, 1000}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 100}, {0.0, 0.0, 0.0, 5, 100}}; //After forwards sequence turn using DIRECTIONAL_MULTIPLIER multiplier and then move forwards and eject cube into switch
        //public static final double[][] RIGHT_SIDE_DELIVERY_SEQUENCES = {{0.0, 0.0, ((0.4)), 0, 1000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 11, 1000}, {0.0, 0.0, 0.0, 2, 100}, {0.0, 0.0, 0.0, 14, 100}, {0.0, 0.0, 0.0, 5, 100}}; //After forwards sequence turn using DIRECTIONAL_MULTIPLIER multiplier and then move forwards and eject cube into switch
        
        public static final double[][] FORWARD_SEQUENCE = {{0, (AUTON_DEFAULT_SPEED), 0.0, 11, 2800}};

        //public static final double[][] RIGHT_SCALE_UNIVERSAL_AUTON_SEQUENCES = {{Autonomous.find_angle_theta(Robot.right_x, Robot.right_y), -0.5, 0.0, 13, 5000}, {0.0, 0.0, 0.0, 10, 1000}, {0.0, 0.0, 0.0, 1, 50}, {0.0, 0.0, 0.0, 2, 50}, {-90.0, 0.75, 0.0, 13, 2000}, {0.0, -0.75, 0.0, 11, 2500}, {90.0, -0.75, 0.0, 11, 2000}, {0.0, 0.5, 0.0, 11, 1000}, {0.0, 0.0, 0.0, 6, 50}, {0.0, 0.0, 0.0, 5, 500}, {0.0, 0.0, 0.0, 3, 50}, {0.0, 0.0, 0.0, 4, 100}, {0.0, 0.0, 0.0, 2, 50}, {0.0, 0.0, 0.0, 5, 1}, {0.0, -0.75, 0.0, 13, 2000}, {0.0, 0.0, 0.0, 7, 100}, {0.0, 0.0, 0.0, 10, 200}}; // Add an angle into index 0
        //public static final double[][] LEFT_SCALE_UNIVERSAL_AUTON_SEQUENCES = {{Autonomous.find_angle_theta(Robot.left_x, Robot.left_y), -0.5, 0.0, 13, 5000}, {0.0, 0.0, 0.0, 10, 1000}, {0.0, 0.0, 0.0, 1, 50}, {0.0, 0.0, 0.0, 2, 50}, {90.0, 0.75, 0.0, 13, 2000}, {0.0, -0.75, 0.0, 11, 2500}, {-90.0, -0.75, 0.0, 11, 2000}, {0.0, 0.5, 0.0, 11, 1000}, {0.0, 0.0, 0.0, 6, 50}, {0.0, 0.0, 0.0, 5, 500}, {0.0, 0.0, 0.0, 3, 50}, {0.0, 0.0, 0.0, 4, 100}, {0.0, 0.0, 0.0, 2, 50}, {0.0, 0.0, 0.0, 5, 1}, {0.0, -0.75, 0.0, 13, 2000}, {0.0, 0.0, 0.0, 7, 100}, {0.0, 0.0, 0.0, 10, 200}}; // Add an angle into index 0
        public static final double[][] PICKUP_FROM_SPOT = {{0.0, 0.0, 0.5, 0, 1000}, {(-1*AUTON_DEFAULT_SPEED), 0.0, 0.0, 13, 2000}, {0.0, 0.0, 0.0, 2, 50}, {0.0, 0.0, 0.0, 6, 50}, {(-1*AUTON_DEFAULT_SPEED), 0.0, 0.0, 13, 1000}, {0.0, 0.0, 0.0, 5, 50}, {0.0, 0.0, 0.0, 9, 50}, {AUTON_DEFAULT_SPEED, 0.0, 0.0, 11, 2000}}; //Pickup a cube, return to common starting spot, and then move cube onto lift
        public static final double[][] RIGHT_SCALE_FROM_SPOT = {{90.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 13, 3000}, {0.0, 0.0, 0.0, 7, 500}, {0.0, 0.0, 0.0, 14, 100}};
        public static final double[][] LEFT_SCALE_FROM_SPOT = {{-90.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}, {0.0, AUTON_DEFAULT_SPEED, 0.0, 13, 3000}, {0.0, 0.0, 0.0, 7, 500}, {0.0, 0.0, 0.0, 14, 100}};
        public static final double[][] AUTON_EXCHANGE_FROM_SPOT = {{32.0, (-1*(AUTON_DEFAULT_SPEED)), 0.0, 13, 3000}};
        // {angle,strength/speed,turning,mode,time}

        public static final double[] FORWARD_CLOCKWISE = {0.3, 0.0, -0.5};
        public static final double[] FORWARD_COUNTERCLOCK = {0.3, 0.0, 0.5};
        public static final double[] FORWARD_RIGHT = {0.3, 0.5, 0.0};
        public static final double[] FORWARD_LEFT = {0.3, -0.5, 0.0};
        public static final double[] FORWARD = {0.5, 0.0, 0.0};
        // {forward, right, counterclock}


        /** Get Sequence Methods */

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



    /** Logger */    
    public static enum LOGGED_CLASSES { SEMAPHORE, DRIVE, GROUNDTRUTH, PNEUMATICS, SHOOTER }

    public static final String TEAM_BANNER = "ICAgICAgICAgICBfX18gICAgICAgICAgICAgIF9fICBfXw0KICAgICAgICAgICAgfCBfIF8gIF8gICAgL3wgfF8gIC8gIFwgfF9ffA0KICAgICAgICAgICAgfCgtKF98fHx8ICAgIHwgX18pIFxfXy8gICAgfA0KDQogICAgICAgICAgICAgICAgICAgICAgICAgXy4NCiAgICAgICAgICAgICAgICAgICAgICAgLicgb28NCiAgICAgICAgICAgICAgICAgICAgICAgfCAgICA+DQogICAgICAgICAgICAgICAgICAgICAgLyAvIDogYC4NCiAgICAgICAgICAgICAgICAgICAgIHxfLyAvICAgfA0KICAgICAgICAgICAgICAgICAgICAgICB8LyAgd3cNCl9fXyAgICAgICAgX18gICAgICAgICAgICAgICAgICAgICAgX18NCiB8IHxfICBfICB8ICBcIF8gXyBfICBfIF8gXyB8XyBfICB8X18pXyBfICBfICAgIC4gXyAgXw0KIHwgfCApKC0gIHxfXy8oLV8pfF8pKC18IChffHxfKC0gIHwgICgtfCApKF8pfF98fHwgKV8pDQogICAgICAgICAgICAgICAgICB8ICAgICAgICAgICAgICAgICAgICAgICBfLw==";
	public static final String ROBOT_BANNER = "ICAgICAvXCAgICAgL1wgICAgICAgICAgICAgICoNCiAgICAnLiBcICAgLyAsJyAgICAgICAgICAgICAgXCAgIH4NCiAgICAgIGAuXC0vLCcgICAgICAgICAgICAgXywsIFwge10NCiAgICAgICAoIFggICApICAgICAgICAgICAiLT1cO19cICUNCiAgICAgICwnLyBcYC5cICAgICAgICAgICAgXyBcXDsoIyklDQogICAgLicgLyAgIFwgYCwgICAgICAgICAgIF9cfCBcXyUlDQogICAgIFwvLS0tLS1cLycgICAgICAgICAgIFwgIFwvXCAgXA0KX19fX19fIHxfSF9fX3xfX19fX19fX19fX19fX18gKCApfn5+X19fXw0KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfCBcDQogICAgICAgICAgICAgICAgICAgICAgICAgICAgIC8gIC8=";
}