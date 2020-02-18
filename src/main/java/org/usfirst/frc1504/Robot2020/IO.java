package org.usfirst.frc1504.Robot2020;

/**
 * 
 * WARNING - THIS FILE HAS BEEN HARD PURGED! REFERENCE master_2019 FOR PREVIOUS IMPLEMENTATIONS
 * 
 */

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	private static Latch_Joystick _secondary = new Latch_Joystick(Map.DRIVE_SECONDARY_JOYSTICK);

	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	

	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static boolean camera_port()
	{
		return _secondary.getRawButton(Map.VISION_INTERFACE_CAMERA_PORT_BUTTON);
	}
	
	

	/** Hid Stuff
	 * 
	 */
	public static int hid()
	{
		return _secondary.getPOV();
	}
	public static boolean hid_N()
	{
		if(hid() == 0)
			return true;
		else 
			return false;
	}
	public static boolean hid_E()
	{
		if(hid() == 90)
			return true;
		else 
			return false;
	}
	public static boolean hid_S()
	{
		if(hid() == 180)
			return true;
		else 
			return false;
	}
	public static boolean hid_W()
	{
		if(hid() == 270)
			return true;
		else
			return false;
	}

	


	/**
	 * Drive stuff
	 */
	
	public static double[] drive_input() {
		double[] inputs = new double[3];

		inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
		inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS));//x
		inputs[2] = Map.DRIVE_INPUT_MAGIC_NUMBERS[2] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));//w
		return inputs;
	}
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}
	public static boolean reset_front_side()
	{
		return (_drive_forward.getRawButton(Map.DRIVE_FRONTSIDE_FRONT));
	}
	public static boolean get_drive_op_toggle()
	{
		return (_drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_rotation.getRawButton(Map.DRIVE_OP_BUTTONS[3]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[0]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[1]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[2]) || _drive_forward.getRawButton(Map.DRIVE_OP_BUTTONS[3]));
	}

	/**
	 * Yearly Components
	 */
		/** Ion Cannon */
		public static boolean bottom_ion_shoot()
		{
			return _secondary.getRawButton(Map.BOTTOM_SHOOT_BUTTON);
		}
		public static boolean top_ion_shoot()
		{
			return _secondary.getRawButton(Map.TOP_SHOOT_BUTTON);
		}
		public static boolean cycle_ion_setpoint() 
		{
			return _secondary.getRawButton(Map.ION_SETPOINT_BUTTON);
		}

		/** Lightsaber */
		public static boolean ls_extend_button()
		{
			return _secondary.getRawButton(Map.LIGHTSABER_EXTEND_BUTTON);
		}
		public static boolean ls_retract_button()
		{
			return _secondary.getRawButton(Map.LIGHTSABER_RETRACT_BUTTON);
		}
		public static double ls_manual_target_speed()
		{
			return _secondary.getRawAxis(Map.LS_SPEED_CTRL_JOYSTICK);
		}

		/** Pizza */
		public static boolean get_pizza_cutter_button()
		{
			return _secondary.getRawButton(Map.PIZZA_SLICER_BUTTON);
		}
		public static boolean get_rotation_control_button()
		{
			return _secondary.getRawButton(Map.ROTATION_CONTROL_BUTTON);
		}

		/** Tokamak */
		public static double get_tokamak_override()
		{
			return _secondary.getRawAxis(Map.TOKAMAK_OVERRIDE);
		}

		/** Tractor Beam */
		public static boolean get_tractor_beam_activation()
		{
			return _secondary.getRawButton(Map.ACTIVATE_TRACTOR_BEAM_BUTTON);
		}

		/** Vision */
		public static boolean get_vison_alignment_button()
		{
			return _secondary.getRawButton(Map.LIGHTSABER_EXTEND_BUTTON);
		}

		// Override button
		public static boolean get_god_button()
		{
			return _secondary.getRawButton(Map.GOD_MODE);
		}
		public static double get_testing1()
		{
			return Math.pow(_drive_rotation.getRawAxis(Map.JOYSTICK_Y_AXIS), 1);
		}
		public static double get_testing2()
		{
			return Math.pow(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS), 1);
		}


		
}
