package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Tokamak implements Updatable
{
    private static final Tokamak instance = new Tokamak();
    private DriverStation _ds = DriverStation.getInstance();

    private WPI_TalonSRX _tokamak_top;
    private WPI_TalonSRX _tokamak_bottom;

    private boolean _manual = false;

    public static Tokamak getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    private Tokamak()
    {
        _tokamak_top = new WPI_TalonSRX(Map.TOKAMAK_TOP); // serializer 
        _tokamak_bottom = new WPI_TalonSRX(Map.TOKAMAK_BOTTOM);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Tokamak is generating plasma");
    }

    private boolean toggle_manual_control() {
        return (IO.get_god_button() ? !_manual : _manual);
    }

    private void update()
    {
        if(IO.manual_ion_speed())
        {
            _tokamak_top.set(-Map.TOKAMAK_SPEED);
            _tokamak_bottom.set(Map.TOKAMAK_SPEED);
        } else if(IO.get_tokamak_override() > 0 && toggle_manual_control())
        {
            _tokamak_top.set(-IO.get_tokamak_override());
            _tokamak_bottom.set(IO.get_tokamak_override());
        } else if(Tractor_Beam.activated() && (System.currentTimeMillis() - Tractor_Beam.timer > 1000))
        {
            _tokamak_bottom.set(Map.TOKAMAK_SPEED);
        } else {
            _tokamak_top.set(0);
            _tokamak_bottom.set(0);
        }
        
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}