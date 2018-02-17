package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
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
	DoubleSolenoid _grab_piston; 
	private Lift _lift = Lift.getInstance();
	Encoder encoder;
	public enum arm_position {UP, DOWN, MIDDLE, OFF}; // declares states of arms
	public double[] arm_angle = {Map.ARM_UP_ANGLE, Map.ARM_DOWN_ANGLE, Map.ARM_UP_ANGLE/2}; // Map.ARM_UP_ANGLE/2 or Map.ARM_MID_ANGLE
	public static arm_position arm_state = arm_position.DOWN; // sets arms to be down at beginning of match
	
	public enum flipper {OPEN, CLOSE}; // declares states of flippers
	public static flipper flipper_state = flipper.CLOSE; // sets flippers to be closed at beginning of match
	
	
	public double[] intake_speeds = {Map.ROLLER_SPEED, -Map.ROLLER_SPEED, 0};
	private static final Pickup instance = new Pickup();
	private DriverStation _ds = DriverStation.getInstance();
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
		_arm.config_kP(0, 0.03, 200); //200 is the timeout ms
		_arm.config_kI(0, 0.00015, 200);
		_grab_piston = new DoubleSolenoid(0, 1); //0 and 1 are the ports, needs to be moved to the map
		_grab_piston.set(DoubleSolenoid.Value.kOff); //not sure about this
		Update_Semaphore.getInstance().register(this);
		
		encoder = new Encoder(Map.ENCODER_PORT_1, Map.ENCODER_PORT_2, true, EncodingType.k4X); //EncodingType sets the decoding scale factor to 4x; if the arm moves more quickly, this could be lowered to 2x
		encoder.reset();
		System.out.println("Pickup Initialized.\nPickup Disabled");
	}
	
	public static void initialize() //initialize
	{
		getInstance();
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
	public boolean lift_safe() //says whether or not the pickup arms are backed where the lift can be
	{
		return false;//_lift.get_lift_height() > Map.LIFT_SAFETY_THRESHOLD;
	}
	private void update_mode() //checks if pickup is in progress
	{
		if (!lift_safe())
		{
			//set_arm_speed();
			//(arm_angle[arm_state.ordinal()] - encoder.get()) * Map.PICKUP_GAIN
			System.out.println(encoder.get());
			// Sets arm velocity based on how far away the target is and where it is.
			// Finds target angle by finding element of arm_state then finds its angle element in the arm_angle array
		}
		_grab_piston.set(DoubleSolenoid.Value.values()[flipper_state.ordinal()+1]);
		//this bit of code should set the piston based on the state

	}
	
	public void set_state(arm_position state) //sets position of arm
	{
		if(!encoder.getStopped()) //making sure the encoder is connected
		{	
			arm_state = state;
		}
	}
	public void set_state(flipper state) //sets position of arm
	{
		flipper_state = state;
	}
	

	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{
			
			set_state(flipper.values()[IO.open_flippers()]); 
			set_intake_speed(IO.get_intake_speed());
			//System.out.println(IO.get_intake_speed());
			if (IO.get_arm_up())
			{
				set_state(arm_position.UP);
			}
			else if (IO.get_arm_down())
			{
				set_state(arm_position.DOWN);
			}
			if (IO.get_override_pickup())
			{
				set_arm_speed(IO.override_input());
				
			}
			
		}
		update_mode();
	}
}
