package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS
 * IMPLEMENTATIONS
 * 
 */

public class IO {
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.DRIVE_SECONDARY_JOYSTICK);

	public static final long ROBOT_START_TIME = System.currentTimeMillis();

	/**
	 * Drive stuff
	 */

	/**
	 * Handle getting joystick values
	 * 
	 * @return
	 */
	public static boolean camera_port() {
		return _secondary.getRawButton(Map.VISION_INTERFACE_CAMERA_PORT_BUTTON);
	}

	/**
	 * Hid Stuff
	 * 
	 */
	public static int hid() {
		return _secondary.getPOV();
	}

	public static boolean hid_N() {
		if (hid() == 0)
			return true;
		else
			return false;
	}

	public static boolean hid_E() {
		if (hid() == 90)
			return true;
		else
			return false;
	}

	public static boolean hid_S() {
		if (hid() == 180)
			return true;
		else
			return false;
	}

	public static boolean hid_W() {
		if (hid() == 270)
			return true;
		else
			return false;
	}

	/**
	 * Drive stuff
	 */

	public static double[] drive_input() {
		double[] inputs = new double[3];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0]
				* Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2)
				* Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1]
				* Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS)), 2)
				* Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS));// x
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2]
				* Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2)
				* Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		return inputs;
	}

	public static double drive_wiggle() {
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}

	public static boolean correction()
	{
		return _drive_forward.getRawAxis(2) < -0.5;
	}

	public static boolean reset_front_side() {
		return (_drive_forward.getRawButton(Map.DRIVE_FRONTSIDE_FRONT));
	}

	public static boolean get_drive_op_toggle() {
		return (_drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[0])
				|| _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[1])
				|| _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[2])
				|| _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[3])
				|| _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[0])
				|| _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[1])
				|| _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[2])
				|| _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[3]));
	}

	/**
	 * Yearly Components
	 */
	/** Ion Cannon */
	public static boolean ion_low() {
		return _secondary.getRawButton(Map.ION_LOW_BT);
	}

	public static boolean ion_high() {
		return _secondary.getRawButton(Map.ION_HIGH_BT);
	}

	public static boolean ion_shoot() {
		return _secondary.getRawButton(Map.ION_SHOOT_BT);
	}

	// God Ion Cannon
	public static double god_ion() {
		return _secondary.getRawAxis(Map.GOD_ION_AX);
	}

	public static boolean god_ex() {
		return _secondary.getRawButtonReleased(Map.GOD_EX_BT);
	}

	/** Lightsaber */
	public static double lightsaber() {
		if (Math.pow(_secondary.getRawAxis(Map.LIGHTSABER_AX), 3) > 0.5) 
			return 0.5;
		else if (Math.pow(_secondary.getRawAxis(Map.LIGHTSABER_AX), 3) < -0.5) 
			return -0.5;
		else if(Math.abs(_secondary.getRawAxis(Map.LIGHTSABER_AX)) > 0.05) 
			return Math.pow(_secondary.getRawAxis(Map.LIGHTSABER_AX), 3);
		else
			return 0;
	}

	/** Pizza */
	public static boolean pizza_auto() {
		return _secondary.getRawButton(Map.PIZZA_AUTO_BT);
	}

	public static boolean pizza_extend() {
		return _secondary.getRawButtonReleased(Map.PIZZA_EXTEND_BT);
	}

	public static double pizza_spin() {
		return _secondary.getRawAxis(Map.PIZZA_SPIN_AX);
	}

	/** Tokamak */
	public static boolean snake_reverse()
	{
		return _secondary.getRawButton(Map.SNAKE_REVERSE);
	}
	// God Tokamak
	public static double snake() {
		return Math.pow(_secondary.getRawAxis(Map.GOD_SNAKE_AX), 3);
	}

	public static double serializer() {
		return Math.pow(_secondary.getRawAxis(Map.GOD_SERIALIZER_AX), 3);
	}

	/** Tractor Beam */
	public static boolean tb_activate() {
		return _secondary.getRawButton(Map.TB_ACTIVATE_BT);
	}

	// God Tractor Beam
	public static double god_tb() {
		return Math.pow(_secondary.getRawAxis(Map.GOD_TB_AX), 3);
	}

	public static boolean god_ef() {
		return _secondary.getRawButtonReleased(Map.GOD_EF_BT);
	}

	/** Misc */
	public static boolean god() {
		return _secondary.getRawButtonReleased(Map.GOD_ENABLE);
	}

	public static boolean god_state = false;

}
