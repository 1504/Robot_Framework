package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc1504.Robot2019.Auto_Alignment.alignment_position;

public class Arms implements Updatable
{
    private static final Arms instance = new Arms();
    private DriverStation _ds = DriverStation.getInstance();

    // Arm rollers and extension
	private WPI_TalonSRX _left_roller;
	private WPI_TalonSRX _right_roller;
    public static DoubleSolenoid _arm_extension;
    
    // Grabber
	public static DoubleSolenoid _grab_piston;

	public boolean lastGrabberButtonState = false;
    public boolean lastArmButtonState = false;
    
    public static Arms getInstance() // sets instance
	{
		return instance;
	}

    private Arms() // Arms constructor
    {
        _arm_extension = new DoubleSolenoid(Map.ARM_EXTENSION_HIGHSIDE_PORT, Map.ARM_EXTENSION_LOWSIDE_PORT);
		_arm_extension.set(DoubleSolenoid.Value.kOff);

		_left_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
        _right_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
        
        _grab_piston = new DoubleSolenoid(Map.GRAB_PISTON_HIGHSIDE_PORT, Map.GRAB_PISTON_LOWSIDE_PORT);
		_grab_piston.set(DoubleSolenoid.Value.kOff);
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    public void set_intake_speed(double speed) // sets both the right and left flipper speeds
	{
		_left_roller.set(speed * Map.ROLLER_SPEED_MULTIPLIER);
		_right_roller.set(-speed * Map.ROLLER_SPEED_MULTIPLIER);
    }
    
    public static void update_grabber_state() {
		if (_grab_piston.get() == DoubleSolenoid.Value.kOff || _grab_piston.get() == DoubleSolenoid.Value.kReverse) {
			open_grabber();
		} else {
			close_grabber();
		}
    }
    
    public static void open_grabber() {
		_grab_piston.set(DoubleSolenoid.Value.kForward);
	}

	public static void close_grabber() {
		_grab_piston.set(DoubleSolenoid.Value.kReverse);
    }
    
    public static void update_arm_state() {
		if (_arm_extension.get() == DoubleSolenoid.Value.kOff
				|| _arm_extension.get() == DoubleSolenoid.Value.kReverse) {
			_arm_extension.set(DoubleSolenoid.Value.kForward);
		} else {
			_arm_extension.set(DoubleSolenoid.Value.kReverse);
		}
    }

    public void semaphore_update() // updates robot information
	{
		if (_ds.isOperatorControl() && !_ds.isDisabled()) // only runs in teleop
		{
			if (IO.get_grabber() && IO.get_grabber() != lastGrabberButtonState) {
				update_grabber_state();
				Auto_Alignment.alignment_state = alignment_position.PLACEMENT_TRACKING;
			}
			lastGrabberButtonState = IO.get_grabber();

			/*
			 * if(IO.extend_arm() && !Auto_Alignment.get_grabber_trigger() &&
			 * IO.extend_arm() != lastArmButtonState) { update_arm_state(); } else if
			 * (Auto_Alignment.get_grabber_trigger()) {
			 * _arm_extension.set(DoubleSolenoid.Value.kReverse); }
			 * if(Auto_Alignment.get_lift_trigger()) { update_end_lift_state(); }
			 * lastArmButtonState = IO.get_grabber();
			 */
			set_intake_speed(IO.get_intake_speed());

		}
	}
    




}