package org.usfirst.frc1504.Robot2020;

import org.usfirst.frc1504.Robot2020.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Proton_Cannon implements Updatable
{
    private static final Proton_Cannon instance = new Proton_Cannon();
    private DriverStation _ds = DriverStation.getInstance();

    private CANSparkMax _top_shoot;
    private CANSparkMax _bottom_shoot;
    private CANEncoder _top_encoder = new CANEncoder(_top_shoot);
    private CANEncoder _bottom_encoder = new CANEncoder(_bottom_shoot);

    private static double speedo = 0.32;
    private static double _gain = (1/5676);
    private static double tspeedo = 0;
    private static final double max_speed = 1;
    private double cannon_spin = 0;

    private int i = 0;
    private static final double[] _setpoints = {0, 0.33, 0.5, 0.67, 1};
    private double setpoint;

    public static Proton_Cannon getInstance() // sets instance
	{
		return instance;
    }

    public static void initialize() // initialize
    {
        getInstance();
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
            tspeedo = tspeedo + 500;
        } else if(IO.hid_S())
        {
            tspeedo = tspeedo - 500;
        }

        speedo = IO.get_proton_speed();

        if(IO.get_proton_setpoint())
        {
            i = i++;
            setpoint = _setpoints[i % _setpoints.length];
        }


		if(speedo > max_speed)
		{
			speedo = max_speed;
		} else if(speedo < 0)
		{
			speedo = 0;
		}
        if(IO.get_proton_speed() > 0 && setpoint == 0) 
        {
            _top_shoot.set(speedo + cannon_spin);
            _bottom_shoot.set(speedo - cannon_spin);
            cannon_spin = (_bottom_encoder.getVelocity() - _top_encoder.getVelocity() + tspeedo) * _gain;
        } else if(IO.get_proton_speed() > 0 && setpoint != 0)
        {
            _top_shoot.set(setpoint);
            _bottom_shoot.set(setpoint);
        } else {
            _top_shoot.set(0);
            _bottom_shoot.set(0);
        }
        

        SmartDashboard.putString("Spew Top Speed", (_top_encoder.getVelocity() + "RPM"));
        SmartDashboard.putString("Spew Bottom Speed", (_bottom_encoder.getVelocity() + "RPM"));
        SmartDashboard.putString("Spin Diff", (tspeedo + "RPM"));
        SmartDashboard.putNumber("Setpoint Number", setpoint);

        System.out.println("Bottom Speed: " + _bottom_encoder.getVelocity());
        System.out.println("Top Speed: " + _top_encoder.getVelocity());
        System.out.println(tspeedo);
    }

    public void semaphore_update() // updates robot information
	{		
		if (_ds.isDisabled()) // only runs in teleop
			return;

		update();
    }
}