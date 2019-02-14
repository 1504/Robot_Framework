package org.usfirst.frc1504.Robot2019;
import org.usfirst.frc1504.Robot2019.Auto_Alignment.alignment_position;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	private WPI_TalonSRX _left_roller;
	private WPI_TalonSRX _right_roller;
	public static DoubleSolenoid _grab_piston; 
	public DoubleSolenoid _grabber;
	private static final Pickup instance = new Pickup();
	private DriverStation _ds = DriverStation.getInstance();
	public static Pickup getInstance() // sets instance
	{
		return instance;
	}
	private Pickup() // pickup constructor
	{
		_left_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
		_right_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
		_grab_piston = new DoubleSolenoid(0, 1); //0 and 1 are the ports, needs to be moved to the map
		_grab_piston.set(DoubleSolenoid.Value.kOff);
		Update_Semaphore.getInstance().register(this);
	}
	public static void initialize() //initialize
	{
		getInstance();
	}
	public void set_intake_speed(double speed) //sets both the right and left flipper speeds
	{
		_left_roller.set(speed*Map.ROLLER_SPEED_MULTIPLIER);
		_right_roller.set(-speed*Map.ROLLER_SPEED_MULTIPLIER);
	}
	public static void update_grabber_state() {
		if(_grab_piston.get() == DoubleSolenoid.Value.kOff || _grab_piston.get() == DoubleSolenoid.Value.kReverse) 
		{
			open_grabber();
		}
		if(_grab_piston.get() == DoubleSolenoid.Value.kForward)
		{
			close_grabber();
		}
	}
	public static void open_grabber() 
	{
		_grab_piston.set(DoubleSolenoid.Value.kForward);
	}
	public static void close_grabber()
	{
		_grab_piston.set(DoubleSolenoid.Value.kReverse);
	}
	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{	
			if(IO.get_grabber())
			{
				update_grabber_state();
				Auto_Alignment.alignment_state = alignment_position.PLACEMENT_TRACKING;
			}
			set_intake_speed(IO.get_intake_speed());
		}
	}
}
