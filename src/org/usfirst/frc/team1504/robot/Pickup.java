package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _grab_left;
	private WPI_TalonSRX _grab_right;
	private WPI_TalonSRX arm_left;
	private WPI_TalonSRX arm_right;
	DoubleSolenoid _grab_piston; 
	private Lift _lift = Lift.getInstance();
	private enum state {OFF, ON};
	private state _mode = state.OFF; 

	
	
	public enum arm {UP, DOWN};
	public arm arm_state = arm.DOWN;
	
	private static final Pickup instance = new Pickup();
	
	public static Pickup getInstance()
	{
		return instance;
	}
	
	private Pickup()
	{	
		_grab_left = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_LEFT);
		_grab_right = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_RIGHT);
		arm_left = new WPI_TalonSRX(Map.DROP_PICKUP_LEFT);
		arm_right = new WPI_TalonSRX(Map.DROP_PICKUP_RIGHT);
		arm_right.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 200); //200 here is the ms timeout when trying to connect
		arm_right.config_kP(0, 0.03, 200); //200 is the timeout ms
		arm_right.config_kI(0, 0.00015, 200);
		arm_left.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 200); //200 here is the ms timeout when trying to connect
		arm_left.config_kP(0, 0.03, 200); //200 is the timeout ms
		arm_left.config_kI(0, 0.00015, 200);
		//arm_left.set(ControlMode.Velocity, 0);
		_grab_piston = new DoubleSolenoid(0, 1); //0 is on/forward, 1 for off/reverse
		_grab_piston.set(DoubleSolenoid.Value.kOff); //not sure about this
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Pickup Initialized.\nPickup Disabled");
	}
	
	public static void initialize()
	{
		getInstance();
	}
	
	public void set_flipper_speed(double speed) 
	{
		_grab_left.set(speed);
		_grab_right.set(-speed);
	}
	
	public void set_arm_speed(double speed) 
	{
		arm_left.set(ControlMode.Velocity, speed);
		arm_right.set(ControlMode.Velocity, speed);
	}
	
	public void pick_up()
	{
		if(arm_left.getSelectedSensorPosition(0) > 1000 && _lift.pickup_safe()){ //1000 is a constant going up is higher
			set_arm_speed(0.3);
		} else{
			set_arm_speed(0);
			System.out.println("Pickup started intaking.");
		}
	}
	
	public void put_down()
	{
		if(arm_left.getSelectedSensorPosition(0) < 1000){ //1000 is a constant
			set_arm_speed(-0.3);
		} else{
			set_arm_speed(0);
			System.out.println("Pickup stopped intaking.");
		}
	}
	
	public void open_arm()
	{
		_grab_piston.set(DoubleSolenoid.Value.kForward);
	}
	
	public void close_arm()
	{
		_grab_piston.set(DoubleSolenoid.Value.kReverse);
	}
	
	public boolean lift_safe() //says whether or not the pickup arms are backed where the lift can be
	{
		double pos = 0.0; //pseudocode
		return (pos > 0);
	}
	private void update_mode()
	{
		
		if (IO.get_pickup_on())
		{
			_mode = state.ON;
			_grab_piston.set(DoubleSolenoid.Value.kForward);
			//drop both cantalons based on sensor. Fake code for now
			pick_up();
			System.out.println("Pickup is intaking some cubes.");

		}
		else if (IO.get_pickup_off())
		{
			_grab_piston.set(DoubleSolenoid.Value.kReverse);
			_mode = state.OFF;
			
			//pick up both cantalons based on sensor. Fake code for now
			put_down();
			System.out.println("Pickup is ejecting some cubes.");
			}

		}
	
	public void set_state(arm state) 
	{
		if (state == arm.UP)
		{
			put_down();
		}
		else if (state == arm.DOWN)
		{
			pick_up();
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			set_flipper_speed(IO.intake_input()*Map.PICKUP_LEFT_MAGIC);
		}
		if (_mode == state.OFF)
		{
			set_flipper_speed(0);
		}
	}
	private void override_pickup()
	{
		if (IO.get_override_pickup())
		{
			arm_left.set(ControlMode.Velocity, IO.intake_input()*Map.PICKUP_LEFT_MAGIC);
			arm_right.set(ControlMode.Velocity, IO.intake_input()*Map.PICKUP_RIGHT_MAGIC);
		}
	}
	public void semaphore_update()
	{
		update_mode();
		set_motor();
		override_pickup();
	}
}
