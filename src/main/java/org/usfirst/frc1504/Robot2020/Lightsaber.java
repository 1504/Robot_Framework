package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Servo;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Lightsaber implements Updatable
{
    private static final Lightsaber instance = new Lightsaber();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _lightsaber_top;
    private CANSparkMax _lightsaber_bottom;

    public static Lightsaber getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }


    private Lightsaber()
    {
        _lightsaber_top = new CANSparkMax(Map.LIGHTSABER_TOP, MotorType.kBrushless); // serializer 
        _lightsaber_bottom = new CANSparkMax(Map.LIGHTSABER_BOTTOM, MotorType.kBrushless);

        Update_Semaphore.getInstance().register(this);
        System.out.println("Lightsaber is generating plasma");
    }

    private void update()
    {
        if(IO.get_lightsaber_button() && !IO.get_lightsaber_inverter())
        {
            _lightsaber_top.set(1);
            _lightsaber_bottom.follow(_lightsaber_top, true);

        } else if(IO.get_lightsaber_button() && IO.get_lightsaber_inverter()) 
        {
            _lightsaber_top.set(-1);
            _lightsaber_bottom.follow(_lightsaber_top, true);
        } else 
        {
            _lightsaber_top.set(0);
            _lightsaber_bottom.follow(_lightsaber_top, true);
        }

        
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}