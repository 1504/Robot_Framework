package org.usfirst.frc1504.Robot2019;
import org.usfirst.frc1504.Robot2019.Auto_Alignment.alignment_position;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.AnalogInput;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
public class Pickup implements Updatable {
	//Arm rollers and extension
	private WPI_TalonSRX _left_roller;
	private WPI_TalonSRX _right_roller;
	public static DoubleSolenoid _arm_extension;

	//Elevator
	private WPI_TalonSRX _first_actuator;
	private WPI_TalonSRX _second_actuator;

	//Grabber
	public static DoubleSolenoid _grab_piston; 

	//We're in the endgame now
	public static DoubleSolenoid _end_lift;

	public boolean lastGrabberButtonState = false;
	public boolean lastArmButtonState = false;
	public boolean lastLiftButtonState = false;

	private static final Pickup instance = new Pickup();
	private DriverStation _ds = DriverStation.getInstance();

	static Potentiometer firstPotentiometer;
	static Potentiometer secondPotentiometer;
	
	public static Pickup getInstance() // sets instance
	{
		return instance;
	}
	private Pickup() // pickup constructor
	{
		AnalogInput a = new AnalogInput(Map.FIRST_POTENTIOMETER_PORT);
		firstPotentiometer = new AnalogPotentiometer(a, 100, 0);

		AnalogInput b = new AnalogInput(Map.SECOND_POTENTIOMETER_PORT);
		secondPotentiometer = new AnalogPotentiometer(b, 100, 0);

		_arm_extension = new DoubleSolenoid(Map.ARM_EXTENSION_HIGHSIDE_PORT, Map.ARM_EXTENSION_LOWSIDE_PORT);
		_arm_extension.set(DoubleSolenoid.Value.kOff);

		_left_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
		_right_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
		
		_first_actuator = new WPI_TalonSRX(Map.FIRST_ACTUATOR_PORT);
		_second_actuator = new WPI_TalonSRX(Map.SECOND_ACTUATOR_PORT);
		
		_grab_piston = new DoubleSolenoid(Map.GRAB_PISTON_HIGHSIDE_PORT, Map.GRAB_PISTON_LOWSIDE_PORT);
		_grab_piston.set(DoubleSolenoid.Value.kOff);

		_end_lift = new DoubleSolenoid(Map.END_LIFT_HIGHSIDE_PORT, Map.END_LIFT_LOWSIDE_PORT);
		_end_lift.set(DoubleSolenoid.Value.kOff);

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
	public void set_lift_speed(double speed) //sets both the right and left flipper speeds
	{
		_first_actuator.set(speed);
		_second_actuator.set(speed);
	}

	public void elevator_levels(){

		int[] front_potentiometer_levels = {};
		int[] back_potentiometer_levels = {};
		int current_level = 0;

		if(IO.elevator_go_home())
		{
			current_level = 0;
		}
		else if(IO.up_elevator_level() && IO.up_elevator_level() != lastLiftButtonState)
		{
			if(current_level < 2)
			{
				current_level = current_level + 1;
			}			
		}
		else if (IO.down_elevator_level() && IO.down_elevator_level() != lastLiftButtonState)
		{
			if(current_level > 0)
			{
				current_level = current_level - 1;
			}
		}

		try
		{
			_first_actuator.set(front_potentiometer_levels[current_level]-firstPotentiometer.get());
			_second_actuator.set(back_potentiometer_levels[current_level]-secondPotentiometer.get());
		}
		catch(Exception e)
		{
			System.out.println("Potentiometer array out of bounds");
		}

		lastLiftButtonState = IO.up_elevator_level() || IO.down_elevator_level();
	}

	public static void update_grabber_state() {
		if(_grab_piston.get() == DoubleSolenoid.Value.kOff || _grab_piston.get() == DoubleSolenoid.Value.kReverse) 
		{
			open_grabber();
		}
		else
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
	public static void update_arm_state() 
	{
        if(_arm_extension.get() == DoubleSolenoid.Value.kOff || _arm_extension.get() == DoubleSolenoid.Value.kReverse) 
        {
            _arm_extension.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            _arm_extension.set(DoubleSolenoid.Value.kReverse);
        }
	}

	public static void update_end_lift_state() 
	{
        if(_end_lift.get() == DoubleSolenoid.Value.kOff || _end_lift.get() == DoubleSolenoid.Value.kReverse) 
        {
            _end_lift.set(DoubleSolenoid.Value.kForward);
        }
        else
        {
            _end_lift.set(DoubleSolenoid.Value.kReverse);
        }
	}
	
	public void update_actuator_speed()
	{
		if(potentiometer_check())
		{
			if(IO.get_first_height_button())
			{
				_first_actuator.set(Map.FIRST_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.FIRST_HEIGHT - secondPotentiometer.get());
			}
			else if(IO.get_second_height_button())
			{
				_first_actuator.set(Map.SECOND_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.SECOND_HEIGHT - secondPotentiometer.get());
			}
			else if(IO.get_third_height_button())
			{
				_first_actuator.set(Map.THIRD_HEIGHT - firstPotentiometer.get());
				_second_actuator.set(Map.THIRD_HEIGHT - secondPotentiometer.get());
			}
		}
	}

	public boolean potentiometer_check()
	{
		if(Math.abs(firstPotentiometer.get()-secondPotentiometer.get()) < Map.POTENTIOMETER_LIMIT)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public void semaphore_update() //updates robot information
	{
		if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{	
			if(IO.get_grabber() && IO.get_grabber() != lastGrabberButtonState)
			{
				update_grabber_state();
				Auto_Alignment.alignment_state = alignment_position.PLACEMENT_TRACKING;
			}
			lastGrabberButtonState = IO.get_grabber();
			/*
			if(IO.extend_arm() && !Auto_Alignment.get_grabber_trigger() && IO.extend_arm() != lastArmButtonState)
            {
                update_arm_state();
            }
            else if (Auto_Alignment.get_grabber_trigger())
            {
                _arm_extension.set(DoubleSolenoid.Value.kReverse);
			}
			if(Auto_Alignment.get_lift_trigger())
			{
				update_end_lift_state();
			}
			lastArmButtonState = IO.get_grabber();
			*/
			//set_lift_speed(IO.get_lift_speed());
			_first_actuator.set(IO.get_actuator_2_speed()*-1*Map.ACTUATOR_MULTIPLIER);
			_second_actuator.set(IO.get_actuator_1_speed()*-1*Map.ACTUATOR_MULTIPLIER);
			set_intake_speed(IO.get_intake_speed());
			
		}
	}
}
