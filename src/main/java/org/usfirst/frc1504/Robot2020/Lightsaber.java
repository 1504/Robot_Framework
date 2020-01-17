package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANEncoder;

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
        System.out.println("Lightsaber is on");
    }

    private void update()
    {
        if(IO.get_lightsaber_button() && !IO.get_lightsaber_inverter())
        {
            _lightsaber_top.set(1);
            if(_lightsaber_top.getEncoder().getPosition() > _lightsaber_bottom.getEncoder().getPosition())
            {
                _lightsaber_bottom.set(1);
            } else if(_lightsaber_top.getEncoder().getPosition() > _lightsaber_bottom.getEncoder().getPosition()) {
                _lightsaber_bottom.set(-1);
            } else {
               _lightsaber_bottom.set(0);
            }

        } else if(IO.get_lightsaber_button() && IO.get_lightsaber_inverter()) {
            _lightsaber_top.set(-1);
            if(_lightsaber_top.getEncoder().getPosition() > _lightsaber_bottom.getEncoder().getPosition())
            {
                _lightsaber_bottom.set(1);
            } else if(_lightsaber_top.getEncoder().getPosition() > _lightsaber_bottom.getEncoder().getPosition()) {
                _lightsaber_bottom.set(-1);
            } else {
               _lightsaber_bottom.set(0);
            }
        }

        
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}