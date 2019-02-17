package org.usfirst.frc1504.Robot2019;
import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Arms implements Updatable {
	private WPI_TalonSRX _left_roller;
    private WPI_TalonSRX _right_roller;
    private WPI_TalonSRX _lift;
    public static DoubleSolenoid _arm_extension;

    private static final Arms instance = new Arms();
	private DriverStation _ds = DriverStation.getInstance();
	public static Arms getInstance() // sets instance
	{
		return instance;
	}
	private Arms() // arms constructor
	{
		_left_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_LEFT);
        _right_roller = new WPI_TalonSRX(Map.ROLLER_TALON_PORT_RIGHT);
        
        _arm_extension = new DoubleSolenoid(Map.ARM_EXTENSION_HIGHSIDE_PORT, Map.ARM_EXTENSION_LOWSIDE_PORT);
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
    public static void update_arm_state() {
        if(_arm_extension.get() == DoubleSolenoid.Value.kOff || _arm_extension.get() == DoubleSolenoid.Value.kReverse) 
        {
            _arm_extension.set(DoubleSolenoid.Value.kForward);
        }
        if(_arm_extension.get() == DoubleSolenoid.Value.kForward)
        {
            _arm_extension.set(DoubleSolenoid.Value.kReverse);
        }
    }
	public void semaphore_update() //updates robot information
	{
        if(_ds.isOperatorControl() && !_ds.isDisabled()) //only runs in teleop
		{	
            set_intake_speed(IO.get_intake_speed());
            if(IO.extend_arm() && !Auto_Alignment.get_grabber_trigger())
            {
                update_arm_state();
            }
            else if (Auto_Alignment.get_grabber_trigger())
            {
                _arm_extension.set(DoubleSolenoid.Value.kReverse);
            }
		}
	}
}