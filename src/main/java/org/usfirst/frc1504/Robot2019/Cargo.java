package org.usfirst.frc1504.Robot2019;

import org.usfirst.frc1504.Robot2019.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Cargo implements Updatable
{
    private static final Cargo instance = new Cargo();
    private DriverStation _ds = DriverStation.getInstance();

	private WPI_TalonSRX _right_slurp;
    private DoubleSolenoid _arm_extension;
    private boolean _arms_out_override;
    private boolean _last_toggle;

	
    public static Cargo getInstance() // sets instance
	{
		return instance;
    }

    private Cargo()
    {
        _arm_extension = new DoubleSolenoid(Map.ARM_EXTENSION_HIGHSIDE_PORT, Map.ARM_EXTENSION_LOWSIDE_PORT);

        
        Update_Semaphore.getInstance().register(this);
        System.out.println("Cargo initialized");
    }
    
    public static void initialize() // initialize
    {
        getInstance();
    }

	private void update()
	{
        if(Elevator.getInstance().getMode() != Elevator.ELEVATOR_MODE.CARGO)
        {
            _arm_extension.set(DoubleSolenoid.Value.kReverse);

            _arms_out_override = false;
            _last_toggle = false;
        } 
        else
        {
            if(IO.get_grabber() && !_last_toggle)
                _arms_out_override = !_arms_out_override;
            _last_toggle = IO.get_grabber();


            _arm_extension.set(_arms_out_override ? DoubleSolenoid.Value.kReverse : DoubleSolenoid.Value.kForward);
        }
	}

	private void update_dashboard()
	{
		//uddb
	}

    public void semaphore_update() // updates robot information
	{
		update_dashboard();
		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
	}
}
