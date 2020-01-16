package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Servo;

public class Tokamak implements Updatable
{
    private static final Tokamak instance = new Tokamak();
    private DriverStation _ds = DriverStation.getInstance();

    private WPI_TalonSRX _tokamak_top;
    private WPI_TalonSRX _tokamak_bottom;

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

    private void update()
    {
        if(IO.get_proton_speed() > 0)
        {
            _tokamak_top.set(1);
            _tokamak_bottom.set(-1);

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