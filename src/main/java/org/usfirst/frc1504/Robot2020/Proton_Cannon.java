package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Servo;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Proton_Cannon implements Updatable
{
    private static final Proton_Cannon instance = new Proton_Cannon();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _top_shoot;
	private CANSparkMax _bottom_shoot;
    private static double speedo = 0.32;
    private static double tspeedo = 0;
    private static final double max_speed = 1;

    public static Proton_Cannon getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
    }

    public static double put_on_speedo()
    {
        return speedo;
    }

    public static double put_on_tspeedo()
    {
        return tspeedo;
    }

    private Proton_Cannon()
    {
		_top_shoot = new CANSparkMax(Map.PROTON_CANNON_TOP, MotorType.kBrushless);
        _bottom_shoot = new CANSparkMax(Map.PROTON_CANNON_BOTTOM, MotorType.kBrushless);
        
        Update_Semaphore.getInstance().register(this);
        System.out.println("Proton Cannon charged");
    }

    private void update()
    {
        if(IO.hid_N())
        {
            tspeedo = tspeedo + 0.005;
        } else if(IO.hid_S())
        {
            tspeedo = tspeedo - 0.005;
        }

        speedo = IO.get_proton_speed();

		if(speedo > max_speed)
		{
			speedo = max_speed;
		} else if(speedo < 0)
		{
			speedo = 0;
		}
        
        if(IO.get_proton_speed() > 0)
        {
            _top_shoot.set(-speedo - tspeedo);
            _bottom_shoot.set(speedo - tspeedo);
        } else {
            _top_shoot.set(0);
            _bottom_shoot.set(0);
        }
        System.out.println(speedo);
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}