package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _grab_left;
	private WPI_TalonSRX _grab_right;
	private WPI_TalonSRX _arm;
	DoubleSolenoid _grab_piston; 
	private Lift _lift = Lift.getInstance();
	public enum arm_position {UP, DOWN, MIDDLE}; // declares states of arms
	public double[] arm_angle = {Map.ARM_UP_ANGLE, Map.ARM_DOWN_ANGLE, Map.ARM_UP_ANGLE/2}; // Map.ARM_UP_ANGLE/2 or Map.ARM_MID_ANGLE
	public static arm_position arm_state = arm_position.DOWN; // sets arms to be down at beginning of match
	
	public enum flipper {OPEN, CLOSE}; // declares states of flippers
	public static flipper flipper_state = flipper.CLOSE; // sets flippers to be closed at beginning of match
	
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
		_grab_piston = new DoubleSolenoid(0, 1); //0 is on/forward, 1 for off/reverse
		_grab_piston.set(DoubleSolenoid.Value.kOff); //not sure about this
		Update_Semaphore.getInstance().register(this);
		System.out.println("Pickup Initialized.\nPickup Disabled");
	}
	
	public static void initialize() //initialize
	{
		getInstance();
	}
	
	public void set_arm_speed(double speed) //sets both the right and left arm speeds
	{
		_arm.set(ControlMode.Velocity, speed);
	}
	
	public void set_intake_speed(double speed) //sets both the right and left flipper speeds
	{
		_grab_left.set(speed);
		_grab_right.set(-speed);
	}
	
	public void flipper_intake() // sets rotors to spin cube in
	{
		set_intake_speed(Map.ROLLER_SPEED);
	}
	
	public void flipper_excrete() // sets rollers to spit cube out
	{
		set_intake_speed(-Map.ROLLER_SPEED);
	}
	
	public void flipper_stop() 
	{
		set_intake_speed(0);
	}
	
	
	
	public void open_flipper() //extends piston between arms to grab cube
	{
		_grab_piston.set(DoubleSolenoid.Value.kForward);
	}
	
	public void close_flipper() // closes space between arms with piston
	{
		_grab_piston.set(DoubleSolenoid.Value.kReverse);
	}
	
	public boolean lift_safe() //says whether or not the pickup arms are backed where the lift can be
	{
		double pos = 0.0; //pseudocode
		return (pos > 0);
	}
	private void update_mode() //checks if pickup is in progress
	{
		if (IO.get_override_pickup())
		{
			set_intake_speed(IO.intake_input()*Map.FLIPPER_MAGIC);
		} 
		if (_lift.pickup_safe())
		{
			set_arm_speed((arm_angle[arm_state.ordinal()]-_arm.getSelectedSensorPosition(0))/Map.ARM_DOWN_ANGLE);
			// Sets arm velocity based on how far away the target is and where it is.
			// Finds target angle by finding element of arm_state then finds its angle element in the arm_angle array
		}
		/*
		else if (arm_state == arm_position.UP)
		{
			if(_arm.getSelectedSensorPosition(0) < Map.ARM_UP_ANGLE && _lift.pickup_safe()){ 
				set_arm_speed(Map.ARM_SPEED);
			}
			else
			{
				set_arm_speed(0);
				System.out.println("Pickup started intaking.");
			}
		}
		else if (arm_state == arm_position.DOWN)
		{
			if(_arm.getSelectedSensorPosition(0) > Map.ARM_DOWN_ANGLE)
			{
				set_arm_speed(-Map.ARM_SPEED);
			}
			else
			{
				set_arm_speed(0);
				System.out.println("Pickup stopped intaking.");
			}
		}
		else if (arm_state == arm_position.MIDDLE);
		{
			int sign = (int) -Math.signum(_arm.getSelectedSensorPosition(0) - Map.ARM_MID_ANGLE);
			if(Math.abs(_arm.getSelectedSensorPosition(0) - Map.ARM_MID_ANGLE) < 10)
			{
				set_arm_speed(sign*Map.ARM_SPEED);
			}
			else
			{
				set_arm_speed(0);
				System.out.println("Pickup started intaking.");
			}
		}
		*/
		
		if (flipper_state == flipper.CLOSE)
		{
			close_flipper();
		}
		else if (flipper_state == flipper.OPEN);
		{
			open_flipper();
		}
		
		if (IO.get_pickup_on())
		{
			set_state(flipper.OPEN);
			set_state(arm_position.DOWN);
			flipper_intake();
		}
		else if (IO.get_pickup_off())
		{
			set_state(flipper.CLOSE);
			set_state(arm_position.UP);
		}
	}
	
	public void set_state(arm_position state) //sets position of arm
	{
		arm_state = state;
	}
	public void set_state(flipper state) //sets position of arm
	{
		flipper_state = state;
	}

	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled())
			set_state(flipper.values()[0]); // 0 --> IO.get_controller_trigger thing
		update_mode();
	}
}
