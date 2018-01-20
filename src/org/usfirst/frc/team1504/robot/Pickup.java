package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _motor_left;
	private WPI_TalonSRX _motor_right;
	private WPI_TalonSRX _motor_arm_left;
	private WPI_TalonSRX _motor_arm_right;
	DoubleSolenoid _grab_piston; 
	private enum state {OFF, ON};
	private state _mode = state.OFF; 
	
	int on_count = 0;
	int off_count = 0;
	
	private static final Pickup instance = new Pickup();
	
	public static Pickup getInstance()
	{
		return instance;
	}
	
	public static void initialize()
	{
		getInstance();
	}
	private Pickup()
	{	
		_motor_left = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_LEFT);
		_motor_right = new WPI_TalonSRX(Map.PICKUP_TALON_PORT_RIGHT);
		_motor_arm_left = new WPI_TalonSRX(Map.DROP_PICKUP_LEFT);
		_motor_arm_right = new WPI_TalonSRX(Map.DROP_PICKUP_RIGHT);
		_grab_piston = new DoubleSolenoid(0, 1); //0 is on/forward, 1 for off/reverse
		_grab_piston.set(DoubleSolenoid.Value.kOff); //not sure about this
		Update_Semaphore.getInstance().register(this);
		
		System.out.println("Pickup Initialized.");
		System.out.println("Pickup Disabled");
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
			off_count = 0;
			_mode = state.ON;
			_grab_piston.set(DoubleSolenoid.Value.kForward);
			if (on_count == 0)
			{
				//drop both cantalons based on sensor. Fake code for now
				_motor_arm_left.set(0);
				_motor_arm_right.set(0);
				System.out.println("Pickup is intaking some cubes.");
				on_count++;
			}
		}
		else if (IO.get_pickup_off())
		{
			_grab_piston.set(DoubleSolenoid.Value.kReverse);
			on_count = 0;
			_mode = state.OFF;
			if (off_count == 0)
			{
				//pick up both cantalons based on sensor. Fake code for now
				_motor_arm_left.set(0);
				_motor_arm_right.set(0);
				System.out.println("Pickup stopped intaking.");
				off_count++;
			}
		}
	}
	
	private void set_motor()
	{
		if (_mode == state.ON)
		{
			_motor_left.set(IO.intake_input()*Map.PICKUP_LEFT_MAGIC);
			_motor_right.set(IO.intake_input()*Map.PICKUP_RIGHT_MAGIC);
		}
		if (_mode == state.OFF)
		{
			_motor_left.set(0);
			_motor_right.set(0);
		}
	}
	private void override_pickup()
	{
		if (IO.get_override_pickup())
		{
			_motor_arm_left.set(IO.intake_input()*Map.PICKUP_LEFT_MAGIC);
			_motor_arm_right.set(IO.intake_input()*Map.PICKUP_RIGHT_MAGIC);
		}
	}
	public void semaphore_update()
	{
		update_mode();
		set_motor();
		override_pickup();
	}
}
