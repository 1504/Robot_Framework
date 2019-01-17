package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _grab_left;
	private WPI_TalonSRX _grab_right;
	private WPI_TalonSRX _arm;
	public DoubleSolenoid _grab_piston; 
	public DoubleSolenoid _grabber;
	private Lift _lift = Lift.getInstance();
	// Encoder encoder;
	public enum arm_position {UP, DOWN, MIDDLE, OFF}; // declares states of arms
	public double[] arm_angle = {Map.ARM_UP_ANGLE, Map.ARM_DOWN_ANGLE, Map.ARM_UP_ANGLE/2}; // Map.ARM_UP_ANGLE/2 or Map.ARM_MID_ANGLE
	public static arm_position arm_state = arm_position.DOWN; // sets arms to be down at beginning of match
	
	public enum flipper {CLOSE, OPEN}; // declares states of flippers
	public static flipper flipper_state = flipper.OPEN; // sets flippers to be closed at beginning of match
	private static final Pickup instance = new Pickup();
	private DriverStation _ds = DriverStation.getInstance();
	private static DigitalInput bottom_arm = new DigitalInput(6);
	private boolean hold_down = false;
	public static Pickup getInstance() // sets instance
	{
		return instance;
	}
	
	private Pickup() // pickup constructor
	{
		_grab_left = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
		_grab_right = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
		_arm = new WPI_TalonSRX(Map.ARM_TALON_PORT);
		_arm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 200); //200 here is the ms timeout when trying to connect
		_grab_piston = new DoubleSolenoid(0, 1); //0 and 1 are the ports, needs to be moved to the map
		_grab_piston.set(DoubleSolenoid.Value.kOff); //not sure about this
		Update_Semaphore.getInstance().register(this);
		System.out.println("Pickup Initialized.\nPickup Disabled");
	}
	
	public static void initialize() //initialize
	{
		getInstance();
	}
	
	public double getPower()
	{
		return _arm.getOutputCurrent();
	}
	public void set_arm_speed(double speed) //sets both the right and left arm speeds
	{
		_arm.set(speed);
	}
	public void set_intake_speed(double speed) //sets both the right and left flipper speeds
	{
		_grab_left.set(speed);
		_grab_right.set(-speed);
	}
	public void rotate_intake()
	{
		if(IO.get_secondary_pov() == 270)
		{
			_grab_left.set(IO.get_intake_speed());
			_grab_right.set(IO.get_intake_speed());
		}
		if(IO.get_secondary_pov() == 90)
		{
			_grab_left.set(-IO.get_intake_speed());
			_grab_right.set(-IO.get_intake_speed());
		}
	}
	public boolean lift_safe() //says whether or not the pickup arms are backed where the lift can be
	{
		return false;//_lift.get_lift_height() > Map.LIFT_SAFETY_THRESHOLD;
	}
	private void update_mode() //checks if pickup is in progress
	{
		rotate_intake();
	}
	
	public void set_state(arm_position state) //sets position of arm
	{
		/*
		if(!encoder.getStopped()) //making sure the encoder is connected
		{	
			arm_state = state;
		}
		*/
		arm_state = state;
	}
	public void set_state(flipper state) //sets position of arm
	{
		flipper_state = state;
	}
	public void change_grabber_state() {
		if(_grab_piston.get() == DoubleSolenoid.Value.kOff)
			_grab_piston.set(DoubleSolenoid.Value.kForward);
		
		if(_grab_piston.get() == DoubleSolenoid.Value.kForward);
			_grab_piston.set(DoubleSolenoid.Value.kReverse);
			
		if(_grab_piston.get() == DoubleSolenoid.Value.kReverse);
			_grab_piston.set(DoubleSolenoid.Value.kForward);
	}
	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{
			
			if(IO.get_grabber()) 
			{
				change_grabber_state();
			}
					
			
			set_intake_speed(IO.get_intake_speed());
			//System.out.println(_arm.getSelectedSensorPosition(0));
			if (IO.get_arm_up())
			{
				set_state(arm_position.UP);
			}
			else if (IO.get_arm_down())
			{
				set_state(arm_position.DOWN);
			}
			//if (IO.get_override_pickup())
			if(!IO.get_override_lift())
			{
				 if(IO.override_input() < -0.1)
				{
					hold_down = false;
					set_arm_speed(IO.override_input());
				}
				 else if(!bottom_arm.get() || hold_down)
				{
					hold_down = true;
					set_arm_speed(0.07);
				}
				else 
				{
					set_arm_speed(IO.override_input());
				}
			}
		}
		update_mode();
	}
	
	public int arm_get_position()
	{
		return _arm.getSelectedSensorPosition(0);
	}
}
